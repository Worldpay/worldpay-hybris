package com.worldpay.fraud.symptoms;

import com.worldpay.constants.WorldpayapiConstants;
import de.hybris.platform.fraud.strategy.AbstractOrderFraudSymptomDetection;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

/**
 * The abstract implementation of the {@link AbstractOrderFraudSymptomDetection}.
 * The fraud score limit is retrieved from the {@code worldpayapi.fraud.scoreLimit} property.
 */
public abstract class AbstractWorldpayOrderFraudSymptomDetection extends AbstractOrderFraudSymptomDetection {

    public static final String SCORE_LIMIT_PROPERTY_NAME = WorldpayapiConstants.EXTENSIONNAME + ".fraud.scoreLimit";

    private ConfigurationService configurationService;

    protected double getScoreLimit() {
        final String configuredScoreLimit = configurationService.getConfiguration().getString(SCORE_LIMIT_PROPERTY_NAME);
        return Double.parseDouble(configuredScoreLimit);
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
