;(function(angular) {

    "use strict";

    angular.module('dmsummary', ['DatePicker','dateRangeModule'])
    .controller("dmsummaryconteroller",function($rootScope, $scope, $http) {
            	
    	var recordsPerPage=20;
            	$scope.onload = function(){
            		$scope.pagename = "ALL HOT JOB ORDERS";
            		
            		$scope.created = { endDate: moment(), startDate:moment({'year' :(new Date()).getFullYear(), 'month' :0, 'day' :1})};
            		
    				$scope.dates4 = { startDate: moment().subtract(1, 'day'), endDate: moment().subtract(1, 'day') };
    				$scope.ranges = {
    						/*'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],
    				        'Today': [moment(),moment()],
    				        'This Month': [moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1}),moment()],
    				        'Last 1 month': [moment().subtract(1, 'month'), moment()],
    				        'Last 3 months': [moment().subtract(3, 'month'), moment()],
    				        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
    				        'Last 1 year': [moment().subtract(12, 'month'), moment()]*/
    						'This Month': [moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1}),moment()]
    					
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
        	    		$scope.viewstartdate = startDate.getFullYear()+'-'+((startDate.getMonth()+1)<10?"0"+(startDate.getMonth()+1):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?"0"+startDate.getDate():startDate.getDate());
        	    		$scope.viewEndDate = endDate.getFullYear()+'-'+((endDate.getMonth()+1)<10?"0"+(endDate.getMonth()+1):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?"0"+endDate.getDate():endDate.getDate());
        			}
            		obj.startDate = $scope.viewstartdate;
            		obj.endDate = $scope.viewEndDate;
            		/*alert(JSON.stringify(obj));*/
            		var response = $http.post("submittalStatsController/getDMsSubmittalStatsReportWithoutLogin",obj);
            		response.success(function(data, status,headers, config) 
        					{
            			/*alert(JSON.stringify(data));*/
            			$scope.totalcount = data.totalRecordsCount;
            			$scope.records = data.records;
            			
            			 $scope.noOfRecords = $scope.records.length;
              			 $scope.pageNo = 0;
            			$scope.lblCurrentPageRecords = "Showing 1 to "+(((recordsPerPage)<$scope.noOfRecords)?(recordsPerPage):($scope.noOfRecords))+" of "+$scope.noOfRecords;
        					});
        			response.error(function(data, status, headers, config){
          			  if(status == constants.FORBIDDEN){
          				location.href = 'login.html';
          			  }else{  			  
          				$state.transitionTo("ErrorPage",{statusvalue  : status});
          			  }
          		  });
            	}
        		
            	
        		
}); 

})(angular);