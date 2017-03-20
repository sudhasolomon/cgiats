;(function(angular){
	"use strict";
	
		
		angular.module("dm_clientreportmodule",[])
		.controller("dm_clientreportcontroller", function($scope, $http, $timeout, dateRangeService, $state){
			
			$scope.dmClientWiseSubInfo=[];
				$scope.onload = function()
				{
					$(".caret").addClass("fa fa-caret-down");
					$(".caret").css("font-size", "16px");
					$(".caret").css("color", "#1895ab");
					
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
					
					
					$scope.getAllUsers();
					
					$timeout(function() {
						$scope.getdataforreport();
						 }, 600);
				}


				
				
/*-*************************** GET USERS FUNCTION STARTS HERE********************************************-*/
				$scope.getAllUsers = function(){
					var response = $http.get('commonController/getAllDMAndADM_OfficeLocations?isAuthRequired='+true+'&isActive='+true);
					response.success(function (data,status,headers,config){
						$scope.userRecords = data;
						$scope.uesrList = [];
						for(var i=0; i<data.users.length; i++ )
							{
								var obj = {id: data.users[i].userId, label: data.users[i].fullName};
								$scope.uesrList.push(obj);
							}
						$scope.dm = {id: data.users[0].userId, label: data.users[0].fullName};
						$scope.dmdata = $scope.uesrList;
						$scope.getselectedClientsfromDB();
						
						
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
				
				
/*--******************************** GET CLIENTS FOR SELECTED DM STARTS HERE*************************************************/
				$scope.dmevent = {
						onItemSelect: function(item) {
							$scope.getselectedClientsfromDB();
						},
				onItemDeselect: function(item) {
					$scope.getselectedClientsfromDB();
				},
				onDeselectAll: function(item) {
					$timeout(function() {
						$scope.getselectedClientsfromDB();
						 }, 10);
				}
				};
				
				
				
				$scope.getselectedClientsfromDB = function()
				{
					var from = dateRangeService.formatDate($scope.startDate);
					var to = dateRangeService.formatDate($scope.endDate);
					
					$scope.obj = {
							"startDate" : from,
							"endDate" :to,
							"dmName" : $scope.dm.id
					}
					var response = $http.post("commonController/getClientsUnderDM", $scope.obj);
					response.success(function (data,status,headers,config){
						$scope.userRecords = data;
						
						if(data.length < 1)
							{
							$scope.clients = [];
							$scope.clientData = [];
							}
						else
							{
							$scope.uesrList = [];
							for(var i=0; i<data.length; i++ )
							{
								var obj = {id: data[i], label: data[i]};
								$scope.uesrList.push(obj);
							}
						$scope.clients = [{id: data[0], label: data[0]}];
						$scope.clientData = $scope.uesrList;
						
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
/*--******************************** GET CLIENTS FOR SELECTED DM ENDS HERE*************************************************/

				
				
				$scope.getdataforreport = function(){
					//alert("dm id::"+ $scope.dm.id + "clientname"+ $scope.clients.id);
					$scope.dmClienWiseSubtTable=false; 
					
					var from = dateRangeService.formatDate($scope.startDate);
					var to = dateRangeService.formatDate($scope.endDate);
					$scope.clientforReport = "";
					
					for(var i=0; i < $scope.clients.length; i++)
					{
						$scope.clientforReport += $scope.clients[i].id + ", ";
					}
					
					$scope.obj = {
							"startDate" : from,
							"endDate" :to,
							"dmName" : $scope.dm.id,
							"clients":  $scope.clientforReport
					}
					
					//alert(JSON.stringify($scope.obj));
					var response = $http.post("customReports/getDMClientWiseSubmittals", $scope.obj);
					response.success(function (data,status,headers,config){
						//alert(JSON.stringify(data));
						$scope.dmClienWiseSubtTable = true;
						customDmClientwiseSubTableView();
						$scope.dmClientWiseSubInfo.submittalsTableControl.options.data = data.gridData;
					});
					response.error(function(data, status, headers, config){
						  if(status == constants.FORBIDDEN){
							location.href = 'login.html';
						  }else{  			  
							$state.transitionTo("ErrorPage",{statusvalue  : status});
						  }
					  });
					
					
				}
				
				
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
			                            field: 'recName',
			                            title: 'Recruiter Name',
			                            align: 'left',
			                            
			                        },{
			                            field: 'level',
			                            title: 'Designation',
			                            align: 'left',
			                            
			                        },{
			                             field: 'jobClient',
			                             title: 'Client',
			                             align: 'left',
    
			                        },{
			                            field: 'submittedCount',
			                            title: 'SBM',
			                            align: 'left',
			                           
			                        },{
			                            field: 'interviewingCount',
			                            title: 'Interviewing',
			                            align: 'left',
			                           
			                        },{
			                            field: 'confirmedCount',
			                            title: 'Confirmed',
			                            align: 'left',
			                           
			                        },{
			                            field: 'startedCount',
			                            title: 'Started',
			                            align: 'left',
			                           
			                        }]
			                    }
			            };
				}

				
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