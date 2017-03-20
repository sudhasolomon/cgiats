;(function(angular){
	"use strict";	
	angular.module("editsubmitals",[])
	.controller("editsubmitalscontroller",function($scope,$location,$http, $state,$timeout,$rootScope,$mdDialog, $window){
		$scope.errMsg = null;
		$scope.submitaldatainfo = {};
		$scope.isEventCompleted = true;
		
		$scope.editHistory = function(historyEvent){
			$scope.editMode = true;
//			$scope.eventHistory = historyEvent;
			historyEvent.oldStrCreatedOn = historyEvent.strCreatedOn;
			historyEvent.jsEditMode = true;
		}
		
		$scope.cancelStatusDate = function(historyEvent){
			historyEvent.strCreatedOn = historyEvent.oldStrCreatedOn;
			$scope.editMode = false;
			historyEvent.jsEditMode = false;
		}
		
		$scope.updateStatusDate = function(historyEvent){
			$scope.editMode = false;
			historyEvent.isEditMode = true;
			historyEvent.jsEditMode = false;
			$scope.eventHistory = {};
		} 
		
		/*function getCurrentTime(){
			var d = new Date,
		    dformat = [ d.getFullYear(),(d.getMonth()+1)<10?(0+""+(d.getMonth()+1)):d.getMonth()+1,
		               d.getDate()<10?(0+""+ d.getDate()): d.getDate()
		              ].join('-')+' '+
		              [d.getHours()<10?(0+""+d.getHours()):d.getHours(),
		               d.getMinutes()<10?(0+""+d.getMinutes()):d.getMinutes()].join(':');
			return dformat;
		}*/
		
		
		function getCurrentTime(isFromEventHistory){
				var response = $http.get('commonController/getCurrentTime');
				response.success(function (data,status,headers,config){
					if(isFromEventHistory){
					$scope.eventHistory.strCreatedOn = data.currentTime;
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
			if($location.search().submittalId){
				$(".underlay").show();	
				var response = $http.get('jobOrder/getSubmittalById/'+$location.search().submittalId);
				response.success(function(data, status, headers,
						config) {
					$(".underlay").hide();
					$scope.submitaldatainfo = data;
					$scope.totoldata = JSON.stringify(data);
					$scope.joborderdatainfo = data.jobOrderDto;
					$scope.joborderdetails = data.jobOrderDto.jobOrderFieldList;
					$scope.candidateinfo = data.candidateDto;
					$scope.submittalEventHistoryDtoList = data.submittalEventHistoryDtoList;
					if($scope.submittalEventHistoryDtoList){
						$scope.submittalEventHistoryDtoList.reverse();
					}
					$scope.submittal = data;
					$scope.eventHistory = {};
					$scope.attachment = $scope.joborderdatainfo.attachmentFileName ?  $scope.joborderdatainfo.attachmentFileName:N/A ;
					$scope.strAttachment = $scope.joborderdatainfo.strAttachment;
					$scope.id = $scope.joborderdatainfo.id;
					$scope.customerHiddenValue = data.jobOrderDto.strCustomerHidden;
					
										
					var persalary = $scope.joborderdatainfo.salary;
					var perpermfee = $scope.joborderdatainfo.permFee;
					var contw2hrly = $scope.joborderdatainfo.hourlyRateW2;
					var contw2hrlymax = $scope.joborderdatainfo.hourlyRateW2max;
					var contw2annl = $scope.joborderdatainfo.annualRateW2;
					var cont1099 = $scope.joborderdatainfo.hourlyRate1099;
					var contc2c = $scope.joborderdatainfo.hourlyRateC2c;
					var contc2cmax = $scope.joborderdatainfo.hourlyRateC2cmax;
					
					
					
					
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
		
		
		
		
		$scope.saveOrUpdateSubmittal = function(){
			$(".underlay").show();			
			$scope.errMsg = null;
			 $scope.submittal.submittalEventHistoryDtoList = $scope.submittalEventHistoryDtoList;
			 $scope.submittal.jobOrderDto = {};
			 var res = $http.get('jobOrder/checkStartedStatusBy_CandidateId_SubmittalId?candidateId='+$scope.candidateinfo.id+'&submittalId='+$scope.submittal.submittalId);
				res.success(function(data){
					 if(data){
						 if(!data.message){
			 var response = $http.post('jobOrder/saveOrUpdateSubmittal', $scope.submittal);
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
			
	
		$scope.addEvent = function(){
			$scope.errMsg = null;
			if($scope.eventHistory.status && $scope.eventHistory.notes){
//			$scope.eventHistory.strCreatedOn = getCurrentTime();
				$scope.submittalEventHistoryDtoList.reverse();
				$scope.isEventCompleted = false;
				if(!$scope.eventHistory.strCreatedOn){
					$scope.eventHistory.strCreatedOn = getCurrentTime(true);
					
					 $timeout(function() {//wait for some time to redirect to another page
						 $scope.submittalEventHistoryDtoList.push($scope.eventHistory);
							$scope.submittalEventHistoryDtoList.reverse();
							$scope.submittal.status = $scope.eventHistory.status;
							$scope.submittal.createdOn = $scope.eventHistory.strCreatedOn;
							$scope.eventHistory = {};
							$scope.isEventCompleted = true;
					 }, 100);
				}else{
					$scope.submittalEventHistoryDtoList.push($scope.eventHistory);
					$scope.submittalEventHistoryDtoList.reverse();
					$scope.submittal.status = $scope.eventHistory.status;
					$scope.submittal.createdOn = $scope.eventHistory.strCreatedOn;
					$scope.eventHistory = {};
					$scope.isEventCompleted = true;
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
//		alert();
		$scope.candidateid= $('#candidateid').val();
//		alert($scope.candidateid);
		if($scope.candidateid != ''){
		$state.transitionTo("EditCandidate",{candidateId : $scope.candidateid, page:"search"});
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
		$window.location = 'jobOrder/downloadJobOrderAttachment/'+jobOrderId;
	}
	
	});
	
	
	
})(angular);