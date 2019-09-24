//import * as angular from '../lib/angular.1.7.8.min.js';
import * as angular from '../lib/angular.min.js';

import '../lib/angular-aria-1.5.8.min.js';
import '../lib/angular-animate-1.5.8.min.js';
import '../lib/angular-messages-1.5.8.min.js';
import '../lib/angular-material-1.1.12.min.js';
import '../lib/angular-cookies-1.5.8.min.js';

import '../lib/angular-sanitize.min.js';
import * as spinners from '../lib/angular-growl.min.js';
import '../lib/xeditable.min.js';
import '../lib/angularSlideables.js';
import '../lib/rzslider.js';
import '../lib/ui-bootstrap-tpls-2.5.0.min.js';

import SearchService from '../angular/service/SearchService';
import ExpansionService from '../angular/service/ExpansionService';
import ModalService from '../angular/service/ModalService';
import UserService from '../angular/service/UserService';
import Search from '../angular/service/Search';
import Settings from '../angular/service/Settings';
import SearchController from '../angular/controller/search/SearchController';
import ResultsController from '../angular/controller/search/ResultsController';
import SettingsController from '../angular/controller/settings/SettingsController';
import ExpansionController from '../angular/controller/search/ExpansionController';
import HelpController from '../angular/controller/help/HelpController';


angular.module( 'app', ['angular-growl', 'ngSanitize', 'xeditable', 'angularSlideables', 'rzModule', 'ui.bootstrap', 'ngMaterial', 'ngCookies'] )
	.service('settings', Settings)
    .service('searchService', SearchService)
    .service('currentSearch', Search)
    .service('expansionService', ExpansionService)
    .service('modalService', ModalService)
    .service('userService', UserService)
	.controller('resultsController', ResultsController)
	.controller('searchController', SearchController)
	.controller('settingsController', SettingsController)
	.controller('expansionController', ExpansionController)
	.controller('helpController', HelpController)
	.run(function(editableOptions) {
		editableOptions.theme = 'default';	//bs3
		editableOptions.icon_set = 'font-awesome';
	})
	.config( ['growlProvider', function(growlProvider) {
		growlProvider.globalPosition('top-right');
	}])
	/*.directive('bsTooltip', function(){
	    return {
	        restrict: 'A',
	        link: function(scope, element, attrs) {
	            $(element).hover(function(){
	            	alert(JSON.stringify(attrs,null,'\t'));
	                // on mouseenter
	                $(element).tooltip('show');
	                //attrs.$set('delay', '{"show":500, "hide":1500}' );
	            }, function(){
	                // on mouseleave
	                $(element).tooltip('hide');
	            });
	        }
	    };
	});*/
	//https://stackoverflow.com/questions/30883446/bootstrap-tooltip-not-rendering-with-angular-ng-repeat
	.directive('bsTooltip', function() {
        return function(scope, element, attrs) {

          element.tooltip({
            trigger:"hover",
            placement:"right",
            html:true,
            delay: {show: 750, hide: 2000}
          });
         

        };
      });


