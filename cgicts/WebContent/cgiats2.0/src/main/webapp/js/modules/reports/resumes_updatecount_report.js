;(function(angular){
	"use strict";	
	angular.module("resumesupdatecountreport",['reportsmodule'])
	.controller("resumesupdatecountcontroller",function($scope,$http,$state,dateRangeService, $timeout){
		var dates = [];
		var countValue=0;
		var avgCountValue=0;
		$scope.candidate = [];
		$scope.exportResumeUpdateData = [];
		$scope.totalResumeUpdateCount=[];
		$scope.resumeUpdateData = {};
		$scope.resumeUpdate={};
		$scope.dateValidation=false;
		$scope.currentDateWithTime = new Date();
		$scope.onload = function(){
			
			$scope.dates = getMonthYear();
			 
			$scope.DateFormat = 'MM-dd-yyyy';
			 
			$scope.resumeUpdateStartDateOptions = {
						date : new Date(),
						showWeeks : false
					};
			$scope.resumeUpdateEndDateOptions = {
						date : new Date(),
						showWeeks : false
					};
				
			$scope.resumeUpdateStartDateopen = function() {
					$scope.resumeUpdateStartDatePopup.opened = true;
				}
			$scope.resumeUpdateEndDateopen = function() {
					$scope.resumeUpdateEndDatePopup.opened = true;
				};
				
			$scope.resumeUpdateStartDatePopup = {
						opened : false
					};
			$scope.resumeUpdateEndDatePopup = {
						opened : false
					};
				
			$scope.resumeUpdate.resumeUpdateStartDate = new Date();
			$scope.resumeUpdate.resumeUpdateEndDate = new Date();
				
			var from = dateRangeService.formatDate($scope.resumeUpdate.resumeUpdateStartDate);
			var to = dateRangeService.formatDate($scope.resumeUpdate.resumeUpdateEndDate);
			$scope.resumeUpdateCount(from,to);
		}
		
		$scope.viewByMonth = function(resumeUpdate){
			 var date = new Date();
			 if(resumeUpdate.resumeUpdateMonthandYear){
			 date.setMonth(resumeUpdate.resumeUpdateMonthandYear);
			 var startdate = new Date(date.getFullYear(), date.getMonth(), 1);
			 var from = dateRangeService.formatDate(startdate);
			 var endDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
			 var to = dateRangeService.formatDate(endDate);
			
			 $scope.resumeUpdateCount(from,to);
			 $scope.dateValidation=false;
			 }
		}
		
		$scope.viewByDates = function(resumeUpdate){
			if($scope.resumeUpdate.resumeUpdateStartDate && $scope.resumeUpdate.resumeUpdateEndDate && $scope.resumeUpdate.resumeUpdateStartDate<=$scope.resumeUpdate.resumeUpdateEndDate){
			var from = dateRangeService.formatDate($scope.resumeUpdate.resumeUpdateStartDate);
			var to = dateRangeService.formatDate($scope.resumeUpdate.resumeUpdateEndDate);
			$scope.resumeUpdateCount(from,to);
			$scope.dateValidation=false;
			}else{
				$scope.dateValidation = true;
			}
		}
		
		$scope.resumeUpdateCount = function(fromDate,toDate){
			$scope.resumeCountTable = false;
			var response = $http.post('reportController/getResumesUpdateCount?startDate='+fromDate+'&endDate='+toDate);
				response.success(function (data,status,headers,config){
					$scope.resumeCountTable = true;
					dispalyTable();
					$scope.candidate.bsTableControl.options.data = data.resumesupdatecountdata;
					
					
					 countValue=0;
					 avgCountValue=0;
					for(var i=0; i<data.resumesupdatecountdata.length; i++){
						countValue+= data.resumesupdatecountdata[i].resumes_count;
						avgCountValue+=data.resumesupdatecountdata[i].avg_count;
					}
					avgCountValue = avgCountValue.toFixed(2);
					$scope.cll();
					
					$scope.resumeUpdateData = data.resumesupdatecountdata;
					$scope.exportResumeUpdateData = [];
					for (var i = 0; i < $scope.resumeUpdateData.length; i++) {
					var obj = {Name: $scope.resumeUpdateData[i].name, Count: $scope.resumeUpdateData[i].resumes_count, AvgCountPerDay: $scope.resumeUpdateData[i].avg_count};
					$scope.exportResumeUpdateData.push(obj);
					}
					
					$scope.totalResumeUpdateCount=[];
					$scope.totalResumeUpdateCount=$scope.exportResumeUpdateData;
					var totalobj = {Name:'Total', Count:countValue, AvgCountPerDay: avgCountValue};
					$scope.totalResumeUpdateCount.push(totalobj);
					
				});
				response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
		}
		
		 $scope.getTimeFnc = function(){
				$scope.currentDateWithTime = new Date();
			}
		
		function dispalyTable() {
						$scope.candidate.bsTableControl = {
							options : {
								pagination : true,
								paginationVAlign : "top",
								sidePagination : 'client',
								silentSort: false,
								pageList : [ 10, 20, 50 ],
								search : false,
								showColumns : false,
								showRefresh : false,
								clickToSelect : false,
								showToggle : false,
								columns : [
										/*{
											field : '',
											title : 'S.No',
				                            formatter : indexformat,
											align : 'center',
										},*/
										{
											field : 'name',
											title : 'Name',
											//align : 'center',
										},
										{
											field : 'resumes_count',
											title : 'Count',
											align : 'center',
										},{
											field : 'avg_count',
											title : 'Avg Count/day',
											align : 'center',
										}],
							}
						};
						
					    /*  
			            function indexformat(value, row,
								index){
			            	return index+1;
			            	}*/
		}
		
		
	  	function getMonthYear(){
    		
  		  var month = new Array();
  		    month[0] = "January";
  		    month[1] = "February";
  		    month[2] = "March";
  		    month[3] = "April";
  		    month[4] = "May";
  		    month[5] = "June";
  		    month[6] = "July";
  		    month[7] = "August";
  		    month[8] = "September";
  		    month[9] = "October";
  		    month[10] = "November";
  		    month[11] = "December";
  		  var d1 = new Date();
  		  var totalMonths = d1.getMonth()+1;
  		    for(var i = 0; i<totalMonths; i++){
  		    	 var d = new Date();
  		    //	d.setMonth(d.getMonth() - i);
  		    	var currentMonth = d.getMonth() - i;
  		    	var months = month[currentMonth];
  		    	 
  		    	var years = d.getFullYear();
  		    	dates.push({
  		    		month : months+" "+years,
  		    		monthIndex : currentMonth
  		    		});
  		    }
  		   return dates;
  	}
	  	
	  	
    	$scope.cll = function()
    	{
    		$timeout(function(){
	    		$("#total").text(countValue);
	    		$("#avgtotal").text(avgCountValue);
	    		}, 400);
    	}
		
	});
	
	
})(angular);