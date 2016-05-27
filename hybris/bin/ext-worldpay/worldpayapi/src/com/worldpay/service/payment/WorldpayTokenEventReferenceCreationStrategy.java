package com.worldpay.service.payment;

/***
 * Exposes methods to handle the token event reference.
 */
public interface WorldpayTokenEventReferenceCreationStrategy {


    /***
     * This creates the token event reference used in the call to Worldpay for token request
     *
     * @return
     */
    String createTokenEventReference();
}
