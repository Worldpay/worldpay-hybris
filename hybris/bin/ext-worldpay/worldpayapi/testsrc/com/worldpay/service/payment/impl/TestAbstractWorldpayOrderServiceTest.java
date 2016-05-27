package com.worldpay.service.payment.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class TestAbstractWorldpayOrderServiceTest {

    private static final String PAYMENT_PROVIDER = "paymentProvider";

    public class TestAbstractWorldpayOrderService extends AbstractWorldpayOrderService {
    }

    @InjectMocks
    private AbstractWorldpayOrderService testObj = new TestAbstractWorldpayOrderService();

    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private CartModel cartModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private CommerceCheckoutService commerceCheckoutServiceMock;

    @Test
    public void shouldCreateCommerceCheckoutParameter() {
        when(commerceCheckoutServiceMock.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);

        final CommerceCheckoutParameter result = testObj.createCommerceCheckoutParameter(cartModelMock, paymentInfoModelMock, BigDecimal.ONE);

        assertEquals(paymentInfoModelMock, result.getPaymentInfo());
        assertTrue(result.isEnableHooks());
        assertEquals(BigDecimal.ONE, result.getAuthorizationAmount());
        assertEquals(PAYMENT_PROVIDER, result.getPaymentProvider());
        assertEquals(cartModelMock, result.getCart());
    }
}