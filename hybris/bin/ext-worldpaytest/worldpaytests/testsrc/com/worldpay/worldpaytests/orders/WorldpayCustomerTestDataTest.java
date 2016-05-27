package com.worldpay.worldpaytests.orders;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worldpay.worldpaytests.orders.WorldpayCustomerTestData.CUSTOMER_NAME;
import static com.worldpay.worldpaytests.orders.WorldpayCustomerTestData.CUSTOMER_TITLE_CODE;
import static com.worldpay.worldpaytests.orders.WorldpayOrderTestData.WORLDPAY_PERFORMANCE_TEST_USER_UID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayCustomerTestDataTest {

    @InjectMocks
    private WorldpayCustomerTestData testObj = new WorldpayCustomerTestData();
    @Mock
    private ModelService modelServiceMock;
    @Mock
    private CustomerModel customerModelMock;
    @Mock
    private TitleModel customerTitleMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private AddressModel addressModelMock;
    @Mock
    private CustomerAccountService customerAccountServiceMock;
    @Captor
    private ArgumentCaptor<AddressData> addressDataCaptor;
    @Mock
    private AddressReversePopulator addressReversePopulatorMock;

    @Test
    public void createCustomerShouldRemoveAndCreateCustomer() {
        when(modelServiceMock.create(CustomerModel.class)).thenReturn(customerModelMock);
        when(userServiceMock.getTitleForCode(CUSTOMER_TITLE_CODE)).thenReturn(customerTitleMock);

        final CustomerModel result = testObj.createCustomer(addressModelMock);

        verify(modelServiceMock).create(CustomerModel.class);
        verify(customerModelMock).setUid(WORLDPAY_PERFORMANCE_TEST_USER_UID);
        verify(customerModelMock).setName(CUSTOMER_NAME);
        verify(customerModelMock).setTitle(customerTitleMock);
        verify(addressModelMock).setOwner(customerModelMock);
        verify(userServiceMock).getTitleForCode(CUSTOMER_TITLE_CODE);
        verify(modelServiceMock).saveAll(customerModelMock, addressModelMock);
        verify(customerAccountServiceMock).saveAddressEntry(customerModelMock, addressModelMock);
        assertSame(customerModelMock, result);
    }

    @Test
    public void shouldCreateVisaCardInfo() {

        final CardInfo result = testObj.createVisaCardInfo();

        assertEquals("John Doe", result.getCardHolderFullName());
        assertEquals("4111111111111111", result.getCardNumber());
        assertEquals(CreditCardType.VISA, result.getCardType());
        assertEquals(Integer.valueOf(12), result.getExpirationMonth());
        assertEquals(Integer.valueOf(2020), result.getExpirationYear());
    }

    @Test
    public void shouldCreateUkBillingInfo() {

        final BillingInfo result = testObj.createUkBillingInfo();

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Holborn Tower", result.getStreet1());
        assertEquals("137 High Holborn", result.getStreet2());
        assertEquals("London", result.getCity());
        assertEquals("WC1V 6PL", result.getPostalCode());
        assertEquals("GB", result.getCountry());
        assertEquals("+44 (0)20 / 7429 4175", result.getPhoneNumber());
    }

    @Test
    public void shouldCreateAddressModel() {
        when(modelServiceMock.create(AddressModel.class)).thenReturn(addressModelMock);

        testObj.createAddressModel();

        verify(modelServiceMock).create(AddressModel.class);
        verify(addressReversePopulatorMock).populate(addressDataCaptor.capture(), eq(addressModelMock));

        final AddressData addressData = addressDataCaptor.getValue();
        assertEquals("Mr.", addressData.getTitle());
        assertEquals("mr", addressData.getTitleCode());
        assertEquals("John", addressData.getFirstName());
        assertEquals("Doe", addressData.getLastName());
        assertEquals("hybris", addressData.getCompanyName());
        assertEquals("137 High Holborn", addressData.getLine1());
        assertEquals("London", addressData.getTown());
        assertEquals("WC1V 6PL", addressData.getPostalCode());
        assertEquals("GB", addressData.getCountry().getIsocode());
        assertEquals("UK", addressData.getCountry().getName());
        assertEquals("+44 (0)20 / 7429 4175", addressData.getPhone());
        assertEquals("sales@hybris.local", addressData.getEmail());
    }
}