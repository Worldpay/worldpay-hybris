<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<c:if test="${requestSecurityCode}">
    <div class="checkout-saved-card">
        <div class="security-form dark">
            <ycommerce:testId code="checkout_paymentDetails_text">
                <div class="security-headline"><spring:theme code="checkout.summary.paymentMethod.securityCode"/></div>
                <div class="form-group">
                    <label class="control-label" for="SecurityCode">
                        <spring:theme code="payment.cvn"/>
                    </label>

                    <input id="SecurityCode" type="text" class="form-control security" name="securityCode"/>
                </div>
            </ycommerce:testId>
        </div>
    </div>
</c:if>