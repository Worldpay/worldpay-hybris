package com.worldpay.support;

import java.util.Set;

import de.hybris.platform.payment.enums.PaymentTransactionType;

/**
 * Interface that exposes information about the notification cronjobs
 */
public interface WorldpayCronJobSupportInformationService {

    /**
     * Exposes the information about the payment transaction types checked for by notification cronjobs
     *
     * @return
     */
    Set<PaymentTransactionType> getPaymentTransactionType();
}
