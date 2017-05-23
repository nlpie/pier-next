import * as angular from '../lib/angular.min.js';

import SearchService from '../angular/service/search/SearchService';
import Search from '../angular/model/search/Search';
import SearchController from '../angular/controller/search/SearchController';
//import UserService from '../angular/service/UserService';
//import UserController from '../angular/controller/UserController';
import UIService from '../angular/service/config/UIService';
import UIState from '../angular/model/ui/UIState';


angular
    .module('app', [])
    //.service('userService', UserService)
    .service('searchService', SearchService)
    .service('currentSearch', Search)
    .service('uiService', UIService)
    .service('uiState', UIState)
    /*.service('injectOn', ($injector) => {
		  return (thisArg) => {
		    if(!thisArg.constructor) {
		      throw new Error('Constructor method not found.');
		    }
		   $injector.annotate(thisArg.constructor).map(name => {
		      if(name !== 'injectOn' && name !== '$scope') {
		        thisArg[name] = $injector.get(name);
		      }
		    });
		  };
		})*/
	//.controller('userController', UserController)
	.controller('searchController', SearchController);