;(function(angular) {

    "use strict";
    
    angular.module('indiacandidate',[])
    .controller('IndiaCandidateController',function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage, mailService){
    	
    	$scope.$storage = $sessionStorage;
    	$scope.candidate = [];
    	$scope.indiaTable = true;
    	$scope.pageNumber = 1;
		$scope.pageSize = 10;
		var expansionData = [];
		$scope.mapCheck = [];
		$scope.dataFromChecked = [];
		$scope.selectionCheck = [];
		$scope.smsCheck = [];
		$scope.orderName = "";
		$scope.orderType = "";
		$rootScope.indiaPagination = false;
		var newCandidateAddedResponse = $stateParams.newCandidate;
		var editCandidateAddedResponse = $stateParams.editCandidate;
    	$scope.onload = function(){
    		 var criteria = {
    				 "pageNumber" : $scope.pageNumber,
    				 "pageSize"   : $scope.pageSize,
    				 "fieldName"  : $scope.orderName,
    				 "sortName"   : $scope.orderType
    		 }
    		var response = $http.post("IndiaCandidates/getCandidates",criteria);
    		response.success(function(data, headers, status, config){
    			$scope.$storage.pageNumber = $scope.pageNumber;
				$scope.$storage.pageSize = $scope.pageSize;
    			dispalyIndiaTable();
				$scope.candidate.bsTableControl.options.data =data; 
				$scope.candidate.bsTableControl.options.pageNumber =$scope.$storage.pageNumber;
				$scope.candidate.bsTableControl.options.pageSize =$scope.$storage.pageSize;
				$scope.candidate.bsTableControl.options.totalRows =data[0].totalRecords;
				 if($rootScope.indiaPagination){
	    			    $scope.candidate.bsTableControl.options.responseHandler(data);} 
				 $rootScope.indiaPagination = false;
				
    		});
    		response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
    		if(newCandidateAddedResponse){
    			$.growl.success({title : "success !",message : "New candidate has been added successfully"});
    			newCandidateAddedResponse = false;
    		}
    		if(editCandidateAddedResponse){
    			$.growl.success({title : "success !",message : "candidate updated successfully"});
    			editCandidateAddedResponse = false;
    		}
    		
    	}
    	
    	$scope.addCandidate = function(){
    		$state.transitionTo("AddEditIndiaCandidates");
    	}
    	
    	
    	
    	
    	function dispalyIndiaTable() {
			$scope.candidate.bsTableControl = {
				options : {
					striped : true,
					pagination : true,
					paginationVAlign : "both",
					sidePagination : 'server',
					silentSort: false,
					pageList : [ 10, 20, 50 ],
					search : false,
					showColumns : false,
					showRefresh : false,
					clickToSelect : false,
					showToggle : false,
					detailView : true,
					maintainSelected : true,
					columns : [
							{
								field : 'state',
								checkbox : true,
							},{
								field : 'id',
								title : 'Candidate ID',
								align : 'left',
								sortable : true
							},
							{
								field : 'firstName',
								title : 'Name',
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
								field : 'location',
								title : 'Location',
								align : 'left',

								sortable : true
							},
							{
								field : 'createdOn',
								title : 'Created',
								align : 'left',
								sortable : true
							},
							{
								field : 'updatedOn',
								title : 'Updated',
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
								field : 'resume',
								title : 'Resume',
								align : 'center',
								sortable : false,
								events : window.indiaResumeEvents,
								formatter : indiaResumeFormatter
							},
							{
								field : 'actions',
								title : 'Actions',
								align : 'left',
								sortable : false,
								events : window.indiaOperateEvents,
								formatter : indiaActionFormatter
							} ],

					/* Table row Expansion */
							
				   onPageChange: function (number, size) {
					  
					   $scope.pageNumber = number;
					   $scope.pageSize = size;
					   $rootScope.indiaPagination = true;
					   angular.element("#indiaCandidateControllerId").scope().onload(); 
					},
					 onSort: function (name, order) {
						 
						 if(!$scope.orderType || ($scope.orderName && $scope.orderName != name)){
                   		  $scope.orderType = order;
                   	  }
                   	  if($scope.orderName && $scope.orderName == name){
                   		  if($scope.orderType && $scope.orderType == constants.ASC){
                   			  $scope.orderType = constants.DESC;
                   		  }else{
                   			  $scope.orderType = constants.ASC;
                   		  }
                   	  }
                   	  $scope.orderName = name;
                   	angular.element("#indiaCandidateControllerId").scope().onload(); 
					/*	 var response = $http.post("IndiaCandidates/getCandidates?pageNumber="+$scope.$storage.pageNumber+"&pageSize="+ $scope.$storage.pageSize+"&orderName="+name+"&orderType="+order);
							response.success(function(data, status,headers, config) 
									{
								dispalyIndiaTable();
								$scope.candidate.bsTableControl.options.data =data; 
								$scope.candidate.bsTableControl.options.pageNumber =$scope.$storage.pageNumber;
								$scope.candidate.bsTableControl.options.pageSize =$scope.$storage.pageSize;
								$scope.candidate.bsTableControl.options.totalRows =data[0].totalRecords;
						      });  
							response.error(function(data, status, headers, config){
			        			  if(status == constants.FORBIDDEN){
			        				location.href = 'login.html';
			        			  }else{  			  
			        				$state.transitionTo("ErrorPage",{statusvalue  : status});
			        			  }
			        		  });*/
				        },
					onExpandRow : function(index,row, $detail) {
						var candidateId = row.id;
						var response = $http.post('IndiaCandidates/getSubmittals?candidateId='+candidateId);
						response.success(function(data,status,headers,config) {
									expansionData = [];
									for (var i = 0; i < data.length; i++) {
										var submittalObj = data[i];
										expansionData.push(submittalObj);
									}
									var tableres = '';
									if (expansionData != '') {
										tableres = '<table class="innertable">'
												+ '<tr><th>JobOrder Id</th><th>Submittal Id</th><th>Created On</th><th>Created By</th><th>Status</th></tr>'
										for (var i = 0; i < expansionData.length; i++) {
											tableres += '<tr><td>'
													+ expansionData[i].jobOrderId
													+ '</td>'
													+ '<td>'
													+ expansionData[i].submittalId
													+ '</td>'
													+ '<td>'
													+ expansionData[i].createdOn
													+ '</td>'
													+ '<td>'
													+ expansionData[i].createdBy
													+ '</td>'
													+ '<td>'
													+ expansionData[i].status
													+ '</td>'
													+'</tr>'
										}
									} else {
										tableres = '<table class="innertable">'
												+ '<tr><th>JobOrder Id</th><th>Submittal Id</th><th>Created By</th><th>Status</th><th>Created On</th></tr>'
												+ '<tr><td colspan="5" align="center">No Submittals Found</td></tr>'
									}
									tableres += '</table>';
									$detail	.html(tableres);
								});
						response.error(function(data, status, headers, config){
		        			  if(status == constants.FORBIDDEN){
		        				location.href = 'login.html';
		        			  }else{  			  
		        				$state.transitionTo("ErrorPage",{statusvalue  : status});
		        			  }
		        		  });
					},

					/* Check box action */
					onCheck : function(row) {
						$("#mesgerror").hide();
						/*$scope.dataFromChecked.push(row.email+ ';');
						$scope.selectionCheck.push(row.id);
						$scope.smsCheck.push(row.phoneNumber);*/

						 var id= row.id;
                   	  	var obj = {"id" : id,
                   			  "email" : row.email+';'}
                   	  $scope.mapCheck.push(obj);
					},
					onUncheck : function(row) {
						
						$.each($scope.mapCheck, function (i, map) {
							if(map.id == row.id){
								var unCheckIndex = $scope.mapCheck.indexOf(map);
								$scope.mapCheck.splice(unCheckIndex, 1);
							}
				    		});
						
						/*var unCheckedEmail = row.email;
						var phone = row.phoneNumber;
						var unCheckedId = row.id;
						var unCheckIndex = $scope.dataFromChecked.indexOf(unCheckedEmail);
						$scope.dataFromChecked.splice(unCheckIndex,	1);
						var selUnCheckIndex = $scope.selectionCheck.indexOf(unCheckedId);
						$scope.selectionCheck.splice(selUnCheckIndex,	1);
						var smsUnCheckIndex = $scope.smsCheck.indexOf(phone);
						$scope.smsCheck.splice(smsUnCheckIndex,	1);*/
						
					},
					onCheckAll : function(rows) {
						$("#mesgerror").hide();
						/*
						for (var i = 0; i < rows.length; i++) {
							if($.inArray(rows[i].id, $scope.selectionCheck) === -1){
								$scope.dataFromChecked.push(rows[i].email+ ';');
								$scope.selectionCheck.push(rows[i].id);
								$scope.smsCheck.push(rows[i].phoneNumber);
							}
							
						}*/
						$.each(rows, function (i, row) {
//							alert(rows);
							if($scope.mapCheck.length>0){
								var flag=false;
								$.each($scope.mapCheck, function (i, map) {
//							alert(map+"  map");
							if(map.id == row.id ){
							flag=true;
							}
				    		})
				    		if(!flag){
				    			var obj = {"id" :  row.id,
	                        			  "email" : row.email+';',
	                        			  };
	                        	  $scope.mapCheck.push(obj);
				    		}
							}else{
								var obj = {"id" :  row.id,
	                        			  "email" : row.email+';',
	                        			   };
	                        	  $scope.mapCheck.push(obj);
							}
						});
					},
					onUncheckAll : function(rows) {
						/*for (var i = 0; i < rows.length; i++) {
							var unCheckedEmail = rows[i].email;
							var phone = rows[i].phoneNumber;
							var uncheckedId = rows[i].id;
							if($.inArray(rows[i].id, $scope.selectionCheck) !== -1){
								var unCheckIndex = $scope.dataFromChecked.indexOf(unCheckedEmail);
								$scope.dataFromChecked.splice(unCheckIndex,	1);
								var unSelCheckIndex = $scope.selectionCheck.indexOf(uncheckedId);
								$scope.selectionCheck.splice(unSelCheckIndex,	1);
								var smsUnCheckIndex = $scope.smsCheck.indexOf(phone);
								$scope.smsCheck.splice(smsUnCheckIndex,	1);
							}
						}*/
						for (var i = 0; i < rows.length; i++) {
							 
							for(var j=0;j < $scope.mapCheck.length;j++){
								if($scope.mapCheck[j].id == rows[i].id ){
									var unCheckIndex = $scope.mapCheck.indexOf($scope.mapCheck[j]);
									$scope.mapCheck.splice(unCheckIndex,	1);
								}
							}
						
						}
					},
					responseHandler : function(res) {
					   /* $.each(res, function (i, row) {
					        row.state = $.inArray(row.id, $scope.selectionCheck) !== -1;
					    });*/
						 $.each(res, function (i, row) {
							 
						    	$.each($scope.mapCheck, function (i, map) {
						    		if(map.id == row.id ){
						    			row.state = true;
						    		}
						    		
						    	})
						    });
					    return res;
					},
				}
			};

			// for resume Dialogue box
			function indiaResumeFormatter(value, row,
					index) {
				return [ '<a class="info actionIcons" title="View Resume"><i class="fa fa-file-word-o"></i></a>' ]
						.join('');
			}

			window.indiaResumeEvents = {
                    'click .info': function (e, value, row, index) { 
                  		var candidateId = row.id;
							var objresume = null;
							var response = $http.get('IndiaCandidates/resumeByCandidateId?candidateId='
									+ candidateId);
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
                     }
			};
			/* Table button action formatters */

			function indiaActionFormatter(value, row,
					index) {
				var src = row.hot ? "resources/img/star-red.png": "resources/img/star-gray.png";
				var blocksrc = row.block ? "resources/img/star-block.png": "resources/img/star-unblock.png";
				var hotTitle = row.hot ? "Remove from hotlist": "Add to hotlist";
				var blockTitle = row.block ? "Remove from blacklist": "Add to blacklist";
					 
				return [
						'<a  ><img class="indiahotlist actionIcons" id = "hotimg'+ row.id+ '" flex-gt-md="auto" title="'	+ hotTitle+ '" width="12" height="12" src='+ src + ' /></a>',
						'<a class="indiablacklist actionIcons" title="'+ blockTitle+'" flex-gt-md="auto"><img id = "blockimg'+ row.id+ '" width="12" height="12" src='+ blocksrc+ ' /></a>',
						'<a class="indiaview actionIcons" title="View details" flex-gt-md="auto"><i class="fa fa-search" style="font-size:12px;"></i></a>',
						'<a class="indiaemail actionIcons" title="'+ row.email+ '" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>',
						'<a class="indiadownload actionIcons" title="Download resume" ><i class="fa fa-download" style="font-size:12px;"></i></a> ',
						'<a class="indiaedit actionIcons" title="Edit candidate"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
						(($rootScope.rsLoginUser.userRole != constants.IN_Recruiter && $rootScope.rsLoginUser.userRole != constants.IN_TL) ?'<a class="indiaremove actionIcons displayInfo" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>':'') ]
						.join('');
			}
			window.indiaOperateEvents = {
					'click .indiablacklist' : function(e,value, row, index) {
						if(row.hot){
							$.growl.warning({title : "warning !",message : "Candidate #"+ row.id+" already in Hot list Make it UnHot for Block list"});
						}else{
						$scope.reason = "";
						$mdDialog.show({
											controller : DialogController,
											templateUrl : 'views/dialogbox/hotblock.html',
											parent : angular.element(document.body),
											locals : {rowData : row,},
											targetEvent : e,
											clickOutsideToClose : true,
										}).then(function(answer) {
											var blocklist = {
												"candidateId" : row.id,
												"reason" : answer}
											$(".underlay").show();
										var response = $http.post("IndiaCandidates/saveBlockList",blocklist);
											response.success(function(data,	status,	headers,config) {
												angular.element("#indiaCandidateControllerId").scope().onload();
												 $(".underlay").hide();
												 $.growl.success({title : "update !",message : data.statusMessage+ "#"+ row.id});
												 
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
					'click .indiahotlist' : function(e,value, row, index) {
						if(row.block){
							$.growl.warning({title : "warning !",message : "Candidate #"+ row.id+" already in Block list Make it UnBlock for Hot list"});
						}else{
						$mdDialog.show(	{
											controller : DialogController,
											templateUrl : 'views/dialogbox/hotblock.html',
											parent : angular.element(document.body),
											targetEvent : e,
											locals : {rowData : row,},
											clickOutsideToClose : true,
										}).then(function(answer) {
											var hotlist = {
												"candidateId" : row.id,
												"reason" : answer
											}
											$(".underlay").show();
								var response = $http.post("IndiaCandidates/saveHotComment",hotlist);
								response.success(function(data,status,headers,config) {
									
									angular.element("#indiaCandidateControllerId").scope().onload();
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
					'click .indiaview' : function(e,value, row, index) {
						var candidateId = row.id;
						var response = $http
								.post('IndiaCandidates/getSubmittals?candidateId='+ candidateId);
						response.success(function(data, status,headers, config) {
							$mdDialog.show(	{
												controller : DialogController,
												templateUrl : 'views/dialogbox/viewindiadetailsdialogbox.html',
												parent : angular.element(document.body),
												targetEvent : e,
												locals : {rowData : row,},
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
					'click .indiaemail' : function(e,value, row, index) {
						var email = row.email;
						mailService.sendMail(email, false);

					},
					'click .indiadownload' : function(e,value, row, index) {
						
						var candidateId = row.id;
						var candidateresumeData=[];
						var objresume = null;
						$window.location = 'IndiaCandidates/downloadResumeByCandidateId/'+candidateId+'/'+constants.ORGDOC;
						/*var response = $http.get('IndiaCandidates/downloadResumeByCandidateId?candidateId='+ candidateId);
						response.success(function(data, status,headers, config){
							var candresume = JSON.stringify(data);
							 objresume = JSON.parse(candresume);
							 
								var A = objresume.resumeContent;
								var a = document.createElement('a');
								a.href = 'data:attachment/octet-stream,'+ encodeURIComponent(A);
								a.target = '_blank';
								a.download = 'rsm' + row.id+ '-' + row.createdOn+ '-' + row.firstName+ '.doc';
								document.body.appendChild(a);
								a.click();
								});
						response.error(function(data, status, headers, config){
		        			  if(status == constants.FORBIDDEN){
		        				location.href = 'login.html';
		        			  }else{  			  
		        				$state.transitionTo("ErrorPage",{statusvalue  : status});
		        			  }
		        		  });*/
					},
					'click .indiaedit' : function(e,value, row, index) {
						
						$rootScope.candidateEditindId = row.id;												
						$state.transitionTo("EditIndiaCandidates",{candidateId : row.id});

					},
					'click .indiaremove' : function(e,value, row, index) {

						$mdDialog.show(	{
											controller : DialogController,
											templateUrl : 'views/dialogbox/removecandiatedialogbox.html',
											parent : angular.element(document.body),
											targetEvent : e,
											locals : {rowData : row,},
											clickOutsideToClose : true,
										})
								.then(function(answer) {
											var deleteReason = {
												"candidateId" : row.id,
												"reason" : answer
											}
											$(".underlay").show();
											var candidateId = row.id;
											var response = $http.post('IndiaCandidates/deleteIndiaCandidate',deleteReason);
											response.success(function(data, status,headers, config) 
													{
													
												angular.element("#indiaCandidateControllerId").scope().onload();
												 $(".underlay").hide();
												$.growl	.notice({title : "Delete !",message : data.statusMessage+ "#"+ row.id});
													
													});
											response.error(function(data, status, headers, config){
												$(".underlay").hide();
							        			  if(status == constants.FORBIDDEN){
							        				location.href = 'login.html';
							        			  }else{  			  
							        				  $state.transitionTo("ErrorPage",{statusvalue  : status});
														$.growl	.error({title : "Error !",message : data+ "#"+ row.id});
							        			  }
							        		  });
										}, function() {
										});
					}
			};
  }
    	/*function tableFunctionality(number, size){
    		 $(".underlay").css("display", "block");
    		 var response = $http.post("IndiaCandidates/getCandidates?pageNumber="+number+"&pageSize="+size);
				response.success(function(data, status,headers, config) {
					 
				 
					dispalyIndiaTable();
					$scope.candidate.bsTableControl.options.responseHandler(data); 
					$scope.candidate.bsTableControl.options.data =data; 
					$scope.candidate.bsTableControl.options.pageNumber =number;
					$scope.candidate.bsTableControl.options.pageSize =size;
					$scope.candidate.bsTableControl.options.totalRows =data[0].totalRecords;
					$(".underlay").css("display", "none");
			      });
				response.error(function(data, status, headers, config){
      			  if(status == constants.FORBIDDEN){
      				location.href = 'login.html';
      			  }else{  			  
      				$state.transitionTo("ErrorPage",{statusvalue  : status});
      			  }
      		  });
    	}*/
    	 function ResumeDialogController($scope,
					$mdDialog, CandidateText,
					CandidateKeywords) {
				$scope.CandidateText = CandidateText;
				$scope.CandidateKeywords = CandidateKeywords;

				$scope.cancel = function() {
					$mdDialog.cancel();
				};
			}
    	function DialogController($scope,
				$mdDialog, rowData) {
			$scope.row = rowData;
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
    	
    	
    	
    	 $scope.sendEmails = function() {
				if($scope.mapCheck.length>0){
					$("#mesgerror").hide();
					var checkedData = '';
    				$.each($scope.mapCheck, function (i, map) {
    					checkedData += map.email;
			    		})  
					mailService.sendMail(checkedData, true);
				}
				else{
					/*alert("Please select candidates to send Mail");*/
					$("#mesgerror").show();
				}
				
			}

    });
})(angular);