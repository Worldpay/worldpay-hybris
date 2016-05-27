<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>

<div id="termsAndConditions" class="terms clear">
    <div class="checkbox">
        <label>
            <form:checkbox id="Terms1" path="termsCheck" />
            <spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${getTermsAndConditionsUrl}" text="Terms and Conditions"/>
        </label>
    </div>
</div>