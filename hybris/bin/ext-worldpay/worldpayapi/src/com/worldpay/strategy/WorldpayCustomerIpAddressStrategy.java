package com.worldpay.strategy;

import javax.servlet.http.HttpServletRequest;

/**
 * Exposes method to retrieve the customer's Ip address from the request
 */
public interface WorldpayCustomerIpAddressStrategy {

    /**
     * Retrieves the customer's ip address from the httpRequest
     * @param request the {@link HttpServletRequest}
     * @return the customer ip address
     */
    String getCustomerIp(HttpServletRequest request);
}
