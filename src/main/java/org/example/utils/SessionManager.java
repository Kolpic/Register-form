package org.example.utils;

import org.example.model.SessionToken;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    // Using concurrentHashMap, so we can use more threads and the map to function properly
    private static ConcurrentHashMap<String, SessionToken> sessionTokens = new ConcurrentHashMap<>();
    private static final int TOKEN_TTL_MINUTES = 30; // TTL(Time to live) will be 30 minutes

    public static void storeSessionToken(String email, String sessionToken) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES);
        sessionTokens.put(email, new SessionToken(sessionToken, expiryTime));
    }

    public static SessionToken getSessionToken(String email) {
        return sessionTokens.get(email);
    }
}
