package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.core.services.APMConfigurationLookupService;
import com.worldpay.data.AdditionalAuthInfo;
import com.worldpay.exception.WorldpayException;
import com.worldpay.facades.order.WorldpayPaymentCheckoutFacade;
import com.worldpay.facades.payment.hosted.WorldpayHostedOrderFacade;
import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.forms.validation.PaymentDetailsFormValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.PAYMENT_DATA;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.PAYMENT_METHOD_PARAM;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.SHOPPER_BANK_CODE;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayIframeCheckoutStepController.REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS;
import static com.worldpay.controllers.pages.checkout.steps.WorldpayIframeCheckoutStepController.SHOW_NGPP_IFRAME;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayIframeCheckoutStepControllerTest {

    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String ERROR_VIEW = "errorView";
    private static final String PAGE_REDIRECT = "pageRedirect";
    private static final String OTHER_PAYMENT = "otherPayment";
    private static final String BANK_CODE = "shopperBankCode";

    @Spy
    @InjectMocks
    private WorldpayIframeCheckoutStepController testObj = new WorldpayIframeCheckoutStepController();

    @Mock
    private RedirectAttributes redirectAttrbsMock;
    @Mock
    private Model modelMock;
    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private PaymentDetailsFormValidator paymentDetailsFormValidatorMock;
    @Mock
    private AddressForm billingAddressMock;
    @Mock
    private AdditionalAuthInfo additionalAuthInfoMock;
    @Mock
    private WorldpayHostedOrderFacade worldpayHostedOrderFacadeMock;
    @Mock
    private PaymentData paymentDataMock;
    @Mock
    private APMConfigurationLookupService apmConfigurationServiceMock;
    @Mock
    private WorldpayPaymentCheckoutFacade worldpayPaymentCheckoutFacade;

    @Before
    public void setUp() {
        when(apmConfigurationServiceMock.getAllApmPaymentTypeCodes()).thenReturn(singleton(OTHER_PAYMENT));
        when(paymentDetailsFormMock.getBillingAddress()).thenReturn(billingAddressMock);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(PAYMENT_METHOD);
        when(paymentDetailsFormMock.getShopperBankCode()).thenReturn(BANK_CODE);
        when(worldpayPaymentCheckoutFacade.hasBillingDetails()).thenReturn(true);
        doReturn(true).when(testObj).paymentMethodIsOnline(PAYMENT_METHOD);
        doReturn(additionalAuthInfoMock).when(testObj).createAdditionalAuthInfo(anyBoolean(), eq(PAYMENT_METHOD));
        doNothing().when(testObj).resetDeclineCodeOnCart();
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
    }

    @Test
    public void addPaymentDetailsShouldReturnIframePage() throws Exception {
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(additionalAuthInfoMock)).thenReturn(paymentDataMock);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrbsMock);

        verify(testObj).resetDeclineCodeOnCart();
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        verify(redirectAttrbsMock).addFlashAttribute(PAYMENT_DATA, paymentDataMock);
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(SHOPPER_BANK_CODE), any());
        verify(redirectAttrbsMock).addFlashAttribute(SHOW_NGPP_IFRAME, true);
        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        assertEquals(REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS, result);
    }

    @Test
    public void addPaymentDetailsShouldReturnBillingPageWithErrors() throws WorldpayException, CMSItemNotFoundException {
        when(worldpayHostedOrderFacadeMock.redirectAuthorise(additionalAuthInfoMock)).thenThrow(new WorldpayException(EXCEPTION_MESSAGE));

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrbsMock);

        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(testObj).resetDeclineCodeOnCart();
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(PAYMENT_DATA), any());
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(PAYMENT_METHOD_PARAM), any());
        verify(redirectAttrbsMock, never()).addFlashAttribute(eq(SHOPPER_BANK_CODE), any());
        verify(redirectAttrbsMock).addFlashAttribute(SHOW_NGPP_IFRAME, false);
        assertEquals(REDIRECT_CHECKOUT_MULTI_WORLDPAY_IFRAME_ADD_PAYMENT_DETAILS, result);
    }

    @Test
    public void addPaymentDetailsShouldReturnErrorsWhenValidationFails() throws CMSItemNotFoundException {
        when(testObj.addGlobalErrors(modelMock, bindingResultMock)).thenReturn(true);
        doReturn(ERROR_VIEW).when(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);

        testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrbsMock);

        verify(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);
    }

    @Test
    public void addPaymentDetailsShouldReturnFullPageRedirectForAPM() throws CMSItemNotFoundException {
        doReturn(PAGE_REDIRECT).when(testObj).getRedirectToPaymentMethod();
        when(apmConfigurationServiceMock.getAllApmPaymentTypeCodes()).thenReturn(singleton(PAYMENT_METHOD));

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrbsMock);

        assertEquals(PAGE_REDIRECT, result);
        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(redirectAttrbsMock).addFlashAttribute(PAYMENT_METHOD_PARAM, PAYMENT_METHOD);
        verify(redirectAttrbsMock).addFlashAttribute(SHOPPER_BANK_CODE, BANK_CODE);
        verify(testObj).resetDeclineCodeOnCart();
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
    }
}