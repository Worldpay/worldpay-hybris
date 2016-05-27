package com.worldpay.support.appender.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import static java.text.MessageFormat.format;

/**
 * Implementation of {@see WorldpaySupportEmailAppender } to include the hybris version
 */
public class WorldpayAddonVersionAppender implements WorldpaySupportEmailAppender {

    protected static final String WORLDPAY_ADDON_VERSION_KEY = "worldpay.addon.version";
    private ConfigurationService configurationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String appendContent() {
        final String addonVersion = configurationService.getConfiguration().getString(WORLDPAY_ADDON_VERSION_KEY);
        if (StringUtils.isBlank(addonVersion)) {
            return StringUtils.EMPTY;
        }

        return format("{0}Worldpay Plugin Version: {1}{0}", System.lineSeparator(), addonVersion);
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
