import Search from '../../model/search/Search';

class SearchService {
	
	//for some reason service constructors do not need inject annotation?
	constructor( $http, $q ) {
		//'ngInject';
		this.$http = $http;
		this.$q = $q;
		this.searchHistory = undefined;
	}
	
	fetchResultsFromElastic( corpus, userInput, contextFilter ) {
		//return the promise and let the client resolve it
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify(contextFilter) );
	}
	
	fetchHistory() {
    	var me = this;	
    	return this.$http.post( APP.ROOT + '/search/historySummary', { "excludeMostRecent":false } )
	    	.then( function(response) {
	    		me.searchHistory = response.data;
	    	});
    }
	
	fetchHistoryExcludingMostRecent() {
    	var me = this;	
    	return this.$http.post( APP.ROOT + '/search/historySummary', { "excludeMostRecent":true } )
	    	.then( function(response) {
	    		me.searchHistory = response.data;
	    	});
    }
	
	fetchRegisteredSearch( id ) {
		return this.$http.get( APP.ROOT + '/search/registeredSearch/' + id );
	}
	
}

SearchService.$inject = [ '$http', '$q' ];

export default SearchService;