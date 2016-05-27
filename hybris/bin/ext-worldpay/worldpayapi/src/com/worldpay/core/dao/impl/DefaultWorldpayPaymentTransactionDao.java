package com.worldpay.core.dao.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_PENDING;

/**
 * {@inheritDoc}
 */
public class DefaultWorldpayPaymentTransactionDao extends AbstractItemDao implements WorldpayPaymentTransactionDao {

    protected static final String TIMEOUT_DATE_PARAMETER = "timeoutDate";
    protected static final String REQUESTID = "requestid";
    protected static final String ORDER_STATUS_PARAMETER = "orderStatus";
    protected static final String CREATION_TIME_PARAMETER = "creationTime";

    protected static final String QUERY_TRANSACTION_BY_REQUESTID_IN_ORDERS = "" +
            "SELECT {pk} " +
            "FROM {" + PaymentTransactionModel._TYPECODE + " AS pt}, {" + OrderModel._TYPECODE + " AS o} " +
            "WHERE {pt." + PaymentTransactionModel.REQUESTID + "} = ?" + REQUESTID + " " +
            "AND {pt." + OrderModel._TYPECODE + "} = {o.pk} " +
            "AND {o." + OrderModel.VERSIONID + "} IS NULL";

    protected static final String QUERY_TRANSACTION_BY_REQUESTID = "" +
            "SELECT x.PK FROM\n" +
            "({{\n" +
            "SELECT {pt.PK}\n" +
            "FROM \n" +
            "  {" + PaymentTransactionModel._TYPECODE + " AS pt},\n" +
            "  {" + OrderModel._TYPECODE + " AS o}\n" +
            " \n" +
            "\n" +
            "WHERE {pt." + REQUESTID + "} = ?" + REQUESTID + "\n" +
            "AND {pt." + PaymentTransactionModel.ORDER + "} = {o.pk}\n" +
            "AND {o." + OrderModel.VERSIONID + "} IS NULL\n" +
            "}}\n" +
            "UNION\n" +
            "{{\n" +
            "SELECT {pt.PK}\n" +
            "FROM \n" +
            "  {" + PaymentTransactionModel._TYPECODE + " AS pt},\n" +
            "  {" + CartModel._TYPECODE + " AS c}\n" +
            "\n" +
            "WHERE {pt." + REQUESTID + "} = ?" + REQUESTID + "\n" +
            "AND {pt." + PaymentTransactionModel.ORDER + "} = {c.pk}\n" +
            "}}) x\n";

    protected static final String PENDING_PAYMENT_TRANSACTION_QUERY = "" +
            "SELECT {pt." + PaymentTransactionModel.PK + "} " +
            "FROM { " + PaymentTransactionModel._TYPECODE + " AS pt " +
            "JOIN " + PaymentInfoModel._TYPECODE + "! AS pi " +
            "ON {pt." + PaymentTransactionModel.INFO + "} = {pi." + PaymentInfoModel.PK + "} " +
            "JOIN " + OrderModel._TYPECODE + " AS o " +
            "ON {pt." + PaymentTransactionModel.ORDER + "} = {o." + OrderModel.PK + "} } " +
            "WHERE {o." + OrderModel.VERSIONID + "} IS NULL " +
            "AND {o.status} = ?" + ORDER_STATUS_PARAMETER + " " +
            "AND {pi." + PaymentInfoModel.PAYMENTTYPE + "} IS NULL " +
            "AND {pt." + PaymentTransactionModel.CREATIONTIME + "} <= ?" + CREATION_TIME_PARAMETER;

    protected static final String CANCELLABLE_APM_PAYMENT_TRANSACTION_QUERY = "" +
            "SELECT {pt." + PaymentTransactionModel.PK + "} " +
            "FROM { " + PaymentTransactionModel._TYPECODE + " AS pt " +
            "JOIN " + WorldpayAPMPaymentInfoModel._TYPECODE + " AS pi " +
            "ON {pt." + PaymentTransactionModel.INFO + "} = {pi." + WorldpayAPMPaymentInfoModel.PK + "} " +
            "JOIN " + OrderModel._TYPECODE + " AS o " +
            "ON {pt." + PaymentTransactionModel.ORDER + "} = {o." + OrderModel.PK + "} " +
            "JOIN " + OrderStatus._TYPECODE + " AS os " +
            "ON {o." + OrderModel.STATUS + "} = {os.PK} } " +
            "WHERE {o." + OrderModel.VERSIONID + "} IS null " +
            "AND {os.code} = ?" + ORDER_STATUS_PARAMETER + " " +
            "AND {pi." + WorldpayAPMPaymentInfoModel.TIMEOUTDATE + "} <= ?" + TIMEOUT_DATE_PARAMETER;

    private FlexibleSearchService flexibleSearchService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PaymentTransactionModel> findPendingPaymentTransactions(final int waitTimeInMinutes) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(PENDING_PAYMENT_TRANSACTION_QUERY);
        final Date creationTimeParameter = DateUtils.addMinutes(Date.from(Instant.now()), -waitTimeInMinutes);

        query.addQueryParameter(CREATION_TIME_PARAMETER, creationTimeParameter);
        query.addQueryParameter(ORDER_STATUS_PARAMETER, PAYMENT_PENDING);

        final SearchResult<PaymentTransactionModel> result = flexibleSearchService.search(query);
        return result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionModel findPaymentTransactionByRequestIdFromOrdersOnly(final String requestId) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_TRANSACTION_BY_REQUESTID_IN_ORDERS);
        query.addQueryParameter(REQUESTID, requestId);
        return flexibleSearchService.<PaymentTransactionModel>searchUnique(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentTransactionModel findPaymentTransactionByRequestId(final String requestId) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_TRANSACTION_BY_REQUESTID);
        query.addQueryParameter(REQUESTID, requestId);
        return flexibleSearchService.<PaymentTransactionModel>searchUnique(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PaymentTransactionModel> findCancellablePendingAPMPaymentTransactions() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(CANCELLABLE_APM_PAYMENT_TRANSACTION_QUERY);

        query.addQueryParameter(TIMEOUT_DATE_PARAMETER, Date.from(Instant.now()));
        query.addQueryParameter(ORDER_STATUS_PARAMETER, PAYMENT_PENDING.getCode());

        final SearchResult<PaymentTransactionModel> result = flexibleSearchService.search(query);
        return result.getResult();
    }

    @Override
    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Override
    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
