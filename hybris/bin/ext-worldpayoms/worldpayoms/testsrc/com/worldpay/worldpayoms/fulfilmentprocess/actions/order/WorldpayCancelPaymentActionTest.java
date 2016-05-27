package com.worldpay.worldpayoms.fulfilmentprocess.actions.order;

import static com.worldpay.worldpayoms.fulfilmentprocess.actions.order.WorldpayCancelPaymentAction.Transition.NOK;
import static com.worldpay.worldpayoms.fulfilmentprocess.actions.order.WorldpayCancelPaymentAction.Transition.OK;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCancelPaymentActionTest {

    @InjectMocks
    private WorldpayCancelPaymentAction testObj = new WorldpayCancelPaymentAction();

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private OrderProcessModel orderProcessModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private PaymentTransactionEntryModel cancellationPaymentTransactionEntryModelMock;

    @Before
    public void setUp() {
        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));
        when(paymentServiceMock.cancel(paymentTransactionEntryModelMock)).thenReturn(cancellationPaymentTransactionEntryModelMock);
    }

    @Test
    public void testExecuteShouldReturnOKWhenCancelIsSuccessful() throws Exception {
        when(cancellationPaymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
        when(cancellationPaymentTransactionEntryModelMock.getTransactionStatusDetails()).thenReturn(TransactionStatusDetails.SUCCESFULL.name());

        final String result = testObj.execute(orderProcessModelMock);

        assertEquals(OK.name(), result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenCancelIsNotSuccessful() throws Exception {
        when(cancellationPaymentTransactionEntryModelMock.getTransactionStatus()).thenReturn(TransactionStatus.ERROR.name());
        when(cancellationPaymentTransactionEntryModelMock.getTransactionStatusDetails()).thenReturn(TransactionStatusDetails.COMMUNICATION_PROBLEM.name());

        final String result = testObj.execute(orderProcessModelMock);

        assertEquals(NOK.name(), result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenOrderHasNoTransactions() throws Exception {
        when(orderModelMock.getPaymentTransactions()).thenReturn(emptyList());

        final String result = testObj.execute(orderProcessModelMock);

        assertEquals(NOK.name(), result);
    }


    @Test
    public void testExecuteShouldReturnNOKWhenOrderNullTransaction() throws Exception {
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(null));

        final String result = testObj.execute(orderProcessModelMock);

        assertEquals(NOK.name(), result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenOrderHasNoTransactionEntries() throws Exception {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(emptyList());

        final String result = testObj.execute(orderProcessModelMock);

        assertEquals(NOK.name(), result);
    }

    @Test
    public void testExecuteShouldReturnNOKWhenOrderHasNullTransactionEntry() throws Exception {
        when(worldpayPaymentTransactionServiceMock.filterPaymentTransactionEntriesOfType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(singletonList(null));

        final String result = testObj.execute(orderProcessModelMock);

        assertEquals(NOK.name(), result);
    }
}