;(function(angular){
	"use strict";
	
		
		angular.module("resumeAuditLogsReportModule",[])
		.controller("resumeauditreportcontroller", function($scope, $http,dateRangeService, $state){
			$scope.errMsg=false;
			$scope.resumeAuditLogInfo = [];
			$scope.candidateId = null;
			$scope.viewedBy = null;
			var startDateVal = new Date();
			startDateVal.setDate(1);
			$scope.startDate = startDateVal;
			$scope.endDate = new Date();
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
				$scope.getResumeAuditLogs();
			}
			
			$scope.getResumeAuditLogs = function(){
				$scope.resumeAuditLogTable = false;
				var startDate = dateRangeService.formatDate($scope.startDate);
				var endDate =  dateRangeService.formatDate($scope.endDate);
				$scope.obj = {"startDate" : startDate,
							"endDate" : endDate,
							"candidateId" : $scope.candidateId,
							"viewedBy" : $scope.viewedBy};
				//alert(JSON.stringify($scope.obj));
				if(endDate >= startDate && startDate!=null && endDate!=null){
					var response = $http.post("reportController/getResumeAuditLogData", $scope.obj);
					response.success(function(data, config, headers, status){
						//alert(JSON.stringify(data));
						if(data){
							$scope.errMsg=false;
						/*	$scope.clientWiseSbmList = data.gridData; 
							$scope.exportClientWiseSbmList = [];
							
							for (var i = 0; i <data.gridData.length; i++) {
								var obj = {jobOrderId:data.gridData[i].orderId,
										CreatedDate: data.gridData[i].createdOn,ClientName: data.gridData[i].jobClient,
										Title: data.gridData[i].jobTitle,DMName:data.gridData[i].dmName,AssignedTo: data.gridData[i].assignedTo,Positions: data.gridData[i].noOfPositions,
										SBM: data.gridData[i].submittedCount,Confirmed: data.gridData[i].confirmedCount,
										Started: data.gridData[i].startedCount,NetPositions: data.gridData[i].netPositions};
								$scope.exportClientWiseSbmList.push(obj);
							}*/
							$scope.resumeAuditLogTable = true;
							resumeAuditLogTableView();
							$scope.resumeAuditLogInfo.resumeAuditLogTableControl.options.data=data.resumeAuditLogData;
						}else{
							$scope.resumeAuditLogTable = true;
							resumeAuditLogTableView();
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
						$scope.resumeAuditLogTable = true;
					}
				
			}
		
			
			function resumeAuditLogTableView(){
				 $scope.resumeAuditLogInfo.resumeAuditLogTableControl = {
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
		                        	field: 'candidateId',
		                        	title: 'Candidate Id',
		                        	align: 'left',
		                        	
		                        },
		                        {
		                            field: 'createdBy',
		                            title: 'Viewed/Downloaded By',
		                            align: 'left',
		                            
		                        },{
		                            field: 'createdOn',
		                            title: 'Viewed/Downloaded Date',
		                            align: 'left',
		                            
		                        },{
		                            field: 'document_status',
		                            title: 'Document Status',
		                            align: 'left',
		                            
		                        },{
		                            field: 'status',
		                            title: 'Status',
		                            align: 'left',
		                           
		                        }]
		                    }
		            };
			}
		});
})(angular);