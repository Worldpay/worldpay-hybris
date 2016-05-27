<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="labelKey" required="true" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="type" required="false" type="java.lang.String" %>
<%@ attribute name="formPath" required="false" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="control-group">
    <label for="${idKey}">
        <spring:theme code="${labelKey}"/>
        <c:if test="${mandatory != null && mandatory == true}">
        <span class="mandatory"> <spring:theme code="form.field.required" var="requiredText"/>
            <img width="5" height="6" alt="${requiredText}" title="${requiredText}" src="${commonResourcePath}/images/mandatory.gif"/>
        </span>
        </c:if>
    </label>

    <c:choose>
        <c:when test="${formPath != null}">
            <form:input path="${formPath}" id="${idKey}"/>
        </c:when>
        <c:otherwise>
            <input id="${idKey}" type="${type}">
        </c:otherwise>
    </c:choose>
    <div class="hidden" id="error-${idKey}"></div>
</div>