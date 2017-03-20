;(function(angular){
	"use strict";
	
		
		angular.module("customrecruiterreportmodule",[])
		.controller("customrecruiterreportcontroller", function($scope, $http, $sessionStorage, dateRangeService,$state ){
			
			//$scope.dms = [];
			$scope.usdms = {};
			$scope.recInfoTable = false;
			$scope.recInfo = [];
			$scope.onload = function(){
				
				$scope.dmRanges = JSON.parse(sessionStorage.getItem("dmsDateRange"));
				
				if($scope.dmRanges.site == "onSite")
					{
					$scope.siteName = "On Site";
					}
				else
					{
					$scope.siteName = "Off Shore";
					}
				
				//alert(JSON.stringify($scope.dmRanges ));
				$scope.DateFormat = 'MM-dd-yyyy';
				
				$scope.startDateOptions = {
						date : new Date(),
						showWeeks : false
					};
				$scope.startDateopen = function() {
					$scope.startDatePopup.opened = true;
				};
				$scope.startDatePopup = {
						opened : false
					};
				
				$scope.endDateOptions = {
						date : new Date(),
						showWeeks : false
					};
				$scope.endDateopen = function() {
					$scope.endDatePopup.opened = true;
				};
				$scope.endDatePopup = {
						opened : false
					};
				
				var startDateVal = new Date();
				startDateVal.setDate(1);
				$scope.usdms.startDate = dateRangeService.convertStringToDate($scope.dmRanges.startDate);
				$scope.usdms.endDate = dateRangeService.convertStringToDate($scope.dmRanges.endDate);
				$scope.usdms.status = "ACTIVE";
				$scope.getRecruiterInfo();
//				$scope.dmsInfoTable = true;
//				recsInfoTableView();
			}
			
			
			$scope.getRecruiterInfo = function(){
			
				$scope.recInfoTable = false;
				var from = dateRangeService.formatDate($scope.usdms.startDate);
				var to = dateRangeService.formatDate($scope.usdms.endDate);
				$scope.obj = {
						"startDate" : from,
						"endDate" :to,
						"dmName" : $scope.dmRanges.dmName,
						"status" : $scope.usdms.status
				}
//				alert("data "+JSON.stringify($scope.obj));
				getRecsInfoAsTable();
			}
			
			$scope.backToDms = function(){
				$state.transitionTo("customdmreportmodule");
			}
			
			function getRecsInfoAsTable(){
				if($scope.dmRanges.site == 'onSite'){
				var response = $http.post("customReports/getDmCustomReportData", $scope.obj);
				}else{
					var response = $http.post("indiaReports/getIndiaDmCustomReportData", $scope.obj);	
				}
				
				response.success(function(data, config, headers, status){
					if(data){
//						alert("success "+JSON.stringify(data));
						$scope.seriesData = data.seriesData;
						$scope.categoryData = data.categories;
						$scope.recInfoTable = true;
						recsInfoTableView();
						$scope.recInfo.recTableControl.options.data = data.gridData;
						
						
						$scope.jobOrderOpenClosedCount = data.jobOrderOpenClosedCount;
						$scope.jobOrderPositionsOpenClosedCount = data.jobOrderPositionsOpenClosedCount;
						$scope.submittalOpenClosedCount = data.submittalOpenClosedCount;
						$scope.submittalConfirmedOpenClosedCount = data.submittalConfirmedOpenClosedCount;
						$scope.submittalStarteddOpenClosedCount = data.submittalStarteddOpenClosedCount;
						
						$scope.submittalStarteddActiveCount = data.submittalStarteddActiveCount;
						$scope.submittalStarteddInActiveCount = data.submittalStarteddInActiveCount;
						
						$scope.jobOrderService = data.jobOrderService;
						$scope.jobOrderPositionsService = data.jobOrderPositionsService;
						
						$scope.submittalService = data.submittalService;
						$scope.submittalCnfService = data.submittalCnfService;
						$scope.submittalStartedService = data.submittalStartedService;
						
						$scope.dmsCount = data.noOfDms;
						if($scope.jobOrderOpenClosedCount && $scope.jobOrderPositionsOpenClosedCount){
							 $scope.jobOrdersCount = parseInt(data.jobOrderOpenClosedCount.OPEN)+parseInt(data.jobOrderOpenClosedCount.CLOSED);
							 $scope.positionsCount = parseInt(data.jobOrderPositionsOpenClosedCount.OPEN)+parseInt(data.jobOrderPositionsOpenClosedCount.CLOSED);
						}else{
							 $scope.jobOrdersCount = 0;
							 $scope.positionsCount = 0;
						}
						
						 $scope.submittalsCount = data.noOfSub;
						 $scope.confirmedCount = data.noOfConfirmed;
						 $scope.startedCount = data.noOfStarts;
						 if(data.ACTIVE_INACTIVE){
						 $scope.noOfActiveInActiveRec = data.ACTIVE_INACTIVE;
						 }
						 
						 getRecsInfoAsBar();
					}else{
						$scope.recInfoTable = true;
						recsInfoTableView();
					}
				});
				response.error(function(data, status, headers, config){
					//$(".underlay").hide();
					  if(status == constants.FORBIDDEN){
						location.href = 'login.html';
					  }else{  			  
						$state.transitionTo("ErrorPage",{statusvalue  : status});
					  }
				  });
			}
			
			function getRecsInfoAsBar(){
				$scope.myCharts = Highcharts.chart('containersidebar',{
					
					  chart: {
				            type: 'column'
				        },
				        title: {
				            text: '<span class="charttext">Recruiters Monthly Avg Hires under '+$scope.dmRanges.dmFullName+ '</dpan>'
				        },
				         
				        xAxis: {
				            categories: $scope.categoryData,
				        },
				        yAxis: {
				            min: 0.01,
				            title: {
				                text: 'Avg Hires'
				            }
				        },
				        tooltip: {
				            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
				            pointFormat: '<tr><td style="color:{series.color};padding:0">Avg Hires:</td>' +
				                '<td style="padding:0"><b>{point.y}</b></td></tr>'
				            ,
				            footerFormat: '</table>',
				            shared: true,
				            useHTML: true
				        },
				        plotOptions: {
				        	
				            column: {
				                pointPadding: 0.2,
				                borderWidth: 0
				            }
				        
				        },
				        series: [
//				                  {type:'line',color:'red',zIndex:505,data:[[0,0.75],[7,0.75]]},
				                 {
				        	showInLegend: false,  
				        	
//				        	threshold: 0.75,
				            //name: 'Tokyo',
				            data: $scope.seriesData

				        } ]
				});
			}
			
			function recsInfoTableView(){
				
				 $scope.recInfo.recTableControl = {
		                    options: { 
		                        striped: true,
		                        pagination: true,
		                        paginationVAlign: "bottom", 
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
		                            field: 'dmName',
		                            title: 'Recruiter Name',
		                            align: 'left',
		                            
		                        },{
		                            field: 'level',
		                            title: 'Level',
		                            align: 'left',
		                            
		                        }/*,{
		                            field: 'rank',
		                            title: 'Rank',
		                            align: 'left',
		                            
		                        }*/, {
		                            field: 'noOfJobOrders',
		                            title: '#JobOrders',
		                            align: 'left',
		                           
		                        },{
		                            field: 'noOfPositions',
		                            title: '#Positions',
		                            align: 'left',
		                           
		                        },{
		                            field: 'submittedCount',
		                            title: '#Sbm',
		                            align: 'left',
		                           
		                        },{
		                            field: 'confirmedCount',
		                            title: 'Confirmed',
		                            align: 'left',
		                           
		                        },{
		                            field: 'startedCount',
		                            title: 'Total Starts',
		                            align: 'left',
		                           
		                        },{
		                            field: 'inActiveStartedCount',
		                            title: 'InActive Starts',
		                            align: 'left',
		                           
		                        },{
		                            field: 'avgHires',
		                            title: 'Avg Hires',
		                            align: 'left',
		                           
		                        },{
		                            field: 'avgHires',
		                            title: 'Status',
		                            align: 'left',
		                            formatter : recColorFormatter,
		                           
		                        },]
		                    }
		            };
			}
			
			function recColorFormatter(value, row, index) {  
				 return [
			                '<div  style = "background-color: '+row.color+'; color:#ffffff; text-align: center;"> '+row.performanceStatus+' </div>'
			                ].join(''); 
					 
			     } 
		});
})(angular);