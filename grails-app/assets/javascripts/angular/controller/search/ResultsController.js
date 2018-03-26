//import Search from '../../model/search/Search';

class ResultsController {
	
	constructor( currentSearch ) {
		this.search = currentSearch;
	}
	
}

ResultsController.$inject = [ 'currentSearch' ];

export default ResultsController;