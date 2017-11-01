//import Search from '../../model/search/Search';

class UIState {

    constructor( uiService, searchService, currentSearch ) {
    	this.uiService = uiService;
    	this.searchService = searchService;
    	this.currentSearch = currentSearch;
    	
    	this.authorizedContexts = undefined;
    	//this.currentContext = undefined;
    	
    	this.init();
    }
    
    init() {
		this.fetchContexts();
		this.searchService.fetchHistory();
	}
    
    fetchContexts() {
    	var me = this;	//use me instead of this, b/c 'this' inside of promise's success function is not the same as 'this' for an instance of the client class 	
    	this.uiService.authorizedContexts()
	    	.then( function(response) {
	    		me.authorizedContexts = response.data;
	    		me.currentSearch.setContext(me.authorizedContexts[0]);	//set to first in list
	    	});
    }
    
    changeContext(context) {
    	//alert(JSON.stringify(context));
    	this.currentSearch.setContext(context);	
	}
    /*lookupAuthorizedContextByLabel (label) {
    	if ( !label ) { alert("ERROR"); return; }
    	var context = undefined;
    	for ( const ac of this.authorizedContexts ) {
    		if ( ac.label==label ) {
    			context = ac;
    			break;
    		}
    	}
    	return ac;
    }*/
}

UIState.$inject = [ 'uiService', 'searchService', 'currentSearch' ];

export default UIState;