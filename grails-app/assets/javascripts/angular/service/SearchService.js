
class SearchService {
	
	//for some reason service constructors do not need inject annotation?
	constructor( $http, $q, growl ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
		this.searchHistory = undefined;
		this.relatedTerms = undefined;
	}
	
	fetchContexts() {
    	return this.$http.get( APP.ROOT + '/config/authorizedContexts' );
    }

	
	fetchAuthorizedContextByLabel( label ) {
    	return this.$http.post( APP.ROOT + '/config/authorizedContextByLabel/', { "label": label } );
    }
	
	setActiveCorpus( corpus, corpora ) {
    	for ( let c of corpora ) {
    		if ( c.name==corpus.name ) {
    			c.status.active = true;
    		} else {
    			c.status.active = false;
    		}
    	}
    }
	
	fetchHistory() {	
		let me = this;
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

	fetchPreviousQuery( id ) {
		return this.$http.get( APP.ROOT + '/search/recentQuery/' + id );
	}
		
	saveQuery( registrationId ) {
		let me = this;
		this.$http.get( APP.ROOT + '/settings/saveQuery/' + registrationId )
			.then( function(response) {	
				//alert("Query saved")
				me.growl.success( "Query saved", {ttl:1000} );
			})
			.catch( function(e) {
				//alert("problem")
				me.growl.error( "Error - contact support", {ttl:30000} );
			});
	}
	
}

SearchService.$inject = [ '$http', '$q', 'growl' ];

export default SearchService;