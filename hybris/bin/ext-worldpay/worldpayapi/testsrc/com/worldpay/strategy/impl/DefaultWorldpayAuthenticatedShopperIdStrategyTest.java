package com.worldpay.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayAuthenticatedShopperIdStrategyTest {

    private static final String CUSTOMER_ID = "customerId";
    private static final String ORIGINAL_UID = "originalUid";

    @InjectMocks
    private DefaultWorldpayAuthenticatedShopperIdStrategy testObj = new DefaultWorldpayAuthenticatedShopperIdStrategy();

    @Mock
    private CustomerModel customerModelMock;

    @Mock
    private UserModel userModelMock;

    @SuppressWarnings("PMD")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnCustomerIdAsAuthenticatedShopperId() throws Exception {
        when(customerModelMock.getCustomerID()).thenReturn(CUSTOMER_ID);

        final String result = testObj.getAuthenticatedShopperId(customerModelMock);

        assertEquals(CUSTOMER_ID, result);
    }

    @Test
    public void shouldReturnOriginalUIDIfCustomerIdIsNotSet() throws Exception {
        when(customerModelMock.getCustomerID()).thenReturn("");
        when(customerModelMock.getOriginalUid()).thenReturn(ORIGINAL_UID);

        final String result = testObj.getAuthenticatedShopperId(customerModelMock);

        assertEquals(ORIGINAL_UID, result);
    }

    @Test
    public void shouldThrowExceptionIfNotCustomer() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(format("The user {0} is not of type Customer.", userModelMock));

        testObj.getAuthenticatedShopperId(userModelMock);
    }
    @Test
    public void shouldRaiseErrorIfUserIsNull() throws Exception {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("The user is null");

        testObj.getAuthenticatedShopperId(null);
    }
}