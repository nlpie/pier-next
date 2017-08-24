<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>
	
		<div class="container-fluid" ng-controller="resultsController as rc">
			<div ng-attr-id="{{ corpus.name }}" ng-show="corpus.selected" class="row" ng-repeat="corpus in rc.search.context.candidateCorpora track by $index">
				<div id="agg-column" class="col-xs-3">
					<div growl reference="aggs"></div>
					<div ng-show="rc.search.status.computingAggs" id="aggs-spinner" style="padding-top:25px">
						<asset:image src="ajax-loader.gif" alt="computing aggregations..." /> computing aggregations...
					</div>
					<div class="pier-ontology"
						ng-if="corpus.results.aggs.aggs && !rc.search.status.computingAggs" 
						ng-repeat="(ontology, aggregations) in corpus.metadata.aggregations track by $index">
						<label class="pier-ontology-label">{{ontology}} <i class="fa fa-question-circle"></i></label>
						<div class="pier-aggregate" ng-repeat="aggregation in aggregations track by $index">
							<div>
								<label>{{aggregation.label}} 
									<i class="fa fa-question-circle"></i>
								</label>
								<span ng-if="aggregation.countDistinct" style="font-size:0.5em;margin-right:1em">{{aggregation.count | number}} distinct</span>
							</div>
							<div class="pier-filter" ng-repeat="bucket in corpus.results.aggs.aggs[aggregation.label].buckets track by $index">
								<i class="fa fa-check-square-o"></i> 
								<span ng-click="rc.search.addFilter( corpus, aggregation, bucket.key )" style="cursor:pointer">{{ aggregation.isTemporal ? bucket.key_as_string : bucket.key}}</span>
								<span style="font-size:0.5em">({{bucket.doc_count | number}})</span>
							</div>
							<hr>
						</div>
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

				<div id="doc-column" class="col-xs-9" ng-style="corpus.opacity">
					<div growl reference="docs"></div>
					<div growl limit-messages="1" reference="full"></div>
					<div ng-show="rc.search.status.searchingDocs" id="docs-spinner" style="padding-top:25px">
						<asset:image src="ajax-loader.gif" alt="searching corpora..." /> searching corpora...
					</div>
					<div ng-repeat="doc in corpus.results.docs.hits track by $index"
						ng-if="corpus.results.docs && !rc.search.status.searchingDocs" 
						ng-switch="corpus.name">
						<div ng-switch-when="Surgical Pathology Reports">
							<pre>{{ doc._source[corpus.metadata.defaultSearchField] }}</pre>
						</div>
						<div ng-switch-default class="panel panel-default panel-body">
							{{ doc._source[corpus.metadata.defaultSearchField] }}
						</div>
					</div>

				</div>
			</div>

		</div>
	
</body>
</html>
