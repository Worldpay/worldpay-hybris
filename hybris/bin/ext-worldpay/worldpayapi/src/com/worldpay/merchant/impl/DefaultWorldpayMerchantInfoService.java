package com.worldpay.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContextAware;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

/**
 * This implementation of {@link WorldpayMerchantInfoService} uses looks the configured map of merchants configured in Spring
 * using the hybris {@link SiteConfigService} and the spring capabilities to retrieve a bean defined in the application context using
 * {@link ApplicationContextAware}
 */
public class DefaultWorldpayMerchantInfoService implements WorldpayMerchantInfoService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayMerchantInfoService.class);

    private WorldpayMerchantConfigDataService worldpayMerchantConfigDataService;
    private SessionService sessionService;
    private BaseSiteService baseSiteService;
    private WorldpayMerchantStrategy worldpayMerchantStrategy;

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws WorldpayConfigurationException
     */
    @Override
    public MerchantInfo getCustomerServicesMerchant() throws WorldpayConfigurationException {
        final WorldpayMerchantConfigData customerServiceMerchantConfigData = worldpayMerchantStrategy.getCustomerServiceMerchant();
        return createMerchantInfo(customerServiceMerchantConfigData);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws WorldpayConfigurationException
     */
    @Override
    public MerchantInfo getCurrentSiteMerchant(final UiExperienceLevel uiExperienceLevel) throws WorldpayConfigurationException {
        final WorldpayMerchantConfigData currentSiteMerchantConfigData = worldpayMerchantStrategy.getMerchant(uiExperienceLevel);
        return createMerchantInfo(currentSiteMerchantConfigData);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws WorldpayConfigurationException
     */
    @Override
    public MerchantInfo getMerchantInfoByCode(final String merchantCode) throws WorldpayConfigurationException {
        final Map<String, WorldpayMerchantConfigData> merchantConfiguration = worldpayMerchantConfigDataService.getMerchantConfiguration();
        return getMerchantConfigDataForCode(merchantConfiguration, merchantCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MerchantInfo getMerchantInfoFromTransaction(final PaymentTransactionModel paymentTransactionModel) throws WorldpayConfigurationException {
        return sessionService.executeInLocalView(new SessionExecutionBody() {
            @Override
            public Object execute() {
                try {
                    baseSiteService.setCurrentBaseSite(paymentTransactionModel.getOrder().getSite().getUid(), false);
                    return getMerchantInfoByCode(paymentTransactionModel.getRequestToken());
                } catch (WorldpayConfigurationException e) {
                    LOG.error(MessageFormat.format("Missing merchant configuration: [{0}]", e.getMessage()), e);
                }
                return null;
            }
        });
    }

    protected MerchantInfo createMerchantInfo(final WorldpayMerchantConfigData worldpayMerchantConfigData) throws WorldpayConfigurationException {
        final MerchantInfo merchantInfo = new MerchantInfo(worldpayMerchantConfigData.getCode(), worldpayMerchantConfigData.getPassword());
        if (worldpayMerchantConfigData.getMacValidation()) {
            merchantInfo.setUsingMacValidation(true);
            merchantInfo.setMacSecret(worldpayMerchantConfigData.getMacSecret());
        }
        return merchantInfo;
    }

    private MerchantInfo getMerchantConfigDataForCode(final Map<String, WorldpayMerchantConfigData> merchantConfiguration, final String merchantCode) throws WorldpayConfigurationException {
        final Optional<WorldpayMerchantConfigData> worldpayMerchantConfigDataOptional =
                merchantConfiguration.values().stream().filter(merchantConfig -> merchantCode.equals(merchantConfig.getCode())).findFirst();
        if (worldpayMerchantConfigDataOptional.isPresent()) {
            return createMerchantInfo(worldpayMerchantConfigDataOptional.get());
        }
        throw new IllegalArgumentException("Could not find a WorldpayMerchantConfigData from: " + merchantCode);
    }

    @Required
    public void setWorldpayMerchantConfigDataService(WorldpayMerchantConfigDataService worldpayMerchantConfigDataService) {
        this.worldpayMerchantConfigDataService = worldpayMerchantConfigDataService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public WorldpayMerchantConfigDataService getWorldpayMerchantConfigDataService() {
        return worldpayMerchantConfigDataService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setWorldpayMerchantStrategy(final WorldpayMerchantStrategy worldpayMerchantStrategy) {
        this.worldpayMerchantStrategy = worldpayMerchantStrategy;
    }
}
