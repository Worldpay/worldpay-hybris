package com.worldpay.facades.payment.merchant;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;

/**
 * Exposes methods to handle the Worldpay merchant configuration.
 */
public interface WorldpayMerchantConfigDataFacade {
    /**
     * Gets the merchant to be used for the given ui experience level on the current site
     *
     * @param uiExperienceLevel
     * @return {@link WorldpayMerchantConfigData}
     */
    WorldpayMerchantConfigData getCurrentSiteMerchantConfigData(final UiExperienceLevel uiExperienceLevel);

    /**
     * Gets the merchant to be used for placing orders in the customer service cockpit
     *
     * @return {@link WorldpayMerchantConfigData}
     */
    WorldpayMerchantConfigData getCustomerServiceMerchantConfigData();
}
