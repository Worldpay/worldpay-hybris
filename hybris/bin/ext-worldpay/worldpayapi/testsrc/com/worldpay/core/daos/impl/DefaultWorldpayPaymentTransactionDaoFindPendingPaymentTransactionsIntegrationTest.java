package com.worldpay.core.daos.impl;

import com.worldpay.core.dao.impl.DefaultWorldpayPaymentTransactionDao;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WorldpayAPMPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;

import static de.hybris.platform.core.enums.OrderStatus.CREATED;
import static de.hybris.platform.core.enums.OrderStatus.PAYMENT_PENDING;
import static org.junit.Assert.*;

@IntegrationTest
public class DefaultWorldpayPaymentTransactionDaoFindPendingPaymentTransactionsIntegrationTest extends ServicelayerTransactionalTest {

    private static final String PAYMENT_INFO_CODE = "paymentInfoCode";
    private static final String PAYMENT_TRANSACTION_CODE = "paymentTransactionCode";
    private static final String PAYMENT_TYPE = "paymentType";
    private static final int WAIT_TIME = 15;

    private DefaultWorldpayPaymentTransactionDao testObj = new DefaultWorldpayPaymentTransactionDao();

    @Resource
    private ModelService modelService;
    @Resource
    private FlexibleSearchService flexibleSearchService;
    @Resource
    private UserService userService;
    @Resource
    private OrderService orderService;

    private PaymentTransactionModel paymentTransactionModelWithoutTimeout;
    private PaymentTransactionModel paymentTransactionModelWithTimeout;
    private PaymentTransactionModel paymentTransactionModelWithPaymentType;
    private PaymentInfoModel paymentInfoModel;
    private PaymentInfoModel paymentInfoModelWithPaymentType;
    private WorldpayAPMPaymentInfoModel worldpayAPMPaymentInfoModelWithTimeout;
    private OrderModel orderModel;

    @Before
    public void setup() throws Exception {
        createCoreData();
        createDefaultCatalog();
        createHardwareCatalog();
        createDefaultUsers();
        importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");

        final UserModel userModel = userService.getUserForUID("ahertz");
        final Collection<CartModel> cartModels = userModel.getCarts();

        assertEquals(1, cartModels.size());
        final CartModel cartModel = cartModels.iterator().next();
        orderModel = orderService.createOrderFromCart(cartModel);
        testObj.setFlexibleSearchService(flexibleSearchService);

        paymentInfoModel = modelService.create(PaymentInfoModel.class);
        paymentInfoModel.setCode(PAYMENT_INFO_CODE);
        paymentInfoModel.setUser(userModel);

        worldpayAPMPaymentInfoModelWithTimeout = modelService.create(WorldpayAPMPaymentInfoModel.class);
        worldpayAPMPaymentInfoModelWithTimeout.setCode(PAYMENT_INFO_CODE);
        worldpayAPMPaymentInfoModelWithTimeout.setUser(userModel);
        worldpayAPMPaymentInfoModelWithTimeout.setTimeoutDate(new Date());

        paymentInfoModelWithPaymentType = modelService.create(PaymentInfoModel.class);
        paymentInfoModelWithPaymentType.setCode(PAYMENT_INFO_CODE);
        paymentInfoModelWithPaymentType.setUser(userModel);
        paymentInfoModelWithPaymentType.setPaymentType(PAYMENT_TYPE);

        modelService.saveAll();
    }

    @Test
    public void findPendingAPMPaymentTransactionsShouldNotReturnPaymentInfoWithTimeoutOrPaymentType() {
        createPaymentTransactions(-1);
        updateOrder(PAYMENT_PENDING);

        final List<PaymentTransactionModel> result = testObj.findPendingPaymentTransactions(WAIT_TIME);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.contains(paymentTransactionModelWithTimeout));
        assertFalse(result.contains(paymentTransactionModelWithPaymentType));
    }

    @Test
    public void findPendingAPMPaymentTransactionsShouldNotReturnPaymentInfoThatsNewlyCreated() {
        createPaymentTransactions(0);
        updateOrder(PAYMENT_PENDING);

        final List<PaymentTransactionModel> result = testObj.findPendingPaymentTransactions(WAIT_TIME);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findPendingPaymentInfoShouldReturnEmptyResultForNotPendingOrder() {
        createPaymentTransactions(-1);
        updateOrder(CREATED);

        final List<PaymentTransactionModel> result = testObj.findPendingPaymentTransactions(WAIT_TIME);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findCancellablePendingAPMPaymentTransactions() {
        createPaymentTransactions(-1);
        updateOrder(PAYMENT_PENDING);
        final List<PaymentTransactionModel> result = testObj.findCancellablePendingAPMPaymentTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(paymentTransactionModelWithTimeout));
        assertFalse(result.contains(paymentTransactionModelWithoutTimeout));
    }

    @Test
    public void findCancellablePendingAPMPaymentTransactionsShouldReturnEmptyResultForNotPendingOrder() {
        createPaymentTransactions(-1);
        updateOrder(CREATED);

        final List<PaymentTransactionModel> result = testObj.findCancellablePendingAPMPaymentTransactions();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    protected void updateOrder(final OrderStatus paymentPending) {
        orderModel.setStatus(paymentPending);
        orderModel.setPaymentTransactions(Arrays.asList(paymentTransactionModelWithoutTimeout, paymentTransactionModelWithTimeout, paymentTransactionModelWithPaymentType));
        modelService.save(orderModel);
    }

    protected void createPaymentTransactions(int daysToAdd) {
        paymentTransactionModelWithoutTimeout = modelService.create(PaymentTransactionModel.class);
        paymentTransactionModelWithoutTimeout.setCode(PAYMENT_TRANSACTION_CODE);
        paymentTransactionModelWithoutTimeout.setInfo(paymentInfoModel);
        paymentTransactionModelWithoutTimeout.setCreationtime(DateUtils.addDays(Calendar.getInstance().getTime(), daysToAdd));

        paymentTransactionModelWithTimeout = modelService.create(PaymentTransactionModel.class);
        paymentTransactionModelWithTimeout.setCode(PAYMENT_TRANSACTION_CODE);
        paymentTransactionModelWithTimeout.setInfo(worldpayAPMPaymentInfoModelWithTimeout);
        paymentTransactionModelWithTimeout.setCreationtime(DateUtils.addDays(Calendar.getInstance().getTime(), daysToAdd));

        paymentTransactionModelWithPaymentType = modelService.create(PaymentTransactionModel.class);
        paymentTransactionModelWithPaymentType.setCode(PAYMENT_TRANSACTION_CODE);
        paymentTransactionModelWithPaymentType.setInfo(paymentInfoModelWithPaymentType);
        paymentTransactionModelWithPaymentType.setCreationtime(DateUtils.addDays(Calendar.getInstance().getTime(), daysToAdd));

        modelService.saveAll(paymentTransactionModelWithoutTimeout, paymentTransactionModelWithTimeout, paymentTransactionModelWithPaymentType);
    }
}
