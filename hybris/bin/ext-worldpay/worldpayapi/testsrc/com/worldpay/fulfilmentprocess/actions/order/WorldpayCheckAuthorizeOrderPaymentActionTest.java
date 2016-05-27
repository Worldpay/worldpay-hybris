package com.worldpay.fulfilmentprocess.actions.order;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_AUTHORIZED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_NOT_AUTHORIZED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_PENDING;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WorldpayCheckAuthorizeOrderPaymentActionTest {

    public static final String NOK = "NOK";
    public static final String OK = "OK";
    public static final String WAIT = "WAIT";

    @InjectMocks
    private WorldpayCheckAuthorizeOrderPaymentAction testObj = new WorldpayCheckAuthorizeOrderPaymentAction();

    @Mock
    private OrderProcessModel processMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private InvoicePaymentInfoModel invoicePaymentInfoMock;
    @Mock
    private PaymentTransactionModel paymentTransactionMock;

    @Before
    public void setup() throws Exception {
        when(processMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionMock));
    }

    @Test
    public void executeShouldReturnOKIfOrderPaymentInfoIsInvoice() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(invoicePaymentInfoMock);

        final String result = testObj.execute(processMock);

        assertEquals(OK, result);
        verify(orderModelMock, never()).setStatus(PAYMENT_PENDING);
    }

    @Test
    public void executeShouldReturnNOKIfNotAllTransactionEntriesAuthorised() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(false);

        final String result = testObj.execute(processMock);

        assertEquals(NOK, result);

        verify(orderModelMock).setStatus(PAYMENT_NOT_AUTHORIZED);
    }

    @Test
    public void executeShouldReturnWAITIfAllTransactionEntriesAcceptedButPending() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, AUTHORIZATION)).thenReturn(true);

        final String result = testObj.execute(processMock);

        assertEquals(WAIT, result);
        verify(orderModelMock).setStatus(PAYMENT_PENDING);
    }

    @Test
    public void executeShouldReturnOKIfAllTransactionEntriesAcceptedAndNotPending() throws Exception {
        when(orderModelMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(worldpayPaymentTransactionServiceMock.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION)).thenReturn(true);
        when(worldpayPaymentTransactionServiceMock.isPaymentTransactionPending(paymentTransactionMock, AUTHORIZATION)).thenReturn(false);

        final String result = testObj.execute(processMock);

        assertEquals(OK, result);
        verify(modelServiceMock).save(orderModelMock);
        verify(orderModelMock).setStatus(PAYMENT_AUTHORIZED);
    }
}