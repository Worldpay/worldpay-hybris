package com.worldpay.attributehandlers;

import com.worldpay.core.services.APMConfigurationLookupService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayPaymentInfoIsApmHandlerTest {

    public static final String PAYMENT_1 = "payment1";
    public static final String PAYMENT_2 = "payment2";
    public static final String NOT_APM = "notAPM";
    @InjectMocks
    private WorldpayPaymentInfoIsApmHandler testObj = new WorldpayPaymentInfoIsApmHandler();
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;

    @Test
    public void testGetShouldReturnTrueWhenPaymentTypeIsAPM() throws Exception {
        when(apmConfigurationLookupServiceMock.getAllApmPaymentTypeCodes()).thenReturn(new HashSet<>(Arrays.asList(PAYMENT_1, PAYMENT_2)));
        when(paymentInfoModelMock.getPaymentType()).thenReturn(PAYMENT_1);

        final Boolean result = testObj.get(paymentInfoModelMock);

        assertTrue(result);
    }

    @Test
    public void testGetShouldReturnFalseWhenPaymentTypeIsNotAPM() throws Exception {
        when(apmConfigurationLookupServiceMock.getAllApmPaymentTypeCodes()).thenReturn(new HashSet<>(Arrays.asList(PAYMENT_1, PAYMENT_2)));
        when(paymentInfoModelMock.getPaymentType()).thenReturn(NOT_APM);

        final Boolean result = testObj.get(paymentInfoModelMock);

        assertFalse(result);
    }
}