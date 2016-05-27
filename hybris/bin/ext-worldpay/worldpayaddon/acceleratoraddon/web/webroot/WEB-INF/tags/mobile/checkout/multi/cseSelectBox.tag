<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="idKey" required="true" type="java.lang.String" %>
<%@ attribute name="items" required="true" type="java.util.Collection" %>
<%@ attribute name="skipBlank" required="false" type="java.lang.String" %>
<%@ attribute name="skipBlankMessageKey" required="false" type="java.lang.String" %>
<%@ attribute name="formPath" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<label class="ui-select" for="${idKey}">
    <spring:theme code="${labelKey}"/>
</label>
<form:select id="${idKey}" path="${formPath}">
    <c:if test="${skipBlank == null || skipBlank == false}">
        <option value="" disabled="disabled" ${empty selectedValue ? 'selected="selected"' : ''}><spring:theme code='${skipBlankMessageKey}'/></option>
    </c:if>
    <c:forEach var="option" items="${items}">
        <option value="${option.code}">${option.name}</option>
    </c:forEach>
</form:select>