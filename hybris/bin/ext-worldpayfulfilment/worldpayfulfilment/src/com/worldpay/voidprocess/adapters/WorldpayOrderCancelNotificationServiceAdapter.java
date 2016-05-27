package com.worldpay.voidprocess.adapters;

import de.hybris.platform.ordercancel.OrderCancelNotificationServiceAdapter;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.event.EventService;
import org.springframework.beans.factory.annotation.Required;

public class WorldpayOrderCancelNotificationServiceAdapter implements OrderCancelNotificationServiceAdapter {
    private EventService eventService;

    @Override
    public void sendCancelFinishedNotifications(final OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
        eventService.publishEvent(new CancelFinishedEvent(orderCancelRecordEntryModel));
    }

    @Override
    public void sendCancelPendingNotifications(final OrderCancelRecordEntryModel orderCancelRecordEntryModel) {
        //Deliberately does nothing...
    }

    @Required
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
