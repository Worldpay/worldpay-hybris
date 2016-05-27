package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static com.worldpay.service.model.AuthorisedStatus.REFUSED;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultOrderNotificationServiceTest {

    private static final String ORDER_CODE = "orderCode";

    @InjectMocks
    private DefaultOrderNotificationService testObj = new DefaultOrderNotificationService();

    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private OrderNotificationProcessorStrategy orderNotificationProcessorStrategyMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;

    @Before
    public void setup() {
        final Map<AuthorisedStatus, OrderNotificationProcessorStrategy> journalTypeToNotificationProcessorMap = new HashMap<>();
        journalTypeToNotificationProcessorMap.put(AUTHORISED, orderNotificationProcessorStrategyMock);
        testObj.setJournalTypeToNotificationProcessorStrategyMap(journalTypeToNotificationProcessorMap);
    }

    @Test
    public void processOrderNotificationMessageDoesNothingIfNoProcessorFound() {
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(REFUSED);

        testObj.processOrderNotificationMessage(orderNotificationMessageMock);

        verify(worldpayPaymentTransactionDaoMock, never()).findPaymentTransactionByRequestIdFromOrdersOnly(anyString());
        verify(orderNotificationProcessorStrategyMock, never()).processNotificationMessage(anyObject(), anyObject());
    }

    @Test
    public void processOrderNotificationMessageInvokesProcessorForJournalType() {
        when(orderNotificationMessageMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(orderNotificationMessageMock.getJournalReply().getJournalType()).thenReturn(AUTHORISED);
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestIdFromOrdersOnly(ORDER_CODE)).thenReturn(paymentTransactionModelMock);

        testObj.processOrderNotificationMessage(orderNotificationMessageMock);

        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestIdFromOrdersOnly(ORDER_CODE);
        verify(orderNotificationProcessorStrategyMock).processNotificationMessage(paymentTransactionModelMock, orderNotificationMessageMock);
    }
}
