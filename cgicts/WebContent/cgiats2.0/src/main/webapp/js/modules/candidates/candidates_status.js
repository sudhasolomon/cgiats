;(function(angular) {

    "use strict";
    
    angular.module('candidatesStatus',['ngMaterial', 'ngMessages','ui.bootstrap','angular-highlight','jcs-autoValidate' ])
     .controller('CandidatesStatusController',function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage){
    	
    	 $scope.statusTable = false;
		 $scope.outOfDetailsTable = false;
		 $scope.candidateStatus = [];
		 $scope.outOfDetails = [];
		 $scope.statusData = [];
		 $scope.showGridTable = false;
		 $scope.$storage = $sessionStorage;
    	
    	 $scope.onload = function (){
    		 $scope.Created = {
 					endDate : moment(),
 					startDate : moment().subtract(1, 'month')
 				};
    		 $scope.daterange = {
    	    			today : [moment(),moment()],
    	    			onemonth : [moment().subtract(1, 'month'), moment()],
    	    			twomonths : [moment().subtract(2, 'month'), moment()],
    	    			threemonths : [moment().subtract(3, 'month'), moment()],
    	    			custom : 'custom'
    	    	}
    	     $scope.ranges = {
    	    	        'Last 1 month': [moment().subtract(1, 'month'), moment()],
    	    	        'Last 3 months': [moment().subtract(3, 'month'), moment()],
    	    	        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
    	    	        'Last 1 year': [moment().subtract(12, 'month'), moment()]
    	    	        };
    		 
    		 if($rootScope.candidateEditstatusId){
    				var editCandidateBackButton = true;
    				$rootScope.candidateEditstatusId = null;
    		}
    		 if($stateParams.editCandidate || editCandidateBackButton){
    	    		$scope.Created = $scope.$storage.created;
    				$scope.status = $scope.$storage.status;
    				$scope.candidateStatusView();
    				/*if($stateParams.editCandidate){
						$.growl.warning({title : "success !",message : "candidate updated successfully"});
						}*/
    		 }
    	 }
    	 
    	 $scope.candidateStatusView = function(){
    		 $(".underlay").show();
    		 $scope.$storage.created = $scope.Created;
    		 $scope.$storage.status = $scope.status;
    		 $scope.statusTable = true;
    		 $scope.outOfDetailsTable = true;
    	  var startDate = new Date($scope.Created.startDate);
   		  var fromDate = startDate.getFullYear()+'-'+((startDate.getMonth()+1)<10?(('0')+(startDate.getMonth()+1)):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?('0'+startDate.getDate()):startDate.getDate());
   		  var endDate = new Date($scope.Created.endDate);
   		  var toDate = endDate.getFullYear()+'-'+((endDate.getMonth()+1)<10?('0'+(endDate.getMonth()+1)):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?('0'+endDate.getDate()):endDate.getDate());
    		var viewCritirea = {
    				"startDate" :fromDate,
					 "endDate" :toDate,
    				"status" : $scope.status,
    				"created" : $scope.created
    		}
    		 if($scope.status == "OutOfProject"){
    			 var response = $http.post("candidateStatus/getOutOfProjectDetails",viewCritirea);
    			 response.success(function(data,status, headers, config){
    				 $scope.statusTable = false;
    	    			$scope.outOfDetailsTable = true;
    	    			 outOfDetailsTable();
    	    			 candidateStatusTable();
    	    			 $scope.outOfDetails.bsTableControl.options.data =data;
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
    			
    			
    		 }else{
    			 var response = $http.post("candidateStatus/getCandidateStatusDetails",viewCritirea);
    			 response.success(function(data,status, headers, config){
    				 $scope.outOfDetailsTable = false;
        			 $scope.statusTable = true;
        			 candidateStatusTable();
        			 outOfDetailsTable();
        			 $scope.candidateStatus.bsTableControl.options.data = data;
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
    	 }
    	 function outOfDetailsTable(){
             $scope.outOfDetails.bsTableControl = {
                     options: { 
                         striped: true,
                         pagination: true,
                         paginationVAlign: "both", 
                         pageList: [10,20,50],
                         search: false,
                         sidePagination : 'client',
                         silentSort: false,
                         showColumns: false,
                         showRefresh: false,
                         clickToSelect: false,
                         showToggle: false,
                         maintainSelected: true, 
                         columns: [
                           {
                           field: 'id',
                           title: 'Id',
                           align: 'left',
                            sortable: true,
                            events : window.operateEvents,
							formatter : actionFormatter
                          },                              
                         {
                             field: 'firstName',
                             title: 'Name',
                             align: 'left',
                             
                             sortable: true
                         }, {
                             field: 'title',
                             title: 'Title',
                             align: 'left',
                            
                             sortable: true
                         }, {
                             field: 'location',
                             title: 'Location',
                             align: 'left',
                             
                             sortable: true
                         },
                         {
                             field: 'email',
                             title: 'Email',
                             align: 'left',
                            
                             sortable: true
                         }, {
                             field: 'phoneNumber',
                             title: 'Phone',
                             align: 'left',
                             
                             sortable: true
                         }, {
                             field: 'visaType',
                             title: 'Visa Status',
                             align: 'left',
                             sortable: true
                          }, {
                             field: 'uploadedBy',
                             title: 'Updated By',
                             align: 'left',
                             sortable: true
                         }, {
                             field: 'updatedOn',
                             title: 'Updated On',
                             align: 'left',
                             sortable: true
                         },{
                             field: 'actions',
                             title: 'Actions',
                             align: 'left',
                             sortable: false,
                             events: window.outOfEvents,
                             formatter: outOfFormatter
                         }],
                     }
            
                 };
             function outOfFormatter(value, row,
						index) {
            	 
            	 	var src = row.hot ? "resources/img/star-red.png": "resources/img/star-gray.png";
					var blocksrc = row.block ? "resources/img/star-block.png": "resources/img/star-unblock.png";
					var hotTitle = row.hot ? "Remove from hotlist": "Add to hotlist";
					var blockTitle = row.block ? "Remove from blacklist": "Add to blacklist";
					
					return [ '<a  ><img class="hot actionIcons" id = "hotimg'+ row.id+ '" flex-gt-md="auto" title="'	+ hotTitle+ '" width="12" height="12" src='+ src + ' /></a>',
							'<a class="block actionIcons" title="'+ blockTitle+'" flex-gt-md="auto"><img id = "blockimg'+ row.id+ '" width="12" height="12" src='+ blocksrc+ ' /></a>',
					         '<a class="view actionIcons" title="View Resume"><i class="fa fa-file-word-o"></i></a>']
							.join('');
				}

				window.outOfEvents = {
						'click .view' : function(e, value,
								row, index) {
							var candidateId = row.id;
							var docStats = "others";
							var candidateresumeData=[];
							var objresume = null;
							var response = $http.get('searchResume/resumeByCandidateId?candidateId='
									+ candidateId+'&docStats='+docStats);
							response.success(function(data, status,headers, config) 
									{
								var candresume = JSON.stringify(data);
								 objresume = JSON.parse(candresume);
								$scope.CandidateText = objresume.resumeContent;
								$scope.CandidateKeywords = $scope.keyskills;

								$mdDialog
										.show({
											controller : ResumeDialogController,
											templateUrl : 'views/dialogbox/resumedialogbox.html',
											parent : angular
													.element(document.body),
											targetEvent : e,
											locals : {
												CandidateText : $scope.CandidateText,
												CandidateKeywords : $scope.CandidateKeywords
											},
											clickOutsideToClose : true,
										});
						      });
							response.error(function(data, status, headers, config){
			        			  if(status == constants.FORBIDDEN){
			        				location.href = 'login.html';
			        			  }else{  			  
			        				$state.transitionTo("ErrorPage",{statusvalue  : status});
			        			  }
			        		  });
							
						},
						'click .hot' : function(e, value, row, index) {
							if(row.block){
								$.growl.warning({title : "warning!",message : "Candidate already in Block list make it UnBlock for Hotlist"+ "#"+ row.id});
							}
							else{
							var image = document.getElementById("hotimg"+ row.id);
							$mdDialog.show(	{
												controller : DialogController,
												templateUrl : 'views/dialogbox/hotblock.html',
												parent : angular.element(document.body),
												targetEvent : e,
												locals : {
													rowData : row,
												},
												clickOutsideToClose : true,
											}).then(function(answer) {
												var hotlist = {
													"candidateId" : row.id,
													"reason" : answer
												}
												$(".underlay").show();
									var response = $http.post("searchResume/saveHotComment",hotlist);
									response.success(function(data,status,headers,config) {
									    
					    				 angular.element("#candidatesStatusControllerId").scope().candidateStatusView();
					    				 $(".underlay").hide();
										 $.growl.warning({title : "update !",message : data.statusMessage+ "#"+ row.id});
													});
									response.error(function(data, status, headers, config){
										$(".underlay").hide();
					        			  if(status == constants.FORBIDDEN){
					        				location.href = 'login.html';
					        			  }else{  			  
					        				$state.transitionTo("ErrorPage",{statusvalue  : status});
					        			  }
					        		  });
											},
											function() {
												$scope.reason = 'You cancelled the dialog.';
											});
							}
						},
						'click .block' : function(e, value,	row, index) {
							if(row.hot){
								$.growl.warning({title : "warning!",message : "Candidate already in Hot list make it UnHot for Blocklist"+ "#"+ row.id});
							}
							else{
							$scope.reason = "";
							$mdDialog.show({
												controller : DialogController,
												templateUrl : 'views/dialogbox/hotblock.html',
												parent : angular.element(document.body),
												locals : {
													rowData : row,
												},
												targetEvent : e,
												clickOutsideToClose : true,
											}).then(function(answer) {
												var blocklist = {
													"candidateId" : row.id,
													"reason" : answer
												}
												$(".underlay").show();
											var response = $http.post("searchResume/saveBlockList",blocklist);
												response.success(function(data,	status,	headers,config) {
													 
													 angular.element("#candidatesStatusControllerId").scope().candidateStatusView();
														$(".underlay").hide();
													 $.growl.warning({title : "update !",message : data.statusMessage+ "#"+ row.id});
												});
												response.error(function(data, status, headers, config){
													$(".underlay").hide();
								        			  if(status == constants.FORBIDDEN){
								        				location.href = 'login.html';
								        			  }else{  			  
								        				$state.transitionTo("ErrorPage",{statusvalue  : status});
								        			  }
								        		  });
											},
											function() {
												$scope.reason = 'You cancelled the dialog.';
											});
							
							}
						}
				}
    	 }
    	 function candidateStatusTable(){
    		 $scope.candidateStatus.bsTableControl = {
                     options: { 
                         striped: true,
                         pagination: true,
                         paginationVAlign: "both", 
                         pageList: [10,20,50],
                         search: false,
                         sidePagination : 'client',
                         silentSort: false,
                         showColumns: false,
                         showRefresh: false,
                         clickToSelect: false,
                         showToggle: false,
                         maintainSelected: true, 
                         columns: [
                           {
                           field: 'id',
                           title: 'Id',
                           align: 'left',
                            sortable: true,
                            events : window.operateEvents,
							formatter : actionFormatter
                          },                              
                         {
                             field: 'firstName',
                             title: 'Name',
                             align: 'left',
                             
                             sortable: true
                         }, {
                             field: 'title',
                             title: 'Title',
                             align: 'left',
                            
                             sortable: true
                         }, {
                             field: 'location',
                             title: 'Location',
                             align: 'left',
                             
                             sortable: true
                         },
                         {
                             field: 'email',
                             title: 'Email',
                             align: 'left',
                            
                             sortable: true
                         }, {
                             field: 'createdOn',
                             title: 'Created On',
                             align: 'left',
                             sortable: true
                         }, {
                             field: 'updatedOn',
                             title: 'Updated On',
                             align: 'left',
                             sortable: true
                         },
                         {
                             field: 'status',
                             title: 'Candidate Status',
                             align: 'left',
                            
                             sortable: true
                         },{
                             field: '',
                             title: 'History',
                             sortable: false,
                             align: 'center',
                             events: window.statusEvents,
                             formatter: statusFormatter
                         }],
                     }
            
                 };
             function statusFormatter(value, row,
						index) {
					return [ '<a class="status actionIcons" title="Status History"><i class="fa fa-dot-circle-o" style="font-size:20px;" ></i></a>']
							.join('');
				}

				window.statusEvents = {
						'click .status' : function(e, value, row, index) {
							var candidateId = row.id;
							var response = $http.post('candidateStatus/getCandidateStatusById?candidateId='+ candidateId);
					response.success(function(data, status,headers, config)	{
						$mdDialog.show(	{
											controller : DialogController,
											templateUrl : 'views/dialogbox/statushistorydialogbox.html',
											parent : angular.element(document.body),
											targetEvent : e,
											locals : {
												rowData : data,
											},
											clickOutsideToClose : true,
										});
							});
					response.error(function(data, status, headers, config){
	        			  if(status == constants.FORBIDDEN){
	        				location.href = 'login.html';
	        			  }else{  			  
	        				$state.transitionTo("ErrorPage",{statusvalue  : status});
	        			  }
	        		  });
							
							
						}
				}
    	 }
    	 function DialogController($scope, $mdDialog, rowData) {
				$scope.history = rowData;
				$scope.hide = function() {
					$mdDialog.hide();
				};

				$scope.cancel = function() {
					$mdDialog.cancel();
				};
				$scope.answer = function(answer) {
					if(answer!=undefined){
						 $mdDialog.hide(answer);
						 
					 }else{
						 $scope.msg = "please give reason."
					 }
				};

			}
    	 
    	 function ResumeDialogController($scope,
					$mdDialog, CandidateText,
					CandidateKeywords) {
				$scope.CandidateText = CandidateText;
				
				var strKeywords = CandidateKeywords;
				if(strKeywords != null || strKeywords != undefined){
				strKeywords = strKeywords.replace(/["']/g, "").replace(/[NOT]/g, "(").replace(/[OR]/g, "(").replace(/[OR]/g, "(").replace(/[(]/g, "").replace(/[)]/g, "");
				strKeywords = strKeywords.split(/[ \(,\)]+/).join(',');
				$scope.CandidateKeywords = strKeywords;
				}
				else{
				
				$scope.CandidateKeywords = strKeywords;
				}
				$scope.cancel = function() {
					$mdDialog.cancel();
				};
			}

			
		function actionFormatter(value, row, index) {
				return ['<a class="candidateId actionIcons" title="'+ row.id+ '" flex-gt-md="auto">'+ row.id+'</a>']
				.join('');
		}
		window.operateEvents = {
			'click .candidateId' : function(e,value, row, index) {
				 $rootScope.candidateEditstatusId = row.id;
				$state.transitionTo("EditCandidate",{candidateId : row.id, page:"status"});
			},
		}
     });
})(angular);