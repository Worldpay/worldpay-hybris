package com.worldpay.worldpaytests.orders;

import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.core.enums.CreditCardType.VISA;

public class WorldpayCustomerTestData {

    protected static final String WORLDPAY_PERFORMANCE_TEST_USER_UID = "worldpayperformancetestuser";
    protected static final String CUSTOMER_NAME = "Charles";
    protected static final String CUSTOMER_TITLE_CODE = "mr";

    private ModelService modelService;
    private AddressReversePopulator addressReversePopulator;
    private UserService userService;
    private CustomerAccountService customerAccountService;

    public CustomerModel createCustomer(final AddressModel addressModel) {

        final CustomerModel customer = modelService.create(CustomerModel.class);
        customer.setUid(WORLDPAY_PERFORMANCE_TEST_USER_UID);
        customer.setName(CUSTOMER_NAME);
        customer.setTitle(userService.getTitleForCode(CUSTOMER_TITLE_CODE));

        addressModel.setOwner(customer);
        modelService.saveAll(customer, addressModel);
        customerAccountService.saveAddressEntry(customer, addressModel);
        return customer;
    }

    public CardInfo createVisaCardInfo() {
        final CardInfo cardInfo = new CardInfo();
        cardInfo.setCardHolderFullName("John Doe");
        cardInfo.setCardNumber("4111111111111111");
        cardInfo.setCardType(VISA);
        cardInfo.setExpirationMonth(Integer.valueOf(12));
        cardInfo.setExpirationYear(Integer.valueOf(2020));
        return cardInfo;
    }

    public BillingInfo createUkBillingInfo() {
        final BillingInfo billingInfo = new BillingInfo();
        billingInfo.setFirstName("John");
        billingInfo.setLastName("Doe");
        billingInfo.setStreet1("Holborn Tower");
        billingInfo.setStreet2("137 High Holborn");
        billingInfo.setCity("London");
        billingInfo.setPostalCode("WC1V 6PL");
        billingInfo.setCountry("GB");
        billingInfo.setPhoneNumber("+44 (0)20 / 7429 4175");
        return billingInfo;
    }

    public AddressModel createAddressModel() {
        final AddressModel addressModel = modelService.create(AddressModel.class);
        addressReversePopulator.populate(createUkAddressData(), addressModel);

        return addressModel;
    }

    protected AddressData createUkAddressData() {
        final AddressData data = new AddressData();
        data.setTitle("Mr.");
        data.setTitleCode("mr");
        data.setFirstName("John");
        data.setLastName("Doe");

        data.setCompanyName("hybris");
        data.setLine1("137 High Holborn");
        data.setTown("London");
        data.setPostalCode("WC1V 6PL");

        final CountryData countryData = new CountryData();
        countryData.setIsocode("GB");
        countryData.setName("UK");
        data.setCountry(countryData);

        data.setPhone("+44 (0)20 / 7429 4175");
        data.setEmail("sales@hybris.local");
        data.setShippingAddress(true);
        data.setBillingAddress(true);

        return data;
    }

    @Required
    public void setAddressReversePopulator(AddressReversePopulator addressReversePopulator) {
        this.addressReversePopulator = addressReversePopulator;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Required
    public void setCustomerAccountService(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }
}
