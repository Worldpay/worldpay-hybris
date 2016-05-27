package com.worldpay.worldpayresponsemock.controllers.pages;

import com.worldpay.internal.model.PaymentService;
import com.worldpay.worldpayresponsemock.facades.WorldpayMockFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayMockControllerTest {

    private static final String XML_RESPONSE = "xmlResponse";

    @Spy
    @InjectMocks
    private WorldpayMockController testObj = new WorldpayMockController();

    @Mock
    private Unmarshaller unMarshallerMock;
    @Mock
    private BufferedReader bufferedReaderMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private WorldpayMockFacade worldpayMockFacadeMock;
    @Mock
    private PaymentService paymentServiceMock;

    @Before
    public void setup() throws JAXBException {
        doReturn(unMarshallerMock).when(testObj).createUnmarshaller();
    }

    @Test
    public void mockWorldpayResponseShouldUnmarshalRequest() throws Exception {
        when(requestMock.getReader()).thenReturn(bufferedReaderMock);
        when(unMarshallerMock.unmarshal(bufferedReaderMock)).thenReturn(paymentServiceMock);
        when(worldpayMockFacadeMock.buildResponse(paymentServiceMock, requestMock)).thenReturn(XML_RESPONSE);

        final String result = testObj.mockWorldpayResponse(requestMock);

        assertEquals(XML_RESPONSE, result);
    }
}