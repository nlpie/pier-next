import * as angular from '../lib/angular.min.js';
import '../lib/angular-sanitize.min.js';
import * as spinners from '../lib/angular-growl.min.js';
import '../lib/xeditable.min.js';
import '../lib/angularSlideables.js';
import '../lib/rzslider.js';
import '../lib/ui-bootstrap-tpls-2.5.0.min.js';

import SearchService from '../angular/service/SearchService';
import ExpansionService from '../angular/service/ExpansionService';
import ModalService from '../angular/service/ModalService';
import Search from '../angular/service/Search';
import Settings from '../angular/service/Settings';
import SearchController from '../angular/controller/search/SearchController';
import ResultsController from '../angular/controller/search/ResultsController';
import SettingsController from '../angular/controller/settings/SettingsController';
import ExpansionController from '../angular/controller/search/ExpansionController';
import HelpController from '../angular/controller/help/HelpController';


angular.module( 'app', ['angular-growl', 'ngSanitize', 'xeditable', 'angularSlideables', 'rzModule', 'ui.bootstrap'] )
	.service('settings', Settings)
    .service('searchService', SearchService)
    .service('currentSearch', Search)
    .service('expansionService', ExpansionService)
    .service('modalService', ModalService)
	.controller('resultsController', ResultsController)
	.controller('searchController', SearchController)
	.controller('settingsController', SettingsController)
	.controller('expansionController', ExpansionController)
	.controller('helpController', HelpController)
	.run(function(editableOptions) {
		editableOptions.theme = 'default';	//bs3
		editableOptions.icon_set = 'font-awesome';
	})
	.config(['growlProvider', function(growlProvider) {
		growlProvider.globalPosition('top-right');
	}]);