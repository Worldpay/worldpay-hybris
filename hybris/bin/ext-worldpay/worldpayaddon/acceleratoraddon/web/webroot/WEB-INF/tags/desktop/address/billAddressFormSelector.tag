<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="supportedCountries" required="false" type="java.util.List"%>
<%@ attribute name="regions" required="false" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/address" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<div id="wpBillingCountrySelector" data-address-code="${cartData.deliveryAddress.id}"
                                   data-country-iso-code="${cartData.deliveryAddress.country.isocode}"
                                   data-display-title="false"
                                   class="clearfix">
    <formElement:formSelectBox idKey="billingAddress.country"
                               labelKey="address.country"
                               path="billingAddress.countryIso"
                               mandatory="true"
                               skipBlank="false"
                               skipBlankMessageKey="address.selectCountry"
                               items="${countries}"
                               itemValue="isocode"
                               tabindex="${tabIndex}"/>
</div>
<div id="wpBillingAddress" class="billingAddress i18nAddressForm">
    <c:if test="${not empty paymentDetailsForm.billingAddress.countryIso}">
        <wp-address:billingAddressFormElements  regions="${regions}" country="${paymentDetailsForm.billingAddress.countryIso}" tabindex="${tabIndex + 1}"/>
    </c:if>
</div>
