package com.gym.util;

import lombok.extern.slf4j.Slf4j;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Slf4j
public class EmailSender {
    
    private Properties emailProperties;
    private Session session;
    private boolean configured;
    
 
    public EmailSender() {
        this.emailProperties = loadEmailProperties();
        this.configured = initializeSession();
    }
    

    private Properties loadEmailProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                log.warn("Unable to find email.properties, email functionality will be disabled");
                return properties;
            }
            properties.load(input);
            log.info("Email properties loaded successfully");
        } catch (IOException e) {
            log.error("Failed to load email properties: {}", e.getMessage());
        }
        return properties;
    }
    

    private boolean initializeSession() {
        try {
            String host = emailProperties.getProperty("mail.smtp.host");
            String port = emailProperties.getProperty("mail.smtp.port");
            
            if (host == null || host.isEmpty() || port == null || port.isEmpty()) {
                log.warn("SMTP host or port not configured, email functionality will be disabled");
                return false;
            }
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", emailProperties.getProperty("mail.smtp.auth", "true"));
            props.put("mail.smtp.starttls.enable", emailProperties.getProperty("mail.smtp.starttls.enable", "true"));
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            
            final String username = emailProperties.getProperty("mail.smtp.username");
            final String password = emailProperties.getProperty("mail.smtp.password");
            
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                log.warn("SMTP username or password not configured, email functionality will be disabled");
                return false;
            }
            
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            log.info("Email session initialized successfully");
            return true;
        } catch (Exception e) {
            log.error("Failed to initialize email session: {}", e.getMessage());
            return false;
        }
    }
    

    public boolean sendEmail(String recipient, String subject, String body) {
        if (!configured) {
            log.warn("Email functionality is not configured, cannot send email");
            return false;
        }
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailProperties.getProperty("mail.from")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
            log.info("Email sent successfully to {}", recipient);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage());
            return false;
        }
    }
    
 
    public boolean isConfigured() {
        return configured;
    }
}
