;(function(angular){
	
	"use strict";
	
	angular.module("pendingJobOrders",['ui.bootstrap', 'DatePicker'])
	.controller("pendingJobOrdersController", function($rootScope, $scope, $http, $timeout, $state,$location){
		
		$scope.pendingJobOrderTable = false;
		$scope.pendingJobOrder = [];
		var priorityval = $("#priority").val();
    	var jobtypeval = $("#jobtype").val();
		
		$scope.onload  = function(){
			$scope.test = "test pending job orders";
			
			$scope.Created = { endDate: moment(), startDate:moment().subtract(1, 'month')};
			$scope.ranges = {
			        //'All Time'  : [moment().set('year', 2012).set('month',6).set('day',2), moment()],
			        'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],
			        'Today': [moment(),moment()], 
			        'Last 1 month': [moment().subtract(1, 'month'), moment()],
			        'Last 3 months': [moment().subtract(3, 'month'), moment()],
			        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
			        'Last 1 year': [moment().subtract(12, 'month'), moment()]
			        }; 
			
			$scope.pendingJobOrderTable = true;
//			pendingJobOrderTableView();
			$scope.searchpendingjoborder();
		}
		
		
		$scope.searchpendingjoborder = function(){
			

			$(".underlay").show();
			$scope.searchFields= {
					"strPriorities" : $("#priority").val().toUpperCase(),
					"strStatuses" : "PENDING",
					"strJobTypes" :($("#jobtype").val().toUpperCase()).replace(/ +/g, ""),
					"jobOrderTimeIntervalMap" :$scope.Created,
			}
			
			
			
			var dateRangeActiveValue = $(".ranges").children("ul").children("li[class='active']").text();
			if(!dateRangeActiveValue){
				dateRangeActiveValue = constants.LAST_ONE_MONTH;
			}else if(!$('#myJobOrderRangesId').val()){
				dateRangeActiveValue = constants.LAST_ONE_MONTH;
			}
//			alert($('#myJobOrderRangesId').val().split(' ')[2]);
			
			
			if($('#myJobOrderRangesId').val() && $('#myJobOrderRangesId').val()!=''){
				$scope.searchFields.jobOrderTimeIntervalMap.startDate = $('#myJobOrderRangesId').val().split(' ')[0];
				$scope.searchFields.jobOrderTimeIntervalMap.endDate = $('#myJobOrderRangesId').val().split(' ')[2];
			}
			
//			alert("json"+JSON.stringify($scope.searchFields));
			var response = $http.post("jobOrder/getJobOrders",$scope.searchFields);
			response.success(function(data, status,headers, config) 
					{
//				alert(JSON.stringify(data));
				pendingJobOrderTableView();
				$scope.pendingJobOrder.pendingJobOrderBsTableControl.options.data = data;
				$(".underlay").hide();
				
		      });
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
//			$(".underlay").hide();
		
			
		}
		
		
		
		
		
		
		
		
		
		function pendingJobOrderTableView(){

			$scope.pendingJobOrder.pendingJobOrderBsTableControl = {
							options : {
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
								//detailView : true,
								maintainSelected : true,
								columns : [
										{
											field : 'jobOrderId',
											title : 'ID',
											align : 'left',
											sortable : true
										},
										{
											field : 'priority',
											title : 'Priority',
											align : 'left',
											sortable : true
										},
										{
											field : 'status',
											title : 'Status',
											align : 'left',
											sortable : true
										},
										{
											field : 'type',
											title : 'Type',
											align : 'left',
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
											title : 'DM',
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
										{
											field : 'sbm',
											title : 'Sbm',
											align : 'left',
											sortable : true
										},
										{
											field : 'activeDays',
											title : 'Active Days',
											align : 'left',
											sortable : true
										},
										{
											field : 'actions',
											title : 'Actions',
											align : 'left',
											sortable : false,
											events : window.operatePendingEvents,
											formatter : actionPendingFormatter
										} ],
										
							}
						    };
						
						
						
						/* Table button action formatters */

						function actionPendingFormatter(value, row,
								index) {
							
							if($rootScope.rsLoginUser.userId == constants.ANIL_USER_ID){
							
									return [
											'<a class="edit actionIcons" title="Edit JobOrder"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
											
											 ]
											.join('');
							}
									
						}

						
						/* Table button actions functionalities */
						window.operatePendingEvents = {

							/* View Resume details */
							
							'click .edit' : function(e, value,
									row, index) {
								$state.transitionTo("editJobOrders",{jobOrderId : row.jobOrderId,dmName:row.dm, page:constants.PENDINGJOBORDERS});

							},

						};
						
		} 
		
		function getDate(fulldate){
			var month = ( '0' + (fulldate.getMonth()+1) ).slice( -2 );
			var date = ( '0' + (fulldate.getDate()) ).slice( -2 );
			var year = fulldate.getFullYear();
			var totaldate = year + "-" + month + "-" + date;
			return totaldate;
		}
		
		$scope.pendingJobordersreset = function(){
			 $("div[class='ranges'] li").removeClass("active");
				$("div[class='ranges'] li:contains('Last 1 month')").addClass("active");
				$scope.Created = { endDate: moment(), startDate:moment().subtract(1, 'month')};
				
				var currentDate = new Date();
				var oneMonthBeforeDate = moment().subtract(1, 'month').toDate();
				var finaldate = getDate(oneMonthBeforeDate) + " - " + getDate(currentDate);
				$("input[type='daterange']").val(finaldate);
				$scope.joborderid = undefined;
				
				
				var elementarry = priorityval.split(", ");
				var inputcheck = $("#priority").siblings(".ddlist").children("li").children("input");
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
				$("#priority").val(priorityval);
				$("#priority").siblings(".blankmsg").hide();
				
				
				
				var elementarry = jobtypeval.split(", ");
				var inputcheck = $("#jobtype").siblings(".ddlist").children("li").children("input");
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
				$("#jobtype").val(jobtypeval);
				$("#jobtype").siblings(".blankmsg").hide();
				
				
				
			
		}
		
	});
})(angular);