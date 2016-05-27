package com.worldpay.worldpayresponsemock.mock;

import com.worldpay.internal.model.Order;
import com.worldpay.internal.model.OrderStatus;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reference;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Submit;
import com.worldpay.worldpayresponsemock.responses.impl.DefaultWorldpayResponseBuilder;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayResponseBuilderTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    @Spy
    @InjectMocks
    private DefaultWorldpayResponseBuilder testObj = new DefaultWorldpayResponseBuilder();

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private Submit submitMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private Order orderMock;
    @Mock
    private HttpServletRequest request;

    @Test
    public void shouldCreateRedirectResponse() {
        when(paymentServiceMock.getMerchantCode()).thenReturn(MERCHANT_CODE);
        when(paymentServiceMock.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify()).thenReturn(singletonList(submitMock));
        when(submitMock.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate()).thenReturn(singletonList(orderMock));
        when(orderMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);

        final PaymentService result = testObj.buildRedirectResponse(paymentServiceMock, request);

        final List<Object> paymentServiceElements = result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify();
        for (Object paymentServiceElement : paymentServiceElements) {
            if (paymentServiceElement instanceof Reply) {
                final Reply reply = (Reply) paymentServiceElement;
                final List<Object> replyElements = reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken();
                assertThat(replyElements, hasItems(instanceOf(OrderStatus.class)));
                for (Object replyElement : replyElements) {
                    if (replyElement instanceof OrderStatus) {
                        final OrderStatus orderStatus = (OrderStatus) replyElement;
                        assertEquals(WORLDPAY_ORDER_CODE, orderStatus.getOrderCode());
                        final List<Object> orderStatusElements = orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTP();
                        assertThat(orderStatusElements, hasItems(instanceOf(Reference.class)));
                        for (Object orderStatusElement : orderStatusElements) {
                            if (orderStatusElement instanceof Reference) {
                                Reference reference = (Reference) orderStatusElement;
                                assertTrue(reference.getvalue().endsWith("/worldpayresponsemock/redirect?"));
                            }
                        }
                    }
                }
            }
        }
        assertThat(paymentServiceElements, hasItems(instanceOf(Reply.class)));

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
    }


}