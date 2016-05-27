package com.worldpay.renderers;

import com.worldpay.model.WorldpayCCComponentModel;
import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;

public class WorldpayCCComponentRenderer extends DefaultAddOnCMSComponentRenderer<WorldpayCCComponentModel> {

    @Override
    protected String getUIExperienceFolder() {
        if (ResponsiveUtils.isResponsive()) {
            return "responsive";
        }

        return super.getUIExperienceFolder();
    }
}
