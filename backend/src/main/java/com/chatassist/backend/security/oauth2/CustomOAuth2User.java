package com.chatassist.backend.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;

    public CustomOAuth2User(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        // Google 'sub' is unique user id
        return oAuth2User.getAttribute("sub");
    }

    public String getEmail() {
        return oAuth2User.<String>getAttribute("email");
    }

    public String getFullName() {
        String name = oAuth2User.getAttribute("name");
        if (name != null) return name;
        return oAuth2User.getAttribute("given_name");
    }

    public String getPicture() {
        return oAuth2User.getAttribute("picture");
    }
}
