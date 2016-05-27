<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/desktop/checkout" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:url value="/checkout/multi/worldpay/summary/placeOrder" var="placeOrderUrl"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <div id="globalMessages">
        <common:globalMessages/>
    </div>

    <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>
    <div class="span-14 append-1">
        <wp-multi-checkout:summaryFlow deliveryAddress="${cartData.deliveryAddress}" deliveryMode="${deliveryMode}" paymentInfo="${paymentInfo}" requestSecurityCode="${requestSecurityCode}" cartData="${cartData}"/>
        <cart:cartPromotions cartData="${cartData}"/>

        <form:form action="${placeOrderUrl}" id="placeOrderForm1" commandName="placeOrderForm">
            <form:input type="hidden" class="securityCodeClass" path="securityCode"/>
            <wp-multi-checkout:termsAndConditions />
            <button type="submit" class="positive right place-order worldpayPlaceOrderWithSecurityCode" >
                <spring:theme code="checkout.summary.placeOrder"/>
            </button>
        </form:form>
    </div>

    <multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

    <cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
        <cms:component component="${feature}"/>
    </cms:pageSlot>

</template:page>
