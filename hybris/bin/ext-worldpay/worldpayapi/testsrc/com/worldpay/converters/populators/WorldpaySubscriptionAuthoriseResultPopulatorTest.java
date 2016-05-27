package com.worldpay.converters.populators;

import com.worldpay.commands.WorldpaySubscriptionAuthorizeResult;
import com.worldpay.service.WorldpayAuthorisationResultService;
import com.worldpay.service.model.AuthorisedStatus;
import com.worldpay.service.model.ErrorDetail;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static com.worldpay.service.model.AuthorisedStatus.ERROR;
import static com.worldpay.service.model.AuthorisedStatus.REFUSED;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpaySubscriptionAuthoriseResultPopulatorTest {

    private static final String ORDER_CODE = "orderCode";
    private static final String REFERENCE_URL = "referenceUrl";
    private static final String ERROR_MESSAGE = "errorMessage";

    @InjectMocks
    private WorldpaySubscriptionAuthoriseResultPopulator testObj = new WorldpaySubscriptionAuthoriseResultPopulator();

    @Mock
    private WorldpayAuthorisationResultService worldpayAuthorisationResultServiceMock;

    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private RedirectReference redirectReferenceMock;
    @Mock
    private ErrorDetail errorDetailMock;

    @Before
    public void setUp() {
        when(directAuthoriseServiceResponseMock.getOrderCode()).thenReturn(ORDER_CODE);
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(paymentReplyMock);
    }

    @Test
    public void populateShouldSetAcceptedAndSuccessfulWhenDirectAuthoriseServiceResponseAuthorised() {
        when(paymentReplyMock.getAuthStatus()).thenReturn(AUTHORISED);

        final WorldpaySubscriptionAuthorizeResult result = new WorldpaySubscriptionAuthorizeResult();
        testObj.populate(directAuthoriseServiceResponseMock, result);

        verify(worldpayAuthorisationResultServiceMock).setAuthoriseResultByTransactionStatus(result, AUTHORISED, ORDER_CODE);
    }

    @Test
    public void populateShouldSetRejectedAndUnknownCodeWhenDirectAuthoriseServiceResponseNotAuthorised() {
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.REFUSED);

        final WorldpaySubscriptionAuthorizeResult result = new WorldpaySubscriptionAuthorizeResult();
        testObj.populate(directAuthoriseServiceResponseMock, result);

        verify(worldpayAuthorisationResultServiceMock).setAuthoriseResultByTransactionStatus(result, REFUSED, ORDER_CODE);
    }

    @Test
    public void populateShouldSetErrorAndGeneralSystemErrorWhenDirectAuthoriseServiceResponseError() {
        when(paymentReplyMock.getAuthStatus()).thenReturn(AuthorisedStatus.ERROR);

        final WorldpaySubscriptionAuthorizeResult result = new WorldpaySubscriptionAuthorizeResult();
        testObj.populate(directAuthoriseServiceResponseMock, result);

        verify(worldpayAuthorisationResultServiceMock).setAuthoriseResultByTransactionStatus(result, ERROR, ORDER_CODE);
    }

    @Test
    public void populateShouldSetErrorAndGeneralSystemErrorWhenDirectAuthoriseServiceResponseHasNoPaymentReplyAndIsNotAPM() {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponseMock.getErrorDetail()).thenReturn(errorDetailMock);
        when(errorDetailMock.getMessage()).thenReturn(ERROR_MESSAGE);

        final WorldpaySubscriptionAuthorizeResult result = new WorldpaySubscriptionAuthorizeResult();
        testObj.populate(directAuthoriseServiceResponseMock, result);

        verify(directAuthoriseServiceResponseMock).getErrorDetail();
        verify(worldpayAuthorisationResultServiceMock).setAuthoriseResultAsError(result);
    }

    @Test
    public void populateShouldSetRejectedAndReviewNeededWhenDirectAuthoriseServiceResponseHasNoPaymentReplyAndIsAPM() {
        when(directAuthoriseServiceResponseMock.getPaymentReply()).thenReturn(null);
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getUrl()).thenReturn(REFERENCE_URL);

        final WorldpaySubscriptionAuthorizeResult result = new WorldpaySubscriptionAuthorizeResult();
        testObj.populate(directAuthoriseServiceResponseMock, result);

        verify(worldpayAuthorisationResultServiceMock).setAuthorizeResultForAPM(directAuthoriseServiceResponseMock, result);
    }
}