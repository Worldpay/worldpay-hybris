<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="form-group hidden" id="bankElement">
    <label class="control-label" for="shopperBankCode">
        <spring:theme code="worldpay.apm.payment.method.bank"/>
        <span class="skip"></span>
    </label>

    <div class="controls">
        <form:select cssClass="form-control" id="shopperBankCode" path="shopperBankCode" tabindex="${tabindex}" data-bankcode="${paymentDetailsForm.shopperBankCode}">
        </form:select>
    </div>
</div>
<div class="clear"></div>
