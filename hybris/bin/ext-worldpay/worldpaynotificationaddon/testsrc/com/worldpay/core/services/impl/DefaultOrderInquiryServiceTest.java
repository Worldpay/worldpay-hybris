package com.worldpay.core.services.impl;

import com.worldpay.core.services.WorldpayPaymentInfoService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.model.WorldpayAPMConfigurationModel;
import com.worldpay.service.WorldpayServiceGateway;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.request.OrderInquiryServiceRequest;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultOrderInquiryServiceTest {

    public static final String ORDER_CODE_APM = "orderCodeAPM";
    public static final String PAYMENT_METHOD_CODE = "paymentMethodCode";
    public static final String EXCEPTION_MESSAGE = "exceptionMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

    @Spy
    @InjectMocks
    private DefaultOrderInquiryService testObj = new DefaultOrderInquiryService();

    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private OrderInquiryServiceRequest orderInquiryServiceRequestMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderInquiryServiceResponse orderInquiryServiceResponseMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private PaymentInfoModel paymentTransactionPaymentInfoModelMock;
    @Mock
    private WorldpayServiceGateway worldpayServiceGatewayMock;
    @Mock
    private WorldpayAPMPaymentInfoModel paymentTransactionAPMPaymentInfoModelMock;
    @Mock
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelMock;
    @Mock
    private AbstractOrderModel orderModelMock;
    @Mock
    private WorldpayAPMConfigurationModel worldpayAPMConfigurationModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private WorldpayPaymentInfoService worldpayPaymentInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;

    @Before
    public void setUp() throws Exception {
        doReturn(worldpayServiceGatewayMock).when(testObj).getWorldpayServiceGateway();
        doReturn(orderInquiryServiceRequestMock).when(testObj).createOrderInquiryServiceRequest(merchantInfoMock, ORDER_CODE_APM);
        when(orderInquiryServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
        when(paymentReplyMock.getMethodCode()).thenReturn(PAYMENT_METHOD_CODE);
        when(paymentTransactionModelMock.getInfo()).thenReturn(paymentTransactionPaymentInfoModelMock);
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);
        when(paymentTransactionAPMPaymentInfoModelMock.getIsApm()).thenReturn(Boolean.TRUE);
        when(paymentTransactionModelMock.getOrder()).thenReturn(orderModelMock);
        when(paymentTransactionAPMPaymentInfoModelMock.getApmConfiguration()).thenReturn(worldpayAPMConfigurationModelMock);

        when(worldpayAPMPaymentInfoModelMock.getPaymentType()).thenReturn(PAYMENT_METHOD_CODE);
        when(modelServiceMock.clone(paymentTransactionAPMPaymentInfoModelMock, WorldpayAPMPaymentInfoModel.class)).thenReturn(worldpayAPMPaymentInfoModelMock);

        when(paymentTransactionModelMock.getRequestId()).thenReturn(ORDER_CODE_APM);

        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock)).thenReturn(orderInquiryServiceResponseMock);

        when(paymentTransactionModelMock.getRequestId()).thenReturn(ORDER_CODE_APM);
    }

    @Test
    public void inquirePendingAPMPaymentTransaction() throws Exception {
        testObj.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);

        verify(paymentTransactionModelMock).getRequestId();
        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, ORDER_CODE_APM);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);
    }

    @Test (expected = WorldpayException.class)
    public void inquirePendingAPMPaymentTransactionReThrowsException() throws Exception {
        when(worldpayServiceGatewayMock.orderInquiry(orderInquiryServiceRequestMock)).thenThrow(new WorldpayException(EXCEPTION_MESSAGE));

        testObj.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);

        verify(paymentTransactionModelMock).getRequestId();
        verify(testObj).createOrderInquiryServiceRequest(merchantInfoMock, ORDER_CODE_APM);
        verify(worldpayServiceGatewayMock).orderInquiry(orderInquiryServiceRequestMock);
    }

    @Test
    public void processOrderInquiryServiceResponseShouldCreateAPMPaymentInfo() {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(true);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    public void processOrderInquiryServiceResponseShouldNotCreateAPMPaymentInfoWhenNotAPM() {
        when(paymentTransactionPaymentInfoModelMock.getIsApm()).thenReturn(false);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock, never()).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }

    @Test
    public void processOrderInquiryServiceResponseShouldDoNothingWhenInquiryResponseHasErrors() {
        when(orderInquiryServiceResponseMock.isError()).thenReturn(true);
        when(orderInquiryServiceResponseMock.getErrorDetail().getMessage()).thenReturn(ERROR_MESSAGE);

        testObj.processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);

        verify(worldpayPaymentInfoServiceMock, never()).savePaymentType(paymentTransactionModelMock, PAYMENT_METHOD_CODE);
        verify(worldpayPaymentInfoServiceMock, never()).createWorldpayApmPaymentInfo(paymentTransactionModelMock);
    }
}