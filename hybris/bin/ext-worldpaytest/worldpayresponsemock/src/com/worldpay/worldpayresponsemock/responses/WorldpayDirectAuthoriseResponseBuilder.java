package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.internal.model.PaymentService;

/**
 * Created by admin on 26/04/16.
 */
public interface WorldpayDirectAuthoriseResponseBuilder {
    PaymentService buildDirectResponse(PaymentService request);
}
