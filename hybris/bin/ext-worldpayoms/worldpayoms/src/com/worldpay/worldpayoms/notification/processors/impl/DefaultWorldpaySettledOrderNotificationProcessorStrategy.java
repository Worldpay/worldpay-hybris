package com.worldpay.worldpayoms.notification.processors.impl;


import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionOperations;

public class DefaultWorldpaySettledOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private TransactionOperations transactionTemplate;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        transactionTemplate.execute(transactionStatus ->
                worldpayPaymentTransactionService.createNotPendingSettledPaymentTransactionEntry(paymentTransactionModel, orderNotificationMessage));
    }

    @Required
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }
}
