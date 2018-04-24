import Search from '../../model/search/Search';

class SearchService {
	
	//for some reason service constructors do not need inject annotation?
	constructor( $http, $q, growl ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
		this.searchHistory = undefined;
	}
	
	/*fetchResultsFromElastic( corpus, userInput, contextFilter ) {
		//return the promise and let the client resolve it
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify(contextFilter) );
	}*/
	
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
	
	/*fetchRegisteredSearch( id ) {
		return this.$http.get( APP.ROOT + '/search/registeredSearch/' + id );
	}*/
	
	fetchPreviousQuery( id ) {
		return this.$http.get( APP.ROOT + '/search/recentQuery/' + id );
	}
	
	saveQuery( registrationId ) {
		let me = this;
		this.$http.get( APP.ROOT + '/settings/saveQuery/' + registrationId )
			.then( function(response) {	
				alert("Query saved")
				me.growl.success( "Query saved", {ttl:1000} );
			})
			.catch( function(e) {
				alert("problem")
				me.growl.error( e.toString(), {ttl:3000} );
			});
	}
	
}

SearchService.$inject = [ '$http', '$q', 'growl' ];

export default SearchService;