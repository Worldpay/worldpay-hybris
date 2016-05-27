package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.Address;
import com.worldpay.internal.model.Address1;
import com.worldpay.internal.model.Address2;
import com.worldpay.internal.model.Address3;

import java.util.List;

public final class AddressBuilder {

    private static final String DEFAULT_ADDRESS_1 = "default address 1";
    private static final String DEFAULT_ADDRESS_2 = "default Address 2";
    private static final String DEFAULT_ADDRESS_3 = "default address 3";
    private static final String DEFAULT_POST_CODE = "postCode";
    private static final String DEFAULT_CITY = "city";
    private static final String DEFAULT_COUNTRY_CODE = "GB";
    private static final String DEFAULT_LAST_NAME = "lastName";

    private String address1 = DEFAULT_ADDRESS_1;
    private String address2 = DEFAULT_ADDRESS_2;
    private String address3 = DEFAULT_ADDRESS_3;
    private String postalCode = DEFAULT_POST_CODE;
    private String city = DEFAULT_CITY;
    private String countryCode = DEFAULT_COUNTRY_CODE;
    private String lastName = DEFAULT_LAST_NAME;

    private AddressBuilder() {
    }

    public static AddressBuilder anAddressBuilder() {
        return new AddressBuilder();
    }

    public AddressBuilder withAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public AddressBuilder withAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public AddressBuilder withAddress3(String address3) {
        this.address3 = address3;
        return this;
    }

    public AddressBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public AddressBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public AddressBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public AddressBuilder withCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public Address build() {
        final Address address = new Address();

        final List<Object> addressDetails = address.getStreetOrHouseNameOrHouseNumberOrHouseNumberExtensionOrAddress1OrAddress2OrAddress3();
        final Address1 addressDetailsAddress1 = new Address1();
        addressDetailsAddress1.setvalue(this.address1);
        addressDetails.add(addressDetailsAddress1);
        final Address2 addressDetailsAddress2 = new Address2();
        addressDetailsAddress2.setvalue(this.address2);
        addressDetails.add(addressDetailsAddress2);
        final Address3 addressDetailsAddress3 = new Address3();
        addressDetailsAddress3.setvalue(this.address3);
        addressDetails.add(addressDetailsAddress3);
        address.setLastName(this.lastName);
        address.setPostalCode(this.postalCode);
        address.setCity(this.city);
        address.setCountryCode(this.countryCode);
        return address;
    }

}
