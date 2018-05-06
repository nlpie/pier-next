<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>
		<div growl></div>
		<div class="container-fluid" ng-controller="resultsController as rc">
			
			<div ng-attr-id="{{ corpus.name }}" ng-show="corpus.status.active" class="row" ng-repeat="corpus in rc.search.context.corpora track by $index">
				<div id="agg-column" class="col-xs-3">
					<div growl reference="aggs"></div>
					<div ng-show="corpus.status.computingAggs" id="aggs-spinner" style="padding-top:25px">
						<asset:image src="ajax-loader.gif" alt="determining filters..." /> determining filters...
					</div>
					<div class="pier-ontology"
						ng-if="corpus.results.aggs.aggs && !corpus.status.computingAggs" 
						ng-repeat="ontology in corpus.metadata.aggregations track by $index">
						<label class="pier-ontology-label">{{ontology.name}}</label>
						<div class="pier-aggregate" ng-repeat="aggregation in ontology.aggregations track by $index">
							<div>
								<label ng-click="rc.show(corpus.status)">{{aggregation.label}} 
									<i class="fa fa-question-circle" title="{{aggregation.field.description}}"></i>
								</label>
								<span ng-if="aggregation.countDistinct" style="font-size:0.5em;margin-right:1em">{{aggregation.count | number}}, {{aggregation.cardinalityEstimate | number}} ({{aggregation.countType}}, cardinality) counts</span>
								<label ng-if="aggregation.isTemporal" class="switch pull-right">
  									<input type="checkbox" ng-click="rc.search.dirty(corpus);corpus.status.userSelectedFilters=true;aggregation.currentSlider.reset( aggregation )" ng-model="aggregation.initialSlider.filtered">
  									<span class="slider round"></span>
								</label>
							</div>
							<div class="pier-filter" ng-repeat="bucket in corpus.results.aggs.aggs[aggregation.label].buckets track by $index">
								<span style="cursor:pointer" ng-click="aggregation.filters[bucket.key]=!aggregation.filters[bucket.key];rc.search.dirty(corpus);corpus.status.userSelectedFilters=true">
									{{ aggregation.isTemporal ? bucket.key_as_string : bucket.key }}
								</span>
								<span style="font-size:0.5em">({{bucket.doc_count | number}})</span>
								<label class="switch pull-right">
  									<input type="checkbox" ng-click="rc.search.dirty(corpus);corpus.status.userSelectedFilters=true" ng-model="aggregation.filters[bucket.key]">
  									<span class="slider round"></span>
								</label>
							</div>
							<rzslider class="custom-slider" ng-if="aggregation.currentSlider" 
								rz-slider-model="aggregation.currentSlider.minValue"
								rz-slider-high="aggregation.currentSlider.maxValue"
								rz-slider-options="aggregation.currentSlider.options"
								ng-mouseup="aggregation.currentSlider.updateAggregationFilter( aggregation )"
								ng-keyup="aggregation.currentSlider.up( aggregation )"
							></rzslider>
							<hr>
						</div>
					</div>
				</div>

				<div id="doc-column" class="col-xs-9" ng-style="corpus.status.opacity">
					<div growl reference="docs"></div>
					<div growl limit-messages="1" reference="full"></div>
					<div ng-show="corpus.status.searchingDocs" id="docs-spinner" style="padding-top:25px">
						<asset:image src="ajax-loader.gif" alt="searching corpus..." /> searching corpus...
					</div>
					<div ng-repeat="doc in corpus.results.docs.hits track by $index"
						ng-if="corpus.results.docs && !corpus.status.searchingDocs" 
						ng-switch="corpus.name">
						<div ng-switch-when="Surgical Pathology Reports" class="panel panel-default panel-body">
							<pre ng-bind-html="doc.highlight ? doc.highlight[corpus.metadata.defaultSearchField].join('<br>&nbsp;&vellip;<br> ') : doc._source[corpus.metadata.defaultSearchField]">
							</pre>
							<!-- <pre 
								ng-repeat="frag in doc.highlight[corpus.metadata.defaultSearchField] track by $index"
								ng-bind-html="frag.toString()">
							</pre>
							 -->
						</div>
						<div ng-switch-default class="panel panel-default panel-body" style="border:none">
							<div>
								<div class="pull-left" style="width:98%" ng-bind-html="doc.highlight ? doc.highlight[corpus.metadata.defaultSearchField].join('<br>&nbsp;&vellip;<br> ') : doc._source[corpus.metadata.defaultSearchField]">
								</div>
								<div class="fa fa-ellipsis-h pull-right" slide-toggle="#doc_{{doc._id}}" ></div>
							</div>
							<div class="pull-right">
								<div id="doc_{{doc._id}}" class="slideable infoWidget" duration="0.3s">
									{{doc._source.encounter_center}} / {{doc._source.encounter_department_specialty}} / {{doc._source.encounter_clinic_type}} |
									<span>Service date:</span> {{doc._source.service_date}} |
									<span>Filed:</span> {{doc._source.filing_datetime}} |
									<span>Provider type:</span> {{doc._source.prov_type}} | 
									<span ng-click="rc.search.encSearch(doc._source.service_id)"><a style="cursor:pointer"> Encounter view</a></span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

	
</body>
</html>
