import * as angular from '../lib/angular.min.js';
import '../lib/angular-sanitize.min.js';
import * as spinners from '../lib/angular-growl.min.js';
import '../lib/xeditable.min.js';
import '../lib/angularSlideables.js';
import '../lib/rzslider.js';

import SearchService from '../angular/service/SearchService';
import Search from '../angular/service/Search';
import Settings from '../angular/service/Settings';
import SearchController from '../angular/controller/search/SearchController';
import ResultsController from '../angular/controller/search/ResultsController';
import SettingsController from '../angular/controller/settings/SettingsController';


angular.module( 'app', ['angular-growl', 'ngSanitize', 'xeditable', 'angularSlideables', 'rzModule'] )
	.service('settings', Settings)
    .service('searchService', SearchService)
    .service('currentSearch', Search)
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