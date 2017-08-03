//import Search from '../../model/search/Search';

class UIState {

    constructor( uiService, searchService, currentSearch ) {
    	this.uiService = uiService;
    	this.searchService = searchService;
    	this.currentSearch = currentSearch;
    	
    	this.authorizedContexts = undefined;
    	this.currentContext = undefined;
    	
    	this.init();
    }
    
    init() {
		this.fetchContexts();
		this.searchService.fetchHistory( false );
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
    
    executeRecentSearch(registeredSearch) {
    	console.log("recent");
    }

}

UIState.$inject = [ 'uiService', 'searchService', 'currentSearch' ];

export default UIState;