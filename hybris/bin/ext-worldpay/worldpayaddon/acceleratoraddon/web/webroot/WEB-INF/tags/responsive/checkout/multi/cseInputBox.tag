<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="labelKey" required="true" type="java.lang.String" %>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean" %>
<%@ attribute name="type" required="false" type="java.lang.String" %>
<%@ attribute name="formPath" required="false" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>

<div class="form-group">
    <c:choose>
        <c:when test="${formPath != null}">
            <formElement:formInputBox idKey="${idKey}" labelKey="${labelKey}" path="${formPath}"  mandatory="${mandatory}"/>
        </c:when>
        <c:otherwise>
            <label for="${idKey}" class="control-label">
                <spring:theme code="${labelKey}"/>
                <c:if test="${mandatory != null && mandatory == false}">
                    <spring:theme code="login.optional" />
                </c:if>
            </label>
            <input id="${idKey}" type="${type}" class="form-control">
        </c:otherwise>
    </c:choose>
    <div class="help-block hidden">
        <span id="error-${idKey}"></span>
    </div>
</div>