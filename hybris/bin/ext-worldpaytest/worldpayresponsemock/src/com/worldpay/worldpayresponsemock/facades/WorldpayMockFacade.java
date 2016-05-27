package com.worldpay.worldpayresponsemock.facades;

import com.worldpay.exception.WorldpayException;
import com.worldpay.internal.model.PaymentService;

import javax.servlet.http.HttpServletRequest;

/**
 * Exposes the methods to build mocked responses as if they were from Worldpay
 */
public interface WorldpayMockFacade {

    /**
     * Builds a response according to the paymentService
     *
     * @param paymentService the {@PaymentService} containing the information of the request
     * @param request
     * @return
     */
    String buildResponse(PaymentService paymentService, HttpServletRequest request) throws WorldpayException;
}
