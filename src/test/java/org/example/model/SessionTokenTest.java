package org.example.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SessionTokenTest {

    private LocalDateTime futureTime;
    private SessionToken sessionToken;

    @BeforeEach
    void setUp() {
        futureTime = LocalDateTime.now().plusMinutes(20);
        sessionToken = new SessionToken("token", futureTime);
    }


    @Test
    public void testTokenShouldNotBeExpiredIfTimeIsBeforeExpiry() {
        // GIVEN --> in setUp method

        // WHEN
        boolean isExpired = sessionToken.isExpired();

        // THEN
        assertFalse(isExpired);
    }

    @Test
    public void testTokenShouldBeExpiredIfTimeIsAfterExpiry() {
        // GIVEN
        LocalDateTime futureTime = LocalDateTime.now().minusMinutes(20);
        SessionToken sessionToken = new SessionToken("token", futureTime);

        // WHEN
        boolean isExpired = sessionToken.isExpired();

        // THEN
        assertTrue(isExpired);
    }

    @Test
    public void testGetTokenShouldGetTokenSuccessful() {
        // GIVEN --> in setUp method

        // WHEN
        String token = sessionToken.getToken();

        // THEN
        assertEquals("token", token);
    }

    @Test
    public void testGetTokenShouldNotGetTokenSuccessful() {
        // GIVEN --> in setUp method

        // WHEN
        String token = sessionToken.getToken();

        // THEN
        assertNotEquals("token123", token);
    }

}