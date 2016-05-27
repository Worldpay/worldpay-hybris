package com.worldpay.service.apm.strategy.impl;

import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class APMAvailabilityBankStrategyTest {

    public static final String APM_CODE = "apmCode";
    @InjectMocks
    private APMAvailabilityBankStrategy testObj = new APMAvailabilityBankStrategy();
    @Mock
    private WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupServiceMock;

    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private WorldpayBankConfigurationModel worldpayBankConfigurationModel;

    @Test
    public void testIsAvailableWhenAPMIsBankAndThereAreBanksConfigured() throws Exception {
        when(apmConfigurationMock.getBank()).thenReturn(Boolean.TRUE);
        when(apmConfigurationMock.getCode()).thenReturn(APM_CODE);
        when(worldpayBankConfigurationLookupServiceMock.getActiveBankConfigurationsForCode(APM_CODE)).thenReturn(Collections.singletonList(worldpayBankConfigurationModel));

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }

    @Test
    public void testIsAvailableWhenAPMIsBankAndThereAreNoBanksConfigured() throws Exception {
        when(apmConfigurationMock.getBank()).thenReturn(Boolean.TRUE);
        when(apmConfigurationMock.getCode()).thenReturn(APM_CODE);
        when(worldpayBankConfigurationLookupServiceMock.getActiveBankConfigurationsForCode(APM_CODE)).thenReturn(Collections.emptyList());

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertFalse(result);
    }

    @Test
    public void testIsAvailableWhenAPMIsNotBank() throws Exception {
        when(apmConfigurationMock.getBank()).thenReturn(Boolean.FALSE);

        final boolean result = testObj.isAvailable(apmConfigurationMock, cartModelMock);

        assertTrue(result);
    }
}
    

