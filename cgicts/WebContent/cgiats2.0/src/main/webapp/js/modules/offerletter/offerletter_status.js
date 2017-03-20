;(function(angular){
	"use strict";	
	angular.module("offerletterstatus",[])
	.controller("offerletterstatuscontroller",function($scope,$location,$http, $state,$timeout,$rootScope) {
		
		$scope.offerLetterHistoryDto = {};
		$scope.errmsg = null;
		
		$scope.onload = function()
		{
			$scope.offerLetterHistoryDto.offerLetterId = $location.search().offerLetterId;
			
			$scope.offerLetterHistoryDto.strStatusCreatedOn = formatteddate(new Date());
			
			if($rootScope.rsLoginUser.userRole == constants.HR){
				$scope.offerLetterStatusList = [
				                                {value:constants.OFFER_LETTER_REQUEST_NOT_RECEIVED},
				                                {value:constants.OFFER_LETTER_REQUEST_RECEIVED},
				                                {value:constants.FIRST_CONTACT_MADE_PRE_OFFER_DISCUSSION},
				                                {value:constants.OFFER_LETTER_SENT_TO_CONSULTANT},
				                                {value:constants.BACKGROUND_CHECK_INITIATED},
				                                {value:constants.DRUG_TEST_INITIATED},
				                                {value:constants.SIGNED_OFFER_RECEIVED},
				                                {value:constants.BENEFITS_ENROLLMENT_FORMS_BEING_SENT_OUT},
				                                {value:constants.BENEFITS_ENROLLMENT_FORMS_NOT_RECEIVED},
				                                {value:constants.BENEFITS_ENROLLMENT_FORMS_RECEIVED},
				                                {value:constants.CANDIDATE_ON_BOARDED},
				                                {value:constants.CANDIDATE_BACKED_OUT}
				                                
								              ];
				
			}else{
				$scope.offerLetterStatusList = [
				                                {value:constants.OFFER_LETTER_CREATED},
				                                {value:constants.OFFER_LETTER_SAVED},
				                                {value:constants.OFFER_LETTER_SUBMITTED}
								              ];
				
			}
			
			$scope.mypagename = "Offer Letter Status";
			
			$scope.StartDateopen = function() {
				$scope.StartDatePopup.opened = true;
			};
			$scope.StartDatePopup = {
				opened : false
			};
			
			
		};
		
		function formatteddate(inputData){
			if(inputData instanceof Date){
	     	  var expDate = new Date(inputData);
	     	 var month = '' + (expDate.getMonth() + 1);
	          var day = '' + expDate.getDate();
	         var  year = expDate.getFullYear();
	     	  if (month.length < 2) month = '0' + month;
	     	    if (day.length < 2) day = '0' + day;
	     	   return [year, month, (day)].join('-');
			}else{
	     	   return inputData;
			}
	       };
		
		$scope.saveOfferLetterStatus = function(){
			$(".underlay").show();
			$scope.errmsg = null;
			
//			alert(JSON.stringify($scope.offerLetterHistoryDto));
			
			var response = $http.post('offerLetter/saveOfferLetterHistory', $scope.offerLetterHistoryDto);
			//var response = $http.post("",formData);
			response.success(function (data,status,headers,config){
				if(data.errMsg){
					$scope.errmsg = data.errMsg;
				}else{
					$scope.offerLetterHistoryDto = {};
					$state.go('offerLetterReports');
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
			
		};
		
	});
	
	
})(angular);