package com.worldpay.forms;

public class CSEPaymentForm {

    private String cseToken;
    private String cvc;
    private boolean termsCheck;
    private boolean saveInAccount;
    private String nameOnCard;
    private String expiryYear;
    private String expiryMonth;

    public String getCseToken() {
        return cseToken;
    }

    public void setCseToken(String cseToken) {
        this.cseToken = cseToken;
    }

    public boolean isTermsCheck() {
        return termsCheck;
    }

    public void setTermsCheck(boolean termsCheck) {
        this.termsCheck = termsCheck;
    }

    public boolean isSaveInAccount() {
        return saveInAccount;
    }

    public void setSaveInAccount(boolean saveInAccount) {
        this.saveInAccount = saveInAccount;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(final String cvc) {
        this.cvc = cvc;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(final String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(final String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(final String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }
}
