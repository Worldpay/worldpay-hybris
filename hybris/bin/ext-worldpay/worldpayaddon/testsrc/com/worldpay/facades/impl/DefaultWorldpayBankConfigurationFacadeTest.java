package com.worldpay.facades.impl;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.core.services.WorldpayBankConfigurationLookupService;
import com.worldpay.facades.BankConfigurationData;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayBankConfigurationFacadeTest {

    private static final String APM_CODE = "apmCode";
    public static final String BANK_TRANSFER_APM = "bankTransferAPM";
    public static final String PAYMENT_METHOD = "payment";

    @InjectMocks
    private DefaultWorldpayBankConfigurationFacade testObj = new DefaultWorldpayBankConfigurationFacade();
    @Mock
    private WorldpayBankConfigurationLookupService worldpayBankConfigurationLookupServiceMock;
    @Mock
    private Converter<WorldpayBankConfigurationModel, BankConfigurationData> converterMock;
    @Mock
    private WorldpayBankConfigurationModel worldpayBankConfigurationModelMock;
    @Mock
    private BankConfigurationData bankConfigurationDataMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupServiceMock;
    @Mock
    private com.worldpay.model.WorldpayAPMConfigurationModel apmConfigurationModelMock;

    @Test
    public void testGetBankConfigurationForAPMCode() throws Exception {
        when(converterMock.convert(eq(worldpayBankConfigurationModelMock))).thenReturn(bankConfigurationDataMock);
        when(worldpayBankConfigurationLookupServiceMock.getActiveBankConfigurationsForCode(APM_CODE)).thenReturn(singletonList(worldpayBankConfigurationModelMock));

        final List<BankConfigurationData> bankConfigurationForAPMCode = testObj.getBankConfigurationForAPMCode(APM_CODE);

        assertTrue(bankConfigurationForAPMCode.contains(bankConfigurationDataMock));
    }

    @Test
    public void isBankTransferApmShouldReturnTrueIfPaymentMethodIsApmThatSupportsBankTransfer(){
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(BANK_TRANSFER_APM)).thenReturn(apmConfigurationModelMock);
        when(apmConfigurationModelMock.getBank()).thenReturn(true);
        assertTrue(testObj.isBankTransferApm(BANK_TRANSFER_APM));
    }

     @Test
    public void isBankTransferApmShouldReturnFalseIfPaymentMethodIsNotApmThatSupportsBankTransfer(){
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(APM_CODE)).thenReturn(apmConfigurationModelMock);
        when(apmConfigurationModelMock.getBank()).thenReturn(false);
        assertFalse(testObj.isBankTransferApm(APM_CODE));
    }

    @Test
    public void isBankTransferApmShouldReturnFalseIfPaymentMethodIsNotApm(){
        when(apmConfigurationLookupServiceMock.getAPMConfigurationForCode(PAYMENT_METHOD)).thenReturn(null);
        assertFalse(testObj.isBankTransferApm(PAYMENT_METHOD));
    }



}
    

