<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showDeliveryAddress" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showPaymentInfo" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/checkout/multi" %>

<spring:url value="/checkout/multi/worldpay/summary/placeOrder" var="placeOrderUrl"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>

<div class="checkout-headline hidden-xs">
    <spring:theme code="checkout.multi.order.summary" text="Order Summary"/>
</div>
<div class="checkout-order-summary">
    <ycommerce:testId code="orderSummary">
        <multi-checkout:deliveryCartItems cartData="${cartData}" showDeliveryAddress="${showDeliveryAddress}"/>

        <c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
            <multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" index="${status.index}" showHead="true"/>
        </c:forEach>

        <multi-checkout:paymentInfo cartData="${cartData}" paymentInfo="${cartData.paymentInfo}" showPaymentInfo="${showPaymentInfo}"/>


        <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}"/>
    </ycommerce:testId>
</div>

<div class="visible-xs clearfix">
    <form:form action="${placeOrderUrl}" id="placeOrderForm1" commandName="placeOrderForm" class="place-order-form col-xs-12">
        <wp-multi-checkout:securityCode/>
        <wp-multi-checkout:termsAndConditions/>

        <button id="placeOrder" type="submit" class="btn btn-primary btn-place-order btn-block worldpayPlaceOrderWithSecurityCode">
            <spring:theme code="checkout.summary.placeOrder" text="Place Order"/>
        </button>
    </form:form>
</div>
