package com.worldpay.service.payment.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.exception.WorldpayMacValidationException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.hostedorderpage.service.WorldpayURIService;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.mac.MacValidator;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.util.Collections.singletonList;


/**
 * Default implementation of the {@link WorldpayRedirectOrderService}
 */
public class DefaultWorldpayRedirectOrderService extends AbstractWorldpayOrderService implements WorldpayRedirectOrderService {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderService.class);

    protected static final String KEY_MAC = "mac";
    protected static final String ORDER_KEY = "orderKey";
    protected static final String KEY_COUNTRY = "country";
    protected static final String KEY_LANGUAGE = "language";
    protected static final String KEY_ERROR_URL = "errorURL";
    protected static final String KEY_CANCEL_URL = "cancelURL";
    protected static final String KEY_SUCCESS_URL = "successURL";
    protected static final String KEY_PENDING_URL = "pendingURL";
    protected static final String KEY_FAILURE_URL = "failureURL";
    protected static final String PAYMENT_STATUS = "paymentStatus";
    protected static final String KEY_PAYMENT_AMOUNT = "paymentAmount";
    protected static final String KEY_PAYMENT_CURRENCY = "paymentCurrency";
    protected static final String WORLDPAY_MERCHANT_CODE = "worldpayMerchantCode";

    private SessionService sessionService;
    private WorldpayURIService worldpayURIService;
    private WorldpayUrlService worldpayUrlService;
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;
    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy;
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayRedirectOrderService#redirectAuthorise
     */
    @Override
    public PaymentData redirectAuthorise(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo) throws WorldpayException {
        final RedirectAuthoriseServiceRequest request = buildRedirectAuthoriseRequest(merchantInfo, cartModel, additionalAuthInfo);
        final RedirectAuthoriseServiceResponse redirectAuthorise = getWorldpayOrderService().getWorldpayServiceGateway().redirectAuthorise(request);

        if (redirectAuthorise == null) {
            throw new WorldpayException("Response from Worldpay is empty");
        }
        final RedirectReference redirectReference = redirectAuthorise.getRedirectReference();

        if (redirectReference == null) {
            final String errorMessage = redirectAuthorise.getErrorDetail().getMessage();
            LOG.error(MessageFormat.format("Error detail: {0}", errorMessage));
            throw new WorldpayException(MessageFormat.format("Reference returned from Worldpay is empty - message was: {0}", errorMessage));
        }

        sessionService.setAttribute(WORLDPAY_MERCHANT_CODE, merchantInfo.getMerchantCode());
        return buildHOPPageData(redirectReference, request.getOrder().getBillingAddress().getCountryCode());
    }

    /**
     * @see WorldpayRedirectOrderService#completeRedirectAuthorise
     * {@inheritDoc}
     */
    @Override
    public void completeRedirectAuthorise(final RedirectAuthoriseResult result, final String merchantCode, final CartModel cartModel) {
        validateParameterNotNull(result, "RedirectAuthoriseResult cannot be null");

        final PaymentInfoModel paymentInfoModel = getWorldpayPaymentInfoService().createPaymentInfo(cartModel);
        cloneAndSetBillingAddressFromCart(cartModel, paymentInfoModel);
        final CommerceCheckoutParameter commerceCheckoutParameter = createCommerceCheckoutParameter(cartModel, paymentInfoModel, null);
        getCommerceCheckoutService().setPaymentInfo(commerceCheckoutParameter);
        final PaymentTransactionModel paymentTransaction = getWorldpayPaymentTransactionService().createPaymentTransaction(result.getPending(), merchantCode, commerceCheckoutParameter);
        getWorldpayPaymentTransactionService().createPendingAuthorisePaymentTransactionEntry(paymentTransaction, merchantCode, cartModel);
    }

    /**
     * @see WorldpayRedirectOrderService#validateRedirectResponse
     * {@inheritDoc}
     */
    @Override
    public boolean validateRedirectResponse(final MerchantInfo merchantInfo, final Map<String, String> resultMap) {
        final String orderKey = resultMap.get(ORDER_KEY);
        if (StringUtils.isBlank(orderKey)) {
            return false;
        }

        final String paymentStatus = resultMap.get(PAYMENT_STATUS);
        if (shouldValidateMac(merchantInfo, paymentStatus)) {
            final String mac = resultMap.get(KEY_MAC);
            final String paymentAmount = resultMap.get(KEY_PAYMENT_AMOUNT);
            final String paymentCurrency = resultMap.get(KEY_PAYMENT_CURRENCY);
            return validateResponse(merchantInfo, orderKey, mac, paymentAmount, paymentCurrency, paymentStatus);
        }
        return true;
    }

    protected boolean shouldValidateMac(final MerchantInfo merchantInfo, final String paymentStatus) {
        return merchantInfo.isUsingMacValidation() && AUTHORISED.getCode().equalsIgnoreCase(paymentStatus);
    }

    protected boolean validateResponse(final MerchantInfo merchantInfo, final String orderKey, final String mac, final String paymentAmount, final String paymentCurrency, final String paymentStatus) {
        try {
            return getMacValidator().validateResponse(orderKey, mac, paymentAmount, paymentCurrency, paymentStatus, merchantInfo.getMacSecret());
        } catch (WorldpayMacValidationException e) {
            LOG.error("Mac validation failed - see log for more details", e);
            return false;
        }
    }

    protected MacValidator getMacValidator() {
        return MacValidator.getInstance();
    }

    /**
     * Build the Redirect Authorise Request from the provided information
     *
     * @param merchantInfo       The {@link MerchantInfo} to use in the communication with Worldpay
     * @param cartModel          The {@link CartModel} to get the information about the current order
     * @param additionalAuthInfo The {@link AdditionalAuthInfo} to use in the communication with Worldpay
     * @return RedirectAuthoriseServiceRequest object
     */
    private RedirectAuthoriseServiceRequest buildRedirectAuthoriseRequest(final MerchantInfo merchantInfo, final CartModel cartModel, final AdditionalAuthInfo additionalAuthInfo)
            throws WorldpayConfigurationException {
        final String orderCode = getWorldpayGenerateMerchantTransactionCodeStrategy().generateCode(cartModel);
        final WorldpayConfig config = getWorldpayConfigLookupService().lookupConfig();

        final CurrencyModel currencyModel = cartModel.getCurrency();
        final Amount amount = getWorldpayOrderService().createAmount(currencyModel, cartModel.getTotalPrice());
        final BasicOrderInfo orderInfo = getWorldpayOrderService().createBasicOrderInfo(orderCode, orderCode, amount);

        final List<PaymentType> includedPTs = getIncludedPaymentTypeList(additionalAuthInfo);

        final AddressModel shippingAddressModel = worldpayDeliveryAddressStrategy.getDeliveryAddress(cartModel);
        final Address shippingAddress = getWorldpayAddressConverter().convert(shippingAddressModel);
        final Address billingAddress = getBillingAddress(cartModel, additionalAuthInfo.getUsingShippingAsBilling());
        final CustomerModel customerModel = (CustomerModel) cartModel.getUser();
        final String customerEmail = getCustomerEmailResolutionService().getEmailForCustomer(customerModel);
        if (additionalAuthInfo.getSaveCard()) {
            final String authenticatedShopperId = worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(customerModel);
            final Shopper shopper = getWorldpayOrderService().createAuthenticatedShopper(customerEmail, authenticatedShopperId, null, null);
            final String tokenEventReference = worldpayTokenEventReferenceCreationStrategy.createTokenEventReference();
            final TokenRequest tokenRequest = getWorldpayOrderService().createTokenRequest(tokenEventReference, null);
            return createRedirectTokenAndAuthoriseRequest(merchantInfo, additionalAuthInfo, shopper, config, orderInfo, includedPTs, null, shippingAddress, billingAddress, tokenRequest);
        } else {
            final Shopper shopper = getWorldpayOrderService().createShopper(customerEmail, null, null);
            return createRedirectAuthoriseRequest(merchantInfo, additionalAuthInfo, shopper, config, orderInfo, includedPTs, null, shippingAddress, billingAddress);
        }
    }

    protected List<PaymentType> getIncludedPaymentTypeList(final AdditionalAuthInfo additionalAuthInfo) {
        return singletonList(PaymentType.getPaymentType(additionalAuthInfo.getPaymentMethod()));
    }

    private Address getBillingAddress(final CartModel cartModel, final Boolean useShippingAsBilling) {
        final AddressModel shippingAddress = cartModel.getDeliveryAddress();
        if (shippingAddress != null && useShippingAsBilling) {
            return getWorldpayAddressConverter().convert(shippingAddress);
        } else {
            return getWorldpayAddressConverter().convert(cartModel.getPaymentAddress());
        }
    }

    protected RedirectAuthoriseServiceRequest createRedirectAuthoriseRequest(final MerchantInfo merchantInfo, final AdditionalAuthInfo additionalAuthInfo, final Shopper shopper, final WorldpayConfig config, final BasicOrderInfo orderInfo, final List<PaymentType> includedPaymentTypes, final List<PaymentType> excludedPaymentTypes, final Address shippingAddress, final Address billingAddress) {
        return RedirectAuthoriseServiceRequest.createRedirectAuthoriseRequest(config, merchantInfo, orderInfo, additionalAuthInfo.getInstallationId(), additionalAuthInfo.getOrderContent(), includedPaymentTypes, excludedPaymentTypes, shopper, shippingAddress, billingAddress, additionalAuthInfo.getStatementNarrative());
    }

    protected RedirectAuthoriseServiceRequest createRedirectTokenAndAuthoriseRequest(final MerchantInfo merchantInfo, final AdditionalAuthInfo additionalAuthInfo, final Shopper shopper, final WorldpayConfig worldpayConfig, final BasicOrderInfo basicOrderInfo, final List<PaymentType> includedPaymentTypes, final List<PaymentType> excludedPaymentTypes, final Address shippingAddress, final Address billingAddress, final TokenRequest tokenRequest) {
        return RedirectAuthoriseServiceRequest.createTokenAndRedirectAuthoriseRequest(worldpayConfig, merchantInfo, basicOrderInfo, additionalAuthInfo.getInstallationId(), additionalAuthInfo.getOrderContent(), includedPaymentTypes, excludedPaymentTypes, shopper, shippingAddress, billingAddress, additionalAuthInfo.getStatementNarrative(), tokenRequest);
    }

    private PaymentData buildPaymentData(final String redirectReferenceUrl, final Map<String, String> params) throws WorldpayException {
        final PaymentData data = new PaymentData();
        data.setPostUrl(redirectReferenceUrl);
        data.setParameters(params);
        return data;
    }

    /**
     * Build HOP page data to be sent via POST to worldpay
     *
     * @param redirectReference The {@link RedirectReference} that contains the URL to redirect the user to
     * @param countryCode       The {@link CartModel} that contains the information about the order and the addresses
     * @return {@link PaymentData} object
     */
    private PaymentData buildHOPPageData(final RedirectReference redirectReference, final String countryCode) throws WorldpayException {
        final String redirectReferenceUrl = redirectReference.getUrl();
        if (StringUtils.isEmpty(redirectReferenceUrl)) {
            throw new WorldpayException("RedirectReferenceUrl is required but is empty");
        }

        final Map<String, String> params = generateParams(countryCode, redirectReferenceUrl);

        return buildPaymentData(redirectReferenceUrl, params);
    }

    private Map<String, String> generateParams(final String countryCode, final String redirectReferenceUrl) throws WorldpayException {
        final Map<String, String> params = new HashMap<>();
        worldpayURIService.extractUrlParamsToMap(redirectReferenceUrl, params);
        params.put(KEY_COUNTRY, countryCode);
        params.put(KEY_LANGUAGE, getCommonI18NService().getCurrentLanguage().getIsocode());
        params.put(KEY_SUCCESS_URL, worldpayUrlService.getFullSuccessURL());
        params.put(KEY_PENDING_URL, worldpayUrlService.getFullPendingURL());
        params.put(KEY_FAILURE_URL, worldpayUrlService.getFullFailureURL());
        params.put(KEY_CANCEL_URL, worldpayUrlService.getFullCancelURL());
        params.put(KEY_ERROR_URL, worldpayUrlService.getFullErrorURL());
        return params;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Required
    public void setWorldpayURIService(WorldpayURIService worldpayURIService) {
        this.worldpayURIService = worldpayURIService;
    }

    @Required
    public void setWorldpayUrlService(WorldpayUrlService worldpayUrlService) {
        this.worldpayUrlService = worldpayUrlService;
    }

    @Required
    public void setWorldpayTokenEventReferenceCreationStrategy(WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategy) {
        this.worldpayTokenEventReferenceCreationStrategy = worldpayTokenEventReferenceCreationStrategy;
    }

    @Required
    public void setWorldpayAuthenticatedShopperIdStrategy(WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy) {
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
    }

    @Required
    public void setWorldpayDeliveryAddressStrategy(final WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategy) {
        this.worldpayDeliveryAddressStrategy = worldpayDeliveryAddressStrategy;
    }
}
