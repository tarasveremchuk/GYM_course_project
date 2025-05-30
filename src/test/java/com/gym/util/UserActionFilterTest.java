package com.gym.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserActionFilterTest {

    private UserActionFilter filter;
    
    @Mock
    private ILoggingEvent mockEvent;
    
    @BeforeEach
    void setUp() {
        filter = new UserActionFilter();
    }
    
    @Test
    void testDecide_WithUserActionMarker_ShouldAccept() {
        
        when(mockEvent.getMessage()).thenReturn("This is a message with [USER_ACTION] marker");
        
        
        FilterReply result = filter.decide(mockEvent);
        
        
        assertEquals(FilterReply.ACCEPT, result);
    }
    
    @Test
    void testDecide_WithUserActionsLogger_ShouldAccept() {
        
        when(mockEvent.getMessage()).thenReturn("This is a message without marker");
        when(mockEvent.getLoggerName()).thenReturn("com.gym.useractions");
        
        
        FilterReply result = filter.decide(mockEvent);
        
        
        assertEquals(FilterReply.ACCEPT, result);
    }
    
    @Test
    void testDecide_WithoutMarkerOrLogger_ShouldDeny() {
        
        when(mockEvent.getMessage()).thenReturn("This is a regular message");
        when(mockEvent.getLoggerName()).thenReturn("com.gym.other");
        
        
        FilterReply result = filter.decide(mockEvent);
        
        
        assertEquals(FilterReply.DENY, result);
    }
    
    @Test
    void testDecide_WithNullLoggerName_ShouldNotThrowException() {
        
        when(mockEvent.getMessage()).thenReturn("This is a regular message");
        when(mockEvent.getLoggerName()).thenReturn(null);
        
        
        assertDoesNotThrow(() -> {
            FilterReply result = filter.decide(mockEvent);
            assertEquals(FilterReply.DENY, result);
        });
    }
}
