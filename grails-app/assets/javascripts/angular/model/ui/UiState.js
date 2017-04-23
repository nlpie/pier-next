import Pagination from '../../model/ui/Pagination';
import Search from '../../model/search/Search';
import Result from '../../model/search/Result';

class UiState {

    constructor( uiService, searchService ) {
    	this.uiService = uiService;
    	this.searchService = searchService;
    	this.userInput = "heart OR valve";
    	this.authorizedContexts = undefined;
    	this.previousSearches = undefined;
    	this.currentContext = undefined;
    	this.currentSearch = undefined;
    	this.pagination = undefined;
		this.init();
    }
    
    init() {
		this.fetchContexts();
		this.currentContext = { "icsRequest": "Static Context" };
		this.pagination = new Pagination();
		this.currentSearch = new Search(this.searchService, this.userInput, this.currentContext, this.pagination.notesPerPage, this.pagination.offset);
	}
    
    fetchContexts() {
    	var me = this;	//use me instead of this, b/c 'this' inside of promise's success function is not the same as 'this' for an instance of the client class 	
    	this.uiService.authorizedRequests().then( function(response) {
    		me.authorizedContexts = response.data;
    		me.currentContext = me.authorizedContexts[0];
    	});
    }
    
    changeContext(c) {
    	angular.copy(c,this.currentContext);	
	}
    
    /*static get $inject() {
		return [ '$http', '$q', 'uiService' ];
	}*/

}

UiState.$inject = [ 'uiService', 'searchService' ];
	

export default UiState;