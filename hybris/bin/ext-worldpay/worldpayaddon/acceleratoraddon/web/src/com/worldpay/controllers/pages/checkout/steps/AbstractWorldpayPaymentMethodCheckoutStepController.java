package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.CSEPaymentForm;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.util.Calendar.YEAR;

@Controller
@RequestMapping (value = "/checkout/multi/payment-method")
public abstract class AbstractWorldpayPaymentMethodCheckoutStepController extends AbstractCheckoutStepController {

    protected static final String REDIRECT_URL_CHOOSE_PAYMENT_METHOD = REDIRECT_PREFIX + "/checkout/multi/worldpay/choose-payment-method";
    protected static final String COMMUNICATION_ERROR = "checkout.multi.paymentMethod.addPaymentDetails.communicationError";

    protected static final String PAYMENT_DATA = "paymentData";
    protected static final String SAVE_PAYMENT_INFO = "savePaymentInfo";
    protected static final String SHOPPER_BANK_CODE = "shopperBankCode";
    protected static final String HOP_DEBUG_MODE_PARAM = "hopDebugMode";
    protected static final String PAYMENT_METHOD_STEP_NAME = "payment-method";
    protected static final String PAYMENT_DETAILS_FORM = "paymentDetailsForm";
    protected static final String HOSTED_ORDER_PAGE_DATA = "hostedOrderPageData";
    protected static final String PAYMENT_METHOD_PARAM = "paymentMethod";
    protected static final String WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL = "worldpayPaymentAndBillingCheckoutStep";
    protected static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    protected static final int MONTHS_IN_A_YEAR_BASED_ON_0 = 11;
    protected static final String REQUEST = "request";

    @Resource
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;

    @Resource
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacade;

    @Resource
    private WorldpayHostedOrderFacade worldpayHostedOrderFacade;

    @ModelAttribute ("billingCountries")
    public Collection<CountryData> getBillingCountries() {
        return getCheckoutFacade().getBillingCountries();
    }

    @ModelAttribute ("cardTypes")
    public Collection<CardTypeData> getCardTypes() {
        return getCheckoutFacade().getSupportedCardTypes();
    }

    @ModelAttribute ("months")
    public List<SelectOption> getMonths() {
        final List<SelectOption> months = new ArrayList<>();
        months.add(new SelectOption("01", "1"));
        months.add(new SelectOption("02", "2"));
        months.add(new SelectOption("03", "3"));
        months.add(new SelectOption("04", "4"));
        months.add(new SelectOption("05", "5"));
        months.add(new SelectOption("06", "6"));
        months.add(new SelectOption("07", "7"));
        months.add(new SelectOption("08", "8"));
        months.add(new SelectOption("09", "9"));
        months.add(new SelectOption("10", "10"));
        months.add(new SelectOption("11", "11"));
        months.add(new SelectOption("12", "12"));
        return months;
    }

    @ModelAttribute ("expiryYears")
    public List<SelectOption> getExpiryYears() {
        final List<SelectOption> expiryYears = new ArrayList<>();
        final Calendar calender = new GregorianCalendar();

        for (int i = calender.get(YEAR); i < (calender.get(YEAR) + MONTHS_IN_A_YEAR_BASED_ON_0); i++) {
            expiryYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
        }

        return expiryYears;
    }

    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
        prepareDataForPage(model);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));

        final ContentPageModel contentPage = getContentPageForLabelOrId(WORLDPAY_PAYMENT_AND_BILLING_CHECKOUT_STEP_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        setCheckoutStepLinksForModel(model, getCheckoutStep());
    }

    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(PAYMENT_METHOD_STEP_NAME);
    }

    protected BankTransferAdditionalAuthInfo createBankTransferAdditionalAuthInfo(final String paymentMethod, final String shopperBankCode) {
        final BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfo = new BankTransferAdditionalAuthInfo();
        populateAdditionalAuthInfo(false, paymentMethod, bankTransferAdditionalAuthInfo);
        bankTransferAdditionalAuthInfo.setShopperBankCode(shopperBankCode);
        return bankTransferAdditionalAuthInfo;
    }

    protected AdditionalAuthInfo createAdditionalAuthInfo(final Boolean savePaymentInfo, final String paymentMethod) {
        final AdditionalAuthInfo additionalAuthInfo = new AdditionalAuthInfo();
        populateAdditionalAuthInfo(savePaymentInfo, paymentMethod, additionalAuthInfo);
        return additionalAuthInfo;
    }

    protected CSEAdditionalAuthInfo createCSEAdditionalAuthInfo(final CSEPaymentForm csePaymentForm) {
        final CSEAdditionalAuthInfo cseAdditionalAuthInfo = new CSEAdditionalAuthInfo();
        populateAdditionalAuthInfo(csePaymentForm.isSaveInAccount(), null, cseAdditionalAuthInfo);
        cseAdditionalAuthInfo.setEncryptedData(csePaymentForm.getCseToken());
        cseAdditionalAuthInfo.setCardHolderName(csePaymentForm.getNameOnCard());
        cseAdditionalAuthInfo.setExpiryYear(csePaymentForm.getExpiryYear());
        cseAdditionalAuthInfo.setExpiryMonth(csePaymentForm.getExpiryMonth());
        return cseAdditionalAuthInfo;
    }

    private void populateAdditionalAuthInfo(final Boolean savePaymentInfo, final String paymentMethod, final AdditionalAuthInfo additionalAuthInfo) {
        additionalAuthInfo.setPaymentMethod(paymentMethod);
        additionalAuthInfo.setUsingShippingAsBilling(!getWorldpayPaymentCheckoutFacade().hasBillingDetails());
        additionalAuthInfo.setSaveCard(savePaymentInfo);
    }

    /**
     * The PaymentType ONLINE {@link com.worldpay.service.model.payment.PaymentType.ONLINE} means
     * for Worldpay that the user's transaction is a card.
     *
     * @param paymentMethod
     * @return
     */
    protected boolean paymentMethodIsOnline(final String paymentMethod) {
        return ONLINE.getMethodCode().equalsIgnoreCase(paymentMethod);
    }

    protected Boolean getSavePaymentInfo(final Model model) {
        return model.asMap().get(SAVE_PAYMENT_INFO) == null ? Boolean.FALSE : (Boolean) model.asMap().get(SAVE_PAYMENT_INFO);
    }

    public WorldpayPaymentCheckoutFacade getWorldpayPaymentCheckoutFacade() {
        return worldpayPaymentCheckoutFacade;
    }

    public WorldpayHostedOrderFacade getWorldpayHostedOrderFacade() {
        return worldpayHostedOrderFacade;
    }

    public WorldpayAdditionalInfoFacade getWorldpayAdditionalInfoFacade() {
        return worldpayAdditionalInfoFacade;
    }
}
