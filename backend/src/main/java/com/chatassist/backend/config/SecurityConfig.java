package com.chatassist.backend.config;

import com.chatassist.backend.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler successHandler;
    private final JwtUtil jwtUtil;

    public SecurityConfig(OAuth2LoginSuccessHandler successHandler, JwtUtil jwtUtil) {
        this.successHandler = successHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil);

        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health", "/login/**", "/oauth2/**", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(o -> o
                .successHandler(successHandler)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // CORS allowed for dev
        http.cors(Customizer.withDefaults());

        return http.build();
    }
}
