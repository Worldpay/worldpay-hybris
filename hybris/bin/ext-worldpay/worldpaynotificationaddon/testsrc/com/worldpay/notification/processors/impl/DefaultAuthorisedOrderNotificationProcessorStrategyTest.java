package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayModelTransformationException;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultAuthorisedOrderNotificationProcessorStrategyTest {

    public static final String CURRENCY_CODE = "GBP";
    @InjectMocks
    private DefaultAuthorisedOrderNotificationProcessorStrategy testObj = new DefaultAuthorisedOrderNotificationProcessorStrategy();

    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
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
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private Amount amountMock;

    private List<PaymentTransactionEntryModel> pendingAuthorizationTransactionEntries;
    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Before
    public void setUp() {
        testObj.setTransactionTemplate(transactionOperationsMock);
        pendingAuthorizationTransactionEntries = Arrays.asList(paymentTransactionEntryModelMock, paymentTransactionEntryModelMock);
        when(worldpayPaymentTransactionServiceMock.getPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(pendingAuthorizationTransactionEntries);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(orderNotificationMessageMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getAmount()).thenReturn(amountMock);
        when(amountMock.getCurrencyCode()).thenReturn(CURRENCY_CODE);
        when(amountMock.getValue()).thenReturn("1000");
    }

    @Test
    public void shouldProcessAuthorisedNotificationAndSetRiskScore() throws WorldpayModelTransformationException {
        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(pendingAuthorizationTransactionEntries, ACCEPTED.name());
        verify(worldpayPaymentTransactionServiceMock).updateEntriesAmount(pendingAuthorizationTransactionEntries, paymentReplyMock.getAmount());
        verify(paymentTransactionModelMock).setPlannedAmount(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_CEILING));
        verify(worldpayPaymentInfoServiceMock).setPaymentInfoModel(paymentTransactionModelMock, orderModelMock, orderNotificationMessageMock);
        verify(paymentTransactionModelMock).setApmOpen(false);
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionServiceMock).addRiskScore(paymentTransactionModelMock, paymentReplyMock);
    }

    @Test
    public void shouldSetAAvFieldsOnTransactionEntry() {

        testObj.processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);

        verify(worldpayPaymentTransactionServiceMock, times(2)).addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);
    }
}