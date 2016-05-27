package com.worldpay.worldpayoms.actions.order.cancel;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.worldpay.worldpayoms.actions.order.cancel.WorldpayCancelOrderAction.SOCKET_OUT_CONTEXT;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCancelOrderActionTest {

    @Spy
    @InjectMocks
    private WorldpayCancelOrderAction testObj = new WorldpayCancelOrderAction();

    @Mock
    private ActionContext<OrderModel> actionContextMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private OrderEntryModel orderEntryModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModelMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private OrderCancelService orderCancelServiceMock;
    @Mock
    private UserModel userModelMock;
    @Mock
    private CancelDecision cancelDecisionMock;

    @Before
    public void setup() {
        when(userServiceMock.getCurrentUser()).thenReturn(userModelMock);
        when(orderCancelServiceMock.isCancelPossible(orderModelMock, userModelMock, true, true)).thenReturn(cancelDecisionMock);
        when(cancelDecisionMock.isAllowed()).thenReturn(true);
        when(actionContextMock.getData()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));
        when(orderModelMock.getEntries()).thenReturn(Collections.singletonList(orderEntryModelMock));
        doNothing().when(testObj).sendOutput(anyString(), any());
    }

    @Test
    public void buttonShouldBeClickable() {
        final boolean result = testObj.canPerform(actionContextMock);

        assertTrue(result);
    }

    @Test
    public void buttonShouldNotBeClickableIfOrderEntriesIsNull() {
        when(orderModelMock.getEntries()).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void buttonShouldNotBeClickableIfOrderWasPaidWithAnAPM() {
        when(paymentTransactionModelMock.getInfo()).thenReturn(worldpayAPMPaymentInfoModelMock);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void buttonShouldBeClickableIfOrderWasPaidWithACreditCard() {
        when(paymentTransactionModelMock.getInfo()).thenReturn(creditCardPaymentInfoModelMock);

        final boolean result = testObj.canPerform(actionContextMock);

        assertTrue(result);
    }

    @Test
    public void buttonShouldNotBeClickableIfCancelIsNotPossible() {
        when(cancelDecisionMock.isAllowed()).thenReturn(false);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void buttonShouldNotBeClickableIfAuthIsPending() throws Exception {
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(true);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void buttonShouldBeClickableIfAuthIsNotPending() throws Exception {
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(false);

        final boolean result = testObj.canPerform(actionContextMock);

        assertTrue(result);
    }

    @Test
    public void buttonShouldNotBeClickableIfOrderIsNull() throws Exception {
        when(actionContextMock.getData()).thenReturn(null);

        final boolean result = testObj.canPerform(actionContextMock);

        assertFalse(result);
    }

    @Test
    public void testGetConfirmationMessage() throws Exception {
        assertNull(testObj.getConfirmationMessage(actionContextMock));
    }

    @Test
    public void testNeedsConfirmation() throws Exception {
        assertFalse(testObj.needsConfirmation(actionContextMock));
    }

    @Test
    public void testPerform() throws Exception {
        final ActionResult<OrderModel> result = testObj.perform(actionContextMock);

        verify(testObj).sendOutput(SOCKET_OUT_CONTEXT, orderModelMock);
        assertEquals(ActionResult.SUCCESS, result.getResultCode());
    }
}