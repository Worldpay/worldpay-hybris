package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the current time.
 */
public class WorldpayCurrentTimeAppender implements WorldpaySupportEmailAppender {
    @Override
    public String appendContent() {
        StringBuilder currentTime = new StringBuilder();
        final DateTime dateTime = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE d MMMM, H:mm:ss, zzzz (z)");
        currentTime.append(System.lineSeparator()).append("Time: ");
        currentTime.append(TAB).append(fmt.print(dateTime)).append(System.lineSeparator());
        return currentTime.toString();
    }
}
