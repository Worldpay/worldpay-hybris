package com.worldpay.worldpayoms.actions.returns;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.ReturnService;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;

import static com.hybris.cockpitng.actions.ActionResult.SUCCESS;
import static com.hybris.cockpitng.actions.ActionResult.StatusFlag.OBJECT_PERSISTED;
import static de.hybris.platform.basecommerce.enums.ConsignmentStatus.PICKUP_COMPLETE;
import static de.hybris.platform.basecommerce.enums.ConsignmentStatus.SHIPPED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static de.hybris.platform.payment.enums.PaymentTransactionType.SETTLED;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

public class WorldpayCreateReturnRequestAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<OrderModel, OrderModel> {
    protected static final String SOCKET_OUT_CONTEXT = "createReturnRequestContext";

    @Resource
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    @Resource
    private ReturnService returnService;

    @Override
    public boolean canPerform(final ActionContext<OrderModel> actionContext) {
        OrderModel order = actionContext.getData();
        return order != null && isReturnable(order) && isRefundable(order);
    }

    @Override
    public String getConfirmationMessage(ActionContext<OrderModel> actionContext) {
        return null;
    }

    @Override
    public boolean needsConfirmation(ActionContext<OrderModel> actionContext) {
        return false;
    }

    @Override
    public ActionResult<OrderModel> perform(ActionContext<OrderModel> actionContext) {
        this.sendOutput(SOCKET_OUT_CONTEXT, actionContext.getData());
        ActionResult actionResult = new ActionResult(SUCCESS);
        actionResult.getStatusFlags().add(OBJECT_PERSISTED);
        return actionResult;
    }

    protected boolean isReturnable(final OrderModel order) {
        return nonNull(order) && nonNull(order.getEntries()) && nonNull(order.getConsignments()) && !order.getConsignments().stream().noneMatch(
                consignment -> consignment.getStatus().equals(SHIPPED) || consignment.getStatus().equals(PICKUP_COMPLETE)) && !isEmpty(returnService.getAllReturnableEntries(order));
    }

    protected boolean isRefundable(final OrderModel orderModel) {
        if (CollectionUtils.isEmpty(orderModel.getPaymentTransactions())) {
            return false;
        }
        boolean isRefundable = true;
        for (final PaymentTransactionModel paymentTransactionModel : orderModel.getPaymentTransactions()) {
            isRefundable = isRefundable && isRefundable(paymentTransactionModel);
        }
        return isRefundable;
    }

    protected boolean isRefundable(final PaymentTransactionModel paymentTransactionModel) {
        if (isAPM(paymentTransactionModel)) {
            final WorldpayAPMPaymentInfoModel paymentInfoModel = (WorldpayAPMPaymentInfoModel) paymentTransactionModel.getInfo();
            return paymentInfoModel.getApmConfiguration().getAutomaticRefunds() &&
                    isNotEmpty(worldpayPaymentTransactionService.filterPaymentTransactionEntriesOfType(paymentTransactionModel, SETTLED));
        } else {
            return isNotEmpty(worldpayPaymentTransactionService.filterPaymentTransactionEntriesOfType(paymentTransactionModel, CAPTURE)) &&
                    !worldpayPaymentTransactionService.isPaymentTransactionPending(paymentTransactionModel, CAPTURE);
        }
    }

    protected boolean isAPM(final PaymentTransactionModel paymentTransactionModel) {
        return paymentTransactionModel.getInfo() instanceof WorldpayAPMPaymentInfoModel;
    }
}

