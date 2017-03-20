;(function(angular){
	
	"use strict";
		
				angular.module("recruiterreportmodule", [])
				.controller("recruitersreportcontroller", function($scope, $http, $timeout){


					$scope.usReport = [];
					$scope.usReportTable = false;
					
					$scope.indiaReport = [];
					$scope.indiaReportTable = false;
					
					$scope.onload = function(){
						
						$scope.indiaReportsLists();
						$scope.usReportsLists();
					}
					
					
					$scope.indiaReportsLists = function(){
					 
							var response = $http.get('indiaReports/getAllIndiaSubmittalYears');
							response.success(function (data,status,headers,config){
								$scope.indiayearList = data;
								
							});
							response.error(function(data, status, headers, config){
				  			  if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
				  		  });
							
							
								var response = $http.get('IndiaCommonController/getAllIndiaDMsNoStatus');
								response.success(function (data,status,headers,config){
									$scope.indiauserRecords = data;
								});
								response.error(function(data, status, headers, config){
					  			  if(status == constants.FORBIDDEN){
					  				location.href = 'login.html';
					  			  }else{  			  
					  				$state.transitionTo("ErrorPage",{statusvalue  : status});
					  			  }
					  		  });
					}
					
					$scope.usReportsLists = function(){
						var response = $http.get('reportController/getAllSubmittalYears');
						response.success(function (data,status,headers,config){
							$scope.usyearList = data;
							
						});
						response.error(function(data, status, headers, config){
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
							var response = $http.get('IndiaCommonController/getAllDMsNoStatus');
							response.success(function (data,status,headers,config){
								$scope.ususerRecords = data;
							});
							response.error(function(data, status, headers, config){
				  			  if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
				  		  });
							$scope.usReportTable = true;
							$scope.indiaReportTable = true;
							indiaReportsTableView();
							usReportsTableView();
					}
					
					$scope.indiaRecruiterReports = function(){
						$scope.searchFields= {
								"year" : (new Date().getFullYear()),
								"month" : $("#indiaMonth").val(),
								"status" : $("#indiaStatus").val(),
								"dmName" : "In_Vkrec,In_Vkdm"
						}
						
						alert(JSON.stringify($scope.searchFields));
						var response = $http.post("indiaReports/getIndiaRecruitersProductivity", $scope.searchFields);
						response.success(function(data,status,config,headers){
							$scope.indiaReportTable = true;
							alert(JSON.stringify(data));
							indiaReportsTableView();
							$scope.indiaReport.indiaBsTableControl.options.data = data;
						});
						response.error(function(data,status,config,headers){
							alert("error  "+ JSON.stringify(data));
						});
					}
					$scope.usRecruiterReports = function(){
						$scope.searchFields= {
								"year" : (new Date().getFullYear()),
								"month" : $("#indiaMonth").val(),
								"status" : $("#indiaStatus").val(),
								"dmName" : "solomon,sachin,Rocky,Devasya"
						}
						alert(JSON.stringify($scope.searchFields));
						var response = $http.post("reportController/getRecruitersProductivity", $scope.searchFields);
						response.success(function(data,status,config,headers){
							$scope.usReportTable = true;
							alert(JSON.stringify(data));
							usReportsTableView();
							$scope.usReport.usBsTableControl.options.data = data;
						});
						response.error(function(data,status,config,headers){
							alert("error  "+ JSON.stringify(data));
						});
					}
					
					
					function indiaReportsTableView(){
						
						 $scope.indiaReport.indiaBsTableControl = {
				                    options: { 
				                        striped: true,
				                        pagination: true,
				                        paginationVAlign: "both", 
				                        pageList: [50,100,200],
				                        search: false,
				                        silentSort: false,
				                        showColumns: false,
				                        showRefresh: false,
				                        clickToSelect: false,
				                        showToggle: false,
				                        maintainSelected: true, 
				                        showFooter : false,
				                        columns: [
				                        {
				                            field: 'Name',
				                            title: 'Recruiter Name',
				                            //align: 'left',
				                            
				                        }, {
				                            field: 'noOfStarts',
				                            title: 'No Of Starts',
				                           // align: 'left',
				                            //bgcolor: 'red'
				                           
				                        },{
				                            field: 'avgDays',
				                            title: 'Avg Per Month',
				                            //align: 'left',
				                           
				                        },{
				                            field: 'avgTime',
				                            title: 'Avg Time Taken',
				                           // align: 'left',
				                           
				                        }]
				                    }
				            };
			    	
						
					}
					function usReportsTableView(){
						

			    		
			            $scope.usReport.usBsTableControl = {
			                    options: { 
			                        striped: true,
			                        pagination: true,
			                        paginationVAlign: "both", 
			                        pageList: [50,100,200],
			                        search: false,
			                        silentSort: false,
			                        showColumns: false,
			                        showRefresh: false,
			                        clickToSelect: false,
			                        showToggle: false,
			                        maintainSelected: true, 
			                        showFooter : false,
			                        columns: [
										{
										    field: 'Name',
										    title: 'Recruiter Name',
										    align: 'left',
										    bcolor:'color'
										    
										}, {
										    field: 'noOfStarts',
										    title: 'No Of Starts',
										    align: 'left',
										    bcolor:'color'
										   
										},{
										    field: 'avgDays',
										    title: 'Avg Per Month',
										    align: 'left',
										    bcolor:'color'
										   
										},{
										    field: 'avgTime',
										    title: 'Avg Time Taken',
										    align: 'left',
										   
										}]
			                    }
			            };
			    	
					}
					
				});
})(angular);