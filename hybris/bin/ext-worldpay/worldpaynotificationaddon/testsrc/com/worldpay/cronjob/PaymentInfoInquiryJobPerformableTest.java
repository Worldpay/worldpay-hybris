package com.worldpay.cronjob;

import com.worldpay.config.WorldpayConfig;
import com.worldpay.config.WorldpayConfigLookupService;
import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.core.services.OrderInquiryService;
import com.worldpay.exception.WorldpayException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import com.worldpay.service.model.MerchantInfo;
import com.worldpay.service.response.OrderInquiryServiceResponse;
import com.worldpay.strategies.PaymentTransactionRejectionStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.worldpay.cronjob.PaymentInfoInquiryJobPerformable.WORLDPAY_APM_DAYS_BEFORE_STOP_INQUIRING_TIMEOUT;
import static com.worldpay.cronjob.PaymentInfoInquiryJobPerformable.WORLDPAY_APM_MINUTES_BEFORE_INQUIRING_TIMEOUT;
import static de.hybris.platform.cronjob.enums.CronJobResult.FAILURE;
import static de.hybris.platform.cronjob.enums.CronJobResult.SUCCESS;
import static de.hybris.platform.cronjob.enums.CronJobStatus.FINISHED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class PaymentInfoInquiryJobPerformableTest {

    private static final String REQUEST_TOKEN_APM = "requestTokenAPM";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final int WAIT_TIME = 15;
    private static final int BLANKET_TIMEOUT_DAYS = 5;
    public static final String SITE_UID = "siteUid";

    @InjectMocks
    private PaymentInfoInquiryJobPerformable testObj = new PaymentInfoInquiryJobPerformable();
    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private WorldpayConfigLookupService worldpayConfigLookupServiceMock;
    @Mock
    private WorldpayConfig worldpayConfigMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private CronJobModel cronjobModelMock;
    @Mock
    private WorldpayMerchantInfoService worldpayMerchantInfoServiceMock;
    @Mock
    private MerchantInfo merchantInfoMock;
    @Mock
    private OrderInquiryService orderInquiryServiceMock;
    @Mock
    private OrderInquiryServiceResponse orderInquiryServiceResponseMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurationService configurationServiceMock;
    @Mock
    private PaymentTransactionRejectionStrategy paymentTransactionRejectionStrategyMock;
    @Mock
    private Configuration configurationMock;

    @Before
    public void setUp() throws Exception {
        when(configurationServiceMock.getConfiguration()).thenReturn(configurationMock);
        when(configurationMock.getInt(WORLDPAY_APM_MINUTES_BEFORE_INQUIRING_TIMEOUT, WAIT_TIME)).thenReturn(WAIT_TIME);
        when(configurationMock.getInt(WORLDPAY_APM_DAYS_BEFORE_STOP_INQUIRING_TIMEOUT, BLANKET_TIMEOUT_DAYS)).thenReturn(BLANKET_TIMEOUT_DAYS);
        when(worldpayPaymentTransactionDaoMock.findPendingPaymentTransactions(WAIT_TIME)).thenReturn(Collections.singletonList(paymentTransactionModelMock));
        when(worldpayConfigLookupServiceMock.lookupConfig()).thenReturn(worldpayConfigMock);
        when(orderInquiryServiceMock.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock)).thenReturn(orderInquiryServiceResponseMock);
        when(paymentTransactionModelMock.getOrder().getSite().getUid()).thenReturn(SITE_UID);
        when(worldpayMerchantInfoServiceMock.getMerchantInfoFromTransaction(paymentTransactionModelMock)).thenReturn(merchantInfoMock);
        when(paymentTransactionModelMock.getRequestToken()).thenReturn(REQUEST_TOKEN_APM);
        when(paymentTransactionModelMock.getCreationtime()).thenReturn(DateTime.now().toDate());
    }

    @Test
    public void performShouldProcessOrderInquiryServiceResponse() throws Exception {

        final PerformResult result = testObj.perform(cronjobModelMock);

        assertEquals(SUCCESS, result.getResult());
        assertEquals(FINISHED, result.getStatus());

        verify(worldpayPaymentTransactionDaoMock).findPendingPaymentTransactions(WAIT_TIME);
        verify(worldpayMerchantInfoServiceMock).getMerchantInfoFromTransaction(paymentTransactionModelMock);
        verify(orderInquiryServiceMock).inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);
        verify(orderInquiryServiceMock).processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);
        verify(configurationMock).getInt(WORLDPAY_APM_MINUTES_BEFORE_INQUIRING_TIMEOUT, WAIT_TIME);
        verify(configurationMock).getInt(WORLDPAY_APM_DAYS_BEFORE_STOP_INQUIRING_TIMEOUT, BLANKET_TIMEOUT_DAYS);
        verify(paymentTransactionRejectionStrategyMock, never()).executeRejection(paymentTransactionModelMock);
    }

    @Test
    public void performShouldBreakWhenWorldpayExceptionThrown() throws Exception {
        when(orderInquiryServiceMock.inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock)).thenThrow(new WorldpayException(EXCEPTION_MESSAGE));

        final PerformResult result = testObj.perform(cronjobModelMock);

        assertEquals(FAILURE, result.getResult());
        assertEquals(FINISHED, result.getStatus());

        verify(worldpayPaymentTransactionDaoMock).findPendingPaymentTransactions(WAIT_TIME);
        verify(worldpayMerchantInfoServiceMock).getMerchantInfoFromTransaction(paymentTransactionModelMock);
        verify(orderInquiryServiceMock).inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);
        verify(orderInquiryServiceMock, never()).processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);
        verify(configurationMock).getInt(WORLDPAY_APM_MINUTES_BEFORE_INQUIRING_TIMEOUT, WAIT_TIME);
        verify(configurationMock).getInt(WORLDPAY_APM_DAYS_BEFORE_STOP_INQUIRING_TIMEOUT, BLANKET_TIMEOUT_DAYS);
        verify(paymentTransactionRejectionStrategyMock, never()).executeRejection(paymentTransactionModelMock);
    }

    @Test
    public void performShouldNotPollWhenPaymentTransactionModelIsOlderThanConfiguredBlanketTimeout() throws WorldpayException {
        when(paymentTransactionModelMock.getCreationtime()).thenReturn(new DateTime().minusDays(BLANKET_TIMEOUT_DAYS + 1).toDate());

        final PerformResult result = testObj.perform(cronjobModelMock);

        assertEquals(SUCCESS, result.getResult());
        assertEquals(FINISHED, result.getStatus());

        verify(worldpayPaymentTransactionDaoMock).findPendingPaymentTransactions(WAIT_TIME);
        verify(paymentTransactionModelMock).getCreationtime();
        verify(configurationMock).getInt(WORLDPAY_APM_MINUTES_BEFORE_INQUIRING_TIMEOUT, WAIT_TIME);
        verify(configurationMock).getInt(WORLDPAY_APM_DAYS_BEFORE_STOP_INQUIRING_TIMEOUT, BLANKET_TIMEOUT_DAYS);
        verify(orderInquiryServiceMock, never()).inquirePaymentTransaction(merchantInfoMock, paymentTransactionModelMock);
        verify(orderInquiryServiceMock, never()).processOrderInquiryServiceResponse(paymentTransactionModelMock, orderInquiryServiceResponseMock);
        verify(paymentTransactionRejectionStrategyMock).executeRejection(paymentTransactionModelMock);
    }
}