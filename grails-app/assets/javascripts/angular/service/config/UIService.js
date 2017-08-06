
class UIService {

    constructor( $http, $q ) {
    	this.$http = $http;
		this.$q = $q;
    }
    
    authorizedContexts() {
		//return the promise and let the client resolve it
		return this.$http.get( APP.ROOT + '/config/authorizedContexts' );
	}
    
    fetchAuthorizedContextByLabel( label ) {
    	return this.$http.post( APP.ROOT + '/config/authorizedContextByLabel/', { "label": label } );
    }

}

UIService.$inject = [ '$http', '$q' ];

export default UIService;