package com.worldpay.transaction;

import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Defines the methods to be implemented by the strategy to generate the {@link PaymentTransactionModel} code
 */
public interface EntryCodeStrategy {

    /**
     * Generates the desired PaymentTransaction code using the information hold by {@param transaction}
     *
     * @param transaction The {@link PaymentTransactionModel} to generate the code for
     * @return The generated code for the PaymentTransaction {@link PaymentTransactionModel}
     */
    String generateCode(final PaymentTransactionModel transaction);
}
