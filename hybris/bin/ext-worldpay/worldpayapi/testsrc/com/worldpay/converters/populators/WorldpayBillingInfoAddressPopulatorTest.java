package com.worldpay.converters.populators;

import com.worldpay.service.model.Address;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.dto.BillingInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith (MockitoJUnitRunner.class)
public class WorldpayBillingInfoAddressPopulatorTest {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String POSTAL_CODE = "postalCode";
    private static final String ADDRESS_1 = "address1";
    private static final String ADDRESS_2 = "address2";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String COUNTRY_CODE = "countryCode";

    @InjectMocks
    private WorldpayBillingInfoAddressPopulator testObj = new WorldpayBillingInfoAddressPopulator();
    @Mock
    private BillingInfo billingInfo;
    private Address address;

    @Before
    public void setUp() {
        address = new Address();
        when(billingInfo.getFirstName()).thenReturn(FIRST_NAME);
        when(billingInfo.getLastName()).thenReturn(LAST_NAME);
        when(billingInfo.getPhoneNumber()).thenReturn(PHONE_NUMBER);
        when(billingInfo.getPostalCode()).thenReturn(POSTAL_CODE);
        when(billingInfo.getStreet1()).thenReturn(ADDRESS_1);
        when(billingInfo.getStreet2()).thenReturn(ADDRESS_2);
        when(billingInfo.getState()).thenReturn(STATE);
        when(billingInfo.getCity()).thenReturn(CITY);
        when(billingInfo.getCountry()).thenReturn(COUNTRY_CODE);
    }

    @Test
    public void populateShouldAddFirstNameOnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(FIRST_NAME, address.getFirstName());
    }

    @Test
    public void populateShouldAddLastNameOnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(LAST_NAME, address.getLastName());
    }

    @Test
    public void populateShouldAddAddress1OnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(ADDRESS_1, address.getAddress1());
    }

    @Test
    public void populateShouldAddAddress2OnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(ADDRESS_2, address.getAddress2());
    }

    @Test
    public void populateShouldAddPostalCodeOnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(POSTAL_CODE, address.getPostalCode());
    }

    @Test
    public void populateShouldAddCityOnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(CITY, address.getCity());
    }

    @Test
    public void populateShouldAddStateOnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(STATE, address.getState());
    }

    @Test
    public void populateShouldAddCountryCodeOnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(COUNTRY_CODE, address.getCountryCode());
    }

    @Test
    public void populateShouldAddPhoneNumberOnAddressInformation() {
        testObj.populate(billingInfo, address);

        assertEquals(PHONE_NUMBER, address.getTelephoneNumber());
    }
}