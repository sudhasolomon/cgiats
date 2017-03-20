;(function(angular){
	"use strict";
	
	angular.module("app",['jcs-autoValidate'])
    .controller("loginController",function($scope,$http,$window,$rootScope,$location){
		
		$scope.onload = function(){
			var log = $location.search().flag;
			//alert(log);
			$scope.logoutMsg = $location.search().flag;
			//alert(JSON.stringify($scope.logoutMsg));
			if($scope.logoutMsg){
				$scope.recovermsg = 'Password has been changed successfully, Login with new credentials';
				$("#forgterror").hide();
				$("#passwordfields").hide();
				$("#recovermsg").show();
			}
			$scope.jobOrders = "My Job Orders Page";
			
			$scope.username = null;
			$scope.password = null;
		}
		
		
		
		$scope.loginsbt = function()
		{
			
			var obj = {
					
					"userId" : 	$scope.username,
					"password" : $scope.password
			};
			
			
//			var response = $http.post('LoginController/loginAction',obj);
//			response.success(function(data) {
//				location.href = 'index.html';
//			});
//			response.error(function(data){
//				alert("error data  "+ data);
//				location.href = 'login.html';
//			});
			
			
//			var data = "username="+$scope.username+"&password="+$scope.password+"&submit=Login";
//
	        $http({
	            method: 'POST',
	            url: 'LoginController/loginAction',
	            data: obj
	        })  
	        .success(function(data, status){

	        	if(data.userRole.startsWith('IN_')){
	        		location.href = '/cgiats/index.html#/india_joborders/my_indiajobOrder';
	        	}else if(data.userRole === 'ATS_Executive'){
	        		location.href = '/cgiats/index.html#/candidates/missingData';
	        	}else{
	        			location.href = '/cgiats/index.html#/dashboard';

	        	}
	        })
	        .error(function(data, status){
	        	/*location.href = 'login.html';*/
	        	$(".invaliduser").show();
	        })
						
			
		}
		
		
		/*function forgotpwdsbt()
		{
			
			
			$("#passwordfields").hide();
			$("#recovermsg").show();
		}*/
		
		$scope.checkfields = function(){
			 
			//alert($scope.fusername);
			if($scope.fusername == undefined && $scope.email == undefined){
				//alert("in undefined");
				$scope.errormsg = "Please provide User Name or Email Address";
				$("#forgterror").show();
			}
			else{
				//alert("in esle");
				$("#subtn").prop('disabled', true)
				$(".underlay").show();
				var forgotDetails = {
						"userId" : $scope.fusername,
						"email" : $scope.email
				}
				//alert(JSON.stringify(forgotDetails));
			var response = $http.post("LoginController/passwordRecovery",forgotDetails);
			response.success(function(data, status, headers, config){
				if(data.statusCode == 200){
					$scope.recovermsg = data.statusMessage;
					$("#forgterror").hide();
					$("#passwordfields").hide();
					$("#recovermsg").show();
					$(".underlay").hide();
					}
				if(data.statusCode == 500){
					//alert("in");
					$scope.errormsg = data.statusMessage;
					$("#forgterror").show();
					$(".underlay").hide();
					}
					
				//alert("success data "+data.statusCode+ "message "+data.statusMessage);
				
			});
			response.error(function(data, status, headers, config){
				$(".underlay").hide();
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				alert("error data "+data.statusCode+ "message "+data.statusMessage);
  			  }
  		  });
			}
			
			
			
			
/*			var forgotDetails = {
					"userId" : $scope.fusername,
					"email" : $scope.email
			}
			var response = $http.post("LoginController/passwordRecovery",forgotDetails);
			response.success(function(data, status, headers, config){
				if(data.statusCode == 200){
					$scope.recovermsg = data.statusMessage;
					$("#forgterror").hide();
					$("#recovermsg").show();
					}
				if(data.statusCode == 500){
					alert("in");
					$scope.forgterror = data.statusMessage;
					$("#forgterror").show();
					}
					
				alert("success data "+data.statusCode+ "message "+data.statusMessage);
			});
			response.error(function(data, status, headers, config){
				alert("error data "+data.statusCode+ "message "+data.statusMessage);
			});
			
			
			if(fuser == undefined || fuser == "")
				{
					var femail = $scope.email;
					if(femail == undefined || femail == "")
						{
						$("#forgterror").show();
						return false
						
						
						}
					else
						{
						$("#forgterror").hide();
						forgotpwdsbt();
						}
				}
			else
				{
				$("#forgterror").hide();
				forgotpwdsbt();
				}*/
		}
		
		
		
	});
	
})(angular);