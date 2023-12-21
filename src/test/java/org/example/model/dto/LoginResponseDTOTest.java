package org.example.model.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseDTOTest {

    @Test
    public void testMessageGetterAndSetter() {
        // GIVEN
        String message = "Success";
        String newMessage = "Error";

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(message, "");

        assertEquals(message, loginResponseDTO.getMessage());

        // WHEN
        loginResponseDTO.setMessage(newMessage);

        // THEN
        assertEquals(newMessage, loginResponseDTO.getMessage());
        assertNotEquals("new new message", loginResponseDTO.getMessage());
    }


    @Test
    public void testUserEmailGetterAndSetter() {
        // GIVEN
        String userEmail = "test@example.com";
        String newUserEmail = "newtest@example.com";

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO("", userEmail);

        assertEquals(userEmail, loginResponseDTO.getUserEmail());

        // WHEN
        loginResponseDTO.setUserEmail(newUserEmail);

        // THEN
        assertEquals(newUserEmail, loginResponseDTO.getUserEmail());
        assertNotEquals("newemail@example.com", loginResponseDTO.getUserEmail());
    }
}