package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.forms.PaymentDetailsForm;
import com.worldpay.forms.validation.PaymentDetailsFormValidator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.UserFacade;
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

import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.PAYMENT_METHOD_PARAM;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayPaymentMethodCheckoutStepController.SAVE_PAYMENT_INFO;
import static com.worldpay.service.model.payment.PaymentType.ONLINE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayRedirectCheckoutStepControllerTest {

    @Spy
    @InjectMocks
    private WorldpayRedirectCheckoutStepController testObj = new WorldpayRedirectCheckoutStepController();

    private static final String BILLING_ERROR_VIEW = "billingErrorView";

    @Mock
    private PaymentDetailsForm paymentDetailsFormMock;
    @Mock
    private Model modelMock;
    @Mock
    private BindingResult bindingResultMock;
    @Mock
    private RedirectAttributes redirectAttrsMock;
    @Mock
    private UserFacade userFacadeMock;
    @Mock (name = "paymentDetailsFormValidator")
    private PaymentDetailsFormValidator paymentDetailsFormValidatorMock;
    @Mock
    private AddressForm addressFormMock;

    @Before
    public void setUp() throws Exception {
        doNothing().when(testObj).resetDeclineCodeOnCart();
        doNothing().when(testObj).handleAndSaveAddresses(paymentDetailsFormMock);
        doReturn(false).when(testObj).addGlobalErrors(modelMock, bindingResultMock);

        when(paymentDetailsFormMock.getSaveInAccount()).thenReturn(true);
        when(paymentDetailsFormMock.getPaymentMethod()).thenReturn(ONLINE.getMethodCode());
        when(paymentDetailsFormMock.getBillingAddress()).thenReturn(addressFormMock);
    }

    @Test
    public void addressShouldBeSavedIfCustomerIsAnonymousAndBillingAddressIsDifferentFromShippingAddress() throws CMSItemNotFoundException {
        when(paymentDetailsFormMock.getUseDeliveryAddress()).thenReturn(FALSE);
        when(userFacadeMock.isAnonymousUser()).thenReturn(TRUE);

        testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        verify(paymentDetailsFormValidatorMock).validate(paymentDetailsFormMock, bindingResultMock);
        verify(testObj).resetDeclineCodeOnCart();
        verify(testObj).handleAndSaveAddresses(paymentDetailsFormMock);

        verify(redirectAttrsMock).addFlashAttribute(SAVE_PAYMENT_INFO, true);
        verify(redirectAttrsMock).addFlashAttribute(PAYMENT_METHOD_PARAM, ONLINE.getMethodCode());
    }

    @Test
    public void shouldRedirectToErrorViewIfErrorsInForm() throws CMSItemNotFoundException {
        doReturn(true).when(testObj).addGlobalErrors(modelMock, bindingResultMock);
        doReturn(BILLING_ERROR_VIEW).when(testObj).handleFormErrors(modelMock, paymentDetailsFormMock);

        final String result = testObj.addPaymentDetails(modelMock, paymentDetailsFormMock, bindingResultMock, redirectAttrsMock);

        assertEquals(BILLING_ERROR_VIEW, result);
    }
}