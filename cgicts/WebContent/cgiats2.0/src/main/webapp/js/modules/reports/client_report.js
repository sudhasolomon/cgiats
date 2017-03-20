;(function(angular){
	"use strict";
	
		
		angular.module("clientsreportmodule",[])
		.controller("clientsreportcontroller", function($scope, $http, $sessionStorage, dateRangeService, $state,$rootScope){
			
			$scope.currentDateWithTime = new Date();
			$scope.jobOrderInfo = [];
			//$scope.customJobOrderTable = false;
			//$scope.startDate = new Date();
			/*var startDateVal = new Date();
			startDateVal.setDate(1);
			$scope.startDate = startDateVal;
			$scope.endDate = new Date();*/
			//$scope.obj = {};
			$scope.errMsg = false;
			$scope.onload = function(){
				
				$scope.tabfunction();
				if(($rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM)){
					$scope.sitename = "Off Shore";
				}else{
				$scope.sitename = "On Site";
				}
				
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
				$scope.clientRange = JSON.parse(sessionStorage.getItem("clientDateRange"));
				$scope.clientRangeinclient = JSON.parse(sessionStorage.getItem("clientDateRange"));
				
				//alert($scope.clientRangeinclient.site);
				
				if($scope.clientRangeinclient == "" || $scope.clientRangeinclient == undefined || $scope.clientRangeinclient == null)
				{
				}
			else
				{
				//alert("SITE NAME" + $scope.clientRangeinclient.site);
					if($scope.clientRangeinclient.site == "On Site")
					{
						$scope.sitename = $scope.clientRangeinclient.site;
						$("div[data-tabname='onsite']").addClass("activetab");
						$("div[data-tabname='ofshore']").removeClass("activetab");
						$("#onsite").show();
						$("#ofshore").hide();
						$("#usfilter").show();
						$("#infilter").hide();
						
					}
					if($scope.clientRangeinclient.site == "Off Shote")
					{
						$scope.sitename = $scope.clientRangeinclient.site;
						$("div[data-tabname='onsite']").removeClass("activetab");
						$("div[data-tabname='ofshore']").addClass("activetab");
						$("#onsite").hide();
						$("#ofshore").show();
						$("#usfilter").hide();
						$("#infilter").show();
					}
				}
				
				
				//alert("client range"+JSON.stringify($scope.clientRange));
				if($scope.clientRange){
					$scope.startDate = dateRangeService.convertStringToDate($scope.clientRange.startDate);;
					$scope.endDate = dateRangeService.convertStringToDate($scope.clientRange.endDate);;
				}else{
					var startDateVal = new Date();
					startDateVal.setDate(1);
					$scope.startDate = startDateVal;
					$scope.endDate = new Date();
				}
				
				
				$scope.getJobOrders();
				}
			
			$scope.getJobOrders = function(){
				/*$scope.customJobOrderTable = true;
				customJobOrderTableView();
				$scope.jobOrderInfo.jobOrderTableControl.options.data="";*/
				$scope.customJobOrderTable = false;
				var startDate = dateRangeService.formatDate($scope.startDate);
				var endDate =  dateRangeService.formatDate($scope.endDate);
				
				var dtStartDate = dateRangeService.convertStringToDate(startDate);
				var dtEndDate =  dateRangeService.convertStringToDate(endDate); 
				$scope.obj = {
						"startDate" : startDate,
						"endDate" : endDate
				};
				//alert($scope.startDate + "  "+ $scope.endDate);
//				alert(JSON.stringify($scope.obj));
//				alert(dtStartDate+'\n'+dtEndDate);
				if(dtStartDate && dtEndDate && dtStartDate <= dtEndDate){
				var response = $http.post("customReports/getClientReportData", $scope.obj);
				response.success(function(data, config, headers, status){
					//alert(JSON.stringify(data));
					if(data){
						$scope.errMsg=false;
						/*$scope.clientWiseSbmList = data.gridData; 
						$scope.exportClientWiseSbmList = [];
						
						for (var i = 0; i <data.gridData.length; i++) {
							var obj = {jobOrderId:data.gridData[i].orderId,
									CreatedDate: data.gridData[i].createdOn,ClientName: data.gridData[i].jobClient,
									Title: data.gridData[i].jobTitle,DMName:data.gridData[i].dmName,AssignedTo: data.gridData[i].assignedTo,Positions: data.gridData[i].noOfPositions,
									SBM: data.gridData[i].submittedCount,Confirmed: data.gridData[i].confirmedCount,
									Started: data.gridData[i].startedCount,NetPositions: data.gridData[i].netPositions};
							$scope.exportClientWiseSbmList.push(obj);
						}*/
						$scope.noOfClients = data.noOfClients;
						$scope.noOfJobs = data.noOfJobs;
						$scope.noOfStarts = data.noOfStarts;
						$scope.noOfPos = data.noOfPos;
						$scope.netPositions = data.netPositions;
						$scope.noOfSub = data.noOfSub;
						$scope.noOfConfirmed = data.noOfConfirmed;
						$scope.customJobOrderTable = true;
						customJobOrderTableView();
						$scope.jobOrderInfo.jobOrderTableControl.options.data=data.gridData;
					}else{
						$scope.customJobOrderTable = true;
						customJobOrderTableView();
					}
				});
				response.error(function(data, status, headers, config){
					  if(status == constants.FORBIDDEN){
						location.href = 'login.html';
					  }else{  			  
						$state.transitionTo("ErrorPage",{statusvalue  : status});
					  }
				  });
				}else{
					$scope.errMsg=true;
					$scope.customJobOrderTable = true;
				}
			}
			
			$scope.getTimeFnc = function(){
				$scope.currentDateWithTime = new Date();
			}
			
			function customJobOrderTableView(){
				 $scope.jobOrderInfo.jobOrderTableControl = {
		                    options: { 
		                        striped: true,
		                        pagination: true,
		                        paginationVAlign: "bottom", 
		                        pageList: [10,20,50],
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
		                        	field: 'jobClient',
		                        	title: 'Client Name',
		                        	events : window.USclientEvents,
									formatter : USclientFormatter,
		                        	align: 'left',
		                        	
		                        },
		                        {
		                            field: 'noOfJobOrders',
		                            title: '# Job Orders',
		                            align: 'left',
		                            
		                        },{
		                            field: 'noOfPositions',
		                            title: '# Positions',
		                            align: 'left',
		                           
		                        },{
		                            field: 'submittedCount',
		                            title: 'SBM',
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
		                            field: 'netPositions',
		                            title: 'Remaining Positions',
		                            align: 'left',
		                           
		                        }/*{
		                            field: 'startedDate',
		                            title: 'Started Date',
		                            align: 'left',
		                           
		                        }*/]
		                    }
		            };
			}
			
			function USclientFormatter(value, row, index) {  
				 
		        return [ 
		        '<a class="clientName actionIcons" title="" flex-gt-md="auto" >'+row.jobClient+'</a>', 
		        ].join(''); 
				 
		     } 
		     
		     window.USclientEvents =  {
		             'click .clientName': function (e, value, row, index) {
		            	 getUSClientData(row.jobClient);
		              },
		     };
		     
		     function getUSClientData(client){
		    	 $scope.obj.clientName = client;
		    	 $scope.obj.site = "On Site";
            	 $scope.clientRange = JSON.stringify($scope.obj);
            	 sessionStorage.setItem("clientDateRange", $scope.clientRange);
            	$state.transitionTo("clientDetailsreportmodule");
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
				
				$scope.showfiltersIN = function()
				{
					if($("#filterfieldsIN").is(":visible"))
					{
					$("#filterfieldsIN").slideUp();
					}
				else
					{
					$("#filterfieldsIN").slideDown();
					}
					
				}
				
				
				
				
				
				
				


				
				
				$scope.tabfunction = function()
				{
					$("div[data-click='tabitem']").click(function(){
						var clickedbuttonname = $(this).attr("data-tabname");
						$(".tabsmain div").removeClass("activetab");
						if(clickedbuttonname == "onsite")
							{
							$scope.sitename = "On Site";
							$(this).addClass("activetab");
							$("#onsite").show();
							$("#ofshore").hide();
							$("#usfilter").show();
							$("#infilter").hide();
							}
						if(clickedbuttonname == "ofshore")
							{
							$scope.sitename = "Off Shore";
							$(this).addClass("activetab");
							$("#onsite").hide();
							$("#ofshore").show();
							$("#usfilter").hide();
							$("#infilter").show();
							}
					});
				}
				
				
				
		})
		
		
		
		
		
		
		
		
		
		
		
		.controller("INclientsreportcontroller", function($scope, $http, $sessionStorage, dateRangeService, $state,$rootScope){
			
			$scope.currentDateWithTime = new Date();
			$scope.jobOrderInfo = [];
			//$scope.customJobOrderTable = false;
			//$scope.startDate = new Date();
			/*var startDateVal = new Date();
			startDateVal.setDate(1);
			$scope.startDate = startDateVal;
			$scope.endDate = new Date();*/
			//$scope.obj = {};
			$scope.errMsg = false;
			$scope.onload = function(){
				
				$scope.DateFormat = 'MM-dd-yyyy';
				
				if(($rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM)){
					$scope.sitename = "Off Shore";
					$(this).addClass("activetab");
					$("#onsite").hide();
					$("#ofshore").show();
					$("#usfilter").hide();
					$("#infilter").show();
					}
				
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
				$scope.clientRange = JSON.parse(sessionStorage.getItem("clientDateRange"));
				//alert("client range"+JSON.stringify($scope.clientRange));
				if($scope.clientRange){
					$scope.startDate = dateRangeService.convertStringToDate($scope.clientRange.startDate);;
					$scope.endDate = dateRangeService.convertStringToDate($scope.clientRange.endDate);;
				}else{
					var startDateVal = new Date();
					startDateVal.setDate(1);
					$scope.startDate = startDateVal;
					$scope.endDate = new Date();
				}
				
				
				$scope.getJobOrders();
				}
			
			$scope.getJobOrders = function(){
				/*$scope.customJobOrderTable = true;
				customJobOrderTableView();
				$scope.jobOrderInfo.jobOrderTableControl.options.data="";*/
				$scope.customJobOrderTable = false;
				var startDate = dateRangeService.formatDate($scope.startDate);
				var endDate =  dateRangeService.formatDate($scope.endDate);
				
				var dtStartDate = dateRangeService.convertStringToDate(startDate);
				var dtEndDate =  dateRangeService.convertStringToDate(endDate); 
				$scope.obj = {
						"startDate" : startDate,
						"endDate" : endDate
				};
				//alert($scope.startDate + "  "+ $scope.endDate);
//				alert(JSON.stringify($scope.obj));
//				alert(dtStartDate+'\n'+dtEndDate);
				if(dtStartDate && dtEndDate && dtStartDate <= dtEndDate){
				var response = $http.post("indiaReports/getIndiaClientReportData", $scope.obj);
				response.success(function(data, config, headers, status){
					//alert(JSON.stringify(data));
					if(data){
						$scope.errMsg=false;
						/*$scope.clientWiseSbmList = data.gridData; 
						$scope.exportClientWiseSbmList = [];
						
						for (var i = 0; i <data.gridData.length; i++) {
							var obj = {jobOrderId:data.gridData[i].orderId,
									CreatedDate: data.gridData[i].createdOn,ClientName: data.gridData[i].jobClient,
									Title: data.gridData[i].jobTitle,DMName:data.gridData[i].dmName,AssignedTo: data.gridData[i].assignedTo,Positions: data.gridData[i].noOfPositions,
									SBM: data.gridData[i].submittedCount,Confirmed: data.gridData[i].confirmedCount,
									Started: data.gridData[i].startedCount,NetPositions: data.gridData[i].netPositions};
							$scope.exportClientWiseSbmList.push(obj);
						}*/
						$scope.noOfClients = data.noOfClients;
						$scope.noOfJobs = data.noOfJobs;
						$scope.noOfStarts = data.noOfStarts;
						$scope.noOfPos = data.noOfPos;
						$scope.netPositions = data.netPositions;
						$scope.noOfSub = data.noOfSub;
						$scope.noOfConfirmed = data.noOfConfirmed;
						$scope.customJobOrderTable = true;
						customJobOrderTableView();
						$scope.jobOrderInfo.jobOrderTableControl.options.data=data.gridData;
					}else{
						$scope.customJobOrderTable = true;
						customJobOrderTableView();
					}
				});
				response.error(function(data, status, headers, config){
					  if(status == constants.FORBIDDEN){
						location.href = 'login.html';
					  }else{  			  
						$state.transitionTo("ErrorPage",{statusvalue  : status});
					  }
				  });
				}else{
					$scope.errMsg=true;
					$scope.customJobOrderTable = true;
				}
			}
			
			$scope.getTimeFnc = function(){
				$scope.currentDateWithTime = new Date();
			}
			
			function customJobOrderTableView(){
				 $scope.jobOrderInfo.jobOrderTableControl = {
		                    options: { 
		                        striped: true,
		                        pagination: true,
		                        paginationVAlign: "bottom", 
		                        pageList: [10,20,50],
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
		                        	field: 'jobClient',
		                        	title: 'Client Name',
		                        	events : window.clientEvents,
									formatter : clientFormatter,
		                        	align: 'left',
		                        	
		                        },
		                        {
		                            field: 'noOfJobOrders',
		                            title: '# Job Orders',
		                            align: 'left',
		                            
		                        },{
		                            field: 'noOfPositions',
		                            title: '# Positions',
		                            align: 'left',
		                           
		                        },{
		                            field: 'submittedCount',
		                            title: 'SBM',
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
		                            field: 'netPositions',
		                            title: 'Remaining Positions',
		                            align: 'left',
		                           
		                        }/*{
		                            field: 'startedDate',
		                            title: 'Started Date',
		                            align: 'left',
		                           
		                        }*/]
		                    }
		            };
			}
			
			function clientFormatter(value, row, index) {  
				 
		        return [ 
		        '<a class="clientName actionIcons" title="" flex-gt-md="auto" >'+row.jobClient+'</a>', 
		        ].join(''); 
				 
		     } 
		     
		     window.clientEvents =  {
		             'click .clientName': function (e, value, row, index) {
		            	 getClientData(row.jobClient);
		              },
		     };
		     
		     function getClientData(client){
		    	 $scope.obj.clientName = client;
		    	 $scope.obj.site = "Off Shore";
            	 $scope.clientRange = JSON.stringify($scope.obj);
            	 sessionStorage.setItem("clientDateRange", $scope.clientRange);
            	$state.transitionTo("clientDetailsreportmodule");
		     }
		     
		     
		     
		     
		
				
				
				
				
				
		});
		
		
})(angular);