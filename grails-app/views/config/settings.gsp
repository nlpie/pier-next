<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>
		<h2 class="page-header">Settings</h2>
		<div ng-controller="DslCtrl">
			<div>
				<div class="pull-left">
					<div>
						<a href="#" editable-text="dsl.role.add.template" e-style="width:400px"
							onbeforesave="dsl.execute($data,dsl.role.add)">{{ dsl.role.add.template }}</a> 
						<span class="admin-success">{{ dsl.role.add.message }}</span>
					</div>
					<div>
						<a href="#" editable-text="dsl.role.del.template" e-style="width:400px"
							onbeforesave="dsl.execute($data,dsl.role.del)">{{ dsl.role.del.template }}</a>
						<span class="admin-success">{{ dsl.role.del.message }}</span>
					</div>
					<div>
						<a href="#" editable-text="dsl.role.addto.template" e-style="width:400px"
							onbeforesave="dsl.execute($data,dsl.role.addto)">{{ dsl.role.addto.template }}</a> 
							<span class="admin-success">{{ dsl.role.addto.message }}</span>
					</div>
					<div>
						<a href="#" editable-text="dsl.role.removefrom.template" e-style="width:400px"
							onbeforesave="dsl.execute($data,dsl.role.removefrom)">{{ dsl.role.removefrom.template }}</a> 
						<span class="admin-success">{{ dsl.role.removefrom.message }}</span>
					</div>
				</div>
				<div class="pull-right" style="text-align:right">
					<div style="text-decoration:underline; font-style:italic">Roles</div>
					<div style="margin-bottom:1px" ng-repeat="r in adminData.roles">
  						{{r.authority}}
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
