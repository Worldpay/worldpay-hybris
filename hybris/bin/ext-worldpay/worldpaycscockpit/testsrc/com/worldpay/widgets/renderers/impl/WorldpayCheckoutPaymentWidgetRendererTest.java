package com.worldpay.widgets.renderers.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.widgets.controllers.WorldpayCardPaymentController;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.widgets.impl.DefaultListboxWidget;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cscockpit.exceptions.PaymentException;
import de.hybris.platform.cscockpit.exceptions.ResourceMessage;
import de.hybris.platform.cscockpit.exceptions.ValidationException;
import de.hybris.platform.cscockpit.widgets.controllers.CheckoutController;
import de.hybris.platform.cscockpit.widgets.models.impl.CheckoutPaymentWidgetModel;
import de.hybris.platform.servicelayer.session.SessionService;
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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.math.BigDecimal;

import static com.worldpay.widgets.renderers.impl.WorldpayCheckoutPaymentWidgetRenderer.ACCEPT_HEADER;
import static com.worldpay.widgets.renderers.impl.WorldpayCheckoutPaymentWidgetRenderer.EVENT_ON_CLOSE;
import static com.worldpay.widgets.renderers.impl.WorldpayCheckoutPaymentWidgetRenderer.USER_AGENT_HEADER;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCheckoutPaymentWidgetRendererTest {

    public static final String SELIALISED_ADDITIONAL_INFO = "selialisedAdditionalInfo";
    public static final String CVV_VALUE = "cvvValue";
    public static final String REMOTE_ADDRESS = "remoteAddress";
    public static final String ACCEPT_HEADER_VALUE = "acceptHeaderValue";
    public static final String USER_AGENT_HEADER_VALUE = "userAgentHeaderValue";
    public static final String EXCEPTION_MESSAGE = "message";
    public static final String SESSION_ID = "sessionId";
    public static final String USER_EMAIL = "userEmail";
    public static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    public static final BigDecimal AMOUNT_VALUE = BigDecimal.valueOf(10.5d);

    @Spy
    @InjectMocks
    private WorldpayCheckoutPaymentWidgetRenderer testObj = new WorldpayCheckoutPaymentWidgetRenderer();
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;

    @Mock
    private DefaultListboxWidget<CheckoutPaymentWidgetModel, CheckoutController> widgetMock;
    @Mock
    private Event eventMock;
    @Mock
    private TypedObject itemMock;
    @Mock
    private Decimalbox amountInputMock;
    @Mock
    private Textbox cvvInputMock;
    @Captor
    private ArgumentCaptor<WorldpayAdditionalInfoData> additionalInfoCaptor;
    @Mock
    private Object itemObjectMock;
    @Mock (extraInterfaces = CheckoutController.class, answer = Answers.RETURNS_DEEP_STUBS)
    private WorldpayCardPaymentController widgetControllerMock;
    @Mock
    private CheckoutPaymentWidgetModel widgetModelMock;
    @Mock
    private Execution executionMock;
    @Mock
    private Div containerMock;
    @Mock
    private Window windowMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private SessionService sessionService;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private CustomerModel customerModelMock;


    @Before
    public void setup() throws PaymentException, ValidationException {
        doReturn(SELIALISED_ADDITIONAL_INFO).when(testObj).serializeAdditionalInfo(additionalInfoCaptor.capture());
        doReturn(executionMock).when(testObj).getExecutionFromEvent(eventMock);

        when(sessionService.getCurrentSession().getSessionId()).thenReturn(SESSION_ID);
        when(cvvInputMock.getValue()).thenReturn(CVV_VALUE);
        when(amountInputMock.getValue()).thenReturn(AMOUNT_VALUE);
        when(itemMock.getObject()).thenReturn(itemObjectMock);
        when(widgetMock.getWidgetController()).thenReturn((CheckoutController) widgetControllerMock);
        when(widgetMock.getWidgetModel()).thenReturn(widgetModelMock);
        when(((CheckoutController) widgetControllerMock).getBasketController().getCart().getObject()).thenReturn(cartModelMock);
        when(cartModelMock.getUser()).thenReturn(customerModelMock);
        when(customerModelMock.getContactEmail()).thenReturn(USER_EMAIL);
        when(((CheckoutController) widgetControllerMock).processPayment(itemMock, AMOUNT_VALUE, SELIALISED_ADDITIONAL_INFO)).thenReturn(true);
        when(executionMock.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);
        when(executionMock.getHeader(ACCEPT_HEADER)).thenReturn(ACCEPT_HEADER_VALUE);
        when(executionMock.getHeader(USER_AGENT_HEADER)).thenReturn(USER_AGENT_HEADER_VALUE);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(customerModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void testHandlePayUsingStoredCardEvent() throws Exception {
        testObj.handlePayUsingStoredCardEvent(widgetMock, eventMock, itemMock, amountInputMock, cvvInputMock);

        verify(widgetControllerMock).dispatchEvent(null, widgetMock, null);
        verify(widgetModelMock).notifyListeners();
        verify(testObj, never()).handleValidationException(anyObject(), anyObject(), anyObject());
        verify(testObj, never()).handlePaymentException(anyObject(), anyObject());

        verifyAdditionalInfoData(additionalInfoCaptor.getValue());
    }

    @Test
    public void testHandlePayUsingStoredCardEventHandlesPaymentException() throws Exception {
        final PaymentException paymentException = new PaymentException(EXCEPTION_MESSAGE);
        doNothing().when(testObj).handlePaymentException(widgetMock, paymentException);
        when(((CheckoutController) widgetControllerMock).processPayment(itemMock, AMOUNT_VALUE, SELIALISED_ADDITIONAL_INFO)).thenThrow(paymentException);

        testObj.handlePayUsingStoredCardEvent(widgetMock, eventMock, itemMock, amountInputMock, cvvInputMock);

        verify(testObj).handlePaymentException(widgetMock, paymentException);
        verify(testObj, never()).handleValidationException(anyObject(), anyObject(), anyObject());
        verify(widgetModelMock, never()).notifyListeners();

        verifyAdditionalInfoData(additionalInfoCaptor.getValue());
    }

    @Test
    public void testHandlePayUsingStoredCardEventHandlesValidationException() throws Exception {
        final ValidationException validationException = new ValidationException(singletonList(new ResourceMessage(EXCEPTION_MESSAGE)));
        doNothing().when(testObj).handleValidationException(widgetMock, AMOUNT_VALUE, validationException);
        when(((CheckoutController) widgetControllerMock).processPayment(itemMock, AMOUNT_VALUE, SELIALISED_ADDITIONAL_INFO)).thenThrow(validationException);

        testObj.handlePayUsingStoredCardEvent(widgetMock, eventMock, itemMock, amountInputMock, cvvInputMock);

        verify(testObj).handleValidationException(widgetMock, AMOUNT_VALUE, validationException);
        verify(testObj, never()).handlePaymentException(anyObject(), anyObject());
        verify(widgetModelMock, never()).notifyListeners();

        verifyAdditionalInfoData(additionalInfoCaptor.getValue());
    }

    @Test
    public void shouldHandlePayUsingStoredCardEventReturnsTrue() {
        assertTrue(testObj.shouldHandlePayUsingStoredCardEvent(itemMock, CVV_VALUE, AMOUNT_VALUE));
    }

    @Test
    public void shouldHandlePayUsingStoredCardEventReturnsFalseWhenAmountIsNull() {
        assertFalse(testObj.shouldHandlePayUsingStoredCardEvent(itemMock, CVV_VALUE, null));
    }

    @Test
    public void shouldHandlePayUsingStoredCardEventReturnsFalseWhenAmountIsZero() {
        assertFalse(testObj.shouldHandlePayUsingStoredCardEvent(itemMock, CVV_VALUE, ZERO));
    }

    @Test
    public void shouldHandlePayUsingStoredCardEventReturnsFalseWhenCvvIsBlank() {
        assertFalse(testObj.shouldHandlePayUsingStoredCardEvent(itemMock, EMPTY, AMOUNT_VALUE));
    }

    @Test
    public void shouldHandlePayUsingStoredCardEventReturnsFalseWhenItemIsNull() {
        when(itemMock.getObject()).thenReturn(null);

        assertFalse(testObj.shouldHandlePayUsingStoredCardEvent(itemMock, CVV_VALUE, AMOUNT_VALUE));
    }

    @Test
    public void successfulAuthorizationReturnsTrue() throws WorldpayException {
        assertTrue(testObj.successfulAuthorization(widgetMock));

        verify(widgetControllerMock).redirectAuthorise();
    }

    @Test
    public void successfulAuthorizationReturnsFalseWhenExceptionOccurs() throws WorldpayException {
        final WorldpayException worldpayException = new WorldpayException(EXCEPTION_MESSAGE);
        doThrow(worldpayException).when(widgetControllerMock).redirectAuthorise();
        doNothing().when(testObj).showFailedToAuthorizeMessage(widgetMock, worldpayException);

        assertFalse(testObj.successfulAuthorization(widgetMock));

        verify(testObj).showFailedToAuthorizeMessage(widgetMock, worldpayException);
    }

    @Test
    public void handleOpenNewPaymentOptionClickEvent() throws ValidationException {
        doReturn(windowMock).when(testObj).createPopupWidget(widgetMock, containerMock);

        testObj.handleOpenNewPaymentOptionClickEvent(widgetMock, eventMock, containerMock);

        verify((CheckoutController) widgetControllerMock).canCreatePayments();
        verify(windowMock).addEventListener(eq(EVENT_ON_CLOSE), anyObject());
        verify(testObj, never()).handleValidationException(anyObject(), anyObject());
    }

    @Test
    public void handleOpenNewPaymentOptionClickEventHandlesValidationException() throws ValidationException {
        final ValidationException validationException = new ValidationException(singletonList(new ResourceMessage(EXCEPTION_MESSAGE)));
        doThrow(validationException).when(((CheckoutController) widgetControllerMock)).canCreatePayments();
        doReturn(windowMock).when(testObj).createPopupWidget(widgetMock, containerMock);
        doNothing().when(testObj).handleValidationException(anyObject(), anyObject());

        testObj.handleOpenNewPaymentOptionClickEvent(widgetMock, eventMock, containerMock);

        verify(testObj).handleValidationException(anyObject(), anyObject());
        verify((CheckoutController) widgetControllerMock).canCreatePayments();
        verify(windowMock, never()).addEventListener(eq(EVENT_ON_CLOSE), anyObject());
    }

    private void verifyAdditionalInfoData(WorldpayAdditionalInfoData additionalInfoData) {
        assertEquals(CVV_VALUE, additionalInfoData.getSecurityCode());
        assertTrue(additionalInfoData.isSavedCardPayment());
        assertEquals(SESSION_ID, additionalInfoData.getSessionId());
        assertEquals(USER_EMAIL, additionalInfoData.getCustomerEmail());
        assertEquals(REMOTE_ADDRESS, additionalInfoData.getCustomerIPAddress());
        assertEquals(ACCEPT_HEADER_VALUE, additionalInfoData.getAcceptHeader());
        assertEquals(USER_AGENT_HEADER_VALUE, additionalInfoData.getUserAgentHeader());
        assertEquals(AUTHENTICATED_SHOPPER_ID, additionalInfoData.getAuthenticatedShopperId());
    }
}