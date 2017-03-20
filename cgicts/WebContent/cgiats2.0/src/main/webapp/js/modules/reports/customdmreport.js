;(function(angular){
	"use strict";
	
		
		angular.module("customdmreportmodule",[])
.controller("customdmreportcontroller", function($scope, $http, dateRangeService, $sessionStorage, $state){
			//$scope.dms = [];
			$scope.usdms = {};
			$scope.dmsInfoTable = false;
			$scope.dmsInfo = [];
			$scope.recInfoTable = false;
			$scope.recInfo = [];
			$scope.onload = function(){
				
				
				
				
				$scope.tabfunction();
				$scope.sitename = "On Site";
				
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
//				moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1})
				$scope.endDatePopup = {
						opened : false
					};
				
				$scope.dmRangeindm = JSON.parse(sessionStorage.getItem("dmsDateRange"));
				
				if($scope.dmRangeindm == "" || $scope.dmRangeindm == undefined || $scope.dmRangeindm == null)
					{
					}
				else
					{
						if($scope.dmRangeindm.site == "onSite")
						{
							$scope.sitename = $scope.dmRangeindm.site;
							$("div[data-tabname='onsite']").addClass("activetab");
							$("div[data-tabname='ofshore']").removeClass("activetab");
							$("#onsite").show();
							$("#ofshore").hide();
							$("#usfilter").show();
							$("#infilter").hide();
							
						}
						else
						{
							$scope.sitename = $scope.dmRangeindm.site;
							$("div[data-tabname='onsite']").removeClass("activetab");
							$("div[data-tabname='ofshore']").addClass("activetab");
							$("#onsite").hide();
							$("#ofshore").show();
							$("#usfilter").hide();
							$("#infilter").show();
						}
					}
				
				
				
				
				if($scope.dmRangeindm){
					$scope.usdms.startDate = dateRangeService.convertStringToDate($scope.dmRangeindm.startDate);;
					$scope.usdms.endDate = dateRangeService.convertStringToDate($scope.dmRangeindm.endDate);;
				}else{
					var startDateVal = new Date();
					startDateVal.setDate(1);
					$scope.usdms.startDate = startDateVal;
					$scope.usdms.endDate = new Date();
				}
				
				$scope.getDmInfo();
				
				
				/*var response = $http.get("commonController/getAllDMs");
				response.success(function(data, config, headers, status){
					if(data){
						$scope.dms = data;
					}
				});
				response.error(function(data, config, headers, status){
					alert("error"+JSON.stringify(data));
				});*/
//				$scope.dmsInfoTable = true;
//				dmsInfoTableView();
			}
			
			
			
			$scope.getDmInfo = function(){
//				alert("success"+JSON.stringify($scope.usdms));
				$scope.dmsInfoTable = false;
				 
				//$scope.recInfoTable = true;
				var from = dateRangeService.formatDate($scope.usdms.startDate);
				var to = dateRangeService.formatDate($scope.usdms.endDate);
				$scope.obj = {
						"startDate" : from,
						"endDate" :to,
						"dmName" : $scope.usdms.dmName
				}
				
				getDmsInfoAsTable();
			}
			
			function getDmsInfoAsTable(){
				var response = $http.post("customReports/getDmCustomReportData", $scope.obj);
				response.success(function(data, config, headers, status){
					if(data){
//						alert("success "+JSON.stringify(data.gridData));
						$scope.seriesData = data.seriesData;
						$scope.categoryData = data.categories;
						
						/*$scope.jobOrderOpenClosedCount = data.jobOrderOpenClosedCount;
						$scope.jobOrderPositionsOpenClosedCount = data.jobOrderPositionsOpenClosedCount;
						$scope.jobOrderService = data.jobOrderService;
						$scope.jobOrderPositionsService = data.jobOrderPositionsService;*/
						
						
						
						
						
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
						 
						 $scope.noOfRec = data.noOfRec;
						 $scope.noOfActiveRec = data.noOfActiveRec;
						 $scope.noOfInActiveRec = data.noOfInActiveRec;
						 $scope.activeRecWithLevelList = data.activeRecWithLevelList;
						 
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
				            text: '<span class="charttext">DMs Monthly Avg Hires</span>'
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
				                '<td style="padding:0"><b>{point.y}</b></td></tr>'+
				            '<tr><td style="color:{series.color};padding:0">No Of Rec:</td>' +
				                '<td style="padding:0"><b>{point.noOfRec}</b></td></tr>',
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
				                    events: {
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
				                        }
				                    }
				                }
				            }
				        },
				        
				        series: [
				                  //{type:'line',color:'red',zIndex:505,data:[[0,0.75],[7,0.75]]},
				                 {
				        	showInLegend: false,  
				        	
				        	//threshold: 0.75,
				            //name: 'Tokyo',
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
		                            field: 'dmName',
		                            title: 'DM Name',
		                            events : window.dmEvents,
									formatter : dmFormatter,
		                            align: 'left',
		                            
		                        },{
		                            field: 'rank',
		                            title: 'Rank',
		                            align: 'left',
		                            
		                        }, {
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
		                            title: 'Team Avg Hires',
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
			
			function dmFormatter(value, row, index) {  
				 
		        return [ 
		        '<a class="jobId actionIcons" title="Recruiters Count: '+row.noOfRecs+'" flex-gt-md="auto" >'+row.dmName+'</a>', 
		        ].join(''); 
				 
		     } 
		     
		     window.dmEvents =  {
		             'click .jobId': function (e, value, row, index) {
//		            	 $scope.obj.noOfJobs = row.noOfJobOrders;
//				    	 $scope.obj.noOfPos =  row.noOfPositions;
		            	 callRecruiterOnClickEvent(row.userId,row.dmName);
		              },
		     };
 
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
  
		     /*function recruiterTableFun(dmId){
//		    	 alert("dfg "+dmId);
		    	 
		    	 var response = $http.post("customReports/getRecruiterCustomReportData", $scope.obj);
					response.success(function(data, config, headers, status){
						 
							$scope.recInfoTable = true;
			            	 recInfoTableView();
			            	 $scope.recInfo.recTableControl.options.data = "";
					 
					});
					response.error(function(data, status, headers, config){
						//$(".underlay").hide();
						  if(status == constants.FORBIDDEN){
							location.href = 'login.html';
						  }else{  			  
							$state.transitionTo("ErrorPage",{statusvalue  : status});
						  }
					  });
		    	 
		    	 
            	 
            	 
		     }*/
		     
		     
				/*function recInfoTableView(){
					
					 $scope.recInfo.recTableControl = {
			                    options: { 
			                        striped: true,
			                        pagination: true,
			                        paginationVAlign: "both", 
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
			                            title: 'Level',
			                            align: 'left',
			                            
			                        },{
			                            field: 'rank',
			                            title: 'Rank',
			                            align: 'left',
			                            
			                        }, {
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
			                            title: 'Started',
			                            align: 'left',
			                           
			                        },{
			                            field: 'avgHires',
			                            title: 'Avg Hires',
			                            align: 'left',
			                           
			                        },{
			                            field: 'satus',
			                            title: 'Status',
			                            align: 'left',
			                           
			                        },]
			                    }
			            };
				}*/
				
				
				
				
				
				
				
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
.controller("INcustomdmreportcontroller", function($scope, $http, dateRangeService, $sessionStorage, $state){
			
			//$scope.dms = [];
			$scope.usdms = {};
			$scope.dmsInfoTable = false;
			$scope.dmsInfo = [];
			$scope.recInfoTable = false;
			$scope.recInfo = [];
			$scope.onload = function(){
				
				$scope.tabfunction();
				$scope.sitename = "On Site";
				
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
//				moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1})
				$scope.endDatePopup = {
						opened : false
					};
				
				$scope.dmRangeindm = JSON.parse(sessionStorage.getItem("dmsDateRange"));
				
				if($scope.dmRangeindm){
					$scope.usdms.startDate = dateRangeService.convertStringToDate($scope.dmRangeindm.startDate);;
					$scope.usdms.endDate = dateRangeService.convertStringToDate($scope.dmRangeindm.endDate);;
				}else{
					var startDateVal = new Date();
					startDateVal.setDate(1);
					$scope.usdms.startDate = startDateVal;
					$scope.usdms.endDate = new Date();
				}
				
				$scope.getDmInfo();
				
				
				/*var response = $http.get("commonController/getAllDMs");
				response.success(function(data, config, headers, status){
					if(data){
						$scope.dms = data;
					}
				});
				response.error(function(data, config, headers, status){
					alert("error"+JSON.stringify(data));
				});*/
//				$scope.dmsInfoTable = true;
//				dmsInfoTableView();
			}
			
			
			
			$scope.getDmInfo = function(){
//				alert("success"+JSON.stringify($scope.usdms));
				$scope.dmsInfoTable = false;
				 
				//$scope.recInfoTable = true;
				var from = dateRangeService.formatDate($scope.usdms.startDate);
				var to = dateRangeService.formatDate($scope.usdms.endDate);
				$scope.obj = {
						"startDate" : from,
						"endDate" :to,
						"dmName" : $scope.usdms.dmName
				}
				
				getDmsInfoAsTable();
			}
			
			function getDmsInfoAsTable(){
				var response = $http.post("indiaReports/getIndiaDmCustomReportData", $scope.obj);
				response.success(function(data, config, headers, status){
					if(data){
//						alert("success "+JSON.stringify(data.gridData));
						$scope.seriesData = data.seriesData;
						$scope.categoryData = data.categories;
						
						/*$scope.jobOrderOpenClosedCount = data.jobOrderOpenClosedCount;
						$scope.jobOrderPositionsOpenClosedCount = data.jobOrderPositionsOpenClosedCount;
						$scope.jobOrderService = data.jobOrderService;
						$scope.jobOrderPositionsService = data.jobOrderPositionsService;*/
						
						
						
						
						
						$scope.jobOrderOpenClosedCount = data.jobOrderOpenClosedCount;
						$scope.jobOrderPositionsOpenClosedCount = data.jobOrderPositionsOpenClosedCount;
						$scope.submittalOpenClosedCount = data.submittalOpenClosedCount;
						$scope.submittalConfirmedOpenClosedCount = data.submittalConfirmedOpenClosedCount;
						$scope.submittalStarteddOpenClosedCount = data.submittalStarteddOpenClosedCount;
						
						$scope.jobOrderService = data.jobOrderService;
						$scope.jobOrderPositionsService = data.jobOrderPositionsService;
						
						$scope.submittalStarteddActiveCount = data.submittalStarteddActiveCount;
						$scope.submittalStarteddInActiveCount = data.submittalStarteddInActiveCount;
						
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
						 
						 $scope.noOfRec = data.noOfRec;
						 $scope.noOfActiveRec = data.noOfActiveRec;
						 $scope.noOfInActiveRec = data.noOfInActiveRec;
						 $scope.activeRecWithLevelList = data.activeRecWithLevelList;
						 
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
				
				
				$scope.myCharts = Highcharts.chart('containersidebarindia',{
					
					  chart: {
				            type: 'column'
				        },
				        title: {
				            text: '<span class="charttext">DMs Monthly Avg Hires</span>'
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
				                '<td style="padding:0"><b>{point.y}</b></td></tr>'+
				            '<tr><td style="color:{series.color};padding:0">No Of Rec:</td>' +
				                '<td style="padding:0"><b>{point.noOfRec}</b></td></tr>',
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
				                    events: {
				                        click: function () {
				                        	var dmFullName=null;
//				                            alert('Category: ' + this.category + ', value: ' + this.y);
				                        	if($scope.gridDateObj){
				                        	for(var i=0;i< $scope.gridDateObj.length;i++){
				                        		if(this.userId === $scope.gridDateObj[i].userId){
				                        			dmFullName =  $scope.gridDateObj[i].dmName;
				                        		}
				                        	}
				                        	}
				                        	
//				                        	 alert(dmFullName);
				                        	
				                            callRecruiterOnClickEvent(this.userId,dmFullName);
				                        }
				                    }
				                }
				            }
				        },
				        
				        series: [
				                  //{type:'line',color:'red',zIndex:505,data:[[0,0.75],[7,0.75]]},
				                 {
				        	showInLegend: false,  
				        	
				        	//threshold: 0.75,
				            //name: 'Tokyo',
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
		                            field: 'dmName',
		                            title: 'DM Name',
		                            events : window.indiaDmEvents,
									formatter : indiaDmFormatter,
		                            align: 'left',
		                            
		                        },{
		                            field: 'rank',
		                            title: 'Rank',
		                            align: 'left',
		                            
		                        }, {
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
		                            title: 'Team Avg Hires',
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
			
			function indiaDmFormatter(value, row, index) {  
				 
		        return [ 
		        '<a class="jobId actionIcons" title="Recruiters Count: '+row.noOfRecs+'" flex-gt-md="auto" >'+row.dmName+'</a>', 
		        ].join(''); 
				 
		     } 
		     
		     window.indiaDmEvents =  {
		             'click .jobId': function (e, value, row, index) {
//		            	 $scope.obj.noOfJobs = row.noOfJobOrders;
//				    	 $scope.obj.noOfPos =  row.noOfPositions;
		            	 callRecruiterOnClickEvent(row.userId,row.dmName);
		              },
		     };
 
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
		    	 $scope.obj.site = "offShore";
            	 $scope.dmRange = JSON.stringify($scope.obj);
            	 //alert("offShore click "+JSON.stringify($scope.dmRange));
            	 sessionStorage.setItem("dmsDateRange", $scope.dmRange);
            	$state.transitionTo("customrecruiterreportmodule");
		     }
  
		     /*function recruiterTableFun(dmId){
//		    	 alert("dfg "+dmId);
		    	 
		    	 var response = $http.post("customReports/getRecruiterCustomReportData", $scope.obj);
					response.success(function(data, config, headers, status){
						 
							$scope.recInfoTable = true;
			            	 recInfoTableView();
			            	 $scope.recInfo.recTableControl.options.data = "";
					 
					});
					response.error(function(data, status, headers, config){
						//$(".underlay").hide();
						  if(status == constants.FORBIDDEN){
							location.href = 'login.html';
						  }else{  			  
							$state.transitionTo("ErrorPage",{statusvalue  : status});
						  }
					  });
		    	 
		    	 
            	 
            	 
		     }*/
		     
		     
				/*function recInfoTableView(){
					
					 $scope.recInfo.recTableControl = {
			                    options: { 
			                        striped: true,
			                        pagination: true,
			                        paginationVAlign: "both", 
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
			                            title: 'Level',
			                            align: 'left',
			                            
			                        },{
			                            field: 'rank',
			                            title: 'Rank',
			                            align: 'left',
			                            
			                        }, {
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
			                            title: 'Started',
			                            align: 'left',
			                           
			                        },{
			                            field: 'avgHires',
			                            title: 'Avg Hires',
			                            align: 'left',
			                           
			                        },{
			                            field: 'satus',
			                            title: 'Status',
			                            align: 'left',
			                           
			                        },]
			                    }
			            };
				}*/
				
				
				
				
				
		
				
 
		});
		
		
		
		
		
})(angular);