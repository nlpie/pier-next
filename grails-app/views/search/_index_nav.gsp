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
			
			<!--<li tooltip-placement="bottom" uib-tooltip-template="'expansion-tooltip.html'" 
						tooltip-popup-delay="1000" tooltip-popup-close-delay="1100" uib-data-container="body">
				<a ng-style="sc.currentSearch.inputExpansion.style">
					<i class="fa fa-expand fa-lg" ng-click="sc.modalService.vectorExpansions('lg','expansionController')"></i> 
					<sup ng-if="sc.currentSearch.inputExpansion.cardinality()" >{{sc.currentSearch.inputExpansion.cardinality()}}</sup>
				</a>
			</li>
			
			<li data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" title="enable/disable distinct counts for applicable category aggregates">
				<a><i class="tally" ng-click="sc.currentSearch.instance.toggleDistinctCounts()" ng-style="sc.currentSearch.instance.distinctCounts.style">
					EB
					</i>
				</a>
			</li>
			-->
			<li data-container="body" data-toggle="tooltip" 
				data-placement="bottom" 
				ng-click="sc.currentSearch.exportResults()"
				title="download query results">
				<a><i class="fa fa-download fa-lg"></i></a>
			</li>
			<li data-container="body" data-toggle="tooltip" data-placement="bottom" title="save query for later use">
				<a><i class="fa fa-floppy-o fa-lg" ng-click="sc.searchService.saveQuery(sc.currentSearch.instance)"></i></a>
			</li>
		</ul>
		
		<g:render template="expansionSelection" />

		<form class="navbar-form">
       		<div class="form-group" style="display:inline;" >
       			<div class="input-group" style="display:table;">
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">					
						<button type="button" class="btn btn-default" data-toggle="dropdown" style="border-right:none;border-left:none"
							uib-tooltip="search contexts" tooltip-popup-delay="1000" tooltip-placement="bottom" uib-data-container="body"
						>
                            <span class="label-icon" title="{{sc.currentSearch.context.description}}" >{{sc.currentSearch.context.label}}</span>
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li role="presentation" class="dropdown-header">Authorized Search Contexts</li>
                        	<li ng-repeat="ctx in sc.currentSearch.authorizedContexts track by $index" class="pull-left">
                        		<a>
		                        	<i class="fa {{ctx.corpus.glyph}} pier-li-left-padded-icon"
		                        		ng-style="{'color':'green'}"
		                        		ng-attr-title="{{sc.currentSearch.context.corpus.metadata.tooltip}}">
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
							position: relative"
					/>
					
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">

						<button type="button" class="btn btn-default recent-queries blend-adjacent" data-toggle="dropdown" 
							uib-tooltip="recent searches" tooltip-popup-delay="1000" tooltip-placement="bottom" uib-data-container="body"
						>
							<i class="fa fa-caret-down recent-queries"></i>
						</button>
						<ul class="dropdown-menu pull-right">
                            <li role="presentation" class="dropdown-header" style="font-variant:small-caps">Recent Searches</li>
                        	<li ng-repeat="item in sc.searchService.searchHistory track by $index">
								<a ng-click="sc.currentSearch.recentSearch(item.query)">
                        			<div title="click to search">
	                        			<sub style="color:gray">{{item.query.authorizedContext}}</sub>
	                        			<br>
	                        			<span style="text-decoration:none">{{item.query.userInput}}</span> 
	                        				<small style="color:green" ng-if="item.query.inputExpansion"><i class="fa fa-expand"></i></small>
		                        			<small style="color:green" ng-if="item.query.distinctCounts"><i class="tally-recent-saved">EB&nbsp;</i></small>
	                        				<span style="font-style:italic;color:green" ng-if="item.query.saved==true"><i class="fa fa-floppy-o"></i></span> 
	                        				<small style="color:gray" ng-if="item.query.filterSummary"><i class="fa fa-filter fa-lg" style="color:green"></i><i>: {{item.query.filterSummary}}</i></small>
                        			</div>
                        		</a>
                            </li>
                        </ul>
                        
                        <button 
                        	class="btn btn-default blend-adjacent" 
		  					tooltip-placement="bottom" uib-tooltip-template="'filter-tooltip.html'" 
							tooltip-popup-delay="1000" tooltip-popup-close-delay="2000" uib-data-container="body"
			  			>
		  					<span data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true">
		  						<i class="fa fa-filter" ng-style="sc.currentSearch.context.corpus.status.filter.style"></i>
							</span>
						</button>

						<button ng-click="sc.modalService.vectorExpansions('lg','expansionController')" 
							class="btn btn-default blend-adjacent" 
							tooltip-placement="bottom" uib-tooltip-template="'expansion-tooltip.html'" 
							tooltip-popup-delay="1000" tooltip-popup-close-delay="1100" uib-data-container="body">
							<span ng-style="sc.currentSearch.inputExpansion.style">
								<i class="fa fa-expand fa-lg"></i> 
								<sup ng-if="sc.currentSearch.inputExpansion.cardinality()" >{{sc.currentSearch.inputExpansion.cardinality()}}</sup>
							</span>
						</button>
						
						<button 
							ng-click="sc.currentSearch.instance.toggleDistinctCounts()"
							class="btn btn-default blend-adjacent"
							data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" 
							title="enable/disable distinct counts for applicable category aggregates">
							<i class="tally" ng-style="sc.currentSearch.instance.distinctCounts.style">
								EB
							</i>
						</button>
						
						<button class="btn btn-default" type="submit" ng-click="sc.currentSearch.e()">
							<i class="fa fa-search" ng-class="sc.currentSearch.searchIconClass"></i>
						</button>
					</div>
				</div>
			</div>
		</form>

		<div ng-if="sc.currentSearch.context.corpus.results.docs" class="btn-group pull-right" role="group" style="margin-right:170px">
			<div ng-if="sc.currentSearch.context.corpus.metadata.searchable">
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.firstPage( sc.currentSearch.context.corpus )" ng-style="{cursor:sc.currentSearch.context.corpus.results.backwardCursor( sc.currentSearch.context.corpus )}"> <i class="fa fa-angle-double-left"> </i></button>
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.previousPage( sc.currentSearch.context.corpus )" ng-style="{cursor:sc.currentSearch.context.corpus.results.backwardCursor( sc.currentSearch.context.corpus )}"> <i class="fa fa-angle-left"> </i></button>
				
				<button 
					type="button"
					class="btn btn-default btn-result-action"
					ng-class="{ 'btn-result-action-on': sc.currentSearch.context.corpus.status.active }"
					data-container="body" data-toggle="tooltip" data-placement="top"
					data-html="true">
					{{sc.currentSearch.context.corpus.name}}
					<span style="font-size: 0.6em">{{ sc.currentSearch.context.corpus.results.pagination.currentPageInfo() }} of {{sc.currentSearch.context.corpus.results.pagination.maxDocs | number}}</span>
				</button>
				
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.nextPage( sc.currentSearch.context.corpus )" ng-style="{cursor:sc.currentSearch.context.corpus.results.forwardCursor( sc.currentSearch.context.corpus )}"> <i class="fa fa-angle-right"></i> </button>
				<button type="button" class="btn btn-default btn-result-pagination" ng-click="sc.currentSearch.lastPage( sc.currentSearch.context.corpus )" ng-style="{cursor:sc.currentSearch.context.corpus.results.forwardCursor( sc.currentSearch.context.corpus )}"> <i class="fa fa-angle-double-right"> </i></button>
				
			</div>
		</div>

    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
  					