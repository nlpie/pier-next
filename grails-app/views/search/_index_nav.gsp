<!-- getting expanded search bar in navbar form based on http://stackoverflow.com/questions/18552714/bootstrap-3-how-to-maximize-input-width-inside-navbar http://www.bootply.com/t7O3HSGlbc -->
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <!-- <a class="navbar-brand" href="#">Brand</a> -->
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1" ng-controller="searchController as sc">

		<ul class="nav navbar-nav navbar-right">
			<!-- configure links in NavCtrl links property -->
			<!-- <li>
				<a><i class="fa fa-expand fa-lg" ng-click="sc.currentSearch.toggleRelatednessExpansion()" ng-style="sc.currentSearch.options.relatednessExpansion.style" data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" title="expand query using semantic similarity"></i></a>
			</li>
			 -->
			<li>
				<a ng-style="sc.currentSearch.inputExpansion.style">
					<i class="fa fa-expand fa-lg" ng-click="sc.modalService.vectorExpansions('lg','modalController')" 	
					data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" 
					title="query expansion suggestions"></i> 
					<sup>{{sc.currentSearch.inputExpansion.cardinality()}}</sup>
				</a>
			</li>
			<!-- <li>
				<a>
					<i class="fa fa-language fa-lg" title="potential expansion terms" uib-popover-template="sc.currentSearch.template" 
						popover-placement="bottom-right" popover-append-to-body="true" popover-title="Query Expansion Choices"
						data-toggle="tooltip" data-placement="bottom" data-html="true"
						ng-click="sc.searchService.fetchRelated(sc.currentSearch.userInput)">
					</i>
				</a>
			</li> -->
			<li>
				<a><i class="fa fa-download fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" title="download query results"></i></a>
			</li>
			<li>
				<a><i class="fa fa-floppy-o fa-lg" ng-click="sc.searchService.saveQuery(sc.currentSearch.registration.id)" data-container="body" data-toggle="tooltip" data-placement="bottom" title="save query for later use"></i></a>
			</li>
		</ul>
				
		<!-- 
		<script type="text/ng-template" id="myPopoverTemplate.html">
        	<div>
				<div> <small> click words to add to search terms</small> </div>
        		<div>
					<div><b>Related misspellings:</b></div>
					<span 
						ng-repeat="item in sc.searchService.relatedTerms._source.misspelling_relatedness track by $index"
						ng-click="sc.currentSearch.addRelatedTerm(item.term)">
						{{item.term}}
					</span>
					<div><b>Semantically related terms:</b></div>
					<span 
						ng-repeat="item in sc.searchService.relatedTerms._source.semantic_relatedness"
						ng-click="sc.currentSearch.addRelatedTerm(item.term)">
						{{item.term}} 
					</span>
        		</div>
			</div>
    	</script>
    	 -->
    	<script type="text/ng-template" id="myModalContent.html">
        	<div class="modal-header">
            	<h3 class="modal-title" id="modal-title">Word vector-based suggestions</h3>
				<small> selected suggestions are added to the current search </small>
        	</div>
        	<div class="modal-body" id="modal-body">
            	
				
				<uib-tabset active="active">
    				<uib-tab ng-repeat="embedding in ctrl.embeddings track by $index" index="$index" heading="{{ctrl.currentSearch.inputExpansion.targetLabel(embedding.word)}}">

						<div class="row">
							<div class="col-md-12" ng-if="ctrl.currentSearch.inputExpansion.targetHasExpansions(embedding.word)">
								<br/>&nbsp;
								<b>Selected expansion terms:</b> {{ ctrl.currentSearch.inputExpansion.flatten(embedding.word) }}
								<br/>&nbsp;
							</div>
							<div class="col-md-12" ng-if="!ctrl.currentSearch.inputExpansion.targetHasExpansions(embedding.word)">
								<br/>&nbsp;
								No expansions selected
								<br>&nbsp;
							</div>
						</div>
						<div class="row">
							<div class="col-md-12"><b>Related misspellings</b></div>
            			</div>
						<div class="row">
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:0">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:8">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>

							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:16">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].relatedMisspellings | limitTo:8:24">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>
						</div>

						<div class="row">
							<div class="col-md-12"><b>Semantically related terms</b></div>
            			</div>
						<div class="row">
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:25:0">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:25:25">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:25:50">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>
							<div class="col-md-3">
								<ul>
                					<li ng-repeat="suggestion in ctrl.embeddings[$index].semanticallyRelatedTerms | limitTo:25:75">
                    					<a ng-click="ctrl.currentSearch.inputExpansion.add(embedding.word, suggestion.term)">{{ suggestion.term }}</a>
                					</li>
            					</ul>
							</div>
						</div>

					</uib-tab>
				</uib-tabset>
        	</div>
        	<div class="modal-footer">
            	<button class="btn btn-primary" type="button" ng-click="ctrl.modalService.ok()">OK</button>
            	<button class="btn btn-warning" type="button" ng-click="ctrl.modalService.cancel()">Cancel</button>
        	</div>
    	</script>

		<form class="navbar-form">
       		<div class="form-group" style="display:inline;" >
       			<div class="input-group" style="display:table;">
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">
						<button ng-repeat="corpus in sc.currentSearch.context.corpora track by $index" type="button" class="btn btn-default" 
								ng-if="corpus.status.active && corpus.status.userSelectedFilters"
								style="border-right:none"
		  						ng-mouseover="corpus.status.showBan=true" 
		  						ng-mouseleave="corpus.status.showBan=false"
		  						title="remove filters for this corpus"
		  						ng-click="corpus.removeFilters();sc.currentSearch.dirty( corpus )">
							<i class="fa fa-filter"></i>
							<i class="fa fa-ban" style="color:red;cursor:hand" ng-if="corpus.status.showBan==true"></i>
						</button>
						<button type="button" class="btn btn-default" data-toggle="dropdown" style="border-right:none;border-left:none">
                            <span class="label-icon" title="{{sc.currentSearch.context.description}}" >{{sc.currentSearch.context.label}}</span>
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li role="presentation" class="dropdown-header">Authorized Search Contexts</li>
                        	<li ng-repeat="ctx in sc.currentSearch.authorizedContexts track by $index" class="pull-left">
                        		<a>
		                        	<i ng-repeat="corpus in ctx.corpora track by $index" 
		                        		class="fa {{corpus.glyph}} pier-li-left-padded-icon"
		                        		ng-style="(corpus.metadata.searchable) ? {'color':'green'} : {'color': 'lightgray'}"
		                        		ng-attr-title="{{corpus.metadata.tooltip}}">
		                        	</i> 
                        			<span class="pier-li-left-padded-content" ng-click="sc.currentSearch.setContext(ctx)" title="{{ctx.description}}">{{ctx.label}}</span>
                        		</a>
                            </li>
                        </ul>
                    </div>
					<input id="user-input" type="text" name="query" class="form-control" 
						placeholder="term1 AND (term2 OR term3) NOT (term4 OR &#34;multiterm phrase&#34;)" 
						ng-model="sc.currentSearch.userInput"
						ng-change="sc.currentSearch.dirty()"
						style="border-right:none;-webkit-box-shadow: none !important;-moz-box-shadow: none !important;box-shadow: none !important;
							position: relative;height:33.1px"
						ng-model-options="{
    						'updateOn': 'default blur',
    						'debounce': {
      							'default': 250,
      							'blur': 0
    						}
    					}" />
					
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">
						
						<button type="button" class="btn btn-default recent-queries" data-toggle="dropdown" style="border-left:none" title="search history">
							<i class="fa fa-caret-down recent-queries" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu pull-right">
                            <li role="presentation" class="dropdown-header pull-right">Recent Searches</li>
                        	<li ng-repeat="sh in sc.searchService.searchHistory track by $index">
								<a ng-click="sc.currentSearch.recentSearch(sh.query.id)">
                        			<div title="click to search">
	                        			<sub style="color:gray">{{sh.registration.authorizedContext}}</sub>
	                        			<br>
	                        			<span style="text-decoration:underline">{{sh.query.label}}</span> 
	                        				<span style="font-style:italic" ng-if="sh.query.saved">[saved]</span> 
	                        				<small style="color:gray" ng-if="sh.query.filters"><i>Filters: {{sh.query.filters}}</i></small>
                        			</div>
                        		</a>
                            </li>
                        </ul>
						<button class="btn btn-default" type="submit" ng-click="sc.currentSearch.e()">
							<i class="fa fa-search" ng-class="sc.currentSearch.searchIconClass" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</form>
{{sc.currentSearch.inputExpansion.expandUserInput(sc.currentSearch.userInput)}}
		<div ng-repeat="corpus in sc.currentSearch.context.corpora track by $index"
			 ng-if="corpus.results.docs" class="btn-group pull-right" role="group" style="margin-right:170px">
			<div ng-if="corpus.metadata.searchable">
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.firstPage( corpus )" ng-style="{cursor:corpus.results.backwardCursor( corpus )}"> <i class="fa fa-angle-double-left"> </i></button>
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.previousPage( corpus )" ng-style="{cursor:corpus.results.backwardCursor( corpus )}"> <i class="fa fa-angle-left"> </i></button>
				
  				<span class="fa-stack" ng-if="corpus.status.userSelectedFilters" 
  						ng-mouseover="corpus.status.showBan=true" 
  						ng-mouseleave="corpus.status.showBan=false"
  						title="remove filters for this corpus"
  						ng-click="corpus.removeFilters();sc.currentSearch.dirty( corpus )">
					<i class="fa fa-filter fa-stack-1x"></i>
					<i class="fa fa-ban fa-stack-1x" style="color:red;cursor:hand" ng-if="corpus.status.showBan==true"></i>
				</span>
				
				<button 
					type="button"
					class="btn btn-default btn-result-action"
					ng-class="{ 'btn-result-action-on': corpus.status.active }"
					data-container="body" data-toggle="tooltip" data-placement="top"
					data-html="true">
					{{corpus.name}}
					<span style="font-size: 0.6em">{{ corpus.results.pagination.currentPageInfo() }} of {{corpus.results.pagination.maxDocs | number}}</span>
				</button>
				
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.nextPage( corpus )" ng-style="{cursor:corpus.results.forwardCursor( corpus )}"> <i class="fa fa-angle-right"></i> </button>
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.lastPage( corpus )" ng-style="{cursor:corpus.results.forwardCursor( corpus )}"> <i class="fa fa-angle-double-right"> </i></button>
				
			</div>
		</div>

    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
  					