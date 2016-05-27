package com.worldpay.strategies.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.worldpay.worldpaynotificationaddon.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;
import static com.worldpay.worldpaynotificationaddon.enums.DefectiveReason.NO_PAYMENT_TRANSACTION_MATCHED;
import static com.worldpay.worldpaynotificationaddon.enums.DefectiveReason.PROCESSING_ERROR;
import static de.hybris.platform.core.enums.OrderStatus.CANCELLED;
import static de.hybris.platform.core.enums.OrderStatus.FRAUD_CHECKED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CANCEL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;
import static de.hybris.platform.payment.enums.PaymentTransactionType.SETTLED;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayOrderModificationProcessStrategyTest {

    private static final int DEFECTIVE_COUNT = 2;
    private static final String ORDER_CODE = "orderCode";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String AUTHORISE_PROCESS_CODE = "authoriseProcessCode";
    private static final String SERIALIZED_JSON_STRING = "Serialized json string";
    private static final String CAPTURE_BPM_PROCESS_CODE = "captureBMPProcessCode";
    private static final String AUTHENTICATED_SHOPPER_ID = "authenticatedShopperId";
    private static final String TOKEN_REPLY_AUTHENTICATED_SHOPPER_ID = "tokenReplyAuthenticatedShopperId";

    @InjectMocks
    private DefaultWorldpayOrderModificationProcessStrategy testObj = new DefaultWorldpayOrderModificationProcessStrategy();

    @Mock
    private OrderModificationDao orderModificationDaoMock;
    @Mock
    private ProcessDefinitionDao processDefinitionDaoMock;
    @Mock
    private WorldpayOrderModificationModel orderModificationModelMock, existingOrderModificationModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private BusinessProcessModel captureWaitingProcessMock, authoriseWaitingProcessMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private OrderNotificationService orderNotificationServiceMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private OrderModificationSerialiser orderModificationSerialiserMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private Set<OrderStatus> nonTriggeringOrderStatusesMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryMock;
    @Mock
    private TokenReply tokenReplyMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategyMock;
    @Mock
    private WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategyMock;

    @Before
    public void setup() {
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(nonTriggeringOrderStatusesMock.contains(CANCELLED)).thenReturn(true);
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(CAPTURE)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(CANCEL)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(AUTHORIZATION)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(REFUND_FOLLOW_ON)).thenReturn(singletonList(orderModificationModelMock));
        when(orderModificationDaoMock.findUnprocessedOrderModificationsByType(SETTLED)).thenReturn(singletonList(orderModificationModelMock));
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CAPTURE)).thenReturn(singletonList(captureWaitingProcessMock));
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, AUTHORIZATION)).thenReturn(singletonList(authoriseWaitingProcessMock));
        when(orderModificationModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModificationModelMock.getOrderNotificationMessage()).thenReturn(SERIALIZED_JSON_STRING);
        when(captureWaitingProcessMock.getCode()).thenReturn(CAPTURE_BPM_PROCESS_CODE);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(orderModificationSerialiserMock.deserialise(SERIALIZED_JSON_STRING)).thenReturn(orderNotificationMessageMock);
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
        when(orderModelMock.getUser()).thenReturn(customerModelMock);
        when(worldpayAuthenticatedShopperIdStrategyMock.getAuthenticatedShopperId(customerModelMock)).thenReturn(AUTHENTICATED_SHOPPER_ID);
        when(tokenReplyMock.getAuthenticatedShopperID()).thenReturn(TOKEN_REPLY_AUTHENTICATED_SHOPPER_ID);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);
    }

    @Test
    public void processOrderModificationMessagesShouldProcessIfWorldpayOrderCodeRelatesToOrder() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(true);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMessageMock);
        verify(businessProcessServiceMock).triggerEvent(CAPTURE_BPM_PROCESS_CODE + "_" + CAPTURE.getCode());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock).save(orderModificationModelMock);
    }

    @Test
    public void processOrderModificationMessagesShouldMarkOrderNotificationAsProcessedAndNotTriggerABusinessProcessEventWhenOrderModificationTypeIsSETTLED() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, SETTLED, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(SETTLED);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMessageMock);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock).save(orderModificationModelMock);
        assertTrue(result);
    }

    @Test
    public void processOrderModificationMessagesShouldDoNothingIfWorldpayOrderCodeRelatesToCart() {
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock, never()).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock, never()).save(any(WorldpayOrderModificationModel.class));
    }

    @Test
    public void processOrderModificationMessagesShouldDoNothingIfNotSingleBusinessProcessFound() {
        when(processDefinitionDaoMock.findWaitingOrderProcesses(ORDER_CODE, CAPTURE)).thenReturn(asList(authoriseWaitingProcessMock, captureWaitingProcessMock));
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock, never()).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock, never()).save(any(WorldpayOrderModificationModel.class));
    }

    @Test
    public void shouldMarkTheOrderModificationAsProcessedAndDefectiveWhenNoTransactionIsFound() {
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(null);
        when(orderModificationDaoMock.getExistingModifications(orderModificationModelMock)).thenReturn(singletonList(existingOrderModificationModelMock));
        when(existingOrderModificationModelMock.getDefectiveCounter()).thenReturn(DEFECTIVE_COUNT);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setDefectiveReason(NO_PAYMENT_TRANSACTION_MATCHED);
        verify(orderModificationModelMock).setDefectiveCounter(DEFECTIVE_COUNT + 1);
        verify(modelServiceMock).save(any(WorldpayOrderModificationModel.class));
        verify(processDefinitionDaoMock, never()).findWaitingOrderProcesses(anyString(), any(PaymentTransactionType.class));
    }

    @Test
    public void processOrderModificationMessageShouldProcessNotificationIfPreviousNotificationIsNotPending() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.TRUE);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        verify(businessProcessServiceMock).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock).save(any(WorldpayOrderModificationModel.class));
        verify(processDefinitionDaoMock).findWaitingOrderProcesses(anyString(), any(PaymentTransactionType.class));
        assertTrue(result);
    }

    @Test
    public void processOrderModificationMessageShouldNotProcessNotificationIfPreviousNotificationIsPending() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.FALSE);

        final boolean result = testObj.processOrderModificationMessages(CAPTURE);

        assertTrue(result);
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock, never()).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(modelServiceMock, never()).save(any(WorldpayOrderModificationModel.class));
        verify(processDefinitionDaoMock, never()).findWaitingOrderProcesses(anyString(), any(PaymentTransactionType.class));
    }

    @Test
    public void processOrderModificationMessageShouldProcessCancelPaymentTransaction() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CANCEL, orderModelMock)).thenReturn(Boolean.TRUE);
        when(authoriseWaitingProcessMock.getCode()).thenReturn(AUTHORISE_PROCESS_CODE);

        final boolean result = testObj.processOrderModificationMessages(CANCEL);

        assertTrue(result);
        verify(processDefinitionDaoMock).findWaitingOrderProcesses(ORDER_CODE, AUTHORIZATION);
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(any(Boolean.class));
        verify(businessProcessServiceMock).triggerEvent(AUTHORISE_PROCESS_CODE + "_" + AUTHORIZATION);
        verify(modelServiceMock).save(orderModificationModelMock);
    }

    @Test
    public void processOrderModificationMessageShouldSetOrderModificationAsDefectWhenAnyExceptionIsThrown() {
        doThrow(new NullPointerException(EXCEPTION_MESSAGE)).when(orderNotificationServiceMock).processOrderNotificationMessage(any(OrderNotificationMessage.class));//NOPMD
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationDaoMock.getExistingModifications(orderModificationModelMock)).thenReturn(singletonList(existingOrderModificationModelMock));
        when(existingOrderModificationModelMock.getDefectiveCounter()).thenReturn(DEFECTIVE_COUNT);

        final boolean result = testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefectiveReason(PROCESSING_ERROR);
        verify(orderModificationModelMock).setDefectiveCounter(DEFECTIVE_COUNT + 1);
        verify(modelServiceMock).save(orderModificationModelMock);
        assertFalse(result);
    }

    @Test
    public void shouldNotWakeUpOrderProcessWhenOrderStatusIsCancelled() {
        when(orderModelMock.getStatus()).thenReturn(CANCELLED);
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
    }

    @Test
    public void shouldWakeUpOrderProcessWhenOrderStatusIsNotCancelled() {
        when(orderModelMock.getStatus()).thenReturn(FRAUD_CHECKED);
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock)).thenReturn(Boolean.TRUE);

        testObj.processOrderModificationMessages(CAPTURE);

        verify(businessProcessServiceMock).triggerEvent(anyString());
    }

    @Test
    public void shouldMarkAsProcessedWhenOrderNotificationIsAUTHAndTransactionEntryIsNotPending() {
        when(worldpayPaymentTransactionServiceMock.getNotPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION)).thenReturn(singletonList(paymentTransactionEntryMock));

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(modelServiceMock).save(orderModificationModelMock);
        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(orderNotificationMessageMock);
    }

    @Test
    public void markNotificationAsDefectiveWhenContainsTokenReplyWithNonMatchingAuthenticatedShopperId() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);

        when(orderModificationDaoMock.getExistingModifications(orderModificationModelMock)).thenReturn(emptyList());

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setDefectiveReason(INVALID_AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void markNotificationAsDefectiveAndCountUpWhenContainsTokenReplyWithNonMatchingAuthenticatedShopperId() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(orderModificationDaoMock.getExistingModifications(orderModificationModelMock)).thenReturn(singletonList(existingOrderModificationModelMock));
        when(existingOrderModificationModelMock.getDefectiveCounter()).thenReturn(DEFECTIVE_COUNT);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setDefectiveReason(INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(orderModificationModelMock).setDefectiveCounter(DEFECTIVE_COUNT + 1);
    }

    @Test
    public void markNotificationAsDefectiveAndSetCountToOneWhenContainsTokenReplyWithNonMatchingAuthenticatedShopperId() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(orderModificationDaoMock.getExistingModifications(orderModificationModelMock)).thenReturn(singletonList(existingOrderModificationModelMock));
        when(existingOrderModificationModelMock.getDefectiveCounter()).thenReturn(null);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setDefectiveReason(INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(orderModificationModelMock).setDefectiveCounter(1);
    }

    @Test
    public void shouldSetCountToOneWhenNoPreviousDefectiveModificationsFound() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(orderModificationDaoMock.getExistingModifications(orderModificationModelMock)).thenReturn(emptyList());
        when(orderModificationModelMock.getDefectiveCounter()).thenReturn(null);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setDefectiveReason(INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(orderModificationModelMock).setDefectiveCounter(1);
    }

    @Test
    public void shouldNotCountWhenNoPreviousDefectiveModificationsFoundButCurrentModificationHasCount() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(orderModificationDaoMock.getExistingModifications(orderModificationModelMock)).thenReturn(emptyList());
        when(orderModificationModelMock.getDefectiveCounter()).thenReturn(10);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock, never()).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock, never()).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock).setDefectiveReason(INVALID_AUTHENTICATED_SHOPPER_ID);
        verify(orderModificationModelMock, never()).setDefectiveCounter(any());
    }


    @Test
    public void markNotificationAsProcessedWhenContainsTokenReplyContainsNonMatchingAuthenticatedShopperId() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(tokenReplyMock);
        when(tokenReplyMock.getAuthenticatedShopperID()).thenReturn(AUTHENTICATED_SHOPPER_ID);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefectiveReason(INVALID_AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void markNotificationAsProcessedWhenDoesNotContainTokenReply() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock)).thenReturn(Boolean.TRUE);

        when(orderModificationSerialiserMock.deserialise(orderModificationModelMock.getOrderNotificationMessage())).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getTokenReply()).thenReturn(null);

        testObj.processOrderModificationMessages(AUTHORIZATION);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(any(OrderNotificationMessage.class));
        verify(businessProcessServiceMock).triggerEvent(anyString());
        verify(orderModificationModelMock).setProcessed(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefective(Boolean.TRUE);
        verify(orderModificationModelMock, never()).setDefectiveReason(INVALID_AUTHENTICATED_SHOPPER_ID);
    }

    @Test
    public void shouldProcessRefundNotification() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, REFUND_FOLLOW_ON, orderModelMock)).thenReturn(Boolean.TRUE);
        when(worldpayOrderModificationRefundProcessStrategyMock.processRefundFollowOn(orderModelMock, orderNotificationMessageMock)).thenReturn(Boolean.TRUE);

        testObj.processOrderModificationMessages(REFUND_FOLLOW_ON);

        verify(orderNotificationServiceMock).processOrderNotificationMessage(orderNotificationMessageMock);
        verify(orderModificationModelMock).setProcessed(true);
        verify(modelServiceMock).save(orderModificationModelMock);

    }

    @Test
    public void shouldMarkRefundNotificationAsDefective() {
        when(worldpayPaymentTransactionServiceMock.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, REFUND_FOLLOW_ON, orderModelMock)).thenReturn(Boolean.TRUE);
        when(worldpayOrderModificationRefundProcessStrategyMock.processRefundFollowOn(orderModelMock, orderNotificationMessageMock)).thenReturn(Boolean.FALSE);

        testObj.processOrderModificationMessages(REFUND_FOLLOW_ON);

        verify(orderModificationModelMock).setDefective(true);
        verify(orderModificationModelMock).setProcessed(true);
        verify(modelServiceMock).save(orderModificationModelMock);
    }
}