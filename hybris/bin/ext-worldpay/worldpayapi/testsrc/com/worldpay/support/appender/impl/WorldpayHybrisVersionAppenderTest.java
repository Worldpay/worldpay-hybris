package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.support.appender.impl.WorldpayHybrisVersionAppender.HYBRIS_BUILD_VERSION_KEY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayHybrisVersionAppenderTest {

    private static final String HYBRIS_VERSION_INFO = "hybrisVersionInfo";

    @InjectMocks
    private WorldpayHybrisVersionAppender testObj = new WorldpayHybrisVersionAppender();

    @Mock
    private ConfigurationService configurationServiceMock;
    @Mock
    private Configuration configurationMock;

    @Test
    public void testAppendContent() throws Exception {
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(HYBRIS_BUILD_VERSION_KEY)).thenReturn(HYBRIS_VERSION_INFO);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Hybris version: " + HYBRIS_VERSION_INFO));
    }
}
    

