package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the hybris version
 */
public class WorldpayHybrisVersionAppender implements WorldpaySupportEmailAppender {

    protected static final String HYBRIS_BUILD_VERSION_KEY = "build.version";

    private ConfigurationService configurationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final String hybrisVersion = configurationService.getConfiguration().getString(HYBRIS_BUILD_VERSION_KEY);
        return "Hybris version: " + hybrisVersion + System.lineSeparator();
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
