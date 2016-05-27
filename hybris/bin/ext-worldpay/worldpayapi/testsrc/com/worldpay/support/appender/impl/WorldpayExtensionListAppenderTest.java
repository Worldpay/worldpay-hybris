package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayExtensionListAppenderTest {

    private static final String EXTENSION_1 = "extension1";
    private static final String EXTENSION_2 = "extension2";

    @Spy
    @InjectMocks
    private WorldpayExtensionListAppender testObj = new WorldpayExtensionListAppender();

    @Mock
    private Tenant tenantMock;

    @Test
    public void testAppendContent() throws Exception {
        doReturn(Arrays.asList(EXTENSION_1, EXTENSION_2)).when(testObj).getTenantSpecificExtensionNames();
        final List<String> extensionNames = asList(EXTENSION_1, EXTENSION_2);
        when(tenantMock.getTenantSpecificExtensionNames()).thenReturn(extensionNames);

        final String result = testObj.appendContent();

        assertTrue(result.contains("Extensions"));
        for (final String extensionName : extensionNames) {
            assertTrue(result.contains(extensionName));
        }
    }
}
    

