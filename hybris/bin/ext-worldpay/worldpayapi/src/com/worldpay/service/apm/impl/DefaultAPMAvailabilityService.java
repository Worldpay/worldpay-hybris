package com.worldpay.service.apm.impl;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import com.worldpay.service.apm.strategy.APMAvailabilityStrategy;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultAPMAvailabilityService implements APMAvailabilityService {

    private List<APMAvailabilityStrategy> apmAvailabilityStrategyList;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel) {
        for (final APMAvailabilityStrategy apmAvailabilityStrategy : apmAvailabilityStrategyList) {
            if (!apmAvailabilityStrategy.isAvailable(apmConfiguration, cartModel)) {
                return false;
            }
        }
        return true;
    }

    public void setApmAvailabilityStrategyList(final List<APMAvailabilityStrategy> apmAvailabilityStrategyList) {
        this.apmAvailabilityStrategyList = apmAvailabilityStrategyList;
    }
}
