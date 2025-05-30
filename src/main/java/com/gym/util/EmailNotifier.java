package com.gym.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;


@Slf4j
public class EmailNotifier {
    private static Properties emailProperties = new Properties();
    private static boolean isEnabled = true;
    private static String smtpHost;
    private static String smtpPort;
    private static String senderEmail;
    private static String senderPassword;
    private static String recipientEmail;
    private static String subjectPrefix;
    
    private static jakarta.mail.Session session;
    
    static {
        try {
            InputStream input = EmailNotifier.class.getClassLoader().getResourceAsStream("email.properties");
            if (input == null) {
                log.error("Unable to find email.properties file. Email notifications will be disabled.");
                isEnabled = false;
            } else {
                emailProperties.load(input);
                input.close();
                
                smtpHost = emailProperties.getProperty("mail.smtp.host", "smtp.gmail.com");
                smtpPort = emailProperties.getProperty("mail.smtp.port", "587");
                senderEmail = emailProperties.getProperty("mail.sender.email", "gym.system.notifications@gmail.com");
                senderPassword = emailProperties.getProperty("mail.sender.password", "your-app-password");
                recipientEmail = emailProperties.getProperty("mail.recipient.email", "admin@yourgym.com");
                subjectPrefix = emailProperties.getProperty("mail.notification.subject.prefix", "[CRITICAL]");
                isEnabled = Boolean.parseBoolean(emailProperties.getProperty("mail.notification.enabled", "true"));
                
                Properties mailProps = new Properties();
                mailProps.put("mail.smtp.host", smtpHost);
                mailProps.put("mail.smtp.port", smtpPort);
                mailProps.put("mail.smtp.auth", emailProperties.getProperty("mail.smtp.auth", "true"));
                mailProps.put("mail.smtp.starttls.enable", emailProperties.getProperty("mail.smtp.starttls.enable", "true"));
                
                if (Boolean.parseBoolean(emailProperties.getProperty("mail.smtp.ssl.enable", "false"))) {
                    mailProps.put("mail.smtp.ssl.enable", "true");
                    mailProps.put("mail.smtp.socketFactory.port", smtpPort);
                    mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                }
                
                if (Boolean.parseBoolean(emailProperties.getProperty("mail.smtp.debug", "false"))) {
                    mailProps.put("mail.debug", "true");
                }
                
                session = jakarta.mail.Session.getInstance(mailProps, new jakarta.mail.Authenticator() {
                    @Override
                    protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new jakarta.mail.PasswordAuthentication(senderEmail, senderPassword);
                    }
                });
                
                log.info("Email notification system initialized successfully");
            }
        } catch (IOException e) {
            log.error("Error loading email properties. Email notifications will be disabled.", e);
            isEnabled = false;
        } catch (Exception e) {
            log.error("Error initializing email notification system. Email notifications will be disabled.", e);
            isEnabled = false;
        }
    }

    public static void sendErrorNotification(String subject, String errorMessage, Throwable exception) {
        if (!isEnabled || session == null) {
            log.warn("Email notifications are disabled. Not sending error notification for: {}", subject);
            return;
        }
        
        try {
            jakarta.mail.Message message = new jakarta.mail.internet.MimeMessage(session);
            message.setFrom(new jakarta.mail.internet.InternetAddress(senderEmail));
            message.setRecipients(jakarta.mail.Message.RecipientType.TO, 
                    jakarta.mail.internet.InternetAddress.parse(recipientEmail));
            message.setSubject(subjectPrefix + " " + subject);
            
            StringBuilder body = new StringBuilder();
            body.append("Critical Error Notification\n\n");
            body.append("Error: ").append(errorMessage).append("\n\n");
            
            if (exception != null) {
                body.append("Exception: ").append(exception.getClass().getName()).append("\n");
                body.append("Message: ").append(exception.getMessage()).append("\n\n");
                
                body.append("Stack Trace:\n");
                StackTraceElement[] stackTrace = exception.getStackTrace();
                for (int i = 0; i < Math.min(stackTrace.length, 10); i++) {
                    body.append("    at ").append(stackTrace[i]).append("\n");
                }
                
                body.append("\nTimestamp: ").append(java.time.LocalDateTime.now()).append("\n");
            }
            
            message.setText(body.toString());
            
            jakarta.mail.Transport.send(message);
            log.info("Critical error notification email sent to {}", recipientEmail);
        } catch (jakarta.mail.MessagingException e) {
            log.error("Failed to send error notification email", e);
        } catch (Exception e) {
            log.error("Unexpected error when sending notification email", e);
        }
    }
    

    public static void sendDatabaseConnectionErrorNotification(String errorMessage, Throwable exception) {
        sendErrorNotification("Database Connection Error", errorMessage, exception);
    }
    

    public static void sendHibernateErrorNotification(String errorMessage, Throwable exception) {
        sendErrorNotification("Hibernate Error", errorMessage, exception);
    }
}
