<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="hasPaymentButtons" value="false" />
<c:set var="hasAvailablePaymentMethods" value="false" scope="session"/>

<div id="paymentButtons" data-paymentmethod="${paymentDetailsForm.paymentMethod}">
    <cms:pageSlot position="PaymentButtons" var="button" element="div" class="cms-payment-button">
        <c:set var="hasPaymentButtons" value="true"/>
        <cms:component component="${button}"/>
    </cms:pageSlot>
</div>

<c:if test="${not hasPaymentButtons or (hasPaymentButtons and not hasAvailablePaymentMethods)}">
    <form:hidden id="paymentMethod_ONLINE" path="paymentMethod" value="ONLINE"/>
</c:if>