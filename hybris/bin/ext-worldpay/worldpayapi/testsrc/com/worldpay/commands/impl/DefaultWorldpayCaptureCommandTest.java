package com.worldpay.commands.impl;

import com.worldpay.config.Environment;
import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayOrderService;
import com.worldpay.service.request.CaptureServiceRequest;
import com.worldpay.service.response.CaptureServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayCaptureCommandTest {

    public static final String MERCHANT_CODE = "merchantCode";
    public static final String AMOUNT = "100";
    public static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    public static final String VERSION = "1.4";

    @Spy
    @InjectMocks
    private DefaultWorldpayCaptureCommand testObj = new DefaultWorldpayCaptureCommand();
    @Mock
    private CaptureRequest captureRequestMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoService;
    @Mock
    private WorldpayConfigLookupService worldpayConfigLookupService;
    @Mock
    private WorldpayConfig worldpayConfigMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private CaptureServiceResponse captureResponseMock;
    @Mock
    private Converter<CaptureServiceResponse, CaptureResult> captureResponseConverterMock;
    @Mock
    private CaptureResult captureResultMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private WorldpayOrderService worldpayOrderServiceMock;
    @Mock
    private Amount amountMock;
    @Mock
    private Environment environmentMock;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        when(captureRequestMock.getRequestToken()).thenReturn(MERCHANT_CODE);
        when(captureRequestMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(captureRequestMock.getTotalAmount()).thenReturn(new BigDecimal(AMOUNT));
        final Currency currency = Currency.getInstance(Locale.UK);
        when(captureRequestMock.getCurrency()).thenReturn(currency);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(worldpayOrderServiceMock.getWorldpayServiceGateway()).thenReturn(worldpayServiceGatewayMock);
        when(worldpayOrderServiceMock.createAmount(currency, Double.valueOf(AMOUNT))).thenReturn(amountMock);
        when(worldpayConfigLookupService.lookupConfig()).thenReturn(worldpayConfigMock);
        when(worldpayConfigMock.getVersion()).thenReturn(VERSION);
        when(worldpayConfigMock.getEnvironment()).thenReturn(environmentMock);
        when(worldpayMerchantInfoService.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(captureResponseMock.getAmount().getCurrencyCode()).thenReturn(currency.getCurrencyCode());
        when(captureResponseMock.getAmount().getValue()).thenReturn(AMOUNT);
        when(captureResponseConverterMock.convert(captureResponseMock)).thenReturn(captureResultMock);
    }

    @Test
    public void performShouldCreateAndSendCaptureCommandToWorldpay() throws WorldpayException {
        when(worldpayServiceGatewayMock.capture(any(CaptureServiceRequest.class))).thenReturn(captureResponseMock);

        testObj.perform(captureRequestMock);

        verify(captureResponseConverterMock).convert(captureResponseMock);
        verify(captureResultMock).setRequestToken(MERCHANT_CODE);
    }

    @Test
    public void performShouldReturnCaptureResultWithErrorStatus() throws WorldpayException {
        when(worldpayServiceGatewayMock.capture(any(CaptureServiceRequest.class))).thenReturn(null);

        final CaptureResult result = testObj.perform(captureRequestMock);

        verify(captureResponseConverterMock, never()).convert(captureResponseMock);
        verify(captureResultMock, never()).setRequestToken(MERCHANT_CODE);

        assertEquals(TransactionStatus.ERROR, result.getTransactionStatus());
        assertEquals(TransactionStatusDetails.COMMUNICATION_PROBLEM, result.getTransactionStatusDetails());
    }
}