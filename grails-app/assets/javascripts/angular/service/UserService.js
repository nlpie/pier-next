class UserService {
	
	constructor( $http, growl ) {
		//alert("constructing UserService");
		this.$http = $http;
		this.growl = growl;
	}
	
	isLoggedIn() {
		var xhr = new XMLHttpRequest();
		xhr.open('GET', APP.ROOT + '/authCheck', false);  // `false` makes the request synchronous
		//xhr.responseType = 'json';
		xhr.send(null);
		let json = JSON.parse( xhr.responseText );
		if ( xhr.status === 200 ) {
			return true;
		} else if ( xhr.status === 419 ) {			
			this.growl.error( json.message + " - please <a href='" + APP.ROOT + "/search'>reauthenticate</a>", {ttl:5000, referenceId:"docs"} );
			return false;
		} else {
			this.growl.error( json.message + " - please contact support", {ttl:5000, referenceId:"docs"} );
			return false;
		}
	}
	
	loggedIn() {
    	let me = this;
    	this.userService.isLoggedIn()
    		.then ( function( successResponse ) {
    			//response code 200
    			me.growl.success( successResponse.data.message, {ttl:1000, referenceId:"docs"} );
    			console.log(successResponse.data);
    			return me;
			})
			.catch( function( errorRepsonse ) {
				console.log(errorResponse.data);
				return false;
			});
    }
}

UserService.$inject = [ '$http', 'growl' ];

export default UserService;