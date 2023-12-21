package org.example.model.dto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserForVerificationDTOTest {

    @Test
    public void testEmailGetterAndSetter() {
        // GIVEN
        String email = "test@example.com";
        String newEmail = "newtest@example.com";

        UserForVerificationDTO userForVerificationDTO = new UserForVerificationDTO(email, "");
        assertEquals(email, userForVerificationDTO.getEmail());

        // WHEN
        userForVerificationDTO.setEmail(newEmail);

        // THEN
        assertEquals(newEmail, userForVerificationDTO.getEmail());
        assertNotEquals("new@example.com", userForVerificationDTO.getEmail());
    }

    @Test
    void testVerificationCodeGetterAndSetter() {
        // GIVEN
        String verificationCode = "123456";
        String newVerificationCode = "654321";

        UserForVerificationDTO userForVerificationDTO = new UserForVerificationDTO("", verificationCode);

        assertEquals(verificationCode, userForVerificationDTO.getVerificationCode());

        // WHEN
        userForVerificationDTO.setVerificationCode(newVerificationCode);

        // THEN
        assertEquals(newVerificationCode, userForVerificationDTO.getVerificationCode());
        assertNotEquals("1258",  userForVerificationDTO.getVerificationCode());
    }
}