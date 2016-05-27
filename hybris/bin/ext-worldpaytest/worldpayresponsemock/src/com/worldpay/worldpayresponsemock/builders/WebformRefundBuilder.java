package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.ShopperWebformRefundDetails;

public final class WebformRefundBuilder {

    private String webformId;
    private String paymentId;
    private String webformStatus;
    private String refundReason;
    private String webformURL;
    private String refundId;
    private String transactionAmount;
    private String currencyCode;
    private String exponent;

    private WebformRefundBuilder() {
    }

    public static WebformRefundBuilder aWebformRefundBuilder() {
        return new WebformRefundBuilder();
    }

    public WebformRefundBuilder withWebformId(String webformId) {
        this.webformId = webformId;
        return this;
    }

    public WebformRefundBuilder withPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    public WebformRefundBuilder withWebformStatus(String webformStatus) {
        this.webformStatus = webformStatus;
        return this;
    }

    public WebformRefundBuilder withRefundReason(String refundReason) {
        this.refundReason = refundReason;
        return this;
    }

    public WebformRefundBuilder withWebformURL(String webformURL) {
        this.webformURL = "<![CDATA[" + webformURL + "]]>";
        return this;
    }

    public WebformRefundBuilder withRefundId(String refundId) {
        this.refundId = "<![CDATA[" + refundId + "]]>";
        return this;
    }


    public WebformRefundBuilder withTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public WebformRefundBuilder withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public WebformRefundBuilder withExponent(String exponent) {
        this.exponent = exponent;
        return this;
    }

    public ShopperWebformRefundDetails build() {
        final ShopperWebformRefundDetails shopperWebformRefundDetails = new ShopperWebformRefundDetails();

        final Amount amount = AmountBuilder.anAmountBuilder().
                withAmount(this.transactionAmount).
                withCurrencyCode(this.currencyCode).
                withExponent(this.exponent).
                build();

        shopperWebformRefundDetails.setAmount(amount);

        shopperWebformRefundDetails.setReason(this.refundReason);
        shopperWebformRefundDetails.setPaymentId(this.paymentId);
        shopperWebformRefundDetails.setWebformStatus(this.webformStatus);
        shopperWebformRefundDetails.setWebformURL(this.webformURL);
        shopperWebformRefundDetails.setWebformId(this.webformId);
        shopperWebformRefundDetails.setRefundId(this.refundId);

        return shopperWebformRefundDetails;
    }
}
