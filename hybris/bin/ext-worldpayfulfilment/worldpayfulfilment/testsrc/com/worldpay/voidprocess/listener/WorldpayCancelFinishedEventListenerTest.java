package com.worldpay.voidprocess.listener;

import com.worldpay.voidprocess.model.WorldpayVoidProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.voidprocess.listener.WorldpayCancelFinishedEventListener.WORLDPAY_VOID_PROCESS_NAME;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCancelFinishedEventListenerTest {

    public static final String ORDER_CODE = "orderCode";
    public static final long CURRENT_TIME_IN_MILLIS = 12l;

    @Spy
    @InjectMocks
    private WorldpayCancelFinishedEventListener testObj = new WorldpayCancelFinishedEventListener();
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private CancelFinishedEvent cancelFinishedEventMock;
    @Mock
    private BusinessProcessService businessProcessServiceMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private WorldpayVoidProcessModel worldpayVoidProcessModelMock;
    @Mock
    private ModelService modelServiceMock;

    @Test
    public void shouldCreateNewWorldpayVoidProcess() throws Exception {
        when(orderModelMock.getCode()).thenReturn(ORDER_CODE);
        final String processCode = WORLDPAY_VOID_PROCESS_NAME + "-" + orderModelMock.getCode() + "-" + CURRENT_TIME_IN_MILLIS;

        when(cancelFinishedEventMock.getCancelRequestRecordEntry().getModificationRecord().getOrder()).thenReturn(orderModelMock);
        when(businessProcessServiceMock.createProcess(processCode, WORLDPAY_VOID_PROCESS_NAME)).thenReturn(worldpayVoidProcessModelMock);
        doReturn(CURRENT_TIME_IN_MILLIS).when(testObj).getCurrentTimeInMillis();

        testObj.onEvent(cancelFinishedEventMock);

        verify(businessProcessServiceMock).createProcess(processCode, WORLDPAY_VOID_PROCESS_NAME);
        verify(worldpayVoidProcessModelMock).setOrder(orderModelMock);
        verify(modelServiceMock).save(worldpayVoidProcessModelMock);
        verify(businessProcessServiceMock).startProcess(worldpayVoidProcessModelMock);
    }
}