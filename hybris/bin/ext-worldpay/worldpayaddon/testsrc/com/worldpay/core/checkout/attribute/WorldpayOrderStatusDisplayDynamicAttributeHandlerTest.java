package com.worldpay.core.checkout.attribute;

import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.worldpay.core.checkout.attribute.WorldpayOrderStatusDisplayDynamicAttributeHandler.APM_OPEN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderStatusDisplayDynamicAttributeHandlerTest {

    private static final String SOME_ORDER_STATUS = "someOrderStatus";
    private static final String ORDER_STATUS_FOR_NULL_ORDER = "orderStatusForNullOrder";
    private static final String APM_OPEN_VALUE = "apmOpenValue";

    @InjectMocks
    @Spy
    private WorldpayOrderStatusDisplayDynamicAttributeHandler testObj = new WorldpayOrderStatusDisplayDynamicAttributeHandler();

    @Mock
    private OrderModel orderMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;

    @Before
    public void setup() {
        final Map<String, String> statusDisplayMap = new HashMap<>();
        statusDisplayMap.put(APM_OPEN, APM_OPEN_VALUE);
        testObj.setStatusDisplayMap(statusDisplayMap);

        doReturn(SOME_ORDER_STATUS).when(testObj).invokeSuperGet(orderMock);
        doReturn(ORDER_STATUS_FOR_NULL_ORDER).when(testObj).invokeSuperGet(null);
    }

    @Test
    public void getShouldInvokeSuperWhenNoTransactionsApmOpen() {
        when(worldpayPaymentTransactionServiceMock.isAnyPaymentTransactionApmOpenForOrder(orderMock)).thenReturn(false);

        final String result = testObj.get(orderMock);

        assertEquals(SOME_ORDER_STATUS, result);
        verify(testObj).invokeSuperGet(orderMock);
    }

    @Test
    public void getShouldReturnPaymentPendingWhenOrderHasAnApmOpenTransactions() {
        when(worldpayPaymentTransactionServiceMock.isAnyPaymentTransactionApmOpenForOrder(orderMock)).thenReturn(true);

        final String result = testObj.get(orderMock);

        assertEquals(APM_OPEN_VALUE, result);
        verify(testObj, never()).invokeSuperGet(anyObject());
    }

    @Test
    public void getShouldInvokeSuperWhenOrderIsNull() {
        final String result = testObj.get(null);

        assertEquals(ORDER_STATUS_FOR_NULL_ORDER, result);
        verify(testObj).invokeSuperGet(null);
    }
}