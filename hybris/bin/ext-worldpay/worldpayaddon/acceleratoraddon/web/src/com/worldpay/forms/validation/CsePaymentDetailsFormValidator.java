package com.worldpay.forms.validation;

import com.worldpay.forms.PaymentDetailsForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;

@Component ("csePaymentDetailsFormValidator")
public class CsePaymentDetailsFormValidator extends PaymentDetailsFormValidator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final Class<?> aClass) {
        return PaymentDetailsForm.class.equals(aClass);
    }

    @Override
    protected void validateTermsCheck(final Errors errors, final PaymentDetailsForm form) {
        if (!isCardPayment(form) && !form.isTermsCheck()) {
            errors.reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
        }
    }

    /**
     * The PaymentType ONLINE {@link com.worldpay.service.model.payment.PaymentType.ONLINE} means
     * for Worldpay that the user's transaction is a card.
     *
     * @param form
     * @return
     */
    private boolean isCardPayment(final PaymentDetailsForm form) {
        final String paymentMethod = form.getPaymentMethod();
        return ONLINE.getMethodCode().equalsIgnoreCase(paymentMethod);
    }
}
