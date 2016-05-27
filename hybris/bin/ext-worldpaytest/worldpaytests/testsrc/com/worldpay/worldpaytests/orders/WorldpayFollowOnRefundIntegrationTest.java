package com.worldpay.worldpaytests.orders;

import com.worldpay.exception.WorldpayConfigurationException;
import com.worldpay.merchant.WorldpayMerchantInfoService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.verify.VerificationTimes;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;

import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static java.time.Instant.now;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@IntegrationTest
public class WorldpayFollowOnRefundIntegrationTest extends ServicelayerTransactionalTest {

    private static final String XML_REFUND_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE paymentService PUBLIC \"-//Worldpay//DTD Worldpay PaymentService v1//EN\" \"http://dtd.worldpay.com/paymentService_v1.dtd\">\n<paymentService version=\"1.4\" merchantCode=\"{0}\"><modify><orderModification orderCode=\"TxRequestId\"><refund reference=\"00000000-REFUND_FOLLOW_ON-2\"><amount value=\"1000\" currencyCode=\"GBP\" exponent=\"2\" debitCreditIndicator=\"credit\"/></refund></orderModification></modify></paymentService>";
    private static final String XML_REFUND_REPLY = "<paymentService version=\"1.4\" merchantCode=\"{0}\"><reply><ok><refundReceived orderCode=\"00001000-1457702892792\"><amount value=\"9985\" currencyCode=\"GBP\" exponent=\"2\" debitCreditIndicator=\"credit\"/></refundReceived></ok></reply></paymentService>\n";

    @Resource
    private ConfigurationService configurationService;
    @Resource
    private PaymentService paymentService;
    @Resource
    private ModelService modelService;
    @Resource
    private BaseSiteService baseSiteService;
    @Resource
    private WorldpayMerchantInfoService worldpayMerchantInfoService;

    private ClientAndServer mockServer;
    private BaseSiteModel baseSiteModel;
    private CurrencyModel currencyModel;
    private String merchantCode;

    @Before
    public void setup() throws Exception {
        mockServer = startClientAndServer(1080);

        configurationService.getConfiguration().setProperty("worldpay.config.environment", "MOCK");
        configurationService.getConfiguration().setProperty("worldpay.config.endpoint.MOCK", "http://localhost:1080");

        baseSiteModel = createBaseSiteModel();
        baseSiteService.setCurrentBaseSite(baseSiteModel, false);
        currencyModel = createGBPCurrencyModel();
        merchantCode = worldpayMerchantInfoService.getCurrentSiteMerchant(UiExperienceLevel.DESKTOP).getMerchantCode();
    }

    @Test
    public void shouldSendFollowOnRefundToWorldpay() throws CommerceCartModificationException, InvalidCartException, WorldpayConfigurationException {
        mockServer.when(request().withMethod("POST").withBody(MessageFormat.format(XML_REFUND_REQUEST.trim(), merchantCode))).
                respond(response().withStatusCode(200).withBody(MessageFormat.format(XML_REFUND_REPLY, merchantCode)));

        final OrderModel orderModel = createOrder(currencyModel);
        final PaymentTransactionModel transactionModel = modelService.create(PaymentTransactionModel.class);
        final PaymentTransactionEntryModel authPaymentTransactionEntry = modelService.create(PaymentTransactionEntryModel.class);
        authPaymentTransactionEntry.setType(AUTHORIZATION);
        authPaymentTransactionEntry.setCode("AuthorisedPaymentTransaction");
        authPaymentTransactionEntry.setCurrency(currencyModel);
        modelService.save(authPaymentTransactionEntry);
        transactionModel.setEntries(Collections.singletonList(authPaymentTransactionEntry));
        transactionModel.setRequestId("TxRequestId");
        transactionModel.setPaymentProvider("Worldpay");
        transactionModel.setRequestToken(merchantCode);
        transactionModel.setOrder(orderModel);
        modelService.save(transactionModel);
        paymentService.refundFollowOn(transactionModel, new BigDecimal(10));

        mockServer.verify(request().withMethod("POST").withBody(MessageFormat.format(XML_REFUND_REQUEST, merchantCode)), VerificationTimes.once());
    }

    protected OrderModel createOrder(CurrencyModel currencyModel) {
        final OrderModel orderModel = modelService.create(OrderModel.class);
        orderModel.setCurrency(currencyModel);
        orderModel.setDate(Date.from(now()));
        orderModel.setUser(createUserModel());
        orderModel.setSite(baseSiteModel);
        return orderModel;
    }

    private BaseSiteModel createBaseSiteModel() {
        final BaseSiteModel baseSiteModel = new BaseSiteModel();
        baseSiteModel.setUid("electronics");
        return baseSiteModel;
    }

    private UserModel createUserModel() {
        final UserModel userModel = new UserModel();
        userModel.setUid("userUid");
        return userModel;
    }

    protected CurrencyModel createGBPCurrencyModel() {
        currencyModel = new CurrencyModel();
        currencyModel.setIsocode("GBP");
        return currencyModel;
    }

    @After
    public void tearDown() {
        mockServer.stop();
    }
}
