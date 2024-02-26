package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private String name;
    private String email;
    private String password;

    @BeforeEach
    void setUp() {
        name = "Galin Petrov";
        email = "petrov@example.com";
        password = "123456";
    }

    @Test
    public void testUserConstructorShouldCreateNewUserSuccessful() {
        // GIVEN --> in the setUp method

        // WHEN
        User user = new User(name, email, password);

        // THEN
        assertEquals(name, user.getFull_name());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testSettersShouldSetFieldsSuccessful() {
        // GIVEN
        User user = new User(name, email, password);

        // WHEN
        user.setFull_name("Ivan Petrov");
        user.setEmail("ivan@example.com");
        user.setPassword("123456789");

        // THEN
        assertEquals("Ivan Petrov", user.getFull_name());
        assertEquals("ivan@example.com", user.getEmail());
        assertEquals("123456789", user.getPassword());
    }
}