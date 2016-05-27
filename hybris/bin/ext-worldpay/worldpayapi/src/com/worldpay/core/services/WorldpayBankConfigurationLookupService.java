package com.worldpay.core.services;

import com.worldpay.model.WorldpayBankConfigurationModel;

import java.util.List;

/**
 * Service providing lookup functionality for Bank configurations.
 * @spring.bean worldpayBankConfigurationLookupService
 */
public interface WorldpayBankConfigurationLookupService {

    /**
     * Returns {@link WorldpayBankConfigurationModel} related to the APM code
     * @param apmCode string representing the APM code
     * @return the list of active banks configured for the APM
     */
    List<WorldpayBankConfigurationModel> getActiveBankConfigurationsForCode(final String apmCode);
}
