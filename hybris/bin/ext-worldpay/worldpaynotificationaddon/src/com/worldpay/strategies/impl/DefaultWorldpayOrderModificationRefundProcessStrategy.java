package com.worldpay.strategies.impl;

import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayOrderModificationRefundProcessStrategy implements WorldpayOrderModificationRefundProcessStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processRefundFollowOn(final OrderModel orderModelMock, final OrderNotificationMessage orderNotificationMessageMock) {
        throw new UnsupportedOperationException("Refunds are only supported with the worldpayoms extension installed");
    }
}
