;(function(angular) {

    "use strict";
    
    angular.module("AddEditIndiaCandidates",['ngMaterial', 'ngMessages', 'ui.bootstrap', 'ngStorage' ])
    .service("resumesViewAndDownload",function($mdDialog, $mdMedia, $http){
		
				this.getResumeContent = function(file){
					var formData = new FormData();
					formData.append("file",file);
					
					var response = $http.post('searchResume/readFileContent',formData, {
						transformRequest : angular.identity,
						headers : {
							'Content-Type' : undefined
						}
						});
					response.success(function(data, headers, status,
							config) {
						return data.content;
					});
					response.error(function(data, status, headers, config){
	        			  if(status == constants.FORBIDDEN){
	        				location.href = 'login.html';
	        			  }else{  			  
	        				$state.transitionTo("ErrorPage",{statusvalue  : status});
	        			  }
	        		  });
					return response;
				}
				this.viewResume = function(element,fileContent){
					 	$mdDialog
					 	.show({
					 		controller : ResumeDialogController,
					 		templateUrl : 'views/dialogbox/resumedialogbox.html',
					 		parent : angular
								.element(document.body),
								targetEvent : element,
								locals : {
									CandidateText : fileContent,
									CandidateKeywords : ""
								},
								clickOutsideToClose : true,
					 		});
					}
				
				this.downloadResume = function (fileContent, firstName, candidateId){
						var A = fileContent;
						var a = document
							.createElement('a');
						a.href = 'data:attachment/octet-stream,'
							+ encodeURIComponent(A);
						a.target = '_blank';
						a.download = 'rsm' + candidateId
							+ '-' + firstName
							+ '.doc';
						document.body.appendChild(a);
						a.click();
					}
				function ResumeDialogController($scope,
						$mdDialog, CandidateText, CandidateKeywords ) {
						$scope.CandidateText = CandidateText;
						$scope.CandidateKeywords = CandidateKeywords;

						$scope.cancel = function() {
						$mdDialog.cancel();
						};
					}
				})
    .controller("AddEditIndiaController",function($rootScope, $scope, $sessionStorage, $interval,resumesViewAndDownload,
			$http,$state, $stateParams, $mdDialog, $mdMedia, $window){
    	$scope.$storage = $sessionStorage;
    	delete $scope.$storage.originalResume;
    	delete $scope.$storage.rtrDocument;
    	delete $scope.$storage.cgiResume;
    	delete $scope.$storage.originalFileContent ;
    	delete $scope.$storage.rtrFileContent ;
    	delete $scope.$storage.cgiFileContent ;
    	$scope.candidate={};
    	$scope.states = indiaStates;
    	
    	$scope.onload = function() {
    		$(".underlay").show();
    	var response = $http.get('IndiaCandidates/getUserDetails');
		response.success(function(data, status,headers, config){
			$scope.atsUserIds = data.userIds;
			$scope.portalEmailIds =data.portalEmails;
//			$scope.uploaded = data.uploaded;
//			$scope.uploaded.splice(0,0,"Dice");
//			$scope.uploaded.splice(0,0,"Red Galaxy");
//			$scope.uploaded.splice(0,0,"Techfetch");
//			$scope.uploaded.splice(0,0,"Monster");
//			$scope.uploaded.splice(0,0,"CGI");
//			$scope.uploaded.splice(0,0,"Sepeare");
//			$scope.uploaded.splice(0,0,"Careerbuilder");
		});
		response.error(function(data, status, headers, config){
			  if(status == constants.FORBIDDEN){
				location.href = 'login.html';
			  }else{  			  
				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  }
		  });

		if($rootScope.rsLoginUser && $rootScope.rsLoginUser.userId){
			$scope.candidate.atsUserId = $rootScope.rsLoginUser.userId;
		}
		
		 if($stateParams.candidateId !=null){
		var candidateId = $stateParams.candidateId;
		$scope.$storage.candidateId = $stateParams.candidateId;
		 }
		 if (candidateId != null) {
			 $(".underlay").show();
				var response = $http.get('IndiaCandidates/getCandidateForEdit?candidateId='+candidateId);
					response.success(function(data, status,headers, config) {
						
						$scope.indiaCandidate = {
								candidateId : candidateId,
								firstname : data.firstname,
								lastname : data.lastname,
								email : data.email,
								status : data.status,
								reason : data.reason,
								phoneWork : data.phoneWork,
								phoneCell : data.phoneCell,
								address1 : data.address1,
								address2 : data.address2,
								city : data.city,
								state : data.state,
								zip : data.zip,
								visaType : data.visaType,
								visaExpiryDate : data.visaExpiryDate,
								title : data.title,
								skills : data.skills,
								keySkills : data.keySkills,
								jobType : data.jobType,
								qualification : data.qualification,
								totalExperience : data.totalExperience,
								lastCompany : data.lastCompany,
								lastPosition : data.lastPosition,
								employmentStatus : data.employmentStatus,
								minSalaryRequirement : data.minSalaryRequirement,
								presentRate : data.presentRate,
								expectedRate : data.expectedRate,
								uploadedBy : data.uploadedBy,
								portalEmail : data.portalEmail,
								atsUserId : data.atsUserId,
								otherResumeSource : data.otherResumeSource,
								relevantExperience : data.relevantExperience
							}
						$scope.indiaCandidate.phoneCell = parseInt(data.phoneCell); 
						$scope.indiaCandidate.phoneWork = parseInt(data.phoneWork);  
						var presal = data.presentRate;
						var expsal = data.expectedRate;
						
						if(presal == "" || presal == undefined || presal == null)
						{
							$scope.indiaCandidate.presentRate = data.presentRate;
						}
						else
						{
							presal = presal.toString().replace(/\D/g,'');
							if(presal.length <= 3)
								{
								$scope.indiaCandidate.presentRate = data.presentRate;
								}
							else
								{
								var presallastthree = presal.substring(presal.length-3);
								presal = presal.substring(0,presal.length-3);
								presal = presal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + presallastthree;
								presal = presal;
								$scope.indiaCandidate.presentRate = presal;
								
								}
							
						}
						
						
						
						
						if(expsal == "" || expsal == undefined || expsal == null)
						{
							$scope.indiaCandidate.expectedRate = data.expectedRate;
						}
						else
						{
							expsal = expsal.toString().replace(/\D/g,'');
							if(expsal.length <= 3)
								{
								$scope.indiaCandidate.expectedRate = data.expectedRate;
								}
							else
								{
								var expsallastthree = expsal.substring(expsal.length-3);
								expsal = expsal.substring(0,expsal.length-3);
								expsal = expsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + expsallastthree;
								expsal = expsal;
								$scope.indiaCandidate.expectedRate = expsal;
								
								}
							
						}
						
						
						
						$scope.candidateStatus = data.statusHistory;
						
						$scope.$storage.originalFileContent = data.resumeContent;
						$scope.$storage.rtrFileContent = data.rtrContent;
						$scope.$storage.cgiFileContent = data.cgiContent;
						var originalcheckfile = $scope.$storage.originalFileContent;
						if( originalcheckfile == "" || originalcheckfile == undefined)
							{
							$scope.originalResume = "N/A";
							$scope.origuploadbtn = true;
							$scope.originlockbtn = false;
							$scope.origindownloadbtn = false;
							}
						else
							{
							$scope.originalResume = data.originalResumeUpdatedMsg;
							$scope.origuploadbtn = false;
							$scope.originlockbtn = true;
							$scope.origindownloadbtn = true;
							}
						var cgicheckfile = $scope.$storage.cgiFileContent;
						if(cgicheckfile == "" || cgicheckfile == undefined)
							{
							$scope.cgiResume = "N/A";
							$scope.cgiuploadbtn = true;
							$scope.cgilockbtn = false;
							$scope.cgidownloadbtn = false;
							}
						else
							{
							$scope.cgiResume = data.cgiDocumentUpdatedMsg;
							$scope.cgiuploadbtn = false;
							$scope.cgilockbtn = true;
							$scope.cgidownloadbtn = true;
							}
						
						var rtrcheckfile = $scope.$storage.rtrFileContent;
						if(rtrcheckfile == "" || rtrcheckfile == undefined)
							{
							$scope.rtrResume = "N/A";
							$scope.rtruploadbtn = true;
							$scope.rtrlockbtn = false;
							$scope.rtrdownloadbtn = false;
							}
						else
							{
							$scope.rtrResume = data.rtrDocumentUpdatedMsg;
							$scope.rtruploadbtn = false;
							$scope.rtrlockbtn = true;
							$scope.rtrdownloadbtn = true;
							}
					});
					response.error(function(data, status, headers, config){
	        			  if(status == constants.FORBIDDEN){
	        				location.href = 'login.html';
	        			  }else{  			  
	        				$state.transitionTo("ErrorPage",{statusvalue  : status});
	        			  }
	        		  });
					$(".underlay").hide();
    	 }
		 $(".underlay").hide();
    	}
    	$scope.originalResumeFile = function(element) {
			$scope
					.$apply(function($scope) {
						var origfilename = element.files[0].name;
						$scope.originalResume = origfilename;
						$("input[ng-model='originalResume']").css("border-color", "#27a4b0");
						$("input[ng-model='originalResume']").siblings(".error-msg").hide();
						$scope.originlockbtn = true;
						$scope.origindownloadbtn = true;
						$scope.$storage.originalResume = element.files[0];
					});
			$.growl.success({title : "Info !", message : "Resume uploaded successfully"});
		};
		$scope.rtrDocumentFile = function(element) {
		
			$scope.$apply(function($scope) {
				var rtrfilename = element.files[0].name;
				$scope.rtrResume = rtrfilename;
				$scope.rtrlockbtn = true;
				$scope.rtrdownloadbtn = true;
				$scope.$storage.rtrDocument = element.files[0];
					});
			$.growl.success({title : "Info !", message : "Document uploaded successfully"});
		};
		$scope.cgiResumeFile = function(element) {
			$scope
					.$apply(function($scope) {
						var cgifilename = element.files[0].name;
						$scope.cgiResume = cgifilename;
						$scope.cgilockbtn = true;
						$scope.cgidownloadbtn = true;
						$scope.$storage.cgiResume = element.files[0];
					});
			$.growl.success({title : "Info !", message : "Resume uploaded successfully"});
		};
		
		
		$scope.addIndiaCandidate = function(candidate) {
			$(".underlay").show();
			
			$scope.candidate.presentRate =  $("input[ng-model='candidate.presentRate']").val().replace(/\D/g,'');
			$scope.candidate.expectedRate =  $("input[ng-model='candidate.expectedRate']").val().replace(/\D/g,'');
//			alert($scope.candidate.presentRate + ' | ' +  $scope.candidate.expectedRate);
			
			
			var workphone = $scope.candidate.phoneWork;
			$scope.candidate.phoneWork=workphone;
										
			var mobilephone = $scope.candidate.phoneCell;
			$scope.candidate.phoneCell = mobilephone; 
			
			var formData = new FormData();
			var fileIndex = 0;
			
			$scope.requestFileNames = '';
			formData.append('candidate', angular
					.toJson($scope.candidate));
			var originalFile = $scope.$storage.originalResume;
			formData.append('file', originalFile);
			$scope.requestFileNames += ',originalFile';
			
			
			var rtrFile = $scope.$storage.rtrDocument;
			if(rtrFile != null){ 
				formData.append("file", rtrFile);
				$scope.requestFileNames += ',rtrFile';
				
			}
			
			var cgiFile = $scope.$storage.cgiResume;
			if(cgiFile != null){
				formData.append("file", cgiFile);
				$scope.requestFileNames += ',cgiFile';
				
			}
			formData.append('fileNames', $scope.requestFileNames);
			var response = $http.post(
					'IndiaCandidates/saveIndiaCandidate', formData, {
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
					delete $scope.$storage.originalResume;
					delete $scope.$storage.rtrDocument;
					delete $scope.$storage.cgiResume;
					$state.transitionTo("indiacandidate",{newCandidate : true});
				}
				if(headers == 204){
					$(".underlay").hide();
					$.growl	.error({title : "Info !",message : "The candidate already existed with the same mobile num or email"});
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
		
		$scope.save = function(indiaCandidate) {
			$(".underlay").show();
			$scope.indiaCandidate = indiaCandidate;
			if($scope.indiaCandidate.uploadedBy != "Other"){
				$scope.indiaCandidate.otherResumeSource = "";
				}else{
					
				}
			var formData = new FormData();
			var fileIndex = 0;
			
			$scope.requestFileNames = '';
			
			formData.append('candidate', angular.toJson($scope.indiaCandidate));
			var originalFile = $scope.$storage.originalResume;
			if(originalFile != null){ 
			formData.append('file', originalFile);
			$scope.requestFileNames += ',originalFile';
			delete $scope.$storage.originalResume;
			}
			
			var rtrFile = $scope.$storage.rtrDocument;
			if(rtrFile != null){ 
				formData.append("file", rtrFile);
				$scope.requestFileNames += ',rtrFile';
				delete $scope.$storage.rtrDocument;
			}
			
			var cgiFile = $scope.$storage.cgiResume;
			if(cgiFile != null){
				formData.append("file", cgiFile);
				$scope.requestFileNames += ',cgiFile';
				delete $scope.$storage.cgiResume;
			}
			
			
			formData.append('fileNames', $scope.requestFileNames);
			
			var response = $http.post('IndiaCandidates/updateIndiaCandidate',formData, {
				transformRequest : angular.identity,headers : {
					'Content-Type' : undefined	}
				});
			response.success(function(data, headers, status,config) {
				$(".underlay").hide();
			 
				$state.transitionTo("indiacandidate",{editCandidate : true});
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
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
		
		
		
		
		
		
		
		
		
		/*-----------------Add Edit Resumes view and download--------------------*/ 
		
		$scope.originalResumeView = function(element){
			if($scope.$storage.originalResume != null){
				resumesViewAndDownload.getResumeContent($scope.$storage.originalResume).then(function(response){
					resumesViewAndDownload.viewResume(element, response.data.content);
				});
			}else{
			resumesViewAndDownload.viewResume(element, $scope.$storage.originalFileContent);
		}
		}
		$scope.downloadOriginalResume = function(){
			if($scope.$storage.originalResume != null){
				resumesViewAndDownload.getResumeContent($scope.$storage.originalResume).then(function(response){
					resumesViewAndDownload.downloadResume(response.data.content, $scope.$storage.firstName, $scope.$storage.candidateId );
				});
			}else{
//			resumesViewAndDownload.downloadResume($scope.$storage.originalFileContent, $scope.$storage.firstName, $scope.$storage.candidateId );
			$window.location = 'IndiaCandidates/downloadResumeByCandidateId/'+$scope.$storage.candidateId+'/'+constants.ORGDOC;
			}
		}
		
		$scope.viewCgiResume = function(element){
			
			if($scope.$storage.cgiResume != null){
				resumesViewAndDownload.getResumeContent($scope.$storage.cgiResume ).then(function(response){
					resumesViewAndDownload.viewResume(element, response.data.content);
				});
			}else{
			resumesViewAndDownload.viewResume(element,$scope.$storage.cgiFileContent);}
		}
		$scope.downloadCgiResume = function(){
			if($scope.$storage.cgiResume != null){
				resumesViewAndDownload.getResumeContent($scope.$storage.cgiResume ).then(function(response){
					resumesViewAndDownload.downloadResume(response.data.content, $scope.$storage.firstName, $scope.$storage.candidateId );
				});
			}else{
//			resumesViewAndDownload.downloadResume($scope.$storage.cgiFileContent, $scope.$storage.firstName, $scope.$storage.candidateId );
			$window.location = 'IndiaCandidates/downloadResumeByCandidateId/'+$scope.$storage.candidateId+'/'+constants.PROCDOC;
			}
		}
		
		$scope.viewRtrDocument = function(element){
			
			if($scope.$storage.rtrDocument != null){
				resumesViewAndDownload.getResumeContent($scope.$storage.rtrDocument).then(function(response){
					resumesViewAndDownload.viewResume(element, response.data.content);
				});
			}else{
			resumesViewAndDownload.viewResume(element, $scope.$storage.rtrFileContent);}
		}
		$scope.downloadRtrDocument = function(){
			if($scope.$storage.rtrDocument != null){
				resumesViewAndDownload.getResumeContent($scope.$storage.rtrDocument).then(function(response){
					resumesViewAndDownload.downloadResume(response.data.content, $scope.$storage.firstName, $scope.$storage.candidateId );
				});
			}else{
//			resumesViewAndDownload.downloadResume($scope.$storage.rtrFileContent, $scope.$storage.firstName, $scope.$storage.candidateId );
			$window.location = 'IndiaCandidates/downloadResumeByCandidateId/'+$scope.$storage.candidateId+'/'+constants.RTRDOC;
			}
		}
		
		/*-----------------Add Edit Resumes view and download End--------------------*/ 
		
		
		
		$scope.updateStatus = function(){
			
			var updateStatus={
					candidateId : $stateParams.candidateId,
					status : $scope.indiaCandidate.status
			}
		var response = $http.post("IndiaCandidates/indiaCandidateStatusUpdate", updateStatus);
			response.success(function(data,status,headers,config) {
				//alert(JSON.stringify(data));
				if(data){
					
				$scope.indiaCandidate.reason =data.reason;
				}
			});
			response.error(function(data, status, headers, config){
    			  if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				$state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
    		  });
			
			$scope.indiaCandidate.reason ="status";
		}
		

		$scope.editFirstTab = function() {
			$scope.tabs[1].active = true;
		}

    	$scope.originlck = function()
		{
			$scope.origuploadbtn = true;
		}
		
		$scope.cgilck = function()
		{
			$scope.cgiuploadbtn = true;
		}
		$scope.rtrlck = function()
		{
			$scope.rtruploadbtn = true;
		}
		
		$scope.tabs = [ {
			type : 'tab1',
			active : true
		}, {
			type : 'tab2'
		} ];
		$scope.firstTab = function() {
			 
			$scope.tabs[1].active = true;
		}

		$scope.addreset01 = function()
		{
			$scope.candidate.firstname = "";
			$scope.candidate.lastname = "";
			$scope.candidate.email = "";
			$scope.candidate.status = "";
			$scope.candidate.phoneWork = "";
			$scope.candidate.phoneCell = "";
			$scope.candidate.address1 = "";
			$scope.candidate.address2 = "";
			$scope.candidate.city = "";
			$scope.candidate.state = "";
			$scope.candidate.zip = "";
			$scope.AddIndiaCandidateForm.$setPristine()
		}
		
		
		
		
		$scope.addreset02 = function()
		{
			$scope.candidate.title = "";
			$scope.candidate.keySkills = "";
			$scope.candidate.skills = "";
			$scope.candidate.jobType = "";
//			$scope.candidate.qualification = "";
			$scope.candidate.totalExperience = "";
			$scope.candidate.lastCompany = "";
			$scope.candidate.lastPosition = "";
			$scope.candidate.employmentStatus = "";
			$scope.candidate.minSalaryRequirement = "";
			$scope.candidate.presentRate = "";
			$scope.candidate.expectedRate = "";
			$scope.AddIndiaCandidateForm.$setPristine()
		}
		
		$scope.addreset03 = function()
		{
			$(".lockbtn").hide();;
			$(".uploadbtn").show();
			$scope.candidate.uploadedBy = $rootScope.rsLoginUser.userId;
			/*$("select[ng-model='candidate.uploadedBy']").prop('selectedIndex',0);*/
			$("select[ng-model='candidate.portalEmail']").prop('selectedIndex',0);
			$("select[ng-model='candidate.atsUserId']").prop('selectedIndex',0);
			$scope.originalResume = "";
			$scope.cgiResume = "";
			$scope.rtrResume = "";
			$scope.AddIndiaCandidateForm.$setPristine()
		}
		
		
		$scope.statushist = function()
	      {
	       if($("#statushistorytbl").is(":visible"))
	       {
	       $("#statushistorytbl").slideUp("slow");
	       $("#statushistorybtn i").removeClass("fa-minus-square");
	       $("#statushistorybtn i").addClass("fa-plus-square");
	       }
	       else
	       {
	       $("#statushistorytbl").slideDown("slow");
	       $("#statushistorybtn i").removeClass("fa-plus-square");
	       $("#statushistorybtn i").addClass("fa-minus-square");
	       }
	      }
		
    });
   
    
})(angular);