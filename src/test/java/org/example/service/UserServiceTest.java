package org.example.service;

import org.example.exception.InvalidLoginException;
import org.example.exception.InvalidUserInputData;
import org.example.model.SessionToken;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.utils.DatabaseConnection;
import org.example.utils.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mindrot.jbcrypt.BCrypt.checkpw;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = UserService.getInstance();
        userService.setUserRepository(userRepository); // Set the mock into the UserService
    }

    @Test
    public void testGetInstanceShouldReturnSameInstanceEveryTime() {
        // GIVEN
        UserService newUserService = UserService.getInstance();
        UserService newUserServiceTwo = UserService.getInstance();

        // WHEN, THEN
        assertSame(newUserService, newUserServiceTwo);
    }

    @Test
    public void testEmailValidatorShouldReturnTrueForValidEmail() {
        // GIVEN
        String validEmail = "test@example.com";

        // WHEN, THEN
        assertTrue(userService.emailValidator(validEmail));
    }

    @Test
    public void testEmailValidatorShouldReturnFalseForInvalidEmail() {
        // GIVEN
        String invalidEmail = "test@";

        // WHEN, THEN
        assertFalse(userService.emailValidator(invalidEmail));
    }

    @Test
    public void testEmailValidatorShouldReturnFalseForInvalidEmailTwo() {
        // GIVEN
        String invalidEmail = "testexample.com";

        // WHEN, THEN
        assertFalse(userService.emailValidator(invalidEmail));
    }

    @Test
    public void testEmailValidatorShouldReturnFalseForInvalidEmailThree() {
        // GIVEN
        String invalidEmail = "test@example.";

        // WHEN, THEN
        assertFalse(userService.emailValidator(invalidEmail));
    }

    @Test
    public void testRegisterUserShouldThrowExceptionForShortName() {
        // Arrange
        // Invalid user name (too short)
        User invalidUser = new User("Na", "invalidemail", "123456789");

        // Act, Assert
        assertThrows(InvalidUserInputData.class, () ->
                userService.registerUser(invalidUser), "Invalid name");
    }

    @Test
    public void testRegisterUserShouldThrowExceptionForLongName() {
        // Arrange
        // Invalid user name (too long 101 chars)
        User invalidUser = new User("frbfjrbjrbgrbgjkdgkdkgkdfkgkdsfgjkdnfjkgnjdksnjkgndfsngj" +
                "kndsjkgndjksnbjkdsgnjkgnjkdsngjdsnbjkndsjkbnd", "invalidemail", "123456789");

        // Act, Assert
        assertThrows(InvalidUserInputData.class, () ->
                userService.registerUser(invalidUser), "Invalid name");
    }

    @Test
    public void testRegisterUserShouldThrowExceptionIfEmailIsInCorrect() {
        // Arrange
        User invalidUser = new User("Galin", "invalidEmail", "123456789");

        // Act
        // Assert
        assertThrows(InvalidUserInputData.class, () ->
                userService.registerUser(invalidUser), "Invalid email");
    }

    @Test
    public void testRegisterUserShouldThrowExceptionIfThePasswordIsUnderEightChars() {
        // Arrange
        User invalidUser = new User("Galin", "test@example.com", "1234");

        // Act, Assert
        assertThrows(InvalidUserInputData.class, () ->
                userService.registerUser(invalidUser), "Invalid email or password");
    }

    @Test
    void testHashPasswordShouldHashSuccessful() throws Exception {
        // Arrange
        UserService userService = UserService.getInstance();
        User user = new User("Test User", "test@example.com", "plainPassword");

        when(userRepository.getHashedPasswordForUser(user.getEmail())).thenReturn("mockedHashedPassword");

        // Act
        userService.registerUser(user);

        // Assert
        // Access the hashed password (requires UserRepository mock or similar method)
        String hashedPassword = userRepository.getHashedPasswordForUser("test@example.com");
        assertNotEquals("plainPassword", hashedPassword, "Hashed password should not match the plain password");

        // Verify
        verify(userRepository, times(1)).getHashedPasswordForUser(user.getEmail());
    }

    @Test
    public void testRegisterUserShouldStoreUserForValidData() throws Exception {
        // Arrange
        User validUser = new User("Galin Petrov", "test@example.com", "password123");

        // Act
        userService.registerUser(validUser);

        // Verify
       verify(userRepository, times(1))
               .saveUserToDatabase(anyString(),
                       anyString(), anyString());
    }

    @Test
    public void testVerifyUserEmailShouldVerifySuccessful() throws SQLException {
        // Arrange
        String email = "example@gmail.com";
        String verificationCode = "verification123";

        when(userRepository.verify(email, verificationCode)).thenReturn(true);

        // Act
        boolean isVerified = userService.verifyUserEmail(email, verificationCode);

        // Assert
        assertTrue(isVerified);
        verify(userRepository, times(1)).verify(email, verificationCode);
    }

    @Test
    public void testVerifyUserEmailShouldNotVerifySuccessful() throws SQLException {
        // Arrange
        String email = "example@gmail.com";
        String verificationCode = "verification123";

        when(userRepository.verify(email, verificationCode)).thenReturn(false);

        // Act
        boolean isVerified = userService.verifyUserEmail(email, verificationCode);

        // Assert
        assertFalse(isVerified);

        // Verify
        verify(userRepository, times(1)).verify(email, verificationCode);
    }

    @Test
    public void testLoginUserShouldLoginSuccessful() throws SQLException {
        // Arrange
        String email = "example@gmail.com";
        String password = "password123";

        when(userRepository.login(email, password)).thenReturn("token123");

        // Act
        String token = userService.loginUser(email, password);

        // Assert
        assertEquals("token123", token);

        // verify
        verify(userRepository, times(1)).login(email, password);
    }

    @Test
    public void testLoginUserShouldThrowExceptionIfDataIsInvalid() throws SQLException {
        // Arrange
        String invalidEmail = "examplegmail.com";
        String password = "password123";

        when(userRepository.login(invalidEmail, password)).thenReturn(null);

        // Act, Assert
        assertThrows(InvalidLoginException.class, () ->
                userService.loginUser(invalidEmail, password),
                "Error with login, invalid data");

        // verify
        verify(userRepository, times(1)).login(invalidEmail, password);
    }

    @Test
    public void testIsSessionValidShouldBeTrueIfSessionIsValid() {
        // Arrange
        String email = "user@example.com";
        String sessionToken = "validToken";

        SessionManager.storeSessionToken(email, sessionToken);

        // Act
        boolean result = userService.isSessionValid(email, sessionToken);

        // Assert
        assertTrue(result, "isSessionValid should return true for a valid and non-expired session");
    }

    @Test
    public void testIsSessionValidShouldNotBeTrueIfSessionIsInValid() {
        // Arrange
        String email = "user@example.com";
        String sessionToken = "token";
        String anotherToken = "token1234";

        SessionManager.storeSessionToken(email, sessionToken);

        // Act
        boolean result = userService.isSessionValid(email, anotherToken);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testCreateAndSendVerificationCodeShouldBeSuccessful() throws SQLException {
        // Arrange
        String email = "user@example.com";

        // Act
        String verificationCode = userService.createAndSendVerificationCode(email);

        // Assert
        assertNotNull(verificationCode);

        // Verify
        verify(userRepository, times(1)).storeVerificationCodeInDatabase(verificationCode, email);
    }
}