package com.worldpay.converters;

import com.worldpay.internal.model.*;
import com.worldpay.service.model.JournalReply;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.WebformRefundReply;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.response.transform.ServiceResponseTransformerHelper;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Converter used to transform an {@link OrderNotificationMessage} into a {@link PaymentService}
 */
public class OrderModificationRequestConverter implements Converter<PaymentService, OrderNotificationMessage> {

    /**
     * Converts a {@link PaymentService} to a {@link OrderNotificationMessage} setting in the target the necessary values.
     * @param source a {@link PaymentService}
     * @return an OrderNotificationMessage with the necessary values from the PaymentService.
     */
    @Override
    public OrderNotificationMessage convert(final PaymentService source) throws ConversionException {
        return convert(source, new OrderNotificationMessage());
    }

    /**
     * Converts a {@link PaymentService} to a {@link OrderNotificationMessage} setting in the target the necessary values.
     * @param source a {@link PaymentService}
     * @param target a {@link OrderNotificationMessage}
     * @return an OrderNotificationMessage with the necessary values from the PaymentService.
     */
    @Override
    public OrderNotificationMessage convert(final PaymentService source, final OrderNotificationMessage target) {
        final OrderStatusEvent orderStatusEvent = getOrderStatusEvent(source);

        target.setOrderCode(orderStatusEvent.getOrderCode());
        target.setMerchantCode(source.getMerchantCode());

        final ServiceResponseTransformerHelper responseTransformerHelper = getServiceResponseTransformerHelper();

        final Payment intPayment = orderStatusEvent.getPayment();
        final PaymentReply paymentReply = responseTransformerHelper.buildPaymentReply(intPayment);
        target.setPaymentReply(paymentReply);

        final Journal intJournal = orderStatusEvent.getJournal();
        final JournalReply journalReply = responseTransformerHelper.buildJournalReply(intJournal);
        target.setJournalReply(journalReply);

        final Token intToken = orderStatusEvent.getToken();
        if (intToken != null) {
            final TokenReply tokenReply = responseTransformerHelper.buildTokenReply(intToken);
            target.setTokenReply(tokenReply);
        }

        final ShopperWebformRefundDetails intShopperWebformRefundDetails = orderStatusEvent.getShopperWebformRefundDetails();
        if (intShopperWebformRefundDetails != null) {
            final WebformRefundReply webformRefundReply = responseTransformerHelper.buildWebformRefundReply(intShopperWebformRefundDetails);
            target.setWebformRefundReply(webformRefundReply);
        }

        return target;
    }

    protected OrderStatusEvent getOrderStatusEvent(final PaymentService source) {
        return (OrderStatusEvent) ((Notify) source
                .getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0))
                .getOrderStatusEventOrReport().get(0);
    }

    protected ServiceResponseTransformerHelper getServiceResponseTransformerHelper() {
        return ServiceResponseTransformerHelper.getInstance();
    }
}
