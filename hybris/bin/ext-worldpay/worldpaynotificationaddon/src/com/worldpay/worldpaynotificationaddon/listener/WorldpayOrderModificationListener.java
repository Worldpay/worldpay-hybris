package com.worldpay.worldpaynotificationaddon.listener;

import com.worldpay.core.event.OrderModificationEvent;
import com.worldpay.core.services.WorldpayCartService;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.util.OrderModificationSerialiser;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.Map;

import static com.worldpay.service.model.AuthorisedStatus.REFUSED;

public class WorldpayOrderModificationListener extends AbstractEventListener<OrderModificationEvent> {

    private static final Logger LOG = Logger.getLogger(WorldpayOrderModificationListener.class);

    private ModelService modelService;
    private WorldpayCartService worldpayCartService;
    private Map<AuthorisedStatus, PaymentTransactionType> paymentTransactionTypeMap;
    private OrderModificationSerialiser orderModificationSerialiser;

    @Override
    protected void onEvent(OrderModificationEvent orderModificationEvent) {
        final OrderNotificationMessage orderNotificationMessage = orderModificationEvent.getOrderNotificationMessage();
        final AuthorisedStatus journalType = orderNotificationMessage.getJournalReply().getJournalType();
        if (journalType.equals(REFUSED)) {
            setDeclineCodeInCart(orderNotificationMessage);
        }
        final PaymentTransactionType transactionType = paymentTransactionTypeMap.get(journalType);

        if (transactionType != null) {
            saveOrderModification(orderNotificationMessage, transactionType);
        } else {
            LOG.warn(MessageFormat.format("Transaction type {0} not supported. Ignoring event.", journalType));
        }
    }

    protected void setDeclineCodeInCart(final OrderNotificationMessage orderNotificationMessage) {
        final int returnCode = orderNotificationMessage.getPaymentReply().getReturnCode();
        if (returnCode != 0) {
            worldpayCartService.setWorldpayDeclineCodeOnCart(orderNotificationMessage.getOrderCode(), returnCode);
        }
    }

    protected void saveOrderModification(OrderNotificationMessage orderNotificationMessage, PaymentTransactionType transactionType) {
        final String worldpayOrderCode = orderNotificationMessage.getOrderCode();
        LOG.info(MessageFormat.format("Saving worldpayOrderModificationModel for worldpay order code: {0}", worldpayOrderCode));
        final WorldpayOrderModificationModel worldpayOrderModificationModel = modelService.create(WorldpayOrderModificationModel.class);
        worldpayOrderModificationModel.setType(transactionType);
        worldpayOrderModificationModel.setWorldpayOrderCode(worldpayOrderCode);
        worldpayOrderModificationModel.setOrderNotificationMessage(orderModificationSerialiser.serialise(orderNotificationMessage));
        modelService.save(worldpayOrderModificationModel);
    }

    @Required
    public void setPaymentTransactionTypeMap(Map<AuthorisedStatus, PaymentTransactionType> paymentTransactionTypeMap) {
        this.paymentTransactionTypeMap = paymentTransactionTypeMap;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setOrderModificationSerialiser(OrderModificationSerialiser orderModificationSerialiser) {
        this.orderModificationSerialiser = orderModificationSerialiser;
    }

    @Required
    public void setWorldpayCartService(WorldpayCartService worldpayCartService) {
        this.worldpayCartService = worldpayCartService;
    }
}
