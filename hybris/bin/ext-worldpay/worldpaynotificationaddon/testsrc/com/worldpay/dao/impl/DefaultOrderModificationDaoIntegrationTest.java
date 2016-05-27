package com.worldpay.dao.impl;

import com.worldpay.worldpaynotificationaddon.model.WorldpayOrderModificationModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.worldpay.util.WorldpayUtil.createDateInPast;
import static de.hybris.platform.payment.enums.PaymentTransactionType.*;
import static org.junit.Assert.assertEquals;

@IntegrationTest
public class DefaultOrderModificationDaoIntegrationTest extends ServicelayerTransactionalTest {

    public static final int EXPIRED_MESSAGE_TIME_IN_DAYS = 20;
    public static final String TEST_WORLDPAY_ORDER_CODE = "testWorldpayOrderCode";
    public static final String TEST_WORLDPAY_ORDER_CODE_PROCESSED = "testWorldpayOrderCodeProcessed";
    public static final String TMP_VAL = "tmpVal";

    private DefaultOrderModificationDao testObj = new DefaultOrderModificationDao();

    @Resource
    private ModelService modelService;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void setup() {
        testObj.setFlexibleSearchService(flexibleSearchService);

        final WorldpayOrderModificationModel worldpayOrderModificationAuth = modelService.create(WorldpayOrderModificationModel.class);
        worldpayOrderModificationAuth.setProcessed(false);
        worldpayOrderModificationAuth.setDefective(false);
        worldpayOrderModificationAuth.setType(AUTHORIZATION);
        worldpayOrderModificationAuth.setWorldpayOrderCode(TEST_WORLDPAY_ORDER_CODE);
        worldpayOrderModificationAuth.setOrderNotificationMessage(TMP_VAL);
        worldpayOrderModificationAuth.setNotified(true);
        worldpayOrderModificationAuth.setCreationtime(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS));

        final WorldpayOrderModificationModel worldpayUnprocessedOrderModificationCapture = modelService.create(WorldpayOrderModificationModel.class);
        worldpayUnprocessedOrderModificationCapture.setProcessed(false);
        worldpayUnprocessedOrderModificationCapture.setDefective(false);
        worldpayUnprocessedOrderModificationCapture.setType(CAPTURE);
        worldpayUnprocessedOrderModificationCapture.setWorldpayOrderCode(TEST_WORLDPAY_ORDER_CODE);
        worldpayUnprocessedOrderModificationCapture.setOrderNotificationMessage(TMP_VAL);
        worldpayUnprocessedOrderModificationCapture.setNotified(true);
        worldpayUnprocessedOrderModificationCapture.setCreationtime(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS));

        final WorldpayOrderModificationModel worldpayOrderModificationProcessed = modelService.create(WorldpayOrderModificationModel.class);
        worldpayOrderModificationProcessed.setProcessed(true);
        worldpayOrderModificationProcessed.setDefective(false);
        worldpayOrderModificationProcessed.setType(CAPTURE);
        worldpayOrderModificationProcessed.setWorldpayOrderCode(TEST_WORLDPAY_ORDER_CODE_PROCESSED);
        worldpayOrderModificationProcessed.setOrderNotificationMessage(TMP_VAL);
        worldpayOrderModificationProcessed.setNotified(true);
        worldpayOrderModificationProcessed.setCreationtime(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS));

        final WorldpayOrderModificationModel expiredUnprocessedOrderModificationMessage1 = modelService.create(WorldpayOrderModificationModel.class);
        expiredUnprocessedOrderModificationMessage1.setProcessed(false);
        expiredUnprocessedOrderModificationMessage1.setDefective(false);
        expiredUnprocessedOrderModificationMessage1.setType(CAPTURE);
        expiredUnprocessedOrderModificationMessage1.setWorldpayOrderCode("expiredUnprocessedOrderModificationMessage1");
        expiredUnprocessedOrderModificationMessage1.setOrderNotificationMessage(TMP_VAL);
        expiredUnprocessedOrderModificationMessage1.setNotified(true);
        expiredUnprocessedOrderModificationMessage1.setCreationtime(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS));

        final WorldpayOrderModificationModel expiredUnprocessedOrderModificationMessage2 = modelService.create(WorldpayOrderModificationModel.class);
        expiredUnprocessedOrderModificationMessage2.setProcessed(false);
        expiredUnprocessedOrderModificationMessage2.setDefective(false);
        expiredUnprocessedOrderModificationMessage2.setType(AUTHORIZATION);
        expiredUnprocessedOrderModificationMessage2.setWorldpayOrderCode("expiredUnprocessedOrderModificationMessage2");
        expiredUnprocessedOrderModificationMessage2.setOrderNotificationMessage(TMP_VAL);
        expiredUnprocessedOrderModificationMessage2.setNotified(true);
        expiredUnprocessedOrderModificationMessage2.setCreationtime(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS));

        final WorldpayOrderModificationModel notNotifiedAndExpiredUnprocessedOrderModificationMessage = modelService.create(WorldpayOrderModificationModel.class);
        notNotifiedAndExpiredUnprocessedOrderModificationMessage.setProcessed(false);
        notNotifiedAndExpiredUnprocessedOrderModificationMessage.setDefective(false);
        notNotifiedAndExpiredUnprocessedOrderModificationMessage.setType(AUTHORIZATION);
        notNotifiedAndExpiredUnprocessedOrderModificationMessage.setWorldpayOrderCode("notNotifiedAndExpiredUnprocessedOrderModificationMessage");
        notNotifiedAndExpiredUnprocessedOrderModificationMessage.setOrderNotificationMessage(TMP_VAL);
        notNotifiedAndExpiredUnprocessedOrderModificationMessage.setNotified(false);
        notNotifiedAndExpiredUnprocessedOrderModificationMessage.setCreationtime(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS));

        final WorldpayOrderModificationModel processedAndDefectiveOrderModificationMessage = modelService.create(WorldpayOrderModificationModel.class);
        processedAndDefectiveOrderModificationMessage.setProcessed(true);
        processedAndDefectiveOrderModificationMessage.setDefective(true);
        processedAndDefectiveOrderModificationMessage.setType(AUTHORIZATION);
        processedAndDefectiveOrderModificationMessage.setWorldpayOrderCode("processedAndDefectiveOrderModificationMessage");
        processedAndDefectiveOrderModificationMessage.setOrderNotificationMessage(TMP_VAL);
        processedAndDefectiveOrderModificationMessage.setNotified(false);
        processedAndDefectiveOrderModificationMessage.setCreationtime(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS));

        modelService.saveAll();
    }

    @Test
    public void findUnprocessedOrderModificationsByTypeAuth() {
        final List<WorldpayOrderModificationModel> result = testObj.findUnprocessedOrderModificationsByType(AUTHORIZATION);
        assertEquals(3, result.size());
    }

    @Test
    public void findUnprocessedOrderModificationsByTypeCapture() {
        final List<WorldpayOrderModificationModel> result = testObj.findUnprocessedOrderModificationsByType(CAPTURE);
        assertEquals(2, result.size());
    }

    @Test
    public void findUnprocessedOrderModificationsByTypeNonExistent() {
        final List<WorldpayOrderModificationModel> result = testObj.findUnprocessedOrderModificationsByType(REFUND_STANDALONE);
        assertEquals(0, result.size());
    }

    @Test
    public void shouldFindAllUnprocessedAndNotNotifiedOrderModificationsWhenAllOrderModificationsExistBeforeDatePassed() {
        final List<WorldpayOrderModificationModel> result = testObj.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(new Date());
        assertEquals(1, result.size());
    }

    @Test
    public void shouldFindZeroUnprocessedWhenNoOrderModificationsExistAfterDatePassed() {
        final List<WorldpayOrderModificationModel> result = testObj.findUnprocessedAndNotNotifiedOrderModificationsBeforeDate(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS + 1));
        assertEquals(0, result.size());
    }

    @Test
    public void shouldReturnAllProcessedOrderModificationsIfExistsBeforeDatePassed() {
        final List<WorldpayOrderModificationModel> processedOrderModificationsBeforeDate = testObj.findProcessedOrderModificationsBeforeDate(createDateInPast(0));
        assertEquals(1, processedOrderModificationsBeforeDate.size());
    }

    @Test
    public void shouldReturnNoProcessedOrderModificationsIfNoneExistAfterDatePassed() {
        final List<WorldpayOrderModificationModel> processedOrderModificationsBeforeDate = testObj.findProcessedOrderModificationsBeforeDate(createDateInPast(EXPIRED_MESSAGE_TIME_IN_DAYS + 1));
        assertEquals(0, processedOrderModificationsBeforeDate.size());
    }
}