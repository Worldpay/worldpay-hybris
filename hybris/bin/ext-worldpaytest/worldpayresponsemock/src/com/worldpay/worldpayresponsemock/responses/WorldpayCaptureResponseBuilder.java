package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.internal.model.PaymentService;

public interface WorldpayCaptureResponseBuilder {
    PaymentService buildCaptureResponse(PaymentService request);
}
