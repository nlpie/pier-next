//only need to import if you are new-ing up an instance, or injecting and using a class in the context of this code
import User from '../model/User';

class UserController {
	
	constructor( $scope, userService, searchService) {
		'ngInject';
		this.someText = "some-text-in-controller";
		this.userService = userService;
		this.searchService = searchService;
		this.user = new User("rmcewan-defined-in-constructor");
		//$scope.vm = this;
		//$scope.uc = this;
	}
	
	date() {
		return new Date().toString();
	}
	
	getUser() {
		//alert("getUser");
		return new User("rmcewan-method").name;
		//return u.name;
	}
}

// injection array contains names of "objects" 
//as defined in the angular.module() definition for the app (userService)
//and angular.min.js ($http, $scope, etc)
//bootstrapped singleton services are available by name
UserController.$inject = [ '$scope', 'userService', 'searchService' ];

export default UserController;