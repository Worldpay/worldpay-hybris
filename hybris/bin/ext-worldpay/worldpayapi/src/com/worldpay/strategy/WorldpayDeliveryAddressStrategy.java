package com.worldpay.strategy;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Exposes methods to select a delivery address.
 */
public interface WorldpayDeliveryAddressStrategy {

    /**
     * Method for getting the address that will act as main delivery address for a cart
     *
     * @param cartModel
     * @return The delivery address best representing the given cart.
     */
    AddressModel getDeliveryAddress(final CartModel cartModel);
}
