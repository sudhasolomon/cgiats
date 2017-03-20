;(function(angular){

	
	"Use Strict";
	
	angular.module("appConfigModule",[])
	.controller('appConfigController', function($scope, $http){
		$scope.test = "config file test";
		
		
		$scope.onload = function(){
			var response = $http.post("config/getAppConfig");
			response.success(function(data, status, headers, config){
				$scope.config = data;
			});
			response.error(function(data, status, headers, config){
				if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				$state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
			});
		}
		
		$scope.saveConfig = function(config){
			var response = $http.post("config/saveConfig", config);
			response.success(function(data, status, headers, config){
				if(data.statusCode == 200){
					$.growl.success({title : "success !",message : data.statusMessage});
				}
			});
			response.error(function(data, status, headers, config){
				if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				$state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
			});
		}
		
		
		$scope.resetDefault = function(){
			var response = $http.post("config/resetToDefaultConfig");
			response.success(function(data, status, headers, config){
//				alert(JSON.stringify(data));
				$scope.config = data;
				$.growl.success({title : "success !",message : "Configuration has reset to default"});
				 
			});
			response.error(function(data, status, headers, config){
				if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				$state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
			});
		}
		
		$scope.cancelConfig = function(){
			location.href = "#/dashboard";
		}
		
		
	});
})(angular);
