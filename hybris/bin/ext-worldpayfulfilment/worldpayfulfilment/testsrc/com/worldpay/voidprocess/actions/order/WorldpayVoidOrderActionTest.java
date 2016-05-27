package com.worldpay.voidprocess.actions.order;

import com.worldpay.voidprocess.model.WorldpayVoidProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.voidprocess.actions.order.WorldpayVoidOrderAction.Transition.NOK;
import static com.worldpay.voidprocess.actions.order.WorldpayVoidOrderAction.Transition.OK;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayVoidOrderActionTest {

    @InjectMocks
    private WorldpayVoidOrderAction testObj = new WorldpayVoidOrderAction();
    @Mock
    private WorldpayVoidProcessModel worldpayVoidProcessModelMock;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;

    @Test
    public void testExecuteShouldSendVoidCommand() throws Exception {
        when(worldpayVoidProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionMock));
        when(paymentTransactionMock.getEntries()).thenReturn(singletonList(paymentTransactionEntryMock));
        when(paymentTransactionEntryMock.getType()).thenReturn(AUTHORIZATION);

        final String result = testObj.execute(worldpayVoidProcessModelMock);

        verify(paymentServiceMock).cancel(paymentTransactionEntryMock);
        assertEquals(OK.toString(), result);
    }

    @Test
    public void testExecuteShouldNotSendVoidCommandIfAnyEntryIsNotAuthOrCapture() throws Exception {
        when(worldpayVoidProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionMock));
        when(paymentTransactionMock.getEntries()).thenReturn(asList(paymentTransactionEntryMock, paymentTransactionEntryMock));
        when(paymentTransactionEntryMock.getType()).thenReturn(AUTHORIZATION).thenReturn(REFUND_FOLLOW_ON);

        final String result = testObj.execute(worldpayVoidProcessModelMock);

        verify(paymentServiceMock, never()).cancel(paymentTransactionEntryMock);
        assertEquals(NOK.toString(), result);
    }

    @Test
    public void testExecuteShouldSendVoidCommandIfAllEntriesAreAuthOrCapture() throws Exception {
        when(worldpayVoidProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionMock));
        when(paymentTransactionMock.getEntries()).thenReturn(asList(paymentTransactionEntryMock, paymentTransactionEntryMock));
        when(paymentTransactionEntryMock.getType()).thenReturn(AUTHORIZATION).thenReturn(CAPTURE);

        final String result = testObj.execute(worldpayVoidProcessModelMock);

        verify(paymentServiceMock).cancel(paymentTransactionEntryMock);
        assertEquals(OK.toString(), result);
    }

    @Test
    public void testGetTransitions() throws Exception {

    }
}