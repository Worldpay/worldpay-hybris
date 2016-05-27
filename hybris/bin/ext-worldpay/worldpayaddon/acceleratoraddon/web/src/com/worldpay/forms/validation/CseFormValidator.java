package com.worldpay.forms.validation;

import com.worldpay.forms.CSEPaymentForm;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component ("cseFormValidator")
public class CseFormValidator implements Validator {
    protected static final String GLOBAL_MISSING_CSE_TOKEN = "checkout.multi.paymentMethod.cse.invalid";
    protected static final String CHECKOUT_ERROR_TERMS_NOT_ACCEPTED = "checkout.error.terms.not.accepted";

    @Override
    public boolean supports(final Class<?> aClass) {
        return CSEPaymentForm.class.equals(aClass);
    }

    @Override
    public void validate(final Object object, final Errors errors) {
        final CSEPaymentForm form = (CSEPaymentForm) object;

        if (StringUtils.isBlank(form.getCseToken())) {
            errors.reject(GLOBAL_MISSING_CSE_TOKEN);
        }

        if (!form.isTermsCheck()) {
            errors.reject(CHECKOUT_ERROR_TERMS_NOT_ACCEPTED);
        }
    }
}
