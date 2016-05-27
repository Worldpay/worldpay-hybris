package com.worldpay.worldpayoms.notification.processors.impl;

import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionOperations;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;

public class DefaultRefundedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private TransactionOperations transactionTemplate;
    private ModelService modelService;

    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        transactionTemplate.execute(transactionStatus -> {
            setMatchingEntryAsNotPending(paymentTransactionModel, orderNotificationMessage);
            return null;
        });
    }

    private void setMatchingEntryAsNotPending(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        paymentTransactionModel.getEntries().stream().filter(entry -> entry.getCode().equals(orderNotificationMessage.getPaymentReply().getRefundReference())).forEach(entry -> {
            entry.setPending(false);
            entry.setTransactionStatus(ACCEPTED.name());
            modelService.save(entry);
        });
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
