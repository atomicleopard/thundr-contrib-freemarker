<#list animals as animal>
	<div class="protected-${animal.protected?string("Y", "N")}">
		${animal.name?cap_first} for ${animal.price!"No"} Euros
	</div>
</#list>