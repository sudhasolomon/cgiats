;(function(angular){
	"use strict";	
	angular.module("helppage",[])
	.controller("helpcontroller",function($scope){
		
		$scope.onload = function()
		{
			$scope.mypagename = "HELP PAGE";
		}
		
	});
	
	
})(angular);