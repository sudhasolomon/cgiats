;(function(angular){
	"use strict";
	angular.module("liverecruitersreportmodule", [])
	.controller("liverecruitersreportcontroller", function($scope, $http, $timeout, $state, $interval){
		var todayData = [];
		$scope.todayReports = [];
		$scope.onload = function()
		{
			$scope.pageName = "Live Recruiter's Report";
			$scope.getAllDMsAndCities();
		}
		
		
		$interval(function() {
			$scope.getSubmittalCurrentReport($scope.selectedCity);
			 }, 300000);
		
		
		
		$scope.getSubmittalCurrentReport = function(selectedCity){
			$scope.RecruitersReportTable = false;
			var response = $http.get('reportController/getSubmittalCurrentReport?city='+selectedCity);
			response.success(function (data,status,headers,config){
						$scope.currentData = data.list;
						$scope.todayTotal = data.total;
						todayData = data.list;
						$scope.RecruitersReportTable = true;
						dispalyRecruiterstodayReportTable();
						$scope.cll();
						
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
		};
		
		
		
		
		$scope.getAllDMsAndCities = function(){
			$scope.cityList = [];
			var response = $http.get('commonController/getAllDMAndADM_OfficeLocations');
			response.success(function (data,status,headers,config){
				if(data){
				$scope.dmname = data.users;
//				$scope.cityList = data.cities;
				
				var tempObj = {userId:constants.ALL,fullName:constants.ALL};
			    $scope.dmname.splice(0, 0, tempObj);
			$scope.selectedCurrentDM = constants.ALL;
			
			
				$scope.selectedCurrentDM = data.users[0].userId;
				$scope.selectedCity = constants.HYD;
				var categories = [];
				$scope.getSubmittalCurrentReport($scope.selectedCity);
				$.each(data.cities, function(index, value) {
//				    if ($.inArray(value.officeLocation, categories) === -1) {
//				        categories.push(value.officeLocation);
					var obj={officeLocation:value};
				        $scope.cityList.push(obj);
//				    }
				});
				
				}
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
		};
		
		
		
		
		
		
		
		
		
		
		function dispalyRecruiterstodayReportTable() {
			$scope.todayReports.reportTableControl= {
				options : {
					data : todayData || {},
					striped : true,
					pagination : true,
					paginationVAlign : "top",
					sidePagination : 'client',
					silentSort: false,
					pageList : [ 10, 20, 50 ],
					search : false,
					showColumns : false,
					showRefresh : false,
					showExport:false,
					showFooter : false,
					clickToSelect : false,
					showToggle : false,
					columns : [
							{
								field : 'Name',
								title : 'Name',
								align : 'left',
								sortable : true
							},
							{
								field : 'Location',
								title : 'Location',
								align : 'left',
								sortable : true
							},
							{
								field : 'SUBMITTED',
								title : 'SUBM',
								align : 'center',
								sortable : true
							},
							{
								field : 'DMREJ',
								title : 'DMREJ',
								align : 'center',
								sortable : true
							},
							{
								field : 'ACCEPTED',
								title : 'ACCEPT',
								align : 'center',
								sortable : true
							},
							{
								field : 'INTERVIEWING',
								title : 'INTVIEW',
								align : 'center',
								sortable : true
							},
							{
								field : 'CONFIRMED',
								title : 'CONF',
								align : 'center',
								sortable : true
							},
							{
								field : 'REJECTED',
								title : 'REJ',
								align : 'center',
								sortable : true
							},
							{
								field : 'STARTED',
								title : 'START',
								align : 'center',
								sortable : true
							},
							{
								field : 'BACKOUT',
								title : 'BACKOUT',
								align : 'center',
								sortable : true
							},
							{
								field : 'OUTOFPROJ',
								title : 'OOP',
								align : 'center',
								sortable : true
							},
							{
								field : 'NotUpdated',
								title : 'NU',
								align : 'center',
								sortable : true
							}],

					},
			
				
				}
			};
		
			
			
			
			
			$scope.cll = function()
	    	{
	    		$timeout(function(){
	    			$("#ydsubmi").text($scope.todayTotal.SUBMITTED);
		    		$("#yddmrej").text($scope.todayTotal.DMREJ);
		    		$("#ydaccepcted").text($scope.todayTotal.ACCEPTED);
		    		$("#ydinterviewing").text($scope.todayTotal.INTERVIEWING);
		    		$("#ydconfirmed").text($scope.todayTotal.CONFIRMED);
		    		$("#ydrejected").text($scope.todayTotal.REJECTED);
		    		$("#ydstarted").text($scope.todayTotal.STARTED);
		    		$("#ydbackout").text($scope.todayTotal.BACKOUT);
		    		$("#ydoutofproj").text($scope.todayTotal.OUTOFPROJ);
		    		$("#ydnu").text($scope.todayTotal.NOT_UPDATED);
		    		
		    		}, 400);
	    	}
		
		
	});
	
})(angular);