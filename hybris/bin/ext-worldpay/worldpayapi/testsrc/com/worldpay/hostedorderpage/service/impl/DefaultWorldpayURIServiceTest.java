package com.worldpay.hostedorderpage.service.impl;

import com.worldpay.exception.WorldpayException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.http.NameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayURIServiceTest {

    private static final String INCORRECT_URL = "#':>@~3'23#4.'";
    private static final String CLEAR_REDIRECT_URL = "http://www.example.com";
    private static final String REDIRECT_URL = CLEAR_REDIRECT_URL + "?key=val&key2=val2";

    @InjectMocks
    private DefaultWorldpayURIService testObj = new DefaultWorldpayURIService();


    @Test (expected = WorldpayException.class)
    public void extractParametersFromUrlThrowsWorldpayExceptionIfIncorrectUrl() throws WorldpayException {
        testObj.extractUrlParamsToMap(INCORRECT_URL, emptyMap());
    }

    @Test
    public void extractParametersFromUrl() throws WorldpayException {
        final List<NameValuePair> result = testObj.extractParametersFromUrl(REDIRECT_URL);
        assertNotNull(result);
        assertEquals("key", result.get(0).getName());
        assertEquals("val", result.get(0).getValue());
        assertEquals("key2", result.get(1).getName());
        assertEquals("val2", result.get(1).getValue());
    }
}