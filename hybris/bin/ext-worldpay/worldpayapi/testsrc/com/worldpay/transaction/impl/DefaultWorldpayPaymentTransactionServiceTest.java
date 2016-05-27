package com.worldpay.transaction.impl;

import com.worldpay.core.dao.WorldpayPaymentTransactionDao;
import com.worldpay.hostedorderpage.data.RedirectAuthoriseResult;
import com.worldpay.internal.model.RiskScore;
import com.worldpay.model.WorldpayAavResponseModel;
import com.worldpay.model.WorldpayRiskScoreModel;
import com.worldpay.service.model.Amount;
import com.worldpay.service.model.PaymentReply;
import com.worldpay.service.notification.OrderNotificationMessage;
import com.worldpay.transaction.EntryCodeStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static de.hybris.platform.payment.dto.TransactionStatus.ACCEPTED;
import static de.hybris.platform.payment.dto.TransactionStatus.REJECTED;
import static de.hybris.platform.payment.dto.TransactionStatusDetails.SUCCESFULL;
import static de.hybris.platform.payment.enums.PaymentTransactionType.AUTHORIZATION;
import static de.hybris.platform.payment.enums.PaymentTransactionType.CAPTURE;
import static de.hybris.platform.payment.enums.PaymentTransactionType.SETTLED;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class DefaultWorldpayPaymentTransactionServiceTest {

    private static final String TRANSACTION_STATUS = "transactionStatus";
    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_TOKEN = "requestToken";
    private static final String MERCHANT_CODE = "merchantCode";
    private static final String PAYMENT_PROVIDER = "paymentProvider";
    private static final String EXCEPTION_MESSAGE = "exceptionMessage";
    private static final String WORLDPAY_ORDER_CODE = "worldpayOrderCode";
    private static final String TRANSACTION_ENTRY_CODE = "transactionEntryCode";

    private final Currency currency = Currency.getInstance(Locale.UK);
    private final Map<PaymentTransactionType, PaymentTransactionType> paymentTransactionDependency = new HashMap<>();

    @Spy
    @InjectMocks
    private DefaultWorldpayPaymentTransactionService testObj = new DefaultWorldpayPaymentTransactionService();

    @Mock
    private PaymentTransactionModel paymentTransactionModelMock;
    @Mock
    private PaymentTransactionEntryModel authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock, authorisedAndRejectedAndPendingEntryMock, pendingCaptureEntryMock;
    @Mock
    private PaymentTransactionEntryModel capturedEntryMock;
    @Mock
    private OrderModel orderModelMock;
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private CommonI18NService commonI18NServiceMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock (answer = Answers.RETURNS_DEEP_STUBS)
    private OrderNotificationMessage orderNotificationMessageMock;
    @Mock
    private Amount amountMock;
    @Mock
    private CartModel cartModelMock;
    @Mock
    private PaymentInfoModel paymentInfoModelMock;
    @Mock
    private EntryCodeStrategy entryCodeStrategyMock;
    @Mock
    private WorldpayPaymentTransactionDao worldpayPaymentTransactionDaoMock;
    @Mock
    private PaymentTransactionModel notApmOpenPaymentTransactionModelMock, apmOpenPaymentTransactionModelMock;
    @Mock
    private RedirectAuthoriseResult redirectAuthoriseResultMock;
    @Mock
    private PaymentReply paymentReplyMock;
    @Mock
    private RiskScore riskScoreMock;
    @Mock
    private Converter<RiskScore, WorldpayRiskScoreModel> worldpayRiskScoreConverterMock;
    @Mock
    private WorldpayRiskScoreModel worldpayRiskScoreModelMock;
    @Mock
    private WorldpayAavResponseModel worldpayAavResponseModelMock;
    @Mock
    private Populator<PaymentReply, WorldpayAavResponseModel> worldpayAavResponsePopulatorMock;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModelMock;
    @Mock
    private CommerceCheckoutParameter commerceCheckoutParameterMock;

    @Before
    public void setup() {
        paymentTransactionDependency.put(CAPTURE, AUTHORIZATION);
        paymentTransactionDependency.put(SETTLED, CAPTURE);
        testObj.setPaymentTransactionDependency(paymentTransactionDependency);

        when(authorisedAndAcceptedAndNotPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndNotPendingEntryMock.getTransactionStatus()).thenReturn(ACCEPTED.name());
        when(authorisedAndAcceptedAndNotPendingEntryMock.getPending()).thenReturn(Boolean.FALSE);

        when(authorisedAndAcceptedAndPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndAcceptedAndPendingEntryMock.getTransactionStatus()).thenReturn(REJECTED.name());
        when(authorisedAndAcceptedAndPendingEntryMock.getPending()).thenReturn(Boolean.TRUE);

        when(authorisedAndRejectedAndPendingEntryMock.getType()).thenReturn(AUTHORIZATION);
        when(authorisedAndRejectedAndPendingEntryMock.getTransactionStatus()).thenReturn(REJECTED.name());
        when(authorisedAndRejectedAndPendingEntryMock.getPending()).thenReturn(Boolean.FALSE);

        when(capturedEntryMock.getType()).thenReturn(CAPTURE);
        when(capturedEntryMock.getTransactionStatus()).thenReturn(ACCEPTED.name());
        when(capturedEntryMock.getPending()).thenReturn(Boolean.FALSE);
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModelMock));

        when(entryCodeStrategyMock.generateCode(paymentTransactionModelMock)).thenReturn(TRANSACTION_ENTRY_CODE);

        when(modelServiceMock.create(PaymentTransactionEntryModel.class)).thenReturn(authorisedAndAcceptedAndPendingEntryMock);
        when(modelServiceMock.create(PaymentTransactionModel.class)).thenReturn(paymentTransactionModelMock);

        when(redirectAuthoriseResultMock.getOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);
        when(redirectAuthoriseResultMock.getPending()).thenReturn(Boolean.TRUE);

        when(notApmOpenPaymentTransactionModelMock.getApmOpen()).thenReturn(Boolean.FALSE);
        when(apmOpenPaymentTransactionModelMock.getApmOpen()).thenReturn(Boolean.TRUE);

        when(cartModelMock.getWorldpayOrderCode()).thenReturn(WORLDPAY_ORDER_CODE);

        when(amountMock.getCurrencyCode()).thenReturn(currency.getCurrencyCode());

        when(amountMock.getValue()).thenReturn("1000");
        when(amountMock.getCurrencyCode()).thenReturn("GBP");

        when(capturedEntryMock.getAmount()).thenReturn(BigDecimal.TEN);

        when(orderNotificationMessageMock.getPaymentReply().getAmount()).thenReturn(amountMock);
        when(orderNotificationMessageMock.getOrderCode()).thenReturn(REQUEST_ID);
        when(orderNotificationMessageMock.getMerchantCode()).thenReturn(REQUEST_TOKEN);

        when(commonI18NServiceMock.getCurrency(currency.getCurrencyCode())).thenReturn(currencyModelMock);

        when(commerceCheckoutParameterMock.getAuthorizationAmount()).thenReturn(BigDecimal.TEN);
        when(commerceCheckoutParameterMock.getCart()).thenReturn(cartModelMock);
        when(commerceCheckoutParameterMock.getPaymentInfo()).thenReturn(paymentInfoModelMock);
        when(commerceCheckoutParameterMock.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);
        when(cartModelMock.getCurrency()).thenReturn(currencyModelMock);
    }

    @Test
    public void allPaymentTransactionTypesAcceptedShouldReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void anyPaymentTransactionTypesOfWrongTypeShouldReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(capturedEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void somePaymentTransactionTypesAcceptedAndSomeNotAcceptedShouldReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.areAllPaymentTransactionsAcceptedForType(orderModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void ifAllPaymentTransactionsPendingThenReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void ifAnyPaymentTransactionsNotPendingThenReturnTrue() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result);
    }

    @Test
    public void ifAllPaymentTransactionsNotPendingThenReturnFalse() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndNotPendingEntryMock, authorisedAndRejectedAndPendingEntryMock));

        final boolean result = testObj.isPaymentTransactionPending(paymentTransactionModelMock, AUTHORIZATION);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnTrueIfTheDependingTransactionEntryIsNotPending() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnTrueIfTheDependingTransactionEntryIsPending() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnFalseIfThereAreNoEntriesInTheTransaction() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.emptyList());

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, CAPTURE, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void isPreviousTransactionCompletedShouldReturnFalseIfThereAreNoEntriesOfDependingTypeInTheTransaction() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndNotPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, SETTLED, orderModelMock);

        assertFalse(result);
    }

    @Test
    public void whenTransactionIsNonDependantOnAPreviousOneThenReturnTrue() {
        when(paymentTransactionModelMock.getRequestId()).thenReturn(WORLDPAY_ORDER_CODE);
        when(paymentTransactionModelMock.getEntries()).thenReturn(Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock));

        final boolean result = testObj.isPreviousTransactionCompleted(WORLDPAY_ORDER_CODE, AUTHORIZATION, orderModelMock);

        assertTrue(result);
    }

    @Test
    public void shouldCreatePendingAuthorizationPaymentTransactionEntry() {
        when(paymentTransactionModelMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);

        final PaymentTransactionEntryModel result = testObj.createPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, REQUEST_TOKEN, cartModelMock);

        verify(result).setType(AUTHORIZATION);
        verify(result).setRequestId(WORLDPAY_ORDER_CODE);
        verify(result).setRequestToken(REQUEST_TOKEN);
        verify(result).setTransactionStatus(ACCEPTED.name());
        verify(result).setTransactionStatusDetails(SUCCESFULL.name());
        verify(result).setCode(TRANSACTION_ENTRY_CODE);
        verify(result, never()).setPending(anyBoolean());
        verify(modelServiceMock).save(result);
        verify(modelServiceMock).refresh(paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateNonPendingAuthorizationPaymentTransactionEntry() {
        when(paymentTransactionModelMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);

        final PaymentTransactionEntryModel result = testObj.createNonPendingAuthorisePaymentTransactionEntry(paymentTransactionModelMock, REQUEST_TOKEN, cartModelMock);

        verify(result).setType(AUTHORIZATION);
        verify(result).setRequestId(WORLDPAY_ORDER_CODE);
        verify(result).setRequestToken(REQUEST_TOKEN);
        verify(result).setTransactionStatus(ACCEPTED.name());
        verify(result).setTransactionStatusDetails(SUCCESFULL.name());
        verify(result).setCode(TRANSACTION_ENTRY_CODE);
        verify(result).setPending(false);
        verify(modelServiceMock).save(result);
        verify(modelServiceMock).refresh(paymentTransactionModelMock);
    }

    @Test
    public void shouldCreateCaptureEntry() {

        final PaymentTransactionEntryModel result = testObj.createCapturedPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);

        verifyPaymentTransactionEntry(result, CAPTURE, false);
        verify(result).setCurrency(currencyModelMock);
    }

    @Test
    public void shouldReturnTransactionWhenTransactionIsFound() {
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE)).thenReturn(paymentTransactionModelMock);

        testObj.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);

        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void shouldReturnNullWhenTransactionIsNotFound() {
        when(worldpayPaymentTransactionDaoMock.findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE)).thenThrow(new ModelNotFoundException(EXCEPTION_MESSAGE));

        final PaymentTransactionModel result = testObj.getPaymentTransactionFromCode(WORLDPAY_ORDER_CODE);

        assertNull(result);
        verify(worldpayPaymentTransactionDaoMock).findPaymentTransactionByRequestId(WORLDPAY_ORDER_CODE);
    }

    @Test
    public void shouldCreatePaymentTransaction() {
        testObj.createPaymentTransaction(true, MERCHANT_CODE, commerceCheckoutParameterMock);

        verify(paymentTransactionModelMock).setCode(WORLDPAY_ORDER_CODE);
        verify(paymentTransactionModelMock).setRequestId(WORLDPAY_ORDER_CODE);
        verify(paymentTransactionModelMock).setRequestToken(MERCHANT_CODE);
        verify(paymentTransactionModelMock).setPaymentProvider(PAYMENT_PROVIDER);
        verify(paymentTransactionModelMock).setOrder(cartModelMock);
        verify(paymentTransactionModelMock).setCurrency(currencyModelMock);
        verify(paymentTransactionModelMock).setInfo(paymentInfoModelMock);
        verify(paymentTransactionModelMock).setApmOpen(true);
        verify(paymentTransactionModelMock).setPlannedAmount(BigDecimal.TEN);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void shouldUpdateTransactionStatus() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(authorisedAndAcceptedAndPendingEntryMock);
        testObj.updateEntriesStatus(paymentTransactionEntries, TRANSACTION_STATUS);

        verify(authorisedAndAcceptedAndPendingEntryMock).setTransactionStatus(TRANSACTION_STATUS);
        verify(authorisedAndAcceptedAndPendingEntryMock).setPending(false);
        verify(modelServiceMock).saveAll(paymentTransactionEntries);
    }

    @Test
    public void isAnyPaymentTransactionApmOpenForOrderReturnsTrueWhenAnyPaymentTransactionIsApmOpen() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(asList(notApmOpenPaymentTransactionModelMock, apmOpenPaymentTransactionModelMock));

        final boolean result = testObj.isAnyPaymentTransactionApmOpenForOrder(orderModelMock);

        assertTrue(result);
    }

    @Test
    public void isAnyPaymentTransactionApmOpenForOrderReturnsFalseWhenAllPaymentTransactionAreNotApmOpen() {
        when(orderModelMock.getPaymentTransactions()).thenReturn(Collections.singletonList(notApmOpenPaymentTransactionModelMock));

        final boolean result = testObj.isAnyPaymentTransactionApmOpenForOrder(orderModelMock);

        assertFalse(result);
    }

    @Test
    public void shouldReturnPendingEntries() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndPendingEntryMock, authorisedAndAcceptedAndNotPendingEntryMock));

        final List<PaymentTransactionEntryModel> result = testObj.getPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION);

        assertTrue(result.contains(authorisedAndAcceptedAndPendingEntryMock));
        assertFalse(result.contains(authorisedAndAcceptedAndNotPendingEntryMock));
    }

    @Test
    public void shouldReturnNonPendingEntries() {
        when(paymentTransactionModelMock.getEntries()).thenReturn(Arrays.asList(authorisedAndAcceptedAndPendingEntryMock, authorisedAndAcceptedAndNotPendingEntryMock));

        final List<PaymentTransactionEntryModel> result = testObj.getNotPendingPaymentTransactionEntriesForType(paymentTransactionModelMock, AUTHORIZATION);

        assertFalse(result.contains(authorisedAndAcceptedAndPendingEntryMock));
        assertTrue(result.contains(authorisedAndAcceptedAndNotPendingEntryMock));
    }

    @Test
    public void shouldDoNothingIfRiskScoreIsNull() throws Exception {
        when(paymentReplyMock.getRiskScore()).thenReturn(null);

        testObj.addRiskScore(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock, never()).setRiskScore(any());
    }

    @Test
    public void shouldSaveRiskScoreOnTransaction() throws Exception {
        when(paymentReplyMock.getRiskScore()).thenReturn(riskScoreMock);
        when(worldpayRiskScoreConverterMock.convert(riskScoreMock)).thenReturn(worldpayRiskScoreModelMock);

        testObj.addRiskScore(paymentTransactionModelMock, paymentReplyMock);

        verify(paymentTransactionModelMock).setRiskScore(worldpayRiskScoreModelMock);
        verify(modelServiceMock).save(paymentTransactionModelMock);
    }

    @Test
    public void shouldUpdateAavFields() {
        when(modelServiceMock.create(WorldpayAavResponseModel.class)).thenReturn(worldpayAavResponseModelMock);

        testObj.addAavFields(paymentTransactionEntryModelMock, paymentReplyMock);

        verify(worldpayAavResponsePopulatorMock).populate(paymentReplyMock, worldpayAavResponseModelMock);
        verify(modelServiceMock).save(paymentTransactionEntryModelMock);
    }

    @Test
    public void shouldUpdateTransactionEntryAmount() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(pendingCaptureEntryMock).setAmount(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_CEILING));
        verify(pendingCaptureEntryMock).setCurrency(currencyModelMock);
        verify(modelServiceMock).saveAll(paymentTransactionEntries);
    }

    @Test
    public void shouldLogWarningForDifferentAmountsUpdated() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);
        when(pendingCaptureEntryMock.getAmount()).thenReturn(BigDecimal.TEN.add(BigDecimal.valueOf(50)));
        when(pendingCaptureEntryMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);
        when(pendingCaptureEntryMock.getType()).thenReturn(CAPTURE);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(testObj).logAmountChanged(pendingCaptureEntryMock, BigDecimal.TEN.setScale(2, BigDecimal.ROUND_CEILING));
    }

    @Test
    public void shouldNotLogWarningWhenEntryAmountIsNull() {
        final List<PaymentTransactionEntryModel> paymentTransactionEntries = Collections.singletonList(pendingCaptureEntryMock);
        when(pendingCaptureEntryMock.getAmount()).thenReturn(null);

        testObj.updateEntriesAmount(paymentTransactionEntries, amountMock);

        verify(testObj, never()).logAmountChanged(any(PaymentTransactionEntryModel.class), any(BigDecimal.class));
    }

    @Test
    public void shouldCreateNonPendingSettledPaymentTransactionEntry() {
        when(paymentTransactionModelMock.getCode()).thenReturn(TRANSACTION_ENTRY_CODE);

        final PaymentTransactionEntryModel result = testObj.createNotPendingSettledPaymentTransactionEntry(paymentTransactionModelMock, orderNotificationMessageMock);

        verifyPaymentTransactionEntry(result, SETTLED, Boolean.FALSE);
    }

    protected void verifyPaymentTransactionEntry(final PaymentTransactionEntryModel result, final PaymentTransactionType transactionType, final Boolean pendingFlag) {
        verify(result).setType(transactionType);
        verify(result).setRequestId(REQUEST_ID);
        verify(result).setRequestToken(REQUEST_TOKEN);
        verify(result).setTransactionStatus(ACCEPTED.name());
        verify(result).setTransactionStatusDetails(SUCCESFULL.name());
        verify(result).setCode(TRANSACTION_ENTRY_CODE);
        verify(result).setAmount(BigDecimal.TEN.setScale(2, BigDecimal.ROUND_CEILING));
        verify(result).setPending(pendingFlag);
        verify(modelServiceMock).save(result);
    }
}