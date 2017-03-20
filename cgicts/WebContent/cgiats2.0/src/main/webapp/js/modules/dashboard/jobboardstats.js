;(function(angular){
	
	"use strict";
	
	angular.module("jobBoardStatsModule",['ui.bootstrap'])
	.controller("jobboardstatscontroller" , function($scope, $http, dateRangeService, $timeout, blockUI, $location, $state){

		
		$scope.isErrorMsg = false;
		$scope.jobBoard = {};
		$scope.jobBoardStats = [];
		$scope.jobBoardStatsTable = false;
		
		$scope.diceJobBoardStats = [];
		$scope.diceJobBoardStatsTable = false;
		
		$scope.resumeStats = [];
		$scope.resumeStatsTable = false;
		var dates = [];
		
		$scope.onload=function(){
			
			
			$scope.dates = getMonthYear();
//			$scope.jobBoard.Created = [moment(), moment()];
			$scope.Created = {
					endDate : moment(),
					startDate : moment()
				};
			 $scope.ranges = {
					 	'Today' : [moment(), moment()],
	        	        'Last 1 month': [moment().subtract(1, 'month'), moment()],
	        	        'Last 3 months': [moment().subtract(3, 'month'), moment()],
	        	        'Last 1 year': [moment().subtract(12, 'month'), moment()],
	        	        'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()]
	        	        };
			
			//Date Range
			$scope.DateFormat = 'MM-dd-yyyy';
			
			$scope.JobBoardStartDateOptions = {
					date : new Date(),
					showWeeks : false
				};
			$scope.JobBoardEndDateOptions = {
					date : new Date(),
					showWeeks : false
				};
			
			$scope.DiceStartDateOptions = {
					date : new Date(),
					showWeeks : false
				};
			$scope.DiceEndDateOptions = {
					date : new Date(),
					showWeeks : false
				};
			
			/*$scope.ResumeStatsStartDateOptions = {
					date : new Date(),
					showWeeks : false
				};
			$scope.ResumeStatsEndDateOptions = {
					date : new Date(),
					showWeeks : false
				};	*/
			
			
			
			$scope.JobBoardStartDateopen = function() {
				$scope.JobBoardStartDatePopup.opened = true;
			}
			$scope.JobBoardEndDateopen = function() {
				$scope.JobBoardEndDatePopup.opened = true;
			};
			
			$scope.DiceStartDateopen = function() {
				$scope.DiceStartDatePopup.opened = true;
			}
			$scope.DiceEndDateopen = function() {
				$scope.DiceEndDatePopup.opened = true;
			};
			
		/*	$scope.ResumeStatsStartDateopen = function() {
				$scope.ResumeStatsStartDatePopup.opened = true;
			}
			$scope.ResumeStatsEndDateopen = function() {
				$scope.ResumeStatsEndDatePopup.opened = true;
			};*/
			
			
			
			$scope.JobBoardStartDatePopup = {
					opened : false
				};
			$scope.JobBoardEndDatePopup = {
					opened : false
				};
			
			$scope.DiceStartDatePopup = {
					opened : false
				};
			$scope.DiceEndDatePopup = {
					opened : false
				};
			
			/*$scope.ResumeStatsStartDatePopup = {
					opened : false
				};
			$scope.ResumeStatsEndDatePopup = {
					opened : false
				};*/
			

			
			$scope.jobBoardStatsTable = true;
			jobBoardStatsTable();
			
			$scope.diceJobBoardStatsTable = true;
			diceJobBoardStatsTable();
			
			$scope.resumeStats();
		}
		
		
		$scope.viewByMonth = function(jobBoard){
			
			 var date = new Date();
			 if(jobBoard.jobBoardStatsMonthandYear){
				 $(".underlay").show();
				 for(var i=0;i<$scope.dates.length;i++){
					 if($scope.dates[i].month == jobBoard.jobBoardStatsMonthandYear){
						 date= $scope.dates[i].fullYear;
					 }
				 }
//			 date.setMonth(jobBoard.jobBoardStatsMonthandYear);
//			 alert(date);
			 var startdate = new Date(date.getFullYear(), date.getMonth(), 1);
			 var from = dateRangeService.formatDate_india(startdate);
			 var endDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
			 var to = dateRangeService.formatDate_india(endDate);
//			 alert(from+'\n'+to);
			 getJobBoardStats(from, to);
			 $(".underlay").hide();
			 }
		}
		$scope.viewByDates = function(jobBoard){
			if($scope.jobBoard.JobBoardStartDate && $scope.jobBoard.JobBoardEndDate){
				
				 $(".underlay").show();
			var from = dateRangeService.formatDate_india($scope.jobBoard.JobBoardStartDate);
			var to = dateRangeService.formatDate_india($scope.jobBoard.JobBoardEndDate);
			 getJobBoardStats(from, to);
			 $(".underlay").hide();
			}
		}
		
		function getJobBoardStats(from, to){
			var response = $http.post('jobBoardStats/getJobBoardStatsInfo?startDate='+from+'&endDate='+to);
			response.success(function(data, status,headers, config) {
				$scope.jobBoardStatsTable = true;
				jobBoardStatsTable();
				for(var i=0;i<data.jobBoardStats.length;i++){
					data.jobBoardStats[i].downloadCount = $scope.getFieldValue(data.jobBoardStats[i].downloadCount);
				}
				$scope.jobBoardStats.jobBoardStasTableControl.options.data = data.jobBoardStats;
				$scope.jobStatsTotal = $scope.getFieldValue(data.Total);
				$scope.jobBoardTotal();
				
			});
			response.error(function(data, status, headers, config){
    			  if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				$state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
    		  });
		}
		//Job Board Stats Table
    	function jobBoardStatsTable(){
    		
            $scope.jobBoardStats.jobBoardStasTableControl = {
                    options: { 
                        striped: true,
                        pagination: true,
                        paginationVAlign: "both", 
                        pageList: [50,100,200],
                        search: false,
                        //sidePagination : 'server',
                        silentSort: false,
                        showColumns: false,
                        showRefresh: false,
                        clickToSelect: false,
                        showToggle: false,
                        maintainSelected: true, 
                        showFooter : false,
                        columns: [
                        {
                            field: 'users',
                            title: 'Users',
                            align: 'left',
                            
                        }, {
                            field: 'downloadCount',
                            title: 'Resumes Downloaded',
                            align: 'left',
                           
                        },]
                    }
            };
    	}
    	
    	
    	
    	$scope.diceViewByMonth = function(jobBoard){
    		 var date = new Date();
    		 if(jobBoard.dicemonthandyear){
    			 $(".underlay").show();
    			 
    			 for(var i=0;i<$scope.dates.length;i++){
					 if($scope.dates[i].month == jobBoard.dicemonthandyear){
						 date= $scope.dates[i].fullYear;
					 }
				 }
    			 
//			 date.setMonth(jobBoard.dicemonthandyear);
			 var startdate = new Date(date.getFullYear(), date.getMonth(), 1);
			 var from = dateRangeService.formatDate_india(startdate);
			 var endDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
			 var to = dateRangeService.formatDate_india(endDate);
			 getDiceJobBoardStats(from, to);
			 $(".underlay").hide();
    		 }
    	}
    	
    	$scope.diceViewByDates = function(jobBoard){
    		if($scope.jobBoard.diceStartDate && $scope.jobBoard.diceEndDate){
    			$(".underlay").show();
    		var from = dateRangeService.formatDate_india($scope.jobBoard.diceStartDate);
			var to = dateRangeService.formatDate_india($scope.jobBoard.diceEndDate);
			 getDiceJobBoardStats(from, to);
			 $(".underlay").hide();
    		}
    	}
    	
    	function getDiceJobBoardStats(from, to){
    		var response = $http.post('jobBoardStats/getDiceJobStatsInfo?startDate='+from+'&endDate='+to);
			response.success(function(data, status,headers, config) {
				$scope.diceJobBoardStatsTable = true;
				diceJobBoardStatsTable();
				for(var i=0;i<data.diceStats.length;i++){
					data.diceStats[i].downloadCount = $scope.getFieldValue(data.diceStats[i].downloadCount);
				}
				$scope.diceJobBoardStats.diceJobBoardStatsTableControl.options.data = data.diceStats;
				$scope.diceStatsTotal = $scope.getFieldValue(data.Total);
				$scope.diceBoardTotal();
			});
			response.error(function(data, status, headers, config){
    			  if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				$state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
    		  });
    	}
    	
    	
		//Dice Job Board Stats Table
    	function diceJobBoardStatsTable(){
    		
            $scope.diceJobBoardStats.diceJobBoardStatsTableControl = {
                    options: { 
                        striped: true,
                        pagination: true,
                        paginationVAlign: "both", 
                        pageList: [50,100,200],
                        search: false,
                        //sidePagination : 'server',
                        silentSort: false,
                        showColumns: false,
                        showRefresh: false,
                        clickToSelect: false,
                        showToggle: false,
                        maintainSelected: true, 
                        showFooter : false,
                        columns: [
                        {
                            field: 'users',
                            title: 'Users',
                            align: 'left',
                            
                        },
                       /* {
                            field: 'monthlyViews',
                            title: 'Monthly Views',
                            align: 'left',
                           
                        },*/{
                            field: 'downloadCount',
                            title: 'Resumes Downloaded',
                            align: 'left',
                           
                        },]
                    }
            };
    	}
    	
    	$scope.resumeStats = function(){
    		
    		$(".underlay").show();
			$timeout(function() {
				blockUI.stop();
				
			}, 10000);
			getResumeStats();
    		$(".underlay").hide();
    		
    	}
    	
    	
    	
    	function getDDMMYY_Date(strValue){
			if(strValue){
			var strDateArray = strValue.split('-');
			return [strDateArray[2], strDateArray[1],strDateArray[0]].join('-');
			}
		}
    
    	function getResumeStats(){
    		var obj=null;
    		 var startDate= null,endDate=null;
     		if($scope.Created){
     			if($('#resumeStatsRange').val() && $('#resumeStatsRange').val()!=''){
     				 startDate = $('#resumeStatsRange').val().split(' ')[0];
     				 startDate = getDDMMYY_Date(startDate);
     				 endDate = $('#resumeStatsRange').val().split(' ')[2];
     				 endDate = getDDMMYY_Date(endDate);
     			}else{
     				obj = {startDate:$scope.Created.startDate, endDate:$scope.Created.endDate};
     				 startDate = obj.startDate.toDate();
     				 endDate = obj.endDate.toDate();
     				startDate = (startDate.getDate()<10?"0"+startDate.getDate():startDate.getDate())+'-'+((startDate.getMonth()+1)<10?"0"+(startDate.getMonth()+1):(startDate.getMonth()+1))+'-'+(startDate.getFullYear());
     				endDate = (endDate.getDate()<10?"0"+endDate.getDate():endDate.getDate())+'-'+((endDate.getMonth()+1)<10?"0"+(endDate.getMonth()+1):(endDate.getMonth()+1))+'-'+(endDate.getFullYear());
     			}
     	 
     		}
    		
     		$scope.resumeStatsTable = true;
    		var response = $http.post('jobBoardStats/getResumeStatsInfo?startDate='+startDate+'&endDate='+endDate);
			response.success(function(data, status,headers, config) {
				resumeStatsTableView();
				for(var i=0;i<data.resumeStats.length;i++){
					data.resumeStats[i].downloadCount = $scope.getFieldValue(data.resumeStats[i].downloadCount);
				}
				$scope.resumeStats.resumeStatsTableControl.options.data = data.resumeStats;
				$scope.resumeStatsTotal = $scope.getFieldValue(data.total);
				$scope.resumeTotal();
			});
			response.error(function(data, status, headers, config){
    			  if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				$state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
    		  });
    	}
		//Resume Stats Table
    	function resumeStatsTableView(){
    		
            $scope.resumeStats.resumeStatsTableControl = {
                    options: { 
                        striped: true,
                        pagination: true,
                        paginationVAlign: "both", 
                        pageList: [50,100,200],
                        search: false,
                        //sidePagination : 'server',
                        silentSort: false,
                        showColumns: false,
                        showRefresh: false,
                        clickToSelect: false,
                        showToggle: false,
                        maintainSelected: true, 
                        showFooter : false,
                        columns: [
                        {
                            field: 'users',
                            title: 'Users',
                            align: 'left',
                            
                        },
                        {
                            field: 'downloadCount',
                            title: 'Resumes',
                            align: 'left',
                           
                        },]
                    }
            };
    	}
    	
    	
    	function getMonthYear(){
//    		var now = new Date();
//    		var sixMonthsFromNow = new Date(now.setMonth(now.getMonth() - 6));
//    		alert(sixMonthsFromNow);
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
    		   
    		    for(var i = 0; i<=5; i++){
    		    	var now = new Date();
    		    	// alert(d.getMonth() - i);
    		    	//d.setMonth(d.getMonth() - i);
//    		    	var currentMonth = d.getMonth() - i;
    		    	
    		    	var currentMonth = new Date(now.setMonth(now.getMonth() - i));
    		    	
    		    	var months = month[currentMonth.getMonth()];
    		    	//alert(currentMonth + " " +months);
    		    	 
    		    	var years = currentMonth.getFullYear();
    		    	dates.push({
    		    		month : months+" "+years,
    		    		monthIndex : currentMonth.getMonth(),
    		    		fullYear : currentMonth 
    		    		});
    		    }
    		   return dates.reverse();
    	}
    	$scope.jobBoardTotal = function(){
    		$timeout(function(){
	    		$("#jobTotal").text($scope.jobStatsTotal);
	    		}, 30);
    	}
    	
    	$scope.diceBoardTotal = function(){
    		$timeout(function(){
	    		$("#diceTotal").text($scope.diceStatsTotal);
	    		}, 30);
    	} 

    	$scope.resumeTotal = function()
    	{
    		$timeout(function(){
	    		$("#resumeTotal").text($scope.resumeStatsTotal);
	    		}, 30);
    	}
    	
    	$scope.getFieldValue = function(fieldValue){
            var num = fieldValue?fieldValue:0
            
            return num.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
           }
	});
})(angular);
