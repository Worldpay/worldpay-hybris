package com.worldpay.strategies.impl;

import com.worldpay.core.services.OrderNotificationService;
import com.worldpay.dao.OrderModificationDao;
import com.worldpay.dao.ProcessDefinitionDao;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.strategies.WorldpayOrderModificationProcessStrategy;
import com.worldpay.strategies.WorldpayOrderModificationRefundProcessStrategy;
import com.worldpay.strategy.WorldpayAuthenticatedShopperIdStrategy;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotificationaddon.enums.DefectiveReason;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Set;

import static com.worldpay.worldpaynotificationaddon.enums.DefectiveReason.INVALID_AUTHENTICATED_SHOPPER_ID;
import static com.worldpay.worldpaynotificationaddon.enums.DefectiveReason.NO_PAYMENT_TRANSACTION_MATCHED;
import static com.worldpay.worldpaynotificationaddon.enums.DefectiveReason.PROCESSING_ERROR;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CANCEL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.REFUND_FOLLOW_ON;
import static de.hybris.platform.payment.enums.PaymentTransactionType.SETTLED;
import static java.text.MessageFormat.format;

/**
 * Default implementation of the {@link WorldpayOrderModificationProcessStrategy} interface.
 * <p>
 * For each unprocessed {@link WorldpayOrderModificationModel} a check is performed if the previous transaction was completed.
 * If it was, the order modification is processed.
 * In case of an error, a defective {@link WorldpayOrderModificationModel} is saved.
 * </p>
 */
public class DefaultWorldpayOrderModificationProcessStrategy implements WorldpayOrderModificationProcessStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderModificationProcessStrategy.class);

    private OrderModificationDao orderModificationDao;
    private ProcessDefinitionDao processDefinitionDao;
    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private OrderNotificationService orderNotificationService;
    private OrderModificationSerialiser orderModificationSerialiser;
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    private Set nonTriggeringOrderStatuses;
    private WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy;
    private WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayOrderModificationProcessStrategy#processOrderModificationMessages(PaymentTransactionType)
     */
    @Override
    public boolean processOrderModificationMessages(final PaymentTransactionType paymentTransactionType) {
        boolean success = true;
        final List<WorldpayOrderModificationModel> orderModificationsByType = orderModificationDao.findUnprocessedOrderModificationsByType(paymentTransactionType);

        for (final WorldpayOrderModificationModel orderModificationModel : orderModificationsByType) {
            final String worldpayOrderCode = orderModificationModel.getWorldpayOrderCode();
            final PaymentTransactionModel paymentTransactionModel = worldpayPaymentTransactionService.getPaymentTransactionFromCode(worldpayOrderCode);
            if (paymentTransactionModel != null) {
                if (AUTHORIZATION.equals(paymentTransactionType)) {
                    markAsProcessedIfEntryIsNotPending(paymentTransactionType, orderModificationModel, paymentTransactionModel);
                }
                AbstractOrderModel abstractOrderModel = paymentTransactionModel.getOrder();
                if (abstractOrderModel instanceof OrderModel) {
                    OrderModel orderModel = (OrderModel) abstractOrderModel;
                    LOG.info(format("Found order for Worldpay Order Code [{0}]. Processing modification message.", worldpayOrderCode));
                    try {
                        if (worldpayPaymentTransactionService.isPreviousTransactionCompleted(worldpayOrderCode, paymentTransactionType, orderModel)) {
                            processMessage(paymentTransactionType, orderModificationModel, orderModel);
                        } else {
                            LOG.info(format("The previous transaction for [{0}] is still pending in worldpayOrder [{1}]", paymentTransactionType, worldpayOrderCode));
                        }
                    } catch (final Exception exception) {
                        setDefectiveReason(orderModificationModel, PROCESSING_ERROR);
                        processDefectiveModification(orderModificationModel, exception);
                        success = false;
                    }
                } else if (abstractOrderModel instanceof CartModel) {
                    LOG.warn(format("Worldpay Order Code [{0}] related to a Cart. Skipping processing modification message.", worldpayOrderCode));
                }
            } else {
                setDefectiveReason(orderModificationModel, NO_PAYMENT_TRANSACTION_MATCHED);
                processDefectiveModification(orderModificationModel, null);
            }
        }
        return success;
    }

    protected void markAsProcessedIfEntryIsNotPending(final PaymentTransactionType paymentTransactionType,
                                                      final WorldpayOrderModificationModel orderModificationModel,
                                                      final PaymentTransactionModel paymentTransactionModel) {
        worldpayPaymentTransactionService.getNotPendingPaymentTransactionEntriesForType(paymentTransactionModel, paymentTransactionType).stream().forEach(paymentTransactionEntryModel -> {
            orderModificationModel.setProcessed(Boolean.TRUE);
            modelService.save(orderModificationModel);
        });
    }

    protected void processDefectiveModification(final WorldpayOrderModificationModel orderModificationModel, final Exception exception) {
        orderModificationModel.setDefective(Boolean.TRUE);
        orderModificationModel.setProcessed(Boolean.TRUE);
        modelService.save(orderModificationModel);
        if (exception != null) {
            LOG.error(format("There was an error processing message [{0}]. Reason: [{1}]", orderModificationModel.getPk(), exception.getMessage()), exception);
        }
    }

    protected void processMessage(final PaymentTransactionType paymentTransactionTypeFromCronJob, final WorldpayOrderModificationModel orderModificationModel, final OrderModel orderModel) {
        PaymentTransactionType paymentTransactionType = paymentTransactionTypeFromCronJob;
        final OrderNotificationMessage notificationMessage = orderModificationSerialiser.deserialise(orderModificationModel.getOrderNotificationMessage());
        if (REFUND_FOLLOW_ON.equals(paymentTransactionType)) {
            if (worldpayOrderModificationRefundProcessStrategy.processRefundFollowOn(orderModel, notificationMessage)) {
                processOrderModification(orderModificationModel, notificationMessage);
            } else {
                processDefectiveModification(orderModificationModel, null);
            }
        }
        if (SETTLED.equals(paymentTransactionTypeFromCronJob)) {
            processOrderModification(orderModificationModel, notificationMessage);
        } else {
            if (CANCEL.equals(paymentTransactionTypeFromCronJob)) {
                // Rejected payments expect the order to be in a waitFor_AUTHORIZATION state
                paymentTransactionType = AUTHORIZATION;
            }
            final List<BusinessProcessModel> businessProcessModels = processDefinitionDao.findWaitingOrderProcesses(orderModel.getCode(), paymentTransactionType);
            if (businessProcessModels.size() == 1) {
                if (notificationIsValid(notificationMessage, orderModel)) {
                    processOrderModification(orderModificationModel, notificationMessage);
                    if (!nonTriggeringOrderStatuses.contains(orderModel.getStatus())) {
                        triggerOrderProcessEvent(paymentTransactionType, businessProcessModels.get(0));
                    }
                } else {
                    setDefectiveReason(orderModificationModel, INVALID_AUTHENTICATED_SHOPPER_ID);
                    LOG.error(format("Received modification with invalid shopperId. The Modification has been marked as defective. worldpayOrderCode = [{0}], type=[{1}]",
                            orderModificationModel.getWorldpayOrderCode(), orderModificationModel.getType()));
                    processDefectiveModification(orderModificationModel, null);
                }
            } else if (businessProcessModels.size() > 1) {
                LOG.error(format("Must only be one businessProcess found, number found: [{0}] " +
                        "for order [{1}] and transactionType [{2}]", businessProcessModels.size(), orderModel.getCode(), paymentTransactionType));
            }
        }
    }

    protected void setDefectiveReason(final WorldpayOrderModificationModel orderModificationModel, DefectiveReason defectiveReason) {
        orderModificationModel.setDefectiveReason(defectiveReason);
        final List<WorldpayOrderModificationModel> modifications = orderModificationDao.getExistingModifications(orderModificationModel);
        if (!modifications.isEmpty()) {
            final WorldpayOrderModificationModel existingModification = modifications.get(0);
            final Integer defectiveCounter = existingModification.getDefectiveCounter();
            orderModificationModel.setDefectiveCounter(defectiveCounter == null ? 1 : defectiveCounter + 1);
            modelService.remove(existingModification);
        } else {
            final Integer defectiveCounter = orderModificationModel.getDefectiveCounter();
            if (defectiveCounter == null) {
                orderModificationModel.setDefectiveCounter(1);
            }
        }
    }

    protected boolean notificationIsValid(OrderNotificationMessage notificationMessage, AbstractOrderModel orderModel) {
        final TokenReply tokenReply = notificationMessage.getTokenReply();
        return tokenReply != null && authenticatedShopperIdMatches(orderModel, tokenReply.getAuthenticatedShopperID()) || tokenReply == null;
    }

    protected boolean authenticatedShopperIdMatches(final AbstractOrderModel orderModel, final String tokenAuthenticatedShopperId) {
        return tokenAuthenticatedShopperId.equals(worldpayAuthenticatedShopperIdStrategy.getAuthenticatedShopperId(orderModel.getUser()));
    }

    protected void processOrderModification(WorldpayOrderModificationModel orderModificationModel, OrderNotificationMessage notificationMessage) {
        orderNotificationService.processOrderNotificationMessage(notificationMessage);
        orderModificationModel.setProcessed(Boolean.TRUE);
        modelService.save(orderModificationModel);
    }

    private void triggerOrderProcessEvent(final PaymentTransactionType paymentTransactionType, final BusinessProcessModel businessProcessModel) {
        final String eventName = getEventName(paymentTransactionType, businessProcessModel);
        businessProcessService.triggerEvent(eventName);
    }

    private String getEventName(PaymentTransactionType paymentTransactionType, BusinessProcessModel businessProcessModels) {
        return businessProcessModels.getCode() + "_" + paymentTransactionType;
    }

    @Required
    public void setOrderModificationDao(OrderModificationDao orderModificationDao) {
        this.orderModificationDao = orderModificationDao;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Required
    public void setProcessDefinitionDao(ProcessDefinitionDao processDefinitionDao) {
        this.processDefinitionDao = processDefinitionDao;
    }

    @Required
    public void setOrderNotificationService(OrderNotificationService orderNotificationService) {
        this.orderNotificationService = orderNotificationService;
    }

    @Required
    public void setOrderModificationSerialiser(OrderModificationSerialiser orderModificationSerialiser) {
        this.orderModificationSerialiser = orderModificationSerialiser;
    }

    @Required
    public void setWorldpayPaymentTransactionService(WorldpayPaymentTransactionService worldpayPaymentTransactionService) {
        this.worldpayPaymentTransactionService = worldpayPaymentTransactionService;
    }

    @Required
    public void setNonTriggeringOrderStatuses(Set nonTriggeringOrderStatuses) {
        this.nonTriggeringOrderStatuses = nonTriggeringOrderStatuses;
    }

    @Required
    public void setWorldpayAuthenticatedShopperIdStrategy(final WorldpayAuthenticatedShopperIdStrategy worldpayAuthenticatedShopperIdStrategy) {
        this.worldpayAuthenticatedShopperIdStrategy = worldpayAuthenticatedShopperIdStrategy;
    }

    @Required
    public void setWorldpayOrderModificationRefundProcessStrategy(final WorldpayOrderModificationRefundProcessStrategy worldpayOrderModificationRefundProcessStrategy) {
        this.worldpayOrderModificationRefundProcessStrategy = worldpayOrderModificationRefundProcessStrategy;
    }
}
