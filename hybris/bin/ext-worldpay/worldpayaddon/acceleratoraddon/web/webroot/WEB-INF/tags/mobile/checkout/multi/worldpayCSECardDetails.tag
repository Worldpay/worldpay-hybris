<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/checkout/multi" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="cardDetailsFieldSet">
    <h2>
        <spring:theme code="checkout.multi.paymentMethod.savedCards.enterNewPaymentDetails" text="Enter New Payment Details"/>
    </h2>

    <div class="infotext">
        <p><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails"/></p>

        <p><spring:theme code="form.required"/></p>
    </div>

    <div class="save_payment_details hidden">
        <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
            <form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
            <label for="SaveDetails"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/></label>
        </sec:authorize>
    </div>

    <div class="form_field-elements payment_details_left_col-card">
        <fieldset>
            <wp-multi-checkout:cseInputBox idKey="nameOnCard" formPath="nameOnCard" mandatory="true" labelKey="payment.nameOnCard" type="text"/>
            <wp-multi-checkout:cseInputBox idKey="number" mandatory="true" labelKey="payment.cardNumber" type="tel"/>
            <fieldset class="cardDate">
                <div class="form_field-label form_field-label-headline">
                    <label class="ui-select"><spring:theme code="payment.expiryDate"/></label>
                </div>
                <span id="exp-date-span">
                    <wp-multi-checkout:cseSelectBox idKey="exp-month" formPath="expiryMonth" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}"/>
                    <wp-multi-checkout:cseSelectBox idKey="exp-year" formPath="expiryYear" skipBlank="false" skipBlankMessageKey="payment.year" items="${expiryYears}"/>
                </span>
            </fieldset>
            <wp-multi-checkout:cseInputBox idKey="cvc" formPath="cvc" mandatory="true" labelKey="payment.cvn" type="tel"/>

            <input type="hidden" id="encryptedData" name="cseToken">
        </fieldset>
    </div>
</div>