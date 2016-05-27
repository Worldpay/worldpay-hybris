package com.worldpay.service.impl;

import com.worldpay.commands.WorldpaySubscriptionAuthorizeResult;
import com.worldpay.service.model.RedirectReference;
import com.worldpay.service.response.DirectAuthoriseServiceResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.AbstractResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.service.model.AuthorisedStatus.AUTHORISED;
import static com.worldpay.service.model.AuthorisedStatus.ERROR;
import static com.worldpay.service.model.AuthorisedStatus.FAILURE;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.GENERAL_SYSTEM_ERROR;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.REVIEW_NEEDED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.UNKNOWN_CODE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayAuthorisationResultServiceTest {

    private static final String REDIRECT_REFERENCE = "redirectReference";
    private static final String ORDER_CODE = "orderCode";

    @InjectMocks
    private DefaultWorldpayAuthorisationResultService testObj = new DefaultWorldpayAuthorisationResultService();

    @Mock
    private AbstractResult abstractResultMock;
    @Mock
    private DirectAuthoriseServiceResponse directAuthoriseServiceResponseMock;
    @Mock
    private WorldpaySubscriptionAuthorizeResult worldpaySubscriptionAuthorizeResultMock;
    @Mock
    private RedirectReference redirectReferenceMock;

    @Test
    public void testSetAuthoriseResultAsError() throws Exception {
        testObj.setAuthoriseResultAsError(abstractResultMock);

        verify(abstractResultMock).setTransactionStatus(TransactionStatus.ERROR);
        verify(abstractResultMock).setTransactionStatusDetails(GENERAL_SYSTEM_ERROR);
    }

    @Test
    public void testSetAuthoriseResultERROR() throws Exception {
        testObj.setAuthoriseResultByTransactionStatus(abstractResultMock, ERROR, ORDER_CODE);

        verify(abstractResultMock).setTransactionStatus(TransactionStatus.ERROR);
        verify(abstractResultMock).setTransactionStatusDetails(GENERAL_SYSTEM_ERROR);
    }

    @Test
    public void testSetAuthoriseResultAUTHORISED() throws Exception {
        testObj.setAuthoriseResultByTransactionStatus(abstractResultMock, AUTHORISED, ORDER_CODE);

        verify(abstractResultMock).setTransactionStatus(TransactionStatus.ACCEPTED);
        verify(abstractResultMock).setTransactionStatusDetails(SUCCESFULL);
    }

    @Test
    public void testSetAuthoriseResultOTHER() throws Exception {
        testObj.setAuthoriseResultByTransactionStatus(abstractResultMock, FAILURE, ORDER_CODE);

        verify(abstractResultMock).setTransactionStatus(TransactionStatus.REJECTED);
        verify(abstractResultMock).setTransactionStatusDetails(UNKNOWN_CODE);
    }

    @Test
    public void testSetAuthorizeResultForAPM() throws Exception {
        when(directAuthoriseServiceResponseMock.getRedirectReference()).thenReturn(redirectReferenceMock);
        when(redirectReferenceMock.getUrl()).thenReturn(REDIRECT_REFERENCE);

        testObj.setAuthorizeResultForAPM(directAuthoriseServiceResponseMock, worldpaySubscriptionAuthorizeResultMock);

        verify(worldpaySubscriptionAuthorizeResultMock).setTransactionStatus(TransactionStatus.REJECTED);
        verify(worldpaySubscriptionAuthorizeResultMock).setTransactionStatusDetails(REVIEW_NEEDED);
        verify(worldpaySubscriptionAuthorizeResultMock).setPaymentRedirectRequired(true);
        verify(worldpaySubscriptionAuthorizeResultMock).setPaymentRedirectUrl(REDIRECT_REFERENCE);
    }
}
    

