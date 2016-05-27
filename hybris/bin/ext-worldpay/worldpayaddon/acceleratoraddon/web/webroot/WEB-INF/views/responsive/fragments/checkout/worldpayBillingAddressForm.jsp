<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/address" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${not empty country}">
    <form:form commandName="wpBillingAddressForm">
        <wp-address:billingAddressFormElements regions="${regions}" country="${country}" />
    </form:form>
</c:if>