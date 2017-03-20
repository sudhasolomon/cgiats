;(function(angular){
	"use strict";	
	angular.module("createindiasubmitals",[])
	.controller("createindiasubmitalscontroller",function( $rootScope, $scope, blockUI, $http, $timeout, $location,
			$filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage,dateRangeService,mailService){
		$scope.msgg = "In Submittal page";
		
		$scope.errMsg = null;
		$scope.errMsg1 = null;
		$scope.usstates = usStates;
		$scope.$storage = $sessionStorage;
		$scope.candidate = [];
		var candidateData = [];
		$scope.searchTable = false;
		
		/*----------Pagination Details---------*/
		$scope.pageNumber = 1;
		$scope.pageSize = 10;
		
		$scope.searchFields = {};

		$scope.submittal = {};
		$scope.submittalEventHistoryDtoList = [{'strCreatedOn':getCurrentTime(),'status':'SUBMITTED','notes':'New Submittal created.'}];
		$scope.submittal.status = constants.SUBMITTED;
		$scope.eventHistory = {};
		
		
		function getCurrentTime(){
			var d = new Date,
		    dformat = [(d.getDate()<10?'0'+d.getDate():d.getDate()),((d.getMonth()+1)<10?'0'+(d.getMonth()+1):d.getMonth()+1),
		               d.getFullYear()
		              ].join('-')+' '+
		              [d.getHours()<10?(0+""+d.getHours()):d.getHours(),
				               d.getMinutes()<10?(0+""+d.getMinutes()):d.getMinutes()].join(':');
			return dformat;
		}
		
		$scope.sendResumeToClient = function(){
			mailService.sendMail("test@charterglobal.com", false);
		};
		
		$scope.onload = function()
		{
			
			
			if($location.search().jobOrder != undefined){
				$(".underlay").show();
				var response = $http.get('India_JobOrder/getReadableJobOrderById/'+$location.search().jobOrder);
				response.success(function(data, status, headers,
						config) {
					$(".underlay").hide();
					//alert("data"+JSON.stringify(data));
					$scope.id = data.id
					$scope.priority = data.priority;
					$scope.client = data.customer;
					$scope.keyskills = data.keySkills;
					$scope.title = data.title;
					$scope.city = data.city;
					$scope.state = data.state;
					$scope.description = data.description;
					$scope.jobtype = data.jobType;
					$scope.attachment = data.attachmentFileName==null ? N/A : data.attachmentFileName;
					$scope.strAttachment = data.strAttachment;
					$scope.salary = data.salary;
					$scope.maxSal = data.maxSal;
					$scope.minExp = data.minExp;
					$scope.maxExp = data.maxExp;
					$scope.permfee = data.permFee;
					$scope.startdate = data.startDate;
					$scope.acceptW2 = data.acceptW2;
					$scope.hourlyRateW2 = data.hourlyRateW2;
					$scope.annualRateW2 = data.annualRateW2;
					$scope.accept1099 = data.accept1099;
					$scope.hourlyRate1099 = data.hourlyRate1099;
					$scope.acceptC2c = data.acceptC2c;
					$scope.hourlyRateC2c = data.hourlyRateC2c;
					$scope.startDate = data.startDate;
					$scope.endDate = data.endDate;
					$scope.joborderdetails = data.jobOrderFieldList;
					$scope.customerHiddenValue = data.strCustomerHidden;
					$scope.noOfResumesRequired = data.noOfResumesRequired;
					$scope.noOfPositions = data.numOfPos;
					
					
					var minsal = data.salary;
					var maxsal = data.maxSal;
					var expminsal = data.minExp;
					var expmaxsal = data.maxExp;
					
					if(minsal == "" || minsal == undefined || minsal == null)
					{
						$scope.salary = data.salary;
					}
					else
					{
						minsal = minsal.toString().replace(/\D/g,'');
						if(minsal.length <= 3)
							{
							$scope.salary = data.salary;
							}
						else
							{
							var minsallastthree = minsal.substring(minsal.length-3);
							minsal = minsal.substring(0,minsal.length-3);
							minsal = minsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + minsallastthree;
							minsal = minsal;
							$scope.salary = minsal;
							
							}
						
					}
					
					
					
					
					if(maxsal == "" || maxsal == undefined || maxsal == null)
					{
						$scope.maxSal = data.maxSal;
					}
					else
					{
						maxsal = maxsal.toString().replace(/\D/g,'');
						if(maxsal.length <= 3)
							{
							$scope.maxSal = data.maxSal;
							}
						else
							{
							var maxsallastthree = maxsal.substring(maxsal.length-3);
							maxsal = maxsal.substring(0,maxsal.length-3);
							maxsal = maxsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + maxsallastthree;
							maxsal = maxsal;
							$scope.maxSal = maxsal;
							}
						
					}
					
					
					
					
					
					if(expminsal == "" || expminsal == undefined || expminsal == null)
					{
						$scope.minExp = data.minExp;
					}
					else
					{
						expminsal = expminsal.toString().replace(/\D/g,'');
						if(expminsal.length <= 3)
							{
							$scope.minExp = data.minExp;
							}
						else
							{
							var expminsallastthree = expminsal.substring(expminsal.length-3);
							expminsal = expminsal.substring(0,expminsal.length-3);
							expminsal = expminsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + expminsallastthree;
							expminsal = expminsal;
							$scope.minExp = expminsal;
							}
						
					}
					
					
					
					
					if(expmaxsal == "" || expmaxsal == undefined || expmaxsal == null)
					{
						$scope.maxExp = data.maxExp;
					}
					else
					{
						expmaxsal = expmaxsal.toString().replace(/\D/g,'');
						if(expmaxsal.length <= 3)
							{
							$scope.maxExp = data.maxExp;
							}
						else
							{
							var expmaxsallastthree = expmaxsal.substring(expmaxsal.length-3);
							expmaxsal = expmaxsal.substring(0,expmaxsal.length-3);
							expmaxsal = expmaxsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + expmaxsallastthree;
							expmaxsal = expmaxsal;
							$scope.maxExp = expmaxsal;
							}
						
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
			$scope.searchFields.created = { endDate: moment(), startDate: moment().subtract(1, 'year')};
			  $scope.ranges = {
				        //'All Time'  : [moment().subtract(6, 'year'), moment()],
					    'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],
				        'Today': [moment(),moment()], 
				        'Last 1 month': [moment().subtract(1, 'month'), moment()],
				        'Last 3 months': [moment().subtract(3, 'month'), moment()],
				        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
				        'Last 1 year': [moment().subtract(12, 'month'), moment()]
				        };		
		
			  $scope.keySkillsItems = [ 'All', 'Boolean' ];
				$scope.keySkillsItem = "All";
				$scope.keySkillSelected = function(item) {
					$scope.keySkillsItem = item;
				}
				
				$scope.jobTitleItems = [ 'All', 'Boolean' ];
				$scope.jobTitleItem = "All";
				$scope.jobTitleSelected = function(item) {
					$scope.jobTitleItem = item;
				}
				
				// Auto Complete
				$scope.result = '';
				$scope.options = {
					country : 'us',
					types : '(cities)'
				};
				$scope.details = '';

		}

		$scope.createsubmital = function()
		{
			$scope.candidateid= $("#candidateid").val();
			var selcanid = $scope.candidateid;
			if(selcanid == "" || selcanid == undefined || selcanid == null)
				{
				$("#res").show();
				}
			else
				{
				$("#res").hide();
				$scope.saveOrUpdateSubmittal();
				}
		}

		$scope.searchsubmitalCandidate = function()
		{
			/*$scope.candidateId = $("#candid").val();*/
			$scope.mobileNumber = $("#candidateMobileNub").val();
			if($scope.mobileNumber != '' && ($scope.mobileNumber).length==10){
				//$scope.errMsg1 = false;
			var response = $http.post('IndiaCandidates/getCandidateBymobileNumber?mobileNumber='+ $scope.mobileNumber);
			response.success(function(data, status,headers, config) 
					{
				//alert("data"+JSON.stringify(data));
				$("#submitalselectedcandidate").show();
				$scope.candidateid = data.id;
				$scope.candidatename = data.firstName;
				$scope.candidatetitle = data.title;
				$scope.candidatekeyskills = data.keySkill;
				$scope.candidatelocation = data.location;
				$('#candidateid').val($scope.candidateid);
				$('#candidatename').val($scope.candidatename);
				$('#candidatetitle').val($scope.candidatetitle);
				$('#candidatekeyskills').val($scope.candidatekeyskills);
				$('#candidatelocation').val($scope.candidatelocation);
				$("#res01").hide();
					});
			response.error(function(data, status, headers, config){
				if(status==500){
					$("#res01").show();
				}else{
					 if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
				}
				
			});
			
			}else{
				//$scope.errMsg1 = true;
			}
		}
		
		$scope.jumpToProfile = function() {
			$scope.candidateid=$('#candidateid').val();
			if($scope.candidateid != ''){
			$state.transitionTo("EditIndiaCandidates",{candidateId : $scope.candidateid, page:"search"});
		}
		}
		
		$scope.cancel = function(){
			$state.go($location.search().pageName);
		}

		$scope.addEvent = function(){
			$scope.errMsg = null;
			if($scope.eventHistory.status && $scope.eventHistory.notes){
//			$scope.eventHistory.strCreatedOn = getCurrentTime();
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
		
		
		
		$scope.saveOrUpdateSubmittal = function(){
			$(".underlay").show();
			$scope.errMsg = null;
			$(".underlay").show();
			var res = $http.get('India_JobOrder/findTheStatusOfCandidate?candidateId='+$scope.candidateid+'&jobOrderId='+$location.search().jobOrder);
			res.success(function(data){
				 if(data){
					 if(!data.message){
						 $scope.submittal.submittalEventHistoryDtoList = $scope.submittalEventHistoryDtoList;
						 $scope.submittal.jobOrderId = $location.search().jobOrder;
						 $scope.submittal.candidateId = $scope.candidateid;
							 
						 var response = $http.post('India_JobOrder/saveOrUpdateSubmittal', $scope.submittal);
							response.success(function (data,status,headers,config){
								 if($stateParams.jobOrderId != undefined && $stateParams.jobOrderId !=null){
									$.growl.success({title : "Info !", message : "Submittal added Successfully"});
								}
								else{
									$.growl.success({title : "Info !", message : "Submittal added Successfully"});
								}
								 $(".underlay").hide();
								 $timeout(function() {//wait for some time to redirect to another page
									 $rootScope.jobOrderInserted = true;
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
	