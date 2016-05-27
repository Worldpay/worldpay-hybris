<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<div class="checkout-success">
    <div class="checkout-success-body">
        <div class="checkout-success-body-headline">
            <c:choose>
                <c:when test="${orderData.isApmOpen}">
                    <spring:theme code="checkout.orderConfirmation.pending.thankYouForOrder" text="Your order is pending!"/>
                </c:when>
                <c:otherwise>
                    <spring:theme code="checkout.orderConfirmation.thankYouForOrder" text="Thank you for your order"/>
                </c:otherwise>
            </c:choose>
        </div>
        <p><spring:theme code="text.account.order.orderNumberLabel"/><b> ${orderData.code}</b></p>
        <p><spring:theme code="checkout.orderConfirmation.copySentToShort"/><b> ${email}</b></p>
    </div>

    <c:if test="${not empty guestRegisterForm}">
        <div class="checkout-new-account">
            <div class="checkout-new-account-headline"><spring:theme code="guest.register"/></div>
            <p><spring:theme code="order.confirmation.guest.register.description"/></p>

            <form:form method="post" commandName="guestRegisterForm" class="checkout-new-account-form clearfix">
                <div class="col-sm-8 col-sm-push-2 col-md-6 col-md-push-3">
                    <form:hidden path="orderCode"/>
                    <form:hidden path="uid"/>

                    <div class="form-group">
                        <label for="email" class="control-label "><spring:theme code="register.email"/></label>
                        <input type="text" value="${fn:escapeXml(guestRegisterForm.uid)}" class="form-control" name="email" id="email" readonly>
                    </div>

                    <formElement:formPasswordBox idKey="password" labelKey="guest.pwd" path="pwd" inputCSS="password strength form-control" mandatory="true"/>
                    <formElement:formPasswordBox idKey="guest.checkPwd" labelKey="guest.checkPwd" path="checkPwd" inputCSS="password form-control" mandatory="true"/>


                    <div class="accountActions-bottom">
                        <ycommerce:testId code="guest_Register_button">
                            <button type="submit" class="btn btn-block btn-primary">
                                <spring:theme code="guest.register"/>
                            </button>
                        </ycommerce:testId>
                    </div>
                </div>
            </form:form>
        </div>
    </c:if>
</div>

<div class="well well-tertiary well-single-headline">
    <div class="well-headline">
        <spring:theme code="checkout.multi.order.summary"/>
    </div>
</div>