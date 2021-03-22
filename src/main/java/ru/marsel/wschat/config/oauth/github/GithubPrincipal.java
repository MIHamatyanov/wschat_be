package ru.marsel.wschat.config.oauth.github;

import ru.marsel.wschat.config.oauth.CustomPrincipal;

import java.util.Map;

public class GithubPrincipal implements CustomPrincipal {

    private final String id;
    private final String login;

    public GithubPrincipal(Map<String, ?> userInfo) {
        this.id = String.valueOf(userInfo.get("id"));
        this.login = (String)userInfo.get("login");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GithubPrincipal that = (GithubPrincipal) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public String getName() {
        return this.login;
    }
}
