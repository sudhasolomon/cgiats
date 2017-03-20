;(function(angular){
	"use strict";
	
		
		angular.module("locationreportmodule",['angularjs-dropdown-multiselect'])
.controller("locationreportcontroller", function($scope, $http, dateRangeService, $sessionStorage, $state, $timeout){
			//$scope.dm 	s = [];
			$scope.location = [];
			$scope.usdms = {};
			$scope.dmsInfoTable = false;
			$scope.dmsInfo = [];
			$scope.recInfoTable = false;
			$scope.recInfo = [];
			$scope.onload = function(){
				
				
				$timeout(function() {
					$(".caret").addClass("fa fa-caret-down");
					$(".caret").css("font-size", "13px");
					$(".caret").css("color", "#555555");
					 }, 100);
				
				
				
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
				$scope.usdms.startDate = startDateVal;
				$scope.usdms.endDate = new Date();
				$scope.getLocationList();
			}
			
			
			
			
			
			
			
		
			
			
			
			
			
			$scope.getDmInfo = function(){
				
				if($scope.location.length < 1)
				{
				$("#locationfield .multiselect .dropdown-toggle").css("border-color", "#ff0000");
				$("#locationfield").children(".mustfield").show();
				}
			else
				{
				$("#locationfield .multiselect .dropdown-toggle").css("border-color", "#c2cad8");
				$("#locationfield").children(".mustfield").hide();
				
				$scope.dmsInfoTable = false;
				 
				var from = dateRangeService.formatDate($scope.usdms.startDate);
				var to = dateRangeService.formatDate($scope.usdms.endDate);
				
				$scope.locationforReport = "";
				for(var i=0; i < $scope.location.length; i++)
				{
				$scope.locationforReport += $scope.location[i].id + ", ";
				}
				$scope.obj = {
						"startDate" : from,
						"endDate" :to,
						"location" : $scope.locationforReport
				}
				
				getDmsInfoAsTable();
			
				
				
				}
				
			}
			
			function getDmsInfoAsTable(){
//				alert(JSON.stringify($scope.obj));
				var response = $http.post("customReports/getLocationReportData", $scope.obj);
				response.success(function(data, config, headers, status){
					if(data){
//						alert("success "+JSON.stringify(data.gridData));
						$scope.seriesData = data.seriesData;
						$scope.categoryData = data.categories;
						
						/*$scope.jobOrderOpenClosedCount = data.jobOrderOpenClosedCount;
						$scope.jobOrderPositionsOpenClosedCount = data.jobOrderPositionsOpenClosedCount;
						$scope.jobOrderService = data.jobOrderService;
						$scope.jobOrderPositionsService = data.jobOrderPositionsService;*/
						
						
						
						
						
						/*$scope.jobOrderOpenClosedCount = data.jobOrderOpenClosedCount;
						$scope.jobOrderPositionsOpenClosedCount = data.jobOrderPositionsOpenClosedCount;
						$scope.submittalOpenClosedCount = data.submittalOpenClosedCount;
						$scope.submittalConfirmedOpenClosedCount = data.submittalConfirmedOpenClosedCount;
						$scope.submittalStarteddOpenClosedCount = data.submittalStarteddOpenClosedCount;*/
						
						$scope.jobOrderService = data.jobOrderService;
						$scope.jobOrderPositionsService = data.jobOrderPositionsService;
						
						$scope.submittalService = data.submittalService;
						$scope.submittalCnfService = data.submittalCnfService;
						$scope.submittalStartedService = data.submittalStartedService;
						
						
						
						
						//alert(JSON.stringify(data));
						$scope.dmsInfoTable = true;
						 dmsInfoTableView();
						 $scope.dmsInfo.dmsTableControl.options.data = data.gridData;
						 
						 $scope.gridDateObj = data.gridData;
						 $scope.dmsCount = data.noOfDms;
						 $scope.jobOrdersCount = data.noOfJobs;
						 $scope.positionsCount = data.noOfPos;
						 $scope.submittalsCount = data.noOfSub;
						 $scope.confirmedCount = data.noOfConfirmed;
						 $scope.startedCount = data.noOfStarts;
						 
						 $scope.submittalStarteddActiveCount = data.submittalStarteddActiveCount;
							$scope.submittalStarteddInActiveCount = data.submittalStarteddInActiveCount;
						 
						 $scope.noOfRec = data.noOfRec;
						 $scope.noOfActiveRec = data.noOfActiveRec;
						 $scope.noOfInActiveRec = data.noOfInActiveRec;
						 $scope.activeRecWithLevelList = data.activeRecWithLevelList;
						 $scope.activeUserRoles = data.activeUserRolesMap;
						 
						 getDmsInfoAsBar();
					}else{
						$scope.dmsInfoTable = true;
						 dmsInfoTableView();
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
			
			function getDmsInfoAsBar(){
				
				
				$scope.myCharts = Highcharts.chart('containersidebar',{
					
					  chart: {
				            type: 'column'
				        },
				        title: {
				            text: '<span class="charttext">Recruiters Monthly Avg Hires</span>'
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
				                '<td style="padding:0"><b>{point.y}</b></td></tr>',
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
				        
				        plotOptions: {
				            series: {
				                cursor: 'pointer',
				                point: {
				                    events: {/*
				                        click: function () {
				                        	var dmFullName=null;
				                          if($scope.gridDateObj){
				                        	for(var i=0;i< $scope.gridDateObj.length;i++){
				                        		if(this.userId === $scope.gridDateObj[i].userId){
				                        			dmFullName =  $scope.gridDateObj[i].dmName;
				                        		}
				                        	}
				                        	}
				                        	
//				                        	 alert(dmFullName);
				                        	
				                            callRecruiterOnClickEvent(this.userId,dmFullName);
				                        }*/
				                    }
				                }
				            }
				        },
				        
				        series: [
				                 {
				        	showInLegend: false,  
				        	
				            data: $scope.seriesData

				        } ]
				});
			}
			
			
			function dmsInfoTableView(){
				var colorCode = 'green';
				 $scope.dmsInfo.dmsTableControl = {
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
		                            field: 'rank',
		                            title: 'Rank',
		                            align: 'left',
		                            
		                        }, {
		                            field: 'level',
		                            title: 'Level',
		                            align: 'left',
		                            
		                        },
		                        {
		                            field: 'location',
		                            title: 'Loc',
		                            align: 'left',
		                            
		                        },
		                        {
		                            field: 'dmName',
		                            title: 'DM Name',
		                            align: 'left',
		                            
		                        },{
		                            field: 'noOfJobOrders',
		                            title: '#JobOrds',
		                            align: 'left',
		                           
		                        },{
		                            field: 'noOfPositions',
		                            title: '#Pos',
		                            align: 'left',
		                           
		                        },{
		                            field: 'submittedCount',
		                            title: '#Sbm',
		                            align: 'left',
		                           
		                        },{
		                            field: 'confirmedCount',
		                            title: '#Conf',
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
		                           // events : window.colorEvents,
									formatter : colorFormatter,
		                           
		                        },]
		                    }
		            };
			}
			
		     
 
		     function colorFormatter(value, row, index) {  
		    			 return [
					                '<div  style = "background-color: '+row.color+'; color:#ffffff; text-align: center;"> '+row.performanceStatus+' </div>'
					                ].join(''); 
			     } 
		     function callRecruiterOnClickEvent(dm,dmFullName){
		    	 $scope.obj.dmName = dm;
		    	 $scope.obj.dmFullName = dmFullName;
//		    	 $scope.obj.noOfJobs = $scope.jobOrdersCount;
//		    	 $scope.obj.noOfPos =  $scope.positionsCount;
		    	 $scope.obj.site = "onSite";
            	 $scope.dmRange = JSON.stringify($scope.obj);
            	 //alert("onSite click "+JSON.stringify($scope.dmRange));
            	 sessionStorage.setItem("dmsDateRange", $scope.dmRange);
            	$state.transitionTo("customrecruiterreportmodule");
		     }
  
 
		     
		     
		     
		     
		     
		     $scope.getLocationList = function()
				{
					var response = $http.get('commonController/getAllLocations');
					response.success(function (data,status,headers,config){
						$scope.locationList = [];
						$scope.lcdt = [];
						for(var i=0; i<data.length; i++ )
							{
								var obj = {id: data[i], label: data[i]};
								$scope.locationList.push(obj);
								$scope.lcdt.push(obj); 
							}
				        $scope.location = $scope.lcdt;
						$scope.locationdata = $scope.locationList;
				        $scope.getDmInfo();
					});
					response.error(function(data, status, headers, config){
		  			  if(status == constants.FORBIDDEN){
		  				location.href = 'login.html';
		  			  }else{  			  
		  				$state.transitionTo("ErrorPage",{statusvalue  : status});
		  			  }
		  		  });

				}
				
				
				
				
				$scope.manysettings = {
			            smartButtonMaxItems: 3,
			            smartButtonTextConverter: function(itemText, originalItem) {
			                return itemText;
			            },
			        };
				
				
				
				
		     
		     
		     
		})
		
		
})(angular);