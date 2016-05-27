package com.worldpay.hostedorderpage.converters;

import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class RedirectAuthoriseResultPopulatorTest {

    private static final String ORDER_CODE = "00002000-1431098192721";
    private static final String MERCHANTCODE = "MERCHANTCODE";
    private static final String MERCHANT_OWNER = "MERCHANT_OWNER";
    private static final String SEPARATOR = "^";
    private static final String ORDER_KEY = MERCHANT_OWNER + SEPARATOR + MERCHANTCODE + SEPARATOR + ORDER_CODE;
    private static final String STATUS = "AUTHORISED";

    @InjectMocks
    private RedirectAuthoriseResultPopulator testObj = new RedirectAuthoriseResultPopulator();

    private final Map<String, String> paymentStatusSourceMap = new HashMap<>();
    private final Map<String, String> statusSourceMap = new HashMap<>();

    @Test
    public void shouldPopulateRedirectResultForPaymentStatusParameter() {
        populateParametersMap();
        final RedirectAuthoriseResult result = new RedirectAuthoriseResult();

        testObj.populate(paymentStatusSourceMap, result);

        assertEquals(ORDER_KEY, result.getOrderKey());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(STATUS, result.getPaymentStatus());
        assertEquals(true, result.getSaveCard());
        assertEquals(false, result.getPending());
    }

    @Test
    public void shouldNotPopulateOrderKeyInRedirectResultForPaymentStatusParameterWhenIsNotPresent() {
        final RedirectAuthoriseResult result = new RedirectAuthoriseResult();

        testObj.populate(paymentStatusSourceMap, result);

        assertEquals(null, result.getOrderKey());
        assertEquals(null, result.getOrderCode());
        assertEquals(null, result.getPaymentStatus());
    }

    @Test
    public void shouldPopulateRedirectResultForStatusParameter() {
        populateParametersMap();
        final RedirectAuthoriseResult result = new RedirectAuthoriseResult();

        testObj.populate(statusSourceMap, result);

        assertEquals(ORDER_KEY, result.getOrderKey());
        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(STATUS, result.getPaymentStatus());
        assertEquals(false, result.getSaveCard());
        assertEquals(false, result.getPending());
    }

    protected void populateParametersMap() {
        paymentStatusSourceMap.put("orderKey", ORDER_KEY);
        paymentStatusSourceMap.put("paymentStatus", STATUS);
        paymentStatusSourceMap.put("savePaymentInfo", String.valueOf(TRUE));

        statusSourceMap.put("orderKey", ORDER_KEY);
        statusSourceMap.put("status", STATUS);
        statusSourceMap.put("savePaymentInfo", String.valueOf(FALSE));
    }
}
