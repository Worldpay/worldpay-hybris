package com.worldpay.widgets.controllers;

import com.worldpay.exception.WorldpayException;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.cscockpit.widgets.controllers.CardPaymentController;


/**
 * Interface defining the worldpay additions to the {@link CardPaymentController}
 *
 */
public interface WorldpayCardPaymentController extends CardPaymentController {

    /**
     * Make the redirect authorise call through to worldpay and return the related HostedOrderPageData
     *
     * @throws worldpayException the world pay exception
     */
    void redirectAuthorise() throws WorldpayException;

    /**
     * Sets payment data.
     *
     * @param hopData the hop data
     */
    void setPaymentData(PaymentData hopData);

    /**
     * Gets payment data.
     *
     * @return the payment data
     */
    PaymentData getPaymentData();
}
