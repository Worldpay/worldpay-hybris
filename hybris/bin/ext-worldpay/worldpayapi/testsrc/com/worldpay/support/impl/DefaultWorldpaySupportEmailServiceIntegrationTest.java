package com.worldpay.support.impl;

import com.worldpay.support.WorldpaySupportEmailService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

@IntegrationTest
public class DefaultWorldpaySupportEmailServiceIntegrationTest extends ServicelayerBaseTest {

    private static final String HYBRIS_VERSION = "Hybris version";
    private static final String TIME = "Time:";
    private static final String USER = "User:";
    private static final String MERCHANT_CONFIGURATION = "Merchant Configuration:";
    private static final String CONFIGURED_MERCHANT_BEAN_NAME = "Configured Merchant Bean Name:";
    private static final String ACTIVE_PAYMENT_FLOW = "Active Payment Flow:";
    private static final String PAYMENT_TRANSACTION_TYPES = "Payment Transaction Types:";
    private static final String EXTENSIONS = "Extensions:";
    private static final String CLUSTER_INFORMATION = "Cluster Information:";
    private static final String WORLDPAY_PLUGIN_VERSION = "Worldpay Plugin Version:";

    @Resource
    private WorldpaySupportEmailService worldpaySupportEmailService;
    @Resource
    private ConfigurationService configurationService;

    @Before
    public void setup() {
        configurationService.getConfiguration().addProperty("worldpay.addon.version", "testCommitId");
    }

    @Test
    public void shouldCreateContent() {

        final String emailBody = worldpaySupportEmailService.createEmailBody();

        assertTrue(emailBody.contains(HYBRIS_VERSION));
        assertTrue(emailBody.contains(WORLDPAY_PLUGIN_VERSION));
        assertTrue(emailBody.contains(TIME));
        assertTrue(emailBody.contains(USER));
        assertTrue(emailBody.contains(MERCHANT_CONFIGURATION));
        assertTrue(emailBody.contains(CONFIGURED_MERCHANT_BEAN_NAME));
        assertTrue(emailBody.contains(ACTIVE_PAYMENT_FLOW));
        assertTrue(emailBody.contains(PAYMENT_TRANSACTION_TYPES));
        assertTrue(emailBody.contains(EXTENSIONS));
        assertTrue(emailBody.contains(CLUSTER_INFORMATION));
    }

    @After
    public void tearDown(){
        configurationService.getConfiguration().clearProperty("worldpay.addon.version");
    }
}
    

