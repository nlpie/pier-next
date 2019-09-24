<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>
		<div  class="pull-right">
			<g:link controller="search">
				<span style="font-size:larger">Search</span>
				<i class="glyphicon glyphicon-arrow-right" style="color:#428BCA"
					tooltip-placement="bottom" tooltip="Start your search..." tooltip-popup-delay="{{tooltipDelay}}"></i>
			</g:link>
		</div>
		<g:render template="/index_content"/>	
	</body>

</html>