
class SearchController {
	
	constructor( $scope, searchService, currentSearch ) {
		this.searchService = searchService;
		this.currentSearch = currentSearch;
	}
	
	//convenience method for easily proofing state of objects
	show( obj ){
		alert(JSON.stringify(obj,null,'\t'));
	}
	
}

SearchController.$inject = [ '$scope', 'searchService', 'currentSearch' ];

export default SearchController;