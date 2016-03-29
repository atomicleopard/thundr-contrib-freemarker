<#list animals as animal>
	<div<#if animal.protected> class="protected"</#if>>
		${animal.name} for ${animal.price} Euros
	</div>
</#list>