<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="payment_details_right_col saved-payment-list">
    <h2>
        <spring:theme code="mobile.multi.checkout.selectExistingCard"
                      text="Choose from existing Payment Details"/>
    </h2>
    <c:forEach items="${paymentInfos}" var="paymentInfo">
        <div class="saved-payment-list-entry">
            <form action="${request.contextPath}/checkout/multi/worldpay/choose-payment-method/choose"
                  method="GET">
                <input type="hidden" name="selectedPaymentMethodId" value="${paymentInfo.id}"/>
                <span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.cardTypeData.name)}</span>
                <span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.cardNumber)}</span>
                <span class="saved-payment-list-item">
                    <spring:theme code="checkout.multi.paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(paymentInfo.expiryMonth)},${fn:escapeXml(paymentInfo.expiryYear)}"/>
                </span>
                <span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.lastName)}</span>
                <span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.billingAddress.line1)}</span>
                <span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.billingAddress.postalCode)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.town)}</span>
                <button type="submit" class="form" data-theme="c">
                    <spring:theme code="checkout.multi.sop.useThisPaymentInfo"
                                  text="Use this Payment Info"/>
                </button>
            </form>
            <form:form
                    action="${request.contextPath}/checkout/multi/worldpay/choose-payment-method/remove"
                    method="POST" class="remove-payment-item-form">
                <input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
                <button type="submit" class="text-button remove-payment-item">
                    <spring:theme code="checkout.multi.sop.remove" text="Remove"/>
                </button>
            </form:form>
        </div>
    </c:forEach>
</div>