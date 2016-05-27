package com.worldpay.worldpayoms.notification.processors.impl;

import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultRefundedOrderNotificationProcessorStrategyTest {

    public static final String REFUND_REFERENCE = "refundReference";
    @InjectMocks
    private DefaultRefundedOrderNotificationProcessorStrategy testObj = new DefaultRefundedOrderNotificationProcessorStrategy();

    @Mock
    private ModelService modelServiceMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;

    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Before
    public void setUp() {
        testObj.setTransactionTemplate(transactionOperationsMock);

        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
    }

    @Test
    public void shouldMarkPaymentTransactionEntriesAsNonPendingWhenTheRefundReferenceMatches() throws WorldpayModelTransformationException {
        when(orderNotificationMessageMock.getPaymentReply().getRefundReference()).thenReturn(REFUND_REFERENCE);
        when(paymentTransactionEntryModelMock.getCode()).thenReturn(REFUND_REFERENCE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(singletonList(paymentTransactionEntryModelMock));

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(paymentTransactionEntryModelMock).setPending(Boolean.FALSE);
        verify(modelServiceMock).save(paymentTransactionEntryModelMock);
    }

    @Test
    public void shouldNotMarkPaymentTransactionEntriesAsNonPendingWhenTheRefundReferenceDoesNotMatches() throws WorldpayModelTransformationException {
        when(orderNotificationMessageMock.getPaymentReply().getRefundReference()).thenReturn(REFUND_REFERENCE);
        when(paymentTransactionEntryModelMock.getCode()).thenReturn(REFUND_REFERENCE + "_INVALID");
        when(paymentTransactionModelMock.getEntries()).thenReturn(singletonList(paymentTransactionEntryModelMock));

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(paymentTransactionEntryModelMock, never()).setPending(Boolean.FALSE);
        verify(modelServiceMock, never()).save(paymentTransactionEntryModelMock);
    }
}