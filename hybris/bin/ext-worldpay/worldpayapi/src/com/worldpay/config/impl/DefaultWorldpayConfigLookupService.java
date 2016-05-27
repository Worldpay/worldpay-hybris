package com.worldpay.config.impl;

import com.worldpay.config.Environment;
import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.exception.WorldpayConfigurationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


import static java.text.MessageFormat.format;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayConfigLookupService implements WorldpayConfigLookupService {

    protected static final String WORLDPAY_CONFIG_VERSION = "worldpay.config.version";
    protected static final String WORLDPAY_CONFIG_ENDPOINT = "worldpay.config.endpoint";
    protected static final String WORLDPAY_CONFIG_ENVIRONMENT = "worldpay.config.environment";
    protected static final String ERROR_RETRIEVING_CONFIGURATION = "There was an error retrieving the configuration parameters for Worldpay. Please check the values of [{0}] and [{1}]";

    private ConfigurationService configurationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayConfig lookupConfig() throws WorldpayConfigurationException {
        final String version = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_VERSION);
        final String environment = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT);
        final String endpoint = configurationService.getConfiguration().getString(WORLDPAY_CONFIG_ENDPOINT + "." + environment);

        if (StringUtils.isBlank(version) || StringUtils.isBlank(endpoint)) {
            throw new WorldpayConfigurationException(format(ERROR_RETRIEVING_CONFIGURATION, WORLDPAY_CONFIG_ENDPOINT, WORLDPAY_CONFIG_VERSION));
        }

        return new WorldpayConfig(version, new Environment(endpoint, Environment.EnvironmentRole.valueOf(environment)));
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
