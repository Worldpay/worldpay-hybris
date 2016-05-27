package com.worldpay.strategies.impl;

import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.strategies.PaymentTransactionRejectionStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

public class DefaultPaymentTransactionRejectionStrategy implements PaymentTransactionRejectionStrategy {

    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private ModelService modelService;
    private ProcessDefinitionDao processDefinitionDao;
    private BusinessProcessService businessProcessService;

    @Override
    public void executeRejection(final PaymentTransactionModel paymentTransactionModel) {
        rejectTransactionEntries(paymentTransactionModel);
        wakeUpBusinessProcess(paymentTransactionModel.getOrder());
    }

    protected void rejectTransactionEntries(final PaymentTransactionModel paymentTransactionModel) {
        worldpayPaymentTransactionService.updateEntriesStatus(paymentTransactionModel.getEntries(), TransactionStatus.REJECTED.name());
        modelService.save(paymentTransactionModel);
    }

    protected void wakeUpBusinessProcess(final AbstractOrderModel orderModel) {
        final List<BusinessProcessModel> businessProcessModels = processDefinitionDao.findWaitingOrderProcesses(orderModel.getCode(), AUTHORIZATION);
        if (businessProcessModels.size() == 1) {
            triggerOrderProcessEvent(businessProcessModels);
        }
    }

    private void triggerOrderProcessEvent(final List<BusinessProcessModel> businessProcessModels) {
        final String eventName = getEventName(AUTHORIZATION, businessProcessModels.get(0));
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        return businessProcessModel.getCode() + "_" + paymentTransactionType;
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
    public void setProcessDefinitionDao(ProcessDefinitionDao processDefinitionDao) {
        this.processDefinitionDao = processDefinitionDao;
    }

    @Required
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }
}
