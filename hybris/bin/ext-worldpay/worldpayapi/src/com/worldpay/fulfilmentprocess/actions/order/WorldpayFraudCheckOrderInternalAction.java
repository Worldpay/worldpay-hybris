package com.worldpay.fulfilmentprocess.actions.order;

import static de.hybris.platform.basecommerce.enums.FraudStatus.CHECK;
import static de.hybris.platform.basecommerce.enums.FraudStatus.OK;

import java.text.MessageFormat;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.FraudService;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class WorldpayFraudCheckOrderInternalAction extends WorldpayAbstractFraudCheckAction<OrderProcessModel> {

    private static final Logger LOG = Logger.getLogger(WorldpayFraudCheckOrderInternalAction.class);

    private FraudService fraudService;
    private String providerName;

    @Override
    public Transition executeAction(final OrderProcessModel process) {
        ServicesUtil.validateParameterNotNull(process, "Process can not be null");
        ServicesUtil.validateParameterNotNull(process.getOrder(), "Order can not be null");

        final OrderModel order = process.getOrder();
        final FraudServiceResponse response = getFraudService().recognizeOrderSymptoms(getProviderName(), order);

        if (CollectionUtils.isEmpty(response.getSymptoms())) {
            final FraudReportModel fraudReport = createFraudReport(providerName, response, order, OK);
            final OrderHistoryEntryModel historyEntry = createHistoryLog(providerName, order, OK, null);
            order.setFraudulent(Boolean.FALSE);
            order.setPotentiallyFraudulent(Boolean.FALSE);
            order.setStatus(OrderStatus.FRAUD_CHECKED);
            modelService.save(fraudReport);
            modelService.save(historyEntry);
            modelService.save(order);
            return Transition.OK;
        } else {
            LOG.warn(MessageFormat.format("The order [{0}] is potentially fraudulent. Sent into Fraud Review state", order.getCode()));
            final FraudReportModel fraudReport = createFraudReport(providerName, response, order, CHECK);
            final OrderHistoryEntryModel historyEntry = createHistoryLog(providerName, order, CHECK,
                    fraudReport.getCode());
            order.setFraudulent(Boolean.FALSE);
            order.setPotentiallyFraudulent(Boolean.TRUE);
            order.setStatus(OrderStatus.FRAUD_CHECKED);
            modelService.save(fraudReport);
            modelService.save(historyEntry);
            modelService.save(order);
            return Transition.POTENTIAL;
        }
    }

    public FraudService getFraudService() {
        return fraudService;
    }

    @Required
    public void setFraudService(final FraudService fraudService) {
        this.fraudService = fraudService;
    }

    public String getProviderName() {
        return providerName;
    }

    @Required
    public void setProviderName(final String providerName) {
        this.providerName = providerName;
    }
}

