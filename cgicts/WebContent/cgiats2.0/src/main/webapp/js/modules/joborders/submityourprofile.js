;(function(angular){
	"use strict";
	
	angular.module("submitprofilemodule",[])
    .controller("submitprofilecontroller",function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state,$location) {
		$scope.candidate = {};
    	$scope.usstates = usStates;     	
    	
    	$scope.onload = function()
    	{
    		$scope.candidate.status = "Available";
    		$scope.candidate.uploadedBy = $location.search().portalName+" Portal";
    		$("#uploadbtn02").click(function() {
				$("#mainuploadbtn02").click();
			});
    		
    		
        	$scope.visaDateopen = function() {
    			$scope.visaDatePopup.opened = true;
    		};
    		$scope.visaDatePopup = {
    				opened : false
    			};
    		
    		
    		
    		
    		
    		$scope.candidate.securityClearance = "true";
    		
    		
    		
    		
    	}
    	
    	
    	$scope.originalResumeFile = function()
    	{
    		$("#tony").show();
    	}
    	
    	$scope.submitprofilefields = function()
    	{
    		$("#submitdiv").hide();
    		$("#openjobordertable").slideUp();
    		$("#addcandidatefields").slideDown();
    		angular.element("#submitprofilecont").scope().addreset01();
    		angular.element("#submitprofilecont").scope().addreset02();
    		angular.element("#submitprofilecont").scope().frstab();
//    		alert(JSON.stringify($scope.candidate));
    	}

    	$scope.tabs = [ {
			type : 'tab0',
			active : true
		}, {
			type : 'tab2'
		} ];
$scope.frstab = function()
{
	$scope.tabs[0].active = true;
}
		
    	
    	$scope.createCandidatevalidate = function()
		{
			setTimeout(function() 
					  {
				var mandfeild = $("form").find(".error-msg");
				if(mandfeild .length > 0)
					{
					$("#res").css("display", "block");
					}
				else
					{
					$("#res").css("display", "none");
					}
					  }, 5);
		}
    	

    	
    	
    	
    	
    	$scope.submitprofile = function()
    	{
    		$(".underlay").show();
    		var workphone = $scope.candidate.phoneWork;
			if(workphone)
				{
				workphone = workphone.replace(/\D/g,'');
				$scope.candidate.phoneWork=workphone;
				}
			
			
			var mobilephone = $scope.candidate.phoneCell;
			if(mobilephone)
				{
				mobilephone = mobilephone.replace(/\D/g,'');
				$scope.candidate.phoneCell=mobilephone;
				}
			
			$scope.candidate.minSalaryRequirement = $("input[ng-model='candidate.minSalaryRequirement']").val().replace(/\D/g,'');
			$scope.candidate.presentRate =  $("input[ng-model='candidate.presentRate']").val().replace(/\D/g,'');
			$scope.candidate.expectedRate =  $("input[ng-model='candidate.expectedRate']").val().replace(/\D/g,'');
			
			$scope.requestFileNames = '';
			var formData = new FormData();
			formData.append('candidate', angular
					.toJson($scope.candidate));
			var originalFile = $scope.originalResumeContent;
			formData.append('file', originalFile);
			$scope.requestFileNames += ',originalFile';
			
			formData.append('fileNames', $scope.requestFileNames);
			
			
			var response = $http.post(
					'searchResume/saveOnlineCandidate', formData, {
						transformRequest : angular.identity,
						headers : {
							'Content-Type' : undefined
						}
					});
			
			response
			.success(function(data, headers, status,
					config) {
				if(headers == 200){
					
					$(".underlay").hide();
					
					delete $scope.originalResumeContent;
					$("#success01").slideDown();
		    		$("#addcandidatefields").slideUp();
				}
				if(headers == 204){
					$.growl	.error({title : "Info !",message : "The candidate already exists"});
					$(".underlay").hide();
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
    	
		$scope.originalResumeFile01 = function(element) {
			$scope
					.$apply(function($scope) {
						var origfilename = element.files[0].name;
						$scope.originalResume = origfilename;
						$("input[ng-model='originalResume']").css("border-color", "#27a4b0");
						$("input[ng-model='originalResume']").siblings(".error-msg").hide();
						$scope.originlockbtn = true;
						$scope.origindownloadbtn = true;
						$scope.originalResumeContent = element.files[0];
					});
			$.growl.warning({title : "Info !", message : "Resume uploaded successfully"});
		};
		
		
		
		$scope.addreset01 = function()
		{
			$scope.candidate.firstname = "";
			$scope.candidate.lastname = "";
			$scope.candidate.email = "";
			$scope.candidate.phoneWork = "";
			$scope.candidate.phoneCell = "";
			$scope.candidate.address1 = "";
			$scope.candidate.address2 = "";
			$scope.candidate.city = "";
			$scope.candidate.state = "";
			$scope.candidate.zip = "";
			$scope.candidate.visaType = "";
			$scope.candidate.visaExpiryDate = "";
			$scope.candidate.securityClearance = "true";
			$scope.submitprofileform.$setPristine()
		}
		
		
		
		
		$scope.addreset02 = function()
		{
			$scope.candidate.title = "";
			$scope.candidate.keySkills = "";
			$scope.candidate.skills = "";
			$scope.candidate.jobType = "";
			$scope.candidate.qualification = "";
			$scope.candidate.totalExperience = "";
			$scope.candidate.lastCompany = "";
			$scope.candidate.lastPosition = "";
			$scope.candidate.employmentStatus = "";
			$scope.candidate.minSalaryRequirement = "";
			$scope.candidate.presentRate = "";
			$scope.candidate.expectedRate = "";
			$scope.originalResume = "";
			$scope.submitprofileform.$setPristine()
		}
		


	});
	
})(angular);