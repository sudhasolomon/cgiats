;(function(angular){
	"use strict";
	
	angular.module('createIndiaJobOrders',['ngMaterial', 'ngMessages', 'ui.bootstrap', 'ngStorage'])
	
	.service("resumesView",function($mdDialog, $mdMedia, $http){
		
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
					 		templateUrl : 'views/dialogbox/joborderdialogbox.html',
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
	
	.controller("createIndiaJobOrdersController",function($scope,$http,$timeout,$state,$rootScope,$stateParams,resumesView,$window,$location, dateRangeService){
		$scope.assignedUserIdlist = {};
    	$scope.indiastates = indiaStates;
    	
    	
    	$scope.client = [];
    	$scope.popclient = [];
    	
    	
    	$scope.onload = function(){
			$scope.createIndiaJobOrdersFields = {};
			$scope.jobOrderFieldList = [];
			
			
			
			$timeout(function() {
				$(".caret").addClass("fa fa-caret-down");
				$(".caret").css("font-size", "13px");
				$(".caret").css("color", "#555555");
				 }, 100);
			   
			$scope.getclientnames();
			
			
			
			
			if($location.search().currentLoginUserId){
				 $timeout(function() {//wait for some time to redirect to another page
					 $scope.onloadFun();
				 }, 400);
				}else{
					$scope.onloadFun();
				}
    	}
    	
    		var response = $http.get("India_JobOrder/getIndiaClients");
    		response.success(function(data, config, headers, status){
    			$scope.indiaClients = data;
    		});
    		response.error(function(data, config, headers, status){
    			//alert("error "+ data);
    		});
			
			$scope.onloadFun = function(){
			 								
				if($stateParams.jobOrderId != undefined && $stateParams.jobOrderId !=null){
					$(".underlay").show();
					 $scope.createSubmittalURL="#/india_submitals/create_indiasubmitals?jobOrder="+$stateParams.jobOrderId+"&pageName="+constants.MYINDIAJOBORDERS;
					 
						var jobOrderId = $stateParams.jobOrderId;
						var response = $http.get('India_JobOrder/getIndiaJobOrderById/'+jobOrderId);
						response.success(function (data,status,headers,config){
							if(data.endDate!=null){
								data.endDate = dateRangeService.convertStringToDate_india(data.endDate);
							}
							$scope.createIndiaJobOrdersFields = data; 
							$scope.jobOrderStatus = data.status;
							$rootScope.statusInEdit = $scope.createIndiaJobOrdersFields.status;
							
							
							
							$scope.client = {id: data.customer, label: data.customer};
							
							var minsal = data.salary;
							var maxsal = data.maxSal;
							var expminsal = data.minExp;
							var expmaxsal = data.maxExp;
							
							if(minsal == "" || minsal == undefined || minsal == null)
							{
								$scope.createIndiaJobOrdersFields.salary = data.salary;
							}
							else
							{
								minsal = minsal.toString().replace(/\D/g,'');
								if(minsal.length <= 3)
									{
									$scope.createIndiaJobOrdersFields.salary = data.salary;
									}
								else
									{
									var minsallastthree = minsal.substring(minsal.length-3);
									minsal = minsal.substring(0,minsal.length-3);
									minsal = minsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + minsallastthree;
									minsal = minsal;
									$scope.createIndiaJobOrdersFields.salary = minsal;
									}
								
							}
							
							
							
							
							if(maxsal == "" || maxsal == undefined || maxsal == null)
							{
								$scope.createIndiaJobOrdersFields.maxSal = data.maxSal;
							}
							else
							{
								maxsal = maxsal.toString().replace(/\D/g,'');
								if(maxsal.length <= 3)
									{
									$scope.createIndiaJobOrdersFields.maxSal = data.maxSal;
									}
								else
									{
									var maxsallastthree = maxsal.substring(maxsal.length-3);
									maxsal = maxsal.substring(0,maxsal.length-3);
									maxsal = maxsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + maxsallastthree;
									maxsal = maxsal;
									$scope.createIndiaJobOrdersFields.maxSal = maxsal;
									}
								
							}
							
							
							
							
							
							if(expminsal == "" || expminsal == undefined || expminsal == null)
							{
								$scope.createIndiaJobOrdersFields.minExp = data.minExp;
							}
							else
							{
								expminsal = expminsal.toString().replace(/\D/g,'');
								if(expminsal.length <= 3)
									{
									$scope.createIndiaJobOrdersFields.minExp = data.minExp;
									}
								else
									{
									var expminsallastthree = expminsal.substring(expminsal.length-3);
									expminsal = expminsal.substring(0,expminsal.length-3);
									expminsal = expminsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + expminsallastthree;
									expminsal = expminsal;
									$scope.createIndiaJobOrdersFields.minExp = expminsal;
									}
								
							}
							
							
							
							
							if(expmaxsal == "" || expmaxsal == undefined || expmaxsal == null)
							{
								$scope.createIndiaJobOrdersFields.maxExp = data.maxExp;
							}
							else
							{
								expmaxsal = expmaxsal.toString().replace(/\D/g,'');
								if(expmaxsal.length <= 3)
									{
									$scope.createIndiaJobOrdersFields.maxExp = data.maxExp;
									}
								else
									{
									var expmaxsallastthree = expmaxsal.substring(expmaxsal.length-3);
									expmaxsal = expmaxsal.substring(0,expmaxsal.length-3);
									expmaxsal = expmaxsal.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",") + ',' + expmaxsallastthree;
									expmaxsal = expmaxsal;
									$scope.createIndiaJobOrdersFields.maxExp = expmaxsal;
									}
								
							}
							
							
							
							
							
							var locationval = data.city;
							if(locationval == "" || locationval == null || locationval == undefined)
								{
								var inputcheck = $("#myjoblocation").siblings(".ddlist").children("li").children("input");
								inputcheck.each(function(){
									
									$(this).removeAttr("checked");
								});
								$("#myjoblocation").val("");
								$("#myjoblocation").siblings(".blankmsg").show();
								}
							else
								{
								var elementarry = locationval.split(", ");
								var inputcheck = $("#myjoblocation").siblings(".ddlist").children("li").children("input");
								inputcheck.each(function(){
									
									$(this).removeAttr("checked");
								})
								for(var i=0; i<elementarry .length; i++)
									{
									inputcheck.each(function(){
										
										if($(this).val() == elementarry [i])
											{
											$(this).prop("checked", true);
											}
									})
									}
								$("#myjoblocation").val(locationval);
								$("#myjoblocation").siblings(".blankmsg").hide();
								
								}
							
							
							
							
							
							
							
							
							var originalcheckfile = $scope.createIndiaJobOrdersFields.strAttachment;
							if( originalcheckfile)
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
							$(".underlay").hide();
							
						});
						response.error(function(data, status, headers, config){
		        			  if(status == constants.FORBIDDEN){
		        				location.href = 'login.html';
		        			  }else{  			  
		        				$state.transitionTo("ErrorPage",{statusvalue  : status});
		        			  }
		        		  });
						 }else{
							 $rootScope.statusInEdit=null;
				$scope.createIndiaJobOrdersFields ={}; 
						 }
					$scope.getAllUsers();
					$scope.getAllDMs();
					
					$scope.DateFormat = 'dd-MM-yyyy';
					
					$scope.visaDateOptions = {
							date : new Date(),
							showWeeks : false
						};
					$scope.ConStartDateOptions = {
							date : new Date(),
							showWeeks : false
						};
					$scope.ConEndDateOptions = {
							date : new Date(),
							showWeeks : false
						};
					
					
					$scope.visaDateopen = function() {
						$scope.visaDatePopup.opened = true;
					};
					$scope.ConStartDateopen = function() {
						$scope.ConStartDatePopup.opened = true;
					}
					$scope.ConEndDateopen = function() {
						$scope.ConEndDatePopup.opened = true;
					};
					
					
					$scope.visaDatePopup = {
							opened : false
						};
					$scope.ConStartDatePopup = {
							opened : false
						};
					$scope.ConEndDatePopup = {
							opened : false
						};
					
					$scope.candidate={
							visaExpiryDate : new Date(),
					      }
			}
			
			$scope.getAllDMs = function(){
				var response = $http.get('IndiaCommonController/getAllDMs');
				response.success(function (data,status,headers,config){
					$scope.dmList = data;
				});
				response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
			};
	
			
			
			$scope.getAllUsers = function(){
				var response = $http.get('IndiaCommonController/getIndiaAllUsers');
				response.success(function (data,status,headers,config){
					$scope.assignedUserIdlist = data;
				});
				response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
			};
			
			$scope.setFile = function(element) {
				$scope
						.$apply(function($scope) {
							$scope.attachment = element.files[0];
						});
				$.growl.success({title : "Info !", message : "Attachment Uploaded Successfully"});
			};
				
				
				
				$scope.createIndiaJobOrder = function(){
					//alert( $("#myjoblocation").val());
					
					$scope.createIndiaJobOrdersFields.city = $("#myjoblocation").val();
					//alert(JSON.stringify($scope.createIndiaJobOrdersFields));
					
				 				
				
				
				$scope.isErrorMsg = false;
				$scope.statusValidationErrMsg = false;
				
				
				if($("input[ng-model='createIndiaJobOrdersFields.salary']").val())
				{
				$scope.createIndiaJobOrdersFields.salary = $("input[ng-model='createIndiaJobOrdersFields.salary']").val().replace(/\D/g,'');
				}
			


			if($("input[ng-model='createIndiaJobOrdersFields.maxSal']").val())
				{
				$scope.createIndiaJobOrdersFields.maxSal = $("input[ng-model='createIndiaJobOrdersFields.maxSal']").val().replace(/\D/g,'');
				}

				
				if($scope.createIndiaJobOrdersFields.numOfPos == 0){
					$scope.isErrorMsg = true;
					$scope.errMsg = "Number of Positions shouldn't be zero";
					return;
				}
				
				if($rootScope.statusInEdit){
				if($rootScope.statusInEdit!="CLOSED" && $rootScope.statusInEdit!="REOPEN" && $scope.createIndiaJobOrdersFields.status == "REOPEN"){
					$scope.isErrorMsg = true;
					$scope.errMsg = "Status must be closed to change status to Reopen";
					return;
				}
				}
				else{
				if(!($scope.createIndiaJobOrdersFields.status === constants.OPEN || $scope.createIndiaJobOrdersFields.status === constants.ASSIGNED 
						|| $scope.createIndiaJobOrdersFields.status === constants.HOLD)){
					$scope.statusValidationErrMsg = true;
					return;
				}
				}
				if($scope.createIndiaJobOrdersFields.jobType === constants.CONTRACT){
				var startDate = new Date($scope.createIndiaJobOrdersFields.startDate);
				var endDate = new Date($scope.createIndiaJobOrdersFields.endDate);
				if(startDate > endDate){
					$scope.isErrorMsg = true;
					$scope.errMsg = "End date must be greater than start date";
					return;
				}
				}
				
				if($scope.client.id == "" || $scope.client.id == undefined || $scope.client.id == null)
				{
				
				}
			else
				{
				//alert("CLIENT NAME: " + $scope.client.id);
				saveOrUpdateIndiaJoborder();
				}
				
				
					
				}
		
				
				function saveOrUpdateIndiaJoborder(){
					
					//alert(JSON.stringify($scope.createIndiaJobOrdersFields));
					var flagValue = 0;
					$scope.fieldList = [];
					var i=0;
					angular.forEach($scope.jobOrderFieldList, function(obj, key){
						var field={};
						if(obj.fieldName){
						field.fieldName = obj.fieldName;
						field.fieldValue = obj.fieldValue;
						field.visible=obj.visible;
						$scope.fieldList[i++]=field;
						}else{
							obj.message = "Required";
							flagValue++;
						}
					});
//					alert(JSON.stringify($scope.jobOrderFieldList));
					if(flagValue==0){
					$(".underlay").show();
					$("#res").css("display", "none");
					$scope.createIndiaJobOrdersFields.jobOrderFieldList= $scope.fieldList;
					if($scope.createIndiaJobOrdersFields.startDate){
					$scope.createIndiaJobOrdersFields.startDate = formatteddate($scope.createIndiaJobOrdersFields.startDate);
					}
					if($scope.createIndiaJobOrdersFields.endDate){
					$scope.createIndiaJobOrdersFields.endDate = formatteddate($scope.createIndiaJobOrdersFields.endDate);
					}
					
					if($scope.createIndiaJobOrdersFields.jobType == "Contract"){
						
					}
					$scope.createIndiaJobOrdersFields.customer = $scope.client.id;
					var formData = new FormData();
					formData.append('addEditJobOrderDto', angular
							.toJson($scope.createIndiaJobOrdersFields));
					var attachedFile = $scope.attachment;
					formData.append('file', attachedFile);
					var response = $http.post('India_JobOrder/saveOrUpdateIndiaJobOrder', formData, {transformRequest : angular.identity,headers : {'Content-Type' : undefined}});
					response.success(function (data,status,headers,config){
						$(".underlay").hide();
						$rootScope.statusInEdit=null;
						 if($stateParams.jobOrderId != undefined && $stateParams.jobOrderId !=null){
							$.growl.success({title : "Info !", message : "India Job Order Updated Successfully"});
						}
						else{
							$.growl.success({title : "Info !", message : "India Job Order Created Successfully"});
						}
						
						 $timeout(function() {//wait for some time to redirect to another page
							 $rootScope.jobOrderInserted = true;
								 $state.go("myIndiaJobOrders");
			        		}, 200);
						
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
				
		
				function formatteddate(inputData){
					var expDate ='';
					if (inputData instanceof Date){
						  expDate = new Date(inputData);
					}else{
						var from = inputData.split("-");
				     	 expDate = new Date(from[2], from[1] - 1, from[0]);
					}
			     	 var month = '' + (expDate.getMonth() + 1);
			          var day = '' + expDate.getDate();
			         var  year = expDate.getFullYear();
			     	  if (month.length < 2) month = '0' + month;
			     	    if (day.length < 2) day = '0' + day;
			     	   return [(day),month,year].join('-');
			       };
			       
			       $scope.createjobordervalidate = function()
					{
			    	   
			    	   var minsalary = $scope.createIndiaJobOrdersFields.salary;
			    	   var maxsalary = $scope.createIndiaJobOrdersFields.maxSal;
			    	   var expminsalary = $scope.createIndiaJobOrdersFields.minExp;
			    	   var expmaxsalary = $scope.createIndiaJobOrdersFields.maxExp;
			    	   
			    	   
			    	   
			    	   if(minsalary == "" || minsalary == undefined || minsalary == null)
		    		   {
		    		   }
		    	   else
		    		   {
		    		   		$scope.createIndiaJobOrdersFields.salary = minsalary.replace(/\D/g,'');
		    		   }
			    	   
			    	   if(maxsalary == "" || maxsalary == undefined || maxsalary == null)
		    		   {
		    		   }
		    	   else
		    		   {
		    		   $scope.createIndiaJobOrdersFields.maxSal = maxsalary.replace(/\D/g,'');
		    		   }
			    	   
			    	   
			    	   
			    	   if(expminsalary == "" || expminsalary == undefined || expminsalary == null)
		    		   {
		    		   }
		    	   else
		    		   {
		    		   $scope.createIndiaJobOrdersFields.minExp = expminsalary.replace(/\D/g,'');
		    		   }
			    	   
			    	   
			    	   
			    	   if(expmaxsalary == "" || expmaxsalary == undefined || expmaxsalary == null)
		    		   {
		    		   }
		    	   else
		    		   {
		    		   $scope.createIndiaJobOrdersFields.maxExp = expmaxsalary.replace(/\D/g,'');
		    		   }
			    	   
			    	   
			    	   
			    	   
			    	   setTimeout(function() 
								  {
							//alert($scope.client.id);
							var mandfeild = $("form").find(".error-msg");
							if(mandfeild .length > 0)
								{


								if($scope.client.id == "" || $scope.client.id == undefined || $scope.client.id == null)
								{
								$("#res").css("display", "block");
								$(".formddlist .dropdown-toggle").css("border-color", "#ff0000");
								$(".clientname .mustfield").show();
								}
							else
								{
								$(".formddlist .dropdown-toggle").css("border-color", "#27a4b0");
								$(".clientname .mustfield").hide();
								$("#res").css("display", "block");
								}
								
								
								
								}
							else
								{


								if($scope.client.id == "" || $scope.client.id == undefined || $scope.client.id == null)
								{
								$("#res").css("display", "block");
								$(".formddlist .dropdown-toggle").css("border-color", "#ff0000");
								$(".clientname .mustfield").show();
								}
							else
								{
								$(".formddlist .dropdown-toggle").css("border-color", "#27a4b0");
								$(".clientname .mustfield").hide();
								$("#res").css("display", "none");
								}
								
								
								}
								  }, 5);
					}
			   
	 
				
				$scope.tabs = [ {
					type : 'tab1',
					active : true
				},   {
					type : 'tab2'
				} ];

				$scope.firstTab = function() {
					$scope.tabs[1].active = true;
				}

				$scope.candidateResettab1 = function()
				{
					$scope.createIndiaJobOrdersFields.onlineFlag = "Yes";
					$scope. createIndiaJobOrdersFields.companyFlag = "";
					$scope.createIndiaJobOrdersFields.priority = "MEDIUM";
					$scope.createIndiaJobOrdersFields.customer = "";
					$scope.createIndiaJobOrdersFields.customerHidden ="";
					$scope.createIndiaJobOrdersFields.status = "OPEN";
					$scope.createIndiaJobOrdersFields.city = "";
					$scope.createIndiaJobOrdersFields.state = "";
					$scope.createIndiaJobOrdersFields.assignedTo = "";
					$scope.createIndiaJobOrdersFields.numOfPos = "";
					$scope.createIndiaJobOrdersFields.category = "";
					$scope.createIndiaJobOrdersFields.emName = "";
					$scope.createIndiaJobOrdersFields.title = "";
					$scope.createIndiaJobOrdersFields.keySkills = "";
					$scope.createIndiaJobOrderForm.$setPristine()
					
				}
				
		 
				
				$scope.candidateResettab3 = function()
				{
//					alert('');
					$(".lockbtn").hide();;
					$(".uploadbtn").show();
					$scope.createIndiaJobOrdersFields.description = "";
					$scope.fileAttachment = "";
					$("#permanentoptions").css("display","block");
					$("#contractoptions").css("display","none");
					$scope.createIndiaJobOrdersFields.jobType = "PERMANENT";
					$scope.createIndiaJobOrdersFields.salary = "";
//					$scope.createIndiaJobOrdersFields.permFee = "";
					$scope.createIndiaJobOrdersFields.startDate = "";
					$scope.createIndiaJobOrdersFields.annualRateW2 = "";
					$scope.createIndiaJobOrdersFields.startDate = "";
					$scope.createIndiaJobOrdersFields.endDate = "";
					$scope.createIndiaJobOrderForm.$setPristine()
				}
				
				$scope.originalResumeView = function(element){
					if($scope.attachment != null){
						resumesView.getResumeContent($scope.attachment).then(function(response){
							resumesView.viewResume(element, response.data.content);
						});
					}else{
						resumesView.viewResume(element, $scope.createIndiaJobOrdersFields.strAttachment);
						}
				}
				$scope.originlck = function()
				{
					$scope.origuploadbtn = true;
				}
				
				$scope.downloadOriginalResume = function(){
					if($scope.attachment != null){
						resumesView.getResumeContent($scope.attachment).then(function(response){
							resumesView.downloadResume(response.data.content, 'Download', 'IndiaJobOrder');
						});
					}else{
						 if($stateParams.jobOrderId != undefined && $stateParams.jobOrderId !=null){
						$window.location = 'India_JobOrder/downloadJobOrderAttachment/'+$stateParams.jobOrderId;
						 }
					}
				}
	 
			$scope.clrval = function()
			{
//				$scope.createIndiaJobOrdersFields.salary = "";
			/*	$scope.createIndiaJobOrdersFields.permFee = "";
				$scope.createIndiaJobOrdersFields.hourlyRateW2 = "";
				$scope.createIndiaJobOrdersFields.annualRateW2 = "";
				$scope.createIndiaJobOrdersFields.hourlyRate1099 = "";
				$scope.createIndiaJobOrdersFields.hourlyRateC2c = "";
				$scope.cw2 = false;
				$scope.c1099 = false;
				$scope.cc2c = false;*/
				}
	
	
	
	
	
	
	
			
			
			
			
			$scope.addeditclient = function()
			{
			$("#addeditclientpop").show();
			$("#samenameerr").hide();
			$("#nameexistserr").hide();
			$scope.selectclientstatus = "";
			$scope.showfield = "no";
			$scope.AddEditClient.$setPristine()
			$scope.clientData.addnewclientname = "";
			$scope.clientData.newclientname = "";
		}
		
		$("#popclosebtn").click(function(){
			$scope.clientdata = $scope.clientList;
			$(this).parent(".popuphead").parent(".popupcont").parent(".undermask").hide();
		});
		
		
		$scope.changepopdd = function()
		{
			if($scope.selectclientstatus == "Add a New Client")
			{
			$scope.showfield = "no";
			}
		}
		
		$scope.addEditClientDB = function()
		{
			if($scope.selectclientstatus == "Add a New Client")
				{
				$scope.showfield = "no";
				$scope.addnewclientfun();
					
				}
			if($scope.selectclientstatus == "Edit an Existing Client")
				{
				if($scope.popclient.id == "" || $scope.popclient.id == undefined || $scope.popclient.id == null)
					{
					$(".formddlistpop .dropdown-toggle").css("border-color", "#ff0000");
					$(".clientnamepop .mustfield").show();
					}
				else
					{
					$(".formddlistpop .dropdown-toggle").css("border-color", "#27a4b0");
					$(".clientnamepop .mustfield").hide();
					$scope.editnewclientfun();
					}
				}
		}
		
		
		
		
		$scope.addnewclientfun = function()
		{
			$scope.addnewclientname = $(".addclientauto .angucomplete-holder #clientId_value").val();
			$scope.samenameinDB = "no";
			for(var i=0; i<$scope.popclientList.length; i++)
				{
					if($scope.addnewclientname.trim() == $scope.popclientList[i].id.trim())
						{
						$scope.samenameinDB = "yes";
						}
					else
						{
						
						}
				}
			
			
			if($scope.samenameinDB != "yes")
				{
				$("#nameexistserr").hide();
					//var obj = {newclientname:$scope.addnewclientname};
					//alert("ADD FUNCTION" + JSON.stringify(obj));
					$scope.addClientObj = {
							"clientName": $scope.addnewclientname
					}
							var response = $http.post('India_JobOrder/saveClient',$scope.addClientObj);
							 response.success(function (data,status,headers,config){
								 $("#addeditclientpop").hide();
									$("#client_success").slideDown();
									$timeout(function() {
										$("#client_success").slideUp();
									 	}, 5000);
									$scope.getclientnames();
							   });
							   response.error(function(data, status, headers, config){
							       if(status == constants.FORBIDDEN){
							      location.href = 'login.html';
							       }else{       
							      $state.transitionTo("ErrorPage",{statusvalue  : status});
							       }
							      });
				}
			else
				{
				$("#nameexistserr").show();
				}
			
			
		}
		
		$scope.editnewclientfun = function()
		{
			$scope.existingname = $scope.client.id;
			$scope.changename = $scope.popclient.id
			$scope.editnewclientname = $(".editclientauto .angucomplete-holder #clientId_value").val();
			if($scope.popclient.id.trim() == $scope.editnewclientname.trim())
				{
				$("#samenameerr").show();
				}
			else
				{
				$("#samenameerr").hide();
				//var obj = {oldclientname:$scope.popclient.id, newclientname:$scope.editnewclientname};
				//alert("EDIT FUNCTION" + JSON.stringify(obj));
				$scope.editClientObj = {
						"oldClientName" : $scope.popclient.id,
						"newClientName" : $scope.editnewclientname
				}
						var response = $http.post('India_JobOrder/updateClient',$scope.editClientObj);
						   response.success(function (data,status,headers,config){
								$("#addeditclientpop").hide();
								$("#client_success").slideDown();
								$timeout(function() {
									$("#client_success").slideUp();
									 }, 5000);
								
								if($scope.existingname == $scope.changename)
								{
								$scope.client = {id: $scope.editnewclientname, label: $scope.editnewclientname};
								}
								
								$scope.getclientnames();
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
		
		
		$scope.clientevent = {
				onItemSelect: function(item) {
					$(".formddlist .dropdown-toggle").css("border-color", "#27a4b0");
					$(".clientname .mustfield").hide();
				},
		onDeselectAll: function(item) {
			$(".formddlist .dropdown-toggle").css("border-color", "#ff0000");
			$(".clientname .mustfield").show();
		}
	               
	    };
		
		
		
		
		
		$scope.popclientevent = {
				onItemSelect: function(item) {
					$(".formddlistpop .dropdown-toggle").css("border-color", "#27a4b0");
					$(".clientnamepop .mustfield").hide();
					$scope.showfield = "yes";
					
					$scope.newclientname = $scope.popclient.id;
					$timeout(function() {
						
						var obj = {id: $scope.newclientname, label: $scope.newclientname};
						  $scope.$broadcast('angucomplete-alt:changeInput', 'clientId', obj);

						 }, 200);
					
					
					
				},
		onDeselectAll: function(item) {
			$(".formddlistpop .dropdown-toggle").css("border-color", "#ff0000");
			$(".clientnamepop .mustfield").show();
			$scope.showfield = "no";
		}
	               
	    };
		
		
		
		
		
	    $scope.selectedClientIdAction = function(selected) {
	        if (selected && selected.title) {
	        	$scope.selectedClient = selected.title;
	        } else {
	          console.log('cleared');
	        }
	      };
	      
	      

	      
	      $scope.getclientnames = function()
	      {
	    	  var response = $http.get('IndiaCommonController/getAllClients');
			   response.success(function (data,status,headers,config){
				   $scope.clientList = [];
				   $scope.popclientList = [];
				   for(var i=0; i<data.length; i++ )
					{
						var obj = {id: data[i], label: data[i]};
						$scope.clientList.push(obj);
						$scope.popclientList.push(obj);
					}
				
				   $scope.clientdata = $scope.clientList;
				   $scope.popclientdata = $scope.popclientList;
				
				
			   });
			   response.error(function(data, status, headers, config){
			       if(status == constants.FORBIDDEN){
			      location.href = 'login.html';
			       }else{       
			      $state.transitionTo("ErrorPage",{statusvalue  : status});
			       }
			      });
	      }
	      
			
			
			
	      

	  	$scope.onesearchsettings = {
	  		    selectionLimit: 1,
	  		    enableSearch: true,
	  		    smartButtonMaxItems: 1,
	  		             smartButtonTextConverter: function(itemText, originalItem) {
	  		                 return itemText;
	  		             },
	  		         };
			
			
			
	
	
	
	
	});
})(angular);