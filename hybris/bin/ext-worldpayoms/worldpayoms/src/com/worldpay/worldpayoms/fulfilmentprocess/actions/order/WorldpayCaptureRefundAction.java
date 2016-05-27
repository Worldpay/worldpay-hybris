package com.worldpay.worldpayoms.fulfilmentprocess.actions.order;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.warehousing.model.ReturnProcessModel;
import de.hybris.platform.warehousing.returns.service.RefundAmountCalculationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.List;

import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSAL_FAILED;
import static de.hybris.platform.basecommerce.enums.ReturnStatus.PAYMENT_REVERSAL_PENDING;
import static de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition.NOK;
import static de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition.OK;
import static java.text.MessageFormat.format;

public class WorldpayCaptureRefundAction extends AbstractSimpleDecisionAction<ReturnProcessModel> {
    private static final Logger LOG = Logger.getLogger(WorldpayCaptureRefundAction.class);

    private PaymentService paymentService;
    private RefundAmountCalculationService refundAmountCalculationService;

    @Override
    public Transition executeAction(final ReturnProcessModel process) throws Exception {
        LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

        final ReturnRequestModel returnRequest = process.getReturnRequest();
        final List<PaymentTransactionModel> transactions = returnRequest.getOrder().getPaymentTransactions();

        if (transactions.isEmpty()) {
            LOG.info("Unable to refund for ReturnRequest " + returnRequest.getCode() + ", no PaymentTransactions found");
            setReturnRequestStatus(returnRequest, PAYMENT_REVERSAL_FAILED);
            return NOK;
        }
        //This assumes that the Order only has one PaymentTransaction
        final PaymentTransactionModel transaction = transactions.get(0);

        final BigDecimal customRefundAmount = refundAmountCalculationService.getCustomRefundAmount(returnRequest);
        BigDecimal amountToRefund;
        if (customRefundAmount != null && customRefundAmount.compareTo(BigDecimal.ZERO) > 0) {
            amountToRefund = customRefundAmount;
        } else {
            amountToRefund = refundAmountCalculationService.getOriginalRefundAmount(returnRequest);
        }

        Transition result;
        try {
            final PaymentTransactionEntryModel refundPaymentTransactionEntry = getPaymentService().refundFollowOn(transaction, amountToRefund);
            returnRequest.setPaymentTransactionEntry(refundPaymentTransactionEntry);
            setReturnRequestStatus(returnRequest, PAYMENT_REVERSAL_PENDING);
            result = OK;
        } catch (final AdapterException e) {
            LOG.error(format("Unable to refund for ReturnRequest [{0}]", returnRequest.getCode()), e);
            setReturnRequestStatus(returnRequest, PAYMENT_REVERSAL_FAILED);
            result = NOK;
        }

        return result;
    }

    /**
     * Update the return status for all return entries in {@link ReturnRequestModel}
     *
     * @param returnRequest - the return request
     * @param status        - the return status
     */
    protected void setReturnRequestStatus(final ReturnRequestModel returnRequest, final ReturnStatus status) {
        returnRequest.setStatus(status);
        returnRequest.getReturnEntries().stream().forEach(entry -> {
            entry.setStatus(status);
            getModelService().save(entry);
        });
        getModelService().save(returnRequest);
    }

    protected PaymentService getPaymentService() {
        return paymentService;
    }

    @Required
    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Required
    public void setRefundAmountCalculationService(final RefundAmountCalculationService refundAmountCalculationService) {
        this.refundAmountCalculationService = refundAmountCalculationService;
    }
}
