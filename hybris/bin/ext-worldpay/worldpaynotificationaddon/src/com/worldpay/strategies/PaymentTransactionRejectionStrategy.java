package com.worldpay.strategies;

import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Strategy for rejecting all entries {@link PaymentTransactionEntryModel} of a paymentTransaction {@link PaymentTransactionModel}.
 */
public interface PaymentTransactionRejectionStrategy {

    /**
     * This method marks all paymentTransactionEntries {@link PaymentTransactionEntryModel} as rejected, and re-triggers the fulfilment process.
     *
     * @param paymentTransactionModel
     */
    void executeRejection(final PaymentTransactionModel paymentTransactionModel);
}
