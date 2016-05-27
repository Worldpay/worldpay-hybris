<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/checkout/multi" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <jsp:attribute name="pageScripts">
        <script>
            $(document).ready(function () {
                ACC.worldpayRedirect.initForm();
            });
        </script>
    </jsp:attribute>

    <jsp:body>
        <div id="hop" class="row">
            <div class="col-sm-6">
                <div class="checkout-headline">
                    <span class="glyphicon glyphicon-lock"></span>
                    <spring:theme code="checkout.multi.secure.checkout"/>
                </div>
                <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                    <ycommerce:testId code="checkoutStepThree">
                        <div class="checkout-paymentmethod">
                            <div class="checkout-indent">
                                <ycommerce:testId code="paymentDetailsForm">

                                    <c:if test="${not empty paymentInfos}">
                                        <div class="form-group">
                                            <c:if test="${not empty paymentInfos}">
                                                <button type="button" class="btn btn-default btn-block js-saved-payments"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></button>
                                            </c:if>
                                        </div>
                                        <wp-multi-checkout:savedPaymentInfos/>
                                    </c:if>

                                    <c:url value="/checkout/multi/worldpay/redirect/add-payment-details" var="addPaymentAddressUrl"/>
                                    <form:form id="worldpayBillingAddressForm" commandName="paymentDetailsForm" method="post"
                                               action="${addPaymentAddressUrl}" class="create_update_payment_form">

                                        <wp-multi-checkout:paymentButtons/>
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

                                        <div class="form-actions">
                                            <button class="btn btn-primary btn-block submit_worldpayHopForm checkout-next" tabindex="20">
                                                <spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
                                            </button>
                                        </div>
                                    </form:form>
                                </ycommerce:testId>
                            </div>
                        </div>
                    </ycommerce:testId>
                </multi-checkout:checkoutSteps>
            </div>

            <div class="col-sm-6 hidden-xs">
                <multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true"/>
            </div>
            <div class="col-sm-12 col-lg-12">
                <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                    <cms:component component="${feature}"/>
                </cms:pageSlot>
            </div>
        </div>
    </jsp:body>
</template:page>
