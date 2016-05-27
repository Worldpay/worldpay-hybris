package com.worldpay.fulfilmentprocess.actions.order;

import static com.worldpay.fulfilmentprocess.actions.order.WorldpayOrderManualCheckedAction.Transition.NOK;
import static com.worldpay.fulfilmentprocess.actions.order.WorldpayOrderManualCheckedAction.Transition.OK;
import static com.worldpay.fulfilmentprocess.actions.order.WorldpayOrderManualCheckedAction.Transition.UNDEFINED;
import static de.hybris.platform.core.enums.OrderStatus.FRAUD_CHECKED;
import static de.hybris.platform.core.enums.OrderStatus.SUSPENDED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderManualCheckedActionTest {

    @InjectMocks
    private WorldpayOrderManualCheckedAction testObj = new WorldpayOrderManualCheckedAction();

    @Mock
    private OrderProcessModel orderProcessModelMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private OrderHistoryEntryModel orderHistoryEntryModelMock;
    @Mock
    private TimeService timeServiceMock;

    @Before
    public void setup() {
        when(modelServiceMock.create(OrderHistoryEntryModel.class)).thenReturn(orderHistoryEntryModelMock);
        when(orderProcessModelMock.getOrder()).thenReturn(orderModelMock);
        when(timeServiceMock.getCurrentTime()).thenReturn(DateTime.now().toDate());
    }

    @Test
    public void executeShouldReturnOK() throws Exception {
        when(orderModelMock.getFraudulent()).thenReturn(false);

        final String result = testObj.execute(orderProcessModelMock);

        verify(modelServiceMock).save(orderHistoryEntryModelMock);
        verify(orderModelMock, never()).setStatus(SUSPENDED);
        verify(orderModelMock).setStatus(FRAUD_CHECKED);
        verify(modelServiceMock).save(orderModelMock);
        assertEquals(OK.toString(), result);
    }

    @Test
    public void executeShouldReturnNOK() throws Exception {
        when(orderModelMock.getFraudulent()).thenReturn(true);

        final String result = testObj.execute(orderProcessModelMock);

        verify(modelServiceMock).save(orderHistoryEntryModelMock);
        verify(orderModelMock).setStatus(SUSPENDED);
        verify(modelServiceMock).save(orderModelMock);
        assertEquals(NOK.toString(), result);
    }

    @Test
    public void executeShouldReturnUNDEFINED() throws Exception {
        when(orderModelMock.getFraudulent()).thenReturn(null);

        final String result = testObj.execute(orderProcessModelMock);

        verify(modelServiceMock,never()).save(any(OrderHistoryEntryModel.class));
        verify(orderModelMock,never()).setStatus(any(OrderStatus.class));
        assertEquals(UNDEFINED.toString(), result);
    }
}