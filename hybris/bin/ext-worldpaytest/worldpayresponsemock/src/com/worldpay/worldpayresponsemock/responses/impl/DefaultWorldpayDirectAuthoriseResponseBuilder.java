package com.worldpay.worldpayresponsemock.responses.impl;

import com.worldpay.internal.model.Address;
import com.worldpay.internal.model.Order;
import com.worldpay.internal.model.OrderStatus;
import com.worldpay.internal.model.Payment;
import com.worldpay.internal.model.PaymentService;
import com.worldpay.internal.model.Reply;
import com.worldpay.internal.model.Shopper;
import com.worldpay.internal.model.Submit;
import com.worldpay.internal.model.Token;
import com.worldpay.worldpayresponsemock.responses.WorldpayDirectAuthoriseResponseBuilder;

import java.util.List;

import static com.worldpay.worldpayresponsemock.builders.AddressBuilder.anAddressBuilder;
import static com.worldpay.worldpayresponsemock.builders.PaymentBuilder.aPaymentBuilder;
import static com.worldpay.worldpayresponsemock.builders.TokenBuilder.aTokenBuilder;

public class DefaultWorldpayDirectAuthoriseResponseBuilder implements WorldpayDirectAuthoriseResponseBuilder {

    protected static final String AUTHORISED = "AUTHORISED";
    protected static final String NEW_TOKEN_EVENT = "NEW";
    protected static final String OBFUSCATED_PAN = "4111********1111";
    protected static final String VISA_SSL = "VISA-SSL";

    @Override public PaymentService buildDirectResponse(final PaymentService request) {
        final Submit submitRequest = (Submit) request.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().get(0);

        final OrderStatus orderStatus = createOrderStatus(submitRequest);

        final Reply reply = new Reply();
        reply.getOrderStatusOrBatchStatusOrErrorOrAddressCheckResponseOrRefundableAmountOrAccountBatchOrShopperOrOkOrFuturePayAgreementStatusOrShopperAuthenticationResultOrFuturePayPaymentResultOrPricePointOrPaymentOptionOrToken().add(orderStatus);

        final PaymentService paymentService = new PaymentService();
        paymentService.setMerchantCode(request.getMerchantCode());
        paymentService.getSubmitOrModifyOrInquiryOrReplyOrNotifyOrVerify().add(reply);

        return paymentService;
    }

    private OrderStatus createOrderStatus(final Submit submitRequest) {
        boolean shouldCreateToken = false;
        String tokenReason = "tokenReason";
        String authenticatedShopperId = null;

        final OrderStatus orderStatus = new OrderStatus();

        final List<Object> requestElements = submitRequest.getOrderOrOrderBatchOrShopperOrFuturePayAgreementOrMakeFuturePayPaymentOrIdentifyMeRequestOrPaymentTokenCreate();
        for (Object requestElement : requestElements) {
            if (requestElement instanceof Shopper) {
                Shopper shopper = (Shopper) requestElement;
                authenticatedShopperId = shopper.getAuthenticatedShopperID();
            }

            if (requestElement instanceof Order) {
                Order requestOrder = (Order) requestElement;
                final Payment payment = aPaymentBuilder()
                        .withTransactionAmount(requestOrder.getAmount().getValue()).withLastEvent(AUTHORISED).build();
                orderStatus.setOrderCode(requestOrder.getOrderCode());
                orderStatus.getReferenceOrBankAccountOrErrorOrPaymentOrOrderModificationOrJournalOrRequestInfoOrFxApprovalRequiredOrZappRTP().add(payment);
                if (requestOrder.getCreateToken() != null) {
                    shouldCreateToken = true;
                    tokenReason = requestOrder.getCreateToken().getTokenReason().getvalue();
                }

            }
        }

        if (shouldCreateToken) {
            final Address addressForCardDetails = anAddressBuilder().build();
            final Token token = aTokenBuilder().withTokenEvent(NEW_TOKEN_EVENT).withCardBrand(VISA_SSL)
                    .withCardSubBrand("CREDIT").withIssuerCountryCode("GB").withObfuscatedPAN(OBFUSCATED_PAN)
                    .withCardHolderName("TEST_NAME").withCardAddress(addressForCardDetails).withAuthenticatedShopperId(authenticatedShopperId)
                    .withTokenDetailsTokenReason(tokenReason).withTokenReason(tokenReason).build();

            orderStatus.setToken(token);
        }
        return orderStatus;
    }
}
