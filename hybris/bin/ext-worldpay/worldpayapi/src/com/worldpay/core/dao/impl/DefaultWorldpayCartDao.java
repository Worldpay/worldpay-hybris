package com.worldpay.core.dao.impl;

import com.worldpay.core.dao.WorldpayCartDao;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayCartDao extends AbstractItemDao implements WorldpayCartDao {

    protected static final String QUERY = "SELECT {" + CartModel.PK + "} FROM {" + CartModel._TYPECODE + "} WHERE {worldpayOrderCode} = ?worldpayOrderCode";
    protected static final String PARAM_WORLD_PAY_ORDER_CODE = "worldpayOrderCode";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CartModel> findCartsByWorldpayOrderCode(final String worldpayOrderCode) {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(QUERY);
        fQuery.addQueryParameters(Collections.singletonMap(PARAM_WORLD_PAY_ORDER_CODE, worldpayOrderCode));
        fQuery.setResultClassList(Collections.singletonList(CartModel.class));
        final SearchResult searchResult = search(fQuery);
        return searchResult.getResult();
    }
}
