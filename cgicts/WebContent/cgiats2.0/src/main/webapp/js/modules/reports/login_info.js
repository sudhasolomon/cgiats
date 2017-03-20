(function(angular){
	
	"use strict";
	
			
				angular.module("loginInfoModule",['ngMaterial', 'ngMessages','ui.bootstrap','jcs-autoValidate', 'DatePicker'])
				.controller("loginInfoController", function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state,dateRangeService,$location){
					$scope.test = " login info test";
					
					$scope.statusButtons = false;
					$scope.loginAttemptsTable = false;
					$scope.loginAttempts = [];
					
					$scope.onload = function(){
						$scope.Created = { endDate: moment(), startDate:moment()};
			    		$scope.daterange ={
			        			today : [moment(),moment()],
			        			onemonth : [moment().subtract(1, 'month'), moment()],
			        			twomonths : [moment().subtract(2, 'month'), moment()],
			        			threemonths : [moment().subtract(3, 'month'), moment()],
			        			custom : 'custom'
			        	}
			        
			        	  $scope.ranges = {
			    				'Today': [moment(),moment()],
			        	        'Last 1 month': [moment().subtract(1, 'month'), moment()],
			        	        'Last 3 months': [moment().subtract(3, 'month'), moment()],
			        	        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
			        	        'Last 1 year': [moment().subtract(12, 'month'), moment()]
			        	        };
			    		$scope.loginInfo();
			    		
					}
					function getDDMMYY_Date(strValue){
						if(strValue){
						var strDateArray = strValue.split('-');
						return [strDateArray[2], strDateArray[1],strDateArray[0]].join('-');
						}
					}
					$scope.loginInfo = function(){
						var obj=null;
			    		 var startDate= null,endDate=null;
						if($scope.Created){
							if($('#loginInfoRange').val() && $('#loginInfoRange').val()!=''){
			     				 startDate = $('#loginInfoRange').val().split(' ')[0];
			     				 startDate = getDDMMYY_Date(startDate);
			     				 endDate = $('#loginInfoRange').val().split(' ')[2];
			     				 endDate = getDDMMYY_Date(endDate);
//			     				 alert(startDate +"   "+endDate);
			     				obj = {startDate:startDate, endDate:endDate};
			     			}else{
			     				obj = {startDate:$scope.Created.startDate, endDate:$scope.Created.endDate};
			     				 startDate = obj.startDate.toDate();
			     				 endDate = obj.endDate.toDate();
			     				startDate = (startDate.getDate()<10?"0"+startDate.getDate():startDate.getDate())+'-'+((startDate.getMonth()+1)<10?"0"+(startDate.getMonth()+1):(startDate.getMonth()+1))+'-'+(startDate.getFullYear());
			     				endDate = (endDate.getDate()<10?"0"+endDate.getDate():endDate.getDate())+'-'+((endDate.getMonth()+1)<10?"0"+(endDate.getMonth()+1):(endDate.getMonth()+1))+'-'+(endDate.getFullYear());
			     				obj = {startDate:startDate, endDate:endDate};
			     			}
						}
						loginInfoAttempt(obj);
					}
					
					function loginInfoAttempt(obj){
						$scope.loginAttemptsTable = true;
						$scope.statusButtons = true;
						var response = $http.post("loginInfoReport/getLoginInfo", obj);
						response.success(function(data,status,headers,config){
//							alert("Success "+JSON.stringify(data));
							if(data){
								loginAttemptsTableView();
								$scope.loginAttempts.loginTableControl.options.data = data;
							}
							
						});
						response.error(function(data,status,headers,config){
							 if(status == constants.FORBIDDEN){
			        				location.href = 'login.html';
			        			  }else{  			  
			        				$state.transitionTo("ErrorPage",{statusvalue  : status});
			        			  }
						})
						
					}
					
					
					function loginAttemptsTableView(){
					    $scope.loginAttempts.loginTableControl = {
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
			                        showFooter : false,
			                        columns: [
			                        {
			                            //field: 'status',
			                            title: 'Status',
			                            formatter : priorityWithImage,
			                            align: 'center',
			                            
			                        },
			                        {
			                            field: 'createdBy',
			                            title: 'Login user',
			                            align: 'left',
			                           
			                        },{
			                            field: 'loginDate',
			                            title: 'First Login Date & Time',
			                            align: 'left',
			                           
			                        },{
			                            field: 'logoutDate',
			                            title: 'Last Logout Date & Time',
			                            align: 'left',
			                           
			                        },{
			                            field: 'duration',
			                            title: 'Duration',
			                            align: 'left',
			                           
			                        },{
			                            field: 'total',
			                            title: '# of Login Attempts',
			                            align: 'left',
			                           
			                        },]
			                    }
			            };
					}
					
					function priorityWithImage(value, row,
							index) {
						if(row.status == constants.LOGIN){
							var src = "resources/img/login.png";
						}
						if(row.status == constants.LOGOUT){
							var src = "resources/img/logout.png";
						}
						return [
						        
								'<img   flex-gt-md="auto" title="'	+row.status+ '" width="20" height="20" src='+ src + ' />'
								 ]
								.join('');
						/*
						if(row.status == constants.LOGOUT){
							return [
									
									'<img   flex-gt-md="auto" title="'	+row.status+ '" width="12" height="12" src='+ src + ' />',
									'&nbsp;<label>'+row.status+'</label>'
									 ]
									.join('');
							}*/
					}
				});
	
	
})(angular);