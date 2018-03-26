
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

}

UIService.$inject = [ '$http', '$q' ];

export default UIService;