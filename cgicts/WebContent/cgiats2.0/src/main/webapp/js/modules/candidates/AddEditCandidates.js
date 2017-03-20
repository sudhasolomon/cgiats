;
(function(angular) {

	"use strict";

	angular.module('AddEditCandidateModule',[ 'ngMaterial', 'ngMessages', 'ui.bootstrap', 'ngStorage' ])
					
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
						$mdDialog, CandidateText, CandidateKeywords) {
						$scope.CandidateText = CandidateText;
						$scope.CandidateKeywords = CandidateKeywords;

						$scope.cancel = function() {
						$mdDialog.cancel();
						};
					}
				})
	.controller('CandidatesController',function($rootScope, $scope, $sessionStorage, $interval,resumesViewAndDownload,
							$http,$state, $stateParams, $mdDialog, $mdMedia,$location,$timeout,$window,dateRangeService) {
		$scope.$storage = $sessionStorage;
		
		$scope.savebtn=false;
		$scope.successMsg = false;
		$scope.candidate={};
		$scope.usstates = usStates;
		
		$scope.onload = function() {
			
			
			if($location.search().currentLoginUserId){
				 $timeout(function() {
					 //wait for some time to redirect to another page
					 $scope.onloadFun();
				 }, 400);
				}else{
					$scope.onloadFun();
				}
			
			$scope.candidate.status = "Available";	
			
			/*FIELD DETAILS FROM DATABASE*/
			delete $scope.$storage.originalResume;
			delete $scope.$storage.rtrDocument;
			delete $scope.$storage.cgiResume;
			delete $scope.$storage.originalFileContent;
			delete $scope.$storage.rtrFileContent;
			delete $scope.$storage.cgiFileContent;
			}
		
					$scope.onloadFun = function() {
							
						$scope.candidate.securityClearance = "false";
					var response = $http.get('searchResume/getUserDetails');
					response.success(function(data, status,headers, config){
						$scope.atsUserIds = data.userIds;
						
						$scope.portalEmailIds =data.portalEmails;
						
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
								var response = $http.get('searchResume/getCandidateForEdit?candidateId='+candidateId);
									response.success(function(data, status,headers, config) {
										//alert("data "+JSON.stringify(data));
										if(data.statusCode == 201){
											$.growl.warning({title : "Warning !",message : data.statusMessage});
										}else{
											$scope.candidateData = {
												candidateId : candidateId,
												firstname : data.firstname,
												lastname : data.lastname,
												email : data.email,
												status : data.status,
												reason : data.statusReason,
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
												otherResumeSource : data.otherResumeSource
											}
												if(data.securityClearance){
													$scope.candidateData.securityClearance = "true";
												}else{
													$scope.candidateData.securityClearance = "false";
												}
									
											if(data.uploadedBy){
												if(resumeSourceNames.indexOf(data.uploadedBy)<0){
													$scope.candidateData.uploadedBy = constants.OTHER;
												}
											}
											if($scope.candidateData.visaExpiryDate){
												$scope.candidateData.visaExpiryDate = dateRangeService.convertStringToDate($scope.candidateData.visaExpiryDate);
											}
											var minsal =  data.minSalaryRequirement;
											var presal = data.presentRate;
											var expsal = data.expectedRate;
											
											if(minsal == "" || minsal == undefined || minsal == null)
											{
												$scope.candidateData.minSalaryRequirement = data.minSalaryRequirement;
											}
											else
											{
											minsal = minsal.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
											minsal = '$ ' + minsal;
											$scope.candidateData.minSalaryRequirement = minsal;
											}
											
											
											
											if(presal == "" || presal == undefined || presal == null)
											{
												$scope.candidateData.presentRate = data.presentRate;
											}
											else
											{
												presal = presal.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
												presal = '$ ' + presal;
											$scope.candidateData.presentRate = presal;
											}
											
											
											if(expsal == "" || expsal == undefined || expsal == null)
											{
												$scope.candidateData.expectedRate = data.expectedRate;
											}
											else
											{
												expsal = expsal.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
												expsal = '$ ' + expsal;
											$scope.candidateData.expectedRate = expsal;
											}
											
											
											
											
											
											
											var editphonecell = data.phoneCell;
											if(editphonecell == "" || editphonecell == null || editphonecell == undefined)
												{
													editphonecell = data.phoneCell;
												}
											else
												{
												editphonecell = editphonecell.replace(/\D/g,'');
												editphonecell = editphonecell.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
												}
											
											
											var editphonework = data.phoneWork;
											if(editphonework== "" || editphonework == null || editphonework == undefined)
												{
												editphonework = data.phoneWork;
												}
											else
												{
												editphonework = editphonework.replace(/\D/g,'');
												editphonework = editphonework.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2 $3');
												}
											
											
											$scope.candidateData.phoneWork = editphonework;
											$scope.candidateData.phoneCell = editphonecell;
											
											$scope.candidateStatus = data.statusHistory;
											
											$scope.addToHotListSrc = data.hot;
											$scope.addToBlockListSrc= data.block;
											 
											$scope.$storage.firstName = data.firstname;
											$scope.$storage.candidateId = candidateId;

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
						}
						// Datepicker Start
						$scope.visaDateOptions = {
							date : new Date(),
							showWeeks : false
						};
						$scope.visaFormat = 'MM-dd-yyyy';
						$scope.visaDateopen = function() {
							$scope.visaDatePopup.opened = true;
						};
						$scope.visaDatePopup = {
							opened : false
						};
						
						$scope.newCandidateMessage = false;
						$scope.existedCandidateMessage = false;
				 
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
							$.growl.warning({title : "Info !", message : "Resume uploaded successfully"});
						};
						$scope.rtrDocumentFile = function(element) {
						
							$scope.$apply(function($scope) {
								var rtrfilename = element.files[0].name;
								$scope.rtrResume = rtrfilename;
								$scope.rtrlockbtn = true;
								$scope.rtrdownloadbtn = true;
								$scope.$storage.rtrDocument = element.files[0];
									});
							$.growl.warning({title : "Info !", message : "Document uploaded successfully"});
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
							$.growl.warning({title : "Info !", message : "Resume uploaded successfully"});
						};
 
						$scope.rows = {
							references : [ {
								referenceName : "",
								referenceEmail : "",
								referencePhone : "",
								referenceCompanyName : "",
								referenceDesignation : ""
							} ]
						}

						$scope.addReference = function() {
							$scope.rows.references.push({
								referenceName : "",
								referenceEmail : "",
								referencePhone : "",
								referenceCompanyName : "",
								referenceDesignation : ""
							})
						}
						
						$scope.addCandidate = function(candidate) {
//							alert("data  "+JSON.stringify(candidate));
							$(".underlay").show();
							$scope.candidate.minSalaryRequirement = $("input[ng-model='candidate.minSalaryRequirement']").val().replace(/\D/g,'');
							$scope.candidate.presentRate =  $("input[ng-model='candidate.presentRate']").val().replace(/\D/g,'');
							$scope.candidate.expectedRate =  $("input[ng-model='candidate.expectedRate']").val().replace(/\D/g,'');
							var workphone = $scope.candidate.phoneWork;
							if(workphone){
								workphone = workphone.replace(/\D/g,'');
								$scope.candidate.phoneWork=workphone;
								}
							
														
							var mobilephone = $scope.candidate.phoneCell;
							if(mobilephone){
							mobilephone = mobilephone.replace(/\D/g,'');
							$scope.candidate.phoneCell = mobilephone;
							}
                         var keyskills = $scope.candidate.keySkills;
                          var len = keyskills.split(",");
                          if(len[0]!="" && len[1]!="" && len[2]!=""){
                        	  if(len[0]!=undefined && len[1]!=undefined && len[2]!=undefined){
                         	 $scope.keyskillsErrorMsg=false;
							 
							var formData = new FormData();
							var fileIndex = 0;
						
							if($scope.candidate.visaExpiryDate instanceof Date){
							$scope.candidate.visaExpiryDate = dateRangeService.formatDate($scope.candidate.visaExpiryDate);
							}
						
							
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
									'searchResume/saveCandidate', formData, {
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
									
									$state.transitionTo("SearchModule",{newCandidate : true});
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
                        	  
                        	  else{
                            	  $scope.keyskillsErrorMsg=true;
                            	  $(".underlay").hide();
                              }
                          }
                          else{
                        	  $scope.keyskillsErrorMsg=true;
                        	  $(".underlay").hide();
                          }
                          
						}

						$scope.save = function(candidateData) {
							$(".underlay").show();
							
							if($scope.candidateData.uploadedBy != "Other"){
								$scope.candidateData.otherResumeSource = "";
								}else{
									
								}
							
							$scope.candidateData.minSalaryRequirement = $("input[ng-model='candidateData.minSalaryRequirement']").val().replace(/\D/g,'');
							$scope.candidateData.presentRate =  $("input[ng-model='candidateData.presentRate']").val().replace(/\D/g,'');
							$scope.candidateData.expectedRate =  $("input[ng-model='candidateData.expectedRate']").val().replace(/\D/g,'');
							
							var workphone = $scope.candidateData.phoneWork;
							if(workphone == "" || workphone == null || workphone == undefined)
								{
								
								}
							else
								{
								workphone = workphone.replace(/\D/g,'');
								$scope.candidateData.phoneWork=workphone;
								}
							
														
							var mobilephone = $scope.candidateData.phoneCell;
							mobilephone = mobilephone.replace(/\D/g,'');
							$scope.candidateData.phoneCell = mobilephone; 
							
							 var keyskills = $scope.candidateData.keySkills;
	                          var len = keyskills.split(",");
	                          if(len[0]!="" && len[1]!="" && len[2]!=""){
	                        	  if(len[0]!=undefined && len[1]!=undefined && len[2]!=undefined){
	                         	 $scope.keyskillsErrorMsg=false;
	                         	 
	                         	 
							$scope.candidateData = candidateData;
							var formData = new FormData();
							var fileIndex = 0;
							
							$scope.requestFileNames = '';
//							alert($scope.candidateData.visaExpiryDate);
							if($scope.candidateData.visaExpiryDate){
							$scope.candidateData.visaExpiryDate = dateRangeService.formatDate($scope.candidateData.visaExpiryDate);
							}
							
							if($stateParams.page && $stateParams.page == constants.MISSING_DATA){
								$scope.candidateData.pageName = constants.MISSING_DATA;
							}
							
//							$scope.candidateData.uploadedBy = $rootScope.rsLoginUser.userId;
//							alert(JSON.stringify($scope.candidateData));
							formData.append('candidate', angular.toJson($scope.candidateData));
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
							
							var response = $http.post('searchResume/updateCandidate',formData, {
								transformRequest : angular.identity,headers : {
									'Content-Type' : undefined	}
								});
							response.success(function(data, headers, status,config) {
								$(".underlay").hide();
								
								$.growl.success({title : "success !",message : "candidate updated successfully"});
								
								if($location.search().currentLoginUserId){
									$rootScope.navigationFromOld = true;
									$state.transitionTo("SearchModule",{editCandidate : false});
								}
								if($stateParams.page == "search"){
								$state.transitionTo("SearchModule",{editCandidate : true});}
								if($stateParams.page == "view"){
								$state.transitionTo("viewcandidate",{editCandidate : true});
								}
								if($stateParams.page == "status"){
									$state.transitionTo("candidatesStatus",{editCandidate : true});
								}
								if($stateParams.page == "missingData"){
									//$.growl.success({title : "success !",message : "Candidate details are updated successfully"});
									$scope.savebtn=true;
									$scope.successMsg = true;
									
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
	                        	  else{
	                            	  $scope.keyskillsErrorMsg=true;
	                            	  $(".underlay").hide();
	                              }
	                          }
	                          else{
	                        	  $scope.keyskillsErrorMsg=true;
	                        	  $(".underlay").hide();
	                          }
						}
						
						/*-----------------Add Edit Resumes view and download--------------------*/ 
						
						
						$scope.$storage.originalFileContent ;
						$scope.$storage.rtrFileContent ;
						$scope.$storage.cgiFileContent ;
						
						
						$scope.originalResumeView = function(element){
							if($scope.$storage.originalResume != null){
								resumesViewAndDownload.getResumeContent($scope.$storage.originalResume).then(function(response){
								resumesViewAndDownload.viewResume(element, response.data.content);
								});
							}else{
							resumesViewAndDownload.viewResume(element, $scope.$storage.originalFileContent);
							if($scope.$storage.originalFileContent ){
							var docStat = "original";
							var status = "viewed";
							saveResumeAuditList($stateParams.candidateId, docStat, status);	
						}}
						}
						$scope.downloadOriginalResume = function(){
							if($scope.$storage.originalResume != null){
								resumesViewAndDownload.getResumeContent($scope.$storage.originalResume).then(function(response){
								resumesViewAndDownload.downloadResume(response.data.content, $scope.$storage.firstName, $scope.$storage.candidateId );
								});
							}else{
//							resumesViewAndDownload.downloadResume($scope.$storage.originalFileContent, $scope.$storage.firstName, $scope.$storage.candidateId );
								$window.location = 'searchResume/resumeByCandidateId/'+$scope.$storage.candidateId+'/'+'original'+'/'+constants.ORGDOC;
							/*if($scope.$storage.originalFileContent ){
							var docStat = "original";
							var status = "downloaded";
							saveResumeAuditList($stateParams.candidateId, docStat, status);}*/
							}
						}
						
						$scope.viewCgiResume = function(element){
							
							if($scope.$storage.cgiResume != null){
								resumesViewAndDownload.getResumeContent($scope.$storage.cgiResume ).then(function(response){
								resumesViewAndDownload.viewResume(element, response.data.content);
								});
							}else{
							resumesViewAndDownload.viewResume(element,$scope.$storage.cgiFileContent);
							if($scope.$storage.cgiFileContent){
							var docStat = "CGI";
							var status = "viewed";
							saveResumeAuditList($stateParams.candidateId, docStat, status);		
							}}
						}
						$scope.downloadCgiResume = function(){
							if($scope.$storage.cgiResume != null){
								resumesViewAndDownload.getResumeContent($scope.$storage.cgiResume ).then(function(response){
									resumesViewAndDownload.downloadResume(response.data.content, $scope.$storage.firstName, $scope.$storage.candidateId );
								});
							}else{
//							resumesViewAndDownload.downloadResume($scope.$storage.cgiFileContent, $scope.$storage.firstName, $scope.$storage.candidateId );
								$window.location = 'searchResume/resumeByCandidateId/'+$scope.$storage.candidateId+'/'+'CGI'+'/'+constants.PROCDOC;
							/*if($scope.$storage.cgiFileContent ){
							var docStat = "CGI";
							var status = "downloaded";
							saveResumeAuditList($stateParams.candidateId, docStat, status);
							}*/
							}
							
						}
						
						$scope.viewRtrDocument = function(element){
							
							if($scope.$storage.rtrDocument != null){
								resumesViewAndDownload.getResumeContent($scope.$storage.rtrDocument).then(function(response){
									resumesViewAndDownload.viewResume(element, response.data.content);
								});
							}else{
								
							resumesViewAndDownload.viewResume(element, $scope.$storage.rtrFileContent);
							if($scope.$storage.rtrFileContent ){
							var docStat = "RTR";
							var status = "viewed";
							saveResumeAuditList($stateParams.candidateId, docStat, status);	}}
						}
						$scope.downloadRtrDocument = function(){
							if($scope.$storage.rtrDocument != null){
								resumesViewAndDownload.getResumeContent($scope.$storage.rtrDocument).then(function(response){
									resumesViewAndDownload.downloadResume(response.data.content, $scope.$storage.firstName, $scope.$storage.candidateId );
								});
							}else{
								
//							resumesViewAndDownload.downloadResume($scope.$storage.rtrFileContent, $scope.$storage.firstName, $scope.$storage.candidateId );
								$window.location = 'searchResume/resumeByCandidateId/'+$scope.$storage.candidateId+'/'+'RTR'+'/'+constants.RTRDOC;
						/*	if($scope.$storage.rtrFileContent ){
							var docStat = "RTR";
							var status = "downloaded";
							saveResumeAuditList($stateParams.candidateId, docStat, status);	}*/
							}
						}
						
						
						function saveResumeAuditList(candidateId, docStat, status){
							var response = $http.post('searchResume/saveResumeAuditList?candidateId='+candidateId+'&docStats='+docStat+'&status='+status);
							response.success(function(data, headers, status, config) {
							});
							response.error(function(data, headers, status, config){
								alert("error data  "+ data);
								 
							});
						}
						/*-----------------Add Edit Resumes view and download End--------------------*/ 
						
						
						/*---------------------------HOT LIST AND BLOCK LIST REASONS-------------------*/
						
						$scope.hotReason = function(e){
//							alert("addToHotListSrc "+$scope.addToHotListSrc );
							
							if($scope.addToBlockListSrc){
								$.growl.warning({title : "Warning !",message : " Candidate#"+ $scope.$storage.candidateId+"" +
										" already in Block list, remove this from Block to make Hot"});
							}else{
							$scope.reason = "";
							$mdDialog.show(	{
												controller : DialogController,
												templateUrl : 'views/dialogbox/hotblock.html',
												parent : angular.element(document.body),
												targetEvent : e,
												clickOutsideToClose : true,
											}).then(function(answer) {
												var hotlist = {
													"candidateId" : $scope.$storage.candidateId,
													"reason" : answer
												}
												$(".underlay").show();
									var response = $http.post("searchResume/saveHotComment",hotlist);
									response.success(function(data,status,headers,config) {
//										alert("hot list  "+!$scope.addToHotListSrc);
										$scope.addToHotListSrc = !$scope.addToHotListSrc;
										$(".underlay").hide();
										 $.growl.success({title : "update !",message : data.statusMessage+ "#"+ $scope.$storage.candidateId});
										});
									response.error(function(data, status, headers, config){
										$(".underlay").hide();
										if(status == constants.FORBIDDEN){
					        				location.href = 'login.html';
					        			  }else{  			  
					        				$state.transitionTo("ErrorPage",{statusvalue  : status});
					        			  }
					        		  });
											},
											function() {
												$scope.reason = 'You cancelled the dialog.';
											});
							}
						}
						
						
						
						$scope.blockReason = function(e){
//							alert("addToBlockListSrc "+$scope.addToBlockListSrc );
							if($scope.addToHotListSrc){
								$.growl.warning({title : "Warning !",message : " Candidate#"+ $scope.$storage.candidateId+"" +
								" already in Hot list, remove this from Hot to make Block"});
							}else{
							$scope.reason = "";
							$mdDialog.show({
												controller : DialogController,
												templateUrl : 'views/dialogbox/hotblock.html',
												parent : angular.element(document.body),
												targetEvent : e,
												clickOutsideToClose : true,
											}).then(function(answer) {
												var blocklist = {
													"candidateId" : $scope.$storage.candidateId,
													"reason" : answer
												}
												$(".underlay").show();
											var response = $http.post("searchResume/saveBlockList",blocklist);
												response.success(function(data,	status,	headers,config) {
//													alert("block list  "+!$scope.addToBlockListSrc);
													$scope.addToBlockListSrc= !$scope.addToBlockListSrc;
													$(".underlay").hide();
													 $.growl.success({title : "update !",message : data.statusMessage+ "#"+ $scope.$storage.candidateId});
												});
												response.error(function(data, status, headers, config){
													$(".underlay").hide();
								        			  if(status == constants.FORBIDDEN){
								        				location.href = 'login.html';
								        			  }else{  			  
								        				$state.transitionTo("ErrorPage",{statusvalue  : status});
								        			  }
								        		  });
											},
											function() {
												$scope.reason = 'You cancelled the dialog.';
											});
							}
						}
						function DialogController($scope,$mdDialog ) {
							$scope.hide = function() {
								$mdDialog.hide();
							};

							$scope.cancel = function() {
								$mdDialog.cancel();
							};

							$scope.answer = function(answer) {
								if(answer!=undefined){
									 $mdDialog.hide(answer);
									 
								 }else{
									 $scope.msg = "please give reason."
								 }
							};

						}
						
						$scope.updateStatus = function(){
							
							var updateStatus={
									candidateId : $stateParams.candidateId,
									status : $scope.candidateData.status
							}
						var response = $http.post("searchResume/candidateStatusUpdate", updateStatus);
							response.success(function(data,status,headers,config) {
								$scope.candidateData.reason =data.reason;
							});
							response.error(function(data, status, headers, config){
			        			  if(status == constants.FORBIDDEN){
			        				location.href = 'login.html';
			        			  }else{  			  
			        				$state.transitionTo("ErrorPage",{statusvalue  : status});
			        			  }
			        		  });
							
							$scope.candidateData.reason ="stsatu";
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

						var candidateEditData = $rootScope.candidateEditData
								|| {};

						$scope.editFirstTab = function() {
							$scope.tabs[1].active = true;
						}

						
						$scope.hotlistfocus = function()
						{
						var blocklist = $("#blocklist");
						$(blocklist).attr('disabled',true);
						}
						
						$scope.hotlistblur = function()
						{
						var blocklist = $("#blocklist");
						var hotlistcont = $("#hotlist").val();
						if(hotlistcont !== "")
							{
							$(blocklist).attr('disabled',true);
							}
						else
							{
							$(blocklist).removeAttr('disabled');
							}
						}
						
						
						$scope.blocklistfocus = function()
						{
						var hotlist = $("#hotlist");
						$(hotlist).attr('disabled',true);
						}
						
						$scope.blocklistblur = function()
						{
						var hotlist = $("#hotlist");
						var blocklistcont = $("#blocklist").val();
						if(blocklistcont !== "")
							{
							$(hotlist).attr('disabled',true);
							}
						else
							{
							$(hotlist).removeAttr('disabled');
							}
						}

						$scope.joborder = function()
						{
							$("#res").css("display", "none");
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
					
						$scope.searchToggle = function(){
							$state.transitionTo("SearchModule",{searchToggle : true});
						}
						
						$scope.addreset01 = function()
						{
							$scope.candidate.firstname = "";
							$scope.candidate.lastname = "";
							$scope.candidate.email = "";
							$scope.candidate.status = "";
							$scope.candidate.reason = "";
							$scope.candidate.phoneWork = "";
							$scope.candidate.phoneCell = "";
							$scope.candidate.address1 = "";
							$scope.candidate.address2 = "";
							$scope.candidate.city = "";
							$scope.candidate.state = "";
							$scope.candidate.zip = "";
							$scope.candidate.visaType = "";
							$scope.candidate.visaExpiryDate = "";
							$scope.AddCandidateForm.$setPristine()
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
							$scope.AddCandidateForm.$setPristine()
						}
						
						$scope.addreset03 = function()
						{
							$(".lockbtn").hide();;
							$(".uploadbtn").show();
							$scope.candidate.uploadedBy = '';
							/*$("select[ng-model='candidate.uploadedBy']").prop('selectedIndex',0);*/
							$("select[ng-model='candidate.portalEmail']").prop('selectedIndex',0);
							$("select[ng-model='candidate.atsUserId']").prop('selectedIndex',0);
							$scope.originalResume = "";
							$scope.cgiResume = "";
							$scope.rtrResume = "";
							$scope.AddCandidateForm.$setPristine()
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