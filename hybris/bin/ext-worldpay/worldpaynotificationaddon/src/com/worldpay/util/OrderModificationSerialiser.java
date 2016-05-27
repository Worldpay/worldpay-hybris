package com.worldpay.util;

import com.worldpay.service.notification.OrderNotificationMessage;

/**
 * Order Modification Serialiser interface.
 * The serialiser is responsible for serialising and deserialising {@link OrderNotificationMessage}
 */
public interface OrderModificationSerialiser {

    /**
     * Serialises the Order Notification Message.
     *
     * @param orderNotificationMessage {@link OrderNotificationMessage}
     * @return serialised order notification message
     */
    String serialise(OrderNotificationMessage orderNotificationMessage);

    /**
     * Deserialises the Order Notification Message.
     *
     * @param json serialised {@link OrderNotificationMessage}
     * @return deserialised {@link OrderNotificationMessage}
     */
    OrderNotificationMessage deserialise(String json);

}
