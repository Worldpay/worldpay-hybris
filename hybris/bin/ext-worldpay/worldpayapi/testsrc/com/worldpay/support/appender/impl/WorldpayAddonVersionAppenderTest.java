package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.support.appender.impl.WorldpayAddonVersionAppender.WORLDPAY_ADDON_VERSION_KEY;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayAddonVersionAppenderTest {

    public static final String ADDON_VERSION = "addonVersion";
    @Mock (answer = RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;

    @InjectMocks
    private WorldpayAddonVersionAppender testObj = new WorldpayAddonVersionAppender();

    @Test
    public void shouldReturnAddonVersionContentToAppend(){
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_ADDON_VERSION_KEY)).thenReturn(ADDON_VERSION);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Worldpay Plugin Version: " + ADDON_VERSION));
    }

    @Test
    public void shouldReturnNoContentToAppendWhenAddonVersionIsUnknown(){
        when(configurationServiceMock.getConfiguration().getString(WORLDPAY_ADDON_VERSION_KEY)).thenReturn(EMPTY);

        final String result = testObj.appendContent();

        assertTrue(result.isEmpty());
    }
}