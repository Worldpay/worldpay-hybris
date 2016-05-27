package com.worldpay.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;

public class PaymentDetailsForm {

    private boolean termsCheck;
    private boolean saveInAccount;
    private boolean useDeliveryAddress;
    private String paymentMethod;
    private AddressForm billingAddress;
    private String shopperBankCode;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean getSaveInAccount() {
        return saveInAccount;
    }

    public void setSaveInAccount(final boolean saveInAccount) {
        this.saveInAccount = saveInAccount;
    }

    public boolean getUseDeliveryAddress() {
        return useDeliveryAddress;
    }

    public void setUseDeliveryAddress(final boolean useDeliveryAddress) {
        this.useDeliveryAddress = useDeliveryAddress;
    }

    public AddressForm getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final AddressForm billingAddress) {
        this.billingAddress = billingAddress;
    }

    public boolean isTermsCheck() {
        return termsCheck;
    }

    public void setTermsCheck(final boolean termsCheck) {
        this.termsCheck = termsCheck;
    }


    public String getShopperBankCode() {
        return shopperBankCode;
    }

    public void setShopperBankCode(String shopperBankCode) {
        this.shopperBankCode = shopperBankCode;
    }

}
