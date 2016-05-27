package com.worldpay.core.services;

import com.worldpay.service.notification.OrderNotificationMessage;


/**
 * Order Notification Service interface. Service is responsible for processing the order notification from worldpay and
 * to organising the calls to the DAO to fetch required objects using the world pay order number.
 */
public interface OrderNotificationService {

    /**
     * Processes the order notification message from worldpay. - For an authorised message, saves the card information
     * against the customer for future use and invokes the place order business process which moves the order through the
     * subsequent states.
     *
     * @param orderNotificationMessage - The order notification message from worldpay.
     */
    void processOrderNotificationMessage(final OrderNotificationMessage orderNotificationMessage);
}
