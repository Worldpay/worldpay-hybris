package com.worldpay.components.impl;

import de.hybris.platform.cockpit.session.UISessionUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;


public class HopResponseWindow extends Window {
    public HopResponseWindow() {
        setShadow(false);

        addEventListener("onCreate", event -> {
            if (UISessionUtils.getCurrentSession().getUser() == null) {
                Executions.sendRedirect("/");
            }
            getHopResponse().showResponse((Window) event.getTarget());
        });
    }

    private HopResponse getHopResponse() {
        return (HopResponse) SpringUtil.getBean("hopResponse");
    }
}
