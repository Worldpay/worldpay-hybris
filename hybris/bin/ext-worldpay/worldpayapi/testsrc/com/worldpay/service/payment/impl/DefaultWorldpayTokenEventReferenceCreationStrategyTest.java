package com.worldpay.service.payment.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayTokenEventReferenceCreationStrategyTest {

    public static final String CART_CODE = "cartCode";
    @InjectMocks
    private DefaultWorldpayTokenEventReferenceCreationStrategy testObj = new DefaultWorldpayTokenEventReferenceCreationStrategy();
    @Mock
    private CartService cartServiceMock;

    @Mock
    private CartModel cartModelMock;

    @Test
    public void shouldCreateATokenReferenceUsingCartCode() {

        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
        when(cartModelMock.getCode()).thenReturn(CART_CODE);

        final String result = testObj.createTokenEventReference();

        assertTrue(result.startsWith(CART_CODE + "_"));
    }
}