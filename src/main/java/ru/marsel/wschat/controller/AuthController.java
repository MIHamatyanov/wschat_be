package ru.marsel.wschat.controller;

import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.marsel.wschat.config.oauth.CustomPrincipal;
import ru.marsel.wschat.dto.OauthCodeDto;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AuthController {
    @Qualifier("googleClient")
    private final AuthorizationCodeResourceDetails googleClient;
    @Qualifier("githubClient")
    private final AuthorizationCodeResourceDetails githubClient;
    @Qualifier("yandexClient")
    private final AuthorizationCodeResourceDetails yandexClient;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/login")
    public ResponseEntity<?> getHref(@RequestParam String provider) {
        return switch (provider) {
            case "google" -> ResponseEntity.ok(googleClient.getUserAuthorizationUri() + "?redirect_uri=" + googleClient.getPreEstablishedRedirectUri() + "&response_type=code&client_id=" + googleClient.getClientId() + "&scope=https://www.googleapis.com/auth/userinfo.email");
            case "github" -> ResponseEntity.ok(githubClient.getUserAuthorizationUri() + "?redirect_uri=" + githubClient.getPreEstablishedRedirectUri() + "&client_id=" + githubClient.getClientId() + "&scope=user");
            case "yandex" -> ResponseEntity.ok(yandexClient.getUserAuthorizationUri() + "?response_type=code&redirect_uri=" + yandexClient.getPreEstablishedRedirectUri() + "&client_id=" + yandexClient.getClientId() + "&scope=login:info");
            default -> new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        };

    }

    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody OauthCodeDto codeDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        AuthorizationCodeResourceDetails client = switch (codeDto.getProvider()) {
            case "google" -> googleClient;
            case "github" -> githubClient;
            case "yandex" -> yandexClient;
            default -> throw new AccessDeniedException("test");
        };

        map.add("redirect_uri", client.getPreEstablishedRedirectUri());
        map.add("client_secret", client.getClientSecret());
        map.add("client_id", client.getClientId());
        map.add("code", codeDto.getCode());

        if (!codeDto.getProvider().equals("github")) {
            map.add("grant_type", "authorization_code");
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<?> response =
                restTemplate.exchange(client.getAccessTokenUri(),
                        HttpMethod.POST,
                        entity,
                        JSONObject.class);

        return ResponseEntity.ok(codeDto.getProvider() + "-" + ((JSONObject) response.getBody()).getAsString("access_token"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomPrincipal principal) {
        return ResponseEntity.ok(principal);
    }
}
