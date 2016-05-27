<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/checkout/multi" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="cardDetailsFieldSet">
    <fieldset class="cardForm">
        <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
            <div class="save_payment_details checkbox">
                <label for="SaveDetails">
                    <form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
                    <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/>
                </label>
            </div>
        </sec:authorize>

        <wp-multi-checkout:cseInputBox idKey="nameOnCard" formPath="nameOnCard" mandatory="true" labelKey="payment.nameOnCard"/>
        <wp-multi-checkout:cseInputBox idKey="number" mandatory="true" labelKey="payment.cardNumber"/>

        <div class="form-group">
            <label class="control-label"><spring:theme code="payment.expiryDate"/></label>

            <div class="row">
                <div class="col-xs-6">
                    <wp-multi-checkout:cseSelectBox idKey="exp-month" formPath="expiryMonth" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}"/>
                </div>
                <div class="col-xs-6">
                    <wp-multi-checkout:cseSelectBox idKey="exp-year" formPath="expiryYear" skipBlank="false" skipBlankMessageKey="payment.year" items="${expiryYears}"/>
                </div>
            </div>
            <div class="help-block hidden">
                <span id="error-exp-date"></span>
            </div>
        </div>

        <div class="row col-xs-6">
            <wp-multi-checkout:cseInputBox idKey="cvc" formPath="cvc" mandatory="false" labelKey="payment.cvn"/>
        </div>
        <input type="hidden" id="encryptedData" name="cseToken">
    </fieldset>
</div>