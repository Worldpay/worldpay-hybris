package com.worldpay.service.impl;

import com.worldpay.exception.WorldpayConfigurationException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.service.impl.DefaultCsWorldpayUrlService.WORLDPAY_CSCOCKPIT_PREFIX;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultCsWorldpayUrlServiceTest {

    private static final String WORLDPAY_CS_COCKPIT_PREFIX = "worldpayCsCockpitPrefix";
    private static final String SUCCESS_PATH = "successPath";
    private static final String FAILURE_PATH = "failurePath";
    private static final String PENDING_PATH = "pendingPath";
    private static final String CANCEL_PATH = "cancelPath";

    @InjectMocks
    private DefaultCsWorldpayUrlService testObj = new DefaultCsWorldpayUrlService();

    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationService;

    @Before
    public void setUp() {
        when(configurationService.getConfiguration().getString(WORLDPAY_CSCOCKPIT_PREFIX)).thenReturn(WORLDPAY_CS_COCKPIT_PREFIX);

        testObj.setSuccessPath(SUCCESS_PATH);
        testObj.setFailurePath(FAILURE_PATH);
        testObj.setPendingPath(PENDING_PATH);
        testObj.setCancelPath(CANCEL_PATH);
    }

    @Test
    public void testGetFullSuccessUrl() throws WorldpayConfigurationException {
        final String result = testObj.getFullSuccessURL();

        assertNotNull(result);
        assertEquals(WORLDPAY_CS_COCKPIT_PREFIX + SUCCESS_PATH, result);
    }

    @Test
    public void testGetFullFailureUrl() throws WorldpayConfigurationException {
        final String result = testObj.getFullFailureURL();

        assertNotNull(result);
        assertEquals(WORLDPAY_CS_COCKPIT_PREFIX + FAILURE_PATH, result);
    }

    @Test
    public void testGetFullPendingUrl() throws WorldpayConfigurationException {
        final String result = testObj.getFullPendingURL();

        assertNotNull(result);
        assertEquals(WORLDPAY_CS_COCKPIT_PREFIX + PENDING_PATH, result);
    }

    @Test
    public void testGetFullCancelUrl() throws WorldpayConfigurationException {
        final String result = testObj.getFullCancelURL();

        assertNotNull(result);
        assertEquals(WORLDPAY_CS_COCKPIT_PREFIX + CANCEL_PATH, result);
    }

    @Test (expected = WorldpayConfigurationException.class)
    public void testGetFullUrlShouldThrowIllegalArgumentExceptionWhenPathPrefixIsNull() throws WorldpayConfigurationException {
        when(configurationService.getConfiguration().getString(WORLDPAY_CSCOCKPIT_PREFIX)).thenReturn(null);

        testObj.getFullSuccessURL();
    }

    @Test (expected = WorldpayConfigurationException.class)
    public void testGetFullUrlShouldThrowIllegalArgumentExceptionWhenPathPrefixIsEmpty() throws WorldpayConfigurationException {
        when(configurationService.getConfiguration().getString(WORLDPAY_CSCOCKPIT_PREFIX)).thenReturn(EMPTY);

        testObj.getFullSuccessURL();
    }
}