package ru.marsel.wschat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthCodeDto {
    private String provider;
    private String code;
}
