package id.ac.ui.cs.advprog.yomubackend.auth.dto;

public class AddIdentityRequest {
    private String email;
    private String phoneNumber;



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}