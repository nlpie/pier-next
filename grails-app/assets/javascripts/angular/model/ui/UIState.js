import Search from '../../model/search/Search';

class UIState {

    constructor( uiService, currentSearch ) {
    	this.uiService = uiService;
    	this.currentSearch = currentSearch;
    	
    	this.authorizedContexts = undefined;
    	this.currentContext = undefined;
    	
    	this.previousSearches = undefined;
		
    	this.init();
    }
    
    init() {
		this.fetchContexts();
		//this.currentSearch = new Search(this.currentContext);
		//= new Search(this.searchService, this.userInput, this.currentContext, this.pagination.notesPerPage, this.pagination.offset);
	}
    
    fetchContexts() {
    	var me = this;	//use me instead of this, b/c 'this' inside of promise's success function is not the same as 'this' for an instance of the client class 	
    	this.uiService.authorizedContexts()
	    	.then( function(response) {
	    		me.authorizedContexts = response.data;
	    		me.currentSearch.setContext(me.authorizedContexts[0]);	//set to first in list
	    		//me.currentContext = me.authorizedContexts[0];
	    		//inject $http, searchService into Search.js, then search (as currentSearch) into uistate and set currentContext on uistate.currentSearch
	    	});
    }
    
    changeContext(index) {
    	//	angular.copy(c,this.currentContext);	
    	this.currentSearch.setContext(this.authorizedContexts[index]);	
	}

}

UIState.$inject = [ 'uiService', 'currentSearch' ];

export default UIState;