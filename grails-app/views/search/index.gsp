<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="pier" />
		<title><g:meta name="admin.title"/></title>
	</head>
	<body>
		<div growl></div>
		<div growl reference="export"></div>
		<div class="container-fluid" ng-controller="resultsController as rc">
			
			<div ng-attr-id="{{ rc.search.context.corpus.name }}" class="row">
				<div id="agg-column" class="col-xs-3">
					<div growl reference="aggs"></div>
					<div ng-show="rc.search.context.corpus.status.computingAggs" id="aggs-spinner" style="padding-top:25px">
						<asset:image src="ajax-loader.gif" alt="determining filters..." /> determining filters...
					</div>
					<div class="pier-ontology"
						ng-if="rc.search.context.corpus.results.aggs.aggs && !rc.search.context.corpus.status.computingAggs" 
						ng-repeat="ontology in rc.search.context.corpus.metadata.aggregations track by $index">
						<label class="pier-ontology-label">{{ontology.name}}</label>
						<div class="pier-aggregate" ng-repeat="aggregation in ontology.aggregations track by $index">
							<div> 
								<label>{{aggregation.label}}
									<i 
										class="fa fa-question-circle" 
										bs-tooltip	
										data-placement="right"
										title="{{aggregation.field.description}}<br>"
										data-toggle="tooltip"
										data-container="body" 
										data-html="true" 
										tooltip-popup-close-delay="1500"
									></i>
								</label>
								<span ng-show="aggregation.status.computingCounts" id="aggs-spinner" style="padding-top:25px">
									<asset:image src="ajax-loader.gif" alt="determining filters..." /> computing <i class="tally-aggregate">EB</i> s
								</span>
								<span ng-if="rc.search.instance.distinctCounts.on && (aggregation.count || aggregation.cardinalityEstimate)" style="font-size:0.7em;margin-right:1em">
									actual:{{aggregation.count | number}}, estimate:{{aggregation.cardinalityEstimate | number}}
								</span>
								<!-- show toggle if temporal aggregation and it's active -->
								<label ng-if="aggregation.isTemporal && aggregation.isActive()" class="switch pull-right">
  									<input type="checkbox" 
  										ng-click="rc.search.toggleAggregation( aggregation )" 
  										ng-model="aggregation.initialSlider.filtered">
  									<span class="slider round"></span>
								</label>
							</div>
							<div class="pier-filter" ng-repeat="bucket in rc.search.context.corpus.results.aggs.aggs[aggregation.label].buckets track by $index">
								<!-- toggle (label/value) for typical string-based filter values for a particular aggregation -->
								<span style="cursor:pointer" 
										ng-click="rc.search.toggleAggregationValue( aggregation, bucket.key )">
									{{ aggregation.isTemporal ? bucket.key_as_string : bucket.label ? bucket.label : bucket.key }}
								</span>
								<span style="font-size:0.5em">({{bucket.doc_count | number}})</span>
								<!-- toggle (slider) for typical string-based filter values for a particular aggregation -->
								<label class="switch pull-right">
  									<input type="checkbox" ng-click="rc.search.context.corpus.updateUiFilterInfo();rc.search.filterChange()" ng-model="aggregation.filters[bucket.key]">
  									<span class="slider round"></span>
								</label>
							</div>
							<rzslider class="custom-slider" ng-if="aggregation.isTemporal" 
								rz-slider-model="aggregation.currentSlider.minValue"
								rz-slider-high="aggregation.currentSlider.maxValue"
								rz-slider-options="aggregation.initialSlider.options"
								ng-mouseup="rc.search.sliderMouseupChange( aggregation )"
								ng-keyup="rc.search.sliderKeyupChange( $event, aggregation )" >
							</rzslider>
							<hr>
						</div>
					</div>
				</div>

				<div id="doc-column" class="col-xs-9" ng-style="rc.search.context.corpus.status.opacity">
					<div growl reference="docs"></div>
					<div growl limit-messages="1" reference="full"></div>
					<div ng-show="rc.search.context.corpus.status.searchingDocs" id="docs-spinner" style="padding-top:25px">
						<asset:image src="ajax-loader.gif" alt="searching corpus..." /> searching corpus...
					</div>
					<div ng-show="rc.search.context.corpus.results.docs.isEmpty() && !rc.search.context.corpus.status.searchingDocs">NO DOCUMENTS FOUND</div>
					<div ng-repeat="doc in rc.search.context.corpus.results.docs.hits track by $index"
						ng-if="!rc.search.context.corpus.results.docs.isEmpty() && !rc.search.context.corpus.status.searchingDocs" 
						ng-switch="rc.search.context.corpus.name">
						<div ng-switch-when="Surgical Pathology Reports" class="panel panel-default panel-body">
							<pre ng-bind-html="doc.highlight ? doc.highlight[rc.search.context.corpus.metadata.defaultSearchField].join('<br>&nbsp;&vellip;<br> ') : doc._source[rc.search.context.corpus.metadata.defaultSearchField]">
							</pre>
						</div>
						<div ng-switch-when="PakhomovS-StressMentions" class="panel panel-default panel-body">
							<pre ng-bind-html="doc.highlight ? doc.highlight[rc.search.context.corpus.metadata.defaultSearchField].join('<br>&nbsp;&vellip;<br> ') : doc._source[rc.search.context.corpus.metadata.defaultSearchField]">
							</pre>
						</div>
						<div ng-switch-when="Copath Reports" class="panel panel-default panel-body">
							<pre ng-bind-html="doc.highlight ? doc.highlight[rc.search.context.corpus.metadata.defaultSearchField].join('<br>&nbsp;&vellip;<br> ') : doc._source[rc.search.context.corpus.metadata.defaultSearchField]">
							</pre>
						</div>
						<div ng-switch-when="MIMIC" >
							<pre ng-bind-html="doc.highlight ? doc.highlight[rc.search.context.corpus.metadata.defaultSearchField].join('<br>&nbsp;&vellip;<br> ') : doc._source[rc.search.context.corpus.metadata.defaultSearchField]">
							</pre>
						</div>
						<div ng-switch-when="Echo Reports" class="panel panel-default panel-body">
							<pre ng-bind-html="'*** PIER-HEADER mrn:' + doc._source['mrn'] + '; ef_results:' + doc._source['ef_results'] + ' ***\n\n' + doc._source[rc.search.context.corpus.metadata.defaultSearchField]">
							</pre>
						</div>
						
						<div ng-switch-default class="panel panel-default panel-body" style="border:none">
							<div>
								<pre style="width:98%" ng-bind-html="doc.highlight ? doc.highlight[rc.search.context.corpus.metadata.defaultSearchField].join('<br>&nbsp;&vellip;<br> ') : doc._source[rc.search.context.corpus.metadata.defaultSearchField]">
								</pre>
								<!-- <div class="fa fa-ellipsis-h pull-right" slide-toggle="#doc_{{doc._id}}" ></div> -->
							</div>
							<div>
								<div id="doc_{{doc._id}}" class="infoWidget" duration="0.3s">
									<!-- {{doc._source.encounter_center}} / {{doc._source.encounter_department_specialty}} / {{doc._source.encounter_clinic_type}} |   -->
									<span>Speciality:</span> {{doc._source.encounter_department_specialty}} | 
									<span>Service date:</span> {{doc._source.service_date}} |
									<span>Provider type:</span> {{doc._source.prov_type}} |
									<span>MRN:</span> {{doc._source.mrn}}
									<span ng-if="!rc.search.instance.lastQuery && doc._source.service_id" ng-click="rc.search.encSearch(doc._source.service_id)"> | <a style="cursor:pointer">Encounter view</a></span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	
</body>
</html>
