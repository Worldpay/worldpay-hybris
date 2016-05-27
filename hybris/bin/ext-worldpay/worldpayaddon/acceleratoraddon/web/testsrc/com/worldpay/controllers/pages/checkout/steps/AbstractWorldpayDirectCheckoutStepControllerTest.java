package com.worldpay.controllers.pages.checkout.steps;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.facades.order.impl.WorldpayCheckoutFacadeDecorator;
import com.worldpay.payment.DirectResponseData;
import com.worldpay.payment.TransactionStatus;
import com.worldpay.service.WorldpayUrlService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static com.worldpay.controllers.WorldpayaddonControllerConstants.Views.Pages.MultiStepCheckout.AutoSubmit3DSecure;
import static com.worldpay.controllers.pages.checkout.steps.AbstractWorldpayDirectCheckoutStepController.TERM_URL_PARAM_NAME;
import static com.worldpay.payment.TransactionStatus.AUTHENTICATION_REQUIRED;
import static com.worldpay.payment.TransactionStatus.AUTHORISED;
import static com.worldpay.payment.TransactionStatus.CANCELLED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class AbstractWorldpayDirectCheckoutStepControllerTest {

    private static final String SECURE_TERM_URL = "termUrl";
    private static final String ORDER_CODE = "orderCode";
    private static final int RETURN_CODE = 12;
    private static final String LOCALISED_DECLINE_MESSAGE = "localisedDeclineMessage";
    private static final String ERROR = "error";

    @Spy
    @InjectMocks
    private AbstractWorldpayDirectCheckoutStepController testObj = new TestAbstractWorldpayDirectCheckoutStepController();
    @Mock
    private DirectResponseData directResponseDataMock;
    @Mock
    private Model modelMock;
    @Mock
    private OrderData orderDataMock;
    @Mock
    private WorldpayUrlService worldpayUrlServiceMock;
    @Mock
    private CartData cartDataMock;
    @Mock
    private WorldpayCheckoutFacadeDecorator checkoutFacadeMock;

    @Before
    public void setup() {
        doReturn(LOCALISED_DECLINE_MESSAGE).when(testObj).getLocalisedDeclineMessage(RETURN_CODE);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHORISED);
        when(directResponseDataMock.getOrderData()).thenReturn(orderDataMock);
        when(directResponseDataMock.getReturnCode()).thenReturn(RETURN_CODE);
        when(orderDataMock.getCode()).thenReturn(ORDER_CODE);
        when(checkoutFacadeMock.getCheckoutCart()).thenReturn(cartDataMock);
    }

    @Test
    public void shouldRedirectTo3dSecureIfAuthenticationRequired() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHENTICATION_REQUIRED);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock);

        assertEquals(AutoSubmit3DSecure, result);
    }

    @Test
    public void shouldAdd3DSecureTermUrlToModelIfPaymentRequiresAuth() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(worldpayUrlServiceMock.getFullThreeDSecureTermURL()).thenReturn(SECURE_TERM_URL);
        when(directResponseDataMock.getTransactionStatus()).thenReturn(AUTHENTICATION_REQUIRED);

        testObj.handleDirectResponse(modelMock, directResponseDataMock);

        verify(modelMock).addAttribute(TERM_URL_PARAM_NAME, SECURE_TERM_URL);
    }

    @Test
    public void shouldReturnErrorPageWhenCancelled() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(CANCELLED);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock);

        assertEquals(ERROR, result);
    }

    @Test
    public void shouldRedirectToErrorIfResponseIsNotAuthorisedOrAuthRequired() throws CMSItemNotFoundException, WorldpayConfigurationException {
        when(directResponseDataMock.getTransactionStatus()).thenReturn(TransactionStatus.ERROR);

        final String result = testObj.handleDirectResponse(modelMock, directResponseDataMock);

        assertEquals(ERROR, result);
    }

    public class TestAbstractWorldpayDirectCheckoutStepController extends AbstractWorldpayDirectCheckoutStepController {
        @Override
        protected String getErrorView(final Model model) throws CMSItemNotFoundException {
            return ERROR;
        }
    }
}