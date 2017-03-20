;(function(angular) {

    "use strict";

    angular.module('dashboardapp', [])
            .controller('kendashboardcontroller', function($scope,$http) {  
            	//Get the login person information
         
//            	$scope.getLoggedInPersonInfo();
            	$scope.dashboarddata = {};
            	$scope.onload = function()
            	{
            		$scope.pagename = "NEW DASH BOARD";
            		
            		$scope.currentYear = new Date().getFullYear();
            		$scope.previousYear = (new Date().getFullYear())-1;
            		
            		$(".underlay").show();
            	 var response = $http.get("dashBoardController/getDashBoardStats");
            	 response.success(function(data, status,headers, config){
            		 $scope.dashboarddata = data;
            		 $(".underlay").hide();
            	 });
            	 response.error(function(data, status,headers, config){
            		 $(".underlay").hide();
            	 });
            		
            	 
            	 var response = $http.get("dashBoardController/getIndiaDashBoardStats");
            	 response.success(function(data, status,headers, config){
            		 $scope.indiadashboarddata = data;
            		 $(".underlay").hide();
            	 });
            	 response.error(function(data, status,headers, config){
            		 $(".underlay").hide();
            	 });
            	 
            	 
            		
            		$scope.getFieldValue = function(fieldValue){
            			var num = fieldValue?fieldValue:0
            			
            			return num.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
            		}
            		
            	}
            	
            }); 

})(angular);