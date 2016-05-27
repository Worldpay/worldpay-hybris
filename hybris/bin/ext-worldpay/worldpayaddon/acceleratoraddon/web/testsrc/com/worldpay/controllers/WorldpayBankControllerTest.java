package com.worldpay.controllers;

import com.worldpay.facades.BankConfigurationData;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayBankControllerTest {

    private static final String APM_CODE = "apmCode";

    @InjectMocks
    private WorldpayBankController testObj = new WorldpayBankController();
    @Mock
    private WorldpayBankConfigurationFacade worldpayBankConfigurationFacadeMock;
    @Mock
    private BankConfigurationData bankConfigurationData;

    @Test
    public void testGetMerchantsBySite() throws Exception {
        final List<BankConfigurationData> bankConfigurations = Collections.singletonList(bankConfigurationData);
        when(worldpayBankConfigurationFacadeMock.getBankConfigurationForAPMCode(APM_CODE)).thenReturn(bankConfigurations);

        List<BankConfigurationData> result = testObj.getBanksForAPM(APM_CODE);

        assertEquals(bankConfigurations, result);
    }
}
    

