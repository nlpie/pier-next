
class UIService {

    constructor( $http, $q ) {
    	this.$http = $http;
		this.$q = $q;
    }
    
    authorizedContexts() {
		//return the promise and let the client resolve it
    	//TODO add config obj with header specifying how long browser should cache the returned object
		return this.$http.get( APP.ROOT + '/config/authorizedContexts' );
	}
    
    fetchAuthorizedContextByLabel( label ) {
    	return this.$http.post( APP.ROOT + '/config/authorizedContextByLabel/', { "label": label } );
    }
    
    fetchPreferences() {
    	return this.$http.get( APP.ROOT + '/settings/preferences/', { "noop": true } );
    }
    
    removeFilters( corpus ) {
    	Object.keys( corpus.metadata.aggregations ).map( function(ontol,index) {
    		let ontology = corpus.metadata.aggregations[ontol];
    		Object.keys( ontology ).map( function(agg,idx) {
    			let aggregation = ontology[agg];
    			if ( !( JSON.stringify(aggregation.filters) === JSON.stringify({}) ) ) {
	    			aggregation.filters = {};
    			}
        	})
    	});
    	corpus.status.userSelectedFilters = false;
    	corpus.status.showBan = false;
    	corpus.status.dirty = true;
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

}

UIService.$inject = [ '$http', '$q' ];

export default UIService;