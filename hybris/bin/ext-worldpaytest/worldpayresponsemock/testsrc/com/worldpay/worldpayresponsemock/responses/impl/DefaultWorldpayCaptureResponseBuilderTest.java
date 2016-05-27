package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.Capture;
import com.worldpay.internal.model.CaptureReceived;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.worldpayresponsemock.builders.AmountBuilder.anAmountBuilder;
import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayCaptureResponseBuilderTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final Amount TRANSACTION_AMOUNT = anAmountBuilder().build();
    private static final PaymentService CAPTURE_REQUEST = buildCaptureRequest();
    private static final String WORLDPAY_ORDER_CODE = "orderCode";

    @InjectMocks
    private DefaultWorldpayCaptureResponseBuilder testObj = new DefaultWorldpayCaptureResponseBuilder();

    @Test
    public void shouldBuildResponseContainingMerchantCode() {
        final PaymentService result = testObj.buildCaptureResponse(CAPTURE_REQUEST);

        assertEquals(MERCHANT_CODE, result.getMerchantCode());
    }

    @Test
    public void shouldBuildResponseContainingOrderCode() {
        final PaymentService result = testObj.buildCaptureResponse(CAPTURE_REQUEST);

        final Reply reply = (Reply) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final Ok ok = (Ok) reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().get(0);
        final CaptureReceived captureReceived = (CaptureReceived) ok.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrTransactionCertificateReceivedOrDefenceReceivedOrUpdateTokenReceived().get(0);
        assertEquals(TRANSACTION_AMOUNT.getValue(), captureReceived.getAmount().getValue());
    }

    @Test
    public void shouldBuildResponseContainingCaptureAmount() {
        final PaymentService result = testObj.buildCaptureResponse(CAPTURE_REQUEST);

        final Reply reply = (Reply) result.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final Ok ok = (Ok) reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().get(0);
        final CaptureReceived captureReceived = (CaptureReceived) ok.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrTransactionCertificateReceivedOrDefenceReceivedOrUpdateTokenReceived().get(0);
        assertEquals(WORLDPAY_ORDER_CODE, captureReceived.getOrderCode());
    }

    private static PaymentService buildCaptureRequest() {
        final Capture capture = new Capture();
        capture.setAmount(TRANSACTION_AMOUNT);

        final OrderModification orderModification = new OrderModification();
        orderModification.setOrderCode(WORLDPAY_ORDER_CODE);
        orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAddTransactionCertificateOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetails().add(capture);

        final Modify modify = new Modify();
        modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdate().add(orderModification);

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(MERCHANT_CODE);
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(modify);
        return paymentService;
    }
}