package com.worldpay.config.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.exception.WorldpayConfigurationException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.config.impl.DefaultWorldpayConfigLookupService.WORLDPAY_CONFIG_ENDPOINT;
import static com.worldpay.config.impl.DefaultWorldpayConfigLookupService.WORLDPAY_CONFIG_ENVIRONMENT;
import static com.worldpay.config.impl.DefaultWorldpayConfigLookupService.WORLDPAY_CONFIG_VERSION;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayConfigLookupServiceTest {

    private static final String VERSION = "1.4";
    private static final String ENDPOINT = "endpoint";
    private static final String ROLE = "TEST";

    @InjectMocks
    private DefaultWorldpayConfigLookupService testObj = new DefaultWorldpayConfigLookupService();

    @Mock (answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    @Test
    public void testLookupConfigExistingValues() throws Exception {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_ENVIRONMENT)).thenReturn(ROLE);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_ENDPOINT + "." + ROLE)).thenReturn(ENDPOINT);


        final WorldpayConfig result = testObj.lookupConfig();

        assertEquals(VERSION, result.getVersion());
        assertEquals(ENDPOINT, result.getEnvironment().getEndpoint());
    }

    @Test (expected = WorldpayConfigurationException.class)
    public void testLookupConfigNonExistingValues() throws Exception {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_ENDPOINT)).thenReturn(null);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(VERSION);

        testObj.lookupConfig();
    }

    @Test (expected = WorldpayConfigurationException.class)
    public void testLookupConfigNonExistingEnvironmentValue() throws Exception {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_ENDPOINT)).thenReturn(ENDPOINT);
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_CONFIG_VERSION)).thenReturn(null);

        testObj.lookupConfig();
    }
}