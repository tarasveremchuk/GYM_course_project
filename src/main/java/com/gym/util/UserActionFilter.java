package com.gym.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


public class UserActionFilter extends Filter<ILoggingEvent> {

    private static final String USER_ACTION_MARKER = "[USER_ACTION]";

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().contains(USER_ACTION_MARKER) || 
            (event.getLoggerName() != null && event.getLoggerName().equals("com.gym.useractions"))) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
