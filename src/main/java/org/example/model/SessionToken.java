package org.example.model;

import java.time.LocalDateTime;

public class SessionToken {

    private  String token;
    private LocalDateTime expiryTime;

    public SessionToken(String token, LocalDateTime expiryTime) {
        this.token = token;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public String getToken() {
        return token;
    }
}
