package com.worldpay.notification.processors.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.notification.processors.OrderNotificationProcessorStrategy;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionOperations;

import java.text.MessageFormat;

import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_PENDING;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;

/**
 * The implementation of {@link OrderNotificationProcessorStrategy} that processes Refused notifications.
 */
public class DefaultRefusedOrderNotificationProcessorStrategy implements OrderNotificationProcessorStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultRefusedOrderNotificationProcessorStrategy.class);

    private WorldpayPaymentInfoService worldpayPaymentInfoService;
    private TransactionOperations transactionTemplate;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private ModelService modelService;

    /**
     * {@inheritDoc}
     * @see OrderNotificationProcessorStrategy#processNotificationMessage(PaymentTransactionModel, OrderNotificationMessage)
     */
    @Override
    public void processNotificationMessage(final PaymentTransactionModel paymentTransactionModel, final OrderNotificationMessage orderNotificationMessage) {
        final AbstractOrderModel orderModel = paymentTransactionModel.getOrder();
        if (isOrderRefusable(orderModel)) {
            worldpayPaymentInfoService.setPaymentInfoModel(paymentTransactionModel, orderModel, orderNotificationMessage);
            transactionTemplate.execute(transactionStatus -> {
                worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionModel.getEntries(), REJECTED.name());
                worldpayPaymentTransactionService.updateEntriesAmount(paymentTransactionModel.getEntries(), orderNotificationMessage.getPaymentReply().getAmount());
                modelService.save(paymentTransactionModel);
                return null;
            });
        } else {
            LOG.warn(MessageFormat.format("The order [{0}] is a cart or not in PAYMENT_PENDING status.", orderModel.getCode()));
        }
    }

    protected boolean isOrderRefusable(final AbstractOrderModel orderModel) {
        return orderModel instanceof OrderModel && PAYMENT_PENDING.equals(orderModel.getStatus());
    }

    @Required
    public void setWorldpayPaymentInfoService(WorldpayPaymentInfoService worldpayPaymentInfoService) {
        this.worldpayPaymentInfoService = worldpayPaymentInfoService;
    }

    @Required
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
