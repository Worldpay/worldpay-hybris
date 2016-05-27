package com.worldpay.cronjob;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.support.TransactionOperations;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import static de.hybris.platform.payment.dto.TransactionStatus.REVIEW;

/**
 * The APM Order Timeout Job Performable that sets the Payment Transaction Entries status to REVIEW for all timed out pending orders with APM as the payment type
 * and awakens the order process of the orders.
 */
public class APMOrderTimeoutJobPerformable extends AbstractJobPerformable {

    private static final Logger LOG = Logger.getLogger(APMOrderTimeoutJobPerformable.class);

    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDao;
    private BusinessProcessService businessProcessService;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private TransactionOperations transactionTemplate;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {
        LOG.debug("Executing Order Timeout cronjob for timed out pending payment transactions");
        final List<PaymentTransactionModel> cancellablePendingAPMPaymentTransactions = worldpayPaymentTransactionDao.findCancellablePendingAPMPaymentTransactions();
        CronJobResult cronJobResult = CronJobResult.SUCCESS;
        for (PaymentTransactionModel paymentTransactionModel : cancellablePendingAPMPaymentTransactions) {
            transactionTemplate.execute(transactionStatus -> {
                worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionModel.getEntries(), REVIEW.name());
                modelService.save(paymentTransactionModel);
                return null;
            });
            final OrderModel orderModel = (OrderModel) paymentTransactionModel.getOrder();
            final Collection<OrderProcessModel> orderProcesses = orderModel.getOrderProcess();
            for (final OrderProcessModel orderProcess : orderProcesses) {
                final String eventName = orderProcess.getCode() + "_" + PaymentTransactionType.AUTHORIZATION;
                LOG.info(MessageFormat.format("Order with code [{0}] timed out. Attempting to trigger an event with code [{1}]", orderModel.getCode(), eventName));
                businessProcessService.triggerEvent(eventName);
            }
        }
        return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
    }

    @Required
    public void setWorldpayPaymentTransactionDao(WorldpayPaymentTransactionDao worldpayPaymentTransactionDao) {
        this.worldpayPaymentTransactionDao = worldpayPaymentTransactionDao;
    }

    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Required
    public void setWorldpayPaymentTransactionService(final WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    @Required
    public void setTransactionTemplate(final TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}

