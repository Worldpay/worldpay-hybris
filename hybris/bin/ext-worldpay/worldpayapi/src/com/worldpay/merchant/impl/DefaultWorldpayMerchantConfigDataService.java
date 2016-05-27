package com.worldpay.merchant.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * Default implementation of {@link WorldpayMerchantConfigDataService}
 */
public class DefaultWorldpayMerchantConfigDataService implements WorldpayMerchantConfigDataService, ApplicationContextAware {

    protected static final String WORLDPAY_MERCHANT_CONFIG = "worldpaymerchantconfig";

    private SiteConfigService siteConfigService;
    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, WorldpayMerchantConfigData> getMerchantConfiguration() {
        final String merchantConfigurationBeanName = siteConfigService.getProperty(WORLDPAY_MERCHANT_CONFIG);
        return getConfiguredMerchants(merchantConfigurationBeanName);
    }

    private Map<String, WorldpayMerchantConfigData> getConfiguredMerchants(final String merchantConfigurationBeanName) {
        return (Map<String, WorldpayMerchantConfigData>) applicationContext.getBean(merchantConfigurationBeanName);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Required
    public void setSiteConfigService(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }
}
