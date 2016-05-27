package com.worldpay.support.impl;

import com.worldpay.support.appender.WorldpaySupportEmailAppender;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.verify;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpaySupportEmailServiceTest {

    @InjectMocks
    private DefaultWorldpaySupportEmailService testObj = new DefaultWorldpaySupportEmailService();

    @Mock
    private WorldpaySupportEmailAppender worldpaySupportEmailAppenderMock1, worldpaySupportEmailAppenderMock2, worldpaySupportEmailAppenderMock3;

    @Test
    public void createEmailBodyShouldAppendContentFromAppenders() {
        testObj.setEmailAppenders(Arrays.asList(worldpaySupportEmailAppenderMock1, worldpaySupportEmailAppenderMock2, worldpaySupportEmailAppenderMock3));

        testObj.createEmailBody();


        verify(worldpaySupportEmailAppenderMock1).appendContent();
        verify(worldpaySupportEmailAppenderMock2).appendContent();
        verify(worldpaySupportEmailAppenderMock3).appendContent();
    }
}