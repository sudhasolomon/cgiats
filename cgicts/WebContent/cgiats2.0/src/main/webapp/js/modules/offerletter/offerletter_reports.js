;(function(angular){
	"use strict";
	
	angular.module("offerLetterReports",['ngMaterial', 'ngMessages','ui.bootstrap','jcs-autoValidate', 'DatePicker','ngJsonExportExcel'])
    .controller("offerLetterReportscontroller",function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state,mailService,dateRangeService) {
		
    	
    	$('.ranges').empty();
    	/*var priorityval = $("#priority").val();
    	var statusval = $("#status").val();
    	var jobtypeval = $("#jobtype").val();
    	var jobbelongsval = $("#jobbelongsto").val();*/
    	
    	$scope.offerLetterReports = [];
		$scope.offerLetterReports.push({});
    	var offerLetterData = [];
    	
    	$scope.currentDateWithTime = new Date();
    	var expansionData = [];
    	$scope.jobOrderTable = false;
		$scope.onload = function(){
			
			$scope.$on('$viewContentLoaded', function() {   
		        // initialize core components
		        App.initAjax();
		    });  

			
			
		//$scope.Created = { endDate: moment(), startDate: moment().subtract(6, 'year')};
			$scope.Created = { endDate: moment(), startDate:moment().subtract(1, 'month')};
			$scope.searchjoborder();
		if($rootScope.jobOrderInserted){
			$scope.Created = { endDate: moment(), startDate: moment()};
			$rootScope.jobOrderInserted = false;
			$scope.searchjoborder();
		}
		
        $scope.ranges = {
        'All Time'  : [moment({'year':2012}).set('year',2012).set('month',5).set('date',1), moment()],
        'Today': [moment(),moment()], 
        'Last 1 month': [moment().subtract(1, 'month'), moment()],
        'Last 3 months': [moment().subtract(3, 'month'), moment()],
        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
        'Last 1 year': [moment().subtract(12, 'month'), moment()]
        };   

		}
		
	/*	$scope.exportData = function(){
//			var response = $http.get("jobOrder/exportJobOrders?jobOrderList="+angular.toJson($scope.searchFields));
			 $window.location = 'jobOrder/exportJobOrders?strPriorities='+$scope.searchFields.strPriorities+'&strStatuses='+$scope.searchFields.strStatuses
			 +'&strJobTypes='+$scope.searchFields.strJobTypes+'&startDate='+ $scope.formatteddate(moment().subtract(6, 'year'))+'&endDate='+ $scope.formatteddate(moment())
			 +'&strJobBelongsTo='+$scope.searchFields.strJobBelongsTo+'&jobOrderId='+$scope.searchFields.jobOrderId;
		}*/
		
		 $scope.getTimeFnc = function(){
				$scope.currentDateWithTime = new Date();
			}

		
		$scope.searchjoborder = function()
		{
			$("#searchtable").css("display", "block");
			search_joborder();
		}
		
		/*//This is to formate dates
		 $scope.formatteddate = function(inputData){
	      	  var expDate = new Date(inputData);
	      	 var month = '' + (expDate.getMonth() + 1);
	           var day = '' + expDate.getDate();
	          var  year = expDate.getFullYear();
	      	  if (month.length < 2) month = '0' + month;
	      	    if (day.length < 2) day = '0' + day;
	      	   return [year, month, day].join('-');
	        };*/
		
		function reloadDataTable(){
//			alert('');
//			$scope.offerLetterReports = [];
//			$scope.offerLetterReports.push({});
			offerLetterData = [];
				dispalyTable();
				$timeout(function() {
					search_joborder();
				}, 400);
	        	
		};
		
		function search_joborder(){
			$(".underlay").show();
//			alert(JSON.stringify($scope.Created));
			$scope.searchFields= {
					"timeIntervalMap" :$scope.Created
			}
			
			var dateRangeActiveValue = $(".ranges").children("ul").children("li[class='active']").text();
			if(!dateRangeActiveValue){
				dateRangeActiveValue = constants.LAST_ONE_MONTH;
			}else if(!$('#offerLetterRangesId').val()){
				dateRangeActiveValue = constants.LAST_ONE_MONTH;
			}
			
			if(dateRangeService.selectedDateAction(dateRangeActiveValue,$scope.Created)){
			$scope.searchFields.timeIntervalMap = dateRangeService.selectedDateAction(dateRangeActiveValue,$scope.Created);
			}
			
			if($('#offerLetterRangesId').val() && $('#offerLetterRangesId').val()!=''){
				$scope.searchFields.timeIntervalMap.startDate = $('#offerLetterRangesId').val().split(' ')[0];
				$scope.searchFields.timeIntervalMap.endDate = $('#offerLetterRangesId').val().split(' ')[2];
			}
			//alert("json"+JSON.stringify($scope.searchFields));
			var response = $http.post("offerLetter/getAllOfferLettersByInterval",$scope.searchFields);
			response.success(function(data, status,headers, config) 
					{
				//alert("data"+data);
				$scope.offerLettersList = data;
				offerLetterData = data;
				$scope.exportOfferLettersList = [];
//				jobOrderId: 'ID',candidateId: 'Candidate ID',fullName: 'Candidate Name',email: 'Email',address: 'Location',companyName: 'Company Name',
//				bdmName: 'DM',recruiterName: 'Recruiter',status: 'Status',updatedBy: 'Updated By',strUpdatedOn: 'Updated On'
				
				for (var i = 0; i < data.length; i++) {
					var obj = {jobOrderId:data[i].jobOrderId,
							candidateId: data[i].candidateId,fullName: data[i].fullName,
							email: data[i].email,address:data[i].address,companyName: data[i].companyName,bdmName: data[i].bdmName,recruiterName: data[i].recruiterName,status: data[i].status,
							updatedBy: data[i].updatedBy,strUpdatedOn: data[i].strUpdatedOn};
					$scope.exportOfferLettersList.push(obj);
				}
				
				dispalyTable();
				$(".underlay").hide();
				$timeout(function() {
					blockUI.stop();
				}, 1000);
		      });
			response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
			
		}
		$scope.jobOrderTable = true;
		
		function dispalyTable() {
			$scope.offerLetterReports
					.forEach(function(candidate, index) {
						candidate.bsTableControl = {
							options : {
								data : offerLetterData || {},
								// cache: false,
								striped : true,
								pagination : true,
								paginationVAlign : "both",
								pageSize : 10,
								pageList : [ 10, 20, 50 ],
								search : false,
								showColumns : false,
								showRefresh : false,
								// minimumCountColumns: 2,
								clickToSelect : false,
								showToggle : false,
								detailView : true,
								maintainSelected : true,
								columns : [
										{
											field : 'jobOrderId',
											title : 'Job Order ID',
											align : 'left',
											formatter : editJobOrder,
											sortable : true
										},
										{
											field : 'candidateId',
											title : 'Candidate ID',
											align : 'left',
											formatter : editCandidate,
											sortable : true
										},
										{
											field : 'fullName',
											title : 'Candidate Name',
											align : 'left',
											sortable : true
										},
										{
											field : 'email',
											title : 'Email',
											align : 'left',
											sortable : true
										},
										{
											field : 'address',
											title : 'Location',
											align : 'left',
											sortable : true
										},
										{
											field : 'companyName',
											title : 'Company Name',
											align : 'left',
											sortable : true
										},
										{
											field : 'bdmName',
											title : 'DM',
											align : 'left',
											sortable : true
										},
										{
											field : 'recruiterName',
											title : 'Recruiter',
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
											field : 'updatedBy',
											title : 'Updated By',
											align : 'left',
											sortable : true
										},
										{
											field : 'strUpdatedOn',
											title : 'Updated On',
											align : 'left',
											sortable : true
										},
										{
											field : 'actions',
											title : 'Actions',
											align : 'left',
											sortable : false,
											events : window.offerLetterReportsOperateEvents,
											formatter : actionFormatter
										} ],
										
										
										/*onColumnSearch: function(field, text) {
											alert("dfgd");
								        	//return false;
										},*/
										
										onExpandRow : function(index,
												row, $detail) {
											var offerLetterId = row.offerLetterId;
											var response = $http
													.get('offerLetter/getHistoryByOfferLetterId/'
															+ offerLetterId);
											response
													.success(function(
															data,
															status,
															headers,
															config) {
														//alert("data::"+JSON.stringify(data));
														expansionData = [];
														for (var i = 0; i < data.length; i++) {
															var submittalObj = data[i];
															expansionData
																	.push(submittalObj);
														}
														var tableres = '<div class="tbdiv">';
														 tableres += '<div class="tbseconddiv"><button type="button" class="btn btn-primary blue" onclick="editOfferLetter('+row.jobOrderId+','+row.candidateId+')"><i class="fa fa-pencil" style="font-size:12px;"></i>&nbsp;Edit OfferLetter</button></div>';
														 tableres += 
														'<div class="tbseconddiv"><button type="button" class="btn btn-primary blue" onclick="updateOfferLetterStatus('+row.offerLetterId+')"><i class="fa fa-pencil" style="font-size:12px;"></i>&nbsp;Update OfferLetter Status</button></div><div class="clearboth"></div></div>'
														
														if (expansionData != '') {
															tableres +='<table class="innertable">'
																	+ '<tr><th>Created By</th><th>Created On</th><th>Status</th><th>Notes</th></tr>'
															for (var i = 0; i < expansionData.length; i++) {
																tableres += '<tr><td style="width:25%">'
																		+(expansionData[i].createdBy)
																		+ '</td>'
																		+ '<td style="width:25%">'
																		+(expansionData[i].strStatusCreatedOn)
																		+ '</td>'
																		+ '<td style="width:25%">'
																		+ expansionData[i].status
																		+ '</td>'
																		+ '<td style="width:25%">'
																		+ expansionData[i].notes 
																		+ '</td>'
																		
															}
														} else {
															tableres += '<table class="innertable">'
																	+ '<tr><th>Created On</th><th>Update on</th><th>Created By</th><th>Status</th><th>Candidate</th></tr>'
																	+ '<tr><td colspan="5" align="center">No Submittals Found</td></tr>'
														}
														tableres += '</table><script type="text/javascript">function updateOfferLetterStatus(offerLetterId){window.location="#/offerletter/offerletterstatus?offerLetterId="+offerLetterId}</script>'
														 +'<script type="text/javascript">function editOfferLetter(jobOrderId,candidateId){window.location="#/offerletter/createofferletter?jobOrderId="+jobOrderId+"&candidateId="+candidateId}</script>';
														
														$detail
																.html(tableres);
												
													});
											response.error(function(data, status, headers, config){
									  			  if(status == constants.FORBIDDEN){
									  				location.href = 'login.html';
									  			  }else{  			  
									  				$state.transitionTo("ErrorPage",{statusvalue  : status});
									  			  }
									  		  });
										},
										
										
										
							}
						    };
						
						
						
						/* Table button action formatters */

						function actionFormatter(value, row,
								index) {
							return [
									
									 '<a class="edit actionIcons" title="Edit" href="#/offerletter/createofferletter?candidateId='+row.candidateId+'&jobOrderId='+row.jobOrderId+'"><i class="fa fa-edit" style="font-size:12px;"></i></a>', 
									 '<a class="email actionIcons" title="Send OfferLetter" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>',
									 '<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>'
									 
									 ]
									.join('');
						}
						
						function editJobOrder(value, row,
								index){
							return [
									
							        '<a class="copy actionIcons" title="Edit" href="#/joborders/editJobOrders/'+row.jobOrderId+'">'+row.jobOrderId+'</a> '
									 ]
									.join('');
						}
						
						function editCandidate(value, row,
								index){
							if($rootScope.rsLoginUser.userRole != constants.Recruiter){
							return [
									
							        '<a class="copy actionIcons" title="Edit" href="#/candidates/edit/'+row.candidateId+'/search">'+row.candidateId+'</a> '
									 ]
									.join('');
							}else{
								return [
								        '<lable>'+row.jobOrderId+'</label> '
										 ]
										.join('');
							}
						}

						/* Table button actions functionalities */
						window.offerLetterReportsOperateEvents = {

							/* View Resume details */
						/*	'click .view' : function(e, value,
									row, index) {
								var jobOrderId = row.jobOrderId;
											$mdDialog
													.show({
														controller : DialogController,
														templateUrl : 'views/dialogbox/joborderdetails.html',
														parent : angular
																.element(document.body),
														targetEvent : e,
														locals : {
															rowData : row,
														},
														clickOutsideToClose : true,
													});
							},
							*/
							'click .email' : function(e,value, row, index) {
								$(".underlay").show();
								var response = $http.get("offerLetter/sendOfferLetter/"+row.offerLetterId);
								response.success(function(data, status,headers, config) 
										{
									$(".underlay").hide();
									$.growl.success({title : "Info !", message : "Offer-letter Send Successfully"});
							      });
								response.error(function(data, status, headers, config){
						  			  if(status == constants.FORBIDDEN){
						  				location.href = 'login.html';
						  			  }else{  			  
						  				$state.transitionTo("ErrorPage",{statusvalue  : status});
						  			  }
						  		  });
								
//								var email = row.email;
//
//								mailService.sendMail(email, false);

							},
							
					/*		'click .copy' : function(e, value,
									row, index) {
								var jobOrderId = row.jobOrderId;
								var response = $http
										.get('jobOrder/copyJobOrder/'+ jobOrderId);
								response
										.success(function(data,
												status,
												headers, config) {
											search_joborder();
							   	     		$.growl	.notice({message : " Job Order Copied Successfully "});
										});
								response
										.error(function(data,
												status,
												headers, config) {
//											alert("Exception Details ::::"
//													+ data);
											$state
											.transitionTo(
													"ErrorPage",
														{
															statusvalue  : status
													});
										});
							},*/
						
							/*'click .download' : function(e,
									value, row, index) {
								
								var jobOrderId = row.jobOrderId;
								var candidateresumeData=[];
								var objresume = null;
								var response = $http.post('jobOrder/downloadJobOrder',row);
								
								 $window.location = 'jobOrder/downloadJobOrder/'+jobOrderId;
						
							},*/
						/*	'click .edit' : function(e, value,
									row, index) {
								$state.transitionTo("EditCandidate",{candidateId : row.id});

							},*/

							'click .remove' : function(e,
									value, row, index) {

								$mdDialog
										.show(
												{
													controller : DialogController,
													templateUrl : 'views/dialogbox/deleteofferletter.html',
													parent : angular
															.element(document.body),
													targetEvent : e,
													locals : {
														rowData : row,
													},
													clickOutsideToClose : true,
												});
							},
							/*
							'click .plus' : function(e,
									value, row, index) {
								var job_orderId = row.jobOrderId;
								window.location="#/submitals/createsubmitals?jobOrder="+job_orderId;
							}*/
						};
						
						function DialogController($scope,
								$mdDialog, rowData) {
							$scope.row = rowData;
							var offerLetterId = rowData.offerLetterId;
							$scope.hide = function() {
								$mdDialog.hide();
							};

							$scope.cancel = function() {
								$mdDialog.cancel();
							};

	                      $scope.deleteQuery = function() {
	                    	  
	                    	  var deletereason = $scope.reason;
	                    	  if(deletereason.length>20){
	                    		 	  var response = $http.get('offerLetter/deleteOfferLetter?offerLetterId='+offerLetterId +'&reason=' +deletereason);
					   	          response.success(function(data, status, headers, config) {
					   	        	var dateRangeActiveValue = $(".ranges").children("ul").children("li[class='active']").text();
					   	        	if(!dateRangeActiveValue){
					   					dateRangeActiveValue = constants.LAST_ONE_MONTH;
					   				}
//					   	        	alert(dateRangeActiveValue);
					   	        	if(dateRangeActiveValue==constants.LAST_ONE_MONTH){
					   	        	 $state.go($state.current, {}, {reload: true});
					   				}else{
					   					reloadDataTable();
					   				}
					   	     		$.growl	.notice({title : "Deleted", message : rowData.offerLetterId + " Successfully Deleted "});
					   	  		   });
					   	          
					   	   	response.error(function(data, status, headers, config){
					  			  if(status == constants.FORBIDDEN){
					  				location.href = 'login.html';
					  			  }else{  			  
					  				$state.transitionTo("ErrorPage",{statusvalue  : status});
					  			  }
					  		  });
					   	  		
						    	    $mdDialog.hide();
	                    	  }else{
	                    		  $scope.deleteErrMsg = 'Reason should be minimum 20 charcters.';
	                    			  
	                    	  }
						    	  };

						}


							});
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			$scope.resetAction = function(){
				$("div[class='ranges'] li").removeClass("active");
				$("div[class='ranges'] li:contains('Last 1 month')").addClass("active");
				$scope.Created = { endDate: moment(), startDate:moment().subtract(1, 'month')};
				
				var currentDate = new Date();
				var oneMonthBeforeDate = moment().subtract(1, 'month').toDate();
				/*var month = ( '0' + (fulldate.getMonth()+1) ).slice( -2 );
				var date = ( '0' + (fulldate.getDate()) ).slice( -2 );
				var year = fulldate.getFullYear();
				var totaldate = year + "-" + month + "-" + date;*/
				var finaldate = getDate(oneMonthBeforeDate) + " - " + getDate(currentDate);
				$("input[type='daterange']").val(finaldate);
			};
			
			function getDate(fulldate){
				var month = ( '0' + (fulldate.getMonth()+1) ).slice( -2 );
				var date = ( '0' + (fulldate.getDate()) ).slice( -2 );
				var year = fulldate.getFullYear();
				var totaldate = year + "-" + month + "-" + date;
				return totaldate;
			}
			
			/*$scope.myjobordersreset = function()
			{
				$("div[class='ranges'] li").removeClass("active");
				$("div[class='ranges'] li:contains('Today')").addClass("active");
				$scope.Created = { endDate: moment(), startDate: moment()};
				var fulldate = new Date();
				var month = ( '0' + (fulldate.getMonth()+1) ).slice( -2 );
				var date = ( '0' + (fulldate.getDate()) ).slice( -2 );
				var year = fulldate.getFullYear();
				var totaldate = year + "-" + month + "-" + date;
				var finaldate = totaldate + " - " + totaldate; 
				$("input[type='daterange']").val(finaldate);
				$scope.joborderid = "";
				
				
				var elementarry = priorityval.split(",");
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
				
				
				
				
				
				var elementarry = statusval.split(",");
				var inputcheck = $("#status").siblings(".ddlist").children("li").children("input");
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
				$("#status").val(statusval);
				$("#status").siblings(".blankmsg").hide();
				
				
				
				
				var elementarry = jobtypeval.split(",");
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
				
				
				
				
				
				var elementarry = jobbelongsval.split(",");
				var inputcheck = $("#jobbelongsto").siblings(".ddlist").children("li").children("input");
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
				$("#jobbelongsto").val(jobbelongsval);
				$("#jobbelongsto").siblings(".blankmsg").show();
				
				
			}*/
			
			
			
			
			
			
						}

	});
	
})(angular);