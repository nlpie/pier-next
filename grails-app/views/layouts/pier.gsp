<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="icon" href="../../favicon.ico">
	
	<title><g:meta name="app.title"/></title>
	
	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
		      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
		      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		    <![endif]-->
		    
	<asset:javascript src="lib/jquery.min.js" />
	<asset:stylesheet src="lib/bootstrap.min.css" />
	<asset:stylesheet src="lib/admin.css" />
	<asset:stylesheet src="lib/column-scroll.css" />

	<asset:javascript src="lib/bootstrap.min.js" />
	<asset:stylesheet src="lib/font-awesome.min.css" />
	<asset:stylesheet src="lib/wfmi-style.css" />
	<asset:stylesheet src="lib/bootstrap-toggle.min.css" />
	<asset:javascript src="lib/bootstrap-toggle.min.js" />
	<asset:stylesheet src="lib/angular-growl.min.css" />
	<asset:stylesheet src="lib/rzslider.css" />
	<asset:stylesheet src="lib/xeditable.min.css" />
	
	<!--<asset:stylesheet src="lib/angular-material-1.1.12.min.css" />-->
	
	
	<asset:stylesheet src="lib/Roboto.css" />
	

	
	
	
	<g:javascript>
		//fill in global values using Grails-provided objects
		//APP.ENV can be used to check client-side for environment of searchable corpora (elastic type concept)
		APP = {
			CONTEXT: "${request.contextPath}",
			ROOT: "${request.scheme}://${request.serverName}:${request.serverPort}${request.contextPath}",
			ENV: "${grailsApplication.config.ENV}"
		};
	</g:javascript>
	 
	<asset:javascript src="lib/system.js" />
	<asset:javascript src="lib/browser.js" />

	<g:layoutHead />
	
	<script>
		//sets up dynamic loading of ES6 modules/classes and loads top level file with imports of other modules
	 	System.config({
	        transpiler: 'babel',
	        //optional: ['es7.classProperties'],
	        defaultJSExtensions: true
	    });
	    //see for timing of this call wrt async loading of system.js and dependencies, https://github.com/jspm/registry/issues/358
	    System.import(APP.CONTEXT + '/assets/angular/app.js')
	    	.then( function() {
	    		angular.element(document).ready(function() {
	    		    angular.bootstrap(document, ['app']);
	    		});
	    		//angular.bootstrap(document.body, ['app'], {strictDi: true});
			});
	</script>
</head>

<body>

	
	
	<nav class="navbar navbar-inverse navbar-fixed-top ">
		<div class="container-fluid">			
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" style="color:white" href="${request.contextPath}"><g:meta name="app.title"/></a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
					<ul bsTooltip class="nav navbar-nav navbar-right"  ng-controller="searchController as menuSc" >
						<!-- configure links in NavCtrl links property -->
						
						<li class="<g:if test="${controllerName=='search' && actionName=='index'}">active</g:if>">
							<a href="${request.contextPath}/search" bsTooltip data-toggle="tooltip" data-placement="bottom" title="{{menuSc.currentSearch.menuIcon.title}}">
								<i class="{{menuSc.currentSearch.menuIcon.class}}"></i>
							</a>
						</li>
						
						<li ng-click="menuSc.currentSearch.navMenuChange()" class="<g:if test="${controllerName=='settings' && actionName=='index'}">active</g:if>">
							<a href="${request.contextPath}/settings" data-toggle="tooltip" data-placement="bottom" title="settings">
								<i class="fa fa-sliders fa-lg" aria-hidden="true"></i>
							</a>
						</li>
						
						<!-- 
						<li ng-controller="helpController as hc" class="<g:if test="${actionName=='help'}">active</g:if>">
							<a data-toggle="tooltip" data-placement="bottom" title="query help">
								<i class="fa fa-question fa-lg" aria-hidden="true"
									ng-click="hc.modalService.queryHelp('lg','helpController')">
								</i>
							</a>
						</li>
						 -->
						 
						<sec:ifNotLoggedIn>
							<li class="<g:if test="${actionName=='auth'}">active</g:if>">
								<a href="${request.contextPath}/search" data-toggle="tooltip" data-placement="bottom" title="sign in">
									<i class="fa fa-sign-in fa-lg" aria-hidden="true"></i>
								</a>
							</li>
						</sec:ifNotLoggedIn>
						<sec:ifLoggedIn>
							<li class="<g:if test="${actionName=='login'}">active</g:if>">
								<a href="${request.contextPath}/logout" data-toggle="tooltip" data-placement="bottom" title="sign out">
									<i class="fa fa-sign-out fa-lg" aria-hidden="true"></i>
								</a>
							</li>
						</sec:ifLoggedIn>
						
						<g:if env="development" test="${actionName==null}">
						<li>
							<a href="${request.contextPath}/logout" data-toggle="tooltip" data-placement="bottom" title="sign out">
								<i class="fa fa-sign-out fa-lg" aria-hidden="true"></i>
							</a>
						</li>
						</g:if>
					</ul>
			</div>
		</div>
	</nav>
	
	<g:if test="${actionName!=null}">
		<g:render template="${actionName}_nav" />
	</g:if>
	
	<script type="text/ng-template" id="queryHelp.template">
		<g:render template="/common/queryHelp" />
	</script>
	
	<g:layoutBody />

	<script>

		$(document).ready( function() {
			$('[data-toggle="tooltip"]').tooltip({
			      animation: true,
			      html: true	
			    });
		});
	</script>
</body>

</html>