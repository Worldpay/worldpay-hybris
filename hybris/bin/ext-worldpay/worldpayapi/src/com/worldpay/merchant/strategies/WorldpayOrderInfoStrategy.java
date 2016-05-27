package com.worldpay.merchant.strategies;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.AdditionalAuthInfo;

/**
 * Strategy for {@link AdditionalAuthInfo} required for redirect authorisation
 *
 */
public interface WorldpayOrderInfoStrategy {

    /**
     * Rerutns {@link AdditionalAuthInfo} based on the provided {@link WorldpayMerchantConfigData} parameter.
     *
     * @param worldpayMerchantConfigData the worldpay merchant config data
     * @return the additional auth info
     */
    AdditionalAuthInfo getAdditionalAuthInfo(final WorldpayMerchantConfigData worldpayMerchantConfigData);

    /**
     * Populates the {@link AdditionalAuthInfo} object based on the provided {@link WorldpayMerchantConfigData} parameter.
     *
     * @param additionalAuthInfo the additional auth info
     * @param worldpayMerchantConfigData the worldpay merchant config data
     */
    void populateAdditionalAuthInfo(final AdditionalAuthInfo additionalAuthInfo, final WorldpayMerchantConfigData worldpayMerchantConfigData);

}
