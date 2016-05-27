package com.worldpay.worldpayasm.strategies.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.merchant.WorldpayMerchantConfigDataService;
import com.worldpay.strategy.WorldpayMerchantStrategy;
import com.worldpay.worldpayasm.asm.WorldpayASMService;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

public class DefaultWorldpayASMMerchantStrategy implements WorldpayMerchantStrategy {

    protected static final String ASM_MERCHANT = "asm";

    private WorldpayMerchantConfigDataService worldpayMerchantConfigDataService;
    private WorldpayASMService worldpayASMService;

    @Override
    public WorldpayMerchantConfigData getMerchant(final UiExperienceLevel uiExperienceLevel) {
        final Map<String, WorldpayMerchantConfigData> merchantConfiguration = worldpayMerchantConfigDataService.getMerchantConfiguration();
        if (worldpayASMService.isASMEnabled()) {
            return merchantConfiguration.get(ASM_MERCHANT);
        } else {
            return UiExperienceLevel.MOBILE.equals(uiExperienceLevel) ? merchantConfiguration.get(MOBILE_MERCHANT) : merchantConfiguration.get(DESKTOP_MERCHANT);
        }
    }

    @Override
    public WorldpayMerchantConfigData getCustomerServiceMerchant() {
        return worldpayMerchantConfigDataService.getMerchantConfiguration().get(CUSTOMER_SERVICE_MERCHANT);
    }

    @Required
    public void setWorldpayMerchantConfigDataService(final WorldpayMerchantConfigDataService worldpayMerchantConfigDataService) {
        this.worldpayMerchantConfigDataService = worldpayMerchantConfigDataService;
    }

    @Required
    public void setWorldpayASMService(final WorldpayASMService worldpayASMService) {
        this.worldpayASMService = worldpayASMService;
    }
}
