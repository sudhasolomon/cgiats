;(function(angular){
	"use strict";	
	angular.module("submittal_list",['DatePicker','ui.bootstrap','angular-highlight','jcs-autoValidate'])
	.controller("submittal_listcontroller",function( $rootScope, $scope, blockUI, $http, $timeout, $location,
			$filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage,dateRangeService,mailService){
		
		var candidateData = [];
		$scope.candidates = [];
		$scope.candidates.push({});
		$scope.submittalTable = false;
		var obj = {};
		$scope.onload = function()
		{
			if($location.search().startDate){
				obj.startDate=$location.search().startDate;
			}
			if($location.search().endDate){
				obj.endDate=$location.search().endDate;
			}
			if($location.search().userId){
				obj.userId=$location.search().userId;
			}
			
			var response = $http.post("submittalStatsController/getAllSubmittalsByUserId",obj);
    		response.success(function(data, status,headers, config) 
				{
    				candidateData = data;
    				dispalyTable();
				});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
			$scope.submittalTable = true;
		}


	/* Set table and pagination */
	function dispalyTable() {
		$scope.candidates
		.forEach(function(candidate, index) {
			candidate.submittalListBsTableControl = {
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
								field : 'createdOn',
								title : 'Created On',
								align : 'left',
								sortable : true
							},
							{
								field : 'updatedOn',
								title : 'Updated On',
								align : 'left',
								sortable : true
							},
							{
								field : 'createdBy',
								title : 'Created By',
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
								field : 'candidateName',
								title : 'Candidate',
								align : 'left',
								sortable : true
							},
							{
								field : 'actions',
								title : 'Actions',
								align : 'left',
								sortable : false,
								events : window.submittalEvent,
								formatter : submittalEventActionFormatter
							} ],

				}
			    };
			
			/* Table button action formatters */

			function submittalEventActionFormatter(value, row,
					index) {
				if(row.jobOrderStatus!=constants.CLOSED && row.jobOrderStatus!=constants.FILLED){
					return [
							'<a class="view actionIcons" title="View Detail"><i class="fa fa-search" style="font-size:12px;"></i></a>',
							'<a class="edit actionIcons" title="Edit" href="#/submitals/editsubmitals?submittalId='+row.submittalId+'&pageName='+constants.RECRUITERSREPORT+'"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
							'<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>'
							 ]
							.join('');
					}else
						{
						return [
								'<a class="view actionIcons" title="View Detail"><i class="fa fa-search" style="font-size:12px;"></i></a>',
								($rootScope.rsLoginUser.userRole == constants.Administrator) ? '<a class="edit actionIcons" title="Edit" href="#/submitals/editsubmitals?submittalId='+row.submittalId+'&pageName='+constants.RECRUITERSREPORT+'"><i class="fa fa-edit" style="font-size:12px;"></i></a>':'',
								'<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>'
								 ]
								.join('');
						}
			/*	return [
						'<a class="view actionIcons" title="View Detail"><i class="fa fa-search" style="font-size:12px;"></i></a>',
						'<a class="edit actionIcons" title="Edit" href="#/submitals/editsubmitals?submittalId='+row.submittalId+'&pageName='+constants.RECRUITERSREPORT+'"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
						'<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>'
						 ]
						.join('');*/
			}

			/* Table button actions functionalities */
			window.submittalEvent = {

					'click .view' : function(e, value,
							row, index) {
						var response = $http.get("jobOrder/getSubmittalEventHistoryBySubmittalId/"+row.submittalId);
						response.success(function(data, status,headers, config) 
								{
							var viewsubmittaldata="";
							for(var i=0; i<data.length; i++){
							 viewsubmittaldata=viewsubmittaldata + "<div><b>" + data[i].status + " - <i>" + data[i].strCreatedOn + "</i></b> <span>"+ (data[i].notes!='+"undefined"+'?data[i].notes:"") + "</span></div>";
							}
							 $("#viewsubmittalid").html(viewsubmittaldata);
							$("#submitalstatus").show();
					      });
						response.error(function(data, status, headers, config){
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
					},
				'click .edit' : function(e, value,
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
				'click .remove' : function(e, value,
						row, index) {
					var deletesubmittal="";
					deletesubmittal = deletesubmittal+ "<div><label>Reason <i id='poperr'>Reason Should be more than 20 characters</i></label><textarea placeholder='Reason&nbsp;For&nbsp;Delete' rows='+8+' cols='+40+' id='deleteReason'></textarea></div>";
					deletesubmittal = deletesubmittal+"<div><button type='button' class='popupbtn' onclick='savedelreason("+row.submittalId+")'>Save</button></div>";
					deletesubmittal = deletesubmittal+"<div><button type='button' class='popupbtn' onclick='canceldelreason()'>cancel</button></div>";
					
					deletesubmittal=deletesubmittal+'<script type="text/javascript">'
					 +'function savedelreason(delId){'
					 +'var deleteReason = $("#deleteReason").val();'
					 +'if(deleteReason.length>20){'
					 +'$("#poperr").hide();'
					 +'$.ajax({'
					 +'url:"jobOrder/deleteSubmittal?submittalID=\"+delId+\"&reason=\"+deleteReason+\"",'
					 +'type:"GET",'
					 +'data:null,'
					 +'dataType:"text",'
					 +'success:function(data){'
					 +'$("#submitaldelete").hide();'
					 +'angular.element("#submittal_listcontrollerId").scope().onload();'
					 +'angular.element("#submittal_listcontrollerId").scope().$apply() '
					 +'}'
					 +'});'
					 +'}'
					 +'else{'
					 +'$("#poperr").show();'
					 +'}'
					 +'}'
					 +'function canceldelreason(){'
					 +'$("#submitaldelete").hide();'
					 +'}'
					 +'</script>';
					
					$("#deletesubmittal").html(deletesubmittal);
					$("#submitaldelete").show();
				},
			};
			

				});
}

	
});
	
})(angular);