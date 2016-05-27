package com.worldpay.facades.payment.hosted.impl;

import com.google.common.collect.ImmutableMap;
import com.worldpay.config.merchant.WorldpayMerchantConfigData;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.payment.merchant.WorldpayMerchantConfigDataFacade;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.merchant.strategies.WorldpayOrderInfoStrategy;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.payment.WorldpayRedirectOrderService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.facades.payment.hosted.impl.DefaultWorldpayHostedOrderFacade.WORLDPAY_MERCHANT_CODE;
import static de.hybris.platform.commerceservices.enums.UiExperienceLevel.DESKTOP;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayHostedOrderFacadeTest {

    private static final String MERCHANT_CODE = "merchantCode";
    private static final ImmutableMap<String, String> WORLDPAY_RESPONSE = ImmutableMap.of("responseKey", "responseValue");

    @InjectMocks
    private DefaultWorldpayHostedOrderFacade testObj = new DefaultWorldpayHostedOrderFacade();

    @Mock
    private WorldpayRedirectOrderService worldpayRedirectOrderServiceMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private UiExperienceService uiExperienceServiceMock;
    @Mock
    private CartService cartServiceMock;
    @Mock
    private WorldpayOrderInfoStrategy worldpayOrderInfoStrategyMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private SessionService sessionServiceMock;
    @Mock
    private WorldpayMerchantConfigData worldpayMerchantConfigDataMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private WorldpayMerchantConfigDataFacade worldpayMerchantConfigDataFacadeMock;

    @Before
    public void setUp() throws WorldpayConfigurationException {
        when(sessionServiceMock.getAttribute(WORLDPAY_MERCHANT_CODE)).thenReturn(MERCHANT_CODE);
        when(cartServiceMock.getSessionCart()).thenReturn(cartModelMock);
    }

    @Test
    public void shouldDoRedirectAuthorise() throws WorldpayException {
        when(worldpayMerchantInfoServiceMock.getCurrentSiteMerchant(DESKTOP)).thenReturn(merchantInfoMock);
        when(worldpayMerchantConfigDataFacadeMock.getCurrentSiteMerchantConfigData(DESKTOP)).thenReturn(worldpayMerchantConfigDataMock);
        when(uiExperienceServiceMock.getUiExperienceLevel()).thenReturn(DESKTOP);

        testObj.redirectAuthorise(additionalAuthInfoMock);

        verify(worldpayOrderInfoStrategyMock).populateAdditionalAuthInfo(additionalAuthInfoMock, worldpayMerchantConfigDataMock);
        verify(worldpayRedirectOrderServiceMock).redirectAuthorise(merchantInfoMock, cartModelMock, additionalAuthInfoMock);
    }

    @Test
    public void shouldCompleteRedirect() {
        testObj.completeRedirectAuthorise(redirectAuthoriseResultMock);

        verify(worldpayRedirectOrderServiceMock).completeRedirectAuthorise(redirectAuthoriseResultMock, MERCHANT_CODE, cartModelMock);
    }

    @Test
    public void shouldReturnTrueWhenRedirectResponseIsValid() throws WorldpayConfigurationException {
        when(worldpayMerchantInfoServiceMock.getMerchantInfoByCode(MERCHANT_CODE)).thenReturn(merchantInfoMock);
        when(worldpayRedirectOrderServiceMock.validateRedirectResponse(eq(merchantInfoMock), eq(WORLDPAY_RESPONSE))).thenReturn(true);

        assertTrue(testObj.validateRedirectResponse(WORLDPAY_RESPONSE));
    }

    @Test
    public void shouldReturnFalseWhenRedirectResponseIsNotValid() throws WorldpayConfigurationException {
        when(worldpayRedirectOrderServiceMock.validateRedirectResponse(eq(merchantInfoMock), eq(WORLDPAY_RESPONSE))).thenReturn(false);

        assertFalse(testObj.validateRedirectResponse(WORLDPAY_RESPONSE));
    }
}
