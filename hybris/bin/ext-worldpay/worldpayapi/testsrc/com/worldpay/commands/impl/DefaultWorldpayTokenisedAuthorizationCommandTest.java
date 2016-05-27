package com.worldpay.commands.impl;

import com.worldpay.config.Environment;
import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.BasicOrderInfo;
import com.worldpay.service.model.Browser;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.Order;
import com.worldpay.service.model.Session;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.token.Token;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import static com.worldpay.commands.impl.DefaultWorldpayTokenisedAuthorizationCommand.UNKNOWN_MERCHANT_CODE;
import static com.worldpay.config.Environment.EnvironmentRole.TEST;
import static com.worldpay.service.model.AuthorisedStatus.REFUSED;
import static de.hybris.platform.payment.dto.TransactionStatus.ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.GENERAL_SYSTEM_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayTokenisedAuthorizationCommandTest {

    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final Currency CURRENCY = Currency.getInstance(Locale.UK);
    private static final String PAYMENT_TOKEN_ID = "paymentTokenId";
    private static final String PAYMENT_REPLY_AMOUNT = "330";
    private static final String CVV = "CVV";
    private static final String REQUEST_AMOUNT = "100";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String SESSION_ID = "sessionId";
    private static final String IP_ADDRESS = "192.168.0.1";
    private static final String SECURITY_CODE = "securityCode";
    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String WORLDPAY_ORDER_CODE = "originalOrderCode";
    private static final String VERSION = "1.4";
    private static final String ENDPOINT_URL = "endpointUrl";

    @Spy
    @InjectMocks
    private DefaultWorldpayTokenisedAuthorizationCommand testObj = new DefaultWorldpayTokenisedAuthorizationCommand();
    @Mock (name = "worldpayAuthorizationResultConverter")
    private Converter<DirectAuthoriseServiceResponse, AuthorizationResult> worldpayAuthorizationResultConverterMock;
    @Mock (name = "worldpayBillingInfoAddressConverter")
    private Converter<BillingInfo, Address> worldpayBillingInfoAddressConverterMock;

    @Mock
    private SubscriptionAuthorizationRequest subscriptionAuthorizationRequestMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private WorldpayServiceGateway worldpayGatewayMock;
    @Mock
    private WorldpayConfigLookupService worldpayConfigLookupServiceMock;
    @Mock
    private WorldpayConfig worldpayConfigMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Captor
    private ArgumentCaptor<DirectAuthoriseServiceRequest> directAuthoriseServiceRequestCaptor;
    @Mock
    private AuthorizationResult authorizationResultMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private Amount amountMock;
    @Mock
    private BasicOrderInfo basicOrderInfoMock;
    @Mock
    private Shopper shopperMock;
    @Mock
    private Address addressMock;
    @Mock
    private Environment environmentMock;
    @Mock
    private Token tokenMock;

    @Before
    public void setUp() throws WorldpayException {
        doReturn(worldpayAdditionalInfoDataMock).when(testObj).getWorldpayAdditionalInfoData(CVV);

        when(worldpayAuthorizationResultConverterMock.convert(directAuthoriseServiceResponseMock)).thenReturn(authorizationResultMock);
        mockSubscriptionAuthorizationRequest();
        mockAdditionalInfo();
        mockWorldpayConfig();
        doReturn(tokenMock).when(testObj).createToken(PAYMENT_TOKEN_ID, SECURITY_CODE);
        when(worldpayOrderServiceMock.getWorldpayServiceGateway()).thenReturn(worldpayGatewayMock);
        when(worldpayOrderServiceMock.createAmount(CURRENCY, subscriptionAuthorizationRequestMock.getTotalAmount().doubleValue())).thenReturn(amountMock);
        when(worldpayOrderServiceMock.createBasicOrderInfo(eq(WORLDPAY_ORDER_CODE), anyString(), eq(amountMock))).thenReturn(basicOrderInfoMock);
        when(worldpayOrderServiceMock.createAuthenticatedShopper(eq(CUSTOMER_EMAIL), eq(AUTHENTICATED_SHOPPER_ID), any(Session.class), any(Browser.class))).thenReturn(shopperMock);
        when(shopperMock.getShopperEmailAddress()).thenReturn(CUSTOMER_EMAIL);
        when(basicOrderInfoMock.getAmount()).thenReturn(amountMock);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(any(UiExperienceLevel.class))).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayBillingInfoAddressConverterMock.convert(subscriptionAuthorizationRequestMock.getShippingInfo())).thenReturn(addressMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void performShouldReturnAuthorizationResultWhenResponseIsAuthorisedUsingCurrentSiteMerchant() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAmount().getValue()).thenReturn(PAYMENT_REPLY_AMOUNT);
        when(authorizationResultMock.getCurrency()).thenReturn(CURRENCY);
        when(authorizationResultMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED);

        testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        verify(worldpayAuthorizationResultConverterMock).convert(directAuthoriseServiceResponseMock);
        verify(authorizationResultMock).setMerchantTransactionCode(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestId(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestToken(MERCHANT_CODE);
        verify(authorizationResultMock).setCurrency(subscriptionAuthorizationRequestMock.getCurrency());
        verify(authorizationResultMock).setAuthorizationTime(any(Date.class));
        verify(authorizationResultMock).setTotalAmount(new BigDecimal(PAYMENT_REPLY_AMOUNT).movePointLeft(CURRENCY.getDefaultFractionDigits()));
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(any(UiExperienceLevel.class));
        checkDirectAuthoriseServiceRequestValues();
    }

    @Test
    public void performShouldReturnErrorAuthorizationDueToMisConfigurationAndShouldNeverTryToDirectAuthorise() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAmount().getValue()).thenReturn(PAYMENT_REPLY_AMOUNT);
        when(authorizationResultMock.getCurrency()).thenReturn(CURRENCY);
        when(authorizationResultMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(any(UiExperienceLevel.class))).thenReturn(null);

        final AuthorizationResult result = testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock, never()).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        verify(worldpayAuthorizationResultConverterMock, never()).convert(directAuthoriseServiceResponseMock);

        assertEquals(ERROR, result.getTransactionStatus());
        assertEquals(GENERAL_SYSTEM_ERROR, result.getTransactionStatusDetails());
        assertEquals(WORLDPAY_ORDER_CODE, result.getMerchantTransactionCode());
        assertEquals(WORLDPAY_ORDER_CODE, result.getRequestId());
        assertEquals(UNKNOWN_MERCHANT_CODE, result.getRequestToken());
        assertEquals(new BigDecimal(REQUEST_AMOUNT), result.getTotalAmount());
        assertEquals(Currency.getInstance(Locale.UK), result.getCurrency());
    }

    @Test
    public void performShouldReturnAuthorizationResultWhenResponseIsNotErrorAndNotAuthorised() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAmount().getValue()).thenReturn(PAYMENT_REPLY_AMOUNT);
        when(directAuthoriseServiceResponseMock.getPaymentReply().getAuthStatus()).thenReturn(REFUSED);
        when(authorizationResultMock.getCurrency()).thenReturn(CURRENCY);

        testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        verify(worldpayAuthorizationResultConverterMock).convert(directAuthoriseServiceResponseMock);

        verify(authorizationResultMock).setMerchantTransactionCode(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestId(WORLDPAY_ORDER_CODE);
        verify(authorizationResultMock).setRequestToken(MERCHANT_CODE);
        verify(authorizationResultMock).setCurrency(subscriptionAuthorizationRequestMock.getCurrency());
        verify(authorizationResultMock).setAuthorizationTime(any(Date.class));
        verify(authorizationResultMock).setTotalAmount((new BigDecimal(REQUEST_AMOUNT)));

        checkDirectAuthoriseServiceRequestValues();
    }

    @Test
    public void performShouldReturnErrorAuthorizationResultWhenResponseIsNull() throws WorldpayException {
        when(worldpayGatewayMock.directAuthorise(directAuthoriseServiceRequestCaptor.capture())).thenReturn(null);

        final AuthorizationResult result = testObj.perform(subscriptionAuthorizationRequestMock);

        verify(worldpayGatewayMock).directAuthorise(directAuthoriseServiceRequestCaptor.capture());
        checkDirectAuthoriseServiceRequestValues();

        assertEquals(ERROR, result.getTransactionStatus());
        assertEquals(GENERAL_SYSTEM_ERROR, result.getTransactionStatusDetails());
        assertEquals(WORLDPAY_ORDER_CODE, result.getMerchantTransactionCode());
        assertEquals(WORLDPAY_ORDER_CODE, result.getRequestId());
        assertEquals(MERCHANT_CODE, result.getRequestToken());
        assertEquals(new BigDecimal(REQUEST_AMOUNT), result.getTotalAmount());
        assertEquals(Currency.getInstance(Locale.UK), result.getCurrency());
    }

    private void mockSubscriptionAuthorizationRequest() {
        when(subscriptionAuthorizationRequestMock.getCv2()).thenReturn(CVV);
        when(subscriptionAuthorizationRequestMock.getCurrency()).thenReturn(CURRENCY);
        when(subscriptionAuthorizationRequestMock.getMerchantTransactionCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(subscriptionAuthorizationRequestMock.getTotalAmount()).thenReturn(new BigDecimal(REQUEST_AMOUNT));
        when(subscriptionAuthorizationRequestMock.getSubscriptionID()).thenReturn(PAYMENT_TOKEN_ID);
    }

    private void checkDirectAuthoriseServiceRequestValues() {
        final DirectAuthoriseServiceRequest directAuthoriseServiceRequest = directAuthoriseServiceRequestCaptor.getValue();
        // Not checking all the mocked fields for DirectAuthoriseServiceRequest
        assertEquals(TEST, directAuthoriseServiceRequest.getWorldpayConfig().getEnvironment().getRole());
        final Order order = directAuthoriseServiceRequest.getOrder();
        assertEquals(addressMock, order.getShippingAddress());
        final Token token = (Token) order.getPaymentDetails().getPayment();
        assertEquals(tokenMock, token);
        final Shopper shopper = order.getShopper();
        assertNotNull(shopper);
        assertEquals(CUSTOMER_EMAIL, shopper.getShopperEmailAddress());
    }

    private void mockAdditionalInfo() {
        when(worldpayAdditionalInfoDataMock.getCustomerIPAddress()).thenReturn(IP_ADDRESS);
        when(worldpayAdditionalInfoDataMock.getSessionId()).thenReturn(SESSION_ID);
        when(worldpayAdditionalInfoDataMock.isSavedCardPayment()).thenReturn(Boolean.TRUE);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(SECURITY_CODE);
        when(worldpayAdditionalInfoDataMock.getCustomerEmail()).thenReturn(CUSTOMER_EMAIL);
    }

    private void mockWorldpayConfig() throws WorldpayConfigurationException {
        when(worldpayConfigLookupServiceMock.lookupConfig()).thenReturn(worldpayConfigMock);
        when(worldpayConfigMock.getVersion()).thenReturn(VERSION);
        when(worldpayConfigMock.getEnvironment()).thenReturn(environmentMock);
        when(environmentMock.getRole()).thenReturn(TEST);
        when(environmentMock.getEndpoint()).thenReturn(ENDPOINT_URL);
    }
}