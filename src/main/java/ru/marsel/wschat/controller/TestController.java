package ru.marsel.wschat.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;

@RestController
@RequestMapping()
public class TestController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/login")
    public ResponseEntity<?> getHref(@RequestParam(value = "code", required = false) String code) {
        if (code != null) {
            return ResponseEntity.ok(code);
        }
        return ResponseEntity.ok("https://accounts.google.com/o/oauth2/auth?redirect_uri=http://localhost:8081&response_type=code&client_id=91996090288-cej3ncdguqome5pbjskh2ji9cj0gdgb7.apps.googleusercontent.com&scope=https://www.googleapis.com/auth/userinfo.email");
    }

    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("redirect_uri", "http://localhost:8081");
        map.add("client_secret", "D3PWFkE_hqL3uJCQFA20_6HT");
        map.add("client_id", "91996090288-cej3ncdguqome5pbjskh2ji9cj0gdgb7.apps.googleusercontent.com");
        map.add("grant_type", "authorization_code");
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<?> response =
                restTemplate.exchange("https://accounts.google.com/o/oauth2/token",
                        HttpMethod.POST,
                        entity,
                        JSONObject.class);

        return ResponseEntity.ok(((JSONObject) response.getBody()).getAsString("access_token"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> get(@AuthenticationPrincipal Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }
}
