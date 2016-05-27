package com.worldpay.facades.impl;

import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.platform.order.CartService;


/**
 * {@inheritDoc}
 */
public class DefaultAPMAvailabilityFacade implements APMAvailabilityFacade {

    private APMAvailabilityService apmAvailabilityService;
    private CartService cartService;

    /**
     * {@inheritDoc}
     */
    public boolean isAvailable(final WorldpayAPMConfigurationModel apmConfiguration) {
        return apmAvailabilityService.isAvailable(apmConfiguration, cartService.getSessionCart());
    }

    public void setApmAvailabilityService(final APMAvailabilityService apmAvailabilityService) {
        this.apmAvailabilityService = apmAvailabilityService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }
}
