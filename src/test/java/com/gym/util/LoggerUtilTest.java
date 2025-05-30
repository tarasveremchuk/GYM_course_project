package com.gym.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
public class LoggerUtilTest {
    
    
    
    @Test
    void testLogUserLogin() {
        
        LoggerUtil.logUserLogin("testuser", "ADMIN", true);
        LoggerUtil.logUserLogin("testuser", "ADMIN", false);
    }
    
    @Test
    void testLogUserLogout() {
        LoggerUtil.logUserLogout("testuser");
    }
    
    @Test
    void testLogClientOperation() {
        LoggerUtil.logClientOperation("додавання", "John Doe", "Новий клієнт");
    }
    
    @Test
    void testLogStaffOperation() {
        LoggerUtil.logStaffOperation("додавання", "Jane Smith", "Новий тренер");
    }
    
    @Test
    void testLogMembershipOperation() {
        LoggerUtil.logMembershipOperation("створення", 123L, "John Doe", "Новий абонемент");
    }
    
    @Test
    void testLogTrainingOperation() {
        LoggerUtil.logTrainingOperation("створення", 456L, "Йога", "Нове тренування");
    }
    
    @Test
    void testLogPaymentOperation() {
        LoggerUtil.logPaymentOperation("створення", 789L, 1500.0, "John Doe");
    }
    
    @Test
    void testLogDatabaseError() {
        Exception testException = new RuntimeException("Test exception");
        LoggerUtil.logDatabaseError("збереження", "Client", testException);
    }
    
    @Test
    void testLogCriticalError() {
        Throwable testException = new RuntimeException("Test exception");
        LoggerUtil.logCriticalError("Database", "Connection failed", testException);
    }
    
    @Test
    void testLogBusinessError() {
        LoggerUtil.logBusinessError("обробка платежу", "Недостатньо коштів");
    }
    
    @Test
    void testLogUserAction() {
        LoggerUtil.logUserAction("testuser", "перегляд", "сторінка клієнтів");
    }
    
    @Test
    void testLogUserClientAction() {
        LoggerUtil.logUserClientAction("testuser", "додавання", "John Doe", "Новий клієнт");
    }
    
    @Test
    void testLogUserMembershipAction() {
        LoggerUtil.logUserMembershipAction("testuser", "створення", 123L, "John Doe");
    }
    
    @Test
    void testLogUserPaymentAction() {
        LoggerUtil.logUserPaymentAction("testuser", "створення", 789L, 1500.0, "John Doe");
    }
    
    @Test
    void testLogApplicationEvent() {
        LoggerUtil.logApplicationEvent("запуск", "Версія 1.0");
    }
}
