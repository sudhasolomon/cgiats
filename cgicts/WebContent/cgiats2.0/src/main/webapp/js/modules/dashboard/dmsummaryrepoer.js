;(function(angular){
	"use strict"
	angular.module("dmsummaryreport",['DatePicker','dateRangeModule'])
	.controller("dmsummaryrptcontroller", function($scope,$http,$mdDialog){
		
		$scope.onload = function()
		{
			$scope.pagename = "DM SUMMARY REPORT";
			
    		$scope.created = { endDate: moment(), startDate:moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1})};
    		
			$scope.dates4 = { startDate: moment().subtract(1, 'day'), endDate: moment().subtract(1, 'day') };
			$scope.ranges = {
					/*'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],*/
					'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],
					'Today': [moment(),moment()],
			        'This Month': [moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1}),moment()],
			        /*'Last 1 month': [moment().subtract(1, 'month'), moment()],*/
			        'Last 3 months': [moment().subtract(3, 'month'), moment()],
			        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
			        'Last 1 year': [moment().subtract(12, 'month'), moment()]
				
			};
			$scope.searchdmsubmital();
			
		}
		
		var obj = null;
		
		
		function getMMDDYY_Date(strValue){
			if(strValue){
			var strDateArray = strValue.split('-');
			return [strDateArray[1], strDateArray[2],strDateArray[0]].join('-');
			}
		}
		
		$scope.searchdmsubmital = function()
    	{
    		
    		$scope.viewstartdate = null;
    		$scope.viewEndDate = null;
    		if($('#dmSubmittalRangesId').val() && $('#dmSubmittalRangesId').val()!=''){
    			
    			
				var startDate = $('#dmSubmittalRangesId').val().split(' ')[0];
				var endDate = $('#dmSubmittalRangesId').val().split(' ')[2];
				obj = {startDate:getMMDDYY_Date(startDate), endDate:getMMDDYY_Date(endDate)};
				$scope.viewstartdate = obj.startDate; 
				$scope.viewEndDate = obj.endDate; 
			}else{
				obj = {startDate:$scope.created.startDate, endDate:$scope.created.endDate};
				var startDate = obj.startDate.toDate();
				var endDate = obj.endDate.toDate();
	    		$scope.viewstartdate = ((startDate.getMonth()+1)<10?"0"+(startDate.getMonth()+1):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?"0"+startDate.getDate():startDate.getDate())+'-'+startDate.getFullYear();
	    		$scope.viewEndDate = ((endDate.getMonth()+1)<10?"0"+(endDate.getMonth()+1):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?"0"+endDate.getDate():endDate.getDate())+'-'+endDate.getFullYear();
			}
    		
    		obj.startDate = $scope.viewstartdate;
    		obj.endDate = $scope.viewEndDate;
    		obj.isAuthRequired = true;
    		/*alert(JSON.stringify(obj));*/
    		var response = $http.post("submittalStatsController/getDMsSubmittalStatsReport",obj);
    		response.success(function(data, status,headers, config) 
					{
    			if(data){
    			/*alert(JSON.stringify(data));*/
    			$scope.totalcount = data.totalRecordsCount;
    			$scope.records = data.records;
    			
    			 $scope.noOfRecords = $scope.records.length;
      			 $scope.pageNo = 0;
      	    	$scope.getCurrentPageRecords();
      	    	$scope.calculatePageNavigationValues();
    			}else{
    				$scope.totalcount = null;
    					$scope.records = null;
    					$scope.displayRecords = null;
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
		
		
		
		$scope.getUserSubmittalsById = function(userId,recordStatus){
    		obj.userId = userId;
    		obj.isDm = true;
    		obj.status = recordStatus;
    		
    		var response = $http.post("submittalStatsController/getUserSubmittalsById",obj);
    		response.success(function(data, status,headers, config) 
					{
//    			$scope.totalcount = data.totalRecordsCount;
//    			$scope.records = data.records;
    			$mdDialog
				.show(
						{
							controller : DialogController,
							templateUrl : 'views/dialogbox/viewSubmittalInfoDialogbox.html',
							parent : angular
									.element(document.body),
							locals : {
								rowData : data,
								recordStatus : recordStatus
							},
							clickOutsideToClose : true,
						});
    			
					});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
    	}
		
		$scope.sbmexport = function()
		{
			alert();
		}
		
		function DialogController($scope,
				$mdDialog, rowData,recordStatus) {
			$scope.records = rowData;
			$scope.recordStatus = recordStatus;
			$scope.hide = function() {
				$mdDialog.hide();
			};

			$scope.cancel = function() {
				$mdDialog.cancel();
			};
		}
		
		
		
		
		
		$scope.exportData = function () {
	        var blob = new Blob([document.getElementById('exporttable').innerHTML], {
	            /*type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"*/
	        	type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
	        		/*type: "text/csv;charset=utf-8;"*/
	        });
	        saveAs(blob, "Report.xls");
			
	        
		}
	    
		
		
		/* *********************************************PAGINATION STARTS HERE-------------------------------------------------- */
		var recordsPerPage=10;
		
		$scope.nextPageRecords = function(pageNo){
		 $scope.lblCurrentPageRecords = "Showing "+(pageNo+1)+" to "+(((pageNo+recordsPerPage)<$scope.noOfRecords)?(pageNo+recordsPerPage):($scope.noOfRecords))+" of "+$scope.noOfRecords;
	    	$scope.isProcessing = true;
	  	  $scope.pageNo = pageNo;
	  	  $scope.getCurrentPageRecords();
	    };
	    
	    //when we click on search button
	    $scope.getCurrentPageRecords = function(){
	    	$scope.displayRecords =[];
	    	var iteratationCount = (($scope.pageNo)+recordsPerPage)> $scope.noOfRecords? $scope.noOfRecords:(($scope.pageNo)+recordsPerPage);
	for(var i=$scope.pageNo;i<iteratationCount;i++){
		$scope.displayRecords.push($scope.records[i]);
	}
	    };
		
		 $scope.calculatePageNavigationValues = function(){
			 $scope.lblCurrentPageRecords = "Showing 1 to "+(((recordsPerPage)<$scope.noOfRecords)?(recordsPerPage):($scope.noOfRecords))+" of "+$scope.noOfRecords;
			 $scope.pages = [{}];
			 $scope.pages.splice(0,1);
			 if($scope.noOfRecords > recordsPerPage){
				var j=1;
				
				for(var i=1;i<=$scope.noOfRecords;){
					var obj = {};
					obj.number = j++;
						obj.recordOffset = i-1;
					$scope.pages.push(obj);
					i += recordsPerPage;
				}
			 }
		    };
		    /* *********************************************PAGINATION ENDS HERE-------------------------------------------------- */		    
		    
	});
	
	
})(angular);