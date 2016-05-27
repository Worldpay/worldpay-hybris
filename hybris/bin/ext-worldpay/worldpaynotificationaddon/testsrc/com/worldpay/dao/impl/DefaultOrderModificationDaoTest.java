package com.worldpay.dao.impl;

import com.worldpay.dao.OrderModificationDao;
import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.UnitTest;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.worldpay.dao.impl.DefaultOrderModificationDao.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderModificationDaoTest {

    @InjectMocks
    private OrderModificationDao testObj = new DefaultOrderModificationDao();
    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;
    @Mock
    private SearchResult searchResultMock;
    @Mock
    private WorldpayOrderModificationModel worldpayOrderModificationModelMock;
    @Captor
    private ArgumentCaptor<FlexibleSearchQuery> queryArgumentCaptor;

    @Before
    public void setUp() {
        final List<WorldpayOrderModificationModel> resultMock = Collections.singletonList(worldpayOrderModificationModelMock);
        when(searchResultMock.getResult()).thenReturn(resultMock);
        when(flexibleSearchServiceMock.search(queryArgumentCaptor.capture())).thenReturn(searchResultMock);
    }

    @Test
    public void testFindUnprocessedOrderModificationsByType() throws Exception {
        final List<WorldpayOrderModificationModel> result = testObj.findUnprocessedOrderModificationsByType(AUTHORIZATION);

        assertTrue(result.size() == 1);
        assertSame(worldpayOrderModificationModelMock, result.get(0));

        verify(flexibleSearchServiceMock).search(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(ORDER_MODIFICATION_PROCESS_QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(AUTHORIZATION.getCode(), queryArgumentCaptorValue.getQueryParameters().get(PAYMENT_TRANSACTION_TYPE));
        assertEquals(false, queryArgumentCaptorValue.getQueryParameters().get(PROCESSED));
    }

    @Test
    public void testFindUnprocessedAndNotNotifiedOrderModificationsBeforeDate() throws Exception {
        final Date myDate = new Date();
        final List<WorldpayOrderModificationModel> result = testObj.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(myDate);

        assertTrue(result.size() == 1);
        assertSame(worldpayOrderModificationModelMock, result.get(0));

        verify(flexibleSearchServiceMock).search(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(ORDER_MODIFICATION_NOTIFICATION_QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(myDate, queryArgumentCaptorValue.getQueryParameters().get(BEFORE_DATE));
        assertEquals(false, queryArgumentCaptorValue.getQueryParameters().get(PROCESSED));
        assertEquals(false, queryArgumentCaptorValue.getQueryParameters().get(NOTIFIED));
    }

    @Test
    public void testFindProcessedOrderModificationsBeforeDate() throws Exception {
        final Date myDate = new Date();
        final List<WorldpayOrderModificationModel> result = testObj.findProcessedOrderModificationsBeforeDate(myDate);

        assertTrue(result.size() == 1);
        assertSame(worldpayOrderModificationModelMock, result.get(0));

        verify(flexibleSearchServiceMock).search(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(ORDER_MODIFICATION_CLEAN_UP_QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(myDate, queryArgumentCaptorValue.getQueryParameters().get(BEFORE_DATE));
        assertEquals(true, queryArgumentCaptorValue.getQueryParameters().get(PROCESSED));
        assertEquals(false, queryArgumentCaptorValue.getQueryParameters().get(DEFECTIVE));
    }
}