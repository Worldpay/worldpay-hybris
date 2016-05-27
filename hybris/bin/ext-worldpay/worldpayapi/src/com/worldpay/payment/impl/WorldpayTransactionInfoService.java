package com.worldpay.payment.impl;

import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.impl.DefaultTransactionInfoService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 * Worldpay specific implementation of the {@link DefaultTransactionInfoService}. Allows for AUTHORISED status to be
 * looked up as a successful transaction status
 */
public class WorldpayTransactionInfoService extends DefaultTransactionInfoService {

    /**
     * Determines if a transaction entry {@link PaymentTransactionEntryModel} was successful or not based on the Worldpay rules.
     * @param entry
     * @return
     */
    @Override
    public boolean isSuccessful(final PaymentTransactionEntryModel entry) {
        if (isEntryPending(entry)) {
            return false;
        }
        final String transactionStatus = extractTransactionStatusFromEntry(entry);
        return transactionStatus.equalsIgnoreCase(TransactionStatus.ACCEPTED.name());
    }

    protected Boolean isEntryPending(PaymentTransactionEntryModel entry) {
        return entry.getType() == PaymentTransactionType.AUTHORIZATION ? Boolean.FALSE : entry.getPending();
    }

    protected String extractTransactionStatusFromEntry(PaymentTransactionEntryModel entry) {
        return entry.getTransactionStatus() != null ? entry.getTransactionStatus().trim() : "";
    }
}
