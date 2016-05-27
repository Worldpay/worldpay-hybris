package com.worldpay.ordercancel.impl.denialstrategies;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

import static java.text.MessageFormat.format;

public class WorldpayApmOrderCancelDenialStrategy extends AbstractCancelDenialStrategy implements OrderCancelDenialStrategy {

    private static final Logger LOG = Logger.getLogger(WorldpayApmOrderCancelDenialStrategy.class);

    @Override
    public OrderCancelDenialReason getCancelDenialReason(final OrderCancelConfigModel orderCancelConfigModel, final OrderModel orderModel,
                                                         final PrincipalModel principalModel, boolean partialCancel, boolean partialEntryCancel) {
        final List<PaymentTransactionModel> paymentTransactions = orderModel.getPaymentTransactions();
        if (paymentTransactions != null) {
            for (final PaymentTransactionModel paymentTransaction : paymentTransactions) {
                final PaymentInfoModel paymentInfo = paymentTransaction.getInfo();
                if (shouldReturnCancelReason(paymentInfo)) {
                    LOG.warn(format("The order [{0}] cannot be cancelled as the payment was made through an APM or is still unknown", orderModel.getCode()));
                    return getReason();
                }
            }
        }
        return null;
    }

    protected boolean shouldReturnCancelReason(final PaymentInfoModel paymentInfo) {
        return paymentInfo == null || paymentInfo.getIsApm() || StringUtils.isBlank(paymentInfo.getPaymentType());
    }
}
