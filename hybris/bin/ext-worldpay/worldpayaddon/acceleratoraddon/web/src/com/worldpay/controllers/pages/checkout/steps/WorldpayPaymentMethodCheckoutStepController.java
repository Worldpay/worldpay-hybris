package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.controllers.WorldpayaddonControllerConstants;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping (value = "/checkout/multi/worldpay/payment-method")
public class WorldpayPaymentMethodCheckoutStepController extends AbstractWorldpayPaymentMethodCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WorldpayPaymentMethodCheckoutStepController.class);

    protected static final String HOP_DEBUG_MODE_CONFIG = "hop.debug.mode";
    protected static final String CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB = "checkout.multi.paymentMethod.breadcrumb";
    protected static final String PREFERRED_PAYMENT_METHOD_PARAM = "preferredPaymentMethod";

    @Resource
    private WorldpayBankConfigurationFacade worldpayBankConfigurationFacade;
    @Resource
    private WorldpayDirectOrderFacade worldpayDirectOrderFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping (value = "/add", method = GET)
    @RequireHardLogIn
    @PreValidateCheckoutStep (checkoutStep = PAYMENT_METHOD_STEP_NAME)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        setupAddPaymentPage(model);

        // Use the checkout PCI strategy for getting the URL for creating new subscriptions.
        setCheckoutStepLinksForModel(model, getCheckoutStep());
        // Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
        try {
            final String paymentMethod = (String) model.asMap().get(PAYMENT_METHOD_PARAM);
            if (isBankTransferAPM(paymentMethod)) {
                PaymentData paymentData = getPaymentDataForBankTransferAPM(model, paymentMethod);
                return REDIRECT_PREFIX + paymentData.getPostUrl();
            }

            final PaymentData paymentData = redirectAuthorise(model, paymentMethod);
            populateModel(model, paymentData);
            // Forward to the hosted order page which outputs the paymentData params into a form and (if not debugging) autosubmits to the
            // supplied postUrl
            return WorldpayaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
        } catch (final WorldpayException e) {
            LOGGER.error("Failed to communicate successfully with worldpay", e);
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, COMMUNICATION_ERROR);
            return REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
        }
    }

    @Override
    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
        prepareDataForPage(model);
        model.addAttribute(BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB));
        final ContentPageModel contentPage = getContentPageForLabelOrId(WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        setCheckoutStepLinksForModel(model, getCheckoutStep());
    }

    protected PaymentData redirectAuthorise(final Model model, final String paymentMethod) throws WorldpayException {
        final Boolean savePaymentInfo = getSavePaymentInfo(model);
        final AdditionalAuthInfo additionalAuthInfo = createAdditionalAuthInfo(savePaymentInfo, paymentMethod);
        final PaymentData hostedOrderPageData = getWorldpayHostedOrderFacade().redirectAuthorise(additionalAuthInfo);
        if (!paymentMethodIsOnline(paymentMethod)) {
            hostedOrderPageData.getParameters().put(PREFERRED_PAYMENT_METHOD_PARAM, paymentMethod);
        }
        return hostedOrderPageData;
    }

    private PaymentData getPaymentDataForBankTransferAPM(final Model model, final String paymentMethod) throws WorldpayException {
        final String shopperBankCode = (String) model.asMap().get(SHOPPER_BANK_CODE);
        final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo = createBankTransferAdditionalAuthInfo(paymentMethod, shopperBankCode);
        final WorldpayAdditionalInfoData worldpayAdditionalInfoData = getWorldpayAdditionalInfoFacade().createWorldpayAdditionalInfoData((HttpServletRequest) model.asMap().get(REQUEST));
        final String redirectUrl = worldpayDirectOrderFacade.authoriseBankTransferRedirect(bankTransferAdditionalAuthInfo, worldpayAdditionalInfoData);
        PaymentData paymentData = new PaymentData();
        paymentData.setPostUrl(redirectUrl);
        return paymentData;
    }

    private boolean isBankTransferAPM(final String paymentMethod) {
        if (paymentMethodIsOnline(paymentMethod)) {
            return false;
        }
        return worldpayBankConfigurationFacade.isBankTransferApm(paymentMethod);
    }

    protected void populateModel(final Model model, final PaymentData hostedOrderPageData) {
        model.addAttribute(HOSTED_ORDER_PAGE_DATA, hostedOrderPageData);

        final boolean hopDebugMode = getSiteConfigService().getBoolean(HOP_DEBUG_MODE_CONFIG, false);
        model.addAttribute(HOP_DEBUG_MODE_PARAM, Boolean.valueOf(hopDebugMode));
    }

    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(PAYMENT_METHOD_STEP_NAME);
    }
}
