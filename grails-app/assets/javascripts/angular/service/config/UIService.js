//import Pagination from '../../model/ui/Pagination';
//import Search from '../../model/search/Search';
//import Result from '../../model/search/Result';

class UIService {

    constructor( $http, $q ) {
    	this.$http = $http;
		this.$q = $q;
    }
    
    authorizedContexts() {
		//return the promise and let the client resolve it
		return this.$http.get( APP.ROOT + '/config/authorizedContexts' );
	}

}

UIService.$inject = [ '$http', '$q' ];

export default UIService;