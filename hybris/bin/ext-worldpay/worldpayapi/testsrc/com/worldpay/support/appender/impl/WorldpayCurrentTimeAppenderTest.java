package com.worldpay.support.appender.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCurrentTimeAppenderTest {

    @InjectMocks
    private WorldpayCurrentTimeAppender testObj = new WorldpayCurrentTimeAppender();

    @Test
    public void testAppendContent() throws Exception {
        final String result = testObj.appendContent();

        assertTrue(result.contains("Time: "));
    }
}
    

