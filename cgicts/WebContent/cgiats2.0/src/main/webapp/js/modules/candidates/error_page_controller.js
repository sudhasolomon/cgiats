;
(function(angular) {

	"use strict";

	angular
			.module(
					'ErrorPage',
					[ 'ngMaterial', 'ngMessages', 'ui.bootstrap',
							'angular-highlight', 'jcs-autoValidate' ])
	.controller("errorPageController", function($scope, $stateParams, $http){
		
					$scope.status = $stateParams.statusvalue;
					if($scope.status == '500'){
						$scope.msg = "Status 500 Internal Server Error.";
					}else if ($scope.status == '201') {
						$scope.msg ="Status 201 No Content Found.";
					}else {
						$scope.msg = "Error Occurred";
					}
					
					$scope.time = new Date();
					
				$scope.goBack = function(){
					 window.history.back();
				}			
							
		});




})(angular);
	
			
	