import SearchService from '../../service/search/SearchService';
import UIService from '../../service/config/UIService';
import UIState from '../../model/ui/UIState';

class SearchController {
	
	constructor( $scope, searchService, uiState, uiService ) {
		//'ngInject';
		this.searchService = searchService;
		this.uiService = uiService;
		this.uiState = uiState;
		//alert("SearchController constructor");
	}
	
}

SearchController.$inject = [ '$scope', 'searchService', 'uiState', 'uiService' ];

export default SearchController;