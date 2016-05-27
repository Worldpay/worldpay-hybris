package com.worldpay.core.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.worldpay.core.dao.impl.DefaultWorldpayPaymentTransactionDao.*;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_PENDING;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentTransactionDaoTest {

    public static final String TEST_REQUEST_ID = "testRequestId";
    @InjectMocks
    private DefaultWorldpayPaymentTransactionDao testObj = new DefaultWorldpayPaymentTransactionDao();

    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;
    @Captor
    private ArgumentCaptor<FlexibleSearchQuery> queryArgumentCaptor;
    @Mock
    private SearchResult<Object> searchResultMock;
    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;

    @Before
    public void setUp() {
        when(flexibleSearchServiceMock.search(queryArgumentCaptor.capture())).thenReturn(searchResultMock);
        when(flexibleSearchServiceMock.searchUnique(queryArgumentCaptor.capture())).thenReturn(paymentTransactionModelMock);
    }

    @Test
    public void testFindPendingPaymentTransactions() throws Exception {
        final List<Object> searchResult = singletonList(paymentTransactionModelMock);
        when(searchResultMock.getResult()).thenReturn(searchResult);

        final List<PaymentTransactionModel> result = testObj.findPendingPaymentTransactions(5);

        assertTrue(result.size() == 1);
        assertSame(paymentTransactionModelMock, result.get(0));

        verify(flexibleSearchServiceMock).search(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(PENDING_PAYMENT_TRANSACTION_QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(PAYMENT_PENDING, queryArgumentCaptorValue.getQueryParameters().get(ORDER_STATUS_PARAMETER));
        assertTrue(queryArgumentCaptorValue.getQueryParameters().containsKey(CREATION_TIME_PARAMETER));
    }

    @Test
    public void testFindPaymentTransactionByRequestIdFromOrdersOnly() throws Exception {
        final PaymentTransactionModel result = testObj.findPaymentTransactionByRequestIdFromOrdersOnly(TEST_REQUEST_ID);

        assertSame(paymentTransactionModelMock, result);

        verify(flexibleSearchServiceMock).searchUnique(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(QUERY_TRANSACTION_BY_REQUESTID_IN_ORDERS, queryArgumentCaptorValue.getQuery());
        assertEquals(TEST_REQUEST_ID, queryArgumentCaptorValue.getQueryParameters().get(REQUESTID));
    }

    @Test
    public void testFindPaymentTransactionByRequestId() throws Exception {
        final PaymentTransactionModel result = testObj.findPaymentTransactionByRequestId(TEST_REQUEST_ID);

        assertSame(paymentTransactionModelMock, result);

        verify(flexibleSearchServiceMock).searchUnique(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(QUERY_TRANSACTION_BY_REQUESTID, queryArgumentCaptorValue.getQuery());
        assertEquals(TEST_REQUEST_ID, queryArgumentCaptorValue.getQueryParameters().get(REQUESTID));
    }

    @Test
    public void testFindCancellablePendingAPMPaymentTransactions() throws Exception {
        final List<Object> searchResult = singletonList(paymentTransactionModelMock);
        when(searchResultMock.getResult()).thenReturn(searchResult);

        final List<PaymentTransactionModel> result = testObj.findCancellablePendingAPMPaymentTransactions();

        assertTrue(result.size() == 1);
        assertSame(paymentTransactionModelMock, result.get(0));

        verify(flexibleSearchServiceMock).search(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(CANCELLABLE_APM_PAYMENT_TRANSACTION_QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(PAYMENT_PENDING.getCode(), queryArgumentCaptorValue.getQueryParameters().get(ORDER_STATUS_PARAMETER));
        assertTrue(queryArgumentCaptorValue.getQueryParameters().containsKey(TIMEOUT_DATE_PARAMETER));
    }
}