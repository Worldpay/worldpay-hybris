package com.worldpay.core.checkout;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Provides specific behaviour to the commerce checkout service to set the merchant transaction code in the same style
 * as other places that use worldpay
 */
public interface WorldpayCheckoutService {
    /**
     * This method is used for saving the billing address in the cartModel
     *
     * @param cartModel the cart model
     * @param addressModel the address model
     */
    void setPaymentAddress(CartModel cartModel, AddressModel addressModel);
}
