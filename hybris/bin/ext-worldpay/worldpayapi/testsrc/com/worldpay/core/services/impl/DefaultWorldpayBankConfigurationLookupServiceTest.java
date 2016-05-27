package com.worldpay.core.services.impl;

import com.worldpay.core.dao.WorldpayBankConfigurationDao;
import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.jalo.WorldpayBankConfiguration;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.model.WorldpayBankConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayBankConfigurationLookupServiceTest {

    public static final String APM_CODE = "apmCode";
    @InjectMocks
    private DefaultWorldpayBankConfigurationLookupService testObj = new DefaultWorldpayBankConfigurationLookupService();

    @Mock
    private WorldpayBankConfigurationDao worldpayBankConfigurationDao;
    @Mock
    private APMConfigurationLookupService apmConfigurationLookupService;
    @Mock
    private WorldpayAPMConfigurationModel apmConfigurationModelMock;
    @Mock
    private WorldpayBankConfigurationModel worldpayBankConfigurationModelMock;
    @Captor
    private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

    @Test
    public void testGetActiveBankConfigurationsForCode() throws Exception {
        when(apmConfigurationLookupService.getAPMConfigurationForCode(APM_CODE)).thenReturn(apmConfigurationModelMock);
        when(worldpayBankConfigurationDao.find(anyMapOf(String.class, Object.class))).thenReturn(singletonList(worldpayBankConfigurationModelMock));

        final List<WorldpayBankConfigurationModel> activeBankConfigurationsForCode = testObj.getActiveBankConfigurationsForCode(APM_CODE);

        verify(worldpayBankConfigurationDao).find(mapArgumentCaptor.capture());
        final Map<String, Object> value = mapArgumentCaptor.getValue();
        assertEquals(apmConfigurationModelMock, value.get(WorldpayBankConfiguration.APM));
        assertEquals(Boolean.TRUE, value.get(WorldpayBankConfiguration.ACTIVE));
        
        assertTrue(activeBankConfigurationsForCode.contains(worldpayBankConfigurationModelMock));
    }
}
    

