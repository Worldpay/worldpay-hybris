package com.worldpay.core.services;

import java.util.Set;

import com.worldpay.model.WorldpayAPMConfigurationModel;

/**
 * Service providing lookup functionality for Alternative Payment Methods (APM).
 * @spring.bean apmConfigurationLookupService
 */
public interface APMConfigurationLookupService {

    /**
     * Returns {@link WorldpayAPMConfigurationModel} related to the payment type code (i.e. SOFORT-SSL)
     * @param paymentTypeCode the payment type
     * @return the aPM configuration for code
     */
    WorldpayAPMConfigurationModel getAPMConfigurationForCode(final String paymentTypeCode);

    /**
     * Returns a {@link Set} containing the all the configured payment type codes
     * @return the all apm payment type codes
     */
    Set<String> getAllApmPaymentTypeCodes();
}
