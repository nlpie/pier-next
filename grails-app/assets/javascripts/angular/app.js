import * as angular from '../lib/angular.min.js';
import '../lib/angular-sanitize.min.js';
import * as spinners from '../lib/angular-growl.min.js';
import '../lib/xeditable.min.js';

import SearchService from '../angular/service/search/SearchService';
import Search from '../angular/model/search/Search';
import Settings from '../angular/service/Settings';
import SearchController from '../angular/controller/search/SearchController';
import ResultsController from '../angular/controller/search/ResultsController';
import SettingsController from '../angular/controller/settings/SettingsController';

//import UserService from '../angular/service/UserService';
//import UserController from '../angular/controller/UserController';
import UIService from '../angular/service/config/UIService';
import UIState from '../angular/model/ui/UIState';


angular.module( 'app', ['angular-growl', 'ngSanitize', 'xeditable'] )
    .service('settings', Settings)
    .service('searchService', SearchService)
    .service('currentSearch', Search)
    .service('uiService', UIService)
    .service('uiState', UIState)
	.controller('resultsController', ResultsController)
	.controller('searchController', SearchController)
	.controller('settingsController', SettingsController)
	.run(function(editableOptions) {
		editableOptions.theme = 'default';	//bs3
		editableOptions.icon_set = 'font-awesome';
	})
	.config(['growlProvider', function(growlProvider) {
		growlProvider.globalPosition('top-right');
	}]);