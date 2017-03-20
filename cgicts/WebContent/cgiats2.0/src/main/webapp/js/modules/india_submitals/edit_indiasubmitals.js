;(function(angular){
	"use strict";	
	angular.module("editindiasubmitals",[])
	.controller("editindiasubmitalscontroller",function($scope,$location,$http, $state,$timeout,$rootScope,$mdDialog, $window){
		$scope.errMsg = null;
		$scope.submitaldatainfo = {};
		function getCurrentTime(){
			var d = new Date,
		    dformat = [(d.getDate()<10?'0'+d.getDate():d.getDate()),((d.getMonth()+1)<10?'0'+(d.getMonth()+1):d.getMonth()+1),
		               d.getFullYear()
		              ].join('-')+' '+
		              [d.getHours()<10?(0+""+d.getHours()):d.getHours(),
				               d.getMinutes()<10?(0+""+d.getMinutes()):d.getMinutes()].join(':');
			return dformat;
		}
		
		$scope.onload = function()
		{
			
			$scope.tabs = [ {
				type : 'tab1'
			}, {
				type : 'tab2'
			}, {
				type : 'tab3',
				active : true
			} ];
			
			$scope.mypagename = "Edit Submital Page";
			
			if($location.search().currentLoginUserId){
				 $timeout(function() {//wait for some time to redirect to another page
					 $scope.onloadFun();
				 }, 400);
				}else{
					$scope.onloadFun();
				}
			
			
		
		}
		
		$scope.onloadFun = function(){
			if($location.search().submittalId != undefined){
				var response = $http.get('India_JobOrder/getSubmittalById/'+$location.search().submittalId);
				response.success(function(data, status, headers,
						config) {
					//alert("data::"+JSON.stringify(data));
					$scope.submitaldatainfo = data;
					$scope.totoldata = JSON.stringify(data);
					$scope.joborderdatainfo = data.jobOrderDto;
					$scope.joborderdetails = data.jobOrderDto.jobOrderFieldList;
					$scope.candidateinfo = data.indiacandidateDto;
					$scope.submittalEventHistoryDtoList = data.submittalEventHistoryDtoList;
					$scope.submittal = data;
					$scope.eventHistory = {};
					$scope.attachment = $scope.joborderdatainfo.attachmentFileName ?  $scope.joborderdatainfo.attachmentFileName:N/A ;
					$scope.strAttachment = $scope.joborderdatainfo.strAttachment;
					$scope.id = $scope.joborderdatainfo.id;
					$scope.customerHiddenValue = data.jobOrderDto.strCustomerHidden;
					
					
					var minsal = data.jobOrderDto.salary;
					var maxsal = data.jobOrderDto.maxSal;
					var expminsal = data.jobOrderDto.minExp;
					var expmaxsal = data.jobOrderDto.maxExp;
					
					if(minsal == "" || minsal == undefined || minsal == null)
					{
						$scope.joborderdatainfo.salary = data.jobOrderDto.salary;
					}
					else
					{
						minsal = minsal.toString().replace(/\D/g,'');
						if(minsal.length <= 3)
							{
							$scope.joborderdatainfo.salary = data.jobOrderDto.salary;
							}
						else
							{
							var minsallastthree = minsal.substring(minsal.length-3);
							minsal = minsal.substring(0,minsal.length-3);
							minsal = minsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + minsallastthree;
							minsal = minsal;
							$scope.joborderdatainfo.salary = minsal;
							
							}
						
					}
					
					
					
					
					if(maxsal == "" || maxsal == undefined || maxsal == null)
					{
						$scope.joborderdatainfo.maxSal = data.jobOrderDto.maxSal;
					}
					else
					{
						maxsal = maxsal.toString().replace(/\D/g,'');
						if(maxsal.length <= 3)
							{
							$scope.joborderdatainfo.maxSal = data.jobOrderDto.maxSal;
							}
						else
							{
							var maxsallastthree = maxsal.substring(maxsal.length-3);
							maxsal = maxsal.substring(0,maxsal.length-3);
							maxsal = maxsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + maxsallastthree;
							maxsal = maxsal;
							$scope.joborderdatainfo.maxSal = maxsal;
							}
						
					}
					
					
					
					
					
					if(expminsal == "" || expminsal == undefined || expminsal == null)
					{
						$scope.joborderdatainfo.minExp = data.jobOrderDto.minExp;
					}
					else
					{
						expminsal = expminsal.toString().replace(/\D/g,'');
						if(expminsal.length <= 3)
							{
							$scope.joborderdatainfo.minExp = data.jobOrderDto.minExp;
							}
						else
							{
							var expminsallastthree = expminsal.substring(expminsal.length-3);
							expminsal = expminsal.substring(0,expminsal.length-3);
							expminsal = expminsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + expminsallastthree;
							expminsal = expminsal;
							$scope.joborderdatainfo.minExp = expminsal;
							}
						
					}
					
					
					
					
					if(expmaxsal == "" || expmaxsal == undefined || expmaxsal == null)
					{
						$scope.joborderdatainfo.maxExp = data.jobOrderDto.maxExp;
					}
					else
					{
						expmaxsal = expmaxsal.toString().replace(/\D/g,'');
						if(expmaxsal.length <= 3)
							{
							$scope.joborderdatainfo.maxExp = data.jobOrderDto.maxExp;
							}
						else
							{
							var expmaxsallastthree = expmaxsal.substring(expmaxsal.length-3);
							expmaxsal = expmaxsal.substring(0,expmaxsal.length-3);
							expmaxsal = expmaxsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + expmaxsallastthree;
							expmaxsal = expmaxsal;
							$scope.joborderdatainfo.maxExp = expmaxsal;
							}
						
					}
					
										
	/*				var persalary = $scope.joborderdatainfo.salary;
					var perpermfee = $scope.joborderdatainfo.permFee;
					var contw2hrly = $scope.joborderdatainfo.hourlyRateW2;
					var contw2annl = $scope.joborderdatainfo.annualRateW2;
					var cont1099 = $scope.joborderdatainfo.hourlyRate1099;
					var contc2c = $scope.joborderdatainfo.hourlyRateC2c;
					
					
					
					
					if(persalary)
					{
					$scope.joborderdatainfo.salary = '$ ' +  String(persalary).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					
					if(perpermfee)
					{
					$scope.joborderdatainfo.permFee = String(perpermfee).replace(/\B(?=(\d{3})+(?!\d))/g, ",") +' %' ;
					}
					
					
					
					
					if(contw2hrly)
					{
					$scope.joborderdatainfo.hourlyRateW2 = '$ ' + String(contw2hrly).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					if(contw2hrlymax)
					{
					$scope.joborderdatainfo.hourlyRateW2max = '$ ' + String(contw2hrlymax).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					if(contw2annl)
					{
					$scope.joborderdatainfo.annualRateW2 = '$ ' + String(contw2annl).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					if(cont1099)
					{
					$scope.joborderdatainfo.hourlyRate1099 = '$ ' + String(cont1099).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					if(contc2c)
					{
					$scope.joborderdatainfo.hourlyRateC2c = '$ ' + String(contc2c).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					if(contc2cmax)
					{
					$scope.joborderdatainfo.hourlyRateC2cmax = '$ ' + String(contc2cmax).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}*/
					
					
					
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
		
		
		
		
	/*	$scope.saveOrUpdateSubmittal = function(){
			$(".underlay").show();			
			$scope.errMsg = null;
			 $scope.submittal.submittalEventHistoryDtoList = $scope.submittalEventHistoryDtoList;
			 $scope.submittal.jobOrderDto = {};
			 var response = $http.post('India_JobOrder/saveOrUpdateSubmittal', $scope.submittal);
				response.success(function (data,status,headers,config){
						$.growl.success({title : "Info !", message : "Submittal updated Successfully"});
						$(".underlay").hide();
					 $timeout(function() {//wait for some time to redirect to another page
							 $state.go($location.search().pageName);
		        		}, 200);
					
				});
				response.error(function(data, status, headers, config){
		  			  if(status == constants.FORBIDDEN){
		  				location.href = 'login.html';
		  			  }else{  			  
		  				$state.transitionTo("ErrorPage",{statusvalue  : status});
		  			  }
		  		  });
			
			};
			*/
			$scope.saveOrUpdateSubmittal = function(){
				$(".underlay").show();			
				$scope.errMsg = null;
				 $scope.submittal.submittalEventHistoryDtoList = $scope.submittalEventHistoryDtoList;
				 $scope.submittal.jobOrderDto = {};
				 var res = $http.get('India_JobOrder/checkStartedStatusBy_CandidateId_SubmittalId?candidateId='+$scope.candidateinfo.id+'&submittalId='+$scope.submittal.submittalId);
					res.success(function(data){
						 if(data){
							 if(!data.message){
				 var response = $http.post('India_JobOrder/saveOrUpdateSubmittal', $scope.submittal);
					response.success(function (data,status,headers,config){
							$.growl.success({title : "Info !", message : "Submittal updated Successfully"});
							$(".underlay").hide();
						 $timeout(function() {//wait for some time to redirect to another page
								 $state.go($location.search().pageName);
			        		}, 200);
						
					});
					response.error(function(data, status, headers, config){
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
							 }else{
								 $scope.errMsg = data.message;
								 $(".underlay").hide();
							 }
						 }
					 });
					res.error(function(data, status, headers, config){
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
				
				};
	
		/*$scope.addEvent = function(){
			$scope.errMsg = null;
			if($scope.eventHistory.status){
//			$scope.eventHistory.strCreatedOn = getCurrentTime();
			$scope.submittalEventHistoryDtoList.push($scope.eventHistory);
			$scope.submittal.status = $scope.eventHistory.status;
			$scope.eventHistory = {};
			}else{
				$scope.errMsg = 'Please Select Status';
			}
		};*/
			
			
			$scope.addEvent = function(){
				$scope.errMsg = null;
				if($scope.eventHistory.status && $scope.eventHistory.notes){
//				$scope.eventHistory.strCreatedOn = getCurrentTime();
					$scope.submittalEventHistoryDtoList.reverse();
					if(!$scope.eventHistory.strCreatedOn){
						$scope.eventHistory.strCreatedOn = getCurrentTime(true);
						
						 $timeout(function() {//wait for some time to redirect to another page
							 $scope.submittalEventHistoryDtoList.push($scope.eventHistory);
								$scope.submittalEventHistoryDtoList.reverse();
								$scope.submittal.status = $scope.eventHistory.status;
								$scope.submittal.createdOn = $scope.eventHistory.strCreatedOn;
								$scope.eventHistory = {};
						 }, 50);
					}else{
						$scope.submittalEventHistoryDtoList.push($scope.eventHistory);
						$scope.submittalEventHistoryDtoList.reverse();
						$scope.submittal.status = $scope.eventHistory.status;
						$scope.submittal.createdOn = $scope.eventHistory.strCreatedOn;
						$scope.eventHistory = {};
					}
				}else{
					if(!$scope.eventHistory.status){
						$scope.errMsg = 'Please Select Status';
						}if(!$scope.eventHistory.notes){
							$scope.errMsg = 'Please Enter Status Notes';
						}
				}
			};
		
	$scope.jumpToProfile = function() {
		$scope.candidateid= $('#candidateid').val();
		if($scope.candidateid != ''){
		$state.transitionTo("EditIndiaCandidates",{candidateId : $scope.candidateid, page:"search"});
	}
	}
	
	$scope.cancelsubmittal = function(){
		$state.go($location.search().pageName);
	}
	
	$scope.viewAttachment = function(element){
		
		$mdDialog
	 	.show({
	 		controller : ResumeViewDialogController,
	 		templateUrl : 'views/dialogbox/joborderdialogbox.html',
	 		parent : angular
				.element(document.body),
				targetEvent : element,
				locals : {
					CandidateText : $scope.strAttachment,
					CandidateKeywords : ""
				},
				clickOutsideToClose : true,
	 		});
		
		function ResumeViewDialogController($scope,
				$mdDialog, CandidateText, CandidateKeywords ) {
				$scope.CandidateText = CandidateText;
				$scope.CandidateKeywords = CandidateKeywords;

				$scope.cancel = function() {
				$mdDialog.cancel();
				};
			}
		
	}
	
	$scope.downloadResume = function(){
		var jobOrderId = $scope.id;
		if($scope.strAttachment!=null)
		$window.location = 'India_JobOrder/downloadJobOrderAttachment/'+jobOrderId;
	}
	
	});
	
	
	
})(angular);