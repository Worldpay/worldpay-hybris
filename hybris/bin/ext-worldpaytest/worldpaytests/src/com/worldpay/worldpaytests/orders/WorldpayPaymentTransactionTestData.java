package com.worldpay.worldpaytests.orders;

import com.worldpay.service.model.JournalReply;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;

public class WorldpayPaymentTransactionTestData {

    protected static final String MERCHANT_CODE = "merchantCode";

    private ModelService modelService;
    private OrderModificationSerialiser orderModificationSerialiser;

    public void setRequestIdsAndCreateOrderModifications(final CustomerModel user) {
        user.getOrders().forEach(orderModel -> orderModel.getPaymentTransactions().forEach(paymentTransactionModel -> {
            final String requestId = "pt_" + orderModel.getCode();
            paymentTransactionModel.setRequestId(requestId);
            paymentTransactionModel.getEntries().forEach(paymentTransactionEntryModel -> {
                paymentTransactionEntryModel.setRequestId(requestId);
                createOrderModification(requestId);
                modelService.save(paymentTransactionEntryModel);
            });
            modelService.save(paymentTransactionModel);
        }));
    }

    protected void createOrderModification(final String requestId) {
        final WorldpayOrderModificationModel orderModification = modelService.create(WorldpayOrderModificationModel.class);
        orderModification.setWorldpayOrderCode(requestId);
        orderModification.setType(AUTHORIZATION);

        final OrderNotificationMessage orderNotificationMessage = new OrderNotificationMessage();
        orderNotificationMessage.setOrderCode(requestId);
        orderNotificationMessage.setMerchantCode(MERCHANT_CODE);
        final JournalReply journalReply = new JournalReply();
        journalReply.setJournalType(AUTHORISED);

        orderNotificationMessage.setJournalReply(journalReply);
        final PaymentReply paymentReply = new PaymentReply();
        orderNotificationMessage.setPaymentReply(paymentReply);

        orderModification.setOrderNotificationMessage(orderModificationSerialiser.serialise(orderNotificationMessage));
        modelService.save(orderModification);
    }

    @Required
    public void setOrderModificationSerialiser(OrderModificationSerialiser orderModificationSerialiser) {
        this.orderModificationSerialiser = orderModificationSerialiser;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }
}
