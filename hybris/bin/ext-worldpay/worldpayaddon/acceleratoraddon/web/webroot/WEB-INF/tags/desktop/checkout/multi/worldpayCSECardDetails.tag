<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="cardDetailsFieldSet">
    <div class="headline"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.paymentCard"/></div>
    <div class="required right"><spring:theme code="form.required"/></div>
    <div class="description"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails"/></div>
    <div class="cardForm">
        <div class="save_payment_details">
            <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
                <form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
                <label for="SaveDetails"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/></label>
            </sec:authorize>
        </div>
        <wp-multi-checkout:cseInputBox idKey="nameOnCard" formPath="nameOnCard" mandatory="true" labelKey="payment.nameOnCard"/>
        <wp-multi-checkout:cseInputBox idKey="number" mandatory="true" labelKey="payment.cardNumber" type="text"/>

        <div class="control-group">
            <label><spring:theme code="payment.expiryDate"/></label>
            <wp-multi-checkout:cseSelectBox idKey="exp-month" formPath="expiryMonth" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}"/>
            <wp-multi-checkout:cseSelectBox idKey="exp-year" formPath="expiryYear" skipBlank="false" skipBlankMessageKey="payment.year" items="${expiryYears}"/>
            <div class="hidden" id="error-exp-date"></div>
        </div>

        <wp-multi-checkout:cseInputBox idKey="cvc" formPath="cvc" mandatory="true" labelKey="payment.cvn"/>

        <input type="hidden" id="encryptedData" name="cseToken">
    </div>
</div>