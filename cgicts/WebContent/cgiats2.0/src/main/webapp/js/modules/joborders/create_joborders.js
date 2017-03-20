;(function(angular){
	"use strict";
	
	angular.module('createJobOrders',['ngMaterial', 'ngMessages', 'ui.bootstrap', 'ngStorage',
	      							'angular-highlight', 'jcs-autoValidate'])
	
	
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
	
    .controller("createJobOrdersController",function($scope,$http,$timeout,$state,$rootScope,$stateParams,resumesView,$window,$location,dateRangeService
    		,$mdDialog, $mdMedia){
    	
    	$scope.clientData = {};
    	$scope.assignedUserIdlist = {};
    	$scope.usstates = usStates;
    	$scope.client = [];
    	$scope.popclient = [];
		$scope.onload = function(){
			$scope.createJobOrdersFields = {};
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
	
//		function convertStringToDate(strDate){
//			var pattern = /(\d{2})\-(\d{2})\-(\d{4})/;
//			var dateArray = pattern.exec(strDate); 
//			var dateObject = new Date(
//				    (+dateArray[3]),
//				    (+dateArray[1])-1, // Careful, month starts at 0!
//				    (+dateArray[2])
//				);
//			return dateObject;
//		}
		
		
		$scope.onloadFun = function(){
			
			if($stateParams.jobOrderId != undefined && $stateParams.jobOrderId !=null){
				$(".underlay").show();
				 $scope.createSubmittalURL="#/submitals/createsubmitals?jobOrder="+$stateParams.jobOrderId+"&pageName="+constants.MYJOBORDERS;
				 
					var jobOrderId = $stateParams.jobOrderId;
					var response = $http.get('jobOrder/getJobOrderById/'+jobOrderId);
					response.success(function (data,status,headers,config){
						$scope.createJobOrdersFields = data; 
						$scope.jobOrderStatus = data.status;
						
						$scope.selectedClient = data.title;
						//alert("NAME: " + data.customer);
						$scope.client = {id: data.customer, label: data.customer};
						
						$scope.fileAttachment = data.attachmentFileName;
						if($scope.createJobOrdersFields.jobOrderFieldList){
						$scope.jobOrderFieldList  = $scope.createJobOrdersFields.jobOrderFieldList;
						}
						$rootScope.statusInEdit = $scope.createJobOrdersFields.status;
						
						
						 
							if($scope.createJobOrdersFields.startDate){
								$scope.createJobOrdersFields.startDate = dateRangeService.convertStringToDate($scope.createJobOrdersFields.startDate);
							}
							
							if($scope.createJobOrdersFields.endDate){
								$scope.createJobOrdersFields.endDate = dateRangeService.convertStringToDate($scope.createJobOrdersFields.endDate);
							}
						
						var presal = data.salary;
						var perfee = data.permFee;
						var w2horly = data.hourlyRateW2;
						var w2horlymax = data.hourlyRateW2max;
						var w2annual = data.annualRateW2;
						var conhorly = data.hourlyRate1099;
						var cschorly = data.hourlyRateC2c;
						var cschorlymax = data.hourlyRateC2cmax;

						$scope.createJobOrdersFields.dmName = $stateParams.dmName;
						
						if(presal)
						{
						presal = String(presal).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.salary = '$ ' + presal;
						}
						
						if(perfee)
						{
						perfee = String(perfee).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.permFee = perfee + ' %';
						}

						if(w2horly)
						{
						w2horly = String(w2horly).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.hourlyRateW2 = '$ ' + w2horly;
						}
						
						
						
						
						if(w2horlymax)
						{
						w2horlymax = String(w2horlymax).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.hourlyRateW2max = '$ ' + w2horlymax;
						}
						
						
						
						
						
						if(w2annual)
						{
						w2annual = String(w2annual).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.annualRateW2 = '$ ' + w2annual;
						}
						
						
						if(conhorly)
						{
						conhorly = String(conhorly).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.hourlyRate1099 = '$ ' + conhorly;
						}
						
						
						if(cschorly)
						{
						cschorly = String(cschorly).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.hourlyRateC2c = '$ ' + cschorly;
						}
						
						
						if(cschorlymax)
						{
						cschorlymax = String(cschorlymax).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
						$scope.createJobOrdersFields.hourlyRateC2cmax = '$ ' + cschorlymax;
						}
						
						
						
						
						
						
						
						
						var originalcheckfile = $scope.createJobOrdersFields.strAttachment;
						if(!originalcheckfile)
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
						 $scope.createJobOrdersFields ={}; 
						 $scope.createJobOrdersFields.acceptW2 = false;
						 $scope.createJobOrdersFields.acceptC2c = false;
						 $scope.jobOrderFieldList = [{"id":Math.floor((Math.random() * -10000) + 1),"fieldName":"WORK_STATUS","fieldValue":"","isCollapse":false,"visible":true},{"id":Math.floor((Math.random() * -10000) + 1),"fieldName":"BILL_RATE","fieldValue":"","isCollapse":false,"visible":true}];
					 }
			
			$scope.getAllUsers();
			$scope.getAllEMs();
			$scope.getAllDMs();
			
			$scope.DateFormat = 'MM-dd-yyyy';
			
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
		
		$scope.getAllUsers = function(){
			var response = $http.get('commonController/getAllRecruiters');
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
		
		$scope.getAllEMs = function(){
			var response = $http.get('commonController/getAllEMs');
			response.success(function (data,status,headers,config){
				$scope.emList = data;
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
		};
		
		
		
		
		$scope.getAllDMs = function(){
			var response = $http.get('commonController/getAllDMs');
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
		
		
		
	
		$scope.editField = function(editablaObj){
			var i=0;
			$scope.prevEditFieldObj = {
					"fieldName":editablaObj.fieldName?editablaObj.fieldName:'',
					"fieldValue":editablaObj.fieldValue?editablaObj.fieldValue:'',
					"visible":editablaObj.visible?editablaObj.visible:false,
					"isCollapse": editablaObj.isCollapse?editablaObj.isCollapse:false
					
			};
			angular.forEach($scope.jobOrderFieldList, function(obj, key){
				if(obj.fieldName == editablaObj.fieldName && obj.fieldValue == editablaObj.fieldValue && obj.visible == editablaObj.visible){
					obj.isCollapse = true;
				}
				i++;
			});
		};
		
		
		$scope.addFieldValue = function(editablaObj){
			if(!editablaObj.fieldName){
				editablaObj.message = "Required";
			}else{
				editablaObj.message = null;
				editablaObj.isCollapse = !editablaObj.isCollapse;
			}
			/*var i=0;
			$scope.prevEditFieldObj = {
					"fieldName":editablaObj.fieldName?editablaObj.fieldName:'',
					"fieldValue":editablaObj.fieldValue?editablaObj.fieldValue:'',
					"visible":editablaObj.visible?editablaObj.visible:false,
					"isCollapse": editablaObj.isCollapse?editablaObj.isCollapse:false
					
			};
			angular.forEach($scope.jobOrderFieldList, function(obj, key){
				if(obj.fieldName == editablaObj.fieldName && obj.fieldValue == editablaObj.fieldValue && obj.visible == editablaObj.visible){
					obj.isCollapse = true;
				}
				i++;
			});*/
		};
		
		$scope.undoChange = function(editablaObj){
			var i=0;
			if(!$scope.prevEditFieldObj.fieldName){
				$scope.deleteField(editablaObj);
			}else{
			angular.forEach($scope.jobOrderFieldList, function(obj, key){
				if(obj.fieldName == editablaObj.fieldName && obj.fieldValue == editablaObj.fieldValue && obj.visible == editablaObj.visible){
					obj.fieldName  = $scope.prevEditFieldObj.fieldName;
					obj.fieldValue  = $scope.prevEditFieldObj.fieldValue;
					obj.visible = $scope.prevEditFieldObj.visible;
					obj.message = null;
					obj.isCollapse = false;
				}
				i++;
			});
			}
		};
		
		 
			$scope.addField = function(){
				var flagValue=0;
				angular.forEach($scope.jobOrderFieldList, function(obj, key){
					if(!obj.fieldName){
						flagValue++;
					}
				});
				if(flagValue==0){
				$scope.prevEditFieldObj = {};
				$scope.jobOrderFieldList.push({"id":Math.floor((Math.random() * -10000) + 1),"isCollapse":true,"visible":false});
				}
			};

			$scope.deleteField = function(deletedObj){
				var i=0;
				angular.forEach($scope.jobOrderFieldList, function(obj, key){
					if(obj.id == deletedObj.id){
						$scope.jobOrderFieldList.splice(i, 1);
					}
					i++;
			});
			};

		
			$scope.setFile = function(element) {
				$scope
						.$apply(function($scope) {
							$scope.originlockbtn = true;
							$scope.origindownloadbtn = true;
							$scope.attachment = element.files[0];
						});
				$.growl.success({title : "Info !", message : "Attachment Uploaded Successfully"});
			};
			
		$scope.joborder = function()
		{
			
//			alert($scope.createJobOrdersFields.status);
			
			
			if($("input[ng-model='createJobOrdersFields.salary']").val())
				{
				$scope.createJobOrdersFields.salary = $("input[ng-model='createJobOrdersFields.salary']").val().replace(/\D/g,'');
				}
			


			if($("input[ng-model='createJobOrdersFields.permFee']").val())
				{
				$scope.createJobOrdersFields.permFee = $("input[ng-model='createJobOrdersFields.permFee']").val().replace(/\D/g,'');
				}


			
			if($("input[ng-model='createJobOrdersFields.hourlyRateW2']").val())
				{
				$scope.createJobOrdersFields.hourlyRateW2 = $("input[ng-model='createJobOrdersFields.hourlyRateW2']").val().replace(/\D/g,'');
				}
			
			
			
			
			if($("input[ng-model='createJobOrdersFields.hourlyRateW2max']").val())
			{
			$scope.createJobOrdersFields.hourlyRateW2max = $("input[ng-model='createJobOrdersFields.hourlyRateW2max']").val().replace(/\D/g,'');
			}
			
			
			
			if($("input[ng-model='createJobOrdersFields.annualRateW2']").val())
				{
				$scope.createJobOrdersFields.annualRateW2 = $("input[ng-model='createJobOrdersFields.annualRateW2']").val().replace(/\D/g,'');
				}


			if($("input[ng-model='createJobOrdersFields.hourlyRate1099']").val())
				{
				$scope.createJobOrdersFields.hourlyRate1099 = $("input[ng-model='createJobOrdersFields.hourlyRate1099']").val().replace(/\D/g,'');
				}


			if($("input[ng-model='createJobOrdersFields.hourlyRateC2c']").val())
			{
			$scope.createJobOrdersFields.hourlyRateC2c = $("input[ng-model='createJobOrdersFields.hourlyRateC2c']").val().replace(/\D/g,'');
			}
			
			var c2cmax = $("input[ng-model='createJobOrdersFields.hourlyRateC2cmax']").val();
			if(c2cmax)
			{
			$scope.createJobOrdersFields.hourlyRateC2cmax = c2cmax.replace(/\D/g,'');
			}
			
			
			
			
			
//			alert($scope.createJobOrdersFields.salary + ' | ' + $scope.createJobOrdersFields.permFee + ' | ' + $scope.createJobOrdersFields.hourlyRateW2 + ' | ' + $scope.createJobOrdersFields.annualRateW2 + ' | ' + $scope.createJobOrdersFields.hourlyRate1099 + ' | ' + $scope.createJobOrdersFields.hourlyRateC2c);
			
			
			$scope.isErrorMsg = false;
			$scope.statusValidationErrMsg = false;
			
			if($scope.createJobOrdersFields.numOfPos == 0){
				$scope.isErrorMsg = true;
				$scope.errMsg = "Number of Positions shouldn't be zero";
				return;
			}
			if($rootScope.statusInEdit){
			if($rootScope.statusInEdit!="CLOSED" && $rootScope.statusInEdit!="REOPEN" && $scope.createJobOrdersFields.status == "REOPEN"){
				$scope.isErrorMsg = true;
				$scope.errMsg = "Status must be closed to change status to Reopen";
				return;
			}
			
			}else{
			/*if(!($scope.createJobOrdersFields.status === constants.OPEN || $scope.createJobOrdersFields.status === constants.ASSIGNED)){
//				alert('s');
				$scope.statusValidationErrMsg = true;
				return;
			}*/
			}
			
			
			
			if($scope.client.id == "" || $scope.client.id == undefined || $scope.client.id == null)
				{
				
				}
			else
				{
				//alert("CLIENT NAME: " + $scope.client.id);
				saveOrUpdateJoborder();
				/*if($scope.createJobOrdersFields.status){
					
					if($scope.createJobOrdersFields.status == constants.PENDING && ($stateParams.page && $stateParams.page == constants.PENDINGJOBORDERS)){
						submitConfirmationForStatus();
					}else{
						saveOrUpdateJoborder();
					}
					
				}*/
				}
			
			
			
		}
		
/*		function submitConfirmationForStatus(e){
			$mdDialog.show(	{
				controller : DialogController,
				templateUrl : 'views/dialogbox/statusconfirmationdialogbox.html',
				parent : angular
						.element(document.body),
				targetEvent : e,
				locals : {
					rowData : $scope.createJobOrdersFields.status,
				},
				clickOutsideToClose : true,
			})
		}
		
		function DialogController($scope,
				$mdDialog, rowData) {
			//$scope.row = rowData;
			$scope.status = rowData;
			$scope.hide = function() {
				$mdDialog.hide();
			};

			$scope.cancel = function() {
				$mdDialog.cancel();
			};

          $scope.pendingJobOrderStatus = function() {
        	  $mdDialog.hide();
        	  saveOrUpdateJoborder();
		    	  };

		}*/
		
		
		
		function saveOrUpdateJoborder(){
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
//			alert(JSON.stringify($scope.jobOrderFieldList));
			if(flagValue==0){
			$(".underlay").show();
			$("#res").css("display", "none");
//			alert($scope.createJobOrdersFields.hourlyRateC2cmax);
			$scope.createJobOrdersFields.jobOrderFieldList= $scope.fieldList;
			if($scope.createJobOrdersFields.startDate){
			$scope.createJobOrdersFields.startDate = formatteddate($scope.createJobOrdersFields.startDate);
			}
			if($scope.createJobOrdersFields.endDate){
			$scope.createJobOrdersFields.endDate = formatteddate($scope.createJobOrdersFields.endDate);
			}
			$scope.createJobOrdersFields.customer = $scope.client.id;
			var formData = new FormData();
			formData.append('addEditJobOrderDto', angular
					.toJson($scope.createJobOrdersFields));
			var attachedFile = $scope.attachment;
			formData.append('file', attachedFile);
			var response = $http.post('jobOrder/saveOrUpdateJobOrder', formData, {transformRequest : angular.identity,headers : {'Content-Type' : undefined}});
			response.success(function (data,status,headers,config){
				$(".underlay").hide();
				$rootScope.statusInEdit=null;
				 if($stateParams.jobOrderId != undefined && $stateParams.jobOrderId !=null){
					 /*if($stateParams.page == constants.PENDINGJOBORDERS){
						 if($scope.createJobOrdersFields.status == constants.PENDING){
							 $.growl.success({title : "Info !", message : "Job Order Updated Successfully"});
						 }else{
						 $.growl.success({title : "Info !", message : "Job Order moved to "+ $scope.createJobOrdersFields.status +" status successfully"});
						 }
					 }else{*/
					$.growl.success({title : "Info !", message : "Job Order Updated Successfully"});
//					 }
				}
				else{
					$.growl.success({title : "Info !", message : "Job Order created successfully and sent for review."});
				}
				
				 $timeout(function() {//wait for some time to redirect to another page
					 $rootScope.jobOrderInserted = true;
					 if($stateParams.page){
						 $state.go($stateParams.page);
					 }else{
						 $state.go(constants.MYJOBORDERS);
						 //$state.go("resultJobOrdersModule");
						 
					 }
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
		
		$scope.createjobordervalidate = function()
		{
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
		
		
		$scope.chkbox = function()
		{
						
			$("#contw2").change(function(event){
			    if (this.checked){
			    	$("#w2hour").removeAttr("readonly");
			    	$("#w2hourmax").removeAttr("readonly");
					$("#w2annual").removeAttr("readonly");
			    } else {
			    	$("#w2hour").attr("readonly","true");
			    	$("#w2hourmax").attr("readonly","true");
					$("#w2annual").attr("readonly","true");
					$("#w2hour").val("");
					$("#w2hourmax").val("");
					$("#w2annual").val("");
					$("#w2hour").css("border-color","#c2cad8");
					$("#w2hourmax").css("border-color","#c2cad8");
					$("#w2annual").css("border-color","#c2cad8");
			    }
			});
			
		}
		
		$scope.chkbox01 = function()
		{
					
			$("#cont1099").change(function(event){
			    if (this.checked){
			    	$("#1099hour").removeAttr("readonly"); 
			    } else {
			    	$("#1099hour").attr("readonly","true");
			    	$("#1099hour").val("");
			    	$("#1099hour").css("border-color","#c2cad8");
			    }
			});
			
		}
		
		
		$scope.chkbox02 = function()
		{
			
					
			$("#contcorp").change(function(event){
			    if (this.checked){
			    	$("#corphour").removeAttr("readonly");
			    	$("#corphourmax").removeAttr("readonly");
			    } else {
			    	$("#corphour").attr("readonly","true");
			    	$("#corphourmax").attr("readonly","true");
			    	$("#corphour").val("");
			    	$("#corphourmax").val("");
			    	$("#corphour").css("border-color","#c2cad8");
			    	$("#corphourmax").css("border-color","#c2cad8");
			    }
			});
			
		}
		
				
		
		
		
		
		
		
		
		
		
		$scope.tabs = [ {
			type : 'tab1',
			active : true
		}, {
			type : 'tab2'
		}, {
			type : 'tab3'
		} ];

		$scope.firstTab = function() {
			$scope.tabs[1].active = true;
		}

		$scope.candidateResettab1 = function()
		{
			$scope.createJobOrdersFields.onlineFlag = "Yes";
			$scope. createJobOrdersFields.companyFlag = "";
			$scope.createJobOrdersFields.priority = "MEDIUM";
			$scope.createJobOrdersFields.customer = "";
			$scope.createJobOrdersFields.customerHidden ="";
			$scope.createJobOrdersFields.status = "OPEN";
			$scope.createJobOrdersFields.city = "";
			$scope.createJobOrdersFields.state = "";
			$scope.createJobOrdersFields.assignedTo = "";
			$scope.createJobOrdersFields.numOfPos = "";
			$scope.createJobOrdersFields.category = "";
			$scope.createJobOrdersFields.emName = "";
			$scope.createJobOrdersFields.title = "";
			$scope.createJobOrdersFields.keySkills = "";
			$scope.client = [];
			$(".formddlist .dropdown-toggle").css("border-color", "#c2cad8");
			$(".clientname .mustfield").hide();
			$("#res").css("display", "none");
			$scope.AddCandidateForm.$setPristine()
		}
		
		
		$scope.candidateResettab2 = function()
		{
			$(".lockbtn").hide();;
			$(".uploadbtn").show();
			$scope.createJobOrdersFields.description = "";
			$scope.createJobOrdersFields.note = "";
			$scope.fileAttachment = "";
			$scope.client = [];
			$(".formddlist .dropdown-toggle").css("border-color", "#c2cad8");
			$(".clientname .mustfield").hide();
			$("#res").css("display", "none");
			$scope.AddCandidateForm.$setPristine()
		}
		
		
		
		$scope.candidateResettab3 = function()
		{
//			alert('');
			$("#permanentoptions").css("display","block");
			$("#contractoptions").css("display","none");
			$scope.createJobOrdersFields.jobType = "PERMANENT";
			$scope.createJobOrdersFields.salary = "";
			$scope.createJobOrdersFields.permFee = "";
			$scope.createJobOrdersFields.startDate = "";
			$scope.createJobOrdersFields.acceptW2 = false;
			$scope.createJobOrdersFields.hourlyRateW2 = "";
			$scope.createJobOrdersFields.annualRateW2 = "";
			$scope.createJobOrdersFields.accept1099 = "";
			$scope.createJobOrdersFields.hourlyRate1099 = "";
			$scope.createJobOrdersFields.acceptC2c = false;
			$scope.createJobOrdersFields.hourlyRateC2c = "";
			$scope.createJobOrdersFields.startDate = "";
			$scope.createJobOrdersFields.endDate = "";
			$("#1099hour").attr("readonly","true");
			$("#corphour").attr("readonly","true");
			$("#w2hour").attr("readonly","true"); 
			$("#w2annual").attr("readonly","true");
			 $scope.jobOrderFieldList = [{"id":Math.floor((Math.random() * -10000) + 1),"fieldName":"WORK_STATUS","fieldValue":"","isCollapse":false,"visible":true},{"id":Math.floor((Math.random() * -10000) + 1),"fieldName":"BILL_RATE","fieldValue":"","isCollapse":false,"visible":true}];
			 $scope.client = [];
				$(".formddlist .dropdown-toggle").css("border-color", "#c2cad8");
				$(".clientname .mustfield").hide();
				$("#res").css("display", "none");
			 $scope.AddCandidateForm.$setPristine()
		}
		
		$scope.originalResumeView = function(element){
			if($scope.attachment != null){
				resumesView.getResumeContent($scope.attachment).then(function(response){
					resumesView.viewResume(element, response.data.content);
				});
			}else{
				resumesView.viewResume(element, $scope.createJobOrdersFields.strAttachment);
				}
		}
		$scope.originlck = function()
		{
			$scope.origuploadbtn = true;
		}
		
		$scope.downloadOriginalResume = function(){
			if($scope.attachment != null){
				resumesView.getResumeContent($scope.attachment).then(function(response){
					resumesView.downloadResume(response.data.content, 'Download', 'JobOrder');
				});
			}else{
				 if($stateParams.jobOrderId != undefined && $stateParams.jobOrderId !=null){
				$window.location = 'jobOrder/downloadJobOrderAttachment/'+$stateParams.jobOrderId;
				 }
			}
		}
	
	$scope.clrval = function()
	{
		$scope.createJobOrdersFields.salary = "";
		$scope.createJobOrdersFields.permFee = "";
		$scope.createJobOrdersFields.hourlyRateW2 = "";
		$scope.createJobOrdersFields.hourlyRateW2max = "";
		$scope.createJobOrdersFields.annualRateW2 = "";
		$scope.createJobOrdersFields.hourlyRate1099 = "";
		$scope.createJobOrdersFields.hourlyRateC2c = "";
		$scope.createJobOrdersFields.hourlyRateC2cmax = "";
		$scope.createJobOrdersFields.acceptW2 = false;
		$scope.createJobOrdersFields.acceptC2c = false;
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
						var response = $http.post('jobOrder/saveClient',$scope.addClientObj);
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
					var response = $http.post('jobOrder/updateClient',$scope.editClientObj);
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
    	  var response = $http.get('commonController/getAllClients');
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
	
	
	
	})
	

	
	
	
	.directive('onlyLettersInput', onlyLettersInput);
	  
	  function onlyLettersInput() {
	      return {
	        require: 'ngModel',
	        link: function(scope, element, attr, ngModelCtrl) {
	          function fromUser(text) {
	            var transformedInput = text.replace(/[^a-z . A-Z]/g, '');
	            //console.log(transformedInput);
	            if (transformedInput !== text) {
	              ngModelCtrl.$setViewValue(transformedInput);
	              ngModelCtrl.$render();
	            }
	            return transformedInput;
	          }
	          ngModelCtrl.$parsers.push(fromUser);
	        }
	      };
	    };
	  
	  
	  
	
	
	
	
	
})(angular);