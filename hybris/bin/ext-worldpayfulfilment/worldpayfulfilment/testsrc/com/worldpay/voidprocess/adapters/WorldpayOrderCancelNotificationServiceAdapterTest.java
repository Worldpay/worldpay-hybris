package com.worldpay.voidprocess.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.event.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayOrderCancelNotificationServiceAdapterTest {

    @InjectMocks
    private WorldpayOrderCancelNotificationServiceAdapter testObj = new WorldpayOrderCancelNotificationServiceAdapter();
    @Mock
    private OrderCancelRecordEntryModel orderCancelRecordEntryModelMock;
    @Mock
    private EventService eventServiceMock;
    @Captor
    private ArgumentCaptor<CancelFinishedEvent> cancelFinishedEventCaptor;

    @Test
    public void testSendCancelFinishedNotificationsShouldPublishEvent() throws Exception {

        testObj.sendCancelFinishedNotifications(orderCancelRecordEntryModelMock);

        verify(eventServiceMock).publishEvent(cancelFinishedEventCaptor.capture());

        final CancelFinishedEvent cancelFinishedEvent = cancelFinishedEventCaptor.getValue();
        assertEquals(orderCancelRecordEntryModelMock, cancelFinishedEvent.getCancelRequestRecordEntry());
    }
}