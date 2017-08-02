import * as angular from '../lib/angular.min.js';
import * as spinners from '../lib/angular-growl.min.js';

import SearchService from '../angular/service/search/SearchService';
import Search from '../angular/model/search/Search';
import SearchController from '../angular/controller/search/SearchController';
import ResultsController from '../angular/controller/search/ResultsController';
//import AngularPocController from '../angular/controller/AngularPocController';
//import UserService from '../angular/service/UserService';
//import UserController from '../angular/controller/UserController';
import UIService from '../angular/service/config/UIService';
import UIState from '../angular/model/ui/UIState';


angular.module( 'app', ['angular-growl'] )
    //.service('userService', UserService)
    .service('searchService', SearchService)
    .service('currentSearch', Search)
    .service('uiService', UIService)
    .service('uiState', UIState)
	.controller('resultsController', ResultsController)
	.controller('searchController', SearchController);