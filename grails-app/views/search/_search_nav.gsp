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
			<li>
				<a><i class="fa fa-expand fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" title="expand query using semantic similarity"></i></a>
			</li>
			<li>
				<a><i class="fa fa-language fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" data-html="true" title="expand query using UMLS CUIs"></i></a>
			</li>
			<li>
				<a><i class="fa fa-download fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" title="download query results"></i></a>
			</li>
			<li>
				<a><i class="fa fa-floppy-o fa-lg" data-container="body" data-toggle="tooltip" data-placement="bottom" title="save query for later use"></i></a>
			</li>
		</ul>

		<form class="navbar-form">
       		<div class="form-group" style="display:inline;" >
       			<div class="input-group" style="display:table;">
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">
						<button type="button" class="btn btn-default" data-toggle="dropdown" style="border-right:none">
                            <span class="label-icon" title="{{sc.uiState.currentSearch.context.description}}" >{{sc.uiState.currentSearch.context.label}}</span>
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li role="presentation" class="dropdown-header pull-left">Authorized Search Contexts</li>
                        	<li ng-repeat="c in sc.uiState.authorizedContexts track by $index" class="pull-left">
                        		<a ng-click="sc.uiState.changeContext($index)">
		                        	<i ng-repeat="ct in c.candidateCorpora track by $index" 
		                        		class="fa {{ct.glyph}} pier-li-left-padded-icon"
		                        		ng-style="(ct.queryInfo.searchable) ? {'color':'green'} : {'color': 'lightgray'}"
		                        		ng-attr-title="{{ct.queryInfo.tooltip}}">
		                        	</i> 
                        			<span class="pier-li-left-padded-content" ng-click="sc.uiState.changeContext(c)" title="{{c.description}}">{{c.label}}</span>
                        		</a>
                            </li>
                        </ul>
                    </div>
					<input id="user-input" type="text" name="query" class="form-control" 
						placeholder="Search words and/or phrases anywhere in the note text" 
						ng-model="sc.uiState.currentSearch.userInput"
						style="border-right:none;-webkit-box-shadow: none !important;-moz-box-shadow: none !important;box-shadow: none !important;
							position: relative;"
						ng-model-options="{
    						'updateOn': 'default blur',
    						'debounce': {
      							'default': 250,
      							'blur': 0
    						}
    					}" />
					
					<div class="input-group-btn" class="input-group-addon" style="width:1%;">
						
						<button type="button" class="btn btn-default recent-queries" data-toggle="dropdown" style="border-left:none" title="query history">
							<i class="fa fa-caret-down recent-queries" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu pull-right">
							<li role="presentation" class="dropdown-header">Query History</li>
							<li>
				          		<a href ng-click="">query n</a>
							</li>
							<li>
				          		<a href ng-click="">query n-1</a>
							</li>
							<li>
				          		<a href ng-click="">query n-2</a>
							</li>
						</ul>
						<button class="btn btn-default" type="submit" ng-click="sc.uiState.currentSearch.execute()">
							<i class="fa fa-search" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</form>

		<div class="btn-group pull-right" role="group" style="margin-right:160px">
			<div>
				<button ng-repeat="corpus in sc.uiState.currentSearch.context.candidateCorpora track by $index"
					ng-if="sc.uiState.currentSearch.results[corpus.name]"
					type="button"
					ng-if="corpus.queryInfo.searchable"
					class="btn btn-default btn-result-action"
					ng-class="{ 'btn-result-action-on': corpus.selected }"
					data-container="body" data-toggle="tooltip" data-placement="top"
					data-html="true">
					{{corpus.name}} <span style="font-size: 0.5em">{{sc.uiState.currentSearch.results[corpus.name].docs.total | number}}</span>
				</button>
				<button type="button"
					class="btn btn-default btn-result-action"
					data-container="body" data-toggle="tooltip" data-placement="top"
					data-html="true">
					Another <span style="font-size: 0.5em">54</span>
				</button>
			</div>
		</div>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>