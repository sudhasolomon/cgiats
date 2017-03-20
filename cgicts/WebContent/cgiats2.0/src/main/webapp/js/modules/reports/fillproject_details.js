;(function(angular){
	"use strict";	
	angular.module("fillprojectopage",[])
	.controller("fillprojectcontroller",function($rootScope, $scope, blockUI, $http, $timeout, $location,
			$filter, $window, $state, $stateParams, $sessionStorage,dateRangeService){
		
		//alert($stateParams.id+" id from select"+$stateParams.infoId);
		$scope.reLocationBenifitList = [
		                                'Airfare: for self/family',
										'1 Week motel',
										'1 Week meals',
										'1 Week rental/cab'
						              ];
		$scope.usstates = usStates;
		
		
		$scope.onload = function()
		{
			
			if($location.search().currentLoginUserId){
				 $timeout(function() {//wait for some time to redirect to another page
					 $scope.onloadFun();
				 }, 400);
				}else{
					$scope.onloadFun();
				}
			
		}
		
		
		$scope.onloadFun = function(){
			$(".underlay").show();
			$scope.DateOfBirthopen = function() {
				$scope.DateOfBirthPopup.opened = true;
			};
			$scope.DateOfBirthPopup = {
				opened : false
			};
			
			$scope.StartDateopen = function() {
				$scope.StartDatePopup.opened = true;
			};
			$scope.StartDatePopup = {
				opened : false
			};
			
			$scope.EndDateopen = function() {
				$scope.EndDatePopup.opened = true;
			};
			$scope.EndDatePopup = {
				opened : false
			};
			
			 
				var response = $http.post("consultantInfo/getFillProjectDetails?id="+$stateParams.id+"&infoId="+$stateParams.infoId);
				response.success(function(data, status, config, headers){
					$(".underlay").hide();
//					alert("success data  "+JSON.stringify(data));
					//alert("success data  "+JSON.stringify(data.candidateDto));
					if(data && data.startDate){
						data.startDate = dateRangeService.convertStringToDate(data.startDate);
					}
					if(data && data.endDate){
						data.endDate = dateRangeService.convertStringToDate(data.endDate);
					}
					if(data && data.dateOfBirth){
						data.dateOfBirth = dateRangeService.convertStringToDate(data.dateOfBirth);
					}
					
					$scope.project = {
							candidateId : $stateParams.id,
							candidateInfoId  : $stateParams.infoId,
							bdmFirstName : data.bdmFirstName,
							bdmLastName : data.bdmLastName,
							dateOfBirth : data.dateOfBirth,
							recruiterFirstName : data.recruiterFirstName,
							recruiterLastName :data.recruiterLastName,
							jobOrderId :data.jobOrderId,
							firstName : data.firstName ,
							lastName : data.lastName,
							email : data.email,
							street1 : data.street1,
							street2 :data.street2,
							city : data.city,
							state : data.state,
							zipCode : data.zipCode,
							country : data.country,
							candidatePhone : data.candidatePhone,
							immigrationStatus : data.immigrationStatus,
							phoneAlt : data.phoneAlt,
							employmentStatus : data.employmentStatus,
							source : data.source,
							ssn : data.ssn,
							salaryRate : data.salaryRate,
							perDiem : data.perDiem,
							startDate : data.startDate,
							endDate : data.endDate,
							clientName : data.clientName,
							clientContactFirstName : data.clientContactFirstName,
							clientContactLastName : data.clientContactLastName,
							clientPhone : data.clientPhone,
							extenstion : data.extenstion,
							fax :data.fax,
							clientEmail : data.clientEmail,
							clientStreet1 : data.clientStreet1,
							clientStreet2 :data.clientStreet2,
							clientcity : data.clientcity,
							clientState : data.clientState,
							clientCountry : data.clientCountry,
							clientZipCode : data.clientZipCode,
							clientinvoicing : data.clientinvoicing,
							clientpaymentTerms : data.clientpaymentTerms,
							clientbillRate : data.clientbillRate,
							clientotEligibility : data.clientotEligibility,
							clientotRate : data.clientotRate,
							endClientName : data.endClientName,
							projectManagerName : data.projectManagerName,
							invoicingStreet1 : data.invoicingStreet1,
							invoicingstreet2 : data.invoicingstreet2,
							invoicingcity : data.invoicingcity,
							invoicingState : data.invoicingState,
							invoicingCountry : data.invoicingCountry,
							invoicingZipCode : data.invoicingZipCode,
							projectManagerPhone : data.projectManagerPhone,
							projectManagerExtension :data.projectManagerExtension,
							c2cAgencyName : data.c2cAgencyName,
							c2cContactFirstName : data.c2cContactFirstName,
							c2cContactLastName : data.c2cContactLastName,
							contactPersonPhone : data.contactPersonPhone,
							contactPersonExtension : data.contactPersonExtension,
							contactPersonFax : data.contactPersonFax,
							contactPersonEmail : data.contactPersonEmail,
							c2cstreet1 : data.c2cstreet1,
							c2cstreet2 : data.c2cstreet2,
							c2ccity : data.c2ccity,
							c2cstate : data.c2cstate,
							c2ccountry : data.c2ccountry,
							c2czipCode : data.c2czipCode,
							paidTimeOff : data.paidTimeOff,
							medical : data.medical,
							dentalVision :data.dentalVision,
							relocationBenefits : data.relocationBenefits,
							expections : data.expections,
							specialInstructions : data.specialInstructions
					}
					$scope.project.country = "United States";
					$scope.project.clientCountry = "United States";
					$scope.project.invoicingCountry = "United States";
					$scope.project.c2ccountry = "United States";
					
					/*clientbillRate : data.clientbillRate,
					clientotRate : data.clientotRate,*/
					
					
					
					
					
					var ph01 = data.candidatePhone
					var ph02 = data.phoneAlt;
					var ph03 = data.clientPhone;
					var ph04 = data.fax;
					var ph05 = data.projectManagerPhone;
					var ph06 = data.contactPersonPhone;
					var ph07 = data.contactPersonFax;
					
					if(ph01)
					{
					ph01 = ph01.replace(/\D/g,'');
					ph01 = ph01.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
					$scope.project.candidatePhone = ph01; 
					}
					
					
					if(ph02)
					{
					ph02 = ph02.replace(/\D/g,'');
					ph02 = ph02.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
					$scope.project.phoneAlt = ph02; 
					}
					
					
					if(ph03)
					{
					ph03 = ph03.replace(/\D/g,'');
					ph03 = ph03.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
					$scope.project.clientPhone = ph03; 
					}
					
					
					
					if(ph04)
					{
					ph04 = ph04.replace(/\D/g,'');
					ph04 = ph04.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
					$scope.project.fax = ph04; 
					}
					
					
					
					if(ph05)
					{
					ph05 = ph05.replace(/\D/g,'');
					ph05 = ph05.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
					$scope.project.projectManagerPhone = ph05; 
					}
					
					
					
					
					if(ph06)
					{
					ph06 = ph06.replace(/\D/g,'');
					ph06 = ph06.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
					$scope.project.contactPersonPhone = ph06; 
					}
					
					
					
					
					if(ph07)
					{
					ph07 = ph07.replace(/\D/g,'');
					ph07 = ph07.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
					$scope.project.contactPersonFax = ph07; 
					}
					
					
					
					
					
					
					
					
					var salaryrate = data.salaryRate;
					var billrate = data.clientbillRate;
					var otrate = data.clientotRate;
					
					if(salaryrate)
					{
					salaryrate = salaryrate.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					$scope.project.salaryRate = '$ ' +  salaryrate;
					}
					
					
					if(billrate)
					{
					billrate = billrate.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					$scope.project.clientbillRate = '$ ' +  billrate;
					}
					
					
					if(otrate)
					{
					otrate = otrate.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					$scope.project.clientotRate = '$ ' +  otrate;
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
		 
			
		};
		
		
		
		
		$scope.fillprojectsvalidate = function()
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
		
		
		
		$scope.createsubmital = function(project){

			//alert("in submit project");
//			alert(JSON.stringify(project));

			$(".underlay").show();

			var salaryrate = $("input[ng-model='project.salaryRate']").val();
			var billrate = $("input[ng-model='project.clientbillRate']").val();
			var otrate = $("input[ng-model='project.clientotRate']").val();
			
			if(salaryrate == "" || salaryrate == undefined || salaryrate == null)
			{
				$scope.project.salaryRate = salaryrate; 
			}
		else
			{
			salaryrate = salaryrate.replace(/\D/g,'');
			$scope.project.salaryRate = salaryrate;;
			}
			
			
			if(billrate == "" || billrate == undefined || billrate == null)
			{
				$scope.project.clientbillRate = billrate; 
			}
		else
			{
			billrate = billrate.replace(/\D/g,'');
			$scope.project.clientbillRate = billrate;;
			}
			
			
			if(otrate == "" || otrate == undefined || otrate == null)
			{
				$scope.project.clientotRate = otrate; 
			}
		else
			{
			otrate = otrate.replace(/\D/g,'');
			$scope.project.clientotRate = otrate;;
			}
			
			if(project.dateOfBirth){
				project.dateOfBirth = dateRangeService.formatDate(project.dateOfBirth);
				}
			if(project.startDate){
				project.startDate = dateRangeService.formatDate(project.startDate);
				}
			if(project.endDate){
				project.endDate = dateRangeService.formatDate(project.endDate);
				}
			
			
			var response = $http.post("consultantInfo/saveOrUpdateProjectDetails",project);
			
			response.success(function(data, status, headers, config){
				//alert("success data "+data+"  status"+status );
				$(".underlay").hide();
				$state.transitionTo("consultantinfo",{saveOrUpdate: true});
			});
			response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
		}
		
		$scope.cancel = function (){
			$state.transitionTo("consultantinfo",{cancelButton : true});
		}
		
	});
	
	
})(angular);