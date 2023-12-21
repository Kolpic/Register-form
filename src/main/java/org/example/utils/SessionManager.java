package org.example.utils;

import org.example.model.SessionToken;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages session tokens for users.
 * This class provides functionalities to store and retrieve session tokens associated with user email addresses.
 * It uses a {@link ConcurrentHashMap} to ensure thread-safe operations.
 */
public class SessionManager {
    // Using concurrentHashMap, so we can use more threads and the map to function properly
    private static ConcurrentHashMap<String, SessionToken> sessionTokens = new ConcurrentHashMap<>();
    private static final int TOKEN_TTL_MINUTES = 30; // TTL(Time to live) will be 30 minutes

    /**
     * Stores a session token for a user identified by an email.
     * The token is stored along with an expiry time, which is set based on a predefined TTL (Time To Live) value.
     *
     * @param email The email address of the user.
     * @param sessionToken The session token to be stored.
     */
    public static void storeSessionToken(String email, String sessionToken) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES);
        sessionTokens.put(email, new SessionToken(sessionToken, expiryTime));
    }

    /**
     * Retrieves the session token for a user identified by an email.
     * If no token is associated with the email, this method returns null.
     *
     * @param email The email address of the user whose session token is to be retrieved.
     * @return The {@link SessionToken} associated with the user, or null if no token is found.
     */
    public static SessionToken getSessionToken(String email) {
        return sessionTokens.get(email);
    }
}
