package com.worldpay.core.event;

import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * Event to be used when the application receives an orderNotificationMessage.
 */
public class OrderModificationEvent extends AbstractEvent {

    private OrderNotificationMessage orderNotificationMessage;

    /**
     * Public constructor for the Event
     * @param orderNotificationMessage the {@link OrderNotificationMessage} received from Worldpay to be attached to the event.
     */
    public OrderModificationEvent(OrderNotificationMessage orderNotificationMessage) {
        super();
        this.orderNotificationMessage = orderNotificationMessage;
    }

    public OrderNotificationMessage getOrderNotificationMessage() {
        return orderNotificationMessage;
    }

    public void setOrderNotificationMessage(OrderNotificationMessage orderNotificationMessage) {
        this.orderNotificationMessage = orderNotificationMessage;
    }
}
