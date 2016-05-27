package com.worldpay.merchant.strategies.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;

/**
 * Default implementation of {@link WorldpayOrderInfoStrategy}
 */
public class DefaultWorldpayOrderInfoStrategy implements WorldpayOrderInfoStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public AdditionalAuthInfo getAdditionalAuthInfo(final WorldpayMerchantConfigData worldpayMerchantConfigData) {
        final AdditionalAuthInfo additionalAuthInfo = new AdditionalAuthInfo();
        populateAdditionalAuthInfo(additionalAuthInfo, worldpayMerchantConfigData);
        return additionalAuthInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateAdditionalAuthInfo(final AdditionalAuthInfo additionalAuthInfo, final WorldpayMerchantConfigData worldpayMerchantConfigData) {
        additionalAuthInfo.setOrderContent(worldpayMerchantConfigData.getOrderContent());
        additionalAuthInfo.setInstallationId(worldpayMerchantConfigData.getInstallationId());
        additionalAuthInfo.setStatementNarrative(worldpayMerchantConfigData.getStatementNarrative());
    }
}
