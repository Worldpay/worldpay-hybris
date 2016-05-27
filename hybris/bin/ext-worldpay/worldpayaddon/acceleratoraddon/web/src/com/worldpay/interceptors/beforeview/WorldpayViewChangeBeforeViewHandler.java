package com.worldpay.interceptors.beforeview;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


public class WorldpayViewChangeBeforeViewHandler implements BeforeViewHandlerAdaptee {

    private Map<String, String> viewMap;

    /**
     * {@inheritDoc}
     */
    @Override
    public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model, final String viewName) {
        if (viewName != null && viewMap.containsKey(viewName)) {
            return viewMap.get(viewName);
        }
        return viewName;
    }

    @Required
    public void setViewMap(final Map<String, String> viewMap) {
        this.viewMap = viewMap;
    }
}
