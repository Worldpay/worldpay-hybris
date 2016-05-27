package com.worldpay.worldpayresponsemock.facades.impl;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.BatchModification;
import com.worldpay.internal.model.Capture;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.Order;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentDetails;
import com.worldpay.internal.model.PaymentMethodMask;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.PaymentTokenCreate;
import com.worldpay.internal.model.Submit;
import com.worldpay.service.marshalling.impl.DefaultPaymentServiceMarshaller;
import com.worldpay.worldpayresponsemock.responses.WorldpayCaptureResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayDirectAuthoriseResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayTokenCreateResponseBuilder;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayMockFacadeTest {

    private static final String XML_RESULT = "RESULT";
    private static final String CAPTURE_OK = "captureOk";
    private static final String REDIRECT_XML = "redirectXML";
    private static final String TOKEN_REPLY_XML = "tokenReplyXML";

    @Spy
    @InjectMocks
    private DefaultWorldpayMockFacade testObj = new DefaultWorldpayMockFacade();

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private PaymentService responsePaymentService;
    @Mock
    private Submit submitMock;
    @Mock
    private Order orderMock;
    @Mock
    private WorldpayResponseBuilder worldpayResponseBuilder;
    @Mock
    private WorldpayTokenCreateResponseBuilder worldpayTokenCreateResponseBuilderMock;
    @Mock
    private WorldpayCaptureResponseBuilder worldpayCaptureResponseBuilder;
    @Mock
    private WorldpayDirectAuthoriseResponseBuilder worldpayDirectAuthoriseResponseBuilder;
    @Mock
    private PaymentDetails paymentMethodDetailMock;
    @Mock
    private Modify modifyMock;
    @Mock
    private OrderModification orderModificationMock;
    @Mock
    private Capture captureMock;
    @Mock
    private PaymentMethodMask paymentMethodMaskMock;
    @Mock
    private HttpServletRequest httpRequestMock;
    @Mock
    private BatchModification batchModification;
    @Mock
    private DefaultPaymentServiceMarshaller paymentServiceMarshaller;
    @Mock
    private PaymentTokenCreate paymentTokenCreateMock;

    @Before
    public void setUp() throws WorldpayException {
        when(worldpayDirectAuthoriseResponseBuilder.buildDirectResponse(paymentServiceMock)).thenReturn(responsePaymentService);
        when(worldpayCaptureResponseBuilder.buildCaptureResponse(paymentServiceMock)).thenReturn(responsePaymentService);
        when(worldpayResponseBuilder.buildRedirectResponse(paymentServiceMock, httpRequestMock)).thenReturn(responsePaymentService);
        when(worldpayTokenCreateResponseBuilderMock.buildTokenResponse(paymentServiceMock)).thenReturn(responsePaymentService);
        doReturn(paymentServiceMarshaller).when(testObj).getPaymentServiceMarshaller();
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(XML_RESULT);
    }

    @Test
    public void shouldBuildDirectResponseIfPaymentServiceContainsSubmitWithPaymentDetails() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(singletonList(orderMock));
        when(orderMock.getPaymentMethodMaskOrPaymentDetailsOrPayAsOrder()).thenReturn(singletonList(paymentMethodDetailMock));

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(XML_RESULT, result);
    }

    @Test
    public void shouldReturnCaptureResponseIfRequestContainsModifyWithCapture() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(modifyMock));
        when(modifyMock.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdate()).thenReturn(singletonList(orderModificationMock));
        when(orderModificationMock.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAddTransactionCertificateOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetails()).thenReturn(singletonList(captureMock));
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(CAPTURE_OK);

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(CAPTURE_OK, result);
    }

    @Test
    public void shouldReturnNullIfRequestContainsModifyWithoutCapture() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(modifyMock));
        when(modifyMock.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdate()).thenReturn(singletonList(batchModification));

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertNull(result);
    }

    @Test
    public void shouldReturnRedirectXMLIfRequestContainsSubmitWithPaymentMethodMask() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(singletonList(orderMock));
        when(orderMock.getPaymentMethodMaskOrPaymentDetailsOrPayAsOrder()).thenReturn(singletonList(paymentMethodMaskMock));
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(REDIRECT_XML);

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(REDIRECT_XML, result);
    }

    @Test
    public void shouldReturnTokenWhenRequestContainsPaymentTokenCreate() throws WorldpayException {
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(singletonList(paymentTokenCreateMock));
        when(paymentServiceMarshaller.marshal(responsePaymentService)).thenReturn(TOKEN_REPLY_XML);

        final String result = testObj.buildResponse(paymentServiceMock, httpRequestMock);

        assertEquals(TOKEN_REPLY_XML, result);
    }
}