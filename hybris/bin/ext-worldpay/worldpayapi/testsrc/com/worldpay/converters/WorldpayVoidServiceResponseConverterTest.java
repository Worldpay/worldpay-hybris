package com.worldpay.converters;

import com.worldpay.service.response.CancelServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.VoidResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayVoidServiceResponseConverterTest {

    private static final String ORDER_CODE = "orderCode";

    @Spy
    @InjectMocks
    private WorldpayVoidServiceResponseConverter testObj = new WorldpayVoidServiceResponseConverter();
    @Mock
    private CancelServiceResponse cancelServiceRequestMock;

    @Before
    public void setUp() {
        testObj.setTargetClass(VoidResult.class);
    }

    @Test
    public void testConvert() throws Exception {
        when(cancelServiceRequestMock.getOrderCode()).thenReturn(ORDER_CODE);

        final VoidResult result = testObj.convert(cancelServiceRequestMock);

        assertEquals(ORDER_CODE, result.getRequestId());
        assertEquals(ACCEPTED, result.getTransactionStatus());
        assertEquals(SUCCESFULL, result.getTransactionStatusDetails());
    }
}