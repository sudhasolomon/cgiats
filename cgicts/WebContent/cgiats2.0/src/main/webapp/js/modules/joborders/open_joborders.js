;(function(angular){
	"use strict";
	
	angular.module("openJobOrders",['ngMaterial', 'ngMessages','ui.bootstrap','jcs-autoValidate', 'DatePicker','ngJsonExportExcel'])
    .controller("openJobOrdersController",function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state,$location) {
		
     	$scope.candidates = [];
		$scope.candidates.push({});
    	var candidateData = [];
    	var expansionData = [];
    	$scope.jobOrderTable = false;
    	
    	$scope.candidateDto = {createdUser:$location.search().portalName};
    	
    	$scope.applyjob = function(){
    		$(".underlay").show();
    		
    		
    		var presal = $("input[ng-model='candidateDto.presentRate']").val();
    		var expsal = $("input[ng-model='candidateDto.expectedRate']").val();
    		
    		
    		if(presal == "" || presal == undefined || presal == null)
			{
				$scope.candidateDto.presentRate = presal;
			}
		else
			{
				presal = String(presal).replace(/\D/g,'');
				$scope.candidateDto.presentRate = presal;
			}
    		
    		
    		
    		
    		if(expsal == "" || expsal == undefined || expsal == null)
			{
				$scope.candidateDto.expectedRate = expsal;
			}
		else
			{
			expsal = String(expsal).replace(/\D/g,'');
				$scope.candidateDto.expectedRate = expsal;
			}
    		

    		
    		var formData = new FormData();
			formData.append('candidateDto', angular
					.toJson($scope.candidateDto));
			var attachedFile = $scope.attachment;
			formData.append('file', attachedFile);
			var response = $http.post('jobOrder/saveOrUpdateCandidateForOnlineJobOrder', formData, {transformRequest : angular.identity,headers : {'Content-Type' : undefined}});
			response.success(function (data,status,headers,config){
				if(data.errMsg){
					$scope.candidateDto = {createdUser:$location.search().portalName};
					$scope.uploadresume = "";
					$scope.ApplyJobForm.$setPristine()
					$scope.errmsg = data.errMsg;
				}else{
					$scope.candidateDto = {createdUser:$location.search().portalName};
					$scope.uploadresume = "";
					$scope.ApplyJobForm.$setPristine()
					$("#applyjob").slideUp();
					$("#success").slideDown();
				}
				$(".underlay").hide();
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
    	}
    	
    	$scope.setFile = function(element) {
			$scope
					.$apply(function($scope) {
						$scope.attachment = element.files[0];
					});
			$.growl.success({title : "Info !", message : "Attachment Uploaded Successfully"});
		};
    	
    	$scope.onload = function(){
    		
    		$("div[data-ng-controller='HeaderController']").hide();
    		$("div[class='page-container']").css("margin-top", "0px");
    		$("ul[class='page-sidebar-menu']").hide();
    		$("div[class='page-content']").css("margin-left", "0px");
    		$("div[class='page-content']").css("padding-top", "0px");
    		$("div[class='sub-page-header']").hide();
    		$("footer").hide();
    		
    		
    		
    		$(".upload-control01 .uploadbtn01").click(function() {
				$(this).siblings(".mainuploadbtn").click();
			});
    		
    		
    		$(".mainuploadbtn").change(function(){
				var filename = $(this)[0].files[0].name;
				$(this).siblings(".txtboxdiv").children(".selectedfile").val(filename);
				$scope.uploadresume = filename;
				$(".txtboxdiv .error-msg").css("display", "none");
				$(".txtboxdiv .selectedfile").css("border-color", "#27a4b0");
				});
    		$scope.$on('$viewContentLoaded', function() {   
		        // initialize core components
		        App.initAjax();
		    });
			$scope.searchjoborder();
		}
		
		$scope.searchjoborder = function()
		{
			$("#searchtable").css("display", "block");
			search_joborder();
		}
		
		function search_joborder(){
			$(".underlay").show();
			var response = $http.get("jobOrder/getOpenJobOrders?portalName="+$location.search().portalName);
			response.success(function(data, status,headers, config) 
					{
				$scope.openJobOrdersList = data;
				candidateData = [];
				for (var i = 0; i < data.length; i++) {
					var candidateObj = data[i];
					candidateData.push(candidateObj);
				}
				dispalyTable();
				$(".underlay").hide();
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
			$scope.candidates
					.forEach(function(candidate, index) {
						candidate.openJobOrderBsTableControl = {
							options : {
								data : candidateData || {},
								striped : true,
								pagination : true,
								paginationVAlign : "both",
								pageSize : 10,
								pageList : [ 10 ],
								search : false,
								showColumns : false,
								showRefresh : false,
								clickToSelect : false,
								showToggle : false,
								columns : [
										{
											field : 'title',
											title : 'Job Title',
											align : 'left',
											events : window.openJobOrderViewEvent,
											formatter : viewOpenJobOrder,
											sortable : true
										},
										{
											field : 'numOfPos',
											title : 'Positions',
											align : 'left',

											sortable : true
										},
										{
											field : 'city',
											title : 'City',
											align : 'left',

											sortable : true
										},
										{
											field : 'state',
											title : 'State',
											align : 'left',
											sortable : true
										},
										{
											field : 'strPostedDate',
											title : 'Posted On',
											align : 'left',
											sortable : true
										},
										{
											field : 'apply',
											title : 'Apply',
											align : 'left',
											events : window.applying,
											formatter : applyOpenJobOrder,
											sortable : false
										}],
									
							}
						    };
						
						function viewOpenJobOrder(value, row,
								index){
							return [
							        '<a class="edit actionIcons" title="View Job Details">'+row.title+'</a>'
									 ].join('');
						}
						
						window.openJobOrderViewEvent = {
								'click .edit' : function(e,value, row,
								index){
									$(".underlay").show();
									var response = $http.get('jobOrder/getOpenJobOrdersDescription/'+row.id);
									response.success(function (data,status,headers,config){
										if(data){
											$scope.candidateDto.jobOrderId = row.id;
											$("input[ng-model='jobtitle']").val(row.title);
											$("input[ng-model='jobpositions']").val(row.numOfPos);
											$("input[ng-model='joblocation']").val(row.city +", " + row.state);
											$("input[ng-model='jobposteddate']").val(row.strPostedDate);
											$scope.jobdescription = data.description;
											/*$("textarea[ng-model='jobdescription']").val(data.description);*/
											$("#maintbl").slideUp();
											$("#applyjob").slideUp();
											$("#viewdtls").slideDown();
											$("#submitdiv").slideUp();
										}
										if(row.createdBy == "vpeaden")
											{
											$("#blockbtn").show();
											$("#openbtn").hide();
											}
										else
											{
											$("#blockbtn").hide();
											$("#openbtn").show();
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
						};
						
						function applyOpenJobOrder(value, row,
								index){
							return [
							        '<a class="copy actionIcons" title="Apply">Apply</a>'
									 ]
									.join('');
							
						
						}
						
						window.applying = {
								'click .copy' : function(e,value, row,
								index){
									if(row.createdBy == "vpeaden")
										{
										$scope.candidateDto.jobOrderId = row.id;
										$("#maintbl").slideUp();
										$("#sendresume").slideDown();
										$("#viewdtls").slideUp();
										$("#submitdiv").slideUp();
										$scope.errmsg = "";
										}
									else
										{
										$scope.candidateDto.jobOrderId = row.id;
										$("#maintbl").slideUp();
										$("#applyjob").slideDown();
										$("#viewdtls").slideUp();
										$("#submitdiv").slideUp();
										$scope.errmsg = "";
										}
								}
						};

							});
			
			
						}
		
		$scope.attachfile = function()
		{
			alert();
		}
		$scope.backtojoborder = function()
		{
			$("#maintbl").slideDown();
			$("#applyjob").slideUp();
			$("#viewdtls").slideUp();
			$("#success").slideUp();
			$("#success01").slideUp();
			$("#addcandidatefields").slideUp();
			$("#submitdiv").slideDown();
			$("#openjobordertable").slideDown();
			$("#sendresume").slideUp();
			$scope.errmsg = "";
		}
		
		$scope.applytojoborder = function()
		{
			$("#maintbl").slideUp();
			$("#applyjob").slideDown();
			$("#viewdtls").slideUp();
			$scope.errmsg = "";
		}
		
		
		
		$scope.applytojoborderblk = function()
		{
			$("#maintbl").slideUp();
			$("#sendresume").slideDown();
			$("#viewdtls").slideUp();
			$scope.errmsg = "";
		}

	});
	
})(angular);