package com.worldpay.strategies;

import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Strategy that handles the REFUNDED order modifications
 */
public interface WorldpayOrderModificationRefundProcessStrategy {

    /**
     * Processes an orderNotification of type REFUND
     * @param orderModel
     * @param orderNotificationMessage
     * @return if the message was processed or not
     */
    boolean processRefundFollowOn(final OrderModel orderModel, final OrderNotificationMessage orderNotificationMessage);

}
