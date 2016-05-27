package com.worldpay.strategies.impl;

import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.model.ReturnProcessModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;

import static de.hybris.platform.basecommerce.enums.ReturnStatus.CANCELED;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayomsOrderModificationRefundProcessStrategy implements WorldpayOrderModificationRefundProcessStrategy {

    private BusinessProcessService businessProcessService;
    private ModelService modelService;

    @Override
    public boolean processRefundFollowOn(final OrderModel orderModel, final OrderNotificationMessage notificationMessage) {
        final List<ReturnRequestModel> returnRequests = orderModel.getReturnRequests();
        if (returnRequests != null) {
            final ReturnRequestModel matchingReturnRequest = findMatchingReturnRequest(notificationMessage, returnRequests);
            if (matchingReturnRequest != null) {
                matchingReturnRequest.setStatus(PAYMENT_REVERSED);
                modelService.save(matchingReturnRequest);
                final Collection<ReturnProcessModel> returnProcesses = matchingReturnRequest.getReturnProcess();
                // Assuming one return process per returnRequest
                final ReturnProcessModel returnProcessModel = returnProcesses.iterator().next();
                triggerOrderProcessEvent(REFUND_FOLLOW_ON, returnProcessModel);
                return true;
            }
        }
        return false;
    }

    protected ReturnRequestModel findMatchingReturnRequest(OrderNotificationMessage notificationMessage, List<ReturnRequestModel> returnRequests) {
        for (final ReturnRequestModel returnRequest : returnRequests) {
            if (!returnRequest.getStatus().equals(CANCELED) && notificationMessage.getPaymentReply().getRefundReference().equals(returnRequest.getPaymentTransactionEntry().getCode())) {
                return returnRequest;
            }
        }
        return null;
    }

    private void triggerOrderProcessEvent(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        final String eventName = getEventName(paymentTransactionType, businessProcessModel);
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(PaymentTransactionType paymentTransactionType, BusinessProcessModel businessProcessModels) {
        return businessProcessModels.getCode() + "_" + paymentTransactionType;
    }

    @Required
    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
