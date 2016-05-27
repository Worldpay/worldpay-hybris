package com.worldpay.forms.validation;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.apm.APMAvailabilityService;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.order.CartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.lang.Boolean.FALSE;


@Component ("paymentDetailsFormValidator")
public class PaymentDetailsFormValidator implements Validator {

    protected static final String GLOBAL_MISSING_DELIVERY_ADDRESS = "checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg";
    protected static final String CHECKOUT_ERROR_TERMS_NOT_ACCEPTED = "checkout.error.terms.not.accepted";

    protected static final String FIELD_PAYMENT_METHOD = "paymentMethod";
    protected static final String FIELD_SHOPPER_BANK_CODE = "shopperBankCode";
    protected static final String FIELD_BILLING_ADDRESS_FIRST_NAME = "billingAddress.firstName";
    protected static final String FIELD_BILLING_ADDRESS_LAST_NAME = "billingAddress.lastName";
    protected static final String FIELD_BILLING_ADDRESS_LINE1 = "billingAddress.line1";
    protected static final String FIELD_BILLING_ADDRESS_TOWN_CITY = "billingAddress.townCity";
    protected static final String FIELD_BILLING_ADDRESS_POSTCODE = "billingAddress.postcode";
    protected static final String FIELD_BILLING_ADDRESS_COUNTRY_ISO = "billingAddress.countryIso";
    protected static final String WORLDPAY_PAYMENT_METHOD_INVALID = "worldpay.paymentMethod.invalid";
    protected static final String WORLDPAY_PAYMENT_METHOD_NO_SHOPPER_BANK_CODE = "worldpay.paymentMethod.noShopperBankCode";

    @Resource
    private CartService cartService;

    @Resource
    private APMConfigurationLookupService apmConfigurationLookupService;

    @Resource
    private APMAvailabilityService apmAvailabilityService;

    @Resource (name = "worldpayCheckoutFacade")
    private AcceleratorCheckoutFacade checkoutFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final Class<?> aClass) {
        return PaymentDetailsForm.class.equals(aClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final Object object, final Errors errors) {
        final PaymentDetailsForm form = (PaymentDetailsForm) object;

        if (isDeliveryAddressIsNull(form)) {
            errors.reject(GLOBAL_MISSING_DELIVERY_ADDRESS);
        }

        if (StringUtils.isBlank(form.getPaymentMethod())) {
            errors.reject(WORLDPAY_PAYMENT_METHOD_INVALID);
        }

        if (!errors.hasErrors() && isAPMPaymentMethodSelected(form.getPaymentMethod())) {
            final WorldpayAPMConfigurationModel apmConfiguration = apmConfigurationLookupService.getAPMConfigurationForCode(form.getPaymentMethod());

            if (!apmAvailabilityService.isAvailable(apmConfiguration, cartService.getSessionCart())) {
                errors.rejectValue(FIELD_PAYMENT_METHOD, "worldpay.paymentMethod.notAvailable", "Payment method is not available");
            } else if (apmConfiguration.getBank() && StringUtils.isBlank(form.getShopperBankCode())) {
                errors.reject(WORLDPAY_PAYMENT_METHOD_NO_SHOPPER_BANK_CODE, "Bank not selected for payment method");
            }
        }

        validateTermsCheck(errors, form);
        // only base property files are resolved - if your property key is in your bundle folder this is will not be found

        if (FALSE.equals(form.getUseDeliveryAddress())) {
            validateField(errors, FIELD_BILLING_ADDRESS_FIRST_NAME, "address.firstName.invalid");
            validateField(errors, FIELD_BILLING_ADDRESS_LAST_NAME, "address.lastName.invalid");
            validateField(errors, FIELD_BILLING_ADDRESS_LINE1, "address.line1.invalid");
            validateField(errors, FIELD_BILLING_ADDRESS_TOWN_CITY, "address.townCity.invalid");
            validateField(errors, FIELD_BILLING_ADDRESS_POSTCODE, "address.postcode.invalid");
            validateField(errors, FIELD_BILLING_ADDRESS_COUNTRY_ISO, "address.country.invalid");
        }
    }

    protected void validateTermsCheck(final Errors errors, final PaymentDetailsForm form) {
        if (!form.isTermsCheck()) {
            errors.reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
        }
    }

    private boolean isDeliveryAddressIsNull(final PaymentDetailsForm form) {
        return form.getUseDeliveryAddress() && checkoutFacade.getCheckoutCart().getDeliveryAddress() == null;
    }

    protected boolean isAPMPaymentMethodSelected(String paymentMethod) {
        return !ONLINE.getMethodCode().equalsIgnoreCase(paymentMethod);
    }

    protected void validateField(Errors errors, String field, String errorCode) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, errorCode);
    }

    protected void validateField(Errors errors, String field, String errorCode, String defaultMessage) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, errorCode, defaultMessage);
    }
}
