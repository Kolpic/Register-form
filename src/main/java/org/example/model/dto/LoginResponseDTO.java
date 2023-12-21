package org.example.model.dto;

public class LoginResponseDTO {

    private String message;
    private String userEmail;

    public LoginResponseDTO(String message, String userEmail) {
        this.message = message;
        this.userEmail = userEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
