package com.worldpay.support.appender.impl;

import com.worldpay.support.WorldpayCronJobSupportInformationService;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayPaymentTransactionTypesAppenderTest {

    @Spy
    @InjectMocks
    private WorldpayPaymentTransactionTypesAppender testObj = new WorldpayPaymentTransactionTypesAppender();

    @Mock
    private WorldpayCronJobSupportInformationService worldpayCronJobSupportInformationServiceMock;

    @Test
    public void testAppendContent() throws Exception {
        doReturn(worldpayCronJobSupportInformationServiceMock).when(testObj).getWorldpayCronJobSupportInformationService();
        when(worldpayCronJobSupportInformationServiceMock.getPaymentTransactionType()).thenReturn(singleton(AUTHORIZATION));

        final String result = testObj.appendContent();

        assertTrue(result.contains("Payment Transaction Types:"));
        assertTrue(result.contains(AUTHORIZATION.getCode()));
    }

    @Test
    public void shouldNotAppendContentIfBeanNotFound() {
        doReturn(null).when(testObj).getWorldpayCronJobSupportInformationService();

        final String result = testObj.appendContent();

        assertFalse(result.contains("Payment Transaction Types:"));
    }
}
    

