package org.example.service;

import org.example.exception.InvalidLoginException;
import org.example.exception.InvalidUserInputData;
import org.example.model.SessionToken;
import org.example.model.User;
import org.example.repository.UserRepository;

import org.example.utils.SessionManager;
import org.mindrot.jbcrypt.BCrypt;
//import org.mindrot.jbcrypt.BCrypt;

import javax.mail.*;
import javax.mail.internet.*;

import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService {

    private static UserService userService;
    private static UserRepository userRepository = UserRepository.getInstance();

    public static UserService getInstance() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    public void setUserRepository(UserRepository userRepository) {
        UserService.userRepository = userRepository;
    }

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

    public String createAndSendVerificationCode(String email) throws SQLException {
        // Generate a random verification code
        String verificationCode = UUID.randomUUID().toString();

        userRepository.storeVerificationCodeInDatabase(verificationCode, email);

        return verificationCode;
    }

    public boolean verifyUserEmail(String email, String verificationCode) throws SQLException {
        boolean isUpdated = userRepository.verify(email, verificationCode);
        if (isUpdated) {
            return true;
        } else {
            return false;
        }
    }

    public String loginUser(String email, String password) throws SQLException {
        String answer = userRepository.login(email, password);
        if (answer != null) {
            return answer;
        } else {
            throw new InvalidLoginException("Error with login, invalid data");
        }
    }

    public boolean isSessionValid(String email, String sessionToken) {
        SessionToken storedToken = SessionManager.getSessionToken(email);
        if  (storedToken != null && storedToken.getToken().equals(sessionToken)) {
            return !storedToken.isExpired();
        }
        return false;
    }

    private void storeSessionToken(String email, String sessionToken) {
        SessionManager.storeSessionToken(email, sessionToken);
    }

    public void sendVerificationEmail(String email, String verificationCode) {
        // Set up SMTP server details
        /**
         * To test the send mail: fromEmail - is your actual gmail,
         * password - is your gmail app password, this password can be made
         * when you make a 2 step verification, then you can make an app password
         * Gmail -> Profile -> Security ->  2 Steps Verification -> App password
         */
        final String fromEmail = "galincho112@gmail.com"; // TODO: Change the email and password
        final String password = "kskf nciq lqfm zevh";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        properties.put("mail.smtp.port", "587"); // TLS Port
        properties.put("mail.smtp.auth", "true"); // enable authentication
        properties.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(properties, authenticator);

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

    boolean emailValidator(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[A-Z0-9_!#$%&'*+/=?`{|}~^-]+↵\n" +
                ")*@[A-Z0-9-]+(?:\\.[A-Z0-9-]+)*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
}
