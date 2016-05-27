package com.worldpay.cscockpit.services.search.generic.query;

import de.hybris.platform.cscockpit.services.search.generic.query.AbstractCsFlexibleSearchQueryBuilder;
import de.hybris.platform.cscockpit.services.search.impl.DefaultCsTextSearchCommand;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import static com.worldpay.cscockpit.services.search.generic.query.DefaultCartSearchQueryBuilder.TextField.CartId;
import static org.apache.commons.lang.StringUtils.isNotEmpty;


public class DefaultCartSearchQueryBuilder extends AbstractCsFlexibleSearchQueryBuilder<DefaultCsTextSearchCommand> {

    private static final String SELECT_FROM_CART_QUERY = "SELECT DISTINCT {c:pk}, {c:code} FROM {Cart AS c }";
    private static final String WHERE_CART_CODE = " WHERE {c:code} LIKE ?cartId";
    private static final String ORDER_BY_CART_CODE = " ORDER BY {c:code} ASC";

    /**
     * (non-Javadoc)
     * @see de.hybris.platform.cscockpit.services.search.generic.query.AbstractCsFlexibleSearchQueryBuilder#
     * buildFlexibleSearchQuery(de.hybris.platform.cscockpit.services.search.CsSearchCommand)
     */
    @Override
    protected FlexibleSearchQuery buildFlexibleSearchQuery(final DefaultCsTextSearchCommand command) {
        ServicesUtil.validateParameterNotNullStandardMessage("command", command);
        final String cartId = command.getText(CartId);
        final boolean searchCartId = isNotEmpty(cartId);
        final StringBuilder query = new StringBuilder(SELECT_FROM_CART_QUERY);
        if (searchCartId) {
            query.append(WHERE_CART_CODE);
        }
        query.append(ORDER_BY_CART_CODE);
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString());
        if (searchCartId) {
            searchQuery.addQueryParameter("cartId", "%" + cartId.trim() + "%");
        }
        return searchQuery;
    }

    public enum TextField {
        CartId
    }
}
