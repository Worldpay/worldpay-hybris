package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.controllers.WorldpayaddonControllerConstants;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static com.worldpay.service.model.AuthorisedStatus.ERROR;
import static com.worldpay.service.model.AuthorisedStatus.REFUSED;
import static java.text.MessageFormat.format;


@Controller
@RequestMapping (value = "/checkout/multi/worldpay")
public class WorldpayHopResponseController extends WorldpayChoosePaymentMethodCheckoutStepController {

    private static final Logger LOG = Logger.getLogger(WorldpayHopResponseController.class);

    protected static final String REDIRECT_URL_ADD_DELIVERY_ADDRESS = REDIRECT_PREFIX + "/checkout/multi/delivery-address/add";
    protected static final String REDIRECT_URL_CHOOSE_DELIVERY_METHOD = REDIRECT_PREFIX + "/checkout/multi/delivery-method/choose";
    protected static final String CHECKOUT_PLACE_ORDER_FAILED = "checkout.placeOrder.failed";
    protected static final String PAYMENT_STATUS_PARAMETER_NAME = "paymentStatus";
    protected static final String BILLING_ADDRESS_FORM = "wpBillingAddressForm";

    @Resource
    private Converter<Map<String, String>, RedirectAuthoriseResult> redirectAuthoriseResultConverter;
    @Resource
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    @Resource
    private Converter<AbstractOrderModel, OrderData> orderConverter;

    @Resource
    private Set<String> apmErrorResponseStatuses;

    @RequestMapping (value = "/hop-response", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandleHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        String paymentStatus = ERROR.getCode();
        if (getWorldpayHostedOrderFacade().validateRedirectResponse(requestParameterMap)) {
            final RedirectAuthoriseResult response = extractAuthoriseResultFromRequest(request);
            if (!getCheckoutFacade().hasValidCart()) {
                final PaymentTransactionModel paymentTransactionFromCode = worldpayPaymentTransactionService.getPaymentTransactionFromCode(response.getOrderCode());
                if (paymentTransactionFromCode != null && paymentTransactionFromCode.getOrder() instanceof OrderModel) {
                    return redirectToOrderConfirmationPage(orderConverter.convert(paymentTransactionFromCode.getOrder()));
                }
                return REDIRECT_URL_CART;
            }
            paymentStatus = response.getPaymentStatus();
            if (AUTHORISED.getCode().equals(paymentStatus)) {
                processRedirectResponse(response);
                return placeOrderAndRedirect(model, redirectAttributes);
            } else {
                LOG.error(format("Failed to create payment authorisation for successful hop-response (/hop-response). Received {0}", paymentStatus));
            }
        }
        return doHostedOrderPageError(paymentStatus, redirectAttributes);
    }

    @RequestMapping (value = "/hop-pending", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandlePendingHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        String paymentStatus = ERROR.getCode();
        if (getWorldpayHostedOrderFacade().validateRedirectResponse(requestParameterMap)) {
            final RedirectAuthoriseResult authoriseResult = extractAuthoriseResultFromRequest(request);
            authoriseResult.setPending(true);
            paymentStatus = authoriseResult.getPaymentStatus();
            if (!apmErrorResponseStatuses.contains(paymentStatus)) {
                processRedirectResponse(authoriseResult);
                return placeOrderAndRedirect(model, redirectAttributes);
            } else {
                LOG.error(format("Failed to create payment authorisation for successful hop-response (/hop-pending). Received {0}", paymentStatus));
            }
        }
        return doHostedOrderPageError(paymentStatus, redirectAttributes);
    }

    /**
     * Handles the non-failing responses from the bank transfer payment methods.
     *
     * @param request the {@HttpServletRequest} coming from Worldpay/Bank
     * @param model   the {@Model } to be used
     * @return
     */
    @RequestMapping (value = "bank-transfer/hop-response", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandleBankTransferHopResponse(final HttpServletRequest request, final Model model, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        final RedirectAuthoriseResult redirectAuthoriseResult = redirectAuthoriseResultConverter.convert(requestParameterMap);
        processRedirectResponse(redirectAuthoriseResult);
        return placeOrderAndRedirect(model, redirectAttributes);
    }

    /**
     * Handles the failing responses from the bank transfer payment methods.
     *
     * @param request            the {@HttpServletRequest} coming from Worldpay/Bank
     * @param redirectAttributes the {@RedirectAttributes } to be used
     * @return
     */
    @RequestMapping (value = "bank-transfer/hop-failure", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHandleBankTransferHopFailure(final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        final Map<String, String> requestParameterMap = getRequestParameterMap(request);
        LOG.info(format("Failed to complete bank transfer for selected payment method. request params {0}", requestParameterMap));

        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, REFUSED.getCode());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
    }

    /**
     * Do hosted order page error.
     *
     * @param paymentStatus      the payment status
     * @param redirectAttributes the {@RedirectAttributes } to be used
     * @return url for view
     */
    @RequestMapping (value = "/error", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doHostedOrderPageError(@RequestParam (value = "paymentStatus", required = false) final String paymentStatus, final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(PAYMENT_STATUS_PARAMETER_NAME, paymentStatus != null ? paymentStatus : ERROR.getCode());
        return REDIRECT_URL_CHOOSE_PAYMENT_METHOD + "?" + PAYMENT_STATUS_PARAMETER_NAME + "=" + paymentStatus;
    }

    /**
     * Do cancel payment.
     *
     * @return the string
     */
    @RequestMapping (value = "/hop-cancel", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doCancelPayment() {
        String redirectUrl = REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
        if (!getCheckoutFacade().hasValidCart()) {
            redirectUrl = REDIRECT_URL_CART;
        } else if (getCheckoutFacade().hasNoDeliveryAddress()) {
            redirectUrl = REDIRECT_URL_ADD_DELIVERY_ADDRESS;
        } else if (getCheckoutFacade().hasNoDeliveryMode()) {
            redirectUrl = REDIRECT_URL_CHOOSE_DELIVERY_METHOD;
        }
        return redirectUrl;
    }

    @RequestMapping (value = "/billingaddressform", method = RequestMethod.GET)
    public String getCountryAddressForm(@RequestParam ("countryIsoCode") final String countryIsoCode,
                                        @RequestParam ("useDeliveryAddress") final boolean useDeliveryAddress, final Model model) {
        model.addAttribute("supportedCountries", getCountries());
        model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(countryIsoCode));
        model.addAttribute("country", countryIsoCode);

        final PaymentDetailsForm wpPaymentDetailsForm = new PaymentDetailsForm();
        model.addAttribute(BILLING_ADDRESS_FORM, wpPaymentDetailsForm);
        populateAddressForm(countryIsoCode, useDeliveryAddress, wpPaymentDetailsForm);
        return WorldpayaddonControllerConstants.Views.Fragments.Checkout.BillingAddressForm;
    }

    protected void processRedirectResponse(final RedirectAuthoriseResult result) {
        getWorldpayHostedOrderFacade().completeRedirectAuthorise(result);
    }

    protected RedirectAuthoriseResult extractAuthoriseResultFromRequest(final HttpServletRequest request) {
        final Map<String, String> resultMap = getRequestParameterMap(request);
        return redirectAuthoriseResultConverter.convert(resultMap);
    }

    private String placeOrderAndRedirect(final Model model, final RedirectAttributes redirectAttributes) {
        final OrderData orderData;
        try {
            orderData = getCheckoutFacade().placeOrder();
        } catch (final InvalidCartException e) {
            LOG.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, CHECKOUT_PLACE_ORDER_FAILED);
            return doHostedOrderPageError(ERROR.getCode(), redirectAttributes);
        }
        return redirectToOrderConfirmationPage(orderData);
    }

    /**
     * {@inheritDoc}
     */
    protected Map<String, String> getRequestParameterMap(final HttpServletRequest request) {
        final Enumeration parameterNames = request.getParameterNames();
        final Map<String, String> map = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            final String paramName = (String) parameterNames.nextElement();
            final String paramValue = request.getParameter(paramName);
            map.put(paramName, paramValue);
        }
        return map;
    }

    protected void populateAddressForm(final String countryIsoCode, final boolean useDeliveryAddress, final PaymentDetailsForm paymentDetailsForm) {
        if (useDeliveryAddress) {
            final AddressData deliveryAddress = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();
            final AddressForm addressForm = new AddressForm();

            final RegionData region = deliveryAddress.getRegion();
            if (region != null && !StringUtils.isEmpty(region.getIsocode())) {
                addressForm.setRegionIso(region.getIsocode());
            }
            addressForm.setFirstName(deliveryAddress.getFirstName());
            addressForm.setLastName(deliveryAddress.getLastName());
            addressForm.setLine1(deliveryAddress.getLine1());
            addressForm.setLine2(deliveryAddress.getLine2());
            addressForm.setTownCity(deliveryAddress.getTown());
            addressForm.setPostcode(deliveryAddress.getPostalCode());
            addressForm.setCountryIso(countryIsoCode);
            addressForm.setPhone(deliveryAddress.getPhone());
            paymentDetailsForm.setBillingAddress(addressForm);
        }
    }
}
