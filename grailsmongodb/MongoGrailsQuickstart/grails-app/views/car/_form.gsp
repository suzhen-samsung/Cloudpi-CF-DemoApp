<%@ page import="mongograilsquickstart.Car" %>



<div class="fieldcontain ${hasErrors(bean: carInstance, field: 'brand', 'error')} ">
	<label for="brand">
		<g:message code="car.brand.label" default="Brand" />
		
	</label>
	<g:textField name="brand" value="${carInstance?.brand}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: carInstance, field: 'ps', 'error')} required">
	<label for="ps">
		<g:message code="car.ps.label" default="Ps" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="ps" type="number" min="30" max="1001" value="${carInstance.ps}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: carInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="car.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${carInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: carInstance, field: 'buildDate', 'error')} required">
	<label for="buildDate">
		<g:message code="car.buildDate.label" default="Build Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="buildDate" precision="day"  value="${carInstance?.buildDate}"  />
</div>

