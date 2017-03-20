;(function(angular) {
	"use strict";
	angular.module("createofferletter", []).controller(
			"createofferlettercontroller", function($scope,$location,$http, $state,$timeout,$rootScope,dateRangeService) {
				
				$scope.offerLetterDto = {};
				
				$scope.reLocationBenifitList = [
				                'Airfare', 
				                '1 Week motel', 
				                '1 Week meals', 
				                '1 Week rental/cab'
				              ];
				
				$scope.DateFormat = 'MM-dd-yyyy';
				
				$scope.onload = function() {
					if($location.search().candidateId != undefined && $location.search().jobOrderId != undefined){
//						alert($location.search().candidateId+" and "+$location.search().jobOrderId);
						var response = $http.get('offerLetter/getOfferLetterByCandidateAndJobOrderId?candidateId='+$location.search().candidateId+'&jobOrderId='+$location.search().jobOrderId);
						response.success(function(data, status, headers,
								config) {
							if(data){
								$scope.offerLetterDto =data;
								if($scope.offerLetterDto.strDateofbirth){
									$scope.offerLetterDto.strDateofbirth = dateRangeService.convertStringToDate($scope.offerLetterDto.strDateofbirth);
								}
								if($scope.offerLetterDto.strStartdateOfAssignment){
									$scope.offerLetterDto.strStartdateOfAssignment = dateRangeService.convertStringToDate($scope.offerLetterDto.strStartdateOfAssignment);
								}
								var offersal = $scope.offerLetterDto.salaryRate;
								if(offersal == "" || offersal == undefined || offersal == null)
									{
									$scope.offerLetterDto.salaryRate = offersal; 
									}
								else
									{
									offersal = String(offersal).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
									$scope.offerLetterDto.salaryRate = '$ ' + offersal;
									}
								$scope.offerLetterDto.country = "United States";
								$scope.offerLetterDto.clientcountry= "United States";
								
								
								var candphonecell = data.candidateDto.phoneNumber;;
								if(candphonecell == "" || candphonecell == null || candphonecell == undefined)
									{
									$scope.offerLetterDto.phone = data.candidateDto.phoneNumber;
									}
								else
									{
									candphonecell = candphonecell.replace(/\D/g,'');
									candphonecell = candphonecell.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
									$scope.offerLetterDto.phone = candphonecell;
									}
								
								
//								alert($scope.offerLetterDto.commibonusEligible);
							}else{
								 $http.get('jobOrder/getSubmittalById/'+$location.search().submittalId).success(function(data){
									 if(data){
										 $scope.offerLetterDto.firstName = data.candidateDto.firstName;
										 $scope.offerLetterDto.lastName = data.candidateDto.lastName;
										 $scope.offerLetterDto.email = data.candidateDto.email;
										 $scope.offerLetterDto.immigrationStatus = data.candidateDto.visaType;
										 $scope.offerLetterDto.phone = data.candidateDto.phoneNumber;
										 $scope.offerLetterDto.street1 = data.candidateDto.street1;
										 $scope.offerLetterDto.street2 = data.candidateDto.street2;
										 $scope.offerLetterDto.city = data.candidateDto.city;
										 $scope.offerLetterDto.state = data.candidateDto.state;
										 $scope.offerLetterDto.country = data.candidateDto.country;
										 $scope.offerLetterDto.zipcode = data.candidateDto.zipcode;
										 $scope.offerLetterDto.status = constants.OFFER_LETTER_CREATED;
										 $scope.offerLetterDto.recruiterName = data.createdBy;
										 if(data.userDto.userRole == constants.ADM){
											 $scope.offerLetterDto.bdmName = data.userDto.assignedBdm;
										 }else{
											 $scope.offerLetterDto.bdmName = data.userDto.userId;
										 }
										 $scope.offerLetterDto.jobOrderId = data.jobOrderId;
										 $scope.offerLetterDto.candidateId = data.candidateId;
										 $scope.offerLetterDto.candidateId = data.candidateId;
										 $scope.offerLetterDto.country = "United States";
										 $scope.offerLetterDto.clientcountry= "United States";
										 
										 										 
										 var candphonecell = data.candidateDto.phoneNumber;;
											if(candphonecell == "" || candphonecell == null || candphonecell == undefined)
												{
												$scope.offerLetterDto.phone = data.candidateDto.phoneNumber;
												}
											else
												{
												candphonecell = candphonecell.replace(/\D/g,'');
												candphonecell = candphonecell.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
												$scope.offerLetterDto.phone = candphonecell;
												}
											
									 }
								 });
								
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
				}

				$scope.createofferletter = function()
				{
					var offersal = $("input[ng-model='offerLetterDto.salaryRate']").val();
					if(offersal == "" || offersal == undefined || offersal == null)
						{
						$scope.offerLetterDto.salaryRate = offersal; 
						}
					else
						{
						offersal = offersal.replace(/\D/g,'');
						$scope.offerLetterDto.salaryRate = offersal;
						}
					
					
					if($scope.offerLetterDto.benifitsCategory){
					if($scope.offerLetterDto.benifitsCategory === constants.category1)
					{
						$scope.offerLetterDto.electingMedicalHourly = false;
						$scope.offerLetterDto.waviningMedicalHourly = false;
					}
					else
					{
						$scope.offerLetterDto.electingMedicalSalired = false;
						$scope.offerLetterDto.waviningMedicalSalired = false;
					}
					}
					if($scope.offerLetterDto.commibonusEligible){
					if($scope.offerLetterDto.commibonusEligible == constants.No){
						$scope.offerLetterDto.bonusDescription = null;
					}}
//					alert(JSON.stringify($scope.offerLetterDto));
					if($scope.offerLetterDto.strDateofbirth && $scope.offerLetterDto.strDateofbirth instanceof Date){
					$scope.offerLetterDto.strDateofbirth = dateRangeService.formatDate($scope.offerLetterDto.strDateofbirth);
					}
					if($scope.offerLetterDto.strStartdateOfAssignment && $scope.offerLetterDto.strStartdateOfAssignment instanceof Date){
					$scope.offerLetterDto.strStartdateOfAssignment = dateRangeService.formatDate($scope.offerLetterDto.strStartdateOfAssignment);
					}
					$(".underlay").show();
					 var response = $http.post('offerLetter/saveOrUpdateOfferLetter', $scope.offerLetterDto);
						//var response = $http.post("",formData);
						response.success(function (data,status,headers,config){
							if($scope.offerLetterDto.offerLetterId){
								$.growl.success({title : "Info !", message : "Offer-letter Updated Successfully"});
							}else{
								$.growl.success({title : "Info !", message : "Offer-letter Created Successfully"});
							}
							$state.go("offerLetterReports");
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
				
				
				
				
				$scope.offerlettervalidation = function()
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

				
				
				
				
				$scope.visaDateopen = function() {
					$scope.visaDatePopup.opened = true;
				};
				$scope.visaDatePopup = {
					opened : false
				};
				
				$scope.startDateopen = function() {
					$scope.startDatePopup.opened = true;
				};
				$scope.startDatePopup = {
					opened : false
				};
				
				$scope.cancelAction = function(){
					if($location.search().pageName){
						$state.go($location.search().pageName);
					}else{
						$state.go("offerLetterReports");
					}
				};
				
				

			});

})(angular);