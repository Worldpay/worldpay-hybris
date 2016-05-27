package com.worldpay.strategies.impl;

import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.model.ReturnProcessModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static de.hybris.platform.basecommerce.enums.ReturnStatus.CANCELED;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSAL_PENDING;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayomsOrderModificationRefundProcessStrategyTest {

    private static final String REFUND_REFERENCE = "refundReference";
    private static final String RETURN_PROCESS_CODE = "returnProcessCode";


    @InjectMocks
    private DefaultWorldpayomsOrderModificationRefundProcessStrategy testObj = new DefaultWorldpayomsOrderModificationRefundProcessStrategy();

    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ReturnRequestModel returnRequestMock1, returnRequestMock2;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock;
    @Mock
    private ReturnProcessModel refundWaitingReturnProcessMock1;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private ReturnProcessModel refundWaitingReturnProcessMock2;
    @Mock
    private ModelService modelServiceMock;

    @Before
    public void setUp() {
        when(returnRequestMock1.getStatus()).thenReturn(PAYMENT_REVERSED);
        when(returnRequestMock2.getStatus()).thenReturn(PAYMENT_REVERSED);
        when(orderNotificationMessageMock.getPaymentReply().getRefundReference()).thenReturn(REFUND_REFERENCE);
        when(paymentTransactionEntryMock.getCode()).thenReturn(REFUND_REFERENCE);
        when(returnRequestMock1.getPaymentTransactionEntry()).thenReturn(paymentTransactionEntryMock);
        when(returnRequestMock2.getPaymentTransactionEntry()).thenReturn(paymentTransactionEntryMock);
        when(returnRequestMock1.getReturnProcess()).thenReturn(singletonList(refundWaitingReturnProcessMock1));
        when(refundWaitingReturnProcessMock1.getCode()).thenReturn(RETURN_PROCESS_CODE);
    }

    @Test
    public void shouldTriggerReturnProcessWhenNotificationIsREFUND() {
        when(orderModelMock.getReturnRequests()).thenReturn(singletonList(returnRequestMock1));

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(businessProcessServiceMock).triggerEvent(RETURN_PROCESS_CODE + "_" + REFUND_FOLLOW_ON.toString());
    }

    @Test
    public void shouldMarkReturnRequestAsRefunded() {
        when(orderModelMock.getReturnRequests()).thenReturn(singletonList(returnRequestMock1));

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(returnRequestMock1).setStatus(ReturnStatus.PAYMENT_REVERSED);
        verify(modelServiceMock).save(returnRequestMock1);
    }

    @Test
    public void shouldTriggerReturnProcessWhenNotificationIsREFUNDAndThereAreMultipleReturnRequest() {
        when(orderModelMock.getReturnRequests()).thenReturn(Arrays.asList(returnRequestMock1, returnRequestMock2));
        when(returnRequestMock2.getReturnProcess()).thenReturn(singletonList(refundWaitingReturnProcessMock2));
        when(refundWaitingReturnProcessMock2.getCode()).thenReturn(RETURN_PROCESS_CODE + "_INVALID");
        when(orderNotificationMessageMock.getPaymentReply().getRefundReference()).thenReturn(REFUND_REFERENCE);

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(businessProcessServiceMock).triggerEvent(RETURN_PROCESS_CODE + "_" + REFUND_FOLLOW_ON.toString());
    }

    @Test
    public void shouldNotTriggerReturnProcessWhenRefundReferenceIsNotEqualToTransactionEntryCode() {
        when(orderModelMock.getReturnRequests()).thenReturn(singletonList(returnRequestMock1));
        when(paymentTransactionEntryMock.getCode()).thenReturn(REFUND_REFERENCE + "_INVALID");
        when(orderNotificationMessageMock.getPaymentReply().getRefundReference()).thenReturn(REFUND_REFERENCE);

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verifyZeroInteractions(businessProcessServiceMock);
    }

    @Test
    public void shouldNotTriggerReturnProcessWhenNoReturnRequestsOnOrder() {
        when(orderModelMock.getReturnRequests()).thenReturn(null);

        testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verifyZeroInteractions(businessProcessServiceMock);
    }

    @Test
    public void shouldTriggerCorrectProcessForCancelledReturnRequest() {
        when(orderModelMock.getReturnRequests()).thenReturn(singletonList(returnRequestMock1));
        when(returnRequestMock1.getStatus()).thenReturn(CANCELED);

        final boolean result = testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verifyZeroInteractions(businessProcessServiceMock);
        assertFalse(result);
    }

    @Test
    public void shouldTriggerCorrectProcessWhenOneIsCancelled() {
        when(orderModelMock.getReturnRequests()).thenReturn(Arrays.asList(returnRequestMock1, returnRequestMock2));
        when(returnRequestMock1.getStatus()).thenReturn(CANCELED);
        when(returnRequestMock2.getStatus()).thenReturn(PAYMENT_REVERSAL_PENDING);
        when(returnRequestMock2.getReturnProcess()).thenReturn(singletonList(refundWaitingReturnProcessMock2));
        when(refundWaitingReturnProcessMock2.getCode()).thenReturn(RETURN_PROCESS_CODE);

        final boolean result = testObj.processRefundFollowOn(orderModelMock, orderNotificationMessageMock);

        verify(businessProcessServiceMock).triggerEvent(RETURN_PROCESS_CODE + "_" + REFUND_FOLLOW_ON.toString());
        verifyNoMoreInteractions(businessProcessServiceMock);
        assertTrue(result);
    }
}