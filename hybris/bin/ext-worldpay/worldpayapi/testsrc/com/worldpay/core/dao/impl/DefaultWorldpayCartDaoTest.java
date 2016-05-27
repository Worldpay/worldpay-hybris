package com.worldpay.core.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.worldpay.core.dao.impl.DefaultWorldpayCartDao.PARAM_WORLD_PAY_ORDER_CODE;
import static com.worldpay.core.dao.impl.DefaultWorldpayCartDao.QUERY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayCartDaoTest {

    public static final String ORDER_CODE = "orderCode";
    @InjectMocks
    private DefaultWorldpayCartDao testObj = new DefaultWorldpayCartDao();

    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;
    @Mock
    private SearchResult searchResultMock;
    @Mock
    private CartModel cartModelMock;
    @Captor
    private ArgumentCaptor<FlexibleSearchQuery> queryArgumentCaptor;

    @Test
    public void testGetCartsByWorldpayOrderCode() throws Exception {
        final List<CartModel> resultMock = Collections.singletonList(cartModelMock);
        when(searchResultMock.getResult()).thenReturn(resultMock);
        when(flexibleSearchServiceMock.search(queryArgumentCaptor.capture())).thenReturn(searchResultMock);

        final List<CartModel> result = testObj.findCartsByWorldpayOrderCode(ORDER_CODE);

        assertTrue(result.size() == 1);
        assertSame(cartModelMock, result.get(0));

        verify(flexibleSearchServiceMock).search(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(ORDER_CODE, queryArgumentCaptorValue.getQueryParameters().get(PARAM_WORLD_PAY_ORDER_CODE));
    }
}