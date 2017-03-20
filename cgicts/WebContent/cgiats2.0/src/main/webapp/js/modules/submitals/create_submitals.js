;(function(angular){
	"use strict";	
	angular.module("createsubmitals",['DatePicker','ui.bootstrap','angular-highlight','jcs-autoValidate'])
	.controller("createsubmitalscontroller",function( $rootScope, $scope, blockUI, $http, $timeout, $location,
			$filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage,dateRangeService,mailService){
		$scope.errMsg = null;
		$scope.usstates = usStates;
		$scope.$storage = $sessionStorage;
		$scope.candidate = [];
		var candidateData = [];
		$scope.searchTable = false;
		$scope.isEventCompleted = true;
		/*----------Pagination Details---------*/
		$scope.pageNumber = 1;
		$scope.pageSize = 10;
		
		$scope.searchFields = {};

		$scope.submittal = {};
		$scope.submittalEventHistoryDtoList = [{'strCreatedOn':getCurrentTime(false),'status':'SUBMITTED','notes':'New Submittal created.'}];
		$scope.submittal.status = constants.SUBMITTED;
		$scope.eventHistory = {};
		
		/*$scope.editHistory = function(historyEvent){
			$scope.editMode = true;
			$scope.eventHistory = historyEvent;
		}
		
		*/
		$scope.editHistory = function(historyEvent){
			$scope.editMode = true;
//			$scope.eventHistory = historyEvent;
			historyEvent.oldStrCreatedOn = historyEvent.strCreatedOn;
			historyEvent.jsEditMode = true;
		}
		
		$scope.cancelStatusDate = function(historyEvent){
			historyEvent.strCreatedOn = historyEvent.oldStrCreatedOn;
			$scope.editMode = false;
			historyEvent.jsEditMode = false;
		}
		
		$scope.updateStatusDate = function(historyEvent){
			$scope.editMode = false;
			historyEvent.isEditMode = true;
			historyEvent.jsEditMode = false;
			$scope.eventHistory = {};
		} 
		
		
		function getCurrentTime(isFromEventHistory){
			/*var d = new Date,
		    dformat = [ d.getFullYear(),(d.getMonth()+1)<10?(0+""+(d.getMonth()+1)):d.getMonth()+1,
		               d.getDate()<10?(0+""+ d.getDate()): d.getDate()
		              ].join('-')+' '+
		              [d.getHours()<10?(0+""+d.getHours()):d.getHours(),
		               d.getMinutes()<10?(0+""+d.getMinutes()):d.getMinutes()].join(':');
			return dformat;*/
				var response = $http.get('commonController/getCurrentTime');
				response.success(function (data,status,headers,config){
					if($scope.submittalEventHistoryDtoList && $scope.submittalEventHistoryDtoList.length ===1){
						$scope.submittalEventHistoryDtoList[0].strCreatedOn = data.currentTime;
					}
					if(isFromEventHistory){
					$scope.eventHistory.strCreatedOn = data.currentTime;
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
		
		
		$scope.sendResumeToClient = function(){
			mailService.sendMail("test@charterglobal.com", false);
		};
		
		$scope.onload = function()
		{
			
			
			if($location.search().jobOrder != undefined){
				$(".underlay").show();
				var response = $http.get('jobOrder/getReadableJobOrderById/'+$location.search().jobOrder);
				response.success(function(data, status, headers,
						config) {
					$(".underlay").hide();
					$scope.id = data.id
					$scope.priority = data.priority;
					$scope.client = data.customer;
					$scope.keyskills = data.keySkills;
					$scope.title = data.title;
					$scope.city = data.city;
					$scope.noOfResumesRequired = data.noOfResumesRequired;
					$scope.jobExpireOn = data.jobExpireIn;
					$scope.state = data.state;
					$scope.description = data.description;
					$scope.jobtype = data.jobType;
					$scope.noOfPositions = data.numOfPos;
					$scope.attachment = data.attachmentFileName==null ? N/A : data.attachmentFileName;
					$scope.strAttachment = data.strAttachment;
					$scope.salary = data.salary;
					$scope.permfee = data.permFee;
					$scope.startdate = data.startDate;
					$scope.acceptW2 = data.acceptW2;
					$scope.hourlyRateW2 = data.hourlyRateW2;
					$scope.hourlyRateW2max = data.hourlyRateW2max;
					$scope.annualRateW2 = data.annualRateW2;
					$scope.accept1099 = data.accept1099;
					$scope.hourlyRate1099 = data.hourlyRate1099;
					$scope.acceptC2c = data.acceptC2c;
					$scope.hourlyRateC2c = data.hourlyRateC2c;
					$scope.hourlyRateC2cmax = data.hourlyRateC2cmax;
					$scope.startDate = data.startDate;
					$scope.endDate = data.endDate;
					$scope.joborderdetails = data.jobOrderFieldList;
					$scope.customerHiddenValue = data.strCustomerHidden;
					
					//Auto populating in candidate Tab
					
//					$scope.searchFields.keySkill = data.keySkills;
//					$scope.searchFields.title = data.title;
					


					
					if($scope.salary)
					{
					$scope.salary = '$ ' + String($scope.salary).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					if($scope.permfee)
					{
					$scope.permfee = String($scope.permfee).replace(/\B(?=(\d{3})+(?!\d))/g, ",")  + ' %';
					}
					
					
					
					
					if($scope.hourlyRateW2)
					{
					$scope.hourlyRateW2 = '$ ' + String($scope.hourlyRateW2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					
					if($scope.hourlyRateW2max)
					{
					$scope.hourlyRateW2max = '$ ' + String($scope.hourlyRateW2max).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					if($scope.annualRateW2)
					{
					$scope.annualRateW2 = '$ ' + String($scope.annualRateW2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					if($scope.hourlyRate1099)
					{
					$scope.hourlyRate1099 = '$ ' + String($scope.hourlyRate1099).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					
					
					if($scope.hourlyRateC2c)
					{
					$scope.hourlyRateC2c = '$ ' + String($scope.hourlyRateC2c).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
					}
					
					
					if($scope.hourlyRateC2cmax)
					{
					$scope.hourlyRateC2cmax = '$ ' + String($scope.hourlyRateC2cmax).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
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
				
				$scope.searchBy = 'All';
				 $timeout(function() {//wait for some time to redirect to another page
					 $scope.searchbyjoborderdetails();
				 }, 1000);

		}
		
		$scope.searchbyjoborderdetails = function(){
			$scope.searchFields = {};
			var states = [];
			if($scope.state!=undefined){
			 states=[$scope.state.split(", ")[1]];
			}
			//var states=[$scope.state.split(", ")[1]];
			$scope.searchBy= $("#searchby").val();
			if($scope.searchBy!= null && $scope.searchBy!='' && $scope.searchBy!=undefined){
				$scope.searchFields.created = { endDate: moment(), startDate: moment().subtract(6, 'year')};
			
			if($scope.searchBy == 'Title'){
				$scope.searchFields.title = "\""+$scope.title+"\"";
			}
			if($scope.searchBy == 'Key Skills'){
				$scope.searchFields.keySkill =$scope.keyskills;
			}
			if($scope.searchBy == 'Location'){
				$scope.searchFields.states = states;
				$scope.searchFields.city = $scope.city;
				//$scope.searchFields.city = $scope.city +", "+states;
			}
			if($scope.searchBy == 'All'){
				$scope.searchFields.title = "\""+$scope.title+"\"";
				$scope.searchFields.keySkill = $scope.keyskills;
				$scope.searchFields.states = states;
				$scope.searchFields.city = $scope.city;
				//$scope.searchFields.city = $scope.city +", "+states;
			}
			
			
			$scope.searchTable = true;
			var searchid = $("input[ng-model='candidatesearchid']");
			searchid.css("border", "1px solid #c2cad8");
			searchid.siblings(".err").hide();
			$("#searchcan_submital").slideUp("slow");
			$("#candidate_table").slideDown("slow");
			$("#submitalsearch i").removeClass("fa-plus-square");
		       $("#submitalsearch i").addClass("fa-minus-square");
			
			$scope.searchFields.fieldName = "";
			$scope.searchFields.sortName = "";
			var response = $http.post("searchResume/getSearchResumes?pageNumber="+$scope.pageNumber+"&pageSize="+$scope.pageSize,$scope.searchFields);
			response.success(function(data, status,headers, config) 
					{
				$scope.$storage.pageNumber = $scope.pageNumber;
				$scope.$storage.pageSize = $scope.pageSize;
				dispalyTable();
				$scope.candidate.bsTableControl.options.data =data; 
				$scope.candidate.bsTableControl.options.pageNumber =$scope.$storage.pageNumber;
				$scope.candidate.bsTableControl.options.pageSize =$scope.$storage.pageSize;
				$scope.candidate.bsTableControl.options.totalRows =data[0].totalRecords;
				$scope.$storage.searchFields = $scope.searchFields;
		      });
			response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
		}
			$("#submitalselectedcandidate").slideUp("slow");
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
		
		$scope.showsearchfields = function()
		{
			$("#candidatesearchid").val("");
			$("#candidateEmailID").val("");
			var fulldate = new Date();
			var month = ( '0' + (fulldate.getMonth()+1) ).slice( -2 );
			var date = ( '0' + (fulldate.getDate()) ).slice( -2 );
			var year = fulldate.getFullYear() - 1;
			var cyear = fulldate.getFullYear();
			var totaldate = year + "-" + month + "-" + date;
			var ctotaldate = cyear + "-" + month + "-" + date;
			var finaldate = totaldate + " - " + ctotaldate; 
			
			
			$scope.searchFields.created = { endDate: moment(), startDate: moment().subtract(1, 'year')};
			  $scope.ranges = {
				        'All Time'  : [moment().subtract(6, 'year'), moment()],
				        'Today': [moment(),moment()], 
				        'Last 1 month': [moment().subtract(1, 'month'), moment()],
				        'Last 3 months': [moment().subtract(3, 'month'), moment()],
				        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
				        'Last 1 year': [moment().subtract(12, 'month'), moment()]
				        };		
			  
			$("input[ng-model='searchFields.created']").val(finaldate);
			$("div[class='ranges'] li").removeClass("active");
			$("div[class='ranges'] li:contains('Last 1 year')").addClass("active");
			
			var searchid = $("input[ng-model='candidatesearchid']");
			searchid.css("border", "1px solid #c2cad8");
			searchid.siblings(".err").hide();
			$("#searchcan_submital").slideDown("slow");
			$("#candidate_table").slideUp("slow");
			$("#submitalselectedcandidate").slideUp("slow");
			$("#submitalsearch i").removeClass("fa-plus-square");
		       $("#submitalsearch i").addClass("fa-minus-square");
		}
		$scope.cancelsubmitalsearch = function()
		{
			var searchid = $("input[ng-model='candidatesearchid']");
			searchid.css("border", "1px solid #c2cad8");
			searchid.siblings(".err").hide();
			$("#searchcan_submital").slideUp("slow");
			$("#submitalsearch i").removeClass("fa-minus-square");
			$("#submitalsearch i").addClass("fa-plus-square");
		}
		$scope.searchsubmitalCandidate = function()
		{
			$scope.searchTable = true;
			var searchid = $("input[ng-model='candidatesearchid']");
			searchid.css("border", "1px solid #c2cad8");
			searchid.siblings(".err").hide();
			$("#searchcan_submital").slideUp("slow");
			$("#candidate_table").slideDown("slow");
			$("#submitalsearch i").removeClass("fa-plus-square");
		       $("#submitalsearch i").addClass("fa-minus-square");
			
		       var dateRangeActiveValue = $(".ranges").children("ul").children("li[class='active']").text();
				if(!dateRangeActiveValue){
					dateRangeActiveValue = constants.LAST_ONE_MONTH;
				}
				if(dateRangeService.selectedDateAction(dateRangeActiveValue,$scope.searchFields.created)){
				$scope.searchFields.created = dateRangeService.selectedDateAction(dateRangeActiveValue,$scope.searchFields.created);
				}
				
				if($('#candidateSearchRangesId').val() && $('#candidateSearchRangesId').val()!=''){
					$scope.searchFields.created.startDate = $('#candidateSearchRangesId').val().split(' ')[0];
					$scope.searchFields.created.endDate = $('#candidateSearchRangesId').val().split(' ')[2];
				}
				if($('#autoLocation').val() && $('#autoLocation').val()!=''){
					$scope.searchFields.city = $('#autoLocation').val();
				}else{
					$scope.searchFields.city = $('#autoLocation').val();
				}
				$scope.searchFields.fieldName = "";
				$scope.searchFields.sortName = "";
				var response = $http.post("searchResume/getSearchResumes?pageNumber="+$scope.pageNumber+"&pageSize="+$scope.pageSize,$scope.searchFields);
				response.success(function(data, status,headers, config) 
						{
					$scope.$storage.pageNumber = $scope.pageNumber;
					$scope.$storage.pageSize = $scope.pageSize;
					dispalyTable();
					$scope.candidate.bsTableControl.options.data =data; 
					$scope.candidate.bsTableControl.options.pageNumber =$scope.$storage.pageNumber;
					$scope.candidate.bsTableControl.options.pageSize =$scope.$storage.pageSize;
					$scope.candidate.bsTableControl.options.totalRows =data[0].totalRecords;
					$scope.$storage.searchFields = $scope.searchFields;
			      });
				response.error(function(data, status, headers, config){
		  			  if(status == constants.FORBIDDEN){
		  				location.href = 'login.html';
		  			  }else{  			  
		  				$state.transitionTo("ErrorPage",{statusvalue  : status});
		  			  }
		  		  });
		}
		
		$scope.submitalcanid = function()
		{
			var searchid = $("input[ng-model='candidatesearchid']");
			searchid.css("border", "1px solid #c2cad8");
			searchid.siblings(".err").hide();
			$("#candidate_table").slideUp("slow");
			$("#submitalselectedcandidate").slideDown("slow");
		}
		
		$scope.submitalbycanid = function()
		{
			var searchid = $("input[ng-model='candidatesearchid']");
			if(searchid.val() == "" || searchid.val() == undefined || searchid.val() == null)
				{
				searchid.css("border", "1px solid #ff0000");
				searchid.siblings(".err").show();
				}
			else
				{
				$scope.candidatesearchid = $('#candidatesearchid').val();
				if($scope.candidatesearchid !=''){
				var response = $http.post('searchResume/getCandidate?candidateId='+$scope.candidatesearchid);
				response.success(function(data, status,headers, config) {
					if(data)
						{
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
						
						searchid.css("border", "1px solid #27a4b0");
						searchid.siblings(".err").hide();
						$("#searchcan_submital").slideUp("slow");
						$("#candidate_table").slideUp("slow");
						$("#submitalselectedcandidate").slideDown("slow");
						$("#res01").hide();
						$("#candidatesearchid").val("");
						
						}
					else
						{
						$("#res01").show();
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
		}
		
		$scope.jumpToProfile = function() {
			$scope.candidateid=$('#candidateid').val();
			if($scope.candidateid != ''){
			$state.transitionTo("EditCandidate",{candidateId : $scope.candidateid, page:"search"});
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
				$scope.isEventCompleted = false;
				if(!$scope.eventHistory.strCreatedOn){
					$scope.eventHistory.strCreatedOn = getCurrentTime(true);
					
					 $timeout(function() {//wait for some time to redirect to another page
						 $scope.submittalEventHistoryDtoList.push($scope.eventHistory);
							$scope.submittalEventHistoryDtoList.reverse();
							$scope.submittal.status = $scope.eventHistory.status;
							$scope.submittal.createdOn = $scope.eventHistory.strCreatedOn;
							$scope.eventHistory = {};
							$scope.isEventCompleted = true;
					 }, 100);
				}else{
					$scope.submittalEventHistoryDtoList.push($scope.eventHistory);
					$scope.submittalEventHistoryDtoList.reverse();
					$scope.submittal.status = $scope.eventHistory.status;
					$scope.submittal.createdOn = $scope.eventHistory.strCreatedOn;
					$scope.eventHistory = {};
					$scope.isEventCompleted = true;
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
			var res = $http.get('jobOrder/findTheStatusOfCandidate?candidateId='+$scope.candidateid+'&jobOrderId='+$location.search().jobOrder);
			res.success(function(data){
				 if(data){
					 if(!data.message){
						 $scope.submittal.submittalEventHistoryDtoList = $scope.submittalEventHistoryDtoList;
						 $scope.submittal.jobOrderId = $location.search().jobOrder;
						 $scope.submittal.candidateId = $scope.candidateid;
							 
						 var response = $http.post('jobOrder/saveOrUpdateSubmittal', $scope.submittal);
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

	/* Set table and pagination */
	function dispalyTable() {
					$scope.candidate.bsTableControl = {
						options : {
							striped : true,
							pagination : true,
							paginationVAlign : "both",
							sidePagination : 'server',
							silentSort: false,
							pageList : [ 10, 20, 50 ],
							search : false,
							showColumns : false,
							showRefresh : false,
							clickToSelect : false,
							showToggle : false,
							maintainSelected : true,
							columns : [
							           {
							        	   field : 'id',
							        	   title : 'Id',
							        	   align : 'left',
							        	   events : window.editevents,
							        	   formatter : editJobOrder,
							        	   sortable : true
							           },
									{
										field : 'firstName',
										title : 'Name',
										align : 'left',

										sortable : true
									},
									{
										field : 'title',
										title : 'Title',
										align : 'left',

										sortable : true
									},
									{
										field : 'city',
										title : 'Location',
										align : 'left',

										sortable : true
									},
									{
										field : 'createdOn',
										title : 'Created',
										align : 'left',
										sortable : true
									},
									{
										field : 'updatedOn',
										title : 'Updated',
										align : 'left',
										sortable : true
									},
									{
										field : 'status',
										title : 'Status',
										align : 'left',
										sortable : true
									},
									{
										field : 'visaType',
										title : 'Visa Status',
										align : 'left',
										sortable : true
									},
									/*{
										field : 'resume',
										title : 'Resume',
										align : 'center',
										sortable : false,
										events : window.resumeEventss,
										formatter : resumeFormatter
									},*/
									{
										field : 'actions',
										title : 'Actions',
										align : 'center',
										sortable : false,
										events : window.operateEventss,
										formatter : actionFormatter
									} ],
									
						   onPageChange: function (number, size) {
							   $(".underlay").css("display", "block");
							   $scope.$storage.pageNumber = number;
							   $scope.$storage.pageSize = size;
							   $scope.$storage.searchFields.isCallFromPagination = true;
							   var response = $http.post("searchResume/getSearchResumes?pageNumber="+number+"&pageSize="+size,$scope.$storage.searchFields);
								response.success(function(data, status,headers, config) 
										{
									$scope.$storage.searchFields.isCallFromPagination = false;
									candidateData = [];
									candidateData.push(data);
									dispalyTable();
									$scope.candidate.bsTableControl.options.responseHandler(data); 
									$scope.candidate.bsTableControl.options.data =data; 
									$scope.candidate.bsTableControl.options.pageNumber =number;
									$scope.candidate.bsTableControl.options.pageSize =size;
									$scope.candidate.bsTableControl.options.totalRows =data[0].totalRecords;
									$(".underlay").css("display", "none");
							      }); 
								response.error(function(data, status, headers, config){
						  			  if(status == constants.FORBIDDEN){
						  				location.href = 'login.html';
						  			  }else{  			  
						  				$state.transitionTo("ErrorPage",{statusvalue  : status});
						  			  }
						  		  });
								
							},
							 onSort: function (name, order) {
								 $(".underlay").show();
//								 alert(name);
								 if(!$scope.orderType || ($scope.orderName && $scope.orderName != name)){
	                        		  $scope.orderType = order;
	                        	  }
	                        	  if(($scope.orderName && $scope.orderName == name) ){
	                        		  if($scope.orderType && $scope.orderType == constants.ASC){
	                        			  $scope.orderType = constants.DESC;
	                        		  }else{
	                        			  $scope.orderType = constants.ASC;
	                        		  }
	                        	  }
	                        	 $scope.orderName = name;  
								 
								var searchFileds = $scope.$storage.searchFields;
								searchFileds["fieldName"] = $scope.orderName;
								 searchFileds["sortName"] = $scope.orderType;
								 $scope.$storage.searchFields.isCallFromPagination = true;
								 var response = $http.post("searchResume/getSearchResumes?pageNumber="+$scope.$storage.pageNumber+"&pageSize="+ $scope.$storage.pageSize,searchFileds);
									response.success(function(data, status,headers, config) 
											{
										if(data){
										$scope.$storage.searchFields.isCallFromPagination = false;
										candidateData = [];
										candidateData.push(data);
										dispalyTable();
										$scope.candidate.bsTableControl.options.data =data; 
										$scope.candidate.bsTableControl.options.pageNumber =$scope.$storage.pageNumber;
										$scope.candidate.bsTableControl.options.pageSize =$scope.$storage.pageSize;
										$scope.candidate.bsTableControl.options.totalRows =data[0].totalRecords;
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
						        },
						    	responseHandler : function(res) {
								    $.each(res, function (i, row) {
								        row.state = $.inArray(row.email, $scope.selectionCheck) !== -1;
								    });
								    return res;
								},
						}
					};
			
					function editJobOrder(value, row,
							index){
						return [
						        '<a class="edit actionIcons" title="Edit candidate">'+row.id+'</a>'
								 ]
								.join('');
					}
					
					window.editevents = {
							'click .edit' : function(e,value, row,
							index){
								$("#res").hide();
								$scope.candidateid = row.id;
								$scope.candidatename = row.firstName;
								$scope.candidatetitle = row.title;
								$scope.candidatekeyskills = row.keySkill;
								$scope.candidatelocation = row.city;
								$('#candidateid').val($scope.candidateid);
								$('#candidatename').val($scope.candidatename);
								$('#candidatetitle').val($scope.candidatetitle);
								$('#candidatekeyskills').val($scope.candidatekeyskills);
								$('#candidatelocation').val($scope.candidatelocation);
								
								$("#candidate_table").slideUp("slow");
								$("#submitalselectedcandidate").slideDown("slow");
								
							}
					};
				
					// for resume Dialogue box
					function resumeFormatter(value, row,
							index) {
						/*return [ '<a class="info actionIcons" title="View Resume"><i class="fa fa-file-word-o"></i></a>' ]
								.join('');*/
					}

					window.resumeEventss = {
						/*'click .info' : function(e, value,
								row, index) {
							var candidateId = row.id;
							var candidateresumeData=[];
							var objresume = null;
							var docStats = "others";
							var response = $http.get('searchResume/resumeByCandidateId?candidateId='
									+ candidateId+'&docStats='+docStats);
							response.success(function(data, status,headers, config) 
									{
								var candresume = JSON.stringify(data);
								 objresume = JSON.parse(candresume);
								$scope.CandidateText = objresume.resumeContent;
								$scope.CandidateKeywords = $scope.searchFields.keySkill;
								
								$mdDialog
										.show({
											controller : ResumeDialogControllers,
											templateUrl : 'views/dialogbox/resumedialogbox.html',
											parent : angular
													.element(document.body),
											targetEvent : e,
											locals : {
												CandidateText : $scope.CandidateText,
												CandidateKeywords : $scope.CandidateKeywords
											},
											clickOutsideToClose : true,
										});
						      });
							response.error(function(data, status, headers, config){
					  			  if(status == constants.FORBIDDEN){
					  				location.href = 'login.html';
					  			  }else{  			  
					  				$state.transitionTo("ErrorPage",{statusvalue  : status});
					  			  }
					  		  });
							
						}*/
					};
					/* Table button action formatters */

					function actionFormatter(value, row,
							index) {
							return [
							        
                            '<a class="view actionIcons" title="View details" flex-gt-md="auto"><i class="fa fa-search" style="font-size:13px;"></i></a>',
							]
							.join('');
					}

					/* Table button actions functionalities */
					window.operateEventss = {


						/* View Resume details */
							'click .view' : function(e,value, row, index) {
								$(".underlay").show();
								var candidateId = row.id;
								var response = $http
										.get('searchResume/viewCandidateDetails?candidateId='
												+ candidateId);
								response.success(function(data, status,headers, config) 
										{
									if(data){
//									if($scope.searchFields.resumeTextQuery){
//										data.keyskillsAndKeyWords = $scope.searchFields.keySkill +","+ $scope.searchFields.resumeTextQuery;
//										}else{
//											data.keyskillsAndKeyWords = $scope.searchFields.keySkill;
//										}
//										if(data.keyskillsAndKeyWords){
//											data.keyskillsAndKeyWords = data.keyskillsAndKeyWords.toLowerCase();
//										}
										
										
										if($scope.searchFields.keySkill && $scope.searchFields.resumeTextQuery){
											data.keyskillsAndKeyWords = $scope.searchFields.keySkill +","+  $scope.searchFields.resumeTextQuery;
											}else if($scope.searchFields.resumeTextQuery){
												data.keyskillsAndKeyWords = $scope.searchFields.resumeTextQuery;
											}else{
												data.keyskillsAndKeyWords = $scope.searchFields.keySkill;
											}
											if(data.keyskillsAndKeyWords){
												data.keyskillsAndKeyWords                = data.keyskillsAndKeyWords.toLowerCase();
											}
									
									$mdDialog.show(	{
														controller : DialogController,
														templateUrl : 'views/dialogbox/viewcandidatedialogbox.html',
														parent : angular
																.element(document.body),
														targetEvent : e,
														locals : {
															rowData : data,
														},
														clickOutsideToClose : true,
													});
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
							},
					
					};
					function DialogController($scope,
							$mdDialog, rowData) {
						$scope.row = rowData;
//						alert(JSON.stringify($scope.row));
						var candresume = JSON.stringify(rowData);
						 var viewObjResume = JSON.parse(candresume);
						$scope.viewCandidateText = viewObjResume.resumeContent;
						
//						alert(rowData.keyskillsAndKeyWords);
						
//						alert(rowData.keyskillsAndKeyWords);
						var viewStrKeywords = rowData.keyskillsAndKeyWords;
						
						
//						alert(viewStrKeywords);
						if(viewStrKeywords != null || viewStrKeywords != undefined){
							viewStrKeywords = viewStrKeywords.replace(/[*+:]/g, ",").replace(/[?]/g, ",").replace(/[!]/g, ",");
//							alert(viewStrKeywords);
							var keywords = viewStrKeywords.replace(/["']/g, ":-").split(/:/);
							if(keywords.length > 1){
								var key="";
							for(var k=0;k<keywords.length;k++){
								if(keywords[k] && keywords[k].length > 2 && keywords[k].charAt(1)!=' ' && keywords[k].indexOf('-') != -1 && keywords[k].indexOf('-') == 0){
									var requiredValue= keywords[k].substring(1,keywords[k].length);
									if(key!=""){
									key += ","+requiredValue.replace(/ /g, "_");
									}else{
										key += requiredValue.replace(/ /g, "_");
									}
								}else if(keywords[k] && keywords[k].length > 2  && keywords[k].indexOf('-') != -1 && keywords[k].indexOf('-') == 0){
									if(key!=""){
										key+=","+keywords[k].substring(2,keywords[k].length);
										}else{
											key+=keywords[k].substring(2,keywords[k].length);
										}
								}else if(keywords[k] && keywords[k].length > 2){
									if(key!=""){
										key+=","+keywords[k];
										}else{
											key+=keywords[k];
										}
								}
							}
							if(key.length>1){
							viewStrKeywords = key;
							}
							}
							
							
						viewStrKeywords = viewStrKeywords.replace(/["']/g, "").replace(/[NOT]/g, "(").replace(/[(]/g, "").replace(/[)]/g, "");
						var viewStrKeywordsArray = viewStrKeywords.split(/[ \(,\)]+/);
						
						var indexOfAnd = -1;
						
//						alert(viewStrKeywordsArray);
						
						if(viewStrKeywordsArray){
							for(var i=0;i<viewStrKeywordsArray.length;i++){
//								alert(viewStrKeywordsArray[i]+' siva');
								viewStrKeywordsArray[i] = viewStrKeywordsArray[i].replace(/_/g, " ");
								viewStrKeywordsArray[i] = viewStrKeywordsArray[i].replace(/ or /g, ",");
								viewStrKeywordsArray[i] = viewStrKeywordsArray[i].replace(/or /g, ",");
								viewStrKeywordsArray[i] = viewStrKeywordsArray[i].replace(/ and /g, ",");
								viewStrKeywordsArray[i] = viewStrKeywordsArray[i].replace(/and /g, ",");
								indexOfAnd = viewStrKeywordsArray.indexOf("and");
								if(indexOfAnd != -1){
									viewStrKeywordsArray.splice( indexOfAnd, 1 );
								}
									indexOfAnd = viewStrKeywordsArray.indexOf("or");
									if(indexOfAnd != -1){
									viewStrKeywordsArray.splice( indexOfAnd, 1 );
									}
									indexOfAnd = viewStrKeywordsArray.indexOf("undefined");
									if(indexOfAnd != -1){
									viewStrKeywordsArray.splice( indexOfAnd, 1 );
									}
									
							}
						}
						
						for(var i=0;i<viewStrKeywordsArray.length;i++){
//							alert(viewStrKeywordsArray[i]);
							if(viewStrKeywordsArray[i] == constants.Dotnet){
								viewStrKeywordsArray[i] = constants.EscDotNet;
							}
						}
						
//						alert(viewStrKeywordsArray);
							viewStrKeywords = viewStrKeywordsArray.join(',');
							viewStrKeywords = viewStrKeywords.replace(/[,]+/g, ",")
							viewStrKeywords = viewStrKeywords.replace(/^,|,$/g,'');
//							alert(viewStrKeywords);
							
							$scope.viewCandidateKeywords = viewStrKeywords;
							 var highlightRe = /<span class="highlight">(.*?)<\/span>/g,
							    highlightHtml = '<span class="highlight">$1</span>';

							 
							 var keywordArray = viewStrKeywords.split(',');
							 for(var j=0;j<keywordArray.length;j++){
								 var term = keywordArray[j];
							        if(term !== '') {
							        	$scope.viewCandidateText = $scope.viewCandidateText.replace(new RegExp('(' + term + ')', 'gi'), highlightHtml);
							        }  
							       
							 }
						}
						
						$scope.hide = function() {
							$mdDialog.hide();
						};

						$scope.cancel = function() {
							$mdDialog.cancel();
						};
					}

					/*function ResumeDialogControllers($scope,
							$mdDialog, CandidateText,
							CandidateKeywords) {
						$scope.CandidateText = CandidateText;
						$scope.CandidateKeywords = CandidateKeywords;

						$scope.cancel = function() {
							$mdDialog.cancel();
						};
					}*/

					function notification($scope) {
						var msg = "show this";
						$
								.notify(
										"Warning: Self-destruct in 3.. 2..",
										"warn");
					}
	}
	
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
		$window.location = 'jobOrder/downloadJobOrderAttachment/'+jobOrderId;
	}
	
	
	
	
	$scope.searchbyemail = function()
	{
		/*$scope.candidateId = $("#candid").val();*/
		$scope.emailId = $("#candidateEmailID").val();
		if($scope.emailId != ''){
			$scope.errMsg1 = false;
		var response = $http.post('searchResume/getCandidateByEmailId?emailId='+ $scope.emailId);
		response.success(function(data, status,headers, config) 
				{
			/*alert("data"+JSON.stringify(data));*/
			$("#searchcan_submital").slideUp();
			$("#candidate_table").slideUp();
			$("#submitalselectedcandidate").slideDown();
			$scope.candidateid = data.id;
			$scope.candidatename = data.fullName;
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
			$scope.errMsg1 = true;
		}

}
	
	
	});
	
	
})(angular);