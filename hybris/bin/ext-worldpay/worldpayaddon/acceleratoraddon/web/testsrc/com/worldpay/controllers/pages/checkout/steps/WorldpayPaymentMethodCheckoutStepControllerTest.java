package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.controllers.WorldpayaddonControllerConstants;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.data.BankTransferAdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.WorldpayBankConfigurationFacade;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.WorldpayAdditionalInfoFacade;
import com.worldpay.facades.payment.direct.WorldpayDirectOrderFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.order.data.WorldpayAdditionalInfoData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutGroup;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.order.CartFacade;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.CMS_PAGE_TITLE;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.HOP_DEBUG_MODE_PARAM;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.HOSTED_ORDER_PAGE_DATA;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.PAYMENT_METHOD_PARAM;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.REQUEST;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.SAVE_PAYMENT_INFO;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.SHOPPER_BANK_CODE;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayPaymentMethodCheckoutStepController.CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayPaymentMethodCheckoutStepController.HOP_DEBUG_MODE_CONFIG;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayPaymentMethodCheckoutStepController.PREFERRED_PAYMENT_METHOD_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayPaymentMethodCheckoutStepControllerTest {

    private static final String CHECKOUT_FLOW_GROUP_KEY = "checkoutFlowGroupKey";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String PAYMENT_METHOD_BANK_TRANSFER = "PAYMENT_METHOD_BANK_TRANSFER";
    private static final String SHOPPER_BANK_CODE_VALUE = "shopperBankCode";
    private static final String REDIRECT_URL = "redirectUrl";

    @Spy
    @InjectMocks
    private WorldpayPaymentMethodCheckoutStepController testObj = new WorldpayPaymentMethodCheckoutStepController();

    @Mock
    private AcceleratorCheckoutFacade acceleratorCheckoutFacadeMock;
    @Mock
    private CheckoutFlowFacade checkoutFlowFacadeMock;
    @Mock
    private SiteConfigService siteConfigServiceMock;
    @Mock
    private CartFacade cartFacadeMock;
    @Mock
    private ResourceBreadcrumbBuilder resourceBreadcrumbBuilderMock;
    @Mock
    private CMSPageService cmsPageServiceMock;
    @Mock
    private PageTitleResolver pageTitleResolver;
    @Mock (name = "checkoutFlowGroupMap")
    private Map<String, CheckoutGroup> checkoutGroupMap;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacadeMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private WorldpayDirectOrderFacade worldpayDirectOrderFacadeMock;
    @Mock
    private WorldpayBankConfigurationFacade bankConfigurationFacadeMock;

    @Mock (answer = RETURNS_DEEP_STUBS)
    private CheckoutGroup checkoutGroupMock;
    @Mock
    private RedirectAttributes redirectAttributesMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private Model modelMock;
    @Mock
    private ContentPageModel contentPageModelMock;
    @Mock
    private CheckoutStep checkoutStepMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock
    private Map<String, String> paymentDataParametersMock;
    @Mock
    private Map<String, CheckoutStep> checkoutStepMapMock;
    @Mock (answer = RETURNS_DEEP_STUBS)
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private WorldpayAdditionalInfoData worldpayAdditionalInfoDataMock;
    @Mock
    private WorldpayAdditionalInfoFacade worldpayAdditionalInfoFacadeMock;

    @Before
    public void setup() throws Exception {
        when(cmsPageServiceMock.getPageForLabelOrId(anyString())).thenReturn(contentPageModelMock);
        when(contentPageModelMock.getTitle()).thenReturn(CMS_PAGE_TITLE);
        when(acceleratorCheckoutFacadeMock.getCheckoutFlowGroupForCheckout()).thenReturn(CHECKOUT_FLOW_GROUP_KEY);
        when(checkoutGroupMap.get(CHECKOUT_FLOW_GROUP_KEY)).thenReturn(checkoutGroupMock);
        when(checkoutGroupMock.getCheckoutStepMap()).thenReturn(checkoutStepMapMock);
        when(checkoutStepMapMock.get(anyString())).thenReturn(checkoutStepMock);
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(any(AdditionalAuthInfo.class))).thenReturn(paymentDataMock);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD);
        when(modelMock.asMap().get(SAVE_PAYMENT_INFO)).thenReturn(true);
        when(modelMock.asMap().get(SHOPPER_BANK_CODE)).thenReturn(SHOPPER_BANK_CODE_VALUE);
        when(modelMock.asMap().get(REQUEST)).thenReturn(httpServletRequestMock);
        when(paymentDataMock.getParameters()).thenReturn(paymentDataParametersMock);
        when(worldpayAdditionalInfoFacadeMock.createWorldpayAdditionalInfoData(httpServletRequestMock)).thenReturn(worldpayAdditionalInfoDataMock);
        when(siteConfigServiceMock.getBoolean(HOP_DEBUG_MODE_CONFIG, false)).thenReturn(false);
    }

    @Test
    public void testEnterStepForCard() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(siteConfigServiceMock).getBoolean("oms.enabled", false);
        verify(checkoutFlowFacadeMock).hasNoPaymentInfo();
        verify(cartFacadeMock).getDeliveryCountries();
        verify(worldpayPaymentCheckoutFacadeMock).hasBillingDetails();
        verify(resourceBreadcrumbBuilderMock).getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB);
        verify(pageTitleResolver).resolveContentPageTitle(CMS_PAGE_TITLE);
        verify(modelMock).addAttribute(HOSTED_ORDER_PAGE_DATA, paymentDataMock);
        verify(modelMock).addAttribute(HOP_DEBUG_MODE_PARAM, false);
        verify(paymentDataParametersMock, never()).put(eq(PREFERRED_PAYMENT_METHOD_PARAM), any());
        assertEquals(WorldpayaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage, result);
    }

    @Test
    public void testEnterStepRedirectsToBankTransfer() throws CMSItemNotFoundException, WorldpayException {
        doReturn(false).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD_BANK_TRANSFER);
        when(bankConfigurationFacadeMock.isBankTransferApm(PAYMENT_METHOD_BANK_TRANSFER)).thenReturn(true);
        when(worldpayDirectOrderFacadeMock.authoriseBankTransferRedirect(any(BankTransferAdditionalAuthInfo.class), eq(worldpayAdditionalInfoDataMock))).thenReturn(REDIRECT_URL);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(siteConfigServiceMock).getBoolean("oms.enabled", false);
        verify(checkoutFlowFacadeMock).hasNoPaymentInfo();
        verify(cartFacadeMock).getDeliveryCountries();
        verify(worldpayPaymentCheckoutFacadeMock).hasBillingDetails();
        verify(resourceBreadcrumbBuilderMock).getBreadcrumbs(CHECKOUT_MULTI_PAYMENT_METHOD_BREADCRUMB);
        verify(pageTitleResolver).resolveContentPageTitle(CMS_PAGE_TITLE);

        assertEquals("redirect:" + REDIRECT_URL, result);
    }

    @Test
    public void testEnterStepRedirectToHopPageIfNotBankTransferPayment() throws CMSItemNotFoundException, WorldpayException {
        doReturn(false).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD);
        when(modelMock.asMap().get(PAYMENT_METHOD_PARAM)).thenReturn(PAYMENT_METHOD);

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        verify(modelMock).addAttribute(HOSTED_ORDER_PAGE_DATA, paymentDataMock);
        verify(paymentDataParametersMock).put(PREFERRED_PAYMENT_METHOD_PARAM, PAYMENT_METHOD);
        assertEquals(WorldpayaddonControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage, result);
    }

    @Test
    public void shouldReturnToChoosePaymentMethodOnWorldpayException() throws WorldpayException, CMSItemNotFoundException {
        doThrow(WorldpayException.class).when(worldpayHostedOrderFacadeMock).redirectAuthorise(any());

        final String result = testObj.enterStep(modelMock, redirectAttributesMock);

        assertEquals(WorldpayPaymentMethodCheckoutStepController.REDIRECT_URL_CHOOSE_PAYMENT_METHOD, result);
    }
}