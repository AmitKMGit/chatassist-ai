package com.chatassist.backend.security.oauth2;

import com.chatassist.backend.model.User;
import com.chatassist.backend.repo.UserRepository;
import com.chatassist.backend.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuthUser.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found after OAuth2 login"));

        String token = jwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail()
        );

        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String targetUrl = redirectUri + "?token=" + encodedToken;

        response.sendRedirect(targetUrl);
    }
}
