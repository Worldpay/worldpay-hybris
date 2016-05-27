package com.worldpay.worldpayapi.hmc;

import com.worldpay.support.WorldpaySupportService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.hmc.util.action.ActionEvent;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.util.action.ToolbarAction;
import de.hybris.platform.jalo.JaloBusinessException;

public class WorldpayAddonInfoToolbarAction extends ToolbarAction {

    @Override
    public ActionResult perform(final ActionEvent actionEvent) throws JaloBusinessException {
        getWorldpaySupportService().sendSupportEmail();
        return new ActionResult(ActionResult.OK, "The email has been sent", false);
    }

    protected WorldpaySupportService getWorldpaySupportService() {
        return Registry.getApplicationContext().getBean("worldpaySupportService", WorldpaySupportService.class);
    }
}



