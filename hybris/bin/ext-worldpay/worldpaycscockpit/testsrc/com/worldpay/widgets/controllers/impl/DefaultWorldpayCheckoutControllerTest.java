package com.worldpay.widgets.controllers.impl;

import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cscockpit.widgets.controllers.BasketController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayCheckoutControllerTest {

    @InjectMocks
    private DefaultWorldpayCheckoutController testObj = new DefaultWorldpayCheckoutController();

    @Mock
    private BasketController basketControllerMock;
    @Mock
    private TypedObject cartTypedObject;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private WorldpayOrderInfoStrategy worldpayOrderInfoStrategyMock;
    @Mock
    private WorldpayRedirectOrderService worldpayRedirectOrderServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private WorldpayMerchantConfigData customerServiceMerchantConfigDataMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;

    @Before
    public void setUp() {
        when(basketControllerMock.getCart()).thenReturn(cartTypedObject);
        when(cartTypedObject.getObject()).thenReturn(cartModelMock);
    }

    @Test
    public void testRedirectAuthorise() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getCustomerServicesMerchant()).thenReturn(merchantInfoMock);
        when(worldpayMerchantConfigDataFacadeMock.getCustomerServiceMerchantConfigData()).thenReturn(customerServiceMerchantConfigDataMock);
        when(worldpayOrderInfoStrategyMock.getAdditionalAuthInfo(customerServiceMerchantConfigDataMock)).thenReturn(additionalAuthInfoMock);

        testObj.redirectAuthorise();

        verify(worldpayRedirectOrderServiceMock).redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
        verify(worldpayMerchantInfoServiceMock).getCustomerServicesMerchant();
        verify(worldpayOrderInfoStrategyMock).getAdditionalAuthInfo(customerServiceMerchantConfigDataMock);
        verify(additionalAuthInfoMock).setUsingShippingAsBilling(FALSE);
        verify(additionalAuthInfoMock).setPaymentMethod(ONLINE.getMethodCode());
        verify(additionalAuthInfoMock).setSaveCard(TRUE);
    }
}