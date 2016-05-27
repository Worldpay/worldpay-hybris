package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.Amount;

public final class AmountBuilder {

    public static final String DEBIT_CREDIT_INDICATOR = "debit";
    private String transactionAmount = "100";
    private String exponent = "2";
    private String currencyCode = "USD";

    private AmountBuilder() {
    }

    public static AmountBuilder anAmountBuilder() {
        return new AmountBuilder();
    }

    public AmountBuilder withAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public AmountBuilder withExponent(String exponent) {
        this.exponent = exponent;
        return this;
    }

    public AmountBuilder withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public Amount build() {
        final Amount amount = new Amount();
        amount.setValue(transactionAmount);
        amount.setCurrencyCode(currencyCode);
        amount.setExponent(String.valueOf(exponent));
        amount.setDebitCreditIndicator(DEBIT_CREDIT_INDICATOR);
        return amount;
    }
}
