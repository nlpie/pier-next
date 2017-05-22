import Search from '../../model/search/Search';

class UIState {

    constructor( uiService ) {
    	this.uiService = uiService;

    	
    	this.authorizedContexts = undefined;
    	this.currentContext = undefined;
    	
    	this.previousSearches = undefined;
		
    	this.init();
    }
    
    init() {
		this.fetchContexts();
		this.currentSearch = new Search();
		//= new Search(this.searchService, this.userInput, this.currentContext, this.pagination.notesPerPage, this.pagination.offset);
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
    
    search() {
    	alert (this.currentContext.toString());
    	this.currentSearch = new Search(this.currentContext);
    }

}

UIState.$inject = [ 'uiService' ];

export default UIState;