<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>
		<div growl></div>
		<div class="container-fluid" ng-controller="settingsController as sc">
			
			<div class="row">
				<div ng-if="sc.settings.view=='filters'" class="col-md-12">
					These options control which filters are displayed along the left side of the search results and how many
					filter options per field are displayed. Changes persist across logins.
				</div>
				<div ng-if="sc.settings.view=='exports'" class="col-md-12">
					These options control which data elements are exported when search results are downloaded. Changes
					persist across logins.
				</div>
			</div>
			
			<div class="row">
				<div class="col-md-12">&nbsp;</div>
			</div>
			
			<div ng-if="sc.settings.view=='filters'" ng-repeat="(corpus, ontologies) in sc.settings.prefs">
				<div ng-repeat="(ontology, prefs) in ontologies">
					<div ng-if="corpus==sc.settings.corpus">
						<div class="row" style="background-color:#e6e6e6;color:black">
							<div class="col-md-1">{{ ontology }}</div>
							<div class="col-md-2">Filter category</div>
							<div class="col-md-1">Enabled</div>
							<div class="col-md-1">Filter values displayed </div>
							<div class="col-md-7">Description</div>
						</div>
						<div class="row" ng-repeat="(label, pref) in prefs" ng-if="pref.field.aggregatable">
							<div class="col-md-1"></div>
							<div class="col-md-2">{{ label }}</div>
							<div class="col-md-1">
								<label class="switch">
  									<input type="checkbox" 
  										ng-change="sc.settings.update( pref.id,'aggregate',pref.aggregate, 'Filter category updated' )"
  										ng-model="pref.aggregate">
  									<span class="slider round"></span>
								</label>
							</div>
							<div class="col-md-1">
								<a class="editable-select" editable-select="pref.numberOfFilterOptions" buttons="no" 
									onbeforesave="sc.settings.update( pref.id,'numberOfFilterOptions',$data,'Number of values updated' )"
									e-ng-options="s.value as s.text for s in sc.settings.filterOptionSizes">
									{{ pref.numberOfFilterOptions }}
								</a>
							</div>
							<div class="col-md-7">{{ pref.field.description }}</div>
						</div>
					</div>
					<div class="row" ng-if="corpus==sc.settings.corpus">
						<div class="col-md-12">&nbsp;</div>
					</div>
				</div>
			</div>
			
			<div ng-if="sc.settings.view=='exports'" ng-repeat="(corpus, ontologies) in sc.settings.prefs">
				<div ng-repeat="(ontology, prefs) in ontologies">
					<div ng-if="corpus==sc.settings.corpus">
						<div class="row" style="background-color:#e6e6e6;color:black">
							<div class="col-md-1">{{ ontology }}</div>
							<div class="col-md-2">Filter category</div>
							<div class="col-md-5">Description</div>
							<div class="col-md-4">Include in export</div>
						</div>
						<div class="row" ng-repeat="(label, pref) in prefs" ng-if="pref.field.exportable==true">
							<div class="col-md-1"></div>
							<div class="col-md-2">{{ label }}</div>
							<div class="col-md-5">{{ pref.fieldDescription }}</div>
							<div class="col-md-4">
								<label class="switch">
  									<input type="checkbox" 
  										ng-change="sc.settings.update( pref.id,'export',pref.export, 'Export status updated' )" 
										ng-model="pref.export" />
									<span class="slider round"></span>
								</label>
							</div>
						</div>
					</div>
					<div class="row" ng-if="corpus==sc.settings.corpus">
						<div class="col-md-12">&nbsp;</div>
					</div>
				</div>
			</div>
			
		</div>

	</body>
</html>
