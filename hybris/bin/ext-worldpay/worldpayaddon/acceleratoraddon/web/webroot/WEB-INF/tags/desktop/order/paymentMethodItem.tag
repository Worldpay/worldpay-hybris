<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wp-order" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/order" %>


<div class="orderBox payment">
    <c:choose>
        <c:when test="${not empty order.paymentInfo.id}">
            <div class="left">
                <order:paymentDetailsItem order="${order}"/>
            </div>
        </c:when>
        <c:when test="${not empty order.worldpayAPMPaymentInfo.name}">
            <div class="left">
                <wp-order:paymentAPMDetailsItem order="${order}"/>
            </div>
        </c:when>
    </c:choose>
    <div class="left">
        <order:billingAddressItem order="${order}"/>
    </div>
</div>