package com.worldpay.converters.populators;

import com.worldpay.facades.BankConfigurationData;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayBankConfigurationPopulatorTest {

    private WorldpayBankConfigurationPopulator testObj = new WorldpayBankConfigurationPopulator();
    @Mock
    private BankConfigurationData bankConfigurationDataMock;
    @Mock
    private WorldpayBankConfigurationModel worlpayBankConfigurationModelMock;

    @Test
    public void populateShouldAddBankCodeToBankConfiguration() throws Exception {
        testObj.populate(worlpayBankConfigurationModelMock, bankConfigurationDataMock);

        verify(bankConfigurationDataMock).setBankCode(worlpayBankConfigurationModelMock.getCode());
    }

    @Test
    public void populateShouldAddBankNameToBankConfiguration() throws Exception {
        testObj.populate(worlpayBankConfigurationModelMock, bankConfigurationDataMock);

        verify(bankConfigurationDataMock).setBankName(worlpayBankConfigurationModelMock.getName());
    }
}