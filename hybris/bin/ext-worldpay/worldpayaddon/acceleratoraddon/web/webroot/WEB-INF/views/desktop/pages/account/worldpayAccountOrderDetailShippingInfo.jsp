<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wp-order" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/order" %>


<div class="orderBoxes clearfix">
    <order:deliveryAddressItem order="${orderData}"/>
    <order:deliveryMethodItem order="${orderData}"/>
    <div class="orderBox billing">
        <order:billingAddressItem order="${orderData}"/>
    </div>
    <c:if test="${not empty orderData.paymentInfo.id}">
        <div class="orderBox payment">
            <order:paymentDetailsItem order="${orderData}"/>
        </div>
    </c:if>
    <c:if test="${not empty orderData.worldpayAPMPaymentInfo.name}">
        <div class="orderBox payment">
            <wp-order:paymentAPMDetailsItem order="${orderData}"/>
        </div>
    </c:if>
</div>
