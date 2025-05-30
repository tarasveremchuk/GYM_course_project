package com.gym.util;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Тестова версія LoggerUtil для використання в юніт-тестах.
 * Дозволяє ін'єктувати мок-об'єкти для тестування логування.
 */
public class TestLoggerUtil {
    
    private static Logger logger;
    private static Logger userActionLogger;
    private static Logger errorLogger;
    private static Marker userActionMarker;
    private static final String USER_ACTION_PREFIX = "[USER_ACTION] ";
    
    /**
     * Ініціалізує TestLoggerUtil з мок-об'єктами для тестування
     */
    public static void initializeWithMocks(Logger mockLogger, Logger mockUserActionLogger, 
                                          Logger mockErrorLogger, Marker mockMarker) {
        logger = mockLogger;
        userActionLogger = mockUserActionLogger;
        errorLogger = mockErrorLogger;
        userActionMarker = mockMarker;
    }
    
    /**
     * Скидає всі мок-об'єкти
     */
    public static void reset() {
        logger = null;
        userActionLogger = null;
        errorLogger = null;
        userActionMarker = null;
    }
    
    /**
     * Логування інформації про вхід користувача в систему
     */
    public static void logUserLogin(String username, String role, boolean successful) {
        if (successful) {
            logger.info("Користувач {} увійшов в систему з роллю {}", username, role);
        } else {
            logger.warn("Невдала спроба входу для користувача {}", username);
        }
    }
    
    /**
     * Логування інформації про вихід користувача з системи
     */
    public static void logUserLogout(String username) {
        logger.info("Користувач {} вийшов з системи", username);
    }
    
    /**
     * Логування операцій з клієнтами
     */
    public static void logClientOperation(String action, String clientName, String details) {
        logger.info("Клієнт '{}': {} - {}", clientName, action, details);
    }
    
    /**
     * Логування операцій з персоналом
     */
    public static void logStaffOperation(String action, String staffName, String details) {
        logger.info("Персонал '{}': {} - {}", staffName, action, details);
    }
    
    /**
     * Логування операцій з абонементами
     */
    public static void logMembershipOperation(String action, Long membershipId, String clientName, String details) {
        logger.info("Абонемент #{} для клієнта '{}': {} - {}", membershipId, clientName, action, details);
    }
    
    /**
     * Логування операцій з тренуваннями
     */
    public static void logTrainingOperation(String action, Long trainingId, String trainingName, String details) {
        logger.info("Тренування #{} '{}': {} - {}", trainingId, trainingName, action, details);
    }
    
    /**
     * Логування операцій з платежами
     */
    public static void logPaymentOperation(String action, Long paymentId, double amount, String clientName) {
        logger.info("Платіж #{} на суму {} для клієнта '{}': {}", paymentId, amount, clientName, action);
    }
    
    /**
     * Логування помилок бази даних
     */
    public static void logDatabaseError(String operation, String entity, Exception exception) {
        
        logger.error("Помилка бази даних при {} сутності {}", operation, entity, exception);
    }
    
    /**
     * Логування критичних помилок додатку
     */
    public static void logCriticalError(String component, String message, Throwable exception) {
        String errorMessage = String.format("КРИТИЧНА ПОМИЛКА в %s: %s", component, message);
        logger.error(errorMessage, exception);
        
        errorLogger.error(errorMessage, exception);
    }
    
    /**
     * Логування помилок бізнес-логіки
     */
    public static void logBusinessError(String operation, String details) {
        logger.error("Помилка бізнес-логіки при {}: {}", operation, details);
    }
    
    /**
     * Логування дій користувача в інтерфейсі
     */
    public static void logUserAction(String username, String action, String details) {
        String message = String.format("Користувач '%s': %s - %s", username, action, details);
        
        
        logger.debug(userActionMarker, "USER_ACTION_PREFIX");
        
        userActionLogger.info(message);
    }
    
    /**
     * Логування дій користувача з клієнтами
     */
    public static void logUserClientAction(String username, String action, String clientName, String details) {
        String message = String.format("Користувач '%s' виконав дію '%s' з клієнтом '%s': %s", 
                username, action, clientName, details);
        userActionLogger.info(message);
    }
    
    /**
     * Логування дій користувача з абонементами
     */
    public static void logUserMembershipAction(String username, String action, Long membershipId, String clientName) {
        String message = String.format("Користувач '%s' виконав дію '%s' з абонементом #%d для клієнта '%s'", 
                username, action, membershipId, clientName);
        userActionLogger.info(message);
    }
    
    /**
     * Логування дій користувача з платежами
     */
    public static void logUserPaymentAction(String username, String action, Long paymentId, double amount, String clientName) {
        String message = String.format("Користувач '%s' виконав дію '%s' з платежем #%d на суму %s для клієнта '%s'", 
                username, action, paymentId, amount, clientName);
        userActionLogger.info(message);
    }
    
    /**
     * Логування запуску і зупинки додатку
     */
    public static void logApplicationEvent(String event, String details) {
        logger.info("Додаток {}: {}", event, details);
    }
}