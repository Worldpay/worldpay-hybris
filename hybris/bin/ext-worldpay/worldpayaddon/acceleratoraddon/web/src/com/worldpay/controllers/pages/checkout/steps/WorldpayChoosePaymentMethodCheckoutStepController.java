package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.forms.validation.PaymentDetailsFormValidator;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping (value = "/checkout/multi/worldpay/choose-payment-method")
public class WorldpayChoosePaymentMethodCheckoutStepController extends AbstractWorldpayPaymentMethodCheckoutStepController {

    protected static final String REGIONS = "regions";
    protected static final String PAYMENT_INFOS = "paymentInfos";
    protected static final String CHOOSE_PAYMENT_METHOD = "choose-payment-method";

    protected static final String CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB = "checkout.multi.paymentMethod.breadcrumb";
    protected static final String TEXT_ACCOUNT_PROFILE_PAYMENT_CART_REMOVED = "text.account.profile.paymentCart.removed";
    protected static final String CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE = "checkout.multi.worldpay.declined.message.";
    protected static final String CHECKOUT_BILLING_ADDRESS_PAGE_GLOBAL_FIELD_ERROR = "checkout.billing.address.page.global.field.error";
    protected static final String CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE_DEFAULT = "checkout.multi.worldpay.declined.message.default";

    protected static final String CHECKOUT_MULTI_TERMS_AND_CONDITIONS = "/checkout/multi/termsAndConditions";

    @Resource
    private MessageSource themeSource;
    @Resource
    private String redirectToPaymentMethod;
    @Resource
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;
    @Resource (name = "worldpayCheckoutFacade")
    private AcceleratorCheckoutFacade checkoutFacade;
    @Resource
    private WorldpayCartService worldpayCartService;
    @Resource
    private PaymentDetailsFormValidator paymentDetailsFormValidator;

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping (method = GET, value = {"", "/", "/add-cse-payment-details", "/add-payment-details"})
    @RequireHardLogIn
    @PreValidateCheckoutStep (checkoutStep = CHOOSE_PAYMENT_METHOD)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        getCheckoutFacade().setDeliveryModeIfAvailable();

        if (model.asMap().get(PAYMENT_STATUS_PARAMETER_NAME) != null) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE_DEFAULT);
        }

        setupAddPaymentPage(model);
        setupPaymentDetailsForm(model);
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        return getViewForPage(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupAddPaymentPage(Model model) throws CMSItemNotFoundException {
        super.setupAddPaymentPage(model);
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        model.addAttribute("cartData", cartData);
        model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
        model.addAttribute(PAYMENT_INFOS, getUserFacade().getCCPaymentInfos(true));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, getResourceBreadcrumbBuilder().getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB));
    }

    protected void setupPaymentDetailsForm(final Model model) {
        final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();
        final AddressForm addressForm = new AddressForm();

        final AddressData billingAddress = ((WorldpayCheckoutFacadeDecorator) getCheckoutFacade()).getBillingAddress();

        if (billingAddress != null) {
            model.addAttribute(REGIONS, getI18NFacade().getRegionsForCountryIso(billingAddress.getCountry().getIsocode()));
            populateAddressForm(billingAddress, addressForm);
        }
        paymentDetailsForm.setBillingAddress(addressForm);
        model.addAttribute(PAYMENT_DETAILS_FORM, paymentDetailsForm);
    }

    /**
     * Used for getting the decline-message for a refused notification, based on the decline-code persisted on the cart {@link de.hybris.platform.core.model.order.CartModel}
     * @return The localized error-message to display.
     */
    @RequestMapping (value = "/getDeclineMessage", method = GET)
    @ResponseBody
    public String getDeclineMessage() {
        final Integer worldpayDeclineCode = getCheckoutFacade().getCheckoutCart().getWorldpayDeclineCode();
        if (worldpayDeclineCode != null && worldpayDeclineCode != 0) {
            return getLocalisedDeclineMessage(worldpayDeclineCode);
        }
        return EMPTY;
    }

    protected void populateAddressForm(AddressData addressData, AddressForm addressForm) {
        final RegionData region = addressData.getRegion();
        if (region != null && !StringUtils.isEmpty(region.getIsocode())) {
            addressForm.setRegionIso(region.getIsocode());
        }
        addressForm.setTitleCode(addressData.getTitle());
        addressForm.setFirstName(addressData.getFirstName());
        addressForm.setLastName(addressData.getLastName());
        addressForm.setLine1(addressData.getLine1());
        addressForm.setLine2(addressData.getLine2());
        addressForm.setTownCity(addressData.getTown());
        addressForm.setPostcode(addressData.getPostalCode());
        addressForm.setCountryIso(addressData.getCountry().getIsocode());
        addressForm.setPhone(addressData.getPhone());
    }

    /**
     * Sets the saved paymentMethod selected and proceeds to next step
     * @param selectedPaymentMethodId The id of the {@link CreditCardPaymentInfoModel} to use
     * @return
     */
    @RequestMapping (value = "/choose", method = GET)
    @RequireHardLogIn
    public String doSelectPaymentMethod(@RequestParam ("selectedPaymentMethodId") final String selectedPaymentMethodId) {
        if (StringUtils.isNotBlank(selectedPaymentMethodId)) {
            getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
        }
        return getCheckoutStep().nextStep();
    }

    protected void resetDeclineCodeOnCart() {
        final String worldpayOrderCode = getCheckoutFacade().getCheckoutCart().getWorldpayOrderCode();
        if (StringUtils.isNotBlank(worldpayOrderCode)) {
            worldpayCartService.setWorldpayDeclineCodeOnCart(worldpayOrderCode, 0);
        }
    }

    protected String getLocalisedDeclineMessage(final int returnCode) {
        return themeSource.getMessage(CHECKOUT_MULTI_WORLD_PAY_DECLINED_MESSAGE + returnCode, null, getI18nService().getCurrentLocale());
    }

    protected String handleFormErrors(final Model model, final PaymentDetailsForm paymentDetailsForm) throws CMSItemNotFoundException {
        prepareErrorView(model, paymentDetailsForm);
        return getViewForPage(model);
    }

    protected void handleAndSaveAddresses(final @Valid PaymentDetailsForm paymentDetailsForm) {
        final Boolean useDeliveryAddress = paymentDetailsForm.getUseDeliveryAddress();
        final AddressData addressData = getAddressData(paymentDetailsForm.getBillingAddress(), useDeliveryAddress);

        addressData.setEmail(getCheckoutCustomerStrategy().getCurrentUserForCheckout().getContactEmail());

        if (shouldSaveAddressInProfile(useDeliveryAddress)) {
            getUserFacade().addAddress(addressData);
        }

        worldpayPaymentCheckoutFacade.setBillingDetails(addressData);
    }

    private boolean shouldSaveAddressInProfile(final Boolean useDeliveryAddress) {
        return !useDeliveryAddress || getUserFacade().isAnonymousUser();
    }

    private AddressData getAddressData(final AddressForm billingAddress, final Boolean useDeliveryAddress) {
        AddressData addressData = new AddressData();
        if (useDeliveryAddress) {
            addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
            addressData.setBillingAddress(true);
        } else {
            populateAddressData(billingAddress, addressData);
        }
        return addressData;
    }

    protected void prepareErrorView(final Model model, final PaymentDetailsForm paymentDetailsForm) throws CMSItemNotFoundException {
        final AddressForm billingAddress = paymentDetailsForm.getBillingAddress();
        if (billingAddress != null) {
            prepareRegionsAttribute(model, billingAddress.getCountryIso());
        }
        final List<CCPaymentInfoData> paymentInfos = getUserFacade().getCCPaymentInfos(true);
        setupAddPaymentPage(model);
        model.addAttribute(PAYMENT_INFOS, paymentInfos);
        model.addAttribute(PAYMENT_DETAILS_FORM, paymentDetailsForm);
    }

    protected boolean addGlobalErrors(final Model model, final BindingResult bindingResult) {
        if (bindingResult.hasGlobalErrors()) {
            GlobalMessages.addErrorMessage(model, bindingResult.getGlobalErrors().get(0).getCode());
            return true;
        }
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, CHECKOUT_BILLING_ADDRESS_PAGE_GLOBAL_FIELD_ERROR);
            return true;
        }
        return false;
    }

    protected void populateAddressData(final AddressForm addressForm, final AddressData addressData) {
        if (addressForm != null) {
            addressData.setId(addressForm.getAddressId());
            addressData.setFirstName(addressForm.getFirstName());
            addressData.setLastName(addressForm.getLastName());
            addressData.setLine1(addressForm.getLine1());
            addressData.setLine2(addressForm.getLine2());
            addressData.setTown(addressForm.getTownCity());
            addressData.setPostalCode(addressForm.getPostcode());
            addressData.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
            if (addressForm.getRegionIso() != null) {
                addressData.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
            }
            addressData.setPhone(addressForm.getPhone());
            addressData.setShippingAddress(TRUE.equals(addressForm.getShippingAddress()));
            addressData.setBillingAddress(TRUE.equals(addressForm.getBillingAddress()));
        }
    }

    protected void prepareRegionsAttribute(final Model model, final String countryIso) {
        if (countryIso != null) {
            model.addAttribute(REGIONS, getI18NFacade().getRegionsForCountryIso(countryIso));
        }
    }

    /**
     * Returns the configured Terms and Conditions URL.
     * @param request
     * @return
     */
    @ModelAttribute (value = "getTermsAndConditionsUrl")
    public String getTermsAndConditionsUrl(HttpServletRequest request) {
        return request.getContextPath() + CHECKOUT_MULTI_TERMS_AND_CONDITIONS;
    }

    /**
     * Removes a saved payment info from the customers account
     * @param paymentMethodId
     * @param redirectAttributes
     * @return
     * @throws CMSItemNotFoundException
     */
    @RequestMapping (value = "/remove", method = POST)
    @RequireHardLogIn
    public String remove(@RequestParam (value = "paymentInfoId") final String paymentMethodId,
                         final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        getUserFacade().unlinkCCPaymentInfo(paymentMethodId);
        GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                TEXT_ACCOUNT_PROFILE_PAYMENT_CART_REMOVED);
        return getCheckoutStep().currentStep();
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping (value = "/back", method = GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    /**
     * {@inheritDoc}
     */
    @RequestMapping (value = "/next", method = GET)
    @RequireHardLogIn
    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(CHOOSE_PAYMENT_METHOD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AcceleratorCheckoutFacade getCheckoutFacade() {
        return this.checkoutFacade;
    }

    public String getRedirectToPaymentMethod() {
        return redirectToPaymentMethod;
    }

    public PaymentDetailsFormValidator getPaymentDetailsFormValidator() {
        return paymentDetailsFormValidator;
    }

    public MessageSource getThemeSource() {
        return themeSource;
    }
}
