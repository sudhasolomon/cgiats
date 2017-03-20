;(function(angilar){
	"use strict";
	angular.module("openJobOrdersApp", ['jcs-autoValidate', 'ui.bootstrap', 'bsTable', 'ngTable'])
	
	
.directive('maskphone', function($http) {
    return {
      require: 'ngModel', // requiring ng-model directive on the element
      link: function(scope, element, attrs, ngModelCtrl) {

        var jquery_element = $(element);
        jquery_element.mask("(999) 999 9999");

        //listen for events on text element
        jquery_element.on('keyup paste focus blur', function() {
            var val = $(this).val();

            // update the ng-model value
            ngModelCtrl.$setViewValue(val);
            ngModelCtrl.$render();

        })

      }
    }
})
	
	
	
	
	
	.config(function($locationProvider) {
    $locationProvider.html5Mode({
      enabled: true,
      requireBase: false
    });
})
	
	
	.controller("openJobOrdersController", function($scope, $http, $location, NgTableParams){
		var vale = "";
		$scope.contactList = [];
		var yesterDayData = [];
		$scope.yesterdayReports = [];
		$scope.candidates = [];
		$scope.candidates.push({});
    	var candidateData = [];
    	var expansionData = [];
    	$scope.jobOrderTable = false;
    	$scope.RecruitersReportTable = false;
    	
    	
    	$scope.candidateDto = {createdUser:$location.search().portalName};
    	
    	
		$scope.onload = function()
		{
			$scope.pageName = "Open Job Orders";
			$scope.getdetails();
		}
		
		
		
		$scope.getdetails = function()
		{
			$(".underlay").show();
			var response = $http.get("jobOrder/getOpenJobOrders?portalName="+$location.search().portalName);
			
			response.success(function(data, status,headers, config) 
					{
					
				$scope.tableParams = new NgTableParams({}, { dataset: data});
		      });
			response.error(function(data, status, headers, config){
  			  
  		  });
			
			$(".underlay").hide();
		
		}
		
		$scope.getjobdetails = function(user)
		{
			$(".underlay").show();
			var response = $http.get('jobOrder/getOpenJobOrdersDescription/'+user.id);
			response.success(function (data,status,headers,config){
				if(data){
					$scope.candidateDto.jobOrderId = user.id;
					$("input[ng-model='jobtitle']").val(user.title);
					$("input[ng-model='jobpositions']").val(user.numOfPos);
					$("input[ng-model='joblocation']").val(user.city +", " + user.state);
					$("input[ng-model='jobposteddate']").val(user.strPostedDate);
					$scope.jobdescription = data.description;
				}
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
		
		
		
		
		
		
		$scope.setFile = function(element) {
			$scope
					.$apply(function($scope) {
						$scope.attachment = element.files[0];
					});
			$.growl.success({title : "Info !", message : "Attachment Uploaded Successfully"});
		};
		
		
		
		
		
		$scope.applyjobonline = function(){
    		$(".underlay").show();
    		
    		var presal = $("input[ng-model='candidateDto.presentRate']").val();
    		var expsal = $("input[ng-model='candidateDto.expectedRate']").val();
    		
    		
    		if(presal == "" || presal == undefined || presal == null)
			{
				$scope.candidateDto.presentRate = presal;
			}
		else
			{
				presal = String(presal).replace(/\D/g,'');
				$scope.candidateDto.presentRate = presal;
			}
    		
    		
    		
    		
    		if(expsal == "" || expsal == undefined || expsal == null)
			{
				$scope.candidateDto.expectedRate = expsal;
			}
		else
			{
			expsal = String(expsal).replace(/\D/g,'');
				$scope.candidateDto.expectedRate = expsal;
			}
    		

    		
    		var formData = new FormData();
			formData.append('candidateDto', angular
					.toJson($scope.candidateDto));
			var attachedFile = $scope.uploadresumeContent;
			formData.append('file', attachedFile);
			var response = $http.post('jobOrder/saveOrUpdateCandidateForOnlineJobOrder', formData, {transformRequest : angular.identity,headers : {'Content-Type' : undefined}});
			response.success(function (data,status,headers,config){
				if(data.errMsg){
					$scope.candidateDto = {createdUser:$location.search().portalName};
					$scope.uploadresume = "";
					$scope.ApplyJobForm.$setPristine()
					$scope.errmsg = data.errMsg;
				}else{
					$scope.candidateDto = {createdUser:$location.search().portalName};
					$scope.uploadresume = "";
					$scope.ApplyJobForm.$setPristine()
					$("#applyjob").slideUp();
					$("#success").slideDown();
				}
				$(".underlay").hide();
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
    	}
		
		
		
		
		$scope.jobdetails = function(user)
		{
			if(user.createdBy == "vpeaden")
			{
				$("#openbtn").hide();
				$("#blockbtn").show();
				$("#submitdiv").slideUp();
				$("#openjobordertable").slideUp();
				$("#viewdtls").slideDown("slow");
			}
		else
			{
			$("#openbtn").show();
			$("#blockbtn").hide();
			$("#submitdiv").slideUp();
			$("#openjobordertable").slideUp();
			$("#viewdtls").slideDown("slow");
			}
			$scope.getjobdetails(user);
			
		}
		$scope.applyjob = function(user)
		{
			$scope.candidateDto.jobOrderId = user.id;
			if(user.createdBy == "vpeaden")
				{
				$("#submitdiv").slideUp();
				$("#openjobordertable").slideUp();
				$("#sendresume").slideDown();
				}
			else
				{
				$("#submitdiv").slideUp();
				$("#openjobordertable").slideUp();
				$("#applyjob").slideDown();
				}
		}
		$scope.backtojoborder = function()
		{
			$("#applyreset").click();
			$("#rreesstt").click();
			$("#res").hide();
			$("#openjobordertable").slideDown();
			$("#submitdiv").slideDown();
			$("#addcandidatefields").slideUp();
			$("#viewdtls").slideUp();
			$("#applyjob").slideUp();
			$("#success").slideUp();
			$("#sendresume").slideUp();
			$("#success01").slideUp();
		}
		$scope.applytojoborder = function()
		{
			$("#viewdtls").slideUp();
			$("#applyjob").slideDown();
		}
		$scope.applytojoborderblk = function()
		{
			$("#viewdtls").slideUp();
			$("#sendresume").slideDown();
		}
		
			
			
			
			
			
		$scope.originalResumeFile01 = function(element) {
			$scope
					.$apply(function($scope) {
						var uploadresume = element.files[0].name;
						$scope.uploadresume = uploadresume;
						$("input[ng-model='uploadresume']").css("border-color", "#27a4b0");
						$("input[ng-model='uploadresume']").siblings(".error-msg").hide();
						$scope.originlockbtn = true;
						$scope.origindownloadbtn = true;
						$scope.uploadresumeContent = element.files[0];
					});
			$.growl.warning({title : "Info !", message : "Resume uploaded successfully"});
		};
			
			
			
			
		
		
		
	})
	
	
	
	
	
	
	
	
	.controller("submitprofilecontroller",function($scope, $http, $location) {
		$scope.candidate = {};
    	$scope.usstates = usStates;     	
    	
    	$scope.onload = function()
    	{
    		$scope.candidate.status = "Available";
    		$scope.candidate.uploadedBy = $location.search().portalName+" Portal";
    		$("#uploadbtn02").click(function() {
				$("#mainuploadbtn02").click();
			});
    		
    		$scope.visaFormat = 'MM-dd-yyyy';
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
    	
    	$scope.formatDate = function(inputDate){
    		if(inputDate instanceof Date){
    		var expDate = new Date(inputDate);
        	 var month = '' + (expDate.getMonth() + 1);
             var day = '' + expDate.getDate();
            var  year = expDate.getFullYear();
        	  if (month.length < 2) month = '0' + month;
        	    if (day.length < 2) day = '0' + day;
        	   return [month, (day),year].join('-');
    		}else{
    			return inputDate;
    		}
    	};

    	
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
			
			
			if($scope.candidate.visaExpiryDate){
				$scope.candidate.visaExpiryDate = $scope.formatDate($scope.candidate.visaExpiryDate);
				}
			
			
			
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
		
		
		
			$scope.resst = function()
			{
				$("#res").hide();
			}
		
		
		
		


	});
	
	
	
})(angular);