<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="regions" required="true" type="java.util.List" %>
<%@ attribute name="country" required="false" type="java.lang.String" %>
<%@ attribute name="tabindex" required="false" type="java.lang.Integer" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:choose>
    <c:when test="${country == 'US'}">
        <formElement:formInputBox idKey="billingAddress.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.lastName" labelKey="address.surname" path="billingAddress.lastName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="form-control" mandatory="false"/>
        <formElement:formInputBox idKey="billingAddress.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="form-control" mandatory="true"/>
        <formElement:formSelectBox idKey="billingAddress.state" labelKey="address.state" path="billingAddress.regionIso" mandatory="true" skipBlank="false"
                                   skipBlankMessageKey="address.selectState" items="${regions}" itemValue="isocode" selectCSSClass="form-control"/>
        <formElement:formInputBox idKey="billingAddress.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.phone" labelKey="address.phone" path="billingAddress.phone" inputCSS="form-control" mandatory="false"/>
    </c:when>
    <c:when test="${country == 'CA'}">
        <formElement:formInputBox idKey="billingAddress.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.lastName" labelKey="address.surname" path="billingAddress.lastName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="form-control" mandatory="false"/>
        <formElement:formInputBox idKey="billingAddress.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="form-control" mandatory="true"/>
        <formElement:formSelectBox idKey="billingAddress.region" labelKey="address.province" path="billingAddress.regionIso" mandatory="true" skipBlank="false"
                                   skipBlankMessageKey="address.selectProvince" items="${regions}" itemValue="isocode" selectCSSClass="form-control"/>
        <formElement:formInputBox idKey="billingAddress.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.phone" labelKey="address.phone" path="billingAddress.phone" inputCSS="form-control" mandatory="false"/>
    </c:when>
    <c:when test="${country == 'CN'}">
        <formElement:formInputBox idKey="billingAddress.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="form-control" mandatory="true"/>
        <formElement:formSelectBox idKey="billingAddress.region" labelKey="address.province" path="billingAddress.regionIso" mandatory="true" skipBlank="false"
                                   skipBlankMessageKey="address.selectProvince" items="${regions}" itemValue="isocode"  selectCSSClass="form-control"/>
        <formElement:formInputBox idKey="billingAddress.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line2" labelKey="address.district_and_street" path="billingAddress.line2" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line1" labelKey="address.building_and_room" path="billingAddress.line1" inputCSS="form-control" mandatory="false"/>
        <formElement:formInputBox idKey="billingAddress.lastName" labelKey="address.surname" path="billingAddress.lastName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.phone" labelKey="address.phone" path="billingAddress.phone" inputCSS="form-control" mandatory="false"/>
    </c:when>
    <c:when test="${country == 'JP'}">
        <formElement:formInputBox idKey="billingAddress.lastName" labelKey="address.surname" path="billingAddress.lastName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.postcode" labelKey="address.postcodeJP" path="billingAddress.postcode" inputCSS="form-control" mandatory="true"/>
        <formElement:formSelectBox idKey="billingAddress.region" labelKey="address.prefecture" path="billingAddress.regionIso" mandatory="true" skipBlank="false"
                                   skipBlankMessageKey="address.selectPrefecture" items="${regions}" itemValue="isocode"  selectCSSClass="form-control"/>
        <formElement:formInputBox idKey="billingAddress.townCity" labelKey="address.townJP" path="billingAddress.townCity" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line2" labelKey="address.subarea" path="billingAddress.line2" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line1" labelKey="address.furtherSubarea" path="billingAddress.line1" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.phone" labelKey="address.phone" path="billingAddress.phone" inputCSS="form-control" mandatory="false"/>
    </c:when>
    <c:otherwise>
        <formElement:formInputBox idKey="billingAddress.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.lastName" labelKey="address.surname" path="billingAddress.lastName" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="form-control" mandatory="false"/>
        <formElement:formInputBox idKey="billingAddress.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="form-control" mandatory="true"/>
        <formElement:formInputBox idKey="billingAddress.phone" labelKey="address.phone" path="billingAddress.phone" inputCSS="form-control" mandatory="false"/>
    </c:otherwise>
</c:choose>
