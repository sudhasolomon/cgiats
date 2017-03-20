;(function(angular){
	
	
			"use strict";
			
				angular.module("recruiterLevelModule",['ngMaterial', 'ngMessages','ui.bootstrap'])
				.controller("recruiterlevelcontroller", function($scope, $http , $timeout, $state, $stateParams){
					$scope.recByJobOrderId = [];
					$scope.recJoborderTable =false;
					 $scope.onload  = function(){
						 $scope.jobOrderArray = [];
						 $scope.detailsfromdm = JSON.parse(sessionStorage.getItem("dmdetails"));
						 
//						 alert($stateParams.jobOrderId);
						/* Highcharts.setOptions({
						        chart: {
						            backgroundColor: {
						                linearGradient: [0, 0, 500, 500],
						                stops: [
						                    [0, 'rgb(255, 255, 255)'],
						                    [1, 'rgb(240, 240, 255)']
						                    ]
						            },
						            //borderWidth: 2,
						            plotBackgroundColor: 'rgba(255, 255, 255, .9)',
						            plotShadow: true,
						           // plotBorderWidth: 1,
						        }
						    });*/
						 $scope.sideBarChartMethodCall();
						 getJobOrderDetailsBarChartMethodCall();
//						 sideBarCallFun();
						 
					 }
					
					
					$scope.sideBarChartMethodCall = function(){
						var response = $http.get('totalReportController/getAllSubmittalsByOrderId/'+$scope.detailsfromdm.jobOrderId);
						response.success(function (data,status,headers,config){
							if(data){
								$scope.totalsidebarData = data;
								sideBarCallFun();
							}else{
								$scope.totalsidebarData = {
										"series" : [],
										"dms" : []
								};
								sideBarCallFun();
							}
							$(".underlay").hide();
						});
						
						response.error(function(data, status, headers, config){
							$(".underlay").hide();
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
					}
					
					function  sideBarCallFun(){
						$scope.myCharts = Highcharts.chart('containerside',{
							
							 chart:{
								 type : 'bar'
							 },
							title:{
								text : 'Submittal Details for Job Order Id: ' + $scope.detailsfromdm.jobOrderId + ''
							},
							xAxis:{
								min: 0,
								categories: $scope.totalsidebarData.dms
							},
							yAxis: {
					            min: 0,
					            title: {
					                text: 'No of Submittals'
					            }
					        },
					        legend: {
					        	itemStyle: {
					                 fontSize:'13px',
					                 color: '#666666'
					              },
					            reversed: true
					        },
							 
							 plotOptions: {
						            series: {
						            	 stacking: 'normal',
						                cursor: 'pointer',
						                point: {
						                    events: {
						                        click: function (e) {
						                        	$scope.recJoborderTable =false;
						                        	$scope.recByJobOrderId = [];
//						                            alert('Category: ' + this.category + ', value: ' + this.y+ ', name'+this.series.name); 
						                        	 $scope.statusName = this.series.name;
							                            $scope.recName = this.category;
						                            getJobOrderDetailsById(this.category, this.series.name);
						                            getJobOrderDetailsBarChartMethodCall();
						                           
						                        }
						                    }
						                }
						            }
						        },
						        series:  $scope.totalsidebarData.series
						 });
					}
					
					function getJobOrderDetailsBarChartMethodCall(){
						$scope.jobOrderArray = [];
//						alert($scope.statusName + "  "+$scope.statusName);
						if($scope.statusName && $scope.recName){
							var response = $http.get('totalReportController/getSubmittalServiceDetailByOrderId_Status_CreatedBy?orderId='+$scope.detailsfromdm.jobOrderId+'&createdBy='+$scope.recName+'&status='+$scope.statusName);
						}else{
							var response = $http.get('totalReportController/getAvgNoOfStatusesDaysByJobOrderId/'+$scope.detailsfromdm.jobOrderId);
						}
						
						response.success(function (data,status,headers,config){
							if(data){
//								alert(JSON.stringify(data));
								$scope.secondsidebarData = data;
								$scope.jobOrderArray.push($scope.detailsfromdm.jobOrderId);
								servicesideBarCallFun();
							} 
							$(".underlay").hide();
						});
						
						response.error(function(data, status, headers, config){
							$(".underlay").hide();
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
					}
					
					function servicesideBarCallFun(){
						$scope.mySecondCharts = Highcharts.chart('containerside2',{
							
							 chart:{
								 type : 'bar'
							 },
							title:{
								text : 'Job Order Id '+$scope.detailsfromdm.jobOrderId+' Serviced Report'
							},
							subtitle:{
								text : 'Status : '+($scope.statusName?$scope.statusName:'All')
							},
							xAxis:{
								min: 0,
								categories: $scope.jobOrderArray
							},
							yAxis: {
					            min: 0,
					            title: {
					                text: 'No of Submittals'
					            }
					        },
					        legend: {
					        	itemStyle: {
					                 fontSize: (!$scope.statusName || ($scope.statusName == constants.SUBMITTED))? '13px':'10px',
					                 color: '#666666'
					              },
					            reversed: true
					        },
					        plotOptions: {
					            series: {
					            	 stacking: 'normal',
					                cursor: 'pointer',
					                point: {
					                    events: {
					                        click: function (e) {
//					                            alert('Category: ' + this.category + ', value: ' + this.y+ ', name'+this.series.name); 
					                        }
					                    }
					                }
					            }
					        },
						        series:  $scope.secondsidebarData.series
						 });
					}
					
					function getJobOrderDetailsById(name, status){
//						alert(status);
//						alert($scope.detailsfromdm.jobOrderId+" sdfjhjfs");
						var response = $http.get('totalReportController/getSubmittalDetailByOrderId_Status_CreatedBy?orderId='+$scope.detailsfromdm.jobOrderId+'&createdBy='+name+'&status='+status);

					
						    						response.success(function (data,status,headers,config){
//						    							alert(JSON.stringify(data));
						    							if(data){
						    								$scope.recJoborderTable =true;
//						    								recJobOrderTableView();
						    								
						    								recDetailsTableView();
						    								$scope.recByJobOrderId.RecJoborderTableControl.options.data = data;
						    								
						    							}
						    						});
						    						
						    						response.error(function(data, status, headers, config){
						    							$(".underlay").hide();
						    			  			  if(status == constants.FORBIDDEN){
						    			  				location.href = 'login.html';
						    			  			  }else{  			  
						    			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
						    			  			  }
						    			  		  });
					}
					
					
					function recDetailsTableView(){
						$scope.recByJobOrderId.RecJoborderTableControl = {
								options: { 
			                        striped: true,
			                        pagination: true,
			                        paginationVAlign: "bottom", 
			                        search: false,
			                        silentSort: false,
			                        pageSize : 5,
									pageList : [5, 10, 20, 50 ],
			                        showColumns: false,
			                        showRefresh: false,
			                        clickToSelect: false,
			                        showToggle: false,
			                        maintainSelected: true, 
			                        showFooter : false,
			                        columns: [{
			                            field: 'orderId',
			                            title: 'Order Id',
			                            align: 'left',
			                           
			                        },
			                         {
			                            field: 'candidateFullName',
			                            title: 'Candidate Name',
			                            align: 'left',
			                           
			                        }, {
			                            field: 'clientName',
			                            title: 'Client',
			                            align: 'left',
			                           
			                        },{
			                            field: 'jobTitle',
			                            title: 'Title',
			                            align: 'left',
			                           
			                        },{
			                            field: 'createdOrUpdatedBy',
			                            title: 'Edited By',
			                            align: 'left',
			                           
			                        },{
			                            field: 'createdOrUpdatedOn',
			                            title: 'Edited On',
			                            align: 'left',
			                           
			                        }]
			                    }
						}
					}
					
			/*		function recJobOrderTableView(){
						$scope.recByJobOrderId.RecJoborderTableControl = {
								options: { 
			                        striped: true,
			                        pagination: true,
			                        paginationVAlign: "bottom", 
			                        pageList: [50,100,200],
			                        search: false,
			                        //sidePagination : 'server',
			                        silentSort: false,
			                        pageSize: 8,
			                        showColumns: false,
			                        showRefresh: false,
			                        clickToSelect: false,
			                        showToggle: false,
			                        maintainSelected: true, 
			                        showFooter : false,
			                        columns: [
			                         {
			                            field: 'recName',
			                            title: 'Rec Name',
			                            align: 'left',
			                           
			                        }, {
			                            field: 'clientName',
			                            title: 'Client',
			                            align: 'left',
			                           
			                        },{
			                            field: 'SUBMITTED',
			                            title: 'Submittals',
			                            align: 'left',
			                           
			                        },{
			                            field: 'INTERVIEWING',
			                            title: 'Interviewing',
			                            align: 'left',
			                           
			                        },{
			                            field: 'CONFIRMED',
			                            title: 'Confirmed',
			                            align: 'left',
			                           
			                        },{
			                            field: 'STARTED',
			                            title: 'Started',
			                            align: 'left',
			                           
			                        },]
			                    }
						}
					}*/
					
					
				});
})(angular);