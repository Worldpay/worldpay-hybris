package com.worldpay.core.dao;

import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

/**
 * Data access to {@link CartModel}
 *
 */
public interface WorldpayCartDao {

    /**
     * Returns a {@link List} of {@link CartModel} which are related to a specific Worldpay order code
     *
     * @param worldpayOrderCode Worldpay order code
     * @return list of {@link CartModel}
     */
    List<CartModel> findCartsByWorldpayOrderCode(final String worldpayOrderCode);
}
