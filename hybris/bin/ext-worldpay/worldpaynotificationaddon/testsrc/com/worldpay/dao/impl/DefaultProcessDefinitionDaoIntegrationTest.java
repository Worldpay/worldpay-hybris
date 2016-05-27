package com.worldpay.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.strategies.CommerceStrategyTestHelper;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@IntegrationTest
public class DefaultProcessDefinitionDaoIntegrationTest extends ServicelayerTransactionalTest {


    public static final String WAIT_FOR_AUTHORIZATION = "waitFor_AUTHORIZATION";
    public static final String WAIT_FOR_CAPTURE = "waitFor_CAPTURE";
    public static final String TASK_RUNNER = "taskRunner";

    private DefaultProcessDefinitionDao testObj = new DefaultProcessDefinitionDao();

    @Resource
    private CartService cartService;
    @Resource
    private OrderService orderService;
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private ModelService modelService;
    @Resource
    private FlexibleSearchService flexibleSearchService;
    private UserModel user;

    //some shared models
    private AddressModel deliveryAddress;
    private DebitPaymentInfoModel paymentInfo;
    private TitleModel testTitle;
    private CustomerModel testCustomer;

    @Resource
    private BaseSiteService baseSiteService;
    private OrderModel order;


    @Before
    public void setUp() throws Exception {
        testObj.setFlexibleSearchService(flexibleSearchService);

        createCoreData();
        createDefaultCatalog();
        importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");

        createTestUnitModel();
        createTestTitleModel();
        createTestCustomerModel();
        modelService.save(testCustomer);

        userService.setCurrentUser(testCustomer);

        user = userService.getCurrentUser();

        final ProductModel testProduct = productService.getProductForCode("testProduct0");

        //add part-of members
        createTestDeliveryAddressModel();

        createTestPaymentInfoModel();

        final CommerceStrategyTestHelper helper = new CommerceStrategyTestHelper();

        final BaseSiteModel baseSite = helper.createSite(modelService, baseSiteService);

        baseSite.setName("testsite");
        baseSite.setChannel(SiteChannel.B2C);
        addBaseStore(baseSite);

        order = createOrder(testProduct);
    }

    private void addBaseStore(BaseSiteModel baseSite) {
        final SearchResult<BaseStoreModel> search = flexibleSearchService.search("select {PK} from {BaseStore}");
        final List<BaseStoreModel> result = search.getResult();
        final BaseStoreModel baseStoreModel = result.iterator().next();
        baseStoreModel.setSubmitOrderProcessCode("order-process");
        baseSite.setStores(Collections.singletonList(baseStoreModel));
    }

    protected void createTestPaymentInfoModel() {
        paymentInfo = modelService.create(DebitPaymentInfoModel.class);
        paymentInfo.setOwner(user);
        paymentInfo.setBank("MeineBank");
        paymentInfo.setUser(user);
        paymentInfo.setAccountNumber("34434");
        paymentInfo.setBankIDNumber("1111112");
        paymentInfo.setBaOwner("Ich");
        paymentInfo.setCode("testPayment");
    }

    protected void createTestDeliveryAddressModel() {
        deliveryAddress = modelService.create(AddressModel.class);
        deliveryAddress.setOwner(user);
        deliveryAddress.setFirstname("Der");
        deliveryAddress.setLastname("Buck");
        deliveryAddress.setTown("Muenchen");
        deliveryAddress.setTitle(testTitle);
        final CountryModel countryModel = modelService.create(CountryModel.class);
        countryModel.setIsocode("ABC");
        deliveryAddress.setCountry(countryModel);
    }


    protected void createTestCustomerModel() {
        testCustomer = modelService.create(CustomerModel.class);
        testCustomer.setCustomerID("customerId");
        testCustomer.setOriginalUid("originalUid");
        testCustomer.setTitle(testTitle);
        testCustomer.setName("testCustomer name");
        testCustomer.setType(CustomerType.GUEST);
    }

    protected void createTestTitleModel() {
        testTitle = modelService.create(TitleModel.class);
        testTitle.setCode("mr");
        testTitle.setName("Mister");
    }

    protected void createTestUnitModel() {
        final UnitModel testUnit = modelService.create(UnitModel.class);
        testUnit.setCode("myUnit");
        testUnit.setName("myUnit");
        testUnit.setUnitType("test");
    }

    private OrderModel createOrder(ProductModel testProduct) throws InvalidCartException {
        final CartModel cart = cartService.getSessionCart();
        final AbstractOrderEntryModel testEntry = modelService.create(CartEntryModel.class);
        testEntry.setBasePrice((double) 0);

        cartService.addNewEntry(cart, testProduct, 1, null);
        cart.setDeliveryAddress(deliveryAddress);
        cart.setPaymentInfo(paymentInfo);

        final OrderModel order = orderService.createOrderFromCart(cart);

        orderService.submitOrder(order);
        orderService.saveOrder(order);
        return order;
    }

    @Test
    public void testFindWaitingProcessForTransactionTypeAUTHORIZATION() throws Exception {

        // put in our wait for auth task as the current (we're just checking that the DAO works) 

        final ProcessTaskModel authorizeTask = new ProcessTaskModel();
        authorizeTask.setAction(WAIT_FOR_AUTHORIZATION);
        authorizeTask.setProcess(order.getOrderProcess().iterator().next());
        authorizeTask.setRunnerBean(TASK_RUNNER);

        order.getOrderProcess().iterator().next().setCurrentTasks(Collections.singletonList(authorizeTask));
        modelService.save(authorizeTask);
        modelService.save(order);

        final List<BusinessProcessModel> result = testObj.findWaitingOrderProcesses(order.getCode(), PaymentTransactionType.AUTHORIZATION);

        assertResults(authorizeTask, result);
    }

    @Test
    public void testFindWaitingProcessForTransactionTypeCAPTURE() throws Exception {

        // put in our wait for capture task as the current (we're just checking that the DAO works) 

        final ProcessTaskModel authorizeTask = new ProcessTaskModel();
        authorizeTask.setAction(WAIT_FOR_CAPTURE);
        authorizeTask.setProcess(order.getOrderProcess().iterator().next());
        authorizeTask.setRunnerBean(TASK_RUNNER);

        order.getOrderProcess().iterator().next().setCurrentTasks(Collections.singletonList(authorizeTask));
        modelService.save(authorizeTask);
        modelService.save(order);

        final List<BusinessProcessModel> result = testObj.findWaitingOrderProcesses(order.getCode(), PaymentTransactionType.CAPTURE);

        assertResults(authorizeTask, result);
    }

    private void assertResults(ProcessTaskModel authorizeTask, List<BusinessProcessModel> result) {
        Assert.assertTrue(!result.isEmpty());
        Assert.assertTrue(!result.get(0).getCurrentTasks().isEmpty());
        Assert.assertEquals(authorizeTask.getPk(), result.get(0).getCurrentTasks().iterator().next().getPk());
    }
}
    