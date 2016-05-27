package com.worldpay.cscockpit.services.search.generic.query;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cscockpit.services.search.impl.DefaultCsTextSearchCommand;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.cscockpit.services.search.generic.query.DefaultCartSearchQueryBuilder.TextField.CartId;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultCartSearchQueryBuilderTest {

    private static final String CART_CODE = "cartCode";
    private static final String WHERE_CART_CODE_CLAUSE = "WHERE {c:code} LIKE ?cartId";

    @InjectMocks
    private DefaultCartSearchQueryBuilder testObj = new DefaultCartSearchQueryBuilder();

    @Mock
    private DefaultCsTextSearchCommand defaultCsTextSearchCommandMock;

    @Test
    public void testBuildFlexibleSearchQueryWithCartId() {
        when(defaultCsTextSearchCommandMock.getText(CartId)).thenReturn(CART_CODE);
        final FlexibleSearchQuery result = testObj.buildFlexibleSearchQuery(defaultCsTextSearchCommandMock);

        assertNotNull(result);
        assertTrue(result.getQuery().contains(WHERE_CART_CODE_CLAUSE));
    }

    @Test
    public void testBuildFlexibleSearchQueryWithoutCartId() {
        when(defaultCsTextSearchCommandMock.getText(CartId)).thenReturn(null);
        final FlexibleSearchQuery result = testObj.buildFlexibleSearchQuery(defaultCsTextSearchCommandMock);

        assertNotNull(result);
        assertFalse(result.getQuery().contains(WHERE_CART_CODE_CLAUSE));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBuildFlexibleSearchQueryThrowsIllegalArgumentExceptionIfCommandIsNull() {
        // Did this for Gold certification. Sonar does not like null params.
        final DefaultCsTextSearchCommand defaultCsTextSearchCommandMock = null;
        testObj.buildFlexibleSearchQuery(defaultCsTextSearchCommandMock);
    }
}