package com.worldpay.strategies;

import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

/**
 * Worldpay Order Modification Process Strategy interface.
 * The strategy is responsible for processing {@link WorldpayOrderModificationModel}
 */
public interface WorldpayOrderModificationProcessStrategy {

    /**
     * Processes the {@link WorldpayOrderModificationModel} for the given payment transaction type.
     * In case of finding a non-pending transaction entry, marks the orderNotificationModification as processed. This is the case when the authorised transaction is created through directXml (CSE)
     *
     * @param paymentTransactionType {@link PaymentTransactionType}
     * @return {@code true} if the operation was successful
     */
    boolean processOrderModificationMessages(final PaymentTransactionType paymentTransactionType);
}
