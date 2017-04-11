import * as angular from '../lib/angular.min.js';
import SearchService from '../angular/service/search/SearchService';
import SearchController from '../angular/controller/search/SearchController';
import UserService from '../angular/service/UserService';
import UserController from '../angular/controller/UserController';
import UiState from '../angular/service/config/UiState';

angular
    .module('app', [])
    .service('uiState', UiState)
    .service('userService', UserService)
    .service('searchService', SearchService)
	.controller('userController', UserController)
	.controller('searchController', SearchController);