package com.worldpay.config;

import com.worldpay.exception.WorldpayConfigurationException;

/**
 * Service providing Worldpay configuration lookup functionality. The service returns a {@link WorldpayConfig}.
 *
 * @spring.bean worldpayConfigLookupService
 */
public interface WorldpayConfigLookupService {

    /**
     * Lookup the WorldpayConfig to use for Worldpay calls
     *
     * @return WorldpayConfig
     */
    WorldpayConfig lookupConfig() throws WorldpayConfigurationException;
}
