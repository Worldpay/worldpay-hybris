package com.worldpay.voidprocess.listener;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.events.CancelFinishedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import java.text.MessageFormat;

public class WorldpayCancelFinishedEventListener extends AbstractEventListener<CancelFinishedEvent> {

    protected static final String WORLDPAY_VOID_PROCESS_NAME = "worldpay-void-process";

    private BusinessProcessService businessProcessService;
    private ModelService modelService;

    @Override
    protected void onEvent(final CancelFinishedEvent cancelFinishedEvent) {
        final OrderModel order = cancelFinishedEvent.getCancelRequestRecordEntry().getModificationRecord().getOrder();
        final String businessProcessId = MessageFormat.format("{0}-{1}-{2}", WORLDPAY_VOID_PROCESS_NAME, order.getCode(), getCurrentTimeInMillis());
        final OrderProcessModel voidOrderProcess = businessProcessService.createProcess(businessProcessId, WORLDPAY_VOID_PROCESS_NAME);
        voidOrderProcess.setOrder(order);
        modelService.save(voidOrderProcess);
        businessProcessService.startProcess(voidOrderProcess);
    }

    protected long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }

    @Required
    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
