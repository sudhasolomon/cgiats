;(function(angular){
	
	
			"use strict";
			angular.module('indiaSubmitalModule',[])
			.controller('indiaSubmitalController',function($scope, $http, $location, $state, $mdDialog){
				$scope.indiaSubmitalTable=false;
				 $scope.indiaSubmital = [];
				$scope.onload= function(){
					$scope.userName = $location.search().statsUser;
					$scope.startDate = $location.search().fromDate;
					$scope.endDate = $location.search().toDate;
					$scope.indiaSubmitalTable=true;
					$scope.obj = {
							"startEntryDate" : $scope.startDate,
							"endEntryDate"   : $scope.endDate,
							"submittalBdms"	: $scope.userName
					}
					indiaSubmitalTable();
					//alert("json data"+ JSON.stringify($scope.obj));
					var response = $http.post('indiaReports/getSubmittalDetails', $scope.obj);
					response.success(function(data, status, headers, config){
						$scope.indiaSubmital.SubmitalTableControl.options.data = data.submittalDetails;
					});
					response.error(function(){
						 if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
					});
					
				}
				
	    	function indiaSubmitalTable(){
		    		
		            $scope.indiaSubmital.SubmitalTableControl = {
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
		                        columns: [
		                        {
		                            field: 'createdOn',
		                            title: 'Created On',
		                            align: 'left',
		                            
		                        }, {
		                            field: 'updatedOn',
		                            title: 'Updated On',
		                            align: 'left',
		                           
		                        }, {
		                            field: 'createdBy',
		                            title: 'Created By',
		                            align: 'left',
		                            
		                        }, {
		                            field: 'status',
		                            title: 'Status',
		                            align: 'left',
		                            
		                        }, {
		                            field: 'candidateName',
		                            title: 'Candidate',
		                            align: 'left',
		                        }, {
		                            field: 'actions',
		                            title: 'Actions',
		                            align: 'left',
		                            events : window.operateEvents,
									formatter : actionFormatter
		                        },]
		                    }
		            };
		            
		            function actionFormatter(value, row,
							index) {
		            	
		            	return [
								
								'<a class="view actionIcons" title="View Details" flex-gt-md="auto"><i class="fa fa-search" style="font-size:12px;"></i></a>',
								'<a class="edit actionIcons" title="Edit JobOrder" href="#/india_submitals/edit_indiasubmitals?submittalId='+row.submittalId+'&pageName='+constants.INDIASUBMITTALSTATUSMODULE+'"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
								'<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>',
								 ]
								.join('');
		            	
		            }
		            
		    		window.operateEvents = {
		    				'click .view' : function(e, value,
									row, index) {
		    					
		    					var response = $http.get('India_JobOrder/getSubmittalEventHistoryBySubmittalId/'+row.submittalId);
		    					response.success(function(data, status, headers, config){
		    						var viewsubmittaldata="";
		    						for(var i=0; i<data.length; i++){
		    							 viewsubmittaldata=viewsubmittaldata + "<div><b>" + data[i].status + " - <i>" + data[i].strCreatedOn + "</i></b> <span>"+ (data[i].notes!='+"undefined"+'?data[i].notes:"") + "</span></div>";
										 $("#viewsubmittalid").html(viewsubmittaldata);
										 $("#submitalstatus").show();
		    						}
		    						
		    						
		    					});
		    					response.error(function(){
		    						alert("error");
		    					});
		    				},
		    				
		    				'click .remove' : function(e, value,
									row, index) {
		    					$mdDialog
								.show(
										{
											controller : SubmittalDialogController,
											templateUrl : 'views/dialogbox/submitaldeletedialogbox.html',
											parent : angular
													.element(document.body),
											targetEvent : e,
											locals : {
												rowData : row,
											},
											clickOutsideToClose : true,
										})
											.then(function(answer) {
												var response = $http.get("India_JobOrder/deleteSubmittal?submittalID="+row.submittalId+"&reason="+answer);
						    					response.success(function(data, status, headers, config){
						    						$.growl	.success({title : "Deleted Successfully"+ "#"+ row.submittalId});
						    						$scope.onload();
						    					});
						    					response.error(function(data, status, headers, config){
								        			  if(status == constants.FORBIDDEN){
								        				location.href = 'login.html';
								        			  }else{  			  
								        				  $state.transitionTo("ErrorPage",{statusvalue  : status});
								        			  }
								        		  });
											});
		    				},
		    		};
		    		
		    		function SubmittalDialogController($scope,
							$mdDialog, rowData) {
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
							//$mdDialog.hide(answer);
						};
		    		}
		    
		    	}
				
			});
})(angular);
			