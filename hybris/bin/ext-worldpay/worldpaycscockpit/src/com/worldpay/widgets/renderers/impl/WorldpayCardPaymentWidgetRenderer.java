package com.worldpay.widgets.renderers.impl;

import com.worldpay.widgets.controllers.WorldpayCardPaymentController;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.cscockpit.widgets.impl.CardPaymentWidget;
import de.hybris.platform.cscockpit.widgets.renderers.impl.CardPaymentWidgetRenderer;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class WorldpayCardPaymentWidgetRenderer extends CardPaymentWidgetRenderer {

    private static final Logger LOG = Logger.getLogger(WorldpayCardPaymentWidgetRenderer.class);

    protected static final String IFRAME_BASE_URL = "/worldpaycscockpit/checkout/hopIFrame.zul?";
    protected static final String IFRAME_HEIGHT = "480px";
    protected static final String IFRAME_WIDTH = "600px";
    protected static final String CONTENT_HEIGHT = "500px";
    protected static final String CONTENT_WIDTH = "600px";
    protected static final String QUERY_PARAM_POST_URL = "postUrl";
    protected static final String QUERY_PARAM_DEBUG_MODE = "debugMode";
    protected static final String ENCODING = "UTF-8";

    private boolean debugMode;

    /**
     * Create the content based on the checkout PCI strategy
     * <p>
     * If the checkout PCI strategy is HOP then create an iFrame and load the hopIFrame.zul file into it. This allows the
     * parameters that are passed to it to be sent via POST to the HOP post Url. This in turn will ensure that the HOP
     * page is correctly loaded into the iFrame
     * </p>
     * <p>
     * If the checkout PCI strategy is PCI then the standard form can be loaded to capture credit card information
     * </p>
     *
     * @see de.hybris.platform.cscockpit.widgets.renderers.impl.CardPaymentWidgetRenderer#createContentInternal(de.hybris.
     * platform.cscockpit.widgets.impl.CardPaymentWidget, org.zkoss.zk.ui.api.HtmlBasedComponent)
     */
    @Override
    protected HtmlBasedComponent createContentInternal(final CardPaymentWidget widget, final HtmlBasedComponent rootContainer) {
        final HtmlBasedComponent content = new Div();
        content.setVisible(true);
        content.setHeight(CONTENT_HEIGHT);
        content.setWidth(CONTENT_WIDTH);

        final WorldpayCardPaymentController controller = (WorldpayCardPaymentController) widget.getWidgetController();
        final PaymentData paymentData = controller.getPaymentData();

        final Iframe iFrame = new Iframe(IFRAME_BASE_URL + buildParams(paymentData));
        iFrame.setParent(content);
        iFrame.setHeight(IFRAME_HEIGHT);
        iFrame.setWidth(IFRAME_WIDTH);

        return content;
    }

    /**
     * Build parameters to be added to the hopIFrame url
     *
     * @param paymentData
     * @return parameter String
     */
    private String buildParams(final PaymentData paymentData) {
        final StringBuilder paramBuffer = new StringBuilder(30);
        try {
            paramBuffer.append(QUERY_PARAM_POST_URL + "=").append(URLEncoder.encode(paymentData.getPostUrl(), ENCODING)).append("&");
            final Map<String, String> parameters = paymentData.getParameters();
            final Set<Entry<String, String>> entrySet = parameters.entrySet();
            for (final Entry<String, String> entry : entrySet) {
                paramBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), ENCODING)).append("&");
            }
            paramBuffer.append(QUERY_PARAM_DEBUG_MODE + "=").append(URLEncoder.encode("" + isDebugMode(), ENCODING));
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Error encoding URL: " + paymentData, e);
        }
        return paramBuffer.toString();
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(final boolean debugMode) {
        this.debugMode = debugMode;
    }
}
