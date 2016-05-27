package com.worldpay.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.worldpay.internal.model.Journal;
import com.worldpay.internal.model.Notify;
import com.worldpay.internal.model.OrderStatusEvent;
import com.worldpay.internal.model.Payment;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.ShopperWebformRefundDetails;
import com.worldpay.internal.model.Token;
import com.worldpay.service.model.JournalReply;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.token.TokenReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.service.response.transform.ServiceResponseTransformerHelper;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class OrderModificationRequestConverterTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String MERCHANT_CODE = "merchantCode";

    @Spy
    @InjectMocks
    private OrderModificationRequestConverter testObj = new OrderModificationRequestConverter();

    @Mock (answer = RETURNS_DEEP_STUBS)
    private PaymentService paymentServiceMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private Notify notifyMock;
    @Mock
    private OrderStatusEvent orderStatusEventMock;
    @Mock
    private Payment paymentMock;
    @Mock
    private Journal journalMock;
    @Mock
    private ServiceResponseTransformerHelper responseTransformerHelperMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private JournalReply journalReplyMock;
    @Mock
    private Token tokenMock;
    @Mock
    private TokenReply tokenReplyMock;
    @Mock
    private ShopperWebformRefundDetails shopperWebformRefundDetailsMock;
    @Mock
    private com.worldpay.service.model.WebformRefundReply webformRefundReplyMock;


    @Before
    public void setUp(){
        doReturn(responseTransformerHelperMock).when(testObj).getServiceResponseTransformerHelper();
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0)).thenReturn(notifyMock);
        when(notifyMock.getOrderStatusEventOrReport().get(0)).thenReturn(orderStatusEventMock);
    }

    @Test
    public void convertShouldConvertXMLMessageIntoOrderModificationMessage() throws Exception {
        when(responseTransformerHelperMock.buildPaymentReply(paymentMock)).thenReturn(paymentReplyMock);
        when(responseTransformerHelperMock.buildJournalReply(journalMock)).thenReturn(journalReplyMock);
        when(orderStatusEventMock.getPayment()).thenReturn(paymentMock);
        when(orderStatusEventMock.getJournal()).thenReturn(journalMock);
        when(orderStatusEventMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(paymentServiceMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(orderStatusEventMock.getToken()).thenReturn(tokenMock);
        when(responseTransformerHelperMock.buildTokenReply(tokenMock)).thenReturn(tokenReplyMock);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertSame(paymentReplyMock, result.getPaymentReply());
        assertSame(journalReplyMock, result.getJournalReply());
        assertSame(tokenReplyMock, result.getTokenReply());
    }

    @Test
    public void convertShouldConvertXMLMessageIntoOrderModificationMessageWithoutToken() throws Exception {
        when(responseTransformerHelperMock.buildPaymentReply(paymentMock)).thenReturn(paymentReplyMock);
        when(responseTransformerHelperMock.buildJournalReply(journalMock)).thenReturn(journalReplyMock);
        when(orderStatusEventMock.getPayment()).thenReturn(paymentMock);
        when(orderStatusEventMock.getJournal()).thenReturn(journalMock);
        when(orderStatusEventMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(paymentServiceMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(orderStatusEventMock.getToken()).thenReturn(null);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertEquals(ORDER_CODE, result.getOrderCode());
        assertEquals(MERCHANT_CODE, result.getMerchantCode());
        assertSame(paymentReplyMock, result.getPaymentReply());
        assertSame(journalReplyMock, result.getJournalReply());
        assertNull(result.getTokenReply());
    }

    @Test
    public void shouldSetShopperWebformReply() {
        when(orderStatusEventMock.getShopperWebformRefundDetails()).thenReturn(shopperWebformRefundDetailsMock);
        when(responseTransformerHelperMock.buildWebformRefundReply(shopperWebformRefundDetailsMock)).thenReturn(webformRefundReplyMock);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertEquals(webformRefundReplyMock, result.getWebformRefundReply());
    }

    @Test
    public void shouldNotSetShopperWebformReplyWhenNull() {
        when(orderStatusEventMock.getShopperWebformRefundDetails()).thenReturn(null);

        final OrderNotificationMessage result = testObj.convert(paymentServiceMock);

        assertNull(result.getWebformRefundReply());
        verify(responseTransformerHelperMock, never()).buildWebformRefundReply(any());
    }
}