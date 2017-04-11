var admin = angular.module("admin", ["xeditable"]);

//https://tylermcginnis.com/angularjs-factory-vs-service-vs-provider-5f426cfe6b8c#.3okucm2xg (background info)
//http://blog.thoughtram.io/angular/2015/07/07/service-vs-factory-once-and-for-all.html (background info)
//https://www.codementor.io/angularjs/tutorial/keeping-angular-service-list-data-sync-controllers (this is what the service template is based on)
admin.service('adminDataService', function ($http) {
	var me = this;
	this.adminData = { "groups": [{"name":"fixed"}] };
	
	this.fetchAdminData = function() {
		$http.get(grailsApp.contextPath+'/admin/groupsAndRoles')
		.then( function(response) { 
			angular.copy(response.data, me.adminData); 
		});
	};
});

admin.run(function(editableOptions) {
	editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
});

admin.controller('NavCtrl', function($scope) {
	$scope.links = [ 
	                { href:"saved", text:"Saved Queries"},
	                { href:"settings", text:"Settings"},
	                { href:"tutorial", text:"Tutorial"}
	              ];
});

admin.controller('LoginCtrl', function($scope) {
	$scope.links = [ 
	                { href:"loginPlaceholder", text:"Log In"}
	              ];
});

admin.controller('DslCtrl', function($scope, $http, adminDataService) {
	$scope.direct = "initval";
	$scope.indirect = "";
	$scope.update = function() {
		$scope.indirect="updated";
	};
	$scope.dsl = {
		command: null,
		activeNode: null,
		user: {
			add: {
				template: 'user-create  <username>  <email>',
				message: null
			},
			del: {
				template: 'user-delete  <username>',
				message: null
			},
			disable: {
				template: 'user-disable  <username>',
				message: null
			},
			newguideeditor: {
				template: 'newuser-guideeditor  <username> <email> <group>',
				message: null
			}
		},
		role: {
			add: {
				template: 'role-create  <role name>',
				message: null
			},
			del: {
				template: 'role-delete  <role name>',
				message: null
			},
			addto: {
				template: 'role-add  <username>  <role name>',
				message: null
			},
			removefrom: {
				template: 'role-remove  <username>  <role name | ALL>',
				message: null
			}
		},
		group: {
			add: {
				template: 'group-create  <group name>',
				message: null
			},
			del: {
				template: 'group-delete  <group name>',
				message: null
			},
			addto: {
				template: 'group-add  <username>  <group name> ',
				message: null
			},
			removefrom: {
				template: 'group-remove  <username>  <group name | ALL>',
				message: null
			}
		},
		clearMessages: function() {
			var nodes = [this.user,this.role,this.group];
			for (var node in nodes) {
				//alert (nodes[key]);
				for (var prop in nodes[node]) {
					//alert(prop);
					for (var key in nodes[node][prop]) {
						//alert(key + " -> " + nodes[node][prop][key]);
						if (key='message') nodes[node][prop].message = null;
					}
				}
			}
		},
		execute: function(cmd,node) {
			//this.command = cmd;
			this.activeNode = node;
			this.clearMessages();
			//alert("node: " + this.activeNode.template);
			var response = $http.post(grailsApp.contextPath+'/admin/dsl', {command:cmd})
				.then(function successCallback(response) {
					node.message = response.data.message;
					adminDataService.fetchAdminData();
					// this callback will be called asynchronously when the response is available
					return false;	//command.template is not updated - this is what we want
				}, function errorCallback(response) {
					// called asynchronously if an error occurs or server returns response with an error status.
					return [response.statusText, '-', response.data.message].join(" ");	//returning string puts response in error message
				});
			return response;
			//return true; 	//command.template is updated - not what we want
		}
	};
	
	adminDataService.fetchAdminData();
	$scope.adminData = adminDataService.adminData;
	
	
});