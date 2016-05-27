<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div id="savedPaymentListHolder">
    <div id="savedPaymentList" class="summaryOverlay clearfix">
        <div class="headline"><spring:theme code="checkout.summary.paymentMethod.savedCards.header"/></div>
        <div class="description"><spring:theme code="checkout.summary.paymentMethod.savedCards.selectSavedCardOrEnterNew"/></div>

        <div class="paymentList">
            <spring:url value="/checkout/multi/worldpay/choose-payment-method/choose" var="chooseCard"/>
            <c:forEach items="${paymentInfos}" var="paymentInfo" varStatus="status">
                <div class="paymentEntry">
                    <form action="${chooseCard}" method="GET">
                        <input type="hidden" name="selectedPaymentMethodId" value="${paymentInfo.id}"/>
                        <ul>
                            <li>${fn:escapeXml(paymentInfo.cardTypeData.name)}</li>
                            <li>${fn:escapeXml(paymentInfo.cardNumber)}</li>
                            <li><spring:theme code="checkout.multi.paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(paymentInfo.expiryMonth)},${fn:escapeXml(paymentInfo.expiryYear)}"/></li>
                            <li>${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.lastName)}</li>
                            <li>${fn:escapeXml(paymentInfo.billingAddress.line1)}</li>
                            <li>${fn:escapeXml(paymentInfo.billingAddress.region.isocodeShort)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.town)}</li>
                            <li>${fn:escapeXml(paymentInfo.billingAddress.postalCode)}</li>
                        </ul>
                        <button type="submit" class="positive right" tabindex="${status.count + 21}">
                            <spring:theme code="checkout.multi.sop.useThisPaymentInfo" text="Use this Payment Info"/>
                        </button>
                    </form>
                    <form:form action="${request.contextPath}/checkout/multi/worldpay/choose-payment-method/remove" method="POST">
                        <input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
                        <button type="submit" class="negative remove-payment-item right" tabindex="${status.count + 22}">
                            <spring:theme code="checkout.multi.sop.remove" text="Remove"/>
                        </button>
                    </form:form>
                </div>
            </c:forEach>
        </div>
    </div>
</div>