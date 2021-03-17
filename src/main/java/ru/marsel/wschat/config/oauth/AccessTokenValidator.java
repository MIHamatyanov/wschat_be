package ru.marsel.wschat.config.oauth;

public interface AccessTokenValidator {
    AccessTokenValidationResult validate(String accessToken);
}
