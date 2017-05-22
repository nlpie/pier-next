import Search from '../../model/search/Search';

class SearchService {
	
	//for some reason service constructors do not need inject annotation?
	constructor( $http, $q ) {
		this.$http = $http;
		this.$q = $q;
	}
	
	fetchResultsFromElastic( userInput, searchContext, corpus ) {
		//return the promise and let the client resolve it
		return this.$http.post( APP.ROOT + '/search/elastic/', JSON.stringify(searchContext) );
	}
 
	//based on https://appendto.com/2016/02/working-promises-angularjs-services/ (deferred technique)
    search_() {
    	var me = this;
    	// if results object is not defined then start the new process for fetch it
    	if ( !me.currentSearch.result || me.currentSearch.dirty ) {
    		this.$http.get('http://jsonplaceholder.typicode.com/posts/1')
    			.then(function(response) {
    				// save fetched results to the local variable
    				me.assignResults(response.data);
    			}, function(error) {
    				me.currentSearch.error = error.statusText;
    			});
    	}
    };
    
    /*doSomething() {
    	let deferred = $q.defer();
    	$http.get('/pwet')
    	.success(data => deferred.resolve(data))
    	.error(err => deferred.reject('You failed'));
    	return deferred.promise;
    }*/
	
}

SearchService.$inject = [ '$http', '$q' ];

export default SearchService;