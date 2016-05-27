package com.worldpay.merchant;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;

import java.util.Map;

/**
 * Interface that provides the method to retrieve the MerchantConfigurations.
 */
public interface WorldpayMerchantConfigDataService {
    /**
     * Returns the merchant configuration configured for the current application.
     *
     * @return Map that contains the configured merchants for the application.
     */
    Map<String, WorldpayMerchantConfigData> getMerchantConfiguration();
}
