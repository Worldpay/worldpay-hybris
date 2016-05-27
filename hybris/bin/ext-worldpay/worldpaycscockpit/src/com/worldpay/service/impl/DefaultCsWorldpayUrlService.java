/**
 *
 */
package com.worldpay.service.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

import static org.apache.commons.lang.StringUtils.isEmpty;


/**
 * Customer Services implementation of the {@link AbstractWorldpayUrlService}.
 * <p>
 * Uses a path prefix to be able to build a fully qualified url for customer services. Has to be different from store as
 * this is the url that worldpay will return to
 * </p>
 */
public class DefaultCsWorldpayUrlService extends AbstractWorldpayUrlService {

    protected static final String WORLDPAY_CSCOCKPIT_PREFIX = "worldpay.cscockpit.prefix";

    private ConfigurationService configurationService;
    private String pathPrefix;

    @Override
    public String getFullSuccessURL() throws WorldpayConfigurationException {
        return getPathPrefix() + getSuccessPath();
    }

    @Override
    public String getFullPendingURL() throws WorldpayConfigurationException {
        return getPathPrefix() + getPendingPath();
    }

    @Override
    public String getFullFailureURL() throws WorldpayConfigurationException {
        return getPathPrefix() + getFailurePath();
    }

    @Override
    public String getFullCancelURL() throws WorldpayConfigurationException {
        return getPathPrefix() + getCancelPath();
    }

    @Override
    public String getFullErrorURL() throws WorldpayConfigurationException {
        return getPathPrefix() + getErrorPath();
    }

    /**
     * In the CS Cockpit the CS Agent will not be asked for the 3D Secure Authentication. In the CS Cockpit the MOTO (Mail Order Telephone Order) merchant
     * should be set.
     *
     * @return null
     * @throws WorldpayConfigurationException
     */
    @Override
    public String getFullThreeDSecureTermURL() throws WorldpayConfigurationException {
        return null;
    }

    /**
     * Get the path prefix from config
     *
     * @return the path prefix of the current cscockpit application
     */
    private String getPathPrefix() throws WorldpayConfigurationException {
        if (isEmpty(pathPrefix)) {
            final String pathPrefixProperty = configurationService.getConfiguration().getString(WORLDPAY_CSCOCKPIT_PREFIX);
            if (isEmpty(pathPrefixProperty)) {
                throw new WorldpayConfigurationException("Missing CS Cockpit prefix of the return HOP url. Please check your configuration");
            }
            this.pathPrefix = pathPrefixProperty;
        }
        return pathPrefix;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
