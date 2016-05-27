package com.worldpay.dao.impl;

import com.worldpay.dao.ProcessDefinitionDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * The default implementation of {@link ProcessDefinitionDao} interface.
 */
public class DefaultProcessDefinitionDao extends AbstractItemDao implements ProcessDefinitionDao {

    protected static final String WAIT_ID_PREFIX = "waitFor_";
    protected static final String QUERY_PARAM_ORDER_CODE = "orderCode";
    protected static final String QUERY_PARAM_ACTION_TYPE = "actionType";

    protected static final String GET_BUSINESS_PROCESS_QUERY = "" +
            "SELECT x.PK FROM\n" +
            "({{\n" +
            "select {op.PK}\n" +
            "from {\n" +
            "  " + OrderProcessModel._TYPECODE + " AS op  \n" +
            "  JOIN " + ProcessTaskModel._TYPECODE + " AS pt ON {op.pk} = {pt." + ProcessTaskModel.PROCESS + "}  \n" +
            "  JOIN " + OrderModel._TYPECODE + " as o ON {op." + OrderProcessModel.ORDER + "} = {o.PK} \n" +
            "}\n" +
            "WHERE {pt." + ProcessTaskModel.ACTION + "} = ?" + QUERY_PARAM_ACTION_TYPE + "\n" +
            "AND {o." + OrderModel.CODE + "} = ?" + QUERY_PARAM_ORDER_CODE + "\n" +
            "}}) x";

    /**
     * {@inheritDoc}
     *
     * @see ProcessDefinitionDao#findWaitingOrderProcesses(String, PaymentTransactionType)
     */
    @Override
    public List<BusinessProcessModel> findWaitingOrderProcesses(final String orderCode, final PaymentTransactionType paymentTransactionType) {
        validateParameterNotNull(paymentTransactionType, "Transaction type must not be null");
        validateParameterNotNull(orderCode, "Order code must not be null");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_BUSINESS_PROCESS_QUERY);
        query.addQueryParameter(QUERY_PARAM_ACTION_TYPE, WAIT_ID_PREFIX + paymentTransactionType.getCode());
        query.addQueryParameter(QUERY_PARAM_ORDER_CODE, orderCode);
        final SearchResult<BusinessProcessModel> searchResult = getFlexibleSearchService().search(query);

        return searchResult.getResult();
    }
}
