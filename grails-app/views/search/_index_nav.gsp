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
			<li tooltip-placement="bottom" uib-tooltip-template="'expansion-tooltip.html'" 
						tooltip-popup-close-delay="1250" uib-data-container="body">
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
			<li data-container="body" data-toggle="tooltip" 
				data-placement="bottom" 
				ng-click="sc.searchService.exportSearch(sc.currentSearch)"
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
						/>
					
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">
						
						<button type="button" class="btn btn-default recent-queries" data-toggle="dropdown" style="border-left:none" title="search history">
							<i class="fa fa-caret-down recent-queries" aria-hidden="true"></i>
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
						<button class="btn btn-default" type="submit" ng-click="sc.currentSearch.e()">
							<i class="fa fa-search" ng-class="sc.currentSearch.searchIconClass" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</form>

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

<script>
$(document).ready(function(){
    //$('.expansion-control').tooltip({delay: {show: 300, hide: 2300}}); 
	$('[data-toggle="tooltip"]').tooltip({
	      animation: true,
	      delay: {show: 200, hide: 300}
	    });
});
</script>
  					