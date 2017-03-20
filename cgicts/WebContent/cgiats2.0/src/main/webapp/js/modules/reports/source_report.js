;(function(angular){
	"use strict";
	
		
		angular.module("sourcereportmodule",[])
		.controller("sourcereportcontroller", function($scope, $http, $timeout, dateRangeService, $state){
			
			$scope.dmClientWiseSubInfo=[];
			$scope.dmClienWiseSubtTable=false; 
				$scope.onload = function()
				{
					$(".caret").addClass("fa fa-caret-down");
					$(".caret").css("font-size", "16px");
					$(".caret").css("color", "#1895ab");
					
					
					var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
					var monthFullName = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
					$scope.selectedYear = new Date().getFullYear();
					$scope.selectedMonth = monthName[new Date().getMonth()];
					$scope.selectedMonthFull = monthFullName[new Date().getMonth()];
					$scope.year = [{id: $scope.selectedYear, label: $scope.selectedYear}];
					$scope.month = [{id: $scope.selectedMonth, label: $scope.selectedMonthFull}];
					$scope.getAllSubmittalYears();
				}


/*-********************************** GET YEARS DATA FUNCTION STARTS HERE******************************************************-*/
				$scope.getAllSubmittalYears = function(){
							

							/*var response = $http.get('reportController/getAllSubmittalYears');
							response.success(function (data,status,headers,config){
								$scope.yearList = [];
								for(var i=0; i<data.length; i++ )
									{
										var obj = {id: data[i], label: data[i]};
										$scope.yearList.push(obj);
									}
								
						        $scope.yeardata = $scope.yearList;
						        $scope.getAllUsers();
								
							});
							response.error(function(data, status, headers, config){
				  			  if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
				  		  });*/
					
					$scope.yeardata = [];
					for(var i=2012;i<=(new Date().getFullYear());i++){
						var obj = {id: i, label: i};
						$scope.yeardata.push(obj);
//						$scope.yeardata.push(i+'');
					}
					 $scope.getAllUsers();
						};
/*-********************************** GET YEARS DATA FUNCTION ENDS HERE******************************************************-*/	
						
						
/*-*************************** GET USERS FUNCTION STARTS HERE********************************************-*/
						$scope.getAllUsers = function(){
							var response = $http.get('commonController/getAllDMAndADM_OfficeLocations?isAuthRequired='+true);
							response.success(function (data,status,headers,config){
								if(data){
								$scope.userRecords = data;
								$scope.uesrList = [];
								for(var i=0; i<data.users.length; i++ )
									{
										var obj = {id: data.users[i].userId, label: data.users[i].fullName};
										$scope.uesrList.push(obj);
									}
								$scope.dm = [{id: data.users[0].userId, label: data.users[0].fullName}];
								$scope.dmdata = $scope.uesrList;
								$scope.getdataforreport();
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
/*-*************************** GET USERS FUNCTION ENDS HERE********************************************-*/
						
				
				

				

				
/*-*************************** GET DATA FOR REPORT FUNCTION ENDS HERE********************************************-*/				
						
						$scope.getdataforreport = function(){
							
							$scope.dmClienWiseSubtTable=false; 
							
							$scope.yearforReport = "";
							$scope.monthforReport = "";
							$scope.dmforReport = "";
							
							for(var i=0; i < $scope.year.length; i++)
							{
							$scope.yearforReport += $scope.year[i].id + ", ";
							}
							for(var i=0; i < $scope.month.length; i++)
							{
							$scope.monthforReport += $scope.month[i].id + ", ";
							}
							for(var i=0; i < $scope.dm.length; i++)
							{
							$scope.dmforReport += $scope.dm[i].id + ", ";
							}
							
							var obj = {year: $scope.yearforReport, month: $scope.monthforReport, name: $scope.dmforReport};
//							alert(JSON.stringify(obj));
							
							
							
							var response = $http.post('totalReportController/getCandidateSourceDetails', obj);
							response.success(function (data,status,headers,config){

//								alert(JSON.stringify(data));
								if(data){
									$scope.dmClienWiseSubtTable=true;
									customDmClientwiseSubTableView();
									$scope.dmClientWiseSubInfo.submittalsTableControl.options.data = data.gridData;
									
									
									$scope.noOfSources = data.noOfSources;
									$scope.noOfSub = data.noOfSub;
									$scope.noOfAccepted = data.noOfAccepted;
									$scope.noOfInterviewing = data.noOfInterviewing;
									$scope.noOfConfirmed = data.noOfConfirmed;
									$scope.noOfRejected = data.noOfRejected;
									$scope.noOfStarts = data.noOfStarts;
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
				
/*-*************************** GET DATA FOR REPORT FUNCTION ENDS HERE********************************************-*/
				
				
/*-************************************************** TABLE FUNCTION ENDS HERE********************************************-*/
				function customDmClientwiseSubTableView(){
					 $scope.dmClientWiseSubInfo.submittalsTableControl = {
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
			                            field: 'source',
			                            title: 'Source',
			                            align: 'left',
			                            
			                        },{
			                            field: 'SUBMITTED',
			                            title: 'SUBMITTED',
			                            align: 'left',
			                            
			                        },{
			                            field: 'ACCEPTED',
			                            title: 'ACCEPTED',
			                            align: 'left',
			                           
			                        },{
			                            field: 'INTERVIEWING',
			                            title: 'INTERVIEWING',
			                            align: 'left',
			                           
			                        },{
			                            field: 'CONFIRMED',
			                            title: 'CONFIRMED',
			                            align: 'left',
			                           
			                        },{
			                            field: 'REJECTED',
			                            title: 'REJECTED',
			                            align: 'left',
			                           
			                        },{
			                            field: 'STARTED',
			                            title: 'STARTED',
			                            align: 'left',
			                           
			                        },{
			                            field: 'inactiveStarted',
			                            title: 'Inactive Started',
			                            align: 'left',
			                           
			                        }]
			                    }
			            };
				}
/*-************************************************** TABLE FUNCTION ENDS HERE********************************************-*/
				
				
				
				
				
				
				
				
				
				$scope.monthdata = [
			       					{id: "Jan", label: "January"},
			       					{id: "Feb", label: "February"},
			       					{id: "Mar", label: "March"},
			       					{id: "Apr", label: "April"},
			       					{id: "May", label: "May"},
			       					{id: "Jun", label: "June"},
			       					{id: "Jul", label: "July"},
			       					{id: "Aug", label: "August"},
			       					{id: "Sep", label: "September"},
			       					{id: "Oct", label: "October"},
			       					{id: "Nov", label: "November"},
			       					{id: "Dec", label: "December"}];
				
				
				
				$scope.manysettings = {
			            smartButtonMaxItems: 3,
			            smartButtonTextConverter: function(itemText, originalItem) {
			                return itemText;
			            },
			        };
				
				
				$scope.manysearchsettings = {
						enableSearch: true,
			            smartButtonMaxItems: 3,
			            smartButtonTextConverter: function(itemText, originalItem) {
			                return itemText;
			            },
			        };
				
				$scope.onesettings = {
					    selectionLimit: 1,
					    smartButtonMaxItems: 1,
					             smartButtonTextConverter: function(itemText, originalItem) {
					                 return itemText;
					             },
					         };
	
		
				$scope.onesearchsettings = {
					    selectionLimit: 1,
					    enableSearch: true,
					    smartButtonMaxItems: 1,
					             smartButtonTextConverter: function(itemText, originalItem) {
					                 return itemText;
					             },
					         };
				
				
				
				
			
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
			
		});
})(angular);