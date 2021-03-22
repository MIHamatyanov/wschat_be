package ru.marsel.wschat.config.oauth.google;

import lombok.Getter;
import lombok.ToString;
import ru.marsel.wschat.config.oauth.CustomPrincipal;

import java.util.Map;

@ToString
@Getter
public class GooglePrincipal implements CustomPrincipal {

    private final String id;
    private final String login;

    public GooglePrincipal(Map<String, ?> userInfo) {
        this.id = (String) userInfo.get("id");
        this.login = (String)userInfo.get("email");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GooglePrincipal that = (GooglePrincipal) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public String getName() {
        return this.login;
    }
}
