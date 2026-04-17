package id.ac.ui.cs.advprog.yomubackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response payload returned after authentication operations.
 */
@Getter
@AllArgsConstructor
public class AuthResponse {

    /** Authentication result status. */
    private String status;

    /** Identifier of the authenticated user. */
    private String userId;

    /** Username of the authenticated user. */
    private String username;

    /** Role assigned to the authenticated user. */
    private String role;

    /** Human-readable message about the authentication operation. */
    private String message;
}
