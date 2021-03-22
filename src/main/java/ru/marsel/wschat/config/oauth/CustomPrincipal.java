package ru.marsel.wschat.config.oauth;

import java.math.BigInteger;
import java.security.Principal;

public interface CustomPrincipal extends Principal {
    String getId();
}
