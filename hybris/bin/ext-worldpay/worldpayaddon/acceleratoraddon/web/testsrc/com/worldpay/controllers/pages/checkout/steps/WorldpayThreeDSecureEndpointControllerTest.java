package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.forms.ThreeDSecureForm;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.REDIRECT_URL_CHOOSE_PAYMENT_METHOD;
import static com.worldpay.payment.TransactionStatus.REFUSED;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayThreeDSecureEndpointControllerTest {

    private static final String PA_RESPONSE = "paResponse";
    private static final String CHECKOUT_ORDER_CONFIRMATION = "/checkout/orderConfirmation/";
    private static final String GUID = "guid";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final int RETURN_CODE = 13;

    @Spy
    @InjectMocks
    private WorldpayThreeDSecureEndpointController testObj = new WorldpayThreeDSecureEndpointController();

    @Mock
    private ThreeDSecureForm threeDSecureFormMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategyMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;

    @Before
    public void setup() throws InvalidCartException, WorldpayException {
        when(threeDSecureFormMock.getPaRes()).thenReturn(PA_RESPONSE);
        when(threeDSecureFormMock.getMD()).thenReturn(WORLDPAY_ORDER_CODE);
        when(worldpayDirectOrderFacadeMock.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock)).thenReturn(directResponseDataMock);
        when(checkoutCustomerStrategyMock.isAnonymousCheckout()).thenReturn(true);
        when(orderDataMock.getGuid()).thenReturn(GUID);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(requestMock)).thenReturn(worldpayAdditionalInfoDataMock);
    }

    @Test
    public void shouldRedirectToOrderConfirmationPageOnAuthorisedDirectResponse() throws InvalidCartException, WorldpayException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.AUTHORISED);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, threeDSecureFormMock, redirectAttributesMock);

        verify(worldpayDirectOrderFacadeMock).authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);
        assertTrue(result.contains(CHECKOUT_ORDER_CONFIRMATION));
    }

    @Test
    public void shouldRedirectToChoosePaymentPageOnRefusedDirectResponse() throws InvalidCartException, WorldpayException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(REFUSED);
        when(directResponseDataMock.getReturnCode()).thenReturn(RETURN_CODE);

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, threeDSecureFormMock, redirectAttributesMock);

        verify(worldpayCartServiceMock).setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, RETURN_CODE);
        verify(worldpayDirectOrderFacadeMock).authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);
        assertTrue(result.contains(REDIRECT_URL_CHOOSE_PAYMENT_METHOD));
    }

    @Test
    public void shouldRedirectToErrorPageOnErrorDirectResponse() throws InvalidCartException, WorldpayException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.ERROR);

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, threeDSecureFormMock, redirectAttributesMock);

        verify(worldpayDirectOrderFacadeMock).authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);
        assertTrue(result.contains(REDIRECT_URL_CHOOSE_PAYMENT_METHOD));
    }

    @Test
    public void shouldRedirectToErrorPageWhenInvalidCartExceptionThrown() throws InvalidCartException, WorldpayException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.ERROR);
        when(worldpayDirectOrderFacadeMock.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock)).thenThrow(new InvalidCartException(EXCEPTION_MESSAGE));

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, threeDSecureFormMock, redirectAttributesMock);

        verify(worldpayDirectOrderFacadeMock).authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);
        assertTrue(result.contains(REDIRECT_URL_CHOOSE_PAYMENT_METHOD));
    }

    @Test
    public void shouldRedirectToErrorPageWhenWorldpayExceptionThrown() throws InvalidCartException, WorldpayException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.ERROR);
        when(worldpayDirectOrderFacadeMock.authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock)).thenThrow(new WorldpayException(EXCEPTION_MESSAGE));

        final String result = testObj.doHandleThreeDSecureResponse(requestMock, threeDSecureFormMock, redirectAttributesMock);

        verify(worldpayDirectOrderFacadeMock).authorise3DSecure(PA_RESPONSE, worldpayAdditionalInfoDataMock);
        assertTrue(result.contains(REDIRECT_URL_CHOOSE_PAYMENT_METHOD));
    }
}