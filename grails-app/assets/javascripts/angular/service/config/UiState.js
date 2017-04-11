import Pagination from '../../model/ui/Pagination';
import Search from '../../model/search/Search';

class UiState {

    constructor( $http, $q ) {
    	//non-query settings
    	this.authorizedContexts = undefined;
    	this.previousSearches = undefined;
    	this.currentContext = undefined;
    	this.pagination = undefined;
    	
    	this.$http = $http;
		this.$q = $q;
		this.init();
    }
    
    init() {
		this.populateAuthorizedRequests();
		this.currentContext = { "icsRequest": "Static Context" };
		this.currentSearch = new Search(this.currentContext);
		this.pagination = new Pagination();
	}
    
    populateAuthorizedRequests() {
		console.log("requests");
		var me = this;
		this.$http.get( APP.root + '/config/requests' )
			.then( function(response) {
				me.authorizedContexts = response.data;
			});
	}
    
    changeContext(c) {
    	angular.copy(c,this.currentContext);	
		//this.currentContext = c.icsRequest;
	}

}

UiState.$inject = [ '$http', '$q' ];

export default UiState;