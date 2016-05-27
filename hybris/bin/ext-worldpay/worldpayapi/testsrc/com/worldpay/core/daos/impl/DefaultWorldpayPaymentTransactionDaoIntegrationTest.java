package com.worldpay.core.daos.impl;

import com.worldpay.core.dao.impl.DefaultWorldpayPaymentTransactionDao;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.refund.RefundService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

@IntegrationTest
public class DefaultWorldpayPaymentTransactionDaoIntegrationTest extends ServicelayerTransactionalTest {

    public static final String REQUEST_ID = "requestId";
    private DefaultWorldpayPaymentTransactionDao testObj = new DefaultWorldpayPaymentTransactionDao();

    @Resource (name = "orderService")
    private OrderService orderService;
    @Resource (name = "commerceCartService")
    private CommerceCartService commerceCartService;
    @Resource
    private UserService userService;
    @Resource
    private ProductService productService;
    @Resource
    private CatalogVersionService catalogVersionService;
    @Resource
    private UnitService unitService;
    @Resource
    private RefundService refundService;
    @Resource
    private FlexibleSearchService flexibleSearchService;
    @Resource
    private ModelService modelService;

    @Before
    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
        createHardwareCatalog();
        createDefaultUsers();
        importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
        testObj.setFlexibleSearchService(flexibleSearchService);
    }

    @Test
    public void findPaymentTransactionModelShouldReturnTheLastVersionOfTheOrder() throws CommerceCartModificationException, InvalidCartException {
        final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
        final ProductModel productModel = productService.getProductForCode(catalogVersionModel, "HW1210-3423");
        final UnitModel unitModel = unitService.getUnitForCode("pieces");
        final UserModel userModel = userService.getUserForUID("ahertz");
        final Collection<CartModel> cartModels = userModel.getCarts();

        assertEquals(1, cartModels.size());
        final CartModel cartModel = cartModels.iterator().next();
        final CommerceCartParameter parameter = buildCommerceCartParameter(productModel, unitModel, cartModel);

        // Add new entry
        commerceCartService.addToCart(parameter);

        final OrderModel orderModel = orderService.createOrderFromCart(cartModel);

        addPaymentTransactionToOrder(orderModel);
        orderService.saveOrder(orderModel);

        PaymentTransactionModel returnedPaymentTransactionModel = testObj.findPaymentTransactionByRequestIdFromOrdersOnly(REQUEST_ID);

        assertNull(((OrderModel) returnedPaymentTransactionModel.getOrder()).getVersionID());

        final OrderModel refundOrderPreview = refundService.createRefundOrderPreview(orderModel);
        orderService.saveOrder(refundOrderPreview);

        returnedPaymentTransactionModel = testObj.findPaymentTransactionByRequestIdFromOrdersOnly(REQUEST_ID);
        assertNull(((OrderModel) returnedPaymentTransactionModel.getOrder()).getVersionID());
    }

    @Test
    public void findPaymentTransactionModelByCodeIncludingCartsShouldReturnOrderAndCart() throws InvalidCartException {
        final UserModel userModel = userService.getUserForUID("ahertz");
        final Collection<CartModel> cartModels = userModel.getCarts();

        assertEquals(1, cartModels.size());
        final CartModel cartModel = cartModels.iterator().next();
        addPaymentTransactionToOrder(cartModel);
        modelService.save(cartModel);

        final PaymentTransactionModel result = testObj.findPaymentTransactionByRequestId(REQUEST_ID);

        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getRequestId());
    }

    private void addPaymentTransactionToOrder(final AbstractOrderModel orderModel) {
        final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setRequestId(REQUEST_ID);
        orderModel.setPaymentTransactions(new ArrayList<>(Collections.singletonList(paymentTransactionModel)));
    }

    private CommerceCartParameter buildCommerceCartParameter(final ProductModel productModel, final UnitModel unitModel, final CartModel cartModel) {
        final CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(cartModel);
        parameter.setProduct(productModel);
        parameter.setQuantity(3);
        parameter.setUnit(unitModel);
        parameter.setCreateNewEntry(false);
        return parameter;
    }
}