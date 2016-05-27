package com.worldpay.service.payment.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.enums.token.TokenEvent;
import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Address;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.Browser;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.model.Session;
import com.worldpay.service.model.Shopper;
import com.worldpay.service.model.payment.Payment;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.payment.request.WorldpayRequestFactory;
import com.worldpay.service.request.CreateTokenServiceRequest;
import com.worldpay.service.request.DirectAuthoriseServiceRequest;
import com.worldpay.service.request.UpdateTokenServiceRequest;
import com.worldpay.service.response.CreateTokenResponse;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.service.response.UpdateTokenResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static com.worldpay.service.payment.impl.DefaultWorldpayDirectOrderService.THREE_D_SECURE_COOKIE_PARAM;
import static com.worldpay.service.payment.impl.DefaultWorldpayDirectOrderService.THREE_D_SECURE_ECHO_DATA_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayDirectOrderServiceTest {

    private static final String SHOPPER_EMAIL_ADDRESS = "shopperEmailAddress";
    private static final String STATEMENT_NARRATIVE = "statementNarrative";
    private static final String ORDER_CODE = "orderCode";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String PA_RESPONSE = "paResponse";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String BANK_TRANSFER_PAYMENT_METHOD = "bankTransferPaymentMethod";
    private static final String BANK_CODE = "bankCode";
    private static final String ECHO_DATA = "echoData";
    private static final String COOKIE = "cookie";

    @Spy
    @InjectMocks
    private DefaultWorldpayDirectOrderService testObj = new DefaultWorldpayDirectOrderService();

    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private Payment paymentMock;
    @Mock
    private Session sessionMock;
    @Mock
    private Browser browserMock;
    @Mock
    private Address shippingAddressMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceRequest directAuthoriseServiceRequestMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private DirectAuthoriseServiceRequest directAuthoriseBankTransferServiceRequestMock;
    @Mock
    private AddressModel deliveryAddressModelMock, paymentAddressModelMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private Request3DInfo request3DInfoMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private Shopper shopperMock;
    @Mock
    private CreateTokenServiceRequest createTokenServiceRequestMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CreateTokenResponse createTokenResponse;
    @Mock
    private WorldpayRequestFactory worldpayRequestFactoryMock;
    @Mock
    private BankTransferAdditionalAuthInfo bankTransferAdditionalAuthInfoMock;
    @Mock
    private ErrorDetail errorDetailMock;
    @Mock
    private Amount amountMock;
    @Mock
    private UpdateTokenServiceRequest updateTokenServiceRequestMock;
    @Mock
    private UpdateTokenResponse updateTokenResponseMock;

    @Before
    public void setup() throws WorldpayException {
        doReturn(commerceCheckoutParameterMock).when(testObj).createCommerceCheckoutParameter(cartModelMock, creditCardPaymentInfoModelMock, BigDecimal.TEN.setScale(2, BigDecimal.ROUND_CEILING));
        when(cartModelMock.getPaymentAddress()).thenReturn(paymentAddressModelMock);
        when(cseAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AUTHORISED);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(amountMock.getCurrencyCode()).thenReturn("GBP");
        when(amountMock.getValue()).thenReturn("1000");
        when(worldpayOrderServiceMock.getWorldpayServiceGateway()).thenReturn(worldpayServiceGatewayMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayServiceGatewayMock.updateToken(updateTokenServiceRequestMock)).thenReturn(updateTokenResponseMock);
        when(worldpayOrderServiceMock.createShopper(SHOPPER_EMAIL_ADDRESS, sessionMock, browserMock)).thenReturn(shopperMock);
        when(shopperMock.getSession()).thenReturn(sessionMock);
        when(cartModelMock.getCode()).thenReturn(ORDER_CODE);
        when(modelServiceMock.clone(paymentAddressModelMock)).thenReturn(deliveryAddressModelMock);
        when(worldpayPaymentTransactionServiceMock.createPaymentTransaction(false, MERCHANT_CODE, commerceCheckoutParameterMock)).thenReturn(paymentTransactionModelMock);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock)).thenReturn(paymentTransactionEntryModelMock);
        when(worldpayAdditionalInfoDataMock.getAuthenticatedShopperId()).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayRequestFactoryMock.buildTokenRequest(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(createTokenServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getCookie()).thenReturn(COOKIE);
        when(directAuthoriseServiceResponseMock.getEchoData()).thenReturn(ECHO_DATA);
    }

    @Test
    public void shouldNotStoreCookieAndEchoDataInSession() throws Exception {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(null);

        testObj.authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
    }

    @Test
    public void authoriseBankTransferShouldBuildRequestWithBankTransferPaymentMethod() throws WorldpayException {
        when(bankTransferAdditionalAuthInfoMock.getPaymentMethod()).thenReturn(BANK_TRANSFER_PAYMENT_METHOD);
        when(bankTransferAdditionalAuthInfoMock.getShopperBankCode()).thenReturn(BANK_CODE);
        when(bankTransferAdditionalAuthInfoMock.getStatementNarrative()).thenReturn(STATEMENT_NARRATIVE);
        when(worldpayOrderServiceMock.createPayment(BANK_TRANSFER_PAYMENT_METHOD, BANK_CODE, shippingAddressMock.getCountryCode())).thenReturn(paymentMock);
        when(worldpayRequestFactoryMock.buildDirectAuthoriseBankTransferRequest(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseBankTransferServiceRequestMock);
        when(worldpayServiceGatewayMock.directAuthorise(directAuthoriseBankTransferServiceRequestMock)).thenReturn(directAuthoriseServiceResponseMock);

        final DirectAuthoriseServiceResponse directAuthoriseServiceResponse = testObj.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(directAuthoriseServiceResponseMock, directAuthoriseServiceResponse);
    }


    @Test
    public void shouldStoreCookieAndEchoDataInSession() throws Exception {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);

        testObj.authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_COOKIE_PARAM, COOKIE);
        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, ECHO_DATA);
    }

    @Test
    public void shouldDirectAuthoriseWithGateway() throws Exception {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void shouldRecoverEchoDataAndCookieFromSessionAndAddTo3dRequest() throws WorldpayException {
        when(worldpayRequestFactoryMock.build3dDirectAuthoriseRequest(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE, ECHO_DATA, COOKIE)).thenReturn(directAuthoriseServiceRequestMock);
        when(sessionServiceMock.getAttribute(THREE_D_SECURE_COOKIE_PARAM)).thenReturn(COOKIE);
        when(sessionServiceMock.getAttribute(THREE_D_SECURE_ECHO_DATA_PARAM)).thenReturn(ECHO_DATA);

        testObj.authorise3DSecure(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE);

        verify(sessionServiceMock).removeAttribute(THREE_D_SECURE_COOKIE_PARAM);
        verify(sessionServiceMock).removeAttribute(THREE_D_SECURE_ECHO_DATA_PARAM);
        verify(worldpayServiceGatewayMock).directAuthorise(directAuthoriseServiceRequestMock);
    }

    @Test
    public void shouldCreateToken() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponse);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock).createCreditCardPaymentInfo(cartModelMock, createTokenResponse, cseAdditionalAuthInfoMock.getSaveCard());
    }

    @Test (expected = WorldpayException.class)
    public void shouldRaiseErrorWhenCreateTokenRequestContainsError() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponse);
        when(createTokenResponse.isError()).thenReturn(true);
        when(createTokenResponse.getErrorDetail()).thenReturn(errorDetailMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(cartModelMock, createTokenResponse, cseAdditionalAuthInfoMock.getSaveCard());
        verify(worldpayServiceGatewayMock, times(1));
    }

    @Test
    public void shouldCreateCreditCardForSuccessfulTokenResponse() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponse);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(cartModelMock, createTokenResponse, false)).thenReturn(creditCardPaymentInfoModelMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).updateCreditCardPaymentInfo(eq(cartModelMock), any(UpdateTokenServiceRequest.class));
        verify(cartModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).save(cartModelMock);
    }

    @Test
    public void shouldUpdateTokenOnConflict() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponse);
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(false);
        when(createTokenResponse.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, createTokenResponse)).thenReturn(updateTokenServiceRequestMock);
        when(worldpayPaymentInfoServiceMock.updateCreditCardPaymentInfo(cartModelMock, updateTokenServiceRequestMock)).thenReturn(creditCardPaymentInfoModelMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayPaymentInfoServiceMock, never()).createCreditCardPaymentInfo(cartModelMock, createTokenResponse, false);
        verify(worldpayServiceGatewayMock).updateToken(updateTokenServiceRequestMock);
        verify(cartModelMock).setPaymentInfo(creditCardPaymentInfoModelMock);
        verify(modelServiceMock).save(cartModelMock);
    }

    @Test (expected = WorldpayException.class)
    public void shouldThrowExceptionOnErrorInUpdateResponse() throws WorldpayException {
        when(worldpayServiceGatewayMock.createToken(createTokenServiceRequestMock)).thenReturn(createTokenResponse);
        when(createTokenResponse.getToken().getTokenDetails().getTokenEvent()).thenReturn(TokenEvent.CONFLICT.name());
        when(worldpayRequestFactoryMock.buildTokenUpdateRequest(merchantInfoMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock, createTokenResponse)).thenReturn(updateTokenServiceRequestMock);
        when(updateTokenResponseMock.isError()).thenReturn(true);
        when(updateTokenResponseMock.getErrorDetail()).thenReturn(errorDetailMock);

        testObj.createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verifyZeroInteractions(worldpayPaymentInfoServiceMock.createCreditCardPaymentInfo(any(), any(), any()));
    }

    @Test
    public void completeAuthoriseShouldCreateNonPendingAuthorisePaymentTransactionEntry() {
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);

        testObj.completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);

        verify(worldpayPaymentTransactionServiceMock).createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock);
        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterMock);
        verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayPaymentTransactionServiceMock).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
        verify(worldpayPaymentInfoServiceMock).setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);
    }

    @Test
    public void shouldCompleteAuthoriseWhenComplete3DAuthorise() {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(directAuthoriseServiceRequestMock.getMerchantInfo().getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(worldpayPaymentTransactionServiceMock.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, MERCHANT_CODE, cartModelMock)).thenReturn(paymentTransactionEntryModelMock);
        when(cartModelMock.getPaymentInfo()).thenReturn(creditCardPaymentInfoModelMock);

        testObj.completeAuthorise3DSecure(directAuthoriseServiceResponseMock, merchantInfoMock);

        verify(testObj).cloneAndSetBillingAddressFromCart(cartModelMock, creditCardPaymentInfoModelMock);
        verify(commerceCheckoutServiceMock).setPaymentInfo(commerceCheckoutParameterMock);
        verify(worldpayPaymentInfoServiceMock).updateAndAttachPaymentInfoModel(paymentTransactionModelMock, cartModelMock, creditCardPaymentInfoModelMock);
        verify(worldpayPaymentTransactionServiceMock).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
        verify(worldpayPaymentInfoServiceMock).setCreditCardType(creditCardPaymentInfoModelMock, paymentReplyMock);
    }

    @Test
    public void shouldAuthoriseRecurringPayment() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_COOKIE_PARAM), anyString());
        verify(sessionServiceMock, never()).setAttribute(eq(THREE_D_SECURE_ECHO_DATA_PARAM), anyString());
        assertEquals(directAuthoriseServiceResponseMock, result);
    }

    @Test
    public void shouldAddSessionAttributesWhenResponseContainsRequest3DInfoOnRecurringPayment() throws WorldpayException {
        when(worldpayRequestFactoryMock.buildDirectAuthoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceRequestMock);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);

        final DirectAuthoriseServiceResponse result = testObj.authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_COOKIE_PARAM, COOKIE);
        verify(sessionServiceMock).setAttribute(THREE_D_SECURE_ECHO_DATA_PARAM, ECHO_DATA);
        assertEquals(directAuthoriseServiceResponseMock, result);
    }
}