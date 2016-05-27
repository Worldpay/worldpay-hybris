package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping (value = "/checkout/multi/worldpay/iframe")
public class WorldpayIframeCheckoutStepController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WorldpayIframeCheckoutStepController.class);

    protected static final String SHOW_NGPP_IFRAME = "showNGPPIframe";
    protected static final String REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS = REDIRECT_PREFIX + "/checkout/multi/worldpay/iframe/add-payment-details";

    @Resource
    private APMConfigurationLookupService apmConfigurationLookupService;

    @RequestMapping (value = "/add-payment-details", method = POST)
    @RequireHardLogIn
    public String addPaymentDetails(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm, final BindingResult bindingResult, final RedirectAttributes redirectAttrs)
            throws CMSItemNotFoundException {
        getPaymentDetailsFormValidator().validate(paymentDetailsForm, bindingResult);

        resetDeclineCodeOnCart();

        if (addGlobalErrors(model, bindingResult)) {
            return handleFormErrors(model, paymentDetailsForm);
        }

        handleAndSaveAddresses(paymentDetailsForm);

        final String paymentMethod = paymentDetailsForm.getPaymentMethod();
        if (isAPM(paymentMethod)) {
            redirectAttrs.addFlashAttribute(PAYMENT_METHOD_PARAM, paymentMethod);
            redirectAttrs.addFlashAttribute(SHOPPER_BANK_CODE, paymentDetailsForm.getShopperBankCode());
            return getRedirectToPaymentMethod();
        }
        try {
            final AdditionalAuthInfo additionalAuthInfo = createAdditionalAuthInfo(paymentDetailsForm.getSaveInAccount(), paymentMethod);
            final PaymentData paymentData = getWorldpayHostedOrderFacade().redirectAuthorise(additionalAuthInfo);
            redirectAttrs.addFlashAttribute(PAYMENT_DATA, paymentData);
            redirectAttrs.addFlashAttribute(SHOW_NGPP_IFRAME, true);
        } catch (WorldpayException e) {
            redirectAttrs.addFlashAttribute(SHOW_NGPP_IFRAME, false);
            GlobalMessages.addFlashMessage(redirectAttrs, GlobalMessages.ERROR_MESSAGES_HOLDER, COMMUNICATION_ERROR);
            LOGGER.error("An error occurred during Worldpay redirect authorise: " + e.getMessage(), e);
        }
        return REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS;
    }

    protected boolean isAPM(final String paymentMethod) {
        return apmConfigurationLookupService.getAllApmPaymentTypeCodes().contains(paymentMethod);
    }
}
