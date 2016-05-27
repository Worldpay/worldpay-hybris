<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.AbstractOrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="label-order">
    <spring:theme code="text.account.paymentType" text="Payment Type"/>
</div>

<c:choose>
    <c:when test="${order.status.code eq 'PAYMENT_PENDING'}">
        <spring:theme code="worldpay.apm.pending.payment.by" text="Pending Payment By"/>:
        ${fn:escapeXml(order.worldpayAPMPaymentInfo.name)}
    </c:when>
    <c:when test="${order.status.code ne 'PAYMENT_PENDING' and order.status.code ne 'CANCELLED' and order.status.code ne 'PROCESSING_ERROR'}">
        <spring:theme code="worldpay.apm.completed.payment.by" text="Paid By"/>:
        ${fn:escapeXml(order.worldpayAPMPaymentInfo.name)}
    </c:when>
    <c:otherwise>
        <spring:theme code="worldpay.apm.payment.method.name" text="Payment method"/>:
        ${fn:escapeXml(order.worldpayAPMPaymentInfo.name)}
    </c:otherwise>
</c:choose>
