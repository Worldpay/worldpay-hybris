package com.worldpay.cronjob;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Collections;

import static de.hybris.platform.cronjob.enums.CronJobResult.SUCCESS;
import static de.hybris.platform.cronjob.enums.CronJobStatus.FINISHED;
import static de.hybris.platform.payment.dto.TransactionStatus.REVIEW;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class APMOrderTimeoutJobPerformableTest {

    public static final String ORDER_PROCESS_CODE = "orderProcessCode";

    @InjectMocks
    @Spy
    private APMOrderTimeoutJobPerformable testObj = new APMOrderTimeoutJobPerformable();

    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CronJobModel cronJobModelMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private OrderProcessModel orderProcessModelMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private TransactionStatus transactionStatusMock;
    @Mock
    private ModelService modelServiceMock;

    private TransactionOperations transactionOperationsMock = new TransactionOperations() {
        @Override
        public <T> T execute(final TransactionCallback<T> transactionCallback) throws TransactionException {
            return transactionCallback.doInTransaction(transactionStatusMock);
        }
    };

    @Before
    public void setUp() throws Exception {
        testObj.setTransactionTemplate(transactionOperationsMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(worldpayPaymentTransactionDaoMock.findCancellablePendingAPMPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));
        when(orderModelMock.getOrderProcess()).thenReturn(Collections.singletonList(orderProcessModelMock));
        when(orderProcessModelMock.getCode()).thenReturn(ORDER_PROCESS_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(paymentTransactionEntryModelMock));
    }

    @Test
    public void testPerformShouldReturnSuccess() throws Exception {
        final PerformResult result = testObj.perform(cronJobModelMock);

        assertEquals(SUCCESS, result.getResult());
        assertEquals(FINISHED, result.getStatus());

        verify(worldpayPaymentTransactionServiceMock).updateEntriesStatus(Collections.singletonList(paymentTransactionEntryModelMock), REVIEW.name());
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(worldpayPaymentTransactionDaoMock).findCancellablePendingAPMPaymentTransactions();
        verify(businessProcessServiceMock).triggerEvent(ORDER_PROCESS_CODE + "_" + PaymentTransactionType.AUTHORIZATION);
    }
}