package com.worldpay.components.impl;

import de.hybris.platform.cockpit.session.UISessionUtils;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;


public class HopCancelWindow extends Window {
    public HopCancelWindow() {
        setShadow(false);
        addEventListener("onCreate", event -> {
            if (UISessionUtils.getCurrentSession().getUser() == null) {
                Executions.sendRedirect("/");
            }
            final Div div = new Div();
            Text text = new Text("Payment has been cancelled - please close the window");

            text.setParent(div);
            text.setVisible(true);
            div.setVisible(true);
            div.setParent(event.getTarget());
        });
    }

}
