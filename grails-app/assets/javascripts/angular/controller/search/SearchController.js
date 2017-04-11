import SearchService from '../../service/search/SearchService';
import UiState from '../../service/config/UiState';

class SearchController {
	
	constructor( $scope, searchService, uiState ) {
		'ngInject';
		this.searchService = searchService;
		this.uiState = uiState;
		//alert("SearchController constructor");
	}
	
}

SearchController.$inject = [ '$scope', 'searchService', 'uiState' ];

export default SearchController;