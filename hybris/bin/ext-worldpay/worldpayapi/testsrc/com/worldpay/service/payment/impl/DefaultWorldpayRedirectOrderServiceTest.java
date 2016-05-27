package com.worldpay.service.payment.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.hostedorderpage.service.WorldpayURIService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.WorldpayUrlService;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.PaymentType;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.request.RedirectAuthoriseServiceRequest;
import com.worldpay.service.response.RedirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_CANCEL_URL;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_COUNTRY;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_ERROR_URL;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_FAILURE_URL;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_LANGUAGE;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_MAC;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_PAYMENT_AMOUNT;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_PAYMENT_CURRENCY;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_PENDING_URL;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.KEY_SUCCESS_URL;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.ORDER_KEY;
import static com.worldpay.service.payment.impl.DefaultWorldpayRedirectOrderService.PAYMENT_STATUS;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayRedirectOrderServiceTest {

    private static final String REDIRECT_URL = "http://www.example.com";
    private static final double TOTAL_PRICE = 100D;
    private static final double PAYMENT_AMOUNT = 100d;
    private static final String GBP = "GBP";
    private static final String LANGUAGE_ISO_CODE = "en";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String MAC_VALUE = "mac";
    private static final String AUTHORISED = "AUTHORISED";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String FULL_SUCCESS_URL = "fullSuccessUrl";
    private static final String FULL_PENDING_URL = "fullPendingUrl";
    private static final String FULL_FAILURE_URL = "fullFailureUrl";
    private static final String FULL_CANCEL_URL = "fullCancelUrl";
    private static final String FULL_ERROR_URL = "fullErrorUrl";
    private static final String WORLD_PAY_MERCHANT_CODE = "worldpayMerchantCode";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";

    @Spy
    @InjectMocks
    private DefaultWorldpayRedirectOrderService testObj = new DefaultWorldpayRedirectOrderService();

    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionServiceMock;
    @Mock
    private WorldpayConfigLookupService worldpayConfigLookupServiceMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private Converter<AddressModel, Address> worldpayAddressConverterMock;
    @Mock
    private BasicOrderInfo basicOrderInfoMock;
    @Mock
    private WorldpayConfig worldpayConfigMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private RedirectAuthoriseServiceRequest redirectAuthoriseServiceRequestMock;
    @Mock
    private RedirectAuthoriseServiceResponse redirectAuthoriseServiceResponseMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private RedirectReference redirectReferenceMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private Address billingAddressMock, potentialPickupAddressMock, cartDeliveryAddressMock;
    @Mock
    private AddressModel potentialPickupAddressModelMock, cartDeliveryAddressModelMock, clonedAddressMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayURIService worldpayURIServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private Shopper shopperMock;
    @Mock
    private TokenRequest tokenRequestMock;
    @Mock
    private Shopper authenticatedShopperMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategyMock;
    @Mock
    private GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategyMock;
    @Mock
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategyMock;
    @Mock
    private List<PaymentType> paymentTypeListMock;

    @Before
    public void setUp() throws WorldpayException {
        when(cartModelMock.getTotalPrice()).thenReturn(TOTAL_PRICE);
        when(cartModelMock.getCurrency().getIsocode()).thenReturn(GBP);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(worldpayConfigLookupServiceMock.lookupConfig()).thenReturn(worldpayConfigMock);
        when(customerEmailResolutionServiceMock.getEmailForCustomer(customerModelMock)).thenReturn(CUSTOMER_EMAIL);
        when(worldpayAddressConverterMock.convert(cartModelMock.getPaymentAddress())).thenReturn(billingAddressMock);
        when(redirectAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(commonI18NServiceMock.getCurrentLanguage().getIsocode()).thenReturn(LANGUAGE_ISO_CODE);
        when(modelServiceMock.create(PaymentInfoModel.class)).thenReturn(paymentInfoModelMock);
        when(modelServiceMock.clone(cartModelMock.getPaymentAddress())).thenReturn(clonedAddressMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(eq(WORLDPAY_ORDER_CODE), eq(WORLDPAY_ORDER_CODE), any(Amount.class))).thenReturn(basicOrderInfoMock);
        when(worldpayOrderServiceMock.getWorldpayServiceGateway()).thenReturn(worldpayServiceGatewayMock);
        when(worldpayOrderServiceMock.createShopper(CUSTOMER_EMAIL, null, null)).thenReturn(shopperMock);
        when(worldpayServiceGatewayMock.redirectAuthorise(redirectAuthoriseServiceRequestMock)).thenReturn(redirectAuthoriseServiceResponseMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(redirectReferenceMock.getUrl()).thenReturn(REDIRECT_URL);
        when(worldpayUrlServiceMock.getFullSuccessURL()).thenReturn(FULL_SUCCESS_URL);
        when(worldpayUrlServiceMock.getFullPendingURL()).thenReturn(FULL_PENDING_URL);
        when(worldpayUrlServiceMock.getFullFailureURL()).thenReturn(FULL_FAILURE_URL);
        when(worldpayUrlServiceMock.getFullCancelURL()).thenReturn(FULL_CANCEL_URL);
        when(worldpayUrlServiceMock.getFullErrorURL()).thenReturn(FULL_ERROR_URL);
        doReturn(commerceCheckoutParameterMock).when(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, null);
        when(worldpayPaymentInfoServiceMock.createPaymentInfo(cartModelMock)).thenReturn(paymentInfoModelMock);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(customerModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(worldpayGenerateMerchantTransactionCodeStrategyMock.generateCode(cartModelMock)).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(potentialPickupAddressModelMock);
        when(worldpayAddressConverterMock.convert(cartDeliveryAddressModelMock)).thenReturn(cartDeliveryAddressMock);
        when(worldpayAddressConverterMock.convert(potentialPickupAddressModelMock)).thenReturn(potentialPickupAddressMock);
        when(redirectAuthoriseServiceRequestMock.getOrder().getBillingAddress().getCountryCode()).thenReturn(COUNTRY_CODE);
        doReturn(paymentTypeListMock).when(testObj).getIncludedPaymentTypeList(additionalAuthInfoMock);
        doReturn(redirectAuthoriseServiceRequestMock).when(testObj).createRedirectAuthoriseRequest(merchantInfoMock, additionalAuthInfoMock, shopperMock, worldpayConfigMock, basicOrderInfoMock, paymentTypeListMock, null, potentialPickupAddressMock, cartDeliveryAddressMock);
        doReturn(redirectAuthoriseServiceRequestMock).when(testObj).createRedirectAuthoriseRequest(merchantInfoMock, additionalAuthInfoMock, shopperMock, worldpayConfigMock, basicOrderInfoMock, paymentTypeListMock, null, potentialPickupAddressMock, billingAddressMock);
        doReturn(redirectAuthoriseServiceRequestMock).when(testObj).createRedirectTokenAndAuthoriseRequest(merchantInfoMock, additionalAuthInfoMock, authenticatedShopperMock, worldpayConfigMock, basicOrderInfoMock, paymentTypeListMock, null, potentialPickupAddressMock, billingAddressMock, tokenRequestMock);
    }

    @Test
    public void shouldGenerateOrderCode() throws WorldpayException {

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

        verify(worldpayOrderServiceMock).createBasicOrderInfo(eq(WORLDPAY_ORDER_CODE), eq(WORLDPAY_ORDER_CODE), any(Amount.class));
        verify(worldpayDeliveryAddressStrategyMock).getDeliveryAddress(cartModelMock);
    }

    @Test
    public void testRedirectAuthoriseUseShippingAsBilling() throws WorldpayException {
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(TRUE);
        when(cartModelMock.getDeliveryAddress()).thenReturn(cartDeliveryAddressModelMock);

        final PaymentData result = testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

        assertEquals(REDIRECT_URL, result.getPostUrl());
        assertEquals(COUNTRY_CODE, result.getParameters().get(KEY_COUNTRY));
        assertEquals(LANGUAGE_ISO_CODE, result.getParameters().get(KEY_LANGUAGE));
        assertEquals(FULL_SUCCESS_URL, result.getParameters().get(KEY_SUCCESS_URL));
        assertEquals(FULL_PENDING_URL, result.getParameters().get(KEY_PENDING_URL));
        assertEquals(FULL_FAILURE_URL, result.getParameters().get(KEY_FAILURE_URL));
        assertEquals(FULL_CANCEL_URL, result.getParameters().get(KEY_CANCEL_URL));
        verify(testObj).createRedirectAuthoriseRequest(merchantInfoMock, additionalAuthInfoMock, shopperMock, worldpayConfigMock, basicOrderInfoMock, paymentTypeListMock, null, potentialPickupAddressMock, cartDeliveryAddressMock);
    }

    @Test
    public void shouldUseBillingAddressWhenShippingAddressIsNull() throws WorldpayException {
        when(cartModelMock.getDeliveryAddress()).thenReturn(null);

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

        verify(testObj).createRedirectAuthoriseRequest(merchantInfoMock, additionalAuthInfoMock, shopperMock, worldpayConfigMock, basicOrderInfoMock, paymentTypeListMock, null, potentialPickupAddressMock, billingAddressMock);
    }

    @Test
    public void shouldUseBillingAddress() throws WorldpayException {
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(FALSE);

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

        verify(testObj).createRedirectAuthoriseRequest(merchantInfoMock, additionalAuthInfoMock, shopperMock, worldpayConfigMock, basicOrderInfoMock, paymentTypeListMock, null, potentialPickupAddressMock, billingAddressMock);
    }

    @Test (expected = WorldpayException.class)
    public void testRedirectAuthoriseShouldThrowWorldpayExceptionIfRedirectUrlIsNull() throws WorldpayException {
        doReturn(redirectAuthoriseServiceRequestMock).when(testObj).createRedirectAuthoriseRequest(eq(merchantInfoMock), eq(additionalAuthInfoMock), eq(shopperMock), eq(worldpayConfigMock), eq(basicOrderInfoMock), anyListOf(PaymentType.class), anyListOf(PaymentType.class), eq(potentialPickupAddressMock), eq(potentialPickupAddressMock));
        when(redirectReferenceMock.getUrl()).thenReturn(EMPTY);

        testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
    }

    @Test
    public void testRedirectAuthoriseNotUseShippingAsBilling() throws Exception {
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(FALSE);
        when(worldpayOrderServiceMock.createAuthenticatedShopper(CUSTOMER_EMAIL, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(authenticatedShopperMock);
        when(worldpayOrderServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, null)).thenReturn(tokenRequestMock);
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(TRUE);

        final PaymentData result = testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

        assertEquals(REDIRECT_URL, result.getPostUrl());
        assertEquals(COUNTRY_CODE, result.getParameters().get(KEY_COUNTRY));
        assertEquals(LANGUAGE_ISO_CODE, result.getParameters().get(KEY_LANGUAGE));
        assertEquals(FULL_SUCCESS_URL, result.getParameters().get(KEY_SUCCESS_URL));
        assertEquals(FULL_PENDING_URL, result.getParameters().get(KEY_PENDING_URL));
        assertEquals(FULL_FAILURE_URL, result.getParameters().get(KEY_FAILURE_URL));
        assertEquals(FULL_CANCEL_URL, result.getParameters().get(KEY_CANCEL_URL));
        assertEquals(FULL_ERROR_URL, result.getParameters().get(KEY_ERROR_URL));

        verify(worldpayURIServiceMock).extractUrlParamsToMap(eq(REDIRECT_URL), anyMapOf(String.class, String.class));
        verify(sessionServiceMock).setAttribute(WORLD_PAY_MERCHANT_CODE, MERCHANT_CODE);
    }

    @Test
    public void shouldCreateRedirectRequestForTokenisationWhenSavedCardIsEnabled() throws WorldpayException {
        when(additionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(FALSE);
        when(additionalAuthInfoMock.getSaveCard()).thenReturn(TRUE);
        when(worldpayOrderServiceMock.createAuthenticatedShopper(CUSTOMER_EMAIL, AUTHENTICATED_SHOPPER_ID, null, null)).thenReturn(authenticatedShopperMock);
        when(worldpayOrderServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, null)).thenReturn(tokenRequestMock);

        final PaymentData result = testObj.redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);

        assertEquals(REDIRECT_URL, result.getPostUrl());
        assertEquals(COUNTRY_CODE, result.getParameters().get(KEY_COUNTRY));
        assertEquals(LANGUAGE_ISO_CODE, result.getParameters().get(KEY_LANGUAGE));
        assertEquals(FULL_SUCCESS_URL, result.getParameters().get(KEY_SUCCESS_URL));
        assertEquals(FULL_PENDING_URL, result.getParameters().get(KEY_PENDING_URL));
        assertEquals(FULL_FAILURE_URL, result.getParameters().get(KEY_FAILURE_URL));
        assertEquals(FULL_CANCEL_URL, result.getParameters().get(KEY_CANCEL_URL));
        assertEquals(FULL_CANCEL_URL, result.getParameters().get(KEY_CANCEL_URL));

        verify(worldpayURIServiceMock).extractUrlParamsToMap(eq(REDIRECT_URL), anyMapOf(String.class, String.class));
        verify(sessionServiceMock).setAttribute(WORLD_PAY_MERCHANT_CODE, MERCHANT_CODE);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToTrue() throws Exception {
        when(redirectAuthoriseResultMock.getSaveCard()).thenReturn(true);
        setUpRedirectAuthoriseResultMock(AUTHORISED);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, null);
        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock);
    }

    @Test
    public void testCompleteRedirectAuthoriseAndSetSavedPaymentInfoToFalse() throws Exception {
        setUpRedirectAuthoriseResultMock(AUTHORISED);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);

        testObj.completeRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, paymentInfoModelMock);
        verify(testObj).createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, null);
        verify(worldpayPaymentTransactionServiceMock).createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);
        verify(worldpayPaymentTransactionServiceMock).createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock);
    }

    @Test
    public void testCheckResponseIsValidWhenNotUsingMacValidation() throws Exception {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(false);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithOrderKeyOnly());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsValidWhenRedirectResponseStatusIsOPEN() throws Exception {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithOrderKeyOnly());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsValidWhenUsingMacValidationAndRedirectResultStatusIsAUTHORISED() throws Exception {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);

        doReturn(true).when(testObj).validateResponse(merchantInfoMock, ORDER_KEY, MAC_VALUE, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponse());

        assertTrue(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenUsingMacValidationAndRedirectResultStatusIsAUTHORISED() throws Exception {
        when(merchantInfoMock.isUsingMacValidation()).thenReturn(true);
        setUpRedirectAuthoriseResultMock(AUTHORISED);

        doReturn(false).when(testObj).validateResponse(merchantInfoMock, ORDER_KEY, MAC_VALUE, String.valueOf(PAYMENT_AMOUNT), GBP, AUTHORISED);

        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createFullWorldpayResponse());

        assertFalse(result);
    }

    @Test
    public void testCheckResponseIsNotValidWhenNoOrderKeyPresent() throws Exception {
        final boolean result = testObj.validateRedirectResponse(merchantInfoMock, createWorldpayResponseWithNoOrderKey());

        assertFalse(result);
    }

    private Map<String, String> createFullWorldpayResponse() {
        final Map<String, String> worldpayResponse = new HashMap<>();
        worldpayResponse.put(PAYMENT_STATUS, AUTHORISED);
        worldpayResponse.put(ORDER_KEY, ORDER_KEY);
        worldpayResponse.put(KEY_MAC, MAC_VALUE);
        worldpayResponse.put(KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT));
        worldpayResponse.put(KEY_PAYMENT_CURRENCY, GBP);
        return worldpayResponse;
    }

    private Map<String, String> createWorldpayResponseWithNoOrderKey() {
        final Map<String, String> worldpayResponse = new HashMap<>();
        worldpayResponse.put(PAYMENT_STATUS, AUTHORISED);
        worldpayResponse.put(KEY_MAC, MAC_VALUE);
        worldpayResponse.put(KEY_PAYMENT_AMOUNT, String.valueOf(PAYMENT_AMOUNT));
        worldpayResponse.put(KEY_PAYMENT_CURRENCY, GBP);
        return worldpayResponse;
    }

    private Map<String, String> createWorldpayResponseWithOrderKeyOnly() {
        final Map<String, String> worldpayResponse = new HashMap<>();
        worldpayResponse.put(ORDER_KEY, ORDER_KEY);
        return worldpayResponse;
    }

    private void setUpRedirectAuthoriseResultMock(final String paymentStatus) {
        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(redirectAuthoriseResultMock.getPaymentStatus()).thenReturn(paymentStatus);
        when(redirectAuthoriseResultMock.getOrderKey()).thenReturn(ORDER_KEY);
        when(redirectAuthoriseResultMock.getPending()).thenReturn(true);
    }
}