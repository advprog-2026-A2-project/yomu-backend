package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    private String identifier;
    private String password;
}
