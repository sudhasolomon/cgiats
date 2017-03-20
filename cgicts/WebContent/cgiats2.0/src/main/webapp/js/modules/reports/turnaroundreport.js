;(function(angular){
	
	
	
	"use strict";
	
				angular.module("turnaroundemodule" , [])
				.controller("turnAroundController" , function($scope, $http, dateRangeService){
					$scope.test = "turn around reports test";
					
					$scope.turnAroundTimeTable = false;
					$scope.turnAroundTimeInfo = [];
					
					$scope.onload = function(){
						
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
						$scope.startDate = startDateVal;
						$scope.endDate = new Date();
						
						$scope.getAllDMs();
						
						
						
						 
						//alert("client range"+JSON.stringify($scope.clientRange));
					 
							/*$scope.startDate = dateRangeService.convertStringToDate($scope.clientRange.startDate);;
							$scope.endDate = dateRangeService.convertStringToDate($scope.clientRange.endDate);;
						 
							var startDateVal = new Date();
							startDateVal.setDate(1);
							$scope.startDate = startDateVal;
							$scope.endDate = new Date();*/
					 
					 
						}
					
					$scope.getTurnAroundData = function(){
						$scope.turnAroundTimeTable = false;
						var startDate = dateRangeService.formatDate($scope.startDate);
						var endDate =  dateRangeService.formatDate($scope.endDate);
						
						$scope.obj = {
								"startDate" : startDate,
								"endDate" : endDate,
								"dmName"  : $scope.dmName
						};
						turnAroundDateFunction();
					}
					
					function turnAroundDateFunction(){
						var response = $http.post("reportController/getTurnAroundReportData", $scope.obj);
						response.success(function(data, config, headers, status){
							if(data){
							$scope.turnAroundTimeTable = true;
							turnAroundTimeTableView();
							$scope.turnAroundTimeInfo.turnAroundTableControl.options.data = data;
							}
							else{
								$scope.turnAroundTimeTable = true;
								turnAroundTimeTableView();
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
					
					
					function turnAroundTimeTableView(){
						 $scope.turnAroundTimeInfo.turnAroundTableControl = {
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
				                        	field: 'orderId',
				                        	title: 'Job Order Id',
				                        	align: 'left',
				                        	
				                        },
				                        {
				                            field: 'jobClient',
				                            title: 'Client',
				                            align: 'left',
				                            
				                        },{
				                            field: 'jobTitle',
				                            title: 'Title',
				                            align: 'left',
				                           
				                        },{
				                            field: 'dmName',
				                            title: 'DM Name',
				                            align: 'left',
				                           
				                        },{
				                            field: 'createdOn',
				                            title: 'Created On',
				                            align: 'left',
				                           
				                        },{
				                            field: 'noOfResumesRequired',
				                            title: '#Resumes Required',
				                            align: 'center',
				                           
				                        },{
				                            field: 'submittedCount',
				                            title: '#Submitted',
				                            align: 'center',
				                           
				                        },{
				                            field: 'turnAroundTime',
				                            title: 'TAT (Hours)',
				                            align: 'center',
				                           
				                        }]
				                    }
				            };
					}
					
					
					$scope.showfilters = function()
					{
						if($("#filterfields").is(":visible"))
							{
							$("#filterfields").slideUp();
							}
						else
							{
							$("#filterfields").slideDown();
							}
						
					}
					
					$scope.getAllDMs = function(){
						var response = $http.get('commonController/getAllDMs');
						response.success(function (data,status,headers,config){
							$scope.dmList = data;
							$scope.dmName =  "ashrivastava";
							$scope.getTurnAroundData();
						});
						response.error(function(data, status, headers, config){
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
					};
				})
})(angular);