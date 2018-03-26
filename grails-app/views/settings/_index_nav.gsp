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
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1" ng-controller="settingsController as sc">
		<ul class="nav navbar-nav navbar-right nav-pills">
			<li class="dropdown">
          		<a class="dropdown-toggle" data-toggle="dropdown" role="button">
					{{ sc.settings.corpus }} <span class="caret"></span>
				</a>
          		<ul class="dropdown-menu">
					<li role="presentation" class="dropdown-header">Searchable Corpora</li>
            		<li ng-repeat="(corpus, ontologies) in sc.settings.prefs">
						<a ng-click="sc.settings.changeCorpus(corpus)">{{ corpus }}</a>
					</li>
          		</ul>
        	</li>
			
			<li id="{{key}}" ng-class="sc.settings.styles.{{key}}" ng-class="sc.settings.styles.filters" ng-click="sc.settings.swap(key)" ng-repeat="(key, view) in sc.settings.views">
				<a style="border-radius:0px">{{ view }}</a>
			</li>
			
		</ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>