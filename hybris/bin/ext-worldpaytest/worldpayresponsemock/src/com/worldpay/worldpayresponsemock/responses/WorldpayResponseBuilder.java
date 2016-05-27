package com.worldpay.worldpayresponsemock.responses;

import com.worldpay.internal.model.PaymentService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by admin on 26/04/16.
 */
public interface WorldpayResponseBuilder {
    PaymentService buildRedirectResponse(PaymentService request, HttpServletRequest httpServletRequest);
}
