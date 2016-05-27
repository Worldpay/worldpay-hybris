package com.worldpay.dao.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Date;
import java.util.List;

import static com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel.TYPE;
import static com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel._TYPECODE;
import static de.hybris.platform.core.model.ItemModel.CREATIONTIME;
import static de.hybris.platform.core.model.ItemModel.PK;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * The default implementation of {@link OrderModificationDao} interface.
 */
public class DefaultOrderModificationDao extends AbstractItemDao implements OrderModificationDao {

    protected static final String NOTIFIED = "notified";
    protected static final String PROCESSED = "processed";
    protected static final String DEFECTIVE = "defective";
    protected static final String BEFORE_DATE = "maxDate";

    protected static final String PAYMENT_TRANSACTION_TYPE = "paymentTransactionType";

    protected static final String ORDER_MODIFICATION_PROCESS_QUERY =
            "select {wom." + PK + "}\n" +
                    "from {" + _TYPECODE + " as wom\n" +
                    "    \tJOIN " + PaymentTransactionType._TYPECODE + " as pt\n" +
                    "        \ton {wom." + TYPE + "} = {pt.PK}\n" +
                    "\t}\n" +
                    "where {pt.code} = ?" + PAYMENT_TRANSACTION_TYPE + "\n" +
                    "AND {wom." + WorldpayOrderModificationModel.PROCESSED + "} = ?" + PROCESSED;
    protected static final String ORDER_MODIFICATION_NOTIFICATION_QUERY =
            "select {" + PK + "}\n" +
                    "from {" + _TYPECODE + "}\n" +
                    "WHERE {" + WorldpayOrderModificationModel.PROCESSED + "} = ?" + PROCESSED + "\n" +
                    "AND {" + WorldpayOrderModificationModel.NOTIFIED + "} = ?" + NOTIFIED + "\n" +
                    "AND {" + CREATIONTIME + "} < ?" + BEFORE_DATE;
    protected static final String ORDER_MODIFICATION_CLEAN_UP_QUERY =
            "select {" + PK + "}\n" +
                    "from {" + _TYPECODE + "}\n" +
                    "WHERE {" + WorldpayOrderModificationModel.PROCESSED + "} = ?" + PROCESSED + "\n" +
                    "AND {" + WorldpayOrderModificationModel.DEFECTIVE + "} = ?" + DEFECTIVE + "\n" +
                    "AND {" + CREATIONTIME + "} < ?" + BEFORE_DATE;

    /**
     * {@inheritDoc}
     *
     * @see OrderModificationDao#findUnprocessedOrderModificationsByType(PaymentTransactionType)
     */
    @Override
    public List<WorldpayOrderModificationModel> findUnprocessedOrderModificationsByType(final PaymentTransactionType paymentTransactionType) {
        validateParameterNotNull(paymentTransactionType, "Transaction type must not be null");
        final FlexibleSearchQuery query = new FlexibleSearchQuery(ORDER_MODIFICATION_PROCESS_QUERY);
        query.addQueryParameter(PAYMENT_TRANSACTION_TYPE, paymentTransactionType.getCode());
        query.addQueryParameter(PROCESSED, false);
        final SearchResult<WorldpayOrderModificationModel> result = search(query);
        return result.getResult();
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderModificationDao#findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(Date)
     */
    @Override
    public List<WorldpayOrderModificationModel> findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(final Date date) {
        validateParameterNotNull(date, "Date must not be null");
        final FlexibleSearchQuery query = new FlexibleSearchQuery(ORDER_MODIFICATION_NOTIFICATION_QUERY);
        query.addQueryParameter(BEFORE_DATE, date);
        query.addQueryParameter(PROCESSED, false);
        query.addQueryParameter(NOTIFIED, false);
        final SearchResult<WorldpayOrderModificationModel> result = search(query);
        return result.getResult();
    }

    /**
     * {@inheritDoc}
     *
     * @see OrderModificationDao#findProcessedOrderModificationsBeforeDate(Date)
     */
    @Override
    public List<WorldpayOrderModificationModel> findProcessedOrderModificationsBeforeDate(final Date date) {
        validateParameterNotNull(date, "Date must not be null");
        final FlexibleSearchQuery query = new FlexibleSearchQuery(ORDER_MODIFICATION_CLEAN_UP_QUERY);
        query.addQueryParameter(BEFORE_DATE, date);
        query.addQueryParameter(PROCESSED, true);
        query.addQueryParameter(DEFECTIVE, false);
        final SearchResult<WorldpayOrderModificationModel> result = search(query);
        return result.getResult();
    }

    @Override
    public List<WorldpayOrderModificationModel> getExistingModifications(final WorldpayOrderModificationModel worldpayOrderModificationModel) {
        WorldpayOrderModificationModel exampleModel = new WorldpayOrderModificationModel();
        exampleModel.setWorldpayOrderCode(worldpayOrderModificationModel.getWorldpayOrderCode());
        exampleModel.setType(worldpayOrderModificationModel.getType());
        exampleModel.setDefectiveReason(worldpayOrderModificationModel.getDefectiveReason());
        return getFlexibleSearchService().getModelsByExample(exampleModel);
    }
}
