class UserService {
	
	constructor($http) {
		//alert("constructing UserService");
		this.$http = $http;
	}
	
	getData () {
		return "userService.data....";
		// return this.$http({method: 'GET', url: './api' });
	}
}

UserService.$inject = ['$http'];

export default UserService;