
class SearchController {
	
	constructor( $scope, searchService, currentSearch, modalService ) {
		this.searchService = searchService;
		this.modalService = modalService;
		this.currentSearch = currentSearch;
	}
	
	//convenience method for easily proofing state of objects
	show( obj ){
		alert(JSON.stringify(obj,null,'\t'));
	}
	
}

SearchController.$inject = [ '$scope', 'searchService', 'currentSearch', 'modalService' ];

export default SearchController;