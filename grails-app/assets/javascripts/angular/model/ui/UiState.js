import Pagination from '../../model/ui/Pagination';
//import Search from '../../model/search/Search';
//import Result from '../../model/search/Result';

class UiState {

    constructor( uiService, searchService ) {
    	this.uiService = uiService;
    	//this.searchService = searchService;
    	this.userInput = "heart OR valve";
    	this.authorizedContexts = undefined;
    	this.previousSearches = undefined;
    	this.currentContext = undefined;
    	//this.currentSearch = undefined;
    	this.pagination = undefined;
		this.init();
    }
    
    init() {
		this.fetchContexts();
		//this.currentContext = { "icsRequest": "Static Context" };
		this.pagination = new Pagination();
		//this.currentSearch = new Search(this.searchService, this.userInput, this.currentContext, this.pagination.notesPerPage, this.pagination.offset);
	}
    
    fetchContexts() {
    	var me = this;	//use me instead of this, b/c 'this' inside of promise's success function is not the same as 'this' for an instance of the client class 	
    	this.uiService.authorizedContexts()
	    	.then( function(response) {
	    		me.authorizedContexts = response.data;
	    		me.currentContext = me.authorizedContexts[0];
	    	});
    }
    
    changeContext(index) {
    	//	angular.copy(c,this.currentContext);	
    	this.currentContext = this.authorizedContexts[index];	
	}
    
    executeSearch() {
    	var me = this;
    	this.searchService.fetchResultsFromElastic( this.userInput, this.currentContext )
    		.then( function(response) {
    			
    		});
    }
    
    assignResults(data) {
    	//alert("populate results");
    	this.currentSearch.result = new Result();
    	this.currentSearch.result.notes = data;	
    }

}

UiState.$inject = [ 'uiService', 'searchService' ];
	

export default UiState;