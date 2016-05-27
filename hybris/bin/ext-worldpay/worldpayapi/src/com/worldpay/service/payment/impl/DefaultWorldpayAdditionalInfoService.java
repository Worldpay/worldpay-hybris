package com.worldpay.service.payment.impl;

import javax.servlet.http.HttpServletRequest;

import com.worldpay.order.data.WorldpayAdditionalInfoData;
import com.worldpay.service.payment.WorldpayAdditionalInfoService;
import com.worldpay.strategy.WorldpayCustomerIpAddressStrategy;
import org.springframework.beans.factory.annotation.Required;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayAdditionalInfoService implements WorldpayAdditionalInfoService {

    protected static final String ACCEPT = "Accept";
    protected static final String USER_AGENT = "User-Agent";

    private WorldpayCustomerIpAddressStrategy worldpayCustomerIpAddressStrategy;

    /**
     * {@inheritDoc}
     */
    @Override
    public WorldpayAdditionalInfoData createWorldpayAdditionalInfoData(final HttpServletRequest request) {
        final WorldpayAdditionalInfoData info = new WorldpayAdditionalInfoData();
        info.setSessionId(request.getSession().getId());
        info.setCustomerIPAddress(worldpayCustomerIpAddressStrategy.getCustomerIp(request));
        info.setAcceptHeader(request.getHeader(ACCEPT));
        info.setUserAgentHeader(request.getHeader(USER_AGENT));
        return info;
    }

    @Required
    public void setWorldpayCustomerIpAddressStrategy(WorldpayCustomerIpAddressStrategy worldpayCustomerIpAddressStrategy) {
        this.worldpayCustomerIpAddressStrategy = worldpayCustomerIpAddressStrategy;
    }
}
