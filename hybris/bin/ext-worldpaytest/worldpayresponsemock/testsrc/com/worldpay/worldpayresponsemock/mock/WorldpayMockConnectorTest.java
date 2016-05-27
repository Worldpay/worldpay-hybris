package com.worldpay.worldpayresponsemock.mock;

import com.worldpay.worldpayresponsemock.form.ResponseForm;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static com.worldpay.worldpayresponsemock.mock.WorldpayMockConnector.SITE_PARAMETER_NAME;
import static com.worldpay.worldpayresponsemock.mock.WorldpayMockConnector.STOREFRONT_CONTEXT_ROOT;
import static com.worldpay.worldpayresponsemock.mock.WorldpayMockConnector.WORLDPAYRESPONSEMOCK_ORDER_NOTIFICATION_ENDPOINT;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayMockConnectorTest {

    @InjectMocks
    private WorldpayMockConnector testObj = new WorldpayMockConnector();

    private static final String ENDPOINT_POSTFIX = "/endpoint-postfix";
    private static final String SITEID = "siteid";
    private static final String SOME_RESPONSE = "some_response";
    private static final String CONTEXT_ROOT = "/contextRoot";
    private static final String HTTP_SCHEME = "http";
    private static final int SERVER_PORT = 80;
    private static final String SERVER_NAME = "serverName";
    private static final String NOTIFICATION_ENDPOINT_URL = HTTP_SCHEME + "://" + SERVER_NAME + ":" + SERVER_PORT + CONTEXT_ROOT + ENDPOINT_POSTFIX;

    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private RestTemplate worldpayRestTemplateMock;
    @Mock
    private ResponseForm responseFormMock;
    @Mock
    private HttpServletRequest requestMock;

    @Before
    public void setup() {
        when(configurationServiceMock.getConfiguration().getString(WORLDPAYRESPONSEMOCK_ORDER_NOTIFICATION_ENDPOINT)).thenReturn(ENDPOINT_POSTFIX);
        when(configurationServiceMock.getConfiguration().getString(STOREFRONT_CONTEXT_ROOT)).thenReturn(CONTEXT_ROOT);
        when(requestMock.getServerPort()).thenReturn(SERVER_PORT);
        when(requestMock.getServerName()).thenReturn(SERVER_NAME);
        when(requestMock.getScheme()).thenReturn(HTTP_SCHEME);
        when(responseFormMock.getSiteId()).thenReturn(SITEID);
    }

    @Test
    public void sendResponseShouldPostNotificationMessageToStorefront() throws Exception {

        testObj.sendResponse(responseFormMock, requestMock, SOME_RESPONSE);

        verify(worldpayRestTemplateMock).postForObject(NOTIFICATION_ENDPOINT_URL + SITE_PARAMETER_NAME + SITEID, SOME_RESPONSE, String.class);
    }

    @Test
    public void getAllAnswersShouldPopulateModelWithDefaultSiteIdAndNotPost() throws Exception {
        testObj.sendResponse(responseFormMock, requestMock, "");

        verify(worldpayRestTemplateMock, never()).postForObject(NOTIFICATION_ENDPOINT_URL + SITE_PARAMETER_NAME + SITEID, SOME_RESPONSE, String.class);
    }
}