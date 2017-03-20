;(function(angular){
	"use strict";
	
	angular.module("deleteJobOrders",['ngMaterial', 'ngMessages','ui.bootstrap','jcs-autoValidate', 'DatePicker'])
    .controller("deleteJobOrdersController",function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state){
    	$('.ranges').empty();
    	$scope.candidates = [];
		$scope.candidates.push({});
    	var candidateData = [];
    	$scope.DeletedMyJobOrderTable = false;
    	$scope.DeletedAllJobOrderTable = false;
		$scope.onload = function(){
			
			$scope.myjobordersreset();
			$timeout(function() {
				$scope.alljoborders();
				$scope.myjoborders();
				 }, 100);
    }
			
		$scope.myjobordersreset = function()
		{
			var priorityval = "High, Medium, Low";
	    	var statusval = "Open, Assigned, Reopen";
	    	var jobtypeval = "Permanent, Contract, Both, Not Specified";
	    	
	    	
			var elementarry = priorityval.split(", ");
			var inputcheck = $("#myjobpriority").siblings(".ddlist").children("li").children("input");
			inputcheck.each(function(){
				
				$(this).removeAttr("checked");
			})
			for(var i=0; i<elementarry .length; i++)
				{
				inputcheck.each(function(){
					
					if($(this).val() == elementarry [i])
						{
						$(this).prop("checked", true);
						}
				})
				}
			$("#myjobpriority").val(priorityval);
			$("#myjobpriority").siblings(".blankmsg").hide();
			
			
			var elementarry = statusval.split(", ");
			var inputcheck = $("#myjobstatus").siblings(".ddlist").children("li").children("input");
			inputcheck.each(function(){
				
				$(this).removeAttr("checked");
			})
			for(var i=0; i<elementarry .length; i++)
				{
				inputcheck.each(function(){
					
					if($(this).val() == elementarry [i])
						{
						$(this).prop("checked", true);
						}
				})
				}
			$("#myjobstatus").val(statusval);
			$("#myjobstatus").siblings(".blankmsg").hide();
			
			
			var elementarry = jobtypeval.split(", ");
			var inputcheck = $("#myjobjobtype").siblings(".ddlist").children("li").children("input");
			inputcheck.each(function(){
				
				$(this).removeAttr("checked");
			})
			for(var i=0; i<elementarry .length; i++)
				{
				inputcheck.each(function(){
					
					if($(this).val() == elementarry [i])
						{
						$(this).prop("checked", true);
						}
				})
				}
			$("#myjobjobtype").val(jobtypeval);
			$("#myjobjobtype").siblings(".blankmsg").hide();
			
			
		}
		
			$scope.alljobordersreset = function()
			{
				var priorityval = "High, Medium, Low";
		    	var statusval = "Open, Assigned, Reopen";
		    	var jobtypeval = "Permanent, Contract, Both, Not Specified";
		    	
		    	
				var elementarry = priorityval.split(", ");
				var inputcheck = $("#alljobpriority").siblings(".ddlist").children("li").children("input");
				inputcheck.each(function(){
					
					$(this).removeAttr("checked");
				})
				for(var i=0; i<elementarry .length; i++)
					{
					inputcheck.each(function(){
						
						if($(this).val() == elementarry [i])
							{
							$(this).prop("checked", true);
							}
					})
					}
				$("#alljobpriority").val(priorityval);
				$("#alljobpriority").siblings(".blankmsg").hide();
				
				
				var elementarry = statusval.split(", ");
				var inputcheck = $("#alljobstatus").siblings(".ddlist").children("li").children("input");
				inputcheck.each(function(){
					
					$(this).removeAttr("checked");
				})
				for(var i=0; i<elementarry .length; i++)
					{
					inputcheck.each(function(){
						
						if($(this).val() == elementarry [i])
							{
							$(this).prop("checked", true);
							}
					})
					}
				$("#alljobstatus").val(statusval);
				$("#alljobstatus").siblings(".blankmsg").hide();
				
				var elementarry = jobtypeval.split(", ");
				var inputcheck = $("#alljobjobtype").siblings(".ddlist").children("li").children("input");
				inputcheck.each(function(){
					
					$(this).removeAttr("checked");
				})
				for(var i=0; i<elementarry .length; i++)
					{
					inputcheck.each(function(){
						
						if($(this).val() == elementarry [i])
							{
							$(this).prop("checked", true);
							}
					})
					}
				$("#alljobjobtype").val(jobtypeval);
				$("#alljobjobtype").siblings(".blankmsg").hide();
				
				
			}

		$scope.myjoborders = function()
		{
			findDeletedMyJobOrders();
		}
		
		function findDeletedMyJobOrders(){
            $(".underlay").show();
         	$scope.searchFields= {
					"strPriorities" : $("#myjobpriority").val().toUpperCase(),
					"strStatuses" : $("#myjobstatus").val().toUpperCase(),
					"strJobTypes" : ($("#myjobjobtype").val().toUpperCase()).replace(/ +/g, "")
			}
         	
			var response = $http.post("jobOrder/getAllDeletedMyJobOrders",$scope.searchFields);
			response.success(function(data, status,headers, config) 
					{
			 candidateData = [];
				for (var i = 0; i < data.length; i++) {
					var candidateObj = data[i];
					candidateData.push(candidateObj);
				}
				dispalyTable();
		      });
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
			$(".underlay").hide();
			 $scope.DeletedMyJobOrderTable = true;
		}
	      
			
			function dispalyTable() {
				$scope.candidates
						.forEach(function(candidate, index) {
							candidate.deletedMyBsTableControl = {
								options : {
									data : candidateData || {},
									striped : true,
									pagination : true,
									paginationVAlign : "both",
									pageSize : 10,
									pageList : [ 10, 20, 50 ],
									search : false,
									showColumns : false,
									showRefresh : false,
									clickToSelect : false,
									showToggle : false,
									maintainSelected : true,
									columns : [
											{
												field : 'jobOrderId',
												title : 'Order ID',
												align : 'left',
												
												sortable : true
											},
											{
												field : 'priority',
												title : 'Priority',
												align : 'left',
												formatter : priorityWithImage,
												sortable : true
											},
											{
												field : 'status',
												title : 'Status',
												align : 'left',
												formatter : statusWithImage,
												sortable : true
											},
											{
												field : 'type',
												title : 'Type',
												align : 'left',
												formatter : typeWithImage,
												sortable : true
											},
											{
												field : 'title',
												title : 'Title',
												align : 'left',
												sortable : true
											},
											{
												field : 'client',
												title : 'Client',
												align : 'left',
												sortable : true
											},
											{
												field : 'location',
												title : 'Location',
												align : 'left',
												sortable : true
											},
											{
												field : 'dm',
												title : 'DM / ADM',
												align : 'left',
												sortable : true
											},
											{
												field : 'assignedTo',
												title : 'Assigned To',
												align : 'left',
												sortable : true
											},
											{
												field : 'updatedDate',
												title : 'Updated On',
												align : 'left',
												sortable : true
											},
											/*{
												field : 'sbm',
												title : 'sbm',
												align : 'left',
												sortable : true
											},
											{
												field : 'activeDays',
												title : 'Active Days',
												align : 'left',
												sortable : true
											},*/
											{
												field : 'actions',
												title : 'Actions',
												align : 'left',
												sortable : false,
												events : window.myReopenOperateEvents,
												formatter : myActionFormatter
											} ],
			
								}
							    };
							
							function priorityWithImage(value, row,
									index) {
								if(row.priority == constants.HIGH){
								return [
										
										'<i class="fa fa-circle" aria-hidden="true" style="color:red;"></i>',
										'&nbsp;<label>'+row.priority+'</label>'
										 ]
										.join('');
								}
								if(row.priority == constants.MEDIUM){
									return [
											
											'<i class="fa fa-circle" aria-hidden="true" style="color:#ff9900;"></i>',
											'&nbsp;<label>'+row.priority+'</label>'
											 ]
											.join('');
									}
								if(row.priority == constants.LOW){
									return [
											
											'<i class="fa fa-circle" aria-hidden="true" style="color:green;"></i>',
											'&nbsp;<label>'+row.priority+'</label>'
											 ]
											.join('');
									}
							}
							
							function statusWithImage(value, row,
									index) {
								if(row.status == constants.OPEN){
								return [
										
										'<i class="fa fa-arrow-circle-right" aria-hidden="true" style="color:#00a3cc;" title="OPEN"></i>',
										'&nbsp;<label>'+row.status+'</label>'
										 ]
										.join('');
								}
								if(row.status == constants.ASSIGNED){
									return [
											
											'<i class="fa fa-user" aria-hidden="true" style="color:#3399ff;" title="ASSIGNED"></i>',
											'&nbsp;<label>'+row.status+'</label>'
											 ]
											.join('');
									}
								if(row.status == constants.REOPEN){
									return [
											
											'<i class="fa fa-folder-open-o" aria-hidden="true" title="REOPEN" style="color:#cc6600;"></i>',
											'&nbsp;<label>'+row.status+'</label>'
											 ]
											.join('');
									}
								if(row.status == constants.CLOSED){
									return [
											
											'<i class="fa fa-times" aria-hidden="true" style="color:#ff4d4d;" title="CLOSED"></i>',
											'&nbsp;<label>'+row.status+'</label>'
											 ]
											.join('');
									}
								if(row.status == constants.FILLED){
									return [
											
											'<i class="fa fa-check-square-o" aria-hidden="true" style="color:#2db300;" title="FILLED"></i>',
											'&nbsp;<label>'+row.status+'</label>'
											 ]
											.join('');
									}
							}
							function typeWithImage(value, row,
									index) {
								if(row.type == constants.PERMANENT){
								return [
										
										'<i class="fa fa-link" aria-hidden="true" title="PERMANENT" style="color:#336d30;"></i>',
										'&nbsp;<label>'+row.type+'</label>'
										 ]
										.join('');
								}
								if(row.type == constants.CONTRACT){
									return [
											
											'<i class="fa fa-chain-broken" aria-hidden="true" style="color:#ff0000;" title="CONTRACT"></i>',
											'&nbsp;<label>'+row.type+'</label>'
											 ]
											.join('');
									}
								if(row.type == constants.BOTH){
									return [
											
											'<i class="fa fa-venus-double" aria-hidden="true" title="BOTH" style="color:#804000;"></i>',
											'&nbsp;<label>'+row.type+'</label>'
											 ]
											.join('');
									}
								if(row.type == constants.NOTSPECIFIED){
									return [
											
											'<i class="fa fa-arrows-alt" aria-hidden="true" title="NOTSPECIFIED" style="color:#c96dad;"></i>',
											'&nbsp;<label>'+row.type+'</label>'
											 ]
											.join('');
									}
							}
							
							/* Table button action formatters */

							function myActionFormatter(value, row,
									index) {
								
								return [
										'<a class="plus actionIcons" title="Reopen JobOrder" ><i class="fa fa-plus" style="font-size:12px;"></i></a> '
										 ]
										.join('');
							}

							/* Table button actions functionalities */
							window.myReopenOperateEvents = {
				
								'click .plus' : function(e, value,
										row, index) {
									$mdDialog
									.show(
											{
												controller : DialogController,
												templateUrl : 'views/dialogbox/confirmAdd.html',
												parent : angular
														.element(document.body),
												targetEvent : e,
												locals : {
													rowData : row,
												},
												clickOutsideToClose : true,
											});

								},
							};
							
							function DialogController($scope,
									$mdDialog, rowData) {
								$scope.row = rowData;
								var jobordid = rowData.jobOrderId;
								$scope.hide = function() {
									$mdDialog.hide();
								};

								$scope.cancel = function() {
									$mdDialog.cancel();
								};

		                      $scope.reopenJobOrder = function() {
						    		
						    		  var response = $http.get('jobOrder/reopenJobOrder/'+jobordid);
						   	          response.success(function(data, status, headers, config) {
//						   	        	findDeletedMyJobOrders();
						   	        	angular.element("#deleteJobOrdersControllerId").scope().myjoborders();
						   	     		$.growl	.notice({title : "Reopened", message : rowData.jobOrderId + " reopened successfully "});
						   	  		   });
						   	          
										response.error(function(data, status, headers, config){
						        			  if(status == constants.FORBIDDEN){
						        				location.href = 'login.html';
						        			  }else{  			  
						        				$state.transitionTo("ErrorPage",{statusvalue  : status});
						        			  }
						        		  });
						   	  		
							    	    $mdDialog.hide();
							    	  };

							}

								});
				
			}

		$scope.alljoborders = function()
		{
			
			findAllDeletedJoborders();
		}
		function findAllDeletedJoborders(){
			$(".underlay").show();
			$scope.searchFields= {
					"strPriorities" : $("#alljobpriority").val().toUpperCase(),
					"strStatuses" : $("#alljobstatus").val().toUpperCase(),
					"strJobTypes" : ($("#alljobjobtype").val().toUpperCase()).replace(/ +/g, "")
			}
         	
			var response = $http.post("jobOrder/getAllDeletedJobOrders",$scope.searchFields);
			response.success(function(data, status,headers, config) 
					{
			 candidateData = [];
				for (var i = 0; i < data.length; i++) {
					var candidateObj = data[i];
					candidateData.push(candidateObj);
				}
				dispalyTable1();
				$(".underlay").hide();
		      });
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
			
			$scope.DeletedAllJobOrderTable = true;
		}
		
		function dispalyTable1() {
			$scope.candidates
					.forEach(function(candidate, index) {
						candidate.deletedAllBsTableControl = {
							options : {
								data : candidateData || {},
								striped : true,
								pagination : true,
								paginationVAlign : "both",
								pageSize : 10,
								pageList : [ 10, 20, 50 ],
								search : false,
								showColumns : false,
								showRefresh : false,
								clickToSelect : false,
								showToggle : false,
								maintainSelected : true,
								columns : [
										{
											field : 'jobOrderId',
											title : 'Order ID',
											align : 'left',
											
											sortable : true
										},
										{
											field : 'priority',
											title : 'Priority',
											align : 'left',
											formatter : priorityWithImage,
											sortable : true
										},
										{
											field : 'status',
											title : 'Status',
											align : 'left',
											formatter : statusWithImage,
											sortable : true
										},
										{
											field : 'type',
											title : 'Type',
											align : 'left',
											formatter : typeWithImage,
											sortable : true
										},
										{
											field : 'title',
											title : 'Title',
											align : 'left',
											sortable : true
										},
										{
											field : 'client',
											title : 'Client',
											align : 'left',
											sortable : true
										},
										{
											field : 'location',
											title : 'Location',
											align : 'left',
											sortable : true
										},
										{
											field : 'dm',
											title : 'DM / ADM',
											align : 'left',
											sortable : true
										},
										{
											field : 'assignedTo',
											title : 'Assigned To',
											align : 'left',
											sortable : true
										},
										{
											field : 'updatedDate',
											title : 'Updated On',
											align : 'left',
											sortable : true
										},
										/*{
											field : 'sbm',
											title : 'sbm',
											align : 'left',
											sortable : true
										},
										{
											field : 'activeDays',
											title : 'Active Days',
											align : 'left',
											sortable : true
										}*/ ],
		
							}
						    };
						
						function priorityWithImage(value, row,
								index) {
							if(row.priority == constants.HIGH){
							return [
									
									'<i class="fa fa-circle" aria-hidden="true" style="color:red;"></i>',
									'&nbsp;<label>'+row.priority+'</label>'
									 ]
									.join('');
							}
							if(row.priority == constants.MEDIUM){
								return [
										
										'<i class="fa fa-circle" aria-hidden="true" style="color:#ff9900;"></i>',
										'&nbsp;<label>'+row.priority+'</label>'
										 ]
										.join('');
								}
							if(row.priority == constants.LOW){
								return [
										
										'<i class="fa fa-circle" aria-hidden="true" style="color:green;"></i>',
										'&nbsp;<label>'+row.priority+'</label>'
										 ]
										.join('');
								}
						}
						
						function statusWithImage(value, row,
								index) {
							if(row.status == constants.OPEN){
							return [
									
									'<i class="fa fa-arrow-circle-right" aria-hidden="true" style="color:#00a3cc;" title="OPEN"></i>',
									'&nbsp;<label>'+row.status+'</label>'
									 ]
									.join('');
							}
							if(row.status == constants.ASSIGNED){
								return [
										
										'<i class="fa fa-user" aria-hidden="true" style="color:#3399ff;" title="ASSIGNED"></i>',
										'&nbsp;<label>'+row.status+'</label>'
										 ]
										.join('');
								}
							if(row.status == constants.REOPEN){
								return [
										
										'<i class="fa fa-folder-open-o" aria-hidden="true" title="REOPEN" style="color:#cc6600;"></i>',
										'&nbsp;<label>'+row.status+'</label>'
										 ]
										.join('');
								}
							if(row.status == constants.CLOSED){
								return [
										
										'<i class="fa fa-times" aria-hidden="true" style="color:#ff4d4d;" title="CLOSED"></i>',
										'&nbsp;<label>'+row.status+'</label>'
										 ]
										.join('');
								}
							if(row.status == constants.FILLED){
								return [
										
										'<i class="fa fa-check-square-o" aria-hidden="true" style="color:#2db300;" title="FILLED"></i>',
										'&nbsp;<label>'+row.status+'</label>'
										 ]
										.join('');
								}
						}
						function typeWithImage(value, row,
								index) {
							if(row.type == constants.PERMANENT){
							return [
									
									'<i class="fa fa-link" aria-hidden="true" title="PERMANENT" style="color:#336d30;"></i>',
									'&nbsp;<label>'+row.type+'</label>'
									 ]
									.join('');
							}
							if(row.type == constants.CONTRACT){
								return [
										
										'<i class="fa fa-chain-broken" aria-hidden="true" style="color:#ff0000;" title="CONTRACT"></i>',
										'&nbsp;<label>'+row.type+'</label>'
										 ]
										.join('');
								}
							if(row.type == constants.BOTH){
								return [
										
										'<i class="fa fa-venus-double" aria-hidden="true" title="BOTH" style="color:#804000;"></i>',
										'&nbsp;<label>'+row.type+'</label>'
										 ]
										.join('');
								}
							if(row.type == constants.NOTSPECIFIED){
								return [
										
										'<i class="fa fa-arrows-alt" aria-hidden="true" title="NOTSPECIFIED" style="color:#c96dad;"></i>',
										'&nbsp;<label>'+row.type+'</label>'
										 ]
										.join('');
								}
						}
						
						/* Table button action formatters */

						function actionFormatter(value, row,
								index) {
							
							return [
									'<a class="plus actionIcons" title="Reopen Submittal" ><i class="fa fa-plus" style="font-size:12px;"></i></a> '
									 ]
									.join('');
						}

						/* Table button actions functionalities */
						window.operateAllDeleteEvents = {
			
							'click .plus' : function(e, value,
									row, index) {
								$mdDialog
								.show(
										{
											controller : DialogController,
											templateUrl : 'views/dialogbox/confirmAdd.html',
											parent : angular
													.element(document.body),
											targetEvent : e,
											locals : {
												rowData : row,
											},
											clickOutsideToClose : true,
										});

							},
						};
						
						function DialogController($scope,
								$mdDialog, rowData) {
							$scope.row = rowData;
							var jobordid = rowData.jobOrderId;
							$scope.hide = function() {
								$mdDialog.hide();
							};

							$scope.cancel = function() {
								$mdDialog.cancel();
							};

	                      $scope.reopenJobOrder = function() {
					    		
					    		  var response = $http.get('jobOrder/reopenJobOrder/'+jobordid);
					   	          response.success(function(data, status, headers, config) {
					   	        	findAllDeletedJoborders();
					   	     		$.growl	.notice({title : "Reopened", message : rowData.jobOrderId + " reopened successfully "});
					   	  		   });
					   	          
									response.error(function(data, status, headers, config){
					        			  if(status == constants.FORBIDDEN){
					        				location.href = 'login.html';
					        			  }else{  			  
					        				$state.transitionTo("ErrorPage",{statusvalue  : status});
					        			  }
					        		  });
					   	  		
						    	    $mdDialog.hide();
						    	  };

						}

							});
			
		}
		
	});
	
})(angular);