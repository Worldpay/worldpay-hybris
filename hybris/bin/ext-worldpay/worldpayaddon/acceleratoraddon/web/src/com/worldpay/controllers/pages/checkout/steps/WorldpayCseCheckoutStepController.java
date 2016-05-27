package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.controllers.WorldpayaddonControllerConstants;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.forms.CSEPaymentForm;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.order.InvalidCartException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping (value = "/checkout/multi/worldpay/cse")
public class WorldpayCseCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WorldpayCseCheckoutStepController.class);

    protected static final String CSE_PUBLIC_KEY = "csePublicKey";
    protected static final String CSE_PAYMENT_FORM = "csePaymentForm";
    protected static final String REDIRECT_TO_CSE_PAGE = "redirect:/checkout/multi/worldpay/cse/cse-data";

    @Resource
    private Validator csePaymentDetailsFormValidator;
    @Resource
    private Validator cseFormValidator;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;
    @Resource
    private UiExperienceService uiExperienceService;
    @Resource
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacade;

    /**
     * Returns the CSE payment details page
     *
     * @param model
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping (value = {"/cse-data", "/place-order"}, method = GET)
    @RequireHardLogIn
    public String getCseDataPage(final Model model) throws CMSItemNotFoundException {
        if (getCheckoutFacade().hasCheckoutCart()) {
            setupAddPaymentPage(model);
            return WorldpayaddonControllerConstants.Views.Pages.MultiStepCheckout.CSEPaymentDetailsPage;
        } else {
            return REDIRECT_URL_CART;
        }
    }

    /**
     * Validates and saves form details on submit of the payment details form for Client Side Encryption flow and redirects to payment form
     *
     * @param model
     * @param paymentDetailsForm
     * @param bindingResult
     * @param redirectAttrs
     * @return The view to redirect to.
     * @throws CMSItemNotFoundException
     */
    @RequestMapping (value = "/add-payment-address", method = POST)
    @RequireHardLogIn
    public String addPaymentAddress(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm, final BindingResult bindingResult, final RedirectAttributes redirectAttrs)
            throws CMSItemNotFoundException {
        csePaymentDetailsFormValidator.validate(paymentDetailsForm, bindingResult);

        if (addGlobalErrors(model, bindingResult)) {
            model.addAttribute(PAYMENT_DETAILS_FORM, paymentDetailsForm);
            return handleFormErrors(model, paymentDetailsForm);
        }

        final String paymentMethod = paymentDetailsForm.getPaymentMethod();

        handleAndSaveAddresses(paymentDetailsForm);
        if (paymentMethodIsOnline(paymentMethod)) {
            return REDIRECT_TO_CSE_PAGE;
        }
        redirectAttrs.addFlashAttribute(SHOPPER_BANK_CODE, paymentDetailsForm.getShopperBankCode());
        redirectAttrs.addFlashAttribute(PAYMENT_METHOD_PARAM, paymentDetailsForm.getPaymentMethod());
        return getRedirectToPaymentMethod();
    }

    /**
     * Validates and saves form details on submit of the CSE payment form and authorises valid payment details.
     *
     * @param request
     * @param model
     * @param csePaymentForm
     * @param bindingResult
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping (value = "/place-order", method = POST)
    @RequireHardLogIn
    public String addCseData(final HttpServletRequest request, final Model model, @Valid final CSEPaymentForm csePaymentForm, final BindingResult bindingResult)
            throws CMSItemNotFoundException {
        cseFormValidator.validate(csePaymentForm, bindingResult);

        if (addGlobalErrors(model, bindingResult)) {
            return getErrorView(model);
        }

        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = createCSEAdditionalAuthInfo(csePaymentForm);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = createWorldpayAdditionalInfo(request, csePaymentForm.getCvc());
        try {
            final DirectResponseData directResponseData = worldpayDirectOrderFacade.authorise(cseAdditionalAuthInfo, worldpayAdditionalInfoData);
            return handleDirectResponse(model, directResponseData);
        } catch (InvalidCartException | WorldpayException e) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_ERROR_PAYMENTETHOD_FORMENTRY_INVALID);
            LOGGER.error("There was an error authorising the transaction", e);
            return getErrorView(model);
        }
    }

    protected WorldpayAdditionalInfoData createWorldpayAdditionalInfo(final HttpServletRequest request, final String cvc) {
        final WorldpayAdditionalInfoData worldpayAdditionalInfo = getWorldpayAdditionalInfoFacade().createWorldpayAdditionalInfoData(request);
        worldpayAdditionalInfo.setSecurityCode(cvc);
        return worldpayAdditionalInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        super.setupAddPaymentPage(model);
        model.addAttribute(CSE_PAYMENT_FORM, new CSEPaymentForm());
        model.addAttribute(CSE_PUBLIC_KEY, worldpayMerchantConfigDataFacade.getCurrentSiteMerchantConfigData(uiExperienceService.getUiExperienceLevel()).getCsePublicKey());
    }

    @Override
    protected String getErrorView(final Model model) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);
        return WorldpayaddonControllerConstants.Views.Pages.MultiStepCheckout.CSEPaymentDetailsPage;
    }
}
