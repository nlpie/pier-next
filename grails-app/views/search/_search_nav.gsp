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
                            <span class="label-icon" title="{{sc.uiState.currentContext.description}}" >{{sc.uiState.currentContext.icsRequest}}</span>
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li role="presentation" class="dropdown-header pull-left">Authorized Search Contexts</li>
                        	<li ng-repeat="c in sc.uiState.authorizedContexts" class="pull-left">
                        		<a>
		                        	<i class="fa fa-file-text-o pier-li-icon" ng-click="sc.uiState.changeContext(c)" title="search only notes"></i>
		                        	<i class="fa icon-i-imaging-root-category pier-li-left-padded-icon" ng-click="sc.uiState.changeContext(c)" title="search only imaging reports"></i>
                        			
                        			<i class="icon-i-pathology pier-li-left-padded-icon"></i>
                        			
                        			<span class="pier-li-left-padded-content" ng-click="sc.uiState.changeContext(c)" title="{{c.description}}">{{c.icsRequest}}</span>
                        		</a>
                            </li>
                        </ul>
                    </div>
					<input type="text" name="query" class="form-control" 
						placeholder="Search words and/or phrases anywhere in the note text" 
						ng-model="sc.uiState.currentSearch.userInput"
						style="border-right:none;-webkit-box-shadow: none !important;-moz-box-shadow: none !important;box-shadow: none !important;"
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

    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>