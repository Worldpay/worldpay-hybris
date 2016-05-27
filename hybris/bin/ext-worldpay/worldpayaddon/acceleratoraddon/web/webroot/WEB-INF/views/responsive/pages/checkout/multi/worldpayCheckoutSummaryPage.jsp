<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:url value="/checkout/multi/worldpay/summary/placeOrder" var="placeOrderUrl"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <div class="row">
        <div class="col-sm-6">
            <div class="checkout-headline">
                <spring:theme code="checkout.multi.secure.checkout" text="Secure Checkout"></spring:theme>
            </div>
            <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                <ycommerce:testId code="checkoutStepFour">
                    <div class="checkout-review hidden-xs">
                        <div class="checkout-order-summary">
                            <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}" subtotalsCssClasses="dark"/>
                        </div>
                    </div>
                    <div class="place-order-form hidden-xs">
                        <form:form action="${placeOrderUrl}" id="placeOrderForm1" commandName="placeOrderForm">
                            <wp-multi-checkout:securityCode/>
                            <wp-multi-checkout:termsAndConditions/>
                            <button type="submit" id="placeOrder" class="btn btn-primary btn-place-order btn-block worldpayPlaceOrderWithSecurityCode">
                                <spring:theme code="checkout.summary.placeOrder" text="Place Order"/>
                            </button>
                        </form:form>
                    </div>
                </ycommerce:testId>
            </multi-checkout:checkoutSteps>
        </div>
        <div class="col-sm-6">
            <wp-multi-checkout:checkoutOrderSummary cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="true" showTaxEstimate="true" showTax="true"/>
        </div>
        <div class="col-sm-12 col-lg-12">
            <br class="hidden-lg">
            <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                <cms:component component="${feature}"/>
            </cms:pageSlot>
        </div>
    </div>
</template:page>