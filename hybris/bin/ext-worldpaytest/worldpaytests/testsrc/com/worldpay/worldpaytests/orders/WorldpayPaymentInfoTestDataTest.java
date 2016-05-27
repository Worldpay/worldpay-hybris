package com.worldpay.worldpaytests.orders;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.worldpaytests.orders.WorldpayCustomerTestData.*;
import static com.worldpay.worldpaytests.orders.WorldpayOrderTestData.CURRENCY_ISO;
import static com.worldpay.worldpaytests.orders.WorldpayOrderTestData.PAYMENT_PROVIDER;
import static de.hybris.platform.core.enums.CreditCardType.MASTERCARD_EUROCARD;
import static de.hybris.platform.core.enums.CreditCardType.VISA;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayPaymentInfoTestDataTest {
    private static final String CARD_OWNER = "cardOwner";

    @Spy
    @InjectMocks
    private WorldpayPaymentInfoTestData testObj = new WorldpayPaymentInfoTestData();

    @Mock
    private ImpersonationContext impersonationContextMock;
    @Mock
    private CurrencyModel currencyModelMock;
    @Mock
    private CommonI18NService i18nServiceMock;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomerModel customerModelMock;
    @Mock
    private CardInfo cardInfoMock;
    @Mock
    private BillingInfo ukBillingInfoMock;
    @Mock
    private CMSSiteModel cmsSiteMock;
    @Mock
    private ImpersonationService impersonationServiceMock;
    @Mock
    private CustomerAccountService customerAccountServiceMock;
    @Mock
    private CreditCardPaymentInfoModel existingCreditCardPaymentInfoModelMock;
    @Mock
    private CreditCardPaymentInfoModel newCreditCardPaymentInfoModelMock;

    @Test
    public void shouldCreatePaymentInfoForTheCustomer() {
        doReturn(impersonationContextMock).when(testObj).createImpersonationContext();
        when(i18nServiceMock.getCurrency(CURRENCY_ISO)).thenReturn(currencyModelMock);

        testObj.createPaymentInfo(customerModelMock, CURRENCY_ISO, cardInfoMock, ukBillingInfoMock, cmsSiteMock);

        verify(impersonationContextMock).setSite(cmsSiteMock);
        verify(impersonationContextMock).setUser(customerModelMock);
        verify(impersonationContextMock).setCurrency(currencyModelMock);
        verify(impersonationServiceMock).executeInContext(eq(impersonationContextMock), any());
    }

    @Test
    public void shouldCreatePaymentInfoInContext() {
        when(customerAccountServiceMock.getCreditCardPaymentInfos(customerModelMock, true)).thenReturn(singletonList(existingCreditCardPaymentInfoModelMock));
        doReturn(false).when(testObj).matchesCardInfo(existingCreditCardPaymentInfoModelMock, cardInfoMock);
        when(customerModelMock.getTitle().getCode()).thenReturn(CUSTOMER_TITLE_CODE);
        when(customerAccountServiceMock.createPaymentSubscription(customerModelMock, cardInfoMock, ukBillingInfoMock, CUSTOMER_TITLE_CODE, PAYMENT_PROVIDER, true)).thenReturn(newCreditCardPaymentInfoModelMock);

        testObj.createPaymentInfoInContext(customerModelMock, cardInfoMock, ukBillingInfoMock);

        verify(customerAccountServiceMock).setDefaultPaymentInfo(customerModelMock, newCreditCardPaymentInfoModelMock);
    }

    @Test
    public void shouldNotCreatePaymentInfoWhenCardExists() {
        when(customerAccountServiceMock.getCreditCardPaymentInfos(customerModelMock, true)).thenReturn(singletonList(existingCreditCardPaymentInfoModelMock));
        doReturn(true).when(testObj).matchesCardInfo(existingCreditCardPaymentInfoModelMock, cardInfoMock);

        testObj.createPaymentInfoInContext(customerModelMock, cardInfoMock, ukBillingInfoMock);

        verify(customerAccountServiceMock, never()).setDefaultPaymentInfo(any(CustomerModel.class), any(CreditCardPaymentInfoModel.class));
    }

    @Test
    public void matchesCardInfoReturnsTrueIfMatching() {
        when(existingCreditCardPaymentInfoModelMock.getType()).thenReturn(VISA);
        when(cardInfoMock.getCardType()).thenReturn(VISA);

        when(existingCreditCardPaymentInfoModelMock.getCcOwner()).thenReturn(CARD_OWNER);
        when(cardInfoMock.getCardHolderFullName()).thenReturn(CARD_OWNER);

        assertTrue(testObj.matchesCardInfo(existingCreditCardPaymentInfoModelMock, cardInfoMock));
    }

    @Test
    public void matchesCardInfoReturnsFalseIfTypesDoesntMatch() {
        when(existingCreditCardPaymentInfoModelMock.getType()).thenReturn(VISA);
        when(cardInfoMock.getCardType()).thenReturn(MASTERCARD_EUROCARD);

        when(existingCreditCardPaymentInfoModelMock.getCcOwner()).thenReturn(CARD_OWNER);
        when(cardInfoMock.getCardHolderFullName()).thenReturn(CARD_OWNER);

        assertFalse(testObj.matchesCardInfo(existingCreditCardPaymentInfoModelMock, cardInfoMock));
    }

    @Test
    public void matchesCardInfoReturnsFalseIfOwnerNameDoesntMatch() {
        when(existingCreditCardPaymentInfoModelMock.getType()).thenReturn(VISA);
        when(cardInfoMock.getCardType()).thenReturn(VISA);

        when(existingCreditCardPaymentInfoModelMock.getCcOwner()).thenReturn(CARD_OWNER);
        when(cardInfoMock.getCardHolderFullName()).thenReturn(CUSTOMER_TITLE_CODE + " " + CARD_OWNER);

        assertFalse(testObj.matchesCardInfo(existingCreditCardPaymentInfoModelMock, cardInfoMock));
    }

}