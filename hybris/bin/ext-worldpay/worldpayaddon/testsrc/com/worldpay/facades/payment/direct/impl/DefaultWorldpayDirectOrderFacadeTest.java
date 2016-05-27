package com.worldpay.facades.payment.direct.impl;

import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.data.CSEAdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.model.Request3DInfo;
import com.worldpay.service.payment.WorldpayDirectOrderService;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.payment.TransactionStatus.AUTHENTICATION_REQUIRED;
import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.service.model.AuthorisedStatus.CANCELLED;
import static com.worldpay.service.model.AuthorisedStatus.REFUNDED;
import static com.worldpay.service.model.AuthorisedStatus.REFUSED;
import static de.hybris.platform.commerceservices.enums.UiExperienceLevel.DESKTOP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayDirectOrderFacadeTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ISSUER_URL = "issuerURL";
    private static final String PA_REQUEST = "paRequest";
    private static final String PA_RESPONSE = "paResponse";
    private static final Integer RETURN_CODE = 12;
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String URL_TO_BANK = "urlToBank";
    private static final String CVC = "cvc";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";

    @InjectMocks
    private DefaultWorldpayDirectOrderFacade testObj = new DefaultWorldpayDirectOrderFacade();

    @Mock
    private CSEAdditionalAuthInfo cseAdditionalAuthInfoMock;
    @Mock
    private BankTransferAdditionalAuthInfo bankTransferAdditionAuthInfoMock;
    @Mock
    private WorldpayDirectOrderService worldpayDirectOrderServiceMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private UiExperienceService uiExperienceServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock, directAuthoriseServiceResponse3dSecureMock;
    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private ErrorDetail errorDetailMock;
    @Mock
    private Request3DInfo request3DInfoMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private RedirectReference redirectReferenceMock;
    @Mock
    private UserModel userModelMock;

    @SuppressWarnings ("PMD")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        when(worldpayDirectOrderServiceMock.authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(worldpayDirectOrderServiceMock.authorise3DSecure(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE)).thenReturn(directAuthoriseServiceResponse3dSecureMock);
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.TRUE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getUser()).thenReturn(userModelMock);
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(DESKTOP)).thenReturn(merchantInfoMock);
        when(uiExperienceServiceMock.getUiExperienceLevel()).thenReturn(DESKTOP);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.AUTHORISED);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(userModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void shouldReturnOrderDataWhenAuthorisedAndOrderPlaced() throws Exception {
        when(cseAdditionalAuthInfoMock.getSaveCard()).thenReturn(Boolean.FALSE);
        when(worldpayAdditionalInfoDataMock.getSecurityCode()).thenReturn(CVC);

        final DirectResponseData result = testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);

        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).createToken(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
        assertEquals(TransactionStatus.AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void authoriseShouldReturnResponseDataWithStatusRefusedAndOrderDataNull() throws Exception {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class), anyString());
        assertNull(result.getOrderData());
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
    }

    @Test
    public void authoriseShouldNotCompleteOrderWhenPaymentReplyIsCancelled() throws Exception {
        when(paymentReplyMock.getAuthStatus()).thenReturn(CANCELLED);

        final DirectResponseData authorise = testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class), anyString());
        assertEquals(TransactionStatus.CANCELLED, authorise.getTransactionStatus());
    }


    @Test
    public void authoriseShouldRaiseIllegalStateExceptionWhenTryingToAuthoriseWithoutCart() throws WorldpayException, InvalidCartException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Cannot authorize payment where there is no cart");
        when(cartServiceMock.hasSessionCart()).thenReturn(Boolean.FALSE);

        testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test (expected = WorldpayConfigurationException.class)
    public void authoriseShouldRaiseWorldpayConfigurationExceptionWhenMerchantsAreNotProperlyConfigured() throws WorldpayException, InvalidCartException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(DESKTOP)).thenThrow(new WorldpayConfigurationException(EXCEPTION_MESSAGE));

        testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock, never()).authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test (expected = WorldpayException.class)
    public void authoriseShouldRaiseExceptionWhenPaymentReplayIsNullAndErrorDetailIsNotNull() throws Exception {
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);

        testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).authorise(merchantInfoMock, cartModelMock, cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise(any(DirectAuthoriseServiceResponse.class), any(CartModel.class), anyString());
    }

    @Test
    public void shouldReturnDirectResponseDataWith3DSecureInformationWhenDirectAuthoriseServiceResponseContainsRequest3DInfo() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(request3DInfoMock.getIssuerUrl()).thenReturn(ISSUER_URL);
        when(request3DInfoMock.getPaRequest()).thenReturn(PA_REQUEST);

        final DirectResponseData result = testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(ISSUER_URL, result.getIssuerURL());
        assertEquals(PA_REQUEST, result.getPaRequest());
        assertEquals(AUTHENTICATION_REQUIRED, result.getTransactionStatus());
    }

    @Test
    public void shouldReturnDirectResponseDataWithOrderDataWhenAuthorise3DSecureIsAuthorised() throws WorldpayException, InvalidCartException {
        final DirectResponseData result = testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);

        inOrder.verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise3DSecure(directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(AUTHORISED, result.getTransactionStatus());
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
    }

    @Test
    public void shouldReturnDirectResponseDataWithRefusedStatusWhenAuthorise3DSecureIsRefused() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.REFUSED);
        when(paymentReplyMock.getReturnCode()).thenReturn(RETURN_CODE);

        final DirectResponseData result = testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        assertEquals(RETURN_CODE, result.getReturnCode());
        assertEquals(TransactionStatus.REFUSED, result.getTransactionStatus());
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test (expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhen3dAuthStatusInvalid() throws WorldpayException, InvalidCartException {
        when(paymentReplyMock.getAuthStatus()).thenReturn(REFUNDED);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test (expected = WorldpayException.class)
    public void authorise3DSecureShouldRaiseExceptionWhenPaymentReplyIsNull() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponse3dSecureMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponse3dSecureMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayDirectOrderServiceMock).authorise3DSecure(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock, PA_RESPONSE);
        verify(worldpayDirectOrderServiceMock, never()).completeAuthorise3DSecure(directAuthoriseServiceResponse3dSecureMock, merchantInfoMock);
    }

    @Test
    public void authoriseShouldGenerateAuthenticatedShopperId() throws InvalidCartException, WorldpayException {

        testObj.authorise(cseAdditionalAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void authorise3dSecureShouldGenerateAuthenticatedShopperId() throws InvalidCartException, WorldpayException {

        testObj.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void shouldReturnBankURLWhenAuthorisedAndOrderPlacedWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getUrl()).thenReturn(URL_TO_BANK);

        final String result = testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        assertEquals(URL_TO_BANK, result);
    }

    @Test (expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenErrorOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test (expected = WorldpayException.class)
    public void shouldRaiseExceptionWhenNoErrorNoRedirectReferenceWasReturnedOccurredAuthorisingRedirectWithBankTransfer() throws WorldpayException {
        when(worldpayDirectOrderServiceMock.authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock)).thenReturn(directAuthoriseServiceResponseMock);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(null);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.authoriseBankTransferRedirect(bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);

        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).authoriseBankTransfer(merchantInfoMock, cartModelMock, bankTransferAdditionAuthInfoMock, worldpayAdditionalInfoDataMock);
    }

    @Test
    public void shouldAuthoriseRecurringPayment() throws WorldpayException, InvalidCartException {

        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);

        final InOrder inOrder = inOrder(acceleratorCheckoutFacadeMock, worldpayDirectOrderServiceMock);
        verify(uiExperienceServiceMock).getUiExperienceLevel();
        verify(worldpayMerchantInfoServiceMock).getCurrentSiteMerchant(DESKTOP);
        verify(worldpayDirectOrderServiceMock).authoriseRecurringPayment(merchantInfoMock, cartModelMock, worldpayAdditionalInfoDataMock);
        inOrder.verify(worldpayDirectOrderServiceMock).completeAuthorise(directAuthoriseServiceResponseMock, cartModelMock, MERCHANT_CODE);
        inOrder.verify(acceleratorCheckoutFacadeMock).placeOrder();
        assertEquals(acceleratorCheckoutFacadeMock.placeOrder(), result.getOrderData());
        assertEquals(TransactionStatus.AUTHORISED, result.getTransactionStatus());
    }

    @Test
    public void shouldRedirectTo3DSecureOnRecurringPayment() throws WorldpayException, InvalidCartException {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponseMock.getRequest3DInfo()).thenReturn(request3DInfoMock);
        when(request3DInfoMock.getIssuerUrl()).thenReturn(ISSUER_URL);
        when(request3DInfoMock.getPaRequest()).thenReturn(PA_REQUEST);

        final DirectResponseData result = testObj.authoriseRecurringPayment(worldpayAdditionalInfoDataMock);

        verify(worldpayAdditionalInfoDataMock).setAuthenticatedShopperId(AUTHENTICATED_SHOPPER_ID);
        assertEquals(ISSUER_URL, result.getIssuerURL());
        assertEquals(PA_REQUEST, result.getPaRequest());
        assertEquals(AUTHENTICATION_REQUIRED, result.getTransactionStatus());
    }
}