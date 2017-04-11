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
	
	<title><g:meta name="admin.title"/></title>
	
	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
		      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
		      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		    <![endif]-->
		    
	<asset:javascript src="lib/jquery.min.js" />
	<!-- <asset:stylesheet src="lib/xeditable.min.css" /> -->
	<asset:stylesheet src="lib/bootstrap.min.css" />
	<asset:stylesheet src="lib/admin.css" />
	<asset:stylesheet src="lib/dashboard.js" />
	<!--  <asset:stylesheet src="lib/sticky-footer.js" />
	<asset:javascript src="lib/angular.min.js" />
	<asset:javascript src="lib/xeditable.min.js" /> -->
	<!--<asset:javascript src="lib/angular.min.js" />  -->
	<asset:javascript src="lib/bootstrap.min.js" />
	<asset:stylesheet src="lib/font-awesome.min.css" />
	<asset:stylesheet src="lib/wfmi-style.css" />
	
	<link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
	<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
	
	<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
	
	<g:javascript>
		APP = {
			context: "${request.contextPath}",
			root: "${request.scheme}://${request.serverName}:${request.serverPort}${request.contextPath}"
		};
	</g:javascript>
	 
	<script src="https://cdnjs.cloudflare.com/ajax/libs/systemjs/0.18.4/system.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/babel-core/5.8.25/browser.js"></script>

	<g:layoutHead />
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
				<a class="navbar-brand" href="${request.contextPath}"><g:meta name="app.title"/></a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
					<!-- <ul class="nav navbar-nav navbar-left" ng-controller="LoginCtrl">
						<li ng-repeat="link in links">
							<a target="_blank" href="{{link.href}}" data-toggle="tooltip" data-placement="bottom" title="Login">{{link.text}} 
							<i class="fa fa-user" aria-hidden="true"></i></a>
						</li>
					</ul> -->
					<ul class="nav navbar-nav navbar-right">
						<!-- configure links in NavCtrl links property -->
						<li class="<g:if test="${actionName=='search'}">active</g:if>">
							<a href="${request.contextPath}/search/search" data-toggle="tooltip" data-placement="bottom" title="search">
								<i class="fa fa-search fa-lg" aria-hidden="true"></i>
							</a>
						</li>
						<li class="<g:if test="${actionName=='saved'}">active</g:if>">
							<a href="${request.contextPath}/admin/saved" data-toggle="tooltip" data-placement="bottom" title="saved searches">
								<i class="fa fa-list fa-lg" aria-hidden="true"></i>
								<i class="fa fa-caret-down" aria-hidden="true"></i>
							</a>
						</li>
						<li class="<g:if test="${actionName=='help'}">active</g:if>">
							<a href="${request.contextPath}/admin/help" data-toggle="tooltip" data-placement="bottom" title="help">
								<i class="fa fa-question fa-lg" aria-hidden="true"></i>
							</a>
						</li>
						<li class="<g:if test="${controllerName=='config' && actionName=='settings'}">active</g:if>">
							<a href="${request.contextPath}/config/settings" data-toggle="tooltip" data-placement="bottom" title="settings">
								<i class="fa fa-cogs fa-lg" aria-hidden="true"></i>
							</a>
						</li>
						<li class="<g:if test="${actionName=='login'}">active</g:if>">
							<a href="${request.contextPath}/admin/login" data-toggle="tooltip" data-placement="bottom" title="sign in">
								<i class="fa fa-sign-in fa-lg" aria-hidden="true"></i>
							</a>
						</li>
					</ul>
			</div>
		</div>
	</nav>
	
	<g:render template="${actionName}_nav" />
	
	<g:layoutBody />

	<script>
		//sets up dynamic loading of ES6 modules/classes and loads top level file with imports of other modules
	 	System.config({
	        transpiler: 'babel',
	        defaultJSExtensions: true
	    });
	    //see for timing of this call wrt async loading of system.js and dependencies, https://github.com/jspm/registry/issues/358
	    System.import(APP.context + '/assets/angular/app.js')
	    	.then( function() {
	    		angular.element(document).ready(function() {
	    		    angular.bootstrap(document, ['app']);
	    		});
	    		//angular.bootstrap(document.body, ['app'], {strictDi: true});
			});
	</script>
	<script>
		$(document).ready(function(){
		    $('[data-toggle="tooltip"]').tooltip({ delay:{show: 500, hide: 100} });
		});
		
		//this set of stmts makes the css work that controls vert scroll under top navvar */
		/* see https://jsfiddle.net/a88n0aet/ and http://stackoverflow.com/questions/31497996/how-to-avoid-that-the-scroll-bar-on-the-right-overlaps-with-the-top-navbar-boot */
		height = $(window).height() - 75;
		$(".main").height(height);
		window.onresize = function(event) {
			var height = $(window).height() - 75;
			$(".main").height(height);
		};
	</script>
	
</body>

</html>