package org.example.utils;

import org.example.model.SessionToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @Test
    public void testStoreSessionTokenShouldBeSuccessful() {
        // GIVEN
        String email = "test@example.com";
        String token = "token123";

        // WHEN
        SessionManager.storeSessionToken(email, token);

        // THEN
        SessionToken retrievedToken = SessionManager.getSessionToken(email);
        assertNotNull(retrievedToken, "Retrieved token should not be null");
        assertEquals(token, retrievedToken.getToken(), "The token should match the stored value");
        assertFalse(retrievedToken.isExpired());
    }

    @Test
    public void testStoreSessionTokenShouldBeSuccessfulIfThereIsMoreThanOneToken() {
        // GIVEN
        String emailOne = "test@example.com";
        String tokenOne = "token123";
        String emailTwo = "testtwo@example.com";
        String tokenTwo = "tokentwo";

        // WHEN
        SessionManager.storeSessionToken(emailOne, tokenOne);
        SessionManager.storeSessionToken(emailTwo, tokenTwo);

        // THEN
        SessionToken retrievedTokenOne = SessionManager.getSessionToken(emailOne);
        SessionToken retrievedTokenTwo = SessionManager.getSessionToken(emailTwo);
        assertEquals(tokenOne, retrievedTokenOne.getToken());
        assertEquals(tokenTwo, retrievedTokenTwo.getToken());
    }
}