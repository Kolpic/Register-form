package org.example.service;

import org.example.exception.InvalidLoginException;
import org.example.exception.InvalidUserInputData;
import org.example.model.SessionToken;
import org.example.model.User;
import org.example.repository.UserRepository;

import org.example.utils.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.*;
import javax.mail.internet.*;

import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service class for handling user-related operations.
 * This class provides functionalities for registering users, verifying user emails,
 * user login, and managing session tokens. It interacts with the UserRepository for data persistence.
 */
public class UserService {

    private static UserService userService;
    private static UserRepository userRepository = UserRepository.getInstance();

    /**
     * Singleton pattern to ensure only one instance of UserService exists.
     *
     * @return The single instance of UserService.
     */
    public static UserService getInstance() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    /**
     * Injects a UserRepository instance for testing purposes.
     *
     * @param userRepository The UserRepository instance to be injected.
     */
    public void setUserRepository(UserRepository userRepository) {
        UserService.userRepository = userRepository;
    }

    /**
     * Registers a new user in the system after validation.
     *
     * @param user The user to be registered.
     * @throws SQLException If a database access error occurs.
     * @throws InvalidUserInputData If user input data is invalid.
     */
    public void registerUser(User user) throws SQLException {
        // Validate email and password
        if (user.getName().length() < 3 || user.getName().length() > 100) {
            throw new InvalidUserInputData("Invalid name");
        }
        if (!emailValidator(user.getEmail())) {
            throw new InvalidUserInputData("Invalid email");
        }
        if (user.getEmail() == null || user.getPassword().length() < 8) {
            throw new InvalidUserInputData("Invalid email or password");
        }

        // Hash password
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        userRepository.saveUserToDatabase(user.getName(), user.getEmail(), hashedPassword);
    }

    /**
     * Creates and stores a verification code for a user's email.
     *
     * @param email The email of the user for whom to create the verification code.
     * @return The generated verification code.
     * @throws SQLException If a database access error occurs.
     */
    public String createAndSendVerificationCode(String email) throws SQLException {
        // Generate a random verification code
        String verificationCode = UUID.randomUUID().toString();

        userRepository.storeVerificationCodeInDatabase(verificationCode, email);

        return verificationCode;
    }

    /**
     * Verifies a user's email with the provided verification code.
     *
     * @param email The email of the user to verify.
     * @param verificationCode The verification code for validation.
     * @return true if verification is successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean verifyUserEmail(String email, String verificationCode) throws SQLException {
        boolean isUpdated = userRepository.verify(email, verificationCode);
        if (isUpdated) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Authenticates a user's login credentials.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     * @return A session token if login is successful, null otherwise.
     * @throws SQLException If a database access error occurs.
     * @throws InvalidLoginException If login data is invalid.
     */
    public String loginUser(String email, String password) throws SQLException {
        String answer = userRepository.login(email, password);
        if (answer != null) {
            return answer;
        } else {
            throw new InvalidLoginException("Error with login, invalid data");
        }
    }

    /**
     * Checks if a session token is valid.
     *
     * @param email The email associated with the session token.
     * @param sessionToken The session token to validate.
     * @return true if the session token is valid, false otherwise.
     */
    public boolean isSessionValid(String email, String sessionToken) {
        SessionToken storedToken = SessionManager.getSessionToken(email);
        if  (storedToken != null && storedToken.getToken().equals(sessionToken)) {
            return !storedToken.isExpired();
        }
        return false;
    }

    /**
     * Database Storage:
     * Pros: Persistence across server restarts, scalable for distributed systems (if using a distributed database).
     * Cons: Slower than in-memory due to I/O operations, adds load to the database.
     *
     * In-Memory Data Structure:
     * Pros: Faster access, simpler to implement for a small-scale application.
     * Cons: Data is lost if the server restarts, not suitable for distributed
     * systems without additional configurations (like a distributed cache).
     */
    private void storeSessionToken(String email, String sessionToken) {
        SessionManager.storeSessionToken(email, sessionToken);
    }

    /**
     * Sends a verification email to the user.
     *
     * @param email The email address where the verification email is sent.
     * @param verificationCode The verification code to be included in the email.
     */
    public void sendVerificationEmail(String email, String verificationCode) {
        // Set up SMTP server details
        /**
         * To test the send mail: fromEmail - is your actual gmail,
         * password - is your gmail app password, this password can be made
         * when you make a 2 step verification, then you can make a app password
         * Gmail -> Profile -> Security ->  2 Steps Verification -> App password
         */
        final String fromEmail = "your email"; // TODO: Change the email and password
        final String password = "your email password";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        properties.put("mail.smtp.port", "587"); // TLS Port
        properties.put("mail.smtp.auth", "true"); // enable authentication
        properties.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

        // Create Authenticator object to pass in Session.getInstance argument
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(properties, authenticator);

        // Send email
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Email Verification");
            message.setText("Your verification code is: " + verificationCode);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    /**
     * Validates an email address using regex.
     *
     * @param email The email address to validate.
     * @return true if the email address is valid, false otherwise.
     */
    boolean emailValidator(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[A-Z0-9_!#$%&'*+/=?`{|}~^-]+↵\n" +
                ")*@[A-Z0-9-]+(?:\\.[A-Z0-9-]+)*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
}
