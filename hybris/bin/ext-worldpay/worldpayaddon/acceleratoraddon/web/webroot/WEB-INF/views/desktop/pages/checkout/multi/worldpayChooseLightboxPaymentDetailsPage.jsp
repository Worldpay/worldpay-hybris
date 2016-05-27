<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>
<%@ taglib prefix="wp-multi-checkout" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>
<%@ taglib prefix="wp-address" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/address" %>
<%@ taglib prefix="wp-cms" tagdir="/WEB-INF/tags/addons/worldpayaddon/desktop/checkout/multi" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<c:url value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}" var="currentServerUrl"/>
<c:url value="${currentServerUrl}${requestScope.contextPath}" var="fullContextPath"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <jsp:attribute name="pageCss">
        <link rel='stylesheet' href='https://payments.worldpay.com/resources/hpp/integrations/embedded/css/hpp-embedded-integration-library.css'/>
    </jsp:attribute>

    <jsp:attribute name="pageScripts">
        <script type="text/javascript" src='https://payments.worldpay.com/resources/hpp/integrations/embedded/js/hpp-embedded-integration-library.js'></script>
        <script type="text/javascript">
            var customOptions = {
                type: "lightbox",
                iframeIntegrationId: 'libraryObject',
                iframeHelperURL: '${fullContextPath}/_ui/addons/worldpayaddon/common/worldpay/nextgen/helper.html',
                iframeBaseURL: '${currentServerUrl}',
                target: 'custom-html',
                trigger: 'custom-trigger',
                lightboxMaskOpacity: ${cmsPage.lightboxMaskOpacity},
                lightboxMaskColor: '${cmsPage.lightboxMaskColor}',
                accessibility: ${cmsPage.accessibility},
                debug: ${cmsPage.debug},
                language: '${currentLanguage.isocode}',
                url: '',
                successURL: '',
                cancelURL: '',
                failureURL: '',
                pendingURL: '',
                errorURL: ''
            };
            $(document).ready(function () {
                ACC.worldpayLightbox.initForm();
            });
        </script>
    </jsp:attribute>

    <jsp:body>
        <div id="globalMessages">
            <common:globalMessages/>
        </div>

        <multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>
        <div id="billingWrapper" class="span-14 append-1">

            <div id="checkoutContentPanel" class="clearfix">
                <div class="headline"><spring:theme code="checkout.multi.paymentMethod"/></div>

                <c:if test="${not empty paymentInfos}">
                    <button type="button" class="positive clear view-saved-payments" id="viewSavedPayments">
                        <spring:theme code="checkout.multi.paymentMethod.viewSavedPayments" text="View Saved Payments"/>
                    </button>
                    <wp-multi-checkout:savedPaymentInfos/>
                </c:if>
                <div id="wpPaymentDetailsFormPlaceHolder">
                    <wp-address:paymentDetailsForm/>
                </div>
                <div class="form-actions">
                    <c:url value="/checkout/multi/delivery-method/choose" var="chooseDeliveryMethodUrl"/>
                    <a class="button" href="${chooseDeliveryMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
                    <button class="positive right submit_worldpayHopForm" tabindex="20">
                        <spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
                    </button>
                </div>
            </div>
            <div id="nextGenHopPlaceHolder">
            </div>
            <div id="custom-trigger"></div>
            <div id='custom-html'></div>
        </div>

        <multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

        <cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </jsp:body>
</template:page>
