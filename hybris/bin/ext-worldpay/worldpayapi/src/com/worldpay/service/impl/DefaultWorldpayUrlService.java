
package com.worldpay.service.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the {@link WorldpayUrlService}
 * <p>
 * Provides a mechanism for building a fully qualified site url from an absolute path given that the paths are provided
 * via configuration
 * </p>
 */
public class DefaultWorldpayUrlService extends AbstractWorldpayUrlService {

    private BaseSiteService baseSiteService;
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    /**
     * Resolves a given URL to a full URL including server and port, etc.
     *
     * @param url      - the URL to resolve
     * @param isSecure - flag to indicate whether the final URL should use a secure connection or not.
     * @return a full URL including HTTP protocol, server, port, path etc.
     */
    protected String getFullUrl(final String url, final boolean isSecure) throws WorldpayConfigurationException {
        final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();

        final String fullResponseUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(currentBaseSite, isSecure, url);
        if (fullResponseUrl == null) {
            throw new WorldpayConfigurationException("The URL returned from the SiteBaseUrlResolutionService is null. Please check the configuration of the HOP URL's");
        }
        return fullResponseUrl;
    }

    /**
     * {@inheritDoc}
     * See {@link WorldpayUrlService#getFullSuccessURL}
     */
    @Override
    public String getFullSuccessURL() throws WorldpayConfigurationException {
        return getFullUrl(getSuccessPath(), true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * See {@link WorldpayUrlService#getFullPendingURL}
     */
    @Override
    public String getFullPendingURL() throws WorldpayConfigurationException {
        return getFullUrl(getPendingPath(), true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * See {@link WorldpayUrlService#getFullFailureURL}
     */
    @Override
    public String getFullFailureURL() throws WorldpayConfigurationException {
        return getFullUrl(getFailurePath(), true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * See {@link WorldpayUrlService#getFullCancelURL}
     */
    @Override
    public String getFullCancelURL() throws WorldpayConfigurationException {
        return getFullUrl(getCancelPath(), true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * See {@link WorldpayUrlService#getFullErrorURL}
     */
    @Override
    public String getFullErrorURL() throws WorldpayConfigurationException {
        return getFullUrl(getErrorPath(), true);
    }
    /**
     * {@inheritDoc}
     * <p>
     * See {@link WorldpayUrlService#getFullThreeDSecureTermURL()}
     */
    @Override
    public String getFullThreeDSecureTermURL() throws WorldpayConfigurationException {
        return getFullUrl(getThreeDSecureTermPath(), true);
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService() {
        return siteBaseUrlResolutionService;
    }

    @Required
    public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }
}
