package com.worldpay.core.dao;

import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Data access to {@link PaymentTransactionModel}
 *
 */
public interface WorldpayPaymentTransactionDao extends Dao {

    /**
     * Find PaymentTransactions that have been created before the (currentTime - waitTimeInMinutes)
     * and orders associated to them are in PAYMENT_PENDING status.
     * Searches through PaymentInfo without its subtypes.
     *
     * @param waitTimeInMinutes wait time in minutes
     * @return list of {@link PaymentTransactionModel}
     */
    List<PaymentTransactionModel> findPendingPaymentTransactions(final int waitTimeInMinutes);

    /**
     * Find PaymentTransactions by the Request Id (Worldpay order code). Only search for Payment Transactions that belong to Orders
     *
     * @param requestId Worldpay order code
     * @return {@link PaymentTransactionModel}
     */
    PaymentTransactionModel findPaymentTransactionByRequestIdFromOrdersOnly(final String requestId);

    /**
     * Find PaymentTransactions by the Request Id (Worldpay order code). Search for Payment Transactions that belong to Orders or Carts
     *
     * @param requestId Worldpay order code
     * @return {@link PaymentTransactionModel}
     */
    PaymentTransactionModel findPaymentTransactionByRequestId(final String requestId);

    /**
     * Find PaymentTransactions with APM PaymentInfo with set timeoutDate older than the current date.
     *
     * @return list of {@link PaymentTransactionModel}
     */
    List<PaymentTransactionModel> findCancellablePendingAPMPaymentTransactions();
}
