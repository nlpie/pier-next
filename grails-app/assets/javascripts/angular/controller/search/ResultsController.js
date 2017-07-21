import Search from '../../model/search/Search';

class ResultsController {
	
	constructor( $scope, currentSearch, growl ) {
		this.search = currentSearch;
	}
	
}

ResultsController.$inject = [ '$scope', 'currentSearch' ];

export default ResultsController;