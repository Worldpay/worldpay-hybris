package com.worldpay.commands;

import de.hybris.platform.payment.commands.result.SubscriptionResult;


/**
 * Extension of SubscriptionResult to add the Alternative Payment Method information.
 */
public class WorldpaySubscriptionAuthorizeResult extends SubscriptionResult {
    // For use when alternate payment method has been chosen
    private boolean paymentRedirectRequired;
    private String paymentRedirectUrl;

    public boolean isPaymentRedirectRequired() {
        return paymentRedirectRequired;
    }

    public void setPaymentRedirectRequired(final boolean paymentRedirectRequired) {
        this.paymentRedirectRequired = paymentRedirectRequired;
    }

    public String getPaymentRedirectUrl() {
        return paymentRedirectUrl;
    }

    public void setPaymentRedirectUrl(final String paymentRedirectUrl) {
        this.paymentRedirectUrl = paymentRedirectUrl;
    }
}
