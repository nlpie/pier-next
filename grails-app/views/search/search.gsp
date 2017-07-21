<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>

		<div class="container-fluid" ng-controller="resultsController as rc">
			<div growl limit-messages="1" reference="full"></div>
			<div ng-repeat="corpus in rc.search.context.candidateCorpora track by $index">
				<div class="row main">
					<div class="col-xs-3">
						<div growl reference="aggs"></div>
						<div ng-show="rc.search.status.computingAggs" id="aggs-spinner" style="padding-top:25px">
							<asset:image src="ajax-loader.gif" alt="Loading..." /> computing filters...
						</div>
						<div ng-if="rc.search.results[corpus.name].aggs.aggs && !rc.search.status.computingAggs" ng-repeat="(category, categoryItems) in corpus.queryFilters track by $index">
							{{category}} <i class="fa fa-question-circle"></i>
							<ul class="pier-ul" style="color:gray">
								<li ng-repeat="qf in categoryItems track by $index">
									<i class="fa fa-check-square-o"></i>
									{{qf.label}} <i class="fa fa-question-circle"></i><br>
									<div ng-repeat="bucket in rc.search.results[corpus.name].aggs.aggs[qf.label].buckets track by $index">
										{{ qf.isTemporal ? bucket.key_as_string : bucket.key}} 
										<span style="font-size:0.5em">({{bucket.doc_count | number}})</span>
									</div>
									<div>&nbsp</div>
								</li>
							</ul>
						</div>
						<!-- 
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
						 -->
					</div>
	
					<div class="col-xs-9">
						<div growl reference="docs"></div>
						<div ng-show="rc.search.status.searchingDocs" id="docs-spinner" style="padding-top:25px">
							<asset:image src="ajax-loader.gif" alt="Loading..." /> searching corpora...
						</div>
						
						<div class="panel panel-default" ng-if="rc.search.results[corpus.name].docs" ng-repeat="doc in rc.search.results[corpus.name].docs.hits track by $index">
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
