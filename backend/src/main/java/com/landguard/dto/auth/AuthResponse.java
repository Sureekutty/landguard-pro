package com.landguard.dto.auth;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private String username;
    private String role;

    public AuthResponse() {}

    public static AuthResponse success(String token, String username, String role) {
        AuthResponse res = new AuthResponse();
        res.success = true;
        res.token = token;
        res.username = username;
        res.role = role;
        return res;
    }

    public static AuthResponse error(String message) {
        AuthResponse res = new AuthResponse();
        res.success = false;
        res.message = message;
        return res;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
