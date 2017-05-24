//import UIService from '../../service/config/UIService';
import Search from '../../model/search/Search';

class ResultsController {
	
	constructor( $scope, currentSearch ) {
		//this.uiState = uiState;
		this.search = currentSearch;
	}
	
}

ResultsController.$inject = [ '$scope', 'currentSearch' ];

export default ResultsController;