package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class AuthResponse {
    private String status;
    private String userId;
    private String username;
    private String role;
    private String message;
}
