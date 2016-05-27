<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty paymentInfos}">
    <div id="savedPaymentListHolder">
        <div id="savedpayments">
            <div id="savedpaymentstitle">
                <div class="headline">
                    <span class="headline-text"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useSavedCard"/></span>
                </div>
            </div>
            <div id="savedpaymentsbody">
                <spring:url value="/checkout/multi/worldpay/choose-payment-method/choose" var="chooseCard"/>
                <c:forEach items="${paymentInfos}" var="paymentInfo" varStatus="status">
                    <form action="${chooseCard}" method="GET">
                        <input type="hidden" name="selectedPaymentMethodId" value="${paymentInfo.id}"/>
                        <strong>${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.lastName)}</strong><br/>
                            ${fn:escapeXml(paymentInfo.cardTypeData.name)}<br/>
                            ${fn:escapeXml(paymentInfo.accountHolderName)}<br/>
                            ${fn:escapeXml(paymentInfo.cardNumber)}<br/>
                        <spring:theme code="checkout.multi.paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(paymentInfo.expiryMonth)},${fn:escapeXml(paymentInfo.expiryYear)}"/><br/>
                            ${fn:escapeXml(paymentInfo.billingAddress.line1)}<br/>
                            ${fn:escapeXml(paymentInfo.billingAddress.town)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.region.isocodeShort)}<br/>
                            ${fn:escapeXml(paymentInfo.billingAddress.postalCode)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.country.isocode)}<br/>
                        <button type="submit" class="btn btn-primary btn-block" tabindex="${(status.count * 2) - 1}"><spring:theme
                                code="checkout.multi.paymentMethod.addPaymentDetails.useThesePaymentDetails"/></button>
                    </form>
                </c:forEach>
            </div>
        </div>
    </div>
</c:if>
