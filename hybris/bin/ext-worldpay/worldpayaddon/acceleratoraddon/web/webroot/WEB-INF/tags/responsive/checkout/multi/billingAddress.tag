<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/address" %>

<c:if test="${cartData.deliveryItemsQuantity > 0}">
    <div class="checkbox">
        <label for="wpUseDeliveryAddress">
            <form:checkbox id="wpUseDeliveryAddress" path="useDeliveryAddress"
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
                           inputCSS="add-address-left-input"
                           labelCSS="add-address-left-label"/>

            <spring:theme code="checkout.multi.sop.useMyDeliveryAddress"/>
        </label>
    </div>
</c:if>

<wp-address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}"/>

