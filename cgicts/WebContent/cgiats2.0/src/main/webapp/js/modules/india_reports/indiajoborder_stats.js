;(function(angular){
	
	
			"use strict";
			angular.module('indiaJobOrderStatsModule',[])
			.controller('indiaJobOrderStatsController',function($scope, $http, $rootScope, $state, dateRangeService, $timeout, blockUI, $location){
				
				$scope.indiaReports = [];
				$scope.jobOrderStatsTotal ={};
				$scope.joborderStatsTable = false;
				$scope.refreshData = false;
				$scope.data = false;
				var statsData = [];
				var exportData =[];
				
				
				$scope.Created = {
						startDate : moment().startOf('month') ,
						endDate : moment()
					};
	    	$scope.daterange ={
	    			Today : [moment(),moment()],
	    			onemonth : [moment().subtract(1, 'month'), moment()],
	    			twomonths : [moment().subtract(2, 'month'), moment()],
	    			threemonths : [moment().subtract(3, 'month'), moment()],
	    			custom : 'custom'
	    	}
	    
	    	  $scope.ranges = {
	    			'Today' : [moment(),moment()],
	    	        'This Month': [moment().startOf('month'), moment()],
	    	        'Last 3 months': [moment().subtract(3, 'month'), moment()],
	    	        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
	    	        'Last 1 year': [moment().subtract(12, 'month'), moment()]
	    	        };
	    	
	    	$scope.onload = function(){
	    		
	    		
	    		$scope.jobOrderStats ();
	    	}
	    	
	    	$scope.refresh = function (){
	    		
	    		if($scope.refreshData){
	    			$(".underlay").show();
	    		$scope.jobOrderStats();
	    		$(".underlay").hide();
	    		}
	    	}
	    	
	    	$scope.jobOrderStats = function(){
	    		
//				$scope.jobOrderStatsTotal = [];
				statsData =[];
				$(".underlay").show();
				$timeout(function() {
					blockUI.stop();
					
				}, 10000);
				getJobOrderStats();
	    		$(".underlay").hide();
	    	}
	    	
	    	function getDDMMYY_Date(strValue){
				if(strValue){
				var strDateArray = strValue.split('-');
				return [strDateArray[2], strDateArray[1],strDateArray[0]].join('-');
				}
			}
	    	
	    	function getJobOrderStats(){
	    		if($scope.Created){
	    			var obj = null;
	    			var startDate= null,endDate=null;
	    			if($('#resumeStatsRange').val() && $('#resumeStatsRange').val()!=''){
	    				 startDate = $('#resumeStatsRange').val().split(' ')[0];
	    				 startDate = getDDMMYY_Date(startDate);
	    				 endDate = $('#resumeStatsRange').val().split(' ')[2];
	    				 endDate = getDDMMYY_Date(endDate);
	    				 $scope.obj = {startDate:startDate, endDate:endDate};
	    			}else{
	    				var obj = {startDate:$scope.Created.startDate, endDate:$scope.Created.endDate};
	    				 startDate = obj.startDate.toDate();
	    				 endDate = obj.endDate.toDate();
	    				startDate = (startDate.getDate()<10?"0"+startDate.getDate():startDate.getDate())+'-'+((startDate.getMonth()+1)<10?"0"+(startDate.getMonth()+1):(startDate.getMonth()+1))+'-'+(startDate.getFullYear());
	    				endDate = (endDate.getDate()<10?"0"+endDate.getDate():endDate.getDate())+'-'+((endDate.getMonth()+1)<10?"0"+(endDate.getMonth()+1):(endDate.getMonth()+1))+'-'+(endDate.getFullYear());
	    				$scope.obj = {startDate:startDate, endDate:endDate};
	    			}
	    		 
	    		}
	    		$scope.joborderStatsTable = true;
	    		var response = $http.post("indiaReports/getJobOrderStats",$scope.obj);
	    		response.success(function(data, headers, status, config){
	    			$scope.jobOrderStatsData =[];
	    			 if(data && data.jobOrderData){
	    			$scope.refreshData = true;
	    			statsData = data.jobOrderData;
	    			exportData = data.jobOrderData;
	    			$scope.jobOrderStatsTotal = data.jobOrderStatsTotal;
//	    			$scope.jobOrderStatsTotal.push(data.jobOrderStatsTotal);  ng-repeat="stats in jobOrderStatsTotal"
	    			
	    			
	    			$scope.jobOrderStatsData = data;
	    			$scope.exportJobOrdersStatsList = [];
					for (var i = 0; i < exportData.length; i++) {
						var obj = {userName:exportData[i].userName,
								open: exportData[i].open,assigned: exportData[i].assigned,
								filled: exportData[i].filled,closed:exportData[i].closed,reopen: exportData[i].reopen,total: exportData[i].total};
						$scope.exportJobOrdersStatsList.push(obj);
					}
					$scope.exportDatalist = [];
					$scope.exportDatalist = $scope.exportJobOrdersStatsList;
					var totalObj ={userName : "Total:", open : $scope.jobOrderStatsTotal.OPEN,  assigned : $scope.jobOrderStatsTotal.ASSIGNED,
							filled : $scope.jobOrderStatsTotal.FILLED, closed : $scope.jobOrderStatsTotal.CLOSED, reopen : $scope.jobOrderStatsTotal.REOPEN,
							total : $scope.jobOrderStatsTotal.TOTAL};
					$scope.exportDatalist.push(totalObj);
	    			dispalyIndiaTable();
	    			 }else{
	    				 statsData =[];
	    				 $scope.jobOrderStatsTotal ={};
	    				 dispalyIndiaTable();
	    			 }
	    			 
	    			 
	    			 $scope.cll();
	    			 
	    			 
	    		});
	    		response.error(function(data, headers, status, config){
	    			alert("error data"+JSON.stringify(data));
	    		});
	    	}
	    	
	    	$scope.getTimeFnc = function(){
				$scope.currentDateWithTime = new Date();
				
			}
	    	
	    	
	    	function dispalyIndiaTable() {
				$scope.indiaReports.jobOrderStats = {
					options : {
						data : statsData || {},
						striped : true,
						pagination : true,
						/*paginationVAlign : "both",*/
						sidePagination : 'client',
						silentSort: false,
						pageList : [ 10, 20, 50 ],
						search : false,
						showColumns : false,
						showRefresh : false,
						showExport:false,
						exportTypes : ['excel', 'pdf'],
						showFooter : false,
						clickToSelect : false,
						showToggle : false,
						/*detailView : true,*/
						/*exportOptions :{
					         fileName: 'testo', 
					         worksheetName: 'test1',         
					       },*/
						maintainSelected : true,
						columns : [
								{
									field : 'userName',
									title : 'DM NAME',
									align : 'left',
									events : window.InJobOrderStatsEvents,
									formatter : InJobOrderStatsFormatter,
									footerFormatter : totalFooter,
									sortable : true
								},
								{
									field : 'open',
									title : 'OPEN',
									align : 'left',
									footerFormatter : openFooter,
									sortable : false
								},
								{
									field : 'assigned',
									title : 'ASSIGNED',
									align : 'left',
									footerFormatter : assignedFooter,
									sortable : false
								},
								{
									field : 'filled',
									title : 'FILLED',
									align : 'left',
									footerFormatter : filledFooter,
									sortable : false
								},
								{
									field : 'closed',
									title : 'CLOSED',
									align : 'left',
									footerFormatter : closedFooter,
									sortable : false
								},
								{
									field : 'reopen',
									title : 'REOPEN',
									align : 'left',
									footerFormatter : reopenFooter,
									sortable : false
								},
								{
									field : 'total',
									title : 'TOTAL',
									align : 'left',
									footerFormatter : orderTotalFooter,
									sortable : false
								}],

						},
				
					
					}
				};

		
				function InJobOrderStatsFormatter(value, row, index) {
					return ['<a class="userName actionIcons" title="'+ row.userId+ '" flex-gt-md="auto" target="_blank">'+ row.userName+'</a>']
					.join('');
			}
			window.InJobOrderStatsEvents = {
				'click .userName' : function(e,value, row, index) {
					var start = dateRangeService.formatDate_india($scope.obj.startDate);
					var end =  dateRangeService.formatDate_india($scope.obj.endDate);
					$rootScope.userName = row.userName;
					var url = $location.href = "#/india_joborders/my_indiajobOrder?statsUser="+row.userId+"&fromDate="+start+"&toDate="+end ;
					window.open(url,'_blank');//myIndiaJobOrders
				},
			}
			
			function totalFooter(data){
			    return 'Total :';   
			}
			function openFooter(data){
				var total = 0;
			    $.each(data, function (i, row) {
			        total += parseInt(row.open);
			    });
			    return ''+total;  
			}
			function assignedFooter(data){
				var total = 0;
			    $.each(data, function (i, row) {
			        total += parseInt(row.assigned);
			    });
			    return ''+total;  
			}
			function filledFooter(data){
				var total = 0;
			    $.each(data, function (i, row) {
			        total += parseInt(row.filled);
			    });
			    return ''+total;  
			}
			function closedFooter(data){
				var total = 0;
			    $.each(data, function (i, row) {
			        total += parseInt(row.closed);
			    });
			    return ''+total;   
			}
			function reopenFooter(data){
				var total = 0;
			    $.each(data, function (i, row) {
			        total += parseInt(row.reopen);
			    });
			    return ''+total;   
			}function orderTotalFooter(data){
				var total = 0;
			    $.each(data, function (i, row) {
			        total += parseInt(row.total);
			    });
			    return ''+total;   
			}
			
			/*function footerFormatterCount (data, field){
			 * footerFormatterCount(data,'field');
				alert(field);
				var total = 0;
			    $.each(data, function (i, row) {
			        total += parseInt(row.open);
			    });
			    alert(total);
			    return ''+total;
			}*/
				
			
			$scope.cll = function()
	    	{
	    		$timeout(function(){
		    		$("#open").text($scope.jobOrderStatsTotal.OPEN);
		    		$("#assigned").text($scope.jobOrderStatsTotal.ASSIGNED);
		    		$("#filled").text($scope.jobOrderStatsTotal.FILLED);
		    		$("#closed").text($scope.jobOrderStatsTotal.CLOSED);
		    		$("#reopen").text($scope.jobOrderStatsTotal.REOPEN);
		    		$("#total").text($scope.jobOrderStatsTotal.TOTAL);
		    		}, 30);
	    	}
			
			
			
			
			});
})(angular);