;(function(angular){
	"use strict";
	
	angular.module("recmodule", [])
	.controller("recsreportcontroller", function($scope, $http,$timeout,$state, $interval){
		var yesterDayData=[];
		var lastWeekData=[];
		var thisMonthData = [];
		$scope.yesterdayReports = [];
		$scope.lastweekReports = [];
		$scope.thisMonthReports = [];
		$scope.export = [];
		$scope.onload = function()
		{
			$scope.pagename = "New Rec's Report";
			$scope.getAllDMsAndCities();
		
			
		}
		
		
	
		
		
		$scope.getAllDMsAndCities = function(){
			$scope.cityList = [];
			var response = $http.get('commonController/getAllDMAndADM_OfficeLocations?isActive='+true);
			response.success(function (data,status,headers,config){
				if(data){
				$scope.dmname = data.users;
//				$scope.cityList = data.cities;
				
				var tempObj = {userId:constants.ALL,fullName:constants.ALL};
			    $scope.dmname.splice(0, 0, tempObj);
			$scope.selectedCurrentDM = constants.ALL;
			
			
				$scope.selectedCurrentDM = data.users[0].userId;
				$scope.selectedCity = constants.HYD;
				var categories = [];

				$.each(data.cities, function(index, value) {
//				    if ($.inArray(value.officeLocation, categories) === -1) {
//				        categories.push(value.officeLocation);
					var obj={officeLocation:value};
				        $scope.cityList.push(obj);
//				    }
				});
				
				$timeout(function() {
					$scope.getReport($scope.selectedCurrentDM);
					 }, 100);
				}
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
		};
		
		 
		 $scope.onchangeDm = function(selectedCurrentDM){
			 $scope.getReport(selectedCurrentDM);
		 };
		 
		$scope.getReport = function(selectedCurrentDM){
			$scope.RecruitersReportTable = false;
			var response = $http.get('reportController/getAllSubmittalRecruitersReport?dmName='+selectedCurrentDM);
			response.success(function (data,status,headers,config){
				if(data){
						yesterDayData = data.yesterDay.list;
						lastWeekData = data.lastWeek.list;
						thisMonthData = data.currentMonth.list;
						$scope.RecruitersReportTable = true;
						dispalyRecruitersYesterdayReportTable();
						dispalyRecruiterslastweekReportTable();
						dispalyRecruitersthismonthReportTable();
						dispalyRecruitersexportReportTable();
						$scope.yesterdayTotal = data.yesterDay.total;
						$scope.lastWeekTotal = data.lastWeek.total;
						$scope.thisMonthTotal = data.currentMonth.total;
						$scope.cll();
						
						
						$timeout(function() {
						$("#exportThisMonth .fixed-table-loading").remove();
						$("#exportThisMonth table thead tr").css("background-color", "#3598dc");
						$("#exportThisMonth table thead tr").css("color", "#ffffff");
						 }, 100);
				}
			
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
		};
		
		
		
	
		
		
		
		function dispalyRecruitersYesterdayReportTable() {
			$scope.yesterdayReports.reportTableControl= {
				options : {
					data : yesterDayData || {},
					striped : true,
					pagination : true,
					paginationVAlign : "top",
					sidePagination : 'client',
					silentSort: false,
					pageList : [ 10, 20, 50 ],
					search : false,
					showColumns : false,
					showRefresh : false,
					showExport:false,
					showFooter : false,
					clickToSelect : false,
					showToggle : false,
					columns : [
							{
								field : 'Name',
								title : 'Name',
								align : 'left',
								sortable : true
							},
							{
								field : 'Location',
								title : 'Location',
								align : 'left',
								sortable : true
							},
							{
								field : 'SUBMITTED',
								title : 'SUBM',
								align : 'center',
								sortable : true
							},
							{
								field : 'DMREJ',
								title : 'DMREJ',
								align : 'center',
								sortable : true
							},
							{
								field : 'ACCEPTED',
								title : 'ACCEPT',
								align : 'center',
								sortable : true
							},
							{
								field : 'INTERVIEWING',
								title : 'INTVIEW',
								align : 'center',
								sortable : true
							},
							{
								field : 'CONFIRMED',
								title : 'CONF',
								align : 'center',
								sortable : true
							},
							{
								field : 'REJECTED',
								title : 'REJ',
								align : 'center',
								sortable : true
							},
							{
								field : 'STARTED',
								title : 'START',
								align : 'center',
								sortable : true
							},
							{
								field : 'BACKOUT',
								title : 'BACKOUT',
								align : 'center',
								sortable : true
							},
							{
								field : 'OUTOFPROJ',
								title : 'OOP',
								align : 'center',
								sortable : true
							},
							{
								field : 'NotUpdated',
								title : 'NU',
								align : 'center',
								sortable : true
							}],

					},
			
				
				}
			};
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			function dispalyRecruiterslastweekReportTable() {
				$scope.lastweekReports.reportTableControl= {
					options : {
						data : lastWeekData || {},
						striped : true,
						pagination : true,
						paginationVAlign : "top",
						sidePagination : 'client',
						silentSort: false,
						pageList : [ 10, 20, 50 ],
						search : false,
						showColumns : false,
						showRefresh : false,
						showExport:false,
						showFooter : false,
						clickToSelect : false,
						showToggle : false,
						columns : [
								{
									field : 'Name',
									title : 'Name',
									align : 'left',
									sortable : true
								},
								{
									field : 'Location',
									title : 'Location',
									align : 'left',
									sortable : true
								},
								{
									field : 'SUBMITTED',
									title : 'SUBM',
									align : 'center',
									sortable : true
								},
								{
									field : 'DMREJ',
									title : 'DMREJ',
									align : 'center',
									sortable : true
								},
								{
									field : 'ACCEPTED',
									title : 'ACCEPT',
									align : 'center',
									sortable : true
								},
								{
									field : 'INTERVIEWING',
									title : 'INTVIEW',
									align : 'center',
									sortable : true
								},
								{
									field : 'CONFIRMED',
									title : 'CONF',
									align : 'center',
									sortable : true
								},
								{
									field : 'REJECTED',
									title : 'REJ',
									align : 'center',
									sortable : true
								},
								{
									field : 'STARTED',
									title : 'START',
									align : 'center',
									sortable : true
								},
								{
									field : 'BACKOUT',
									title : 'BACKOUT',
									align : 'center',
									sortable : true
								},
								{
									field : 'OUTOFPROJ',
									title : 'OOP',
									align : 'center',
									sortable : true
								},
								{
									field : 'NotUpdated',
									title : 'NU',
									align : 'center',
									sortable : true
								}],

						},
				
					
					}
				};
				
				
				
				
				
				
				
				
				
				
				function dispalyRecruitersthismonthReportTable() {
					$scope.thisMonthReports.reportTableControl= {
						options : {
							data : thisMonthData || {},
							striped : true,
							pagination : true,
							paginationVAlign : "top",
							sidePagination : 'client',
							silentSort: false,
							pageList : [ 10, 20, 50 ],
							search : false,
							showColumns : false,
							showRefresh : false,
							showExport:false,
							showFooter : false,
							clickToSelect : false,
							showToggle : false,
							columns : [
									{
										field : 'Name',
										title : 'Name',
										align : 'left',
										sortable : true
									},
									{
										field : 'Location',
										title : 'Location',
										align : 'left',
										sortable : true
									},
									{
										field : 'SUBMITTED',
										title : 'SUBM',
										align : 'center',
										sortable : true
									},
									{
										field : 'DMREJ',
										title : 'DMREJ',
										align : 'center',
										sortable : true
									},
									{
										field : 'ACCEPTED',
										title : 'ACCEPT',
										align : 'center',
										sortable : true
									},
									{
										field : 'INTERVIEWING',
										title : 'INTVIEW',
										align : 'center',
										sortable : true
									},
									{
										field : 'CONFIRMED',
										title : 'CONF',
										align : 'center',
										sortable : true
									},
									{
										field : 'REJECTED',
										title : 'REJ',
										align : 'center',
										sortable : true
									},
									{
										field : 'STARTED',
										title : 'START',
										align : 'center',
										sortable : true
									},
									{
										field : 'BACKOUT',
										title : 'BACKOUT',
										align : 'center',
										sortable : true
									},
									{
										field : 'OUTOFPROJ',
										title : 'OOP',
										align : 'center',
										sortable : true
									},
									{
										field : 'NotUpdated',
										title : 'NU',
										align : 'center',
										sortable : true
									}],

							},
					
						
						}
					};
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					function dispalyRecruitersexportReportTable() {
						$scope.export.reportTableControl= {
							options : {
								data : thisMonthData || {},
								striped : true,
								pagination : false,
								paginationVAlign : "top",
								sidePagination : 'client',
								silentSort: false,
								pageList : [ 10, 20, 50 ],
								search : false,
								showColumns : false,
								showRefresh : false,
								showExport:false,
								showFooter : false,
								clickToSelect : false,
								showToggle : false,
								columns : [
										{
											field : 'Name',
											title : 'Name',
											align : 'left',
											sortable : true
										},
										{
											field : 'Location',
											title : 'Location',
											align : 'left',
											sortable : true
										},
										{
											field : 'SUBMITTED',
											title : 'SUBMITTED',
											align : 'center',
											sortable : true
										},
										{
											field : 'DMREJ',
											title : 'DMREJ',
											align : 'center',
											sortable : true
										},
										{
											field : 'ACCEPTED',
											title : 'ACCEPTED',
											align : 'center',
											sortable : true
										},
										{
											field : 'INTERVIEWING',
											title : 'INTERVIEWING',
											align : 'center',
											sortable : true
										},
										{
											field : 'CONFIRMED',
											title : 'CONFIRMED',
											align : 'center',
											sortable : true
										},
										{
											field : 'REJECTED',
											title : 'REJECTED',
											align : 'center',
											sortable : true
										},
										{
											field : 'STARTED',
											title : 'STARTED',
											align : 'center',
											sortable : true
										},
										{
											field : 'BACKOUT',
											title : 'BACKOUT',
											align : 'center',
											sortable : true
										},
										{
											field : 'OUTOFPROJ',
											title : 'OUTOFPROJ',
											align : 'center',
											sortable : true
										},
										{
											field : 'NotUpdated',
											title : 'NotUpdated',
											align : 'center',
											sortable : true
										}],

								},
						
							
							}
						};
				
				
				
			
					
					
					
					$scope.cll = function()
			    	{
			    		$timeout(function(){
			    			$("#ydsubmi").text($scope.yesterdayTotal.SUBMITTED);
				    		$("#yddmrej").text($scope.yesterdayTotal.DMREJ);
				    		$("#ydaccepcted").text($scope.yesterdayTotal.ACCEPTED);
				    		$("#ydinterviewing").text($scope.yesterdayTotal.INTERVIEWING);
				    		$("#ydconfirmed").text($scope.yesterdayTotal.CONFIRMED);
				    		$("#ydrejected").text($scope.yesterdayTotal.REJECTED);
				    		$("#ydstarted").text($scope.yesterdayTotal.STARTED);
				    		$("#ydbackout").text($scope.yesterdayTotal.BACKOUT);
				    		$("#ydoutofproj").text($scope.yesterdayTotal.OUTOFPROJ);
				    		$("#ydnu").text($scope.yesterdayTotal.NOT_UPDATED);
				    		
				    		
				    		$("#lwsubmi").text($scope.lastWeekTotal.SUBMITTED);
				    		$("#lwdmrej").text($scope.lastWeekTotal.DMREJ);
				    		$("#lwaccepcted").text($scope.lastWeekTotal.ACCEPTED);
				    		$("#lwinterviewing").text($scope.lastWeekTotal.INTERVIEWING);
				    		$("#lwconfirmed").text($scope.lastWeekTotal.CONFIRMED);
				    		$("#lwrejected").text($scope.lastWeekTotal.REJECTED);
				    		$("#lwstarted").text($scope.lastWeekTotal.STARTED);
				    		$("#lwbackout").text($scope.lastWeekTotal.BACKOUT);
				    		$("#lwoutofproj").text($scope.lastWeekTotal.OUTOFPROJ);
				    		$("#lwnu").text($scope.lastWeekTotal.NOT_UPDATED);
				    		
				    		
				    		$("#tmsubmi").text($scope.thisMonthTotal.SUBMITTED);
				    		$("#tmdmrej").text($scope.thisMonthTotal.DMREJ);
				    		$("#tmaccepcted").text($scope.thisMonthTotal.ACCEPTED);
				    		$("#tminterviewing").text($scope.thisMonthTotal.INTERVIEWING);
				    		$("#tmconfirmed").text($scope.thisMonthTotal.CONFIRMED);
				    		$("#tmrejected").text($scope.thisMonthTotal.REJECTED);
				    		$("#tmstarted").text($scope.thisMonthTotal.STARTED);
				    		$("#tmbackout").text($scope.thisMonthTotal.BACKOUT);
				    		$("#tmoutofproj").text($scope.thisMonthTotal.OUTOFPROJ);
				    		$("#tmnu").text($scope.thisMonthTotal.NOT_UPDATED);
				    		
				    		
				    		
				    		
				    		$("#expsubmi").text($scope.thisMonthTotal.SUBMITTED);
				    		$("#expdmrej").text($scope.thisMonthTotal.DMREJ);
				    		$("#expaccepcted").text($scope.thisMonthTotal.ACCEPTED);
				    		$("#expinterviewing").text($scope.thisMonthTotal.INTERVIEWING);
				    		$("#expconfirmed").text($scope.thisMonthTotal.CONFIRMED);
				    		$("#exprejected").text($scope.thisMonthTotal.REJECTED);
				    		$("#expstarted").text($scope.thisMonthTotal.STARTED);
				    		$("#expbackout").text($scope.thisMonthTotal.BACKOUT);
				    		$("#expoutofproj").text($scope.thisMonthTotal.OUTOFPROJ);
				    		$("#expnu").text($scope.thisMonthTotal.NOT_UPDATED);
				    		
				    		}, 400);
			    	}
					
					
					

					$scope.exportData = function () {
				        var blob = new Blob([document.getElementById('exportThisMonth').innerHTML], {
				        	type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
				        });
				        saveAs(blob, "Report.xls");
						
				        
					}
				
		
		

	});
	
	
})(angular);