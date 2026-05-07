package id.ac.ui.cs.advprog.yomubackend.auth.dto;

public class UpdateProfileRequest {
    private String username;
    private String displayName;
    private String password;

    // --- Bawah ini Getter & Setter manual ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}