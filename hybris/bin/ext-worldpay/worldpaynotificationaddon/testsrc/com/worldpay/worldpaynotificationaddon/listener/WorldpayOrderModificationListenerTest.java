package com.worldpay.worldpaynotificationaddon.listener;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.JournalReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.worldpay.service.model.AuthorisedStatus.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderModificationListenerTest {

    public static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    public static final String ORDER_CODE = "orderCode";
    public static final String SERIALIZED = "serialized";
    public static final int RETURN_CODE = 19;

    @InjectMocks
    private WorldpayOrderModificationListener testObj = new WorldpayOrderModificationListener();

    @Mock
    private OrderModificationEvent orderModificationEventMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private JournalReply journalReplyMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderModificationSerialiser orderModificationSerialiser;
    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationModelMock;
    @Mock
    private WorldpayCartService worldpayCartServiceMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private CartModel cartModelMock;

    private Map<AuthorisedStatus, PaymentTransactionType> paymentTransactionTypeMap = new HashMap<>();

    @Before
    public void setup() {
        paymentTransactionTypeMap.put(AUTHORISED, AUTHORIZATION);
        paymentTransactionTypeMap.put(REFUSED, CANCEL);
        paymentTransactionTypeMap.put(CAPTURED, CAPTURE);
        testObj.setPaymentTransactionTypeMap(paymentTransactionTypeMap);
        when(orderModificationEventMock.getOrderNotificationMessage()).thenReturn(orderNotificationMessageMock);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn(RETURN_CODE);
        when(orderNotificationMessageMock.getJournalReply()).thenReturn(journalReplyMock);
        when(orderNotificationMessageMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
        when(orderModificationSerialiser.serialise(orderNotificationMessageMock)).thenReturn(SERIALIZED);
        when(modelServiceMock.create(WorldpayOrderModificationModel.class)).thenReturn(worldpayOrderModificationModelMock);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
    }

    @Test
    public void onEventRefusedShouldUpdateCartModelWhenDeclineCodeExists() throws OrderCancelException {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock).setWorldpayDeclineCodeOnCart(WORLDPAY_ORDER_CODE, RETURN_CODE);
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
    }

    @Test
    public void onEventRefusedShouldNotUpdateCartModelWhenDeclineCodeIsZero() throws OrderCancelException {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn(0);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyInt());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(CANCEL);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventAUTHORISEDShouldSaveModificationModel() throws OrderCancelException {
        when(journalReplyMock.getJournalType()).thenReturn(AUTHORISED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyInt());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(AUTHORIZATION);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventCAPTUREDShouldSaveModificationModel() throws OrderCancelException {
        when(journalReplyMock.getJournalType()).thenReturn(CAPTURED);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyInt());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(CAPTURE);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventREFUSEDShouldSaveModificationModelWhenTransactionBelongsToACart() throws OrderCancelException {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn(0);
        when(paymentTransactionModelMock.getOrder()).thenReturn(cartModelMock);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyInt());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(any(PaymentTransactionType.class));
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(anyString());
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(anyString());
    }


    @Test
    public void onEventREFUSEDShouldSaveModificationModelWhenTransactionBelongsToAnOrder() throws OrderCancelException {
        when(journalReplyMock.getJournalType()).thenReturn(REFUSED);
        when(orderNotificationMessageMock.getPaymentReply().getReturnCode()).thenReturn(0);

        testObj.onEvent(orderModificationEventMock);

        verify(worldpayCartServiceMock, never()).setWorldpayDeclineCodeOnCart(anyString(), anyInt());
        verify(modelServiceMock).save(worldpayOrderModificationModelMock);
        verify(worldpayOrderModificationModelMock).setType(CANCEL);
        verify(worldpayOrderModificationModelMock).setWorldpayOrderCode(WORLDPAY_ORDER_CODE);
        verify(worldpayOrderModificationModelMock).setOrderNotificationMessage(SERIALIZED);
    }

    @Test
    public void onEventShouldNotSaveModificationModelDueToIncorrectMapping() {
        when(journalReplyMock.getJournalType()).thenReturn(EXPIRED);

        testObj.onEvent(orderModificationEventMock);

        verify(modelServiceMock, never()).save(any());
    }
}