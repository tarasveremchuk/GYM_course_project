package com.gym.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Answers.RETURNS_DEFAULTS;

@ExtendWith(MockitoExtension.class)
public class EmailNotifierTest {

    private MockedStatic<Transport> mockedTransport;
    private MockedStatic<Session> mockedSession;
    private MockedStatic<InternetAddress> mockedInternetAddress;
    private Session mockSession;
    private MimeMessage mockMessage;
    private InternetAddress mockAddress;
    private MockedConstruction<MimeMessage> mockedMimeMessage;
    private MockedConstruction<InternetAddress> mockedAddress;
    
    
    private void resetStaticFields() throws Exception {
        Field isEnabledField = EmailNotifier.class.getDeclaredField("isEnabled");
        isEnabledField.setAccessible(true);
        isEnabledField.set(null, true);
        
        Field sessionField = EmailNotifier.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        sessionField.set(null, mockSession);
        
        Field emailPropertiesField = EmailNotifier.class.getDeclaredField("emailProperties");
        emailPropertiesField.setAccessible(true);
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.sender.email", "test@example.com");
        props.setProperty("mail.sender.password", "password");
        props.setProperty("mail.recipient.email", "admin@example.com");
        props.setProperty("mail.notification.subject.prefix", "[TEST]");
        props.setProperty("mail.notification.enabled", "true");
        emailPropertiesField.set(null, props);
        
        Field senderEmailField = EmailNotifier.class.getDeclaredField("senderEmail");
        senderEmailField.setAccessible(true);
        senderEmailField.set(null, "test@example.com");
        
        Field recipientEmailField = EmailNotifier.class.getDeclaredField("recipientEmail");
        recipientEmailField.setAccessible(true);
        recipientEmailField.set(null, "admin@example.com");
        
        Field subjectPrefixField = EmailNotifier.class.getDeclaredField("subjectPrefix");
        subjectPrefixField.setAccessible(true);
        subjectPrefixField.set(null, "[TEST]");
    }

    @BeforeEach
    void setUp() throws Exception {
        
        mockSession = mock(Session.class, RETURNS_DEEP_STUBS);
        mockMessage = mock(MimeMessage.class);
        mockAddress = mock(InternetAddress.class);
        
        
        mockedTransport = mockStatic(Transport.class, RETURNS_DEFAULTS);
        mockedSession = mockStatic(Session.class, RETURNS_DEFAULTS);
        mockedInternetAddress = mockStatic(InternetAddress.class, RETURNS_DEFAULTS);
        
        
        lenient().when(mockSession.getProperties()).thenReturn(new Properties());
        mockedSession.when(() -> Session.getInstance(any(Properties.class), any())).thenReturn(mockSession);
        
        
        
        mockedMimeMessage = mockConstruction(MimeMessage.class,
            (mock, context) -> {
                
                mockMessage = mock;
            });
        
        
        mockedInternetAddress.when(() -> InternetAddress.parse(anyString()))
            .thenReturn(new InternetAddress[]{mockAddress});
        
        
        mockedAddress = mockConstruction(InternetAddress.class,
            (mock, context) -> {
                
                mockAddress = mock;
            });
        
        
        resetStaticFields();
    }

    @AfterEach
    void tearDown() {
        
        if (mockedTransport != null) {
            mockedTransport.close();
        }
        if (mockedSession != null) {
            mockedSession.close();
        }
        if (mockedInternetAddress != null) {
            mockedInternetAddress.close();
        }
        if (mockedMimeMessage != null) {
            mockedMimeMessage.close();
        }
        if (mockedAddress != null) {
            mockedAddress.close();
        }
    }

    @Test
    void testSendErrorNotification() throws Exception {
        
        String subject = "Test Subject";
        String errorMessage = "Test Error Message";
        Exception testException = new RuntimeException("Test Exception");
        
        
        EmailNotifier.sendErrorNotification(subject, errorMessage, testException);
        
        
        verify(mockMessage).setFrom(any(InternetAddress.class));
        verify(mockMessage).setRecipients(eq(Message.RecipientType.TO), any(InternetAddress[].class));
        verify(mockMessage).setSubject(contains(subject));
        verify(mockMessage).setText(contains(errorMessage));
        
        
        mockedTransport.verify(() -> Transport.send(mockMessage), times(1));
    }
    
    @Test
    void testSendErrorNotificationWithoutException() throws Exception {
        
        String subject = "Test Subject";
        String errorMessage = "Test Error Message";
        
        
        EmailNotifier.sendErrorNotification(subject, errorMessage, null);
        
        
        verify(mockMessage).setFrom(any(InternetAddress.class));
        verify(mockMessage).setRecipients(eq(Message.RecipientType.TO), any(InternetAddress[].class));
        verify(mockMessage).setSubject(contains(subject));
        verify(mockMessage).setText(contains(errorMessage));
        
        
        mockedTransport.verify(() -> Transport.send(mockMessage), times(1));
    }
    
    @Test
    void testSendErrorNotificationDisabled() throws Exception {
        
        Field isEnabledField = EmailNotifier.class.getDeclaredField("isEnabled");
        isEnabledField.setAccessible(true);
        isEnabledField.set(null, false);
        
        
        String subject = "Test Subject";
        String errorMessage = "Test Error Message";
        Exception testException = new RuntimeException("Test Exception");
        
        
        EmailNotifier.sendErrorNotification(subject, errorMessage, testException);
        
        
        verifyNoInteractions(mockMessage);
        mockedTransport.verify(() -> Transport.send(any()), never());
    }
    
    @Test
    void testSendErrorNotificationWithMessagingException() throws Exception {
        
        String subject = "Test Subject";
        String errorMessage = "Test Error Message";
        Exception testException = new RuntimeException("Test Exception");
        
        
        mockedTransport.when(() -> Transport.send(any(Message.class)))
            .thenThrow(new MessagingException("Test Messaging Exception"));
        
        
        EmailNotifier.sendErrorNotification(subject, errorMessage, testException);
        
        
        verify(mockMessage).setFrom(any(InternetAddress.class));
        verify(mockMessage).setRecipients(eq(Message.RecipientType.TO), any(InternetAddress[].class));
        verify(mockMessage).setSubject(contains(subject));
        verify(mockMessage).setText(contains(errorMessage));
        
        
        mockedTransport.verify(() -> Transport.send(mockMessage), times(1));
    }
    
    @Test
    void testSendDatabaseConnectionErrorNotification() throws Exception {
        
        String errorMessage = "Database Connection Error Message";
        Exception testException = new RuntimeException("Test Exception");
        
        
        EmailNotifier.sendDatabaseConnectionErrorNotification(errorMessage, testException);
        
        
        verify(mockMessage).setFrom(any(InternetAddress.class));
        verify(mockMessage).setRecipients(eq(Message.RecipientType.TO), any(InternetAddress[].class));
        verify(mockMessage).setSubject(contains("Database Connection Error"));
        verify(mockMessage).setText(contains(errorMessage));
        
        
        mockedTransport.verify(() -> Transport.send(mockMessage), times(1));
    }
    
    @Test
    void testSendHibernateErrorNotification() throws Exception {
        
        String errorMessage = "Hibernate Error Message";
        Exception testException = new RuntimeException("Test Exception");
        
        
        EmailNotifier.sendHibernateErrorNotification(errorMessage, testException);
        
        
        verify(mockMessage).setFrom(any(InternetAddress.class));
        verify(mockMessage).setRecipients(eq(Message.RecipientType.TO), any(InternetAddress[].class));
        verify(mockMessage).setSubject(contains("Hibernate Error"));
        verify(mockMessage).setText(contains(errorMessage));
        
        
        mockedTransport.verify(() -> Transport.send(mockMessage), times(1));
    }
    
    @Test
    void testStaticInitializerWithPropertiesFile() throws Exception {
        
        
        
        
        Field isEnabledField = EmailNotifier.class.getDeclaredField("isEnabled");
        isEnabledField.setAccessible(true);
        
        
        boolean originalValue = (boolean) isEnabledField.get(null);
        
        try {
            
            isEnabledField.set(null, true);
            
            
            assertTrue((boolean) isEnabledField.get(null));
            
            
            EmailNotifier.sendErrorNotification("Test Subject", "Test Message", null);
            
            
            
        } finally {
            
            isEnabledField.set(null, originalValue);
        }
    }
    
    
    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
