package com.worldpay.core.services.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayGenerateMerchantTransactionCodeStrategyTest {

    private static final String CART_CODE = "cartCode";
    private static final Long TIME_STAMP = 12345L;

    @Spy
    @InjectMocks
    private WorldpayGenerateMerchantTransactionCodeStrategy testObj = new WorldpayGenerateMerchantTransactionCodeStrategy();

    @Mock
    private CartModel cartModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private CartService cartServiceMock;

    @Test
    public void generatedCodeShouldContainOrderCode() throws Exception {
        doReturn(TIME_STAMP).when(testObj).getTime();
        when(cartModelMock.getCode()).thenReturn(CART_CODE);

        final String result = testObj.generateCode(cartModelMock);

        assertTrue(result.startsWith(cartModelMock.getCode() + "-"));
        assertTrue(result.endsWith(String.valueOf(TIME_STAMP)));
    }

    @Test
    public void shouldSetWorldpayOrderOrderCodeOnCart() {

        testObj.generateCode(cartModelMock);

        verify(cartModelMock).setWorldpayOrderCode(anyString());
        verify(modelServiceMock).save(cartModelMock);
    }


    @Test
    public void shouldSetWorldpayOrderOrderCodeOnSessionCart() {
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);

        testObj.generateCode(null);

        verify(cartModelMock).setWorldpayOrderCode(anyString());
        verify(modelServiceMock).save(cartModelMock);
    }
}