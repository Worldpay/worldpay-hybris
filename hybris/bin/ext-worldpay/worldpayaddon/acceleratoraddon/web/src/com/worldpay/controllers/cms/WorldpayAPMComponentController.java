package com.worldpay.controllers.cms;

import com.worldpay.facades.APMAvailabilityFacade;
import com.worldpay.model.WorldpayAPMComponentModel;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import de.hybris.platform.addonsupport.controllers.cms.GenericCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller("WorldpayAPMComponentController")
@RequestMapping(value = "/view/WorldpayAPMComponentController")
public class WorldpayAPMComponentController extends GenericCMSAddOnComponentController {

    protected static final String IS_AVAILABLE = "isAvailable";

    @Resource
    private APMAvailabilityFacade apmAvailabilityFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        invokeSuperFillModel(request, model, component);
        if (component instanceof WorldpayAPMComponentModel) {
            final WorldpayAPMConfigurationModel apmConfiguration = ((WorldpayAPMComponentModel) component).getApmConfiguration();
            model.addAttribute(IS_AVAILABLE, apmAvailabilityFacade.isAvailable(apmConfiguration));
        }
    }

    protected void invokeSuperFillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        super.fillModel(request, model, component);
    }
}
