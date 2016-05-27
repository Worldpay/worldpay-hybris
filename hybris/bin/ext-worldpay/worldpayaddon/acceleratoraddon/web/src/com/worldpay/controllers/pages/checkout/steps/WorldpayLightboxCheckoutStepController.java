package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.controllers.WorldpayaddonControllerConstants;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping (value = "/checkout/multi/worldpay/lightbox")
public class WorldpayLightboxCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WorldpayIframeCheckoutStepController.class);

    protected static final String CHECKOUT_MULTI_LIGHTBOX_REDIRECT_ERROR = "checkout.multi.lightbox.redirect.error";
    protected static final String ERROR_MESSAGE_KEY = "errorMessage";
    protected static final String PAYMENT_DETAILS_FORM_ERRORS = "paymentDetailsFormErrors";

    @RequireHardLogIn
    @RequestMapping (value = "/add-payment-details", method = POST)
    public String addPaymentDetails(Model model, @ModelAttribute PaymentDetailsForm paymentDetailsForm, BindingResult bindingResult) throws CMSItemNotFoundException {
        getSessionService().setAttribute(PAYMENT_DETAILS_FORM, paymentDetailsForm);
        getSessionService().setAttribute(PAYMENT_DETAILS_FORM_ERRORS, bindingResult);
        getPaymentDetailsFormValidator().validate(paymentDetailsForm, bindingResult);

        resetDeclineCodeOnCart();

        if (addGlobalErrors(model, bindingResult)) {
            prepareErrorView(model, paymentDetailsForm);
        } else {
            handleAndSaveAddresses(paymentDetailsForm);
            model.addAttribute(SAVE_PAYMENT_INFO, paymentDetailsForm.getSaveInAccount());
            setupAddPaymentPage(model);
        }
        return WorldpayaddonControllerConstants.Views.Fragments.Checkout.BillingAddressInPaymentForm;
    }

    @RequestMapping (value = "/check-global-errors", method = GET)
    @RequireHardLogIn
    public String getGlobalErrors(final Model model) throws CMSItemNotFoundException {
        final BindingResult bindingResult = getSessionService().getAttribute(PAYMENT_DETAILS_FORM_ERRORS);
        addGlobalErrors(model, bindingResult);
        return WorldpayaddonControllerConstants.Views.Fragments.Common.GlobalErrorsFragment;
    }

    @RequestMapping (value = "/payment-data", method = GET)
    @RequireHardLogIn
    @ResponseBody
    public PaymentData getNextGenHopSlot() throws CMSItemNotFoundException {
        try {
            final PaymentDetailsForm paymentDetailsForm = getSessionService().getAttribute(PAYMENT_DETAILS_FORM);
            final AdditionalAuthInfo additionalAuthInfo = createAdditionalAuthInfo(paymentDetailsForm.getSaveInAccount(), paymentDetailsForm.getPaymentMethod());
            getSessionService().removeAttribute(PAYMENT_DETAILS_FORM);
            getSessionService().removeAttribute(PAYMENT_DETAILS_FORM_ERRORS);
            return getWorldpayHostedOrderFacade().redirectAuthorise(additionalAuthInfo);
        } catch (WorldpayException e) {
            LOGGER.error("An error occurred during Worldpay redirect authorise: " + e.getMessage(), e);
            PaymentData errorData = new PaymentData();
            errorData.setParameters(Collections.singletonMap(ERROR_MESSAGE_KEY, getThemeSource().getMessage(CHECKOUT_MULTI_LIGHTBOX_REDIRECT_ERROR, null, getI18nService().getCurrentLocale())));
            return errorData;
        }
    }
}
