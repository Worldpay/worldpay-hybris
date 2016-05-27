package com.worldpay.cronjob;

import com.worldpay.strategies.WorldpayOrderModificationProcessStrategy;
import com.worldpay.worldpaynotificationaddon.model.OrderModificationCronJobModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderModificationProcessorJobPerformableTest {
    
    @InjectMocks
    private OrderModificationProcessorJobPerformable testObj = new OrderModificationProcessorJobPerformable();
    
    @Mock
    private OrderModificationCronJobModel cronJobModelMock;
    @Mock
    private WorldpayOrderModificationProcessStrategy worldpayOrderModificationProcessStrategyMock;
    
    private Set<PaymentTransactionType> paymentTransactionTypes = new HashSet<>();
    
    @Before
    public void setup() {
        paymentTransactionTypes.add(AUTHORIZATION);
        paymentTransactionTypes.add(CAPTURE);
        paymentTransactionTypes.add(CANCEL);
    }

    @Test
    public void performShouldProcessAllProcessableModifications() {
        when(cronJobModelMock.getTypeOfPaymentTransactionToProcessSet()).thenReturn(paymentTransactionTypes);
        when(worldpayOrderModificationProcessStrategyMock.processOrderModificationMessages(any(PaymentTransactionType.class))).thenReturn(true);

        final PerformResult result = testObj.perform(cronJobModelMock);

        assertEquals(CronJobResult.SUCCESS, result.getResult());
        assertEquals(CronJobStatus.FINISHED, result.getStatus());

        verify(worldpayOrderModificationProcessStrategyMock).processOrderModificationMessages(AUTHORIZATION);
        verify(worldpayOrderModificationProcessStrategyMock).processOrderModificationMessages(CAPTURE);
        verify(worldpayOrderModificationProcessStrategyMock).processOrderModificationMessages(CANCEL);
    }

    @Test
    public void performShouldMarkErrorWhenFalseReturnedFromStragety() {
        when(cronJobModelMock.getTypeOfPaymentTransactionToProcessSet()).thenReturn(paymentTransactionTypes);
        when(worldpayOrderModificationProcessStrategyMock.processOrderModificationMessages(any(PaymentTransactionType.class))).thenReturn(false);

        final PerformResult result = testObj.perform(cronJobModelMock);

        assertEquals(CronJobResult.ERROR, result.getResult());
        assertEquals(CronJobStatus.FINISHED, result.getStatus());

        verify(worldpayOrderModificationProcessStrategyMock).processOrderModificationMessages(AUTHORIZATION);
        verify(worldpayOrderModificationProcessStrategyMock).processOrderModificationMessages(CAPTURE);
        verify(worldpayOrderModificationProcessStrategyMock).processOrderModificationMessages(CANCEL);
    }
    
}