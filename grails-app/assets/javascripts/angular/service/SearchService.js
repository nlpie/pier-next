import AuthorizedContext from '../model/ui/AuthorizedContext';

class SearchService {
	
	//for some reason service constructors do not need inject annotation?
	constructor( $http, $q, growl, $timeout, userService ) {
		this.$http = $http;
		this.$q = $q;
		this.growl = growl;
		this.$timeout = $timeout;
		this.userService = userService;
		
		this.searchHistory = undefined;
		this.lastQuery = undefined;
		this.returnQuery = undefined;
		this.savedQueriesByContext = undefined;
		this.savedQueriesByUserExcludingContext = undefined;
		this.relatedTerms = undefined;
		this.exportMsg = "now";
	}
	
	fetchContexts() {
    	return this.$http.get( APP.ROOT + '/config/authorizedContexts' );
    }

	
	fetchAuthorizedContextByLabel( label ) {
//alert("auth context by label");
    	return this.$http.post( APP.ROOT + '/config/authorizedContextByLabel/', { "label": label } );
    }
	
	fetchAuthorizedContextByQueryId( queryId ) {
    	return this.$http.post( APP.ROOT + '/config/authorizedContextByQueryId/', { "queryId": queryId } );
    }
	
	fetchHistory() {
//alert("fetch history");
		let me = this;
		return this.$http.post( APP.ROOT + '/search/historySummary', { "excludeMostRecent":false } )
		.then( function(response) {
			me.searchHistory = response.data;
			me.setLastQuery();
			//me.lastQuery = this.searchHistory[0].query;
		});
	}
	
	setLastQuery() {
		//alert(JSON.stringify(this.searchHistory));
		if ( this.searchHistory.length>0 ) {
			this.lastQuery = this.searchHistory[0].query;
			//this.lastQuery = response.data[0].query;
		}
	}	
	
	/*fetchHistoryExcludingMostRecent() {
    	var me = this;	
    	return this.$http.post( APP.ROOT + '/search/historySummary', { "excludeMostRecent":true } )
	    	.then( function(response) {
	    		me.searchHistory = response.data;
	    	});
    }*/
	
	fetchSavedQueries( authorizedContext ) {
		if ( authorizedContext ) {
			let me = this;
			this.$http.post( APP.ROOT + '/search/savedQueriesByContext', {"authorizedContext": authorizedContext} )
			.then( function(response) {
				me.savedQueriesByContext = response.data;
			});
			this.$http.post( APP.ROOT + '/search/savedQueriesByUserExcludingContext', {"authorizedContext": authorizedContext} )
			.then( function(response) {
				me.savedQueriesByUserExcludingContext = response.data;
			});
		}
	}

	fetchPreviousQueries( id ) {
//alert( "fetch prev queries");
		return this.$http.get( APP.ROOT + '/search/recentQuery/' + id );
	}
		
	saveQuery( searchInstance ) {
		if ( !this.userService.isLoggedIn() ) return;
		let me = this;
		this.$http.get( APP.ROOT + '/settings/saveQuery/' + searchInstance.uuid )
			.then( function(response) {	
				me.fetchSavedQueries( searchInstance.authorizedContext );
				me.growl.success( "Query saved", {ttl:1000} );
			})
			.catch( function(e) {
				me.growl.error( "Error - contact support", {ttl:3000} );
			});
	}
	
	exportSearch( currentSearch ) {
		let me = this;
		currentSearch.status.exporting = true;
		//let gr = this.exporting();
		this.$http.get( APP.ROOT + '/search/export/' + currentSearch )
			.then( function(response) {	
				currentSearch.status.exporting = false;
				me.$timeout(function() { 
					gr.destroy();
					me.exported();
				}, 5000 );
				//return data				
			})
			.catch( function(e) {
				me.growl.error( "Error - " + JSON.stringify(e), {ttl:15000} );
				currentSearch.status.exporting = false;
			});
	}
	
	exporting() {
		let growl = this.growl.info( "Exporting...<i class=\"fa fa-spinner fa-spin\"></i>", {ttl:-1, disableCloseButton: true, referenceId:"export"} );
		return growl;
	}
	
	exported( currentSearch, timeout ) {
		this.growl.success( "done", {ttl:5000, referenceId:"export"} );
	}
	
}

SearchService.$inject = [ '$http', '$q', 'growl', '$timeout', 'userService' ];

export default SearchService;