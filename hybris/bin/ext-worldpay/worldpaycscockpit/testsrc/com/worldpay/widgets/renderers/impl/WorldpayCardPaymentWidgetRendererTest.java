package com.worldpay.widgets.renderers.impl;

import com.worldpay.widgets.controllers.WorldpayCardPaymentController;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.cscockpit.widgets.impl.CardPaymentWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zul.Iframe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.CONTENT_HEIGHT;
import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.CONTENT_WIDTH;
import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.ENCODING;
import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.IFRAME_BASE_URL;
import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.IFRAME_HEIGHT;
import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.IFRAME_WIDTH;
import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.QUERY_PARAM_DEBUG_MODE;
import static com.worldpay.widgets.renderers.impl.WorldpayCardPaymentWidgetRenderer.QUERY_PARAM_POST_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCardPaymentWidgetRendererTest {

    private static final String POST_URL = "http://domain.com";

    private WorldpayCardPaymentWidgetRenderer testObj = new WorldpayCardPaymentWidgetRenderer();

    @Mock
    private CardPaymentWidget widgetMock;
    @Mock
    private HtmlBasedComponent containerMock;
    @Mock
    private WorldpayCardPaymentController paymentControllerMock;
    @Mock
    private PaymentData paymentDataMock;

    @Before
    public void setup() {
        when(widgetMock.getWidgetController()).thenReturn(paymentControllerMock);
        when(paymentControllerMock.getPaymentData()).thenReturn(paymentDataMock);
        when(paymentDataMock.getPostUrl()).thenReturn(POST_URL);
    }

    @Test
    public void createContentInternalCreatesHtmlBasedComponent() throws UnsupportedEncodingException {
        final HtmlBasedComponent result = testObj.createContentInternal(widgetMock, containerMock);

        assertTrue(result.isVisible());
        assertEquals(CONTENT_HEIGHT, result.getHeight());
        assertEquals(CONTENT_WIDTH, result.getWidth());
        assertTrue(result.getChildren().size() == 1);

        final Iframe iframe = (Iframe) result.getFirstChild();
        assertEquals(IFRAME_HEIGHT, iframe.getHeight());
        assertEquals(IFRAME_WIDTH, iframe.getWidth());
        assertEquals(IFRAME_BASE_URL + QUERY_PARAM_POST_URL + "=" + URLEncoder.encode(POST_URL, ENCODING) + "&" + QUERY_PARAM_DEBUG_MODE + "=false", iframe.getSrc());
    }

    @Test
    public void createContentInternalCreatesHtmlBasedComponentWhenDebugModeIsOn() throws UnsupportedEncodingException {
        testObj.setDebugMode(true);

        final HtmlBasedComponent result = testObj.createContentInternal(widgetMock, containerMock);

        assertTrue(result.isVisible());
        assertEquals(CONTENT_HEIGHT, result.getHeight());
        assertEquals(CONTENT_WIDTH, result.getWidth());
        assertTrue(result.getChildren().size() == 1);

        final Iframe iframe = (Iframe) result.getFirstChild();
        assertEquals(IFRAME_HEIGHT, iframe.getHeight());
        assertEquals(IFRAME_WIDTH, iframe.getWidth());
        assertEquals(IFRAME_BASE_URL + QUERY_PARAM_POST_URL + "=" + URLEncoder.encode(POST_URL, ENCODING) + "&" + QUERY_PARAM_DEBUG_MODE + "=true", iframe.getSrc());
    }
}