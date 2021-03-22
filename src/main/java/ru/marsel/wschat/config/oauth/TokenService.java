package ru.marsel.wschat.config.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.client.RestTemplate;
import ru.marsel.wschat.config.oauth.github.GithubPrincipal;
import ru.marsel.wschat.config.oauth.google.GooglePrincipal;
import ru.marsel.wschat.config.oauth.yandex.YandexPrincipal;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singleton;

@RequiredArgsConstructor
public class TokenService implements ResourceServerTokenServices {
    private final AuthorizationCodeResourceDetails googleClient;
    private final AuthorizationCodeResourceDetails githubClient;
    private final AuthorizationCodeResourceDetails yandexClient;

    private AuthorizationCodeResourceDetails client;

    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
    private RestTemplate restTemplate = new RestTemplate();
    private String userInfoUrl;

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        String provider = "";
        if (accessToken.contains("google")) {
            client = googleClient;
            accessToken = accessToken.replaceFirst("google-", "");
            userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo";
            provider = "google";
        } else if (accessToken.contains("github")) {
            client = githubClient;
            accessToken = accessToken.replaceFirst("github-", "");
            userInfoUrl = "https://api.github.com/user";
            provider = "github";
        } else if (accessToken.contains("yandex")) {
            client = yandexClient;
            accessToken = accessToken.replaceFirst("yandex-", "");
            userInfoUrl = "https://login.yandex.ru/info?format=json&oauth_token=" + accessToken;
            provider = "yandex";
        }
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("client_id", client.getClientId());

        return getAuthentication(tokenInfo, accessToken, provider);

    }

    private OAuth2Authentication getAuthentication(Map<String, ?> tokenInfo, String accessToken, String provider) {
        OAuth2Request request = tokenConverter.extractAuthentication(tokenInfo).getOAuth2Request();
        Authentication authentication = getAuthenticationToken(accessToken, provider);
        return new OAuth2Authentication(request, authentication);
    }

    private Authentication getAuthenticationToken(String accessToken, String provider) {
        Map<String, ?> userInfo = getUserInfo(accessToken);

        CustomPrincipal principal = switch (provider) {
            case "google" -> new GooglePrincipal(userInfo);
            case "github" -> new GithubPrincipal(userInfo);
            case "yandex" -> new YandexPrincipal(userInfo);
            default -> throw new AccessDeniedException("test");
        };
        return new UsernamePasswordAuthenticationToken(principal, null, singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private HttpHeaders getHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        HttpHeaders headers = getHttpHeaders(accessToken);
        return (Map<String, Object>) restTemplate.exchange(userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}
