;(function(angular){
	"use strict";	
	angular.module("consultantinfopage",['ui.bootstrap'])
	.controller("consultantinfocontroller",function($rootScope, $scope, blockUI, $http, $timeout, $location,
			$filter, $window, $state, $stateParams, $sessionStorage){
		
		
		$scope.clientTable = false;
		$scope.candidate = [];
		$scope.onload = function()
		{
			//alert("$rootScope.candidateid  "+$rootScope.candidateid);
			//alert("$rootScope.candidateinfo id  "+$rootScope.candidateinfoid);
			$(".underlay").show();
			var response = $http.post("consultantInfo/getAllCandidateIds");
			response.success(function(data, status, headers, config){
//				alert("success data"+JSON.stringify(data));
				
				 $scope.candidateIds = data.candidateIds;
			});
			response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
			
			var response = $http.post("consultantInfo/getAllClientInfo");
			response.success(function(data, status, headers, config){
				//alert("success data"+JSON.stringify(data));
				
				
				
				angular.forEach(data, function(obj, key){
					if(obj.salaryRate)
						{
						obj.salaryRate = obj.salaryRate.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						obj.salaryRate = '$ ' + obj.salaryRate;
						}
					else
						{
						}
					
					
				});
				
				$scope.clientTable = true;
				
				dispalyTable();
				$scope.candidate.bsTableControl.options.data =data; 
				$(".underlay").hide();
				if($rootScope.candidateids !=null && $stateParams.saveOrUpdate || $rootScope.candidateids != undefined && $stateParams.saveOrUpdate){
					if($rootScope.candidateinfoid != null || $rootScope.candidateinfoid != undefined){
						$.growl.success({title : "Info !", message : "Project Details updated Successfully"});
					}else{
						$.growl.success({title : "Info !", message : "Project Details Saved Successfully"});
					}
					$rootScope.candidateids = null;
					$rootScope.candidateinfoid = null;
				}
				
			});
			response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
			
			
		}
		
		$scope.fillprojdetails = function()
		{
			$rootScope.candidateids = $scope.candidateid;
			$state.transitionTo("fillprojectdetails",{id : $scope.candidateid});
		}
		
		
		function dispalyTable() {
			//alert("pagining data"+JSON.stringify(pagingData));
			
			
						$scope.candidate.bsTableControl = {
							options : {
								pagination : true,
								paginationVAlign : "top",
								sidePagination : 'client',
								silentSort: false,
								pageList : [ 10, 20, 50 ],
								search : false,
								//showFooter : true,
								showColumns : false,
								showRefresh : false,
								clickToSelect : false,
								showToggle : false,
								columns : [
										{
											field : 'candidateId',
											title : 'Candidate Id',
											align : 'left',
											events : window.consultantOperateEvents,
											formatter : consultantActionFormatter,
											sortable : true
										},
										{
											field : 'jobOrderId',
											title : 'Job Order Id',
											align : 'left',

											sortable : true
										},
										{
											field : 'candidateInfoId',
											title : 'candidate Info Id',
											align : 'left',
											visible : false,
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
											field : 'clientName',
											title : 'Client Name',
											align : 'left',
											sortable : true
										},
										{
											field : 'salaryRate',
											title : 'Pay Rate',
											align : 'left',
											sortable : true
										},
										{
											field : 'startDate',
											title : 'Started Date',
											align : 'left',
											sortable : true
										},
										{
											field : 'endDate',
											title : 'End Date',
											align : 'left',
											sortable : true
										} ],
										
							
							}
						};
						
						function consultantActionFormatter(value, row, index) {
								return ['<a class="candidateId actionIcons" title="'+ row.candidateId+ '" flex-gt-md="auto">'+ row.candidateId+'</a>']
								.join('');
						}
						window.consultantOperateEvents = {
							'click .candidateId' : function(e,value, row, index) {
								//alert("row data "+JSON.stringify(row));
								$rootScope.candidateids = row.candidateId;
								$rootScope.candidateinfoid = row.candidateInfoId;
								$state.transitionTo("fillprojectdetails",{id : row.candidateId, infoId : row.candidateInfoId});
							},
						}
		}
		
	});
	
	
})(angular);