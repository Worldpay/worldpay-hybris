package com.worldpay.notification.processors.impl;

import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionOperations;

import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Captured notifications.
 */
public class DefaultCapturedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private TransactionOperations transactionTemplate;
    private ModelService modelService;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;

    /**
     * {@inheritDoc}
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final PaymentInfoModel paymentInfoModel = paymentTransactionModel.getInfo();
        if (paymentInfoModel != null && paymentInfoModel.getIsApm()) {
            transactionTemplate.execute(transactionStatus -> {
                worldpayPaymentTransactionService.createCapturedPaymentTransactionEntry(paymentTransactionModel, orderNotificationMessage);
                return null;
            });
        } else {
            final List<PaymentTransactionEntryModel> paymentTransactionEntries = worldpayPaymentTransactionService.getPendingPaymentTransactionEntriesForType(paymentTransactionModel, CAPTURE);
            transactionTemplate.execute(transactionStatus -> {
                updatePaymentTransactionEntry(paymentTransactionModel, paymentTransactionEntries, ACCEPTED.name());
                worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionEntries, orderNotificationMessage.getPaymentReply().getAmount());
                return null;
            });
        }
    }

    protected void updatePaymentTransactionEntry(final PaymentTransactionModel transactionModel, final List<PaymentTransactionEntryModel> paymentTransactionEntries, final String transactionStatus) {
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionEntries, transactionStatus);
        modelService.save(transactionModel);
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    @Required
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
