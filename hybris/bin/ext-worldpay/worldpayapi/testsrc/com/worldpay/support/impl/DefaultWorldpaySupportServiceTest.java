package com.worldpay.support.impl;

import com.worldpay.support.WorldpaySupportEmailService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.support.impl.DefaultWorldpaySupportService.*;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpaySupportServiceTest {

    private static final String WORLDPAY_SUPPORT_EMAIL_ADDRESS = "emailAddress";
    private static final String CUSTOMER_EMAIL_ADDRESS = "customerEmailAddress";
    private static final String WORLDPAY_SUPPORT_EMAIL_DISPLAY_NAME = "emailDisplayName";
    private static final String CUSTOMER_EMAIL_DISPLAY_NAME = "customerEmailDisplayName";
    private static final String REPLY_TO_ADDRESS = "replyToAddress";
    private static final String SUBJECT = "subject";
    private static final String EMAIL_BODY = "body";

    @Spy
    @InjectMocks
    private DefaultWorldpaySupportService testObj = new DefaultWorldpaySupportService();

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configurationMock;
    @Mock
    private WorldpaySupportEmailService worldpaySupportEmailServiceMock;
    @Mock
    private Email emailMock;


    @Before
    public void setUp() throws EmailException {
        doReturn(emailMock).when(testObj).getPreConfiguredEmail();

        when(configurationService.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getString(WORLDPAY_SUPPORT_EMAIL_CONFIG_KEY)).thenReturn(WORLDPAY_SUPPORT_EMAIL_ADDRESS);
        when(configurationMock.getString(WORLDPAY_SUPPORT_EMAIL_DISPLAY_NAME_KEY)).thenReturn(WORLDPAY_SUPPORT_EMAIL_DISPLAY_NAME);

        when(configurationMock.getString(CUSTOMER_EMAIL_CONFIG_KEY)).thenReturn(CUSTOMER_EMAIL_ADDRESS);
        when(configurationMock.getString(CUSTOMER_EMAIL_DISPLAY_NAME_KEY)).thenReturn(CUSTOMER_EMAIL_DISPLAY_NAME);

        when(configurationMock.getString(CUSTOMER_REPLY_TO_ADDRESS)).thenReturn(REPLY_TO_ADDRESS);
        when(configurationMock.getString(EMAIL_SUBJECT_KEY)).thenReturn(SUBJECT);
        when(worldpaySupportEmailServiceMock.createEmailBody()).thenReturn(EMAIL_BODY);
    }

    @Test
    public void testSendSupportEmail() throws Exception {
        testObj.sendSupportEmail();

        verify(emailMock).addTo(WORLDPAY_SUPPORT_EMAIL_ADDRESS, WORLDPAY_SUPPORT_EMAIL_DISPLAY_NAME);
        verify(emailMock).setFrom(CUSTOMER_EMAIL_ADDRESS, CUSTOMER_EMAIL_DISPLAY_NAME);
        verify(emailMock).addReplyTo(REPLY_TO_ADDRESS);
        verify(emailMock).setSubject(SUBJECT);
        verify(emailMock).setMsg(EMAIL_BODY);
        verify(emailMock).send();
    }
}