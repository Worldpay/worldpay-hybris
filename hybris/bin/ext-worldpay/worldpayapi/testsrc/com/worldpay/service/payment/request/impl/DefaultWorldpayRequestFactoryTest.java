package com.worldpay.service.payment.request.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.Browser;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.Session;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.Cse;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.model.token.CardDetails;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.model.token.TokenRequest;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.WorldpayTokenEventReferenceCreationStrategy;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.strategy.WorldpayDeliveryAddressStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.service.payment.request.impl.DefaultWorldpayRequestFactory.TOKEN_UPDATED;
import static com.worldpay.service.payment.request.impl.DefaultWorldpayRequestFactory.TOKEN_UPDATE_DATE_FORMAT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayRequestFactoryTest {

    private static final String SHOPPER_EMAIL_ADDRESS = "shopperEmailAddress";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final double INTEGER_TOTAL = 119.12;
    private static final String PA_RESPONSE = "paResponse";
    private static final String TOKEN_EVENT_REFERENCE = "tokenEventReference";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String CVC_CODE = "cvcCode";
    private static final String ECHO_DATA = "echoData";
    private static final String COOKIE = "cookie";
    private static final String STATEMENT_NARRATIVE = "statementNarrative";
    private static final String SHOPPER_BANK_CODE = "shopperBankCode";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String EXPIRY_MONTH = "expiryMonth";
    private static final String EXPIRY_YEAR = "expiryYear";
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";

    @Spy
    @InjectMocks
    private DefaultWorldpayRequestFactory testObj = new DefaultWorldpayRequestFactory();

    @Mock
    private WorldpayDeliveryAddressStrategy worldpayDeliveryAddressStrategyMock;
    @Mock
    private WorldpayConfigLookupService worldpayConfigLookupServiceMock;
    @Mock
    private Converter<AddressModel, Address> worldpayAddressConverterMock;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionServiceMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private WorldpayConfig worldpayConfigMock;
    @Mock
    private BasicOrderInfo basicOrderInfoMock;
    @Mock
    private Cse csePaymentMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock
    private Address shippingAddressMock, billingAddressMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceRequest directTokenisedAuthoriseServiceRequestMock;
    @Mock
    private Amount amountMock;
    @Mock
    private AddressModel deliveryAddressModelMock, paymentAddressModelMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private TokenRequest tokenRequestMockWithReasonNull, tokenRequestMockWithReason;
    @Mock
    private Shopper authenticatedShopperMock;
    @Mock
    private WorldpayTokenEventReferenceCreationStrategy worldpayTokenEventReferenceCreationStrategyMock;
    @Mock
    private GenerateMerchantTransactionCodeStrategy worldpayGenerateMerchantTransactionCodeStrategyMock;
    @Mock
    private Token tokenMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock
    private BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfoMock;
    @Mock
    private Payment paymentMock;
    @Mock
    private Shopper shopperMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CreateTokenResponse createTokenResponseMock;
    @Captor
    private ArgumentCaptor<CardDetails> cardDetailsCaptor;

    @Before
    public void setup() throws WorldpayException {
        doReturn(csePaymentMock).when(testObj).createCsePayment(cseAdditionalAuthInfoMock, billingAddressMock);
        when(worldpayConfigLookupServiceMock.lookupConfig()).thenReturn(worldpayConfigMock);
        when(worldpayAddressConverterMock.convert(deliveryAddressModelMock)).thenReturn(shippingAddressMock);
        when(worldpayAddressConverterMock.convert(paymentAddressModelMock)).thenReturn(billingAddressMock);
        when(cartModelMock.getDeliveryAddress()).thenReturn(deliveryAddressModelMock);
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(worldpayTokenEventReferenceCreationStrategyMock.createTokenEventReference()).thenReturn(TOKEN_EVENT_REFERENCE);
        when(worldpayOrderServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, null)).thenReturn(tokenRequestMockWithReasonNull);
        when(worldpayGenerateMerchantTransactionCodeStrategyMock.generateCode(cartModelMock)).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayOrderServiceMock.createAmount(currencyModelMock, INTEGER_TOTAL)).thenReturn(amountMock);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
        when(cartModelMock.getTotalPrice()).thenReturn(INTEGER_TOTAL);
        when(worldpayOrderServiceMock.createBasicOrderInfo(WORLDPAY_ORDER_CODE, WORLDPAY_ORDER_CODE, amountMock)).thenReturn(basicOrderInfoMock);
        doReturn(tokenMock).when(testObj).createToken(SUBSCRIPTION_ID, CVC_CODE);
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);
        when(creditCardPaymentInfoModelMock.getSubscriptionId()).thenReturn(SUBSCRIPTION_ID);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC_CODE);
        when(customerEmailResolutionServiceMock.getEmailForCustomer(customerModelMock)).thenReturn(SHOPPER_EMAIL_ADDRESS);
        when(worldpayOrderServiceMock.createBrowser(worldpayAdditionalInfoDataMock)).thenReturn(browserMock);
        when(worldpayOrderServiceMock.createSession(worldpayAdditionalInfoDataMock)).thenReturn(sessionMock);
        when(worldpayOrderServiceMock.createAuthenticatedShopper(SHOPPER_EMAIL_ADDRESS, AUTHENTICATED_SHOPPER_ID, sessionMock, browserMock)).thenReturn(authenticatedShopperMock);
        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(worldpayOrderServiceMock.createPayment(PAYMENT_METHOD, SHOPPER_BANK_CODE, COUNTRY_CODE)).thenReturn(paymentMock);
        when(worldpayOrderServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, sessionMock, browserMock)).thenReturn(shopperMock);
        when(bankTransferAdditionalAuthInfoMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(bankTransferAdditionalAuthInfoMock.getShopperBankCode()).thenReturn(SHOPPER_BANK_CODE);
        when(bankTransferAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayDeliveryAddressStrategyMock.getDeliveryAddress(cartModelMock)).thenReturn(deliveryAddressModelMock);
    }

    @Test
    public void shouldCreateTokenRequest() throws Exception {
        when(cseAdditionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(false);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenRequest(worldpayConfigMock, merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void shouldUseBillingAddressWhenNoShippingAddress() throws Exception { // User has selected "pick up in store" only
        when(cartModelMock.getDeliveryAddress()).thenReturn(null);
        when(cseAdditionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(false);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenRequest(worldpayConfigMock, merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void shouldUseShippingAsBilling() throws Exception {
        when(cseAdditionalAuthInfoMock.getUsingShippingAsBilling()).thenReturn(true);
        doReturn(directTokenisedAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(worldpayConfigMock, merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock);
        doReturn(csePaymentMock).when(testObj).createCsePayment(cseAdditionalAuthInfoMock, shippingAddressMock);

        testObj.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenRequest(worldpayConfigMock, merchantInfoMock, AUTHENTICATED_SHOPPER_ID, csePaymentMock, tokenRequestMockWithReasonNull);
    }

    @Test
    public void shouldCreateTokenisedDirectAuthoriseRequest() throws Exception {
        doReturn(directTokenisedAuthoriseServiceRequestMock).when(testObj).createTokenisedDirectAuthoriseRequest(worldpayConfigMock, merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock);

        testObj.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(worldpayConfigMock, merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock);
    }

    @Test
    public void shouldCreate3DSecureAuthoriseRequest() throws WorldpayException {
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createDirect3DAuthoriseRequest(worldpayConfigMock, merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, sessionMock, PA_RESPONSE, ECHO_DATA, shippingAddressMock);

        final DirectAuthoriseServiceRequest result = testObj.build3dDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE, ECHO_DATA, COOKIE);

        assertEquals(directAuthoriseServiceRequestMock, result);
        verify(directAuthoriseServiceRequestMock).setCookie(COOKIE);
    }

    @Test
    public void shouldCreateDirectAuthoriseBankTransferRequest() throws WorldpayConfigurationException {
        when(shippingAddressMock.getCountryCode()).thenReturn(COUNTRY_CODE);
        doReturn(directAuthoriseServiceRequestMock).when(testObj).createDirectAuthoriseRequest(worldpayConfigMock, merchantInfoMock, basicOrderInfoMock, paymentMock, shopperMock, shippingAddressMock, billingAddressMock, STATEMENT_NARRATIVE);

        final DirectAuthoriseServiceRequest result = testObj.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceRequestMock, result);
    }

    @Test
    public void shouldCreateUpdateTokenRequest() throws WorldpayConfigurationException {
        when(worldpayOrderServiceMock.createTokenRequest(TOKEN_EVENT_REFERENCE, TOKEN_UPDATED + DateTime.now().toString(TOKEN_UPDATE_DATE_FORMAT))).thenReturn(tokenRequestMockWithReason);

        when(createTokenResponseMock.getToken().getTokenDetails().getPaymentTokenID()).thenReturn(PAYMENT_TOKEN_ID);

        when(cseAdditionalAuthInfoMock.getCardHolderName()).thenReturn(CARD_HOLDER_NAME);
        when(cseAdditionalAuthInfoMock.getExpiryMonth()).thenReturn(EXPIRY_MONTH);
        when(cseAdditionalAuthInfoMock.getExpiryYear()).thenReturn(EXPIRY_YEAR);

        testObj.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, createTokenResponseMock);

        verify(testObj).createUpdateTokenServiceRequest(eq(merchantInfoMock), eq(worldpayAdditionalInfoDataMock), eq(worldpayConfigMock), eq(tokenRequestMockWithReason), eq(PAYMENT_TOKEN_ID), cardDetailsCaptor.capture());
        final CardDetails cardDetails = cardDetailsCaptor.getValue();
        assertEquals(CARD_HOLDER_NAME, cardDetails.getCardHolderName());
        assertEquals(EXPIRY_MONTH, cardDetails.getExpiryDate().getMonth());
        assertEquals(EXPIRY_YEAR, cardDetails.getExpiryDate().getYear());
    }

    @Test
    public void shouldCreateAuthoriseRecurringPaymentRequest() throws WorldpayConfigurationException {
        testObj.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(testObj).createTokenisedDirectAuthoriseRequest(worldpayConfigMock, merchantInfoMock, basicOrderInfoMock, tokenMock, authenticatedShopperMock, shippingAddressMock);
    }
}