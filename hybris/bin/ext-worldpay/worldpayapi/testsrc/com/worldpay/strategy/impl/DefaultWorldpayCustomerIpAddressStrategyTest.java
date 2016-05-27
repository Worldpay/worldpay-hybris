package com.worldpay.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayCustomerIpAddressStrategyTest {

    private static final String HEADER_NAME = "HEADER_NAME";
    private static final String HEADER_VALUE = "customerIp";
    private static final String REMOTE_ADDRESS = "remoteAddress";

    @Mock
    private HttpServletRequest httpRequestMock;

    @InjectMocks
    private DefaultWorldpayCustomerIpAddressStrategy testObj = new DefaultWorldpayCustomerIpAddressStrategy();

    @Test
    public void testShouldGetCustomerIpFromSpecifiedHeaderAttribute() throws Exception {
        testObj.setHeaderName(HEADER_NAME);
        when(httpRequestMock.getHeader(HEADER_NAME)).thenReturn(HEADER_VALUE);

        final String result = testObj.getCustomerIp(httpRequestMock);

        assertEquals(HEADER_VALUE, result);
    }

    @Test
    public void testShouldGetCustomerIpFromRemoteAddrIfHeaderValueIsEmpty() throws Exception {
        testObj.setHeaderName(HEADER_NAME);
        when(httpRequestMock.getHeader(HEADER_NAME)).thenReturn(null);
        when(httpRequestMock.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);

        final String result = testObj.getCustomerIp(httpRequestMock);

        assertEquals(REMOTE_ADDRESS, result);
    }
}
