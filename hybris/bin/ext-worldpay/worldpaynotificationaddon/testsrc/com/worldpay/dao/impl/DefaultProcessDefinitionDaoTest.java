package com.worldpay.dao.impl;

import com.worldpay.dao.ProcessDefinitionDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.processengine.model.BusinessProcessModel;
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
import java.util.List;

import static com.worldpay.dao.impl.DefaultProcessDefinitionDao.*;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultProcessDefinitionDaoTest {

    public static final String ORDER_CODE = "orderCode";
    @InjectMocks
    private ProcessDefinitionDao testObj = new DefaultProcessDefinitionDao();

    @Mock
    private FlexibleSearchService flexibleSearchServiceMock;
    @Mock
    private SearchResult searchResultMock;
    @Mock
    private BusinessProcessModel businessProcessModelMock;
    @Captor
    private ArgumentCaptor<FlexibleSearchQuery> queryArgumentCaptor;

    @Before
    public void setUp() {
        final List<BusinessProcessModel> resultMock = Collections.singletonList(businessProcessModelMock);
        when(searchResultMock.getResult()).thenReturn(resultMock);
        when(flexibleSearchServiceMock.search(queryArgumentCaptor.capture())).thenReturn(searchResultMock);
    }

    @Test
    public void testFindWaitingProcessForTransactionType() throws Exception {
        final List<BusinessProcessModel> result = testObj.findWaitingOrderProcesses(ORDER_CODE, AUTHORIZATION);

        assertTrue(result.size() == 1);
        assertSame(businessProcessModelMock, result.get(0));

        verify(flexibleSearchServiceMock).search(queryArgumentCaptor.capture());
        final FlexibleSearchQuery queryArgumentCaptorValue = queryArgumentCaptor.getValue();

        assertEquals(GET_BUSINESS_PROCESS_QUERY, queryArgumentCaptorValue.getQuery());
        assertEquals(WAIT_ID_PREFIX + AUTHORIZATION.getCode(), queryArgumentCaptorValue.getQueryParameters().get(QUERY_PARAM_ACTION_TYPE));
        assertEquals(ORDER_CODE, queryArgumentCaptorValue.getQueryParameters().get(QUERY_PARAM_ORDER_CODE));
    }
}