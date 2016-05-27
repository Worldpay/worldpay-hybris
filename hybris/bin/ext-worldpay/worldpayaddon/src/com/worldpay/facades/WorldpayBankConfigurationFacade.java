package com.worldpay.facades;

import com.worldpay.model.WorldpayAPMConfigurationModel;

import java.util.List;

/**
 * Exposes methods to handle BankConfigurationData
 */
public interface WorldpayBankConfigurationFacade {

    /**
     * Retrieves a list of BankConfigurationData {@link BankConfigurationData} for a given apmCode.
     * @param apmCode
     * @return
     */
    List<BankConfigurationData> getBankConfigurationForAPMCode(final String apmCode);

    /**
     * Specifies whether a paymentMethod is an apm that supports bank transfer based on its {@link WorldpayAPMConfigurationModel}
     * @param paymentMethod
     * @return
     */
    boolean isBankTransferApm(final String paymentMethod);
}
