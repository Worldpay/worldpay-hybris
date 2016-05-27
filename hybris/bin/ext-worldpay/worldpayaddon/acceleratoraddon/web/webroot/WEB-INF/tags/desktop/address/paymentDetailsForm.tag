<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/address" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="wp-cms" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:url value="/checkout/multi/worldpay/redirect/add-payment-details" var="addPaymentAddressUrl"/>

<form:form id="worldpayBillingAddressForm" commandName="paymentDetailsForm" method="post"
           action="${addPaymentAddressUrl}" class="create_update_payment_form">

    <wp-cms:paymentButtons/>
    <div class="save_payment_details hidden">
        <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
            <form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
            <label for="SaveDetails"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/></label>
        </sec:authorize>
    </div>

    <wp-multi-checkout:bankSelect/>
    <wp-multi-checkout:billingAddress/>
    <div class="form-additionals">
    </div>

    <wp-multi-checkout:termsAndConditions/>
</form:form>