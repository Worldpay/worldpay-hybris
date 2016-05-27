package com.worldpay.worldpaytests.orders;

import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayPaymentTransactionTestDataTest {

    private static final String PT = "pt_";
    private static final String ORDER_CODE = "orderCode";
    private static final String REQUEST_ID = PT + ORDER_CODE;
    private static final String SERIALIZED_MODIFICATION_MESSAGE = "serializedModificationMessage";

    @InjectMocks
    private WorldpayPaymentTransactionTestData testObj = new WorldpayPaymentTransactionTestData();
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private OrderModificationSerialiser orderModificationSerialiserMock;

    @Test
    public void shouldSetRequestIdsOnPaymentTransactionsAndCreateOrderModifications() {
        when(customerModelMock.getOrders()).thenReturn(singletonList(orderModelMock));
        when(orderModelMock.getPaymentTransactions()).thenReturn(singletonList(paymentTransactionModelMock));
        when(paymentTransactionModelMock.getEntries()).thenReturn(singletonList(paymentTransactionEntryModelMock));
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
        when(modelServiceMock.create(WorldpayOrderModificationModel.class)).thenReturn(worldpayOrderModificationMock);
        when(modelServiceMock.create(WorldpayOrderModificationModel.class)).thenReturn(worldpayOrderModificationMock);
        when(orderModificationSerialiserMock.serialise(any(OrderNotificationMessage.class))).thenReturn(SERIALIZED_MODIFICATION_MESSAGE);

        testObj.setRequestIdsAndCreateOrderModifications(customerModelMock);

        verify(modelServiceMock).save(paymentTransactionEntryModelMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
        verify(paymentTransactionModelMock).setRequestId(REQUEST_ID);
        verify(paymentTransactionEntryModelMock).setRequestId(REQUEST_ID);
        verify(modelServiceMock).create(WorldpayOrderModificationModel.class);
        verify(worldpayOrderModificationMock).setWorldpayOrderCode(REQUEST_ID);
        verify(worldpayOrderModificationMock).setType(AUTHORIZATION);
        verify(modelServiceMock).save(worldpayOrderModificationMock);
        verify(orderModificationSerialiserMock).serialise(any(OrderNotificationMessage.class));
        verify(worldpayOrderModificationMock).setOrderNotificationMessage(SERIALIZED_MODIFICATION_MESSAGE);
    }
}