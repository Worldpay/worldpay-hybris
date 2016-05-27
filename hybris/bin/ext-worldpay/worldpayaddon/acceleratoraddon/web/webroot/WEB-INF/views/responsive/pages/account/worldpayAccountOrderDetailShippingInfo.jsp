<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="wp-order" tagdir="/WEB-INF/tags/addons/worldpayaddon/responsive/order" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="account-orderdetail">
    <div class="account-orderdetail-item-section-header item-box">
        <ycommerce:testId code="orderDetails_paymentDetails_section">
            <div class="col-md-5 order-billing-address">
                <order:billingAddressItem order="${orderData}"/>
            </div>
            <c:if test="${not empty orderData.paymentInfo.id}">
                <div class="col-md-6 order-payment-data">
                    <order:paymentDetailsItem order="${orderData}"/>
                </div>
            </c:if>
            <c:if test="${not empty orderData.worldpayAPMPaymentInfo.name}">
                <div class="col-md-6 order-payment-data">
                    <wp-order:paymentAPMDetailsItem order="${orderData}"/>
                </div>
            </c:if>
        </ycommerce:testId>
    </div>
</div>
