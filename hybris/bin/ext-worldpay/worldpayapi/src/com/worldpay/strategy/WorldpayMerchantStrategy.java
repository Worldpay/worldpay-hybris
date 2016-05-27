package com.worldpay.strategy;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;

/**
 * Exposes methods to retrieve the configured merchants.
 */
public interface WorldpayMerchantStrategy {

    String MOBILE_MERCHANT = "mobile";
    String DESKTOP_MERCHANT = "web";
    String CUSTOMER_SERVICE_MERCHANT = "customerService";

    /**
     * Returns the merchant configured for the current uiExperience
     *
     * @param uiExperienceLevel
     * @return
     */
    WorldpayMerchantConfigData getMerchant(final UiExperienceLevel uiExperienceLevel);

    /**
     * Returns the merchant configured for CSCockpit
     *
     * @return
     */
    WorldpayMerchantConfigData getCustomerServiceMerchant();
}
