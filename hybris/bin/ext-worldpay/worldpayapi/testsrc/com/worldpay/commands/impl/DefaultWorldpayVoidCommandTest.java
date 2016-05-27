package com.worldpay.commands.impl;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.request.CancelServiceRequest;
import com.worldpay.service.response.CancelServiceResponse;
import com.worldpay.transaction.WorldpayPaymentTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayVoidCommandTest {

    public static final String MERCHANT_CODE = "merchantCode";
    public static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    public static final String EXCEPTION_MESSAGE = "exceptionMessage";

    @Spy
    @InjectMocks
    private DefaultWorldpayVoidCommand testObj = new DefaultWorldpayVoidCommand();

    @Mock
    private Converter<CancelServiceResponse, VoidResult> voidServiceResponseConverterMock;
    @Mock
    private VoidRequest voidRequestMock;
    @Mock
    private WorldpayServiceGateway worldpayGatewayMock;
    @Mock
    private CancelServiceResponse cancelResponseMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private WorldpayConfigLookupService worldpayConfigLookupServiceMock;
    @Mock
    private WorldpayConfig worldpayConfigMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private VoidResult voidResultMock;
    @Mock
    private WorldpayPaymentTransactionService worldpayPaymentTransactionServiceMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        doReturn(worldpayGatewayMock).when(testObj).getWorldpayServiceGatewayInstance();
        when(worldpayConfigLookupServiceMock.lookupConfig()).thenReturn(worldpayConfigMock);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoMock);
        when(merchantInfoMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(voidServiceResponseConverterMock.convert(cancelResponseMock)).thenReturn(voidResultMock);
        when(worldpayPaymentTransactionServiceMock.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);
        when(voidRequestMock.getRequestToken()).thenReturn(MERCHANT_CODE);
        when(voidRequestMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void testPerformShouldReturnASuccessfulVoidResponse() throws Exception {
        when(worldpayGatewayMock.cancel(any(CancelServiceRequest.class))).thenReturn(cancelResponseMock);

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));
    }

    @Test
    public void testPerformShouldReturnAFailingVoidResponseWhenAnExceptionOcurredInvokingTheGateway() throws Exception {
        doThrow(new WorldpayException(EXCEPTION_MESSAGE)).when(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));
        verify(voidServiceResponseConverterMock, never()).convert(any(CancelServiceResponse.class));
    }

    @Test
    public void testPerformShouldReturnAFailingVoidResponseWhenResponseFromGatewayIsNull() throws Exception {
        when(worldpayGatewayMock.cancel(any(CancelServiceRequest.class))).thenReturn(null);

        testObj.perform(voidRequestMock);

        verify(worldpayGatewayMock).cancel(any(CancelServiceRequest.class));
        verify(voidServiceResponseConverterMock, never()).convert(any(CancelServiceResponse.class));
    }
}