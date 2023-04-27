<#include init />

<#if portletDisplay.isStateMax()>
	<@liferay.control_menu />

	<main id="main-content">
		<@displayPortlet/>
	</main>
<#else>
	<@displayPortlet/>
</#if>

<#macro displayPortlet>
	<section class="portlet" id="portlet_${htmlUtil.escapeAttribute(portletDisplay.getId())}">
		${portletDisplay.writeContent(writer)}
	</section>
</#macro>