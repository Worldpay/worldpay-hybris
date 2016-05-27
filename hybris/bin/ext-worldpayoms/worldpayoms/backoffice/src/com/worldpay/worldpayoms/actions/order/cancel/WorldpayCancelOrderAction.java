package com.worldpay.worldpayoms.actions.order.cancel;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import static com.hybris.cockpitng.actions.ActionResult.SUCCESS;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static java.util.Objects.nonNull;

public class WorldpayCancelOrderAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<OrderModel, OrderModel> {
    protected static final String SOCKET_OUT_CONTEXT = "cancelOrderContext";

    @Resource
    private WorldpayPaymentTransactionService worldpayPaymentTransactionService;
    @Resource
    private UserService userService;
    @Resource
    private OrderCancelService orderCancelService;

    @Override
    public boolean canPerform(final ActionContext<OrderModel> actionContext) {
        final OrderModel order = actionContext.getData();
        if (order != null){
            final PaymentTransactionModel paymentTransactionModel = order.getPaymentTransactions().get(0);
            if (paymentTransactionModel.getInfo() instanceof WorldpayAPMPaymentInfoModel) {
                return false;
            }
            boolean authPending = worldpayPaymentTransactionService.isPaymentTransactionPending(paymentTransactionModel, AUTHORIZATION);
            return nonNull(order.getEntries()) && !authPending && this.orderCancelService.isCancelPossible(order, userService.getCurrentUser(), true, true).isAllowed();
        }
        return false;
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
        return new ActionResult(SUCCESS);
    }
}
