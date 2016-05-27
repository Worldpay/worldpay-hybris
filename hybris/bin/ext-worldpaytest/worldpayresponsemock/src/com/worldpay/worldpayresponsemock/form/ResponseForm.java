package com.worldpay.worldpayresponsemock.form;

public class ResponseForm implements java.io.Serializable {

    private String selectedPaymentMethod;
    private String transactionAmount;
    private String currencyCode;
    private String cardHolderName;
    private String worldpayOrderCode;
    private Integer exponent;
    private String currentMonth;
    private String riskValue;
    private String cardYear;
    private String siteId;
    private String cardMonth;
    private String currentDay;
    private String currentYear;
    private String responseType;
    private String merchantCode;
    private Integer responseCode;
    private String responseDescription;
    private String testCreditCard;
    private String ccPaymentType;
    private String apmPaymentType;
    private String finalScore;
    private String lastEvent;
    private String selectedRiskScore;
    private String journalType;
    private String aavAddress;
    private String aavCardholderName;
    private String aavEmail;
    private String aavPostcode;
    private String aavTelephone;
    private String authenticatedShopperId;
    private String tokenEventReference;
    private String tokenReason;
    private String tokenEvent;
    private String paymentTokenId;
    private String tokenExpiryDay;
    private String tokenExpiryMonth;
    private String tokenExpiryYear;
    private String tokenDetailsEventReference;
    private String tokenDetailsReason;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private String tokenCardHolderName;
    private String lastName;
    private String address1;
    private String address2;
    private String address3;
    private String postalCode;
    private String city;
    private String countryCode;
    private String cardSubBrand;
    private String cardBrand;
    private String issuerCountry;
    private String obfuscatedPAN;
    private String selectToken;
    private String webformId;
    private String paymentId;
    private String webformStatus;
    private String refundReason;
    private String webformURL;
    private String refundId;
    private String reference;

    public String getWebformId() {
        return webformId;
    }

    public void setWebformId(String webformId) {
        this.webformId = webformId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getWebformStatus() {
        return webformStatus;
    }

    public void setWebformStatus(String webformStatus) {
        this.webformStatus = webformStatus;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getWebformURL() {
        return webformURL;
    }

    public void setWebformURL(String webformURL) {
        this.webformURL = webformURL;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public void setTransactionAmount(final String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCardHolderName(final String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setWorldpayOrderCode(final String worldpayOrderCode) {
        this.worldpayOrderCode = worldpayOrderCode;
    }

    public String getWorldpayOrderCode() {
        return worldpayOrderCode;
    }

    public void setExponent(final Integer exponent) {
        this.exponent = exponent;
    }

    public Integer getExponent() {
        return exponent;
    }

    public void setCurrentMonth(final String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setRiskValue(final String riskValue) {
        this.riskValue = riskValue;
    }

    public String getRiskValue() {
        return riskValue;
    }

    public void setCardYear(final String cardYear) {
        this.cardYear = cardYear;
    }

    public String getCardYear() {
        return cardYear;
    }

    public void setSiteId(final String siteId) {
        this.siteId = siteId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setCardMonth(final String cardMonth) {
        this.cardMonth = cardMonth;
    }

    public String getCardMonth() {
        return cardMonth;
    }

    public void setCurrentDay(final String currentDay) {
        this.currentDay = currentDay;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentYear(final String currentYear) {
        this.currentYear = currentYear;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setResponseType(final String responseType) {
        this.responseType = responseType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setMerchantCode(final String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getTestCreditCard() {
        return testCreditCard;
    }

    public void setTestCreditCard(String testCreditCard) {
        this.testCreditCard = testCreditCard;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public String getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(String selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(final String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public String getSelectedRiskScore() {
        return selectedRiskScore;
    }

    public void setSelectedRiskScore(String selectedRiskScore) {
        this.selectedRiskScore = selectedRiskScore;
    }

    public String getJournalType() {
        return journalType;
    }

    public void setJournalType(final String journalType) {
        this.journalType = journalType;
    }

    public String getApmPaymentType() {
        return apmPaymentType;
    }

    public void setApmPaymentType(String apmPaymentType) {
        this.apmPaymentType = apmPaymentType;
    }

    public String getCcPaymentType() {
        return ccPaymentType;
    }

    public void setCcPaymentType(String ccPaymentType) {
        this.ccPaymentType = ccPaymentType;
    }

    public String getAavAddress() {
        return aavAddress;
    }

    public void setAavAddress(String aavAddress) {
        this.aavAddress = aavAddress;
    }

    public String getAavCardholderName() {
        return aavCardholderName;
    }

    public void setAavCardholderName(String aavCardholderName) {
        this.aavCardholderName = aavCardholderName;
    }

    public String getAavEmail() {
        return aavEmail;
    }

    public void setAavEmail(String aavEmail) {
        this.aavEmail = aavEmail;
    }

    public String getAavPostcode() {
        return aavPostcode;
    }

    public void setAavPostcode(String aavPostcode) {
        this.aavPostcode = aavPostcode;
    }

    public String getAavTelephone() {
        return aavTelephone;
    }

    public void setAavTelephone(String aavTelephone) {
        this.aavTelephone = aavTelephone;
    }

    public String getAuthenticatedShopperId() {
        return authenticatedShopperId;
    }

    public void setAuthenticatedShopperId(final String authenticatedShopperId) {
        this.authenticatedShopperId = authenticatedShopperId;
    }

    public String getTokenEventReference() {
        return tokenEventReference;
    }

    public void setTokenEventReference(final String tokenEventReference) {
        this.tokenEventReference = tokenEventReference;
    }

    public String getTokenReason() {
        return tokenReason;
    }

    public void setTokenReason(final String tokenReason) {
        this.tokenReason = tokenReason;
    }

    public String getTokenEvent() {
        return tokenEvent;
    }

    public void setTokenEvent(final String tokenEvent) {
        this.tokenEvent = tokenEvent;
    }

    public String getPaymentTokenId() {
        return paymentTokenId;
    }

    public void setPaymentTokenId(final String paymentTokenId) {
        this.paymentTokenId = paymentTokenId;
    }

    public String getTokenExpiryDay() {
        return tokenExpiryDay;
    }

    public void setTokenExpiryDay(final String tokenExpiryDay) {
        this.tokenExpiryDay = tokenExpiryDay;
    }

    public String getTokenExpiryMonth() {
        return tokenExpiryMonth;
    }

    public void setTokenExpiryMonth(final String tokenExpiryMonth) {
        this.tokenExpiryMonth = tokenExpiryMonth;
    }

    public String getTokenExpiryYear() {
        return tokenExpiryYear;
    }

    public void setTokenExpiryYear(final String tokenExpiryYear) {
        this.tokenExpiryYear = tokenExpiryYear;
    }

    public String getTokenDetailsEventReference() {
        return tokenDetailsEventReference;
    }

    public void setTokenDetailsEventReference(final String tokenDetailsEventReference) {
        this.tokenDetailsEventReference = tokenDetailsEventReference;
    }

    public String getTokenDetailsReason() {
        return tokenDetailsReason;
    }

    public void setTokenDetailsReason(final String tokenDetailsReason) {
        this.tokenDetailsReason = tokenDetailsReason;
    }

    public String getCardExpiryMonth() {
        return cardExpiryMonth;
    }

    public void setCardExpiryMonth(final String cardExpiryMonth) {
        this.cardExpiryMonth = cardExpiryMonth;
    }

    public String getCardExpiryYear() {
        return cardExpiryYear;
    }

    public void setCardExpiryYear(final String cardExpiryYear) {
        this.cardExpiryYear = cardExpiryYear;
    }

    public String getTokenCardHolderName() {
        return tokenCardHolderName;
    }

    public void setTokenCardHolderName(final String tokenCardHolderName) {
        this.tokenCardHolderName = tokenCardHolderName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(final String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(final String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(final String address3) {
        this.address3 = address3;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCardSubBrand() {
        return cardSubBrand;
    }

    public void setCardSubBrand(final String cardSubBrand) {
        this.cardSubBrand = cardSubBrand;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(final String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getIssuerCountry() {
        return issuerCountry;
    }

    public void setIssuerCountry(final String issuerCountry) {
        this.issuerCountry = issuerCountry;
    }

    public String getObfuscatedPAN() {
        return obfuscatedPAN;
    }

    public void setObfuscatedPAN(final String obfuscatedPAN) {
        this.obfuscatedPAN = obfuscatedPAN;
    }

    public String getSelectToken() {
        return selectToken;
    }

    public void setSelectToken(String selectToken) {
        this.selectToken = selectToken;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
}