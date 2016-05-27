<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wp-order" tagdir="/WEB-INF/tags/addons/worldpayaddon/mobile/order" %>


<c:choose>
    <c:when test="${not empty order.paymentInfo.id}">
        <div class="left">
            <wp-order:paymentDetailsItem order="${order}"/>
        </div>
    </c:when>
    <c:when test="${not empty order.worldpayAPMPaymentInfo.name}">
        <div class="left">
            <wp-order:paymentAPMDetailsItem order="${order}"/>
        </div>
    </c:when>
</c:choose>