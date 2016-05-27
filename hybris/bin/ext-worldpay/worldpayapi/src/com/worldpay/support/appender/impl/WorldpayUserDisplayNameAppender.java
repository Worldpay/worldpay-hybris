package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the current user display name
 */
public class WorldpayUserDisplayNameAppender implements WorldpaySupportEmailAppender {

    private UserService userService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final String userName = userService.getCurrentUser().getDisplayName();
        return System.lineSeparator() + "User: " + userName + System.lineSeparator();
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
