package com.worldpay.core.services;

import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

/**
 * Service providing extended Worldpay cart service functionality.
 *
 * @spring.bean worldpayCartService
 */
public interface WorldpayCartService {

    /**
     * Sets worldpay decline code on the {@link de.hybris.platform.core.model.order.CartModel}.
     *
     * @param worldpayOrderCode the world pay order code
     * @param declineCode       the decline code
     */
    void setWorldpayDeclineCodeOnCart(final String worldpayOrderCode, final int declineCode);

    /**
     * Returns the list of carts by WorldpayOrderCode
     *
     * @param worldpayOrderCode
     * @return
     */
    List<CartModel> findCartsByWorldpayOrderCode(String worldpayOrderCode);
}
