package com.worldpay.service.apm;

import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.core.model.order.CartModel;

/**
 * Defines the methods used to check the availability of an APM in the current order
 */
public interface APMAvailabilityService {

    /**
     * Checks that the {@param WorldpayAPMConfigurationModel} is available to be used to process the payment of the order in {@param cartModel}
     *
     * @param apmConfiguration The configuration of the APM to check {@link WorldpayAPMConfigurationModel}
     * @param cartModel        The current order {@link CartModel}
     * @return Availability to use the APM in the order {@param cartModel}
     */
    boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration, final CartModel cartModel);

}
