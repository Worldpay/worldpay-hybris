package com.worldpay.strategies.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.strategies.WorldpayOrderModificationCleanUpStrategy;
import com.worldpay.util.WorldpayUtil;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;
import java.util.List;

/**
 * Default implementation of the {@link WorldpayOrderModificationCleanUpStrategy}.
 * <p>
 * Retrieves all processed Order Modifications created before the certain amount of time
 * and removes them from the database.
 * </p>
 */
public class DefaultWorldpayOrderModificationCleanUpStrategy implements WorldpayOrderModificationCleanUpStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultWorldpayOrderModificationCleanUpStrategy.class);

    private ModelService modelService;
    private OrderModificationDao orderModificationDao;

    /**
     * {@inheritDoc}
     *
     * @see WorldpayOrderModificationCleanUpStrategy#doCleanUp(int)
     */
    @Override
    public void doCleanUp(int days) {
        List<WorldpayOrderModificationModel> processedOrderModificationsBeforeDate = getOrderModificationDao().findProcessedOrderModificationsBeforeDate(WorldpayUtil.createDateInPast(days));
        processedOrderModificationsBeforeDate.stream().forEach(orderModificationModel -> {
            LOG.info(MessageFormat.format("Deleting Order Modification Model with Worldpay Order code: {0}", orderModificationModel.getWorldpayOrderCode()));
            getModelService().remove(orderModificationModel);
        });
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public OrderModificationDao getOrderModificationDao() {
        return orderModificationDao;
    }

    @Required
    public void setOrderModificationDao(OrderModificationDao orderModificationDao) {
        this.orderModificationDao = orderModificationDao;
    }
}
