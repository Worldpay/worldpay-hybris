package com.worldpay.worldpayresponsemock.facades.impl;

import com.worldpay.exception.WorldpayException;
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
import com.worldpay.worldpayresponsemock.facades.WorldpayMockFacade;
import com.worldpay.worldpayresponsemock.responses.WorldpayCaptureResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayDirectAuthoriseResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayResponseBuilder;
import com.worldpay.worldpayresponsemock.responses.WorldpayTokenCreateResponseBuilder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class DefaultWorldpayMockFacade implements WorldpayMockFacade {

    @Resource
    private WorldpayResponseBuilder worldpayResponseBuilder;
    @Resource
    private WorldpayCaptureResponseBuilder worldpayCaptureResponseBuilder;
    @Resource
    private WorldpayDirectAuthoriseResponseBuilder worldpayDirectAuthoriseResponseBuilder;
    @Resource
    private WorldpayTokenCreateResponseBuilder worldpayTokenCreateResponseBuilder;

    @Override
    public String buildResponse(PaymentService paymentServiceRequest, HttpServletRequest request) throws WorldpayException {
        if (requestContainsCapture(paymentServiceRequest)) {
            return getPaymentServiceMarshaller().marshal(worldpayCaptureResponseBuilder.buildCaptureResponse(paymentServiceRequest));
        }
        if (requestContainsSubmitOrderWithPaymentDetails(paymentServiceRequest)) {
            return getPaymentServiceMarshaller().marshal(worldpayDirectAuthoriseResponseBuilder.buildDirectResponse(paymentServiceRequest));
        }
        if (requestContainsSubmitOrderWithPaymentMethodMask(paymentServiceRequest)) {
            return getPaymentServiceMarshaller().marshal(worldpayResponseBuilder.buildRedirectResponse(paymentServiceRequest, request));
        }
        if (requestContainsTokenCreate(paymentServiceRequest)) {
            return getPaymentServiceMarshaller().marshal(worldpayTokenCreateResponseBuilder.buildTokenResponse(paymentServiceRequest));
        }
        return null;
    }


    protected DefaultPaymentServiceMarshaller getPaymentServiceMarshaller() {
        return DefaultPaymentServiceMarshaller.getInstance();
    }

    protected boolean requestContainsTokenCreate(final PaymentService paymentServiceRequest) {
        if (paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Submit) {
            Submit submit = (Submit) paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            final Object possibleTokenCreate = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
            return possibleTokenCreate instanceof PaymentTokenCreate;
        }
        return false;

    }

    private boolean requestContainsSubmitOrderWithPaymentMethodMask(PaymentService paymentServiceRequest) {
        if (paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Submit) {
            Submit submit = (Submit) paymentServiceRequest.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            final Object possibleOrder = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
            if (possibleOrder instanceof Order) {
                return ((Order) possibleOrder).getPaymentMethodMaskOrPaymentDetailsOrPayAsOrder().get(0) instanceof PaymentMethodMask;
            }
        }
        return false;
    }

    private boolean requestContainsCapture(PaymentService request) {
        if (request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Modify) {
            Modify modify = (Modify) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            if (modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdate().get(0) instanceof OrderModification) {
                OrderModification orderModification = (OrderModification) modify.getOrderModificationOrBatchModificationOrAccountBatchModificationOrFuturePayAgreementModificationOrPaymentTokenUpdate().get(0);
                return orderModification.getCancelOrCaptureOrRefundOrRevokeOrAddBackOfficeCodeOrAddTransactionCertificateOrAuthoriseOrIncreaseAuthorisationOrCancelOrRefundOrDefendOrShopperWebformRefundDetails().get(0) instanceof Capture;
            }
        }
        return false;
    }

    private boolean requestContainsSubmitOrderWithPaymentDetails(PaymentService request) {
        if (request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0) instanceof Submit) {
            Submit submit = (Submit) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);
            final Object possibleOrder = submit.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate().get(0);
            if (possibleOrder instanceof Order) {
                return ((Order) possibleOrder).getPaymentMethodMaskOrPaymentDetailsOrPayAsOrder().get(0) instanceof PaymentDetails;
            }
        }
        return false;
    }
}
