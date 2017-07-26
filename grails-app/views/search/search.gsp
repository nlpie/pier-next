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
					<div id="aggs" class="col-xs-3">
						<div growl reference="aggs"></div>
						<div ng-show="rc.search.status.computingAggs" id="aggs-spinner" style="padding-top:25px">
							<asset:image src="ajax-loader.gif" alt="Loading..." /> computing aggregations...
						</div>
						<ul ng-if="corpus.results.aggs.aggs && !rc.search.status.computingAggs" 
							ng-repeat="(ontology, aggregations) in corpus.metadata.aggregations track by $index">
							<li class="pier-li-ontology">{{ontology}} <i class="fa fa-question-circle"></i>
								<ul>
									<li class="pier-li-aggregation" ng-repeat="aggregation in aggregations track by $index">
										<i class="fa fa-check-square-o"></i> {{aggregation.label}} <i class="fa fa-question-circle"></i><br>
										<ul>
											<li class="pier-li-filter" ng-repeat="bucket in corpus.results.aggs.aggs[aggregation.label].buckets track by $index">
												<span ng-click="rc.search.addFilter( corpus, aggregation, bucket.key )" style="cursor:pointer">{{ aggregation.isTemporal ? bucket.key_as_string : bucket.key}}</span>
												<span style="font-size:0.5em">({{bucket.doc_count | number}})</span>
											</li>
										</ul>
									</li>
								</ul>
							</li>
						</ul>
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
	
					<div class="col-xs-9" ng-style="corpus.opacity">
						<div growl reference="docs"></div>
						<div ng-show="rc.search.status.searchingDocs" id="docs-spinner" style="padding-top:25px">
							<asset:image src="ajax-loader.gif" alt="Loading..." /> searching corpora...
						</div>
						
						<div class="panel panel-default" ng-if="corpus.results.docs && !rc.search.status.searchingDocs" ng-repeat="doc in corpus.results.docs.hits track by $index">
							<div class="panel-body">
								{{ doc._source.text }}
							</div>
						</div>
	
					</div>
				</div>
				
				<div class="row main">
					<div id="docs" class="col-xs-12">
						some centered stuff here
					</div>
				</div>
				
			</div>
		</div>
	
</body>
</html>
