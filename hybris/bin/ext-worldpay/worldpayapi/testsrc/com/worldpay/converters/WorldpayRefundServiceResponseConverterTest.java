package com.worldpay.converters;

import com.worldpay.service.response.RefundServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayRefundServiceResponseConverterTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String STRING_AMOUNT = "1099";
    private static final String STRING_FRACTIONAL_AMOUNT = "10.99";

    @InjectMocks
    private WorldpayRefundServiceResponseConverter testObj = new WorldpayRefundServiceResponseConverter();
    @Mock (answer = RETURNS_DEEP_STUBS)
    private RefundServiceResponse refundServiceResponseMock;

    @Before
    public void setUp() {
        testObj.setTargetClass(RefundResult.class);

        when(refundServiceResponseMock.getAmount().getValue()).thenReturn(STRING_AMOUNT);
        when(refundServiceResponseMock.getOrderCode()).thenReturn(ORDER_CODE);
    }

    @Test
    public void convertShouldReturnCaptureResultForFractionalCurrencies() {
        final BigDecimal amount = new BigDecimal(STRING_FRACTIONAL_AMOUNT);
        final Currency currency = Currency.getInstance(Locale.UK);

        when(refundServiceResponseMock.getAmount().getCurrencyCode()).thenReturn(currency.getCurrencyCode());

        final RefundResult result = testObj.convert(refundServiceResponseMock);

        assertEquals(currency, result.getCurrency());
        assertEquals(ORDER_CODE, result.getRequestId());
        assertNotNull(result.getRequestTime());
        assertEquals(amount, result.getTotalAmount());
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }

    @Test
    public void convertShouldReturnCaptureResultForNonFractionalCurrencies() {
        final BigDecimal amount = new BigDecimal(STRING_AMOUNT);
        final Currency currency = Currency.getInstance(Locale.JAPAN);

        when(refundServiceResponseMock.getAmount().getCurrencyCode()).thenReturn(currency.getCurrencyCode());

        final RefundResult result = testObj.convert(refundServiceResponseMock);

        assertEquals(currency, result.getCurrency());
        assertEquals(ORDER_CODE, result.getRequestId());
        assertNotNull(result.getRequestTime());
        assertEquals(amount, result.getTotalAmount());
        assertEquals(TransactionStatus.ACCEPTED, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.SUCCESFULL, result.getTransactionStatusDetails());
    }
}