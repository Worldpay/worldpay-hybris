package com.worldpay.dao;

import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.List;

/**
 * Process Definition DAO interface.
 * The DAO is responsible for retrieving business processes.
 */
public interface ProcessDefinitionDao {

    /**
     * Finds waiting processes for the event based on the payment transaction type of the given order.
     *
     * @param orderCode              the order code
     * @param paymentTransactionType {@link PaymentTransactionType}
     * @return list of {@link BusinessProcessModel}
     */
    List<BusinessProcessModel> findWaitingOrderProcesses(String orderCode, PaymentTransactionType paymentTransactionType);
}
