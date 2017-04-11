
<%@ page import="edu.umn.nlpie.pier.elastic.Field" %>
<%@ page import="edu.umn.nlpie.pier.ui.FieldPreference" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'field.label', default: 'Field')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-field" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-field" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list field">
			
				<g:if test="${fieldInstance?.fieldName}">
				<li class="fieldcontain">
					<span id="fieldName-label" class="property-label"><g:message code="field.fieldName.label" default="Field Name" /></span>
					
						<span class="property-value" aria-labelledby="fieldName-label"><g:fieldValue bean="${fieldInstance}" field="fieldName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${fieldInstance?.dataTypeName}">
				<li class="fieldcontain">
					<span id="dataTypeName-label" class="property-label"><g:message code="field.dataTypeName.label" default="Data Type Name" /></span>
					
						<span class="property-value" aria-labelledby="dataTypeName-label"><g:fieldValue bean="${fieldInstance}" field="dataTypeName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${fieldInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="field.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${fieldInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${fieldInstance?.preferences}">
				<li class="fieldcontain">
					<span id="preferences-label" class="property-label"><g:message code="field.preferences.label" default="Preferences" /></span>
					
						<g:each in="${FieldPreference.findAllByFieldAndApplicationDefault(fieldInstance,true)}" var="p">
						<span class="property-value" aria-labelledby="preferences-label"><g:link controller="fieldPreference" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${fieldInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="field.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${fieldInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${fieldInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="field.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${fieldInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${fieldInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="field.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:link controller="type" action="show" id="${fieldInstance?.type?.id}">${fieldInstance?.type?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:fieldInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${fieldInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
