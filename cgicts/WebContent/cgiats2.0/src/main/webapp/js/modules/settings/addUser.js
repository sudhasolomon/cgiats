;(function(angular){

	"use strict"
	
			angular.module('addUserModule',[])
			.controller("addUserController", function($scope, $http, $state, $stateParams,dateRangeService){
				$scope.user = {};
				$scope.errMsg=false;
				$scope.Fromedit = false;
				$scope.onload = function(){
					
					if($stateParams.userId && $stateParams.userRole){
						$scope.Fromedit = true;
						var response = $http.get('userController/getUserDetais?userId='
								+ $stateParams.userId+'&userRole='+$stateParams.userRole);
						response.success(function(data, status,headers, config) 
								{
							if(data){
								for(var i=0;i<data.length;i++){
									if(data[i].userId == $stateParams.userId){
								$scope.user.userId = data[i].userId;
								$scope.user.userRole = data[i].userRole;
								$scope.user.firstName = data[i].firstName;
								$scope.user.lastName = data[i].lastName;
								$scope.user.status = data[i].status;
								$scope.user.phone = data[i].phone;
								$scope.user.city = data[i].city;
								$scope.user.email = data[i].email;
								$scope.user.officeLocation = data[i].officeLocation;
								$scope.user.assignedBdm = data[i].assignedBdm;
								$scope.user.employeeId = data[i].employeeId;
								$scope.user.level = data[i].level;
								$scope.user.joiningDate = data[i].joiningDate;
								$scope.user.password = null;
								$scope.user.newPassword = null;
								}}}
								});
						response.error(function(data, status, headers, config){
		        			  if(status == constants.FORBIDDEN){
		        				location.href = 'login.html';
		        			  }else{  			  
		        				  $state.transitionTo("ErrorPage",{statusvalue  : status});
		        			  }
		        		  });
						
						
						
					}else {
						//$scope.Fromedit = false;
						
					}
					$scope.getAllUsers();
				}
				
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
				
				$scope.getAllUsers = function(){
					$scope.getAllUSUsers();
					$scope.getAllIndiaUsers();
				};
				
				
				$scope.getAllUSUsers = function(){
					var response = $http.get('commonController/getAllUsers');
					response.success(function (data,status,headers,config){
						if($scope.assignedUserIdlist){
							$scope.assignedUserIdlist.push.apply($scope.assignedUserIdlist, data);
							$scope.assignedUserIdlist.sort(function(a, b){
								 var nameA=a.fullName.toLowerCase(), nameB=b.fullName.toLowerCase();
								if (nameA < nameB) //sort string ascending
							        return -1 
							    if (nameA > nameB)
							        return 1
							    return 0 
							})
						}else{
						$scope.assignedUserIdlist = data;
						}
					});
					response.error(function(data, status, headers, config){
		  			  if(status == constants.FORBIDDEN){
		  				location.href = 'login.html';
		  			  }else{  			  
		  				$state.transitionTo("ErrorPage",{statusvalue  : status});
		  			  }
		  		  });
				};
				
				$scope.getAllIndiaUsers = function(){
					var response = $http.get('IndiaCommonController/getAllUsers');
					response.success(function (data,status,headers,config){
						if($scope.assignedUserIdlist){
							$scope.assignedUserIdlist.push.apply($scope.assignedUserIdlist, data);
							$scope.assignedUserIdlist.sort(function(a, b){
								    var nameA=a.fullName.toLowerCase(), nameB=b.fullName.toLowerCase();
								if (nameA < nameB) //sort string ascending
							        return -1 
							    if (nameA > nameB)
							        return 1
							    return 0 
							})
						}else{
							$scope.assignedUserIdlist = data;
						}
					});
					response.error(function(data, status, headers, config){
		  			  if(status == constants.FORBIDDEN){
		  				location.href = 'login.html';
		  			  }else{  			  
		  				$state.transitionTo("ErrorPage",{statusvalue  : status});
		  			  }
		  		  });
				};
				
				
				
				$scope.saveOrUpdateUser = function(user){
//					alert("user type::"+$scope.user.userType);
//					alert("bdm::"+$scope.user.assignedBdm);
					if($scope.user.joiningDate){
						$scope.user.joiningDate = dateRangeService.formatDate($scope.user.joiningDate);
						//$scope.user.relievingDate = dateRangeService.formatDate($scope.user.relievingDate);
					}
					if($scope.Fromedit ==false){
							var response = $http.get('commonController/checkTheUserExistence?userId='
									+ $scope.user.userId+'&userRole='+$scope.user.userRole);
							response.success(function (data,status,headers,config){
								if(data.statusMessage=='User Already Exits'){
									$.growl	.error({message :" "+data.statusMessage+" "});
								}else{
									saveOrUpdate(user);
								}
							});
							response.error(function(data, status, headers, config){
				  			  if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
				  		  });
							
					}else{
						saveOrUpdate(user);
					}
					
				}
				
				
				function saveOrUpdate(user){
					
					if(($scope.user.userRole=='Recruiter' || $scope.user.userRole=='ADM' || $scope.user.userRole=='IN_Recruiter') && ($scope.user.assignedBdm==undefined || $scope.user.assignedBdm == null)){
						$.growl	.error({message :" For "+$scope.user.userRole+" role Assigned-To field is mandatory"});
					}
					else{
					if(user.password == $scope.user.newPassword){
						
					user.isFromProfile = false;
					//alert(JSON.stringify(user));
					var response = $http.post('userController/saveOrUpdateUser',user);
					response.success(function (data,status,headers,config){
						 if(data.statusCode == 200){

							 $state.transitionTo("usersModule", {create : true});

						 }
						 if(data.statusCode == 200 && $stateParams.userId){
							 $state.transitionTo("usersModule", {edit : true});
						 }
						 
					});
					response.error(function(data, status, headers, config){
		  			  if(status == constants.FORBIDDEN){
		  				location.href = 'login.html';
		  			  }else{  			  
		  				$state.transitionTo("ErrorPage",{statusvalue  : status});
		  			  }
		  		  });
				}else{
					$scope.errMsg=true;
				}
					}
				
				}
				
				$scope.cancelClick = function(){
					$state.transitionTo("usersModule");
				}
			});
})(angular);