package com.gym.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


@Slf4j
public class LoggerUtil {
    
    private static final Logger userActionLogger = LoggerFactory.getLogger("com.gym.useractions");
    private static final Marker USER_ACTION_MARKER = MarkerFactory.getMarker("USER_ACTION");
    private static final String USER_ACTION_PREFIX = "[USER_ACTION] ";
    
    private static final Logger errorLogger = LoggerFactory.getLogger("com.gym.error");
    

    public static void logUserLogin(String username, String role, boolean successful) {
        if (successful) {
            log.info("Користувач {} увійшов в систему з роллю {}", username, role);
        } else {
            log.warn("Невдала спроба входу для користувача {}", username);
        }
    }
    

    public static void logUserLogout(String username) {
        log.info("Користувач {} вийшов з системи", username);
    }
    
 
    public static void logClientOperation(String action, String clientName, String details) {
        log.info("Клієнт '{}': {} - {}", clientName, action, details);
    }
    

    public static void logStaffOperation(String action, String staffName, String details) {
        log.info("Персонал '{}': {} - {}", staffName, action, details);
    }
    

    public static void logMembershipOperation(String action, Long membershipId, String clientName, String details) {
        log.info("Абонемент #{} для клієнта '{}': {} - {}", membershipId, clientName, action, details);
    }
    

    public static void logTrainingOperation(String action, Long trainingId, String trainingName, String details) {
        log.info("Тренування #{} '{}': {} - {}", trainingId, trainingName, action, details);
    }
    

    public static void logPaymentOperation(String action, Long paymentId, double amount, String clientName) {
        log.info("Платіж #{} на суму {} для клієнта '{}': {}", paymentId, amount, clientName, action);
    }
    

    public static void logDatabaseError(String operation, String entity, Exception exception) {
        String message = String.format("Помилка бази даних при %s для %s: %s", 
                operation, entity, exception.getMessage());
        log.error(message, exception);
        errorLogger.error(message, exception);
    }
    

    public static void logCriticalError(String component, String message, Throwable exception) {
        String errorMessage = String.format("КРИТИЧНА ПОМИЛКА в %s: %s", component, message);
        log.error(errorMessage, exception);
        errorLogger.error(errorMessage, exception);
    }
    

    public static void logBusinessError(String operation, String details) {
        log.error("Помилка бізнес-логіки при {}: {}", operation, details);
    }

    public static void logUserAction(String username, String action, String details) {
        String message = String.format("Користувач '%s': %s - %s", username, action, details);
        log.debug(USER_ACTION_MARKER, USER_ACTION_PREFIX + message);
        userActionLogger.info(message);
    }
    

    public static void logUserClientAction(String username, String action, String clientName, String details) {
        String message = String.format("Користувач '%s' виконав дію '%s' з клієнтом '%s': %s", 
                username, action, clientName, details);
        userActionLogger.info(message);
    }
    

    public static void logUserMembershipAction(String username, String action, Long membershipId, String clientName) {
        String message = String.format("Користувач '%s' виконав дію '%s' з абонементом #%d для клієнта '%s'", 
                username, action, membershipId, clientName);
        userActionLogger.info(message);
    }
    

    public static void logUserPaymentAction(String username, String action, Long paymentId, double amount, String clientName) {
        String message = String.format("Користувач '%s' виконав дію '%s' з платежем #%d на суму %s для клієнта '%s'", 
                username, action, paymentId, amount, clientName);
        userActionLogger.info(message);
    }
    

    public static void logApplicationEvent(String event, String details) {
        log.info("Додаток {}: {}", event, details);
    }
}
