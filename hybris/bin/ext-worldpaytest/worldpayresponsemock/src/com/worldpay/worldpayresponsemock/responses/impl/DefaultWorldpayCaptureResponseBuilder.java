package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.Capture;
import com.worldpay.internal.model.CaptureReceived;
import com.worldpay.internal.model.Modify;
import com.worldpay.internal.model.Ok;
import com.worldpay.internal.model.OrderModification;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.worldpayresponsemock.responses.WorldpayCaptureResponseBuilder;

public class DefaultWorldpayCaptureResponseBuilder implements WorldpayCaptureResponseBuilder {

    @Override
    public PaymentService buildCaptureResponse(PaymentService request) {
        final Modify modify = (Modify) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
        final OrderModification orderModification = (OrderModification) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdate().get(0);
        final Capture captureRequest = (Capture) orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAddTransactionCertificateOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetails().get(0);

        CaptureReceived capture = getCaptureReceived(orderModification.getOrderCode(), captureRequest.getAmount());

        final Ok ok = new Ok();
        ok.getCancelReceivedOrVoidReceivedOrCaptureReceivedOrRevokeReceivedOrRefundReceivedOrBackofficeCodeReceivedOrAuthorisationCodeReceivedOrTransactionCertificateReceivedOrDefenceReceivedOrUpdateTokenReceived().add(capture);

        final Reply reply = new Reply();
        reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().add(ok);

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantCode());
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);
        return paymentService;
    }

    private CaptureReceived getCaptureReceived(final String orderCode, final Amount amount) {
        CaptureReceived capture = new CaptureReceived();
        capture.setOrderCode(orderCode);
        capture.setAmount(amount);
        return capture;
    }
}
