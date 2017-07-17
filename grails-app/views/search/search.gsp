<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>

		<div class="container-fluid" ng-controller="resultsController as rc">
			<div ng-repeat="corpus in rc.search.context.candidateCorpora track by $index">
				<div class="row main">
					<div class="col-xs-3" ng-if="rc.search.results[corpus.name]">
						
						<div ng-repeat="(category, categoryItems) in corpus.queryFilters track by $index">
							{{category}} <i class="fa fa-question-circle" aria-hidden="true"></i>
							<ul class="pier-ul" style="color:gray">
								<li ng-repeat="field in categoryItems track by $index"><i class="fa fa-check-square-o" aria-hidden="true"></i>
									{{field.label}} (3,101)</li>
							</ul>
						</div>
						
						
						
						<ul class="pier-ul" style="color:gray">
							<li><i class="fa fa-check-square-o" aria-hidden="true"></i>
								7.b. Office (3,101)</li>
							<li><i class="fa fa-square-o" aria-hidden="true"></i>
								4. Inpatient Hospital (44) </li>
							<li><i class="fa fa-check-square-o" aria-hidden="true"></i>
								7.d. Urgent Care Center (13)</li>
							<li><i class="fa fa-minus-square-o" aria-hidden="true"></i>
								3. Emergency Department (3)</li>
							<li><i class="fa fa-square-o" aria-hidden="true"></i>
								7.c. Outpatient Hospital (1)</li>
						</ul>
	
						
					</div>
	
					<div class="col-xs-9">
						
						<div class="panel panel-default" ng-if="rc.search.results[corpus.name]" ng-repeat="doc in rc.search.results[corpus.name].docs.hits track by $index">
							<div class="panel-body">
								{{ doc._source.text }}
							</div>
						</div>
	
					</div>
				</div>
			</div>
		</div>
	
</body>
</html>
