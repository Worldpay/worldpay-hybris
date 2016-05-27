package com.worldpay.facades.order;

import de.hybris.platform.commercefacades.user.data.AddressData;


/**
 * worldpay specific checkout facade interface. This is overridden from the standard to ensure the worldpay information
 * is included at the correct point. Adds convenience methods required for worldpay interaction
 */
public interface WorldpayPaymentCheckoutFacade {

    /**
     * Sets billing details to the session cart (payment address)
     *
     * @param addressData the address data
     */
    void setBillingDetails(AddressData addressData);


    /**
     * Does the session cart have a billing details (payment address)
     *
     * @return the boolean
     */
    boolean hasBillingDetails();
}
