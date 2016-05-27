package com.worldpay.notification.processors;

import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Order Notification Processor Strategy interface.
 * The strategy is responsible for processing {@link OrderNotificationMessage}.
 */
public interface OrderNotificationProcessorStrategy {

    /**
     * Processes the {@link OrderNotificationMessage} for the given {@link PaymentTransactionModel}
     *
     * @param paymentTransactionModel  the {@link PaymentTransactionModel}
     * @param orderNotificationMessage the {@link OrderNotificationMessage} to be processed
     */
    void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage);

}
