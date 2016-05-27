<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/address" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/checkout/multi" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>

<div class="payment_details_left_col-billing">
    <div class="title_holder">
        <div class="title">
            <div class="title-top"><span></span></div>
        </div>
        <h2><spring:theme code="text.billingAddress" text="Billing Address"/></h2>
    </div>

    <c:if test="${cartData.deliveryItemsQuantity > 0}">
        <formElement:formCheckbox idKey="wpUseDeliveryAddress" labelKey="checkout.multi.sop.useMyDeliveryAddress" path="useDeliveryAddress" labelCSS="add-address-left-label" mandatory="false" tabindex="10"/>
        <input type="hidden" id="useDeliveryAddressFields"
               data-firstname="${deliveryAddress.firstName}"
               data-lastname="${deliveryAddress.lastName}"
               data-line1="${deliveryAddress.line1}"
               data-line2="${deliveryAddress.line2}"
               data-town="${deliveryAddress.town}"
               data-postalcode="${deliveryAddress.postalCode}"
               data-countryisocode="${deliveryAddress.country.isocode}"
               data-regionisocode="${deliveryAddress.region.isocode}"
               data-address-id="${deliveryAddress.id}"
               data-phone="${deliveryAddress.phone}"
               tabindex="9"/>
    </c:if>
    <wp-address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="11"/>
</div>

