package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;
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

import java.util.Collections;

import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultRefusedOrderNotificationProcessorStrategyTest {

    public static final String ORDER_CODE = "orderCode";

    @InjectMocks
    private DefaultRefusedOrderNotificationProcessorStrategy testObj = new DefaultRefusedOrderNotificationProcessorStrategy();

    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private com.worldpay.service.model.PaymentReply paymentReplyMock;
    @Mock
    private com.worldpay.service.model.Amount amountMock;

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
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));
        when(orderNotificationMessageMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
    }

    @Test
    public void shouldProcessRefusedNotification() throws OrderCancelException {
        when(orderModelMock.getStatus()).thenReturn(OrderStatus.PAYMENT_PENDING);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(paymentTransactionModelMock).getOrder();
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(Collections.singletonList(paymentTransactionEntryModelMock), REJECTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(Collections.singletonList(paymentTransactionEntryModelMock), amountMock);
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);
    }

    @Test
    public void shouldProcessRefusedNotificationAndShouldNOTTriggerAnyEventIfOrderStatusIsNOTPaymentPending() throws OrderCancelException {
        when(orderModelMock.getStatus()).thenReturn(OrderStatus.CREATED);

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(paymentTransactionModelMock).getOrder();
        verify(worldpayPaymentInfoServiceMock, never()).setPaymentInfoModel(any(PaymentTransactionModel.class), any(AbstractOrderModel.class), any(OrderNotificationMessage.class));
        verify(modelServiceMock, never()).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionServiceMock, never()).updateEntriesStatus(anyListOf(PaymentTransactionEntryModel.class), anyString());
    }
}