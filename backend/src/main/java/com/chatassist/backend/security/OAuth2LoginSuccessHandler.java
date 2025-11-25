package com.chatassist.backend.security;

import com.chatassist.backend.model.User;
import com.chatassist.backend.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = (String) oauthUser.getAttributes().get("email");
        String name = (String) oauthUser.getAttributes().get("name");
        String picture = (String) oauthUser.getAttributes().get("picture");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setName(name);
            u.setPicture(picture);
            return u;
        });

        user.setLastLoginAt(Instant.now());
        if (name != null) user.setName(name);
        if (picture != null) user.setPicture(picture);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        // Return token as JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = String.format("{\"token\":\"%s\",\"userId\":\"%s\",\"email\":\"%s\"}", token, user.getId(), user.getEmail());
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
