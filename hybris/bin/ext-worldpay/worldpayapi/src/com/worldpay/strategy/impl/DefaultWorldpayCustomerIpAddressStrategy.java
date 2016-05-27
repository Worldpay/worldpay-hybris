package com.worldpay.strategy.impl;

import com.worldpay.strategy.WorldpayCustomerIpAddressStrategy;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCustomerIpAddressStrategy implements WorldpayCustomerIpAddressStrategy {

    private String headerName;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomerIp(HttpServletRequest request) {
        final String headerValue = request.getHeader(headerName);
        return containsValidIp(headerValue) ? request.getRemoteAddr() : headerValue;
    }

    private boolean containsValidIp(String headerValue) {
        return StringUtils.isBlank(headerValue) || "unknown".equalsIgnoreCase(headerValue);
    }

    @Required
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
