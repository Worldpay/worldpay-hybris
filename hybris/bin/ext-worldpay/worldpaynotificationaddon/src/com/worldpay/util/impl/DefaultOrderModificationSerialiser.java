package com.worldpay.util.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.util.OrderModificationSerialiser;

import java.lang.reflect.Type;

/**
 * Default implementation of {@link OrderModificationSerialiser}.
 */
public class DefaultOrderModificationSerialiser implements OrderModificationSerialiser {

    /**
     * {@inheritDoc}
     * @see OrderModificationSerialiser#serialise(OrderNotificationMessage)
     */
    @Override
    public String serialise(final OrderNotificationMessage orderNotificationMessage) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(orderNotificationMessage);
    }

    /**
     * {@inheritDoc}
     * @see OrderModificationSerialiser#deserialise(String)
     */
    @Override
    public OrderNotificationMessage deserialise(final String json) {
        final Gson gson = new Gson();
        final Type type = new TypeToken<OrderNotificationMessage>() {/**/
        }.getType();
        return gson.fromJson(json, type);
    }
}
