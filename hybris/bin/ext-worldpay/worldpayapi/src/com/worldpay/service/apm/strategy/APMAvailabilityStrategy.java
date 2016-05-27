package com.worldpay.service.apm.strategy;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Defines the methods to be implemented by the different strategies to check the availability
 * of the APM depending on the rules applied {@link WorldpayAPMConfigurationModel}
 */
public interface APMAvailabilityStrategy {

    /**
     * Checks if the APM defined in {@param WorldpayAPMConfigurationModel} can be used to process the payment of {@param cartModel}
     *
     * @param apmConfiguration The configuration of the APM to check {@link WorldpayAPMConfigurationModel}
     * @param cartModel        The current order {@link CartModel}
     * @return Availability to use the APM in the order based on the implemented strategy {@param cartModel}
     */
    boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel);

}
