package com.worldpay.transaction.impl;

import com.worldpay.transaction.EntryCodeStrategy;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.List;

/**
 * Strategy that implements the {@link EntryCodeStrategy}
 */
public class WorldpayEntryCodeStrategyImpl implements EntryCodeStrategy {

    /**
     * {@inheritDoc}
     * <p>
     * Generates the PaymentTransaction code using the number of entries and the paymentTransaction code.
     * <p>
     * When there are no PaymentTransactionEntries {@link PaymentTransactionEntryModel} in the PaymentTransactionModel, the generated
     * code is the existing code and a "-1" as suffix.
     * <p>
     * When there are PaymentTransactionEntries, the code generated is the existing code and as suffix, a "-" followed by the number of
     * entries in the PaymentTransactionModel
     *
     * @param paymentTransaction The {@link PaymentTransactionModel} to generate the code for
     * @return
     */
    @Override
    public String generateCode(final PaymentTransactionModel paymentTransaction) {
        final String transactionCode = paymentTransaction.getCode();
        final List<PaymentTransactionEntryModel> entries = paymentTransaction.getEntries();
        if (entries == null) {
            return transactionCode + "-1";
        } else {
            return transactionCode + "-" + (entries.size() + 1);
        }
    }
}
