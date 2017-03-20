;(function(angular){

	"use strict"
	
			angular.module('usersModule',[])
			.controller("userController", function($scope, $http, $state, $rootScope, $mdDialog, mailService, $stateParams, dateRangeService){
				$scope.errMsg = false; 
				$scope.usersTable = true;
				$scope.refreshData = false;
				$scope.addUserButton = false;
				$scope.month = null;
				$scope.year = null;
				//var settings = [];
				$scope.settings = [];
				$scope.onload = function(){
					$scope.getUsers(true);
					if($stateParams.create){
						$.growl.success({title : "success !",message : "User has been created Successfully"});
					}
					if($stateParams.edit){
						$.growl.success({title : "success !",message : "User has been updated Successfully"});
					}
					
					$scope.years = [];
					for(var i=2010;i<=(new Date().getFullYear());i++){
						$scope.years.push(i+'');
					}
				}
				
				$scope.getUsers = function(isFromOnload){
					$(".underlay").show();
//					var month = ($scope.month==undefined)? '' : $scope.month;
//					var year = ($scope.year==undefined)? '' : $scope.year;
					if(!isFromOnload && (!$scope.year || !$scope.month)){
						$(".underlay").hide();
						$scope.errMsg = true; 
					}
					else{
					$scope.errMsg = false; 
					displayTable();
					$scope.obj = {
							"month" : $scope.month,
							"year"  :$scope.year
					}
					var response = $http.post('userController/createdUserByDate',$scope.obj);
					response.success(function (data,status,headers,config){
						$(".underlay").hide();
						if(data){
							data.sort(function(a, b){
								 var nameA=a.userId.toLowerCase(), nameB=b.userId.toLowerCase();
								if (nameA < nameB) //sort string ascending
							        return -1 
							    if (nameA > nameB)
							        return 1
							    return 0 
							})
							$scope.settings.userDetails.options.data = data;
						}
						
						
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
				
//Add User Button 				
			if(($rootScope.rsLoginUser.userId === constants.HARI_USER_ID) ||
					($rootScope.rsLoginUser.userId === constants.RVEMULA_USER_ID)){
				$scope.addUserButton = true;
			}
			
//Refresh Button
			
			$scope.refresh= function(){
				$scope.month = null;
				$scope.year = null;
				//$scope.onload();
				$scope.getUsers(true);
			}
			
			
				function displayTable(){
					$scope.settings.userDetails = {
							options : {
								//data :  {},
								striped : true,
								pagination : true,
								paginationVAlign : "both",
								sidePagination : 'client',
								silentSort: false,
								pageList : [ 10, 20, 50 ],
								search : false,
								showColumns : false,
								showRefresh : false,
								showExport:false,
								exportTypes : ['excel', 'pdf'],
								showFooter : false,
								clickToSelect : false,
								showToggle : false,
								/*detailView : true,*/
								/*exportOptions :{
							         fileName: 'testo', 
							         worksheetName: 'test1',         
							       },*/
								maintainSelected : true,
								columns : [
										{
											field : 'userId',
											title : 'Id',
											events: window.editUserEvent,
				                            formatter : editUser,
											align : 'left',
											sortable : true
										},{
											field : 'userRole',
											title : 'Role',
											align : 'left',
											sortable : true
										},
										{
											field : 'fullName',
											title : 'Name',
											align : 'left',
											sortable : true
										},
										{
											field : 'assignedBdm',
											title : 'Assigned To',
											align : 'left',
											sortable : true
										},
										{
											field : 'officeLocation',
											title : 'Office Loc',
											align : 'left',
											sortable : true
										},
										{
											field : 'createdon',
											title : 'Created',
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
											title : 'Actions',
											align : 'left',
											events : window.userEvents,
											formatter : userFormatter,
										}],

								},
						
							
							};
				      
		            function editUser(value, row,
							index){
		            	if($rootScope.rsLoginUser.userId === constants.HARI_USER_ID || $rootScope.rsLoginUser.userId === constants.RVEMULA_USER_ID){
		            	return [
								
						        '<a class="userName actionIcons" title="Edit">'+row.userId+'</a> '
								 ]
								.join('');
		            	}else{
		            		return row.userId;
		            	}
		            	}
		            
		            window.editUserEvent = {
		            		
		    				'click .userName' : function(e,value, row, index) {
		    					$state.transitionTo("editUserModule",{userId : row.userId,userRole:row.userRole});
		    				},
		    			}
					
					function userFormatter(value, row, index) {
		            	if($rootScope.rsLoginUser.userId === constants.HARI_USER_ID || $rootScope.rsLoginUser.userId === constants.RVEMULA_USER_ID){
					
						if(row.status=='INACTIVE'){
							return [
							        '<a class="email actionIcons" title="'+ row.email+ '" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>',
							        '<a class="edit actionIcons" title="Edit"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
							        //'<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>',
							        '<a class="check actionIcons" title="Activate"><i class="fa fa-check" style="font-size:12px;"></i></a>',]
						.join('');
							
						}
						return [
						        '<a class="email actionIcons" title="'+ row.email+ '" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>',
						        '<a class="edit actionIcons" title="Edit"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
						        '<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>',
]
						.join('');
				}else if($rootScope.rsLoginUser.userId === constants.HARI_USER_ID || $rootScope.rsLoginUser.userId === constants.RVEMULA_USER_ID){
					return [
					        '<a class="email actionIcons" title="'+ row.email+ '" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>',
					        '<a class="edit actionIcons" title="Edit"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
					        '<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>',
]
					.join('');
				}
					
					else{
					return [
					        '<a class="email actionIcons" title="'+ row.email+ '" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>',]
					        
				.join('');
				}
					}
					
				window.userEvents = {
					'click .check' : function(e,value, row, index) {
							$mdDialog.show(	{
												controller : DialogController,
												templateUrl : 'views/dialogbox/activateuserdialogbox.html',
												parent : angular.element(document.body),
												targetEvent : e,
												locals : {rowData : row,},
												clickOutsideToClose : true,
											})
									.then(function(userDetails) {
												var userId = row.userId;
												$scope.joiningDate = dateRangeService.formatDate(userDetails.joiningDate);
												var response = $http.get('userController/activateUser?userId='+ userId + '&joiningDate='+$scope.joiningDate+
														'&userRole='+row.userRole);
												//var response = $http.post('IndiaCandidates/deleteIndiaCandidate',deleteReason);
												response.success(function(data, status,headers, config) 
														{
													angular.element("#userDetails").scope().getUsers(true);
														//$scope.onload();
														$.growl.success({title : "success !",message :" User "+row.userId+" Activated Successfully "});
														
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
					
					'click .remove' : function(e,value, row, index) {
						$mdDialog.show(	{
											controller : DialogController,
											templateUrl : 'views/dialogbox/deleteuserdialogbox.html',
											parent : angular.element(document.body),
											targetEvent : e,
											locals : {rowData : row,},
											clickOutsideToClose : true,
										})
								.then(function(userData) {
											var userId = row.userId;
											$scope.relievDate = dateRangeService.formatDate(userData.relievingDate);
											var response = $http.get('userController/deleteUser?userId='+ userId + '&relievDate='+$scope.relievDate+'&userRole='+row.userRole);
											//var response = $http.post('IndiaCandidates/deleteIndiaCandidate',deleteReason);
											response.success(function(data, status,headers, config) 
													{
												angular.element("#userDetails").scope().getUsers(true);
													//$scope.onload();
													$.growl.success({title : "success !",message :" User "+row.userId+" Successfully Deleted"});
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
				
				'click .email' : function(e,value, row, index) {
					var email = row.email;
					mailService.sendMail(email, false);
				
				},
				
				'click .edit' : function(e,value, row, index) {
					$state.transitionTo("editUserModule",{userId : row.userId,userRole:row.userRole});
				
				},
					
					
				};
				}
				
				function DialogController($scope,
						$mdDialog, rowData) {
					$scope.row = rowData;
					$scope.userDetails={};
					$scope.userData={};
					$scope.hide = function() {
						$mdDialog.hide();
					};

					$scope.cancel = function() {
						$mdDialog.cancel();
					};

					$scope.answer = function(userDetails) {
						if(userDetails.joiningDate==undefined || userDetails.joiningDate == ''){
							$scope.joiningDateErrMsg = "Please Provide Joining Date";
						}else{
							$scope.joiningDateErrMsg = "";
							$mdDialog.hide(userDetails);
						}
						
					};
					
					$scope.answer1 = function(userData) {
						if(userData.relievingDate==undefined || userData.relievingDate == ''){
							$scope.relievingDateErrMsg = "Please Provide Leaving Date";
						}else{
							$scope.relievingDateErrMsg = "";
							$mdDialog.hide(userData);
						}
					};
					
					
					
					
					// Datepicker Start
					$scope.dateOfJoining = {
						date : new Date(),
						showWeeks : false
					};
					$scope.joiningDateFormat = 'MM-dd-yyyy';
					$scope.joiningDateopen = function() {
						$scope.joiningDatePopup.opened = true;
					};
					$scope.joiningDatePopup = {
						opened : false
					};
					
					// Datepicker Start
					$scope.dateOfReliving = {
						date : new Date(),
						showWeeks : false
					};
					$scope.relivingDateFormat = 'MM-dd-yyyy';
					$scope.relivingDateopen = function() {
						$scope.relivingDatePopup.opened = true;
					};
					$scope.relivingDatePopup = {
						opened : false
					};

				}
				
				
				$scope.addUser = function(){
					$state.transitionTo("addUserModule");
				}
			});
})(angular);
