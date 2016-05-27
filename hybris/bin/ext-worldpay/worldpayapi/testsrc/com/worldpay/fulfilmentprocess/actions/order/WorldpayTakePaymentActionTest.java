package com.worldpay.fulfilmentprocess.actions.order;


import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static de.hybris.platform.core.enums.OrderStatus.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayTakePaymentActionTest {

    private static final String WAIT = "WAIT";
    private static final String NOK = "NOK";
    private static final String OK = "OK";

    @InjectMocks
    private WorldpayTakePaymentAction testObj = new WorldpayTakePaymentAction();

    @Mock
    private OrderProcessModel processMock;
    @Mock
    private OrderModel orderMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock2;
    @Mock
    private PaymentInfoModel paymentInfoMock;

    @Before
    public void setup() {
        when(processMock.getOrder()).thenReturn(orderMock);
        when(orderMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionMock));
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));
        when(paymentTransactionMock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(false);
    }

    @Test
    public void existingPendingCaptureEntryReturnsWAIT() throws Exception {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderMock, CAPTURE)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, CAPTURE)).thenReturn(true);

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
    }

    @Test
    public void captureNotAcceptedReturnsNOK() throws Exception {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderMock, CAPTURE)).thenReturn(false);

        final String result = testObj.execute(processMock);

        assertEquals(NOK, result);
        verify(orderMock).setStatus(PAYMENT_NOT_CAPTURED);
        verify(modelServiceMock).save(orderMock);
    }

    @Test
    public void captureNotPendingAndAcceptedReturnsOK() throws Exception {
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderMock, CAPTURE)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, CAPTURE)).thenReturn(false);
        when(paymentServiceMock.capture(paymentTransactionMock)).thenReturn(paymentTransactionEntryModelMock);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final String result = testObj.execute(processMock);

        assertEquals(OK, result);
        verify(orderMock).setStatus(PAYMENT_CAPTURED);
        verify(modelServiceMock).save(orderMock);
    }

    @Test
    public void noCaptureReturnsWAITAndUpdatesOrderStatus() throws Exception {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).thenReturn(Collections.emptyList());
        when(paymentServiceMock.capture(paymentTransactionMock)).thenReturn(paymentTransactionEntryModelMock);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
        verify(paymentServiceMock).capture(paymentTransactionMock);
    }

    @Test
    public void whenOverOneTransactionEntryThenReturnNOK() throws Exception {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).
                thenReturn(Arrays.asList(paymentTransactionEntryModelMock, paymentTransactionEntryModelMock2));
        when(paymentServiceMock.capture(paymentTransactionMock)).thenReturn(paymentTransactionEntryModelMock);
        when(paymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());

        final String result = testObj.execute(processMock);

        assertEquals(NOK, result);
        verify(orderMock).setStatus(PAYMENT_NOT_CAPTURED);
        verify(modelServiceMock).save(orderMock);
    }

    @Test
    public void executeShouldNotCaptureAndShouldWaitWhenNoCaptureEntriesAndPaymentInfoIsAPM() {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionMock, CAPTURE)).thenReturn(Collections.emptyList());
        when(paymentTransactionMock.getInfo()).thenReturn(paymentInfoMock);
        when(paymentInfoMock.getIsApm()).thenReturn(true);

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
        verify(paymentServiceMock, never()).capture(anyObject());
    }
}