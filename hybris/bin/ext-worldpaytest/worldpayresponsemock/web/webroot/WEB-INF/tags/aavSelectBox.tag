<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="labelText" required="true" type="java.lang.String" %>

<form:label path="${path}">
    <span>${labelText}:</span>
    <form:select path="${path}">
        <form:option value="A">A - Matched </form:option>
        <form:option value="B">B - Not Supplied</form:option>
        <form:option value="C">C - Not Checked</form:option>
        <form:option value="D">D - Not Matched</form:option>
    </form:select>
</form:label>