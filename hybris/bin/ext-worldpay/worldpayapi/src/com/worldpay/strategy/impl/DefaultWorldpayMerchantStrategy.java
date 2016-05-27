package com.worldpay.strategy.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

public class DefaultWorldpayMerchantStrategy implements WorldpayMerchantStrategy {

    private WorldpayMerchantConfigDataService worldpayMerchantConfigDataService;

    @Override
    public WorldpayMerchantConfigData getMerchant(final UiExperienceLevel uiExperienceLevel) {
        final Map<String, WorldpayMerchantConfigData> merchantConfiguration = worldpayMerchantConfigDataService.getMerchantConfiguration();
        return UiExperienceLevel.MOBILE.equals(uiExperienceLevel) ? merchantConfiguration.get(MOBILE_MERCHANT) : merchantConfiguration.get(DESKTOP_MERCHANT);
    }

    @Override
    public WorldpayMerchantConfigData getCustomerServiceMerchant() {
        return worldpayMerchantConfigDataService.getMerchantConfiguration().get(CUSTOMER_SERVICE_MERCHANT);
    }


    @Required
    public void setWorldpayMerchantConfigDataService(final WorldpayMerchantConfigDataService worldpayMerchantConfigDataService) {
        this.worldpayMerchantConfigDataService = worldpayMerchantConfigDataService;
    }
}
