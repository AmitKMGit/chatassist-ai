package com.chatassist.backend.dto;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse() {}
    public AuthResponse(String token) { this.token = token; }
    // getters/setters
}
