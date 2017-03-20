;(function(angular){
	
	
			"use strict";
			angular.module('indiaSubmitalStatsModule',[])
			.controller('indiaSubmitalStatsController',function($scope, $http, $state, $location, dateRangeService, $timeout){
				var notUpdatedTotal=0;
				$scope.indiaSubmitalStatsTable = false;
				$scope.currentDateWithTime = new Date();
		    	$scope.indiaSubmitalStats = [];
		    	$scope.submittalData ={};
		    	$scope.submittalStatsTotal = {};
		    	$scope.officeLocation = "ALL";
		    	$scope.exportStatsData = [];
		    	$scope.totalExportStatsData = [];
				$scope.onload = function(){
					$scope.toatal = '6'
					$scope.Created = { endDate: moment(), startDate:moment().startOf('month')};
					 $scope.ranges = {
							 	'Today':[moment(),moment()],
							 	'This Month' : [moment().startOf('month'), moment()],
			        	        /*'Last 1 month': [moment().subtract(1, 'month'), moment()],*/
			        	        'Last 3 months': [moment().subtract(3, 'month'), moment()],
			        	        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
			        	        'Last 1 year': [moment().subtract(12, 'month'), moment()]
			        	        };
					 
					 $scope.SubmittalStats();
				}
				
				function getDDMMYY_Date(strValue){
					if(strValue){
					var strDateArray = strValue.split('-');
					return [strDateArray[2], strDateArray[1],strDateArray[0]].join('-');
					}
				}
				
				$scope.SubmittalStats = function(){
					$scope.indiaSubmitalStatsTable = true;
					indiaSubmitalStatsTable();
					
					if($('#indiaSubmittalStatsRangesId').val() && $('#indiaSubmittalStatsRangesId').val()!=''){
		    			
		    			
						var startDate = $('#indiaSubmittalStatsRangesId').val().split(' ')[0];
						var endDate = $('#indiaSubmittalStatsRangesId').val().split(' ')[2];
						$scope.obj = {startDate:getDDMMYY_Date(startDate), endDate:getDDMMYY_Date(endDate),officeLocation:$scope.officeLocation};
					}else{
						var obj = {startDate:$scope.Created.startDate, endDate:$scope.Created.endDate};
						var startDate = obj.startDate.toDate();
						var endDate = obj.endDate.toDate();
			    		var viewstartdate = (startDate.getDate()<10?"0"+startDate.getDate():startDate.getDate())+'-'+((startDate.getMonth()+1)<10?"0"+(startDate.getMonth()+1):(startDate.getMonth()+1))+'-'+startDate.getFullYear();
			    		var viewEndDate = (endDate.getDate()<10?"0"+endDate.getDate():endDate.getDate())+'-'+((endDate.getMonth()+1)<10?"0"+(endDate.getMonth()+1):(endDate.getMonth()+1))+'-'+endDate.getFullYear();
			    		
			    		$scope.obj = {startDate:viewstartdate, endDate:viewEndDate,officeLocation:$scope.officeLocation};
					}
					
					
					var response = $http.post("indiaReports/getSubmitalStats",$scope.obj);
					response.success(function(data, status, headers, config){
						$scope.indiaSubmitalStats.SubmitalStasTableControl.options.data =  data.submittalStatsData;
						
						$scope.submittalData = data.submittalStatsData;
						$scope.submittalStatsTotal = data.submittalTotalsByStatus;
						$scope.exportStatsData = [];
						for (var i = 0; i < $scope.submittalData.length; i++) {
						var obj = {name:$scope.submittalData[i].fullName,
								dm: $scope.submittalData[i].DM
								,Location: $scope.submittalData[i].Location,
								SUBMITTED: $scope.submittalData[i].SUBMITTED,DMREJ:$scope.submittalData[i].DMREJ,ACCEPTED: $scope.submittalData[i].ACCEPTED,INTERVIEWING: $scope.submittalData[i].INTERVIEWING,CONFIRMED: $scope.submittalData[i].CONFIRMED,REJECTED: $scope.submittalData[i].REJECTED,
								STARTED: $scope.submittalData[i].STARTED,BACKOUT: $scope.submittalData[i].BACKOUT,OUTOFPROJ: $scope.submittalData[i].OUTOFPROJ,NotUpdated: $scope.submittalData[i].NotUpdated};
						$scope.exportStatsData.push(obj);
						}
						notUpdatedTotal=0;
		            	for(var i=0; i<data.submittalStatsData.length; i++){
		            		notUpdatedTotal+=data.submittalStatsData[i].NotUpdated;
		            		
		            	}
		            	$scope.totalExportStatsData = [];
						$scope.totalExportStatsData = $scope.exportStatsData;
						
						var totalobj = {name: "Total:",dm: "",Location: "", SUBMITTED: $scope.submittalStatsTotal.SUBMITTED, DMREJ: $scope.submittalStatsTotal.DMREJ, ACCEPTED : $scope.submittalStatsTotal.ACCEPTED, INTERVIEWING : $scope.submittalStatsTotal.INTERVIEWING,
								CONFIRMED:$scope.submittalStatsTotal.CONFIRMED, REJECTED: $scope.submittalStatsTotal.REJECTED, STARTED: $scope.submittalStatsTotal.STARTED, BACKOUT: $scope.submittalStatsTotal.BACKOUT, OUTOFPROJ: $scope.submittalStatsTotal.OUTOFPROJ, NotUpdated: notUpdatedTotal};
						$scope.totalExportStatsData.push(totalobj);
						
						
					});
					response.error(function(){
						 if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
					});
					$scope.cll();
				}
				
				 $scope.getTimeFnc = function(){
						$scope.currentDateWithTime = new Date();
					}
				
		    	function indiaSubmitalStatsTable(){
		    		
		            $scope.indiaSubmitalStats.SubmitalStasTableControl = {
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
		                            field: 'fullName',
		                            title: 'NAME',
		                            events: window.editSubmittalEvent,
		                            formatter : editSubmittal,
		                            align: 'left',
		                            
		                        }, {
		                            field: 'DM',
		                            title: 'DM',
		                            align: 'left',
		                           
		                        }, {
		                            field: 'Location',
		                            title: 'Location',
		                            align: 'left',
		                            
		                        }, {
		                            field: 'SUBMITTED',
		                            title: 'SUBMITTED',
		                            align: 'left',
		                        }, {
		                            field: 'DMREJ',
		                            title: 'DMREJ',
		                            align: 'left',
		                        }, {
		                            field: 'ACCEPTED',
		                            title: 'ACCEPTED',
		                            align: 'left',
		                        },{
		                            field: 'INTERVIEWING',
		                            title: 'INTERVIEWING',
		                            align: 'left',
		                        }, {
		                            field: 'CONFIRMED',
		                            title: 'CONFIRMED',
		                            align: 'left',
		                        }, {
		                            field: 'REJECTED',
		                            title: 'REJECTED',
		                            align: 'left',
		                        }, {
		                            field: 'STARTED',
		                            title: 'STARTED',
		                            align: 'left',
		                        }, {
		                            field: 'BACKOUT',
		                            title: 'BACKOUT',
		                            align: 'left',
		                        }, {
		                            field: 'OUTOFPROJ',
		                            title: 'OUTOFPROJ',
		                            align: 'left',
		                        }, {
		                            field: 'NotUpdated',
		                            title: 'NU',
		                            align: 'left',
		                        },]
		                    }
		            };
		            
		            
		            
		            function editSubmittal(value, row,
							index){
		            	return [
								
						        '<a class="userName actionIcons" title="Edit">'+row.fullName+'</a> '
								 ]
								.join('');
					}
		            
		            window.editSubmittalEvent = {
		            		
		    				'click .userName' : function(e,value, row, index) {
		    					/*var start = dateRangeService.formatDate_india($scope.Created.startDate);
		    					var end =  dateRangeService.formatDate_india($scope.Created.endDate);*/
		    					var start = dateRangeService.formatDate_india($scope.obj.startDate);
		    					var end =  dateRangeService.formatDate_india($scope.obj.endDate);
		    					window.location = "#/indiaReports/submital?statsUser="+row.Name+"&fromDate="+start+"&toDate="+end ;
		    					//window.open(url,'_blank');
		    				},
		    			}
		    	}
		    	
		    	
		    	$scope.cll = function()
		    	{
		    		$timeout(function(){
			    		$("#submitted").text($scope.submittalStatsTotal.SUBMITTED);
			    		$("#dmrej").text($scope.submittalStatsTotal.DMREJ);
			    		$("#accepcted").text($scope.submittalStatsTotal.ACCEPTED);
			    		$("#interviewing").text($scope.submittalStatsTotal.INTERVIEWING);
			    		$("#confirmed").text($scope.submittalStatsTotal.CONFIRMED);
			    		$("#rejected").text($scope.submittalStatsTotal.REJECTED);
			    		$("#started").text($scope.submittalStatsTotal.STARTED);
			    		$("#backout").text($scope.submittalStatsTotal.BACKOUT);
			    		$("#outofproj").text($scope.submittalStatsTotal.OUTOFPROJ);
			    		$("#notup").text(notUpdatedTotal);
			    		}, 600);
		    	}
		    	
		    	
		    	
		    	
				
			});
})(angular);