;
(function(angular) {

	"use strict";

	angular
			.module(
					'SearchModule',
					[ 'ngMaterial', 'ngMessages', 'ui.bootstrap',
							'angular-highlight', 'jcs-autoValidate', 'ngStorage'])
							
			.service('searchData', ['$http', function ($http) {
								this.candidateSearch = function(searchFields,number, size){
									var response = $http.post("searchResume/getSearchResumes?pageNumber="+number+"&pageSize="+size,searchFields);
									response.success(function(data, status,headers, config) 
											{
										return data;
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
							}])
			.controller(
					'SearchController',
					function(searchData,smsService, mailService, $rootScope, $scope, blockUI, $http, $timeout, $location,dateRangeService,
							$filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage) {
						
							$scope.$storage = $sessionStorage;
						// Set sidebar closed and body solid layout mode
						$rootScope.settings.layout.pageContentWhite = true;
						$rootScope.settings.layout.pageBodySolid = false;
						$rootScope.settings.layout.pageSidebarClosed = false;
						var isCancel = false;
						$scope.$on('$viewContentLoaded', function() {
							// initialize core components
							App.initAjax();
						});
						// Date Picker
						$scope.Created = {
							endDate : moment(),
							startDate : moment().subtract(12, 'month')
						};
						
						$scope.usstates = usStates;
						//alert(JSON.stringify(usstates));
						$scope.ranges = {
							'Today' : [ new Date(), new Date() ],
							'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],
							'Last 1 month' : [ moment().subtract(1, 'month'),
									moment() ],
							'Last 3 months' : [ moment().subtract(3, 'month'),
									moment() ],
							'Last 6 months' : [ moment().subtract(6, 'month'),
									moment() ],
							'Last 1 year' : [ moment().subtract(12, 'month'),
									moment() ]
						};

						// Auto Complete
						$scope.result = '';
						$scope.options = {
							country : 'us',
							types : '(cities)'
						};
						$scope.details = '';
						
						var queryNameAutopopulate = [];
						$scope.searchToggleBtn = function() {
							$(".mesgerror").hide();
							$("#autoLocation").css("border-color", "#27a4b0");
							savedQueryNames = [];
							$scope.getSavedNamedQueries();
							$scope.list_category = queryNameAutopopulate;
							$scope.searchForm = true;
							$scope.searchToggle = true;
							$scope.searchBtn = false;
							$scope.searchCadidateRow = true;
							$scope.advancedSearchRow = true;
							$scope.searchRecords = true;
							if(queryNameAutopopulate && queryNameAutopopulate!=''){
							$scope.SavedQueries();
							}
						}
						
						
						
						
						
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
						var newCandidateAddedResponse = $stateParams.newCandidate;
						var editCandidateAddedResponse = $stateParams.editCandidate;
						var searchTogglebutton = $stateParams.searchToggle;
						// Search Candidate
						$scope.searchForm = true;
						$scope.searchToggle = false;
						$scope.showGridTable = false;
						$scope.searchBtn = true;
						$scope.advancedContainer = true;
						$scope.advancedSearchRow = true;
						$scope.searchTable = false;
						$scope.searchRecords = true;
						$scope.searchTable = false;
						$scope.advancedSearch = false;
						var critiriaFromOld = false;
						$scope.orderName = "";
						$scope.orderType = "";
						// Search Candidate Action
						$scope.candidate = {};
						//$scope.candidates.push({});
						var candidateData = [];
						$scope.dataFromChecked = [];
						$scope.searchCandidateCheck = [];
						$scope.selectionCheck = [];
						$scope.smsCheck = [];
						$scope.viewSubmittalData = [];
						var expansionData = [];
						var qurydata = [];
						var queryId = null;
						
						/*----------Pagination Details---------*/
						$scope.pageNumber = 1;
						$scope.pageSize = 10;
						
						// Retrive Queries
						var savedQueryNames = [];
						$scope.list_categories = null;
						
						$scope.onload = function() {
							if($location.search().currentLoginUserId){
								 $timeout(function() {//wait for some time to redirect to another page
									 $scope.onloadFun();
								 }, 400);
								}else{
									$scope.onloadFun();
								}
						};
						$scope.locblur = function(){
						var ab = $("#autoLocation").val();
						
						if(ab == "" || ab == undefined || ab == null)
							{
							$("#autoLocation").css("border-color", "#c2cad8");
							}
						else
						{
							$("#autoLocation").css("border-color", "#27a4b0");
						}

						
						}
						$scope.onloadFun = function() {
							if($location.search().paramValue != undefined){
								var paramValue= $location.search().paramValue;
								var n = paramValue.indexOf("_");
								var key = paramValue.substring(0,n);
								var value = paramValue.substring(n+1,paramValue.length);
								
								if(key == 'location'){
									$("#autoLocation").val(value);
									$scope.location = value;
								}
								if(key =='title'){
									$scope.title = "\""+value+"\"";
								}
								if(key == 'keyskills'){
									$scope.keyskills = value;
								}
								$scope.Created = {
										endDate : moment(),
										startDate : moment().subtract(6, 'year')
									};
								
								SearchCandidate();
								dispalyTable();
								
								 }
							/* if($rootScope.navigationFromOld){
								 $scope.Created = {
											endDate : moment(),
											startDate : moment().subtract(12, 'month')
										};
								 SearchCandidate();
									dispalyTable();
									$rootScope.navigationFromOld = null;
							}*/
							if($location.search().title != undefined || $location.search().location != undefined || $location.search().keySkills != undefined){
								var critiriaFromOld = true;
							}
							if($rootScope.candidateEditedId){
								var editCandidateBackButton = true;
								$rootScope.candidateEditedId = null;
							}
							var location  = $location.search();
							if(newCandidateAddedResponse || editCandidateAddedResponse || critiriaFromOld || editCandidateBackButton && !searchTogglebutton){
								/*$scope.keywords = $scope.$storage.keywords;
								$scope.keyskills = $scope.$storage.keyskills;
								$scope.title = $scope.$storage.title;
								$scope.location = $scope.$storage.location;
								$scope.state = $scope.$storage.state;
								$scope.visatype = $scope.$storage.visatype;
								$scope.firstname = $scope.$storage.firstname;
								$scope.lastname = $scope.$storage.lastname;
								$scope.email = $scope.$storage.email;
								$scope.LastUpdated = $scope.$storage.LastUpdated;
								$scope.Created = $scope.$storage.Created;
								$scope.phone = $scope.$storage.phone;
								$scope.candidateid = $scope.$storage.candidateid;
								$scope.education = $scope.$storage.education;
								$scope.minexp = $scope.$storage.minexp;
								$scope.maxexp = $scope.$storage.maxexp;
								*/
								
								$scope.defaultAllTimeDate = {
										startDate :  moment().subtract(6, 'year'),
										endDate:  moment()
								}
								if(critiriaFromOld){
									$scope.newSearch={};
									if($location.search().title != undefined){
										$scope.newSearch.title = '"'+$location.search().title+'"';
										$scope.newSearch.created = $scope.defaultAllTimeDate;
									}
									if($location.search().location != undefined){
										$scope.newSearch.city = $location.search().location;
										$scope.newSearch.created = $scope.defaultAllTimeDate;
									}
									if($location.search().keySkills != undefined){
										$scope.newSearch.keySkills = '"'+$location.search().keySkills+'"';
										$scope.newSearch.created = $scope.defaultAllTimeDate;
									}
									var newSearch = $scope.newSearch;
									var pageNumber = 1;
									var pageSize = 10;
									$scope.$storage.pageNumber = pageNumber;
									$scope.$storage.pageSize = pageSize;
									$scope.$storage.searchFields = newSearch;
								}
								
								if(newCandidateAddedResponse){
									$scope.defaultTodayDate = {
											endDate : moment(),
											startDate : moment()};
								var newSearch = {
										"created" : $scope.defaultTodayDate
								}
								var pageNumber = 1;
								var pageSize = 10;
								$scope.$storage.searchFields = newSearch;
								}
								if(editCandidateAddedResponse || editCandidateBackButton && !searchTogglebutton){ 
									var newSearch = $scope.$storage.searchFields;
									var pageNumber = $scope.$storage.pageNumber;
									var pageSize = $scope.$storage.pageSize;
								}
								 
								$scope.searchTable = true;
								$scope.showGridTable = true;
								
								searchData.candidateSearch(newSearch, pageNumber, pageSize).then(function(response){
									var data = response.data;
									dispalyTable();
									$scope.candidate.candidateBsTableControl.options.data =data; 
									$scope.candidate.candidateBsTableControl.options.pageNumber =$scope.$storage.pageNumber;
									$scope.candidate.candidateBsTableControl.options.pageSize =$scope.$storage.pageSize;
									$scope.candidate.candidateBsTableControl.options.totalRows =data[0].totalRecords;
									
								});
								if(newCandidateAddedResponse){
									$.growl.success({title : "success !",message : "New candidate has been added successfully"});
									}
								/*	if(editCandidateAddedResponse){
										$.growl.success({title : "success !",message : "candidate updated successfully"});
										}*/
								$scope.searchForm = false;
								$scope.searchToggle = true;
								$scope.searchBtn = true;
								$scope.advancedSearchRow = false;
								$scope.searchRecords = true;
								newCandidateAddedResponse =false;
								editCandidateAddedResponse =false;
							} 
							
							$scope.getSavedNamedQueries();
						}

						$scope.getSavedNamedQueries = function(){
							var response = $http.get('searchResume/getSavedQueryNames');
							response.success(function(data, status, headers,
									config) {
								for (var i = 0; i < data.length; i++) {
									var savequeryobj = data[i];
									if((savequeryobj.queryName)!=undefined)
									savedQueryNames.push(savequeryobj);
								}
								$scope.list_categories = savedQueryNames;
							});
							response.error(function(data, status, headers, config){
			        			  if(status == constants.FORBIDDEN){
			        				location.href = 'login.html';
			        			  }else{  			  
			        				$state.transitionTo("ErrorPage",{statusvalue  : status});
			        			  }
			        		  });
						}
						
						$scope.removeQuery = function(e, value, row, index) {

							queryId = $scope.candidate_search_id;
							if (queryId != undefined) {
								$mdDialog
										.show({
											controller : DialogController,
											templateUrl : 'views/dialogbox/deletequery.html',
											parent : angular
													.element(document.body),
											targetEvent : e,
										});
							}
						}
						function DialogController($scope,$mdDialog) {
					    	  $scope.cancel = function() {
					    	    $mdDialog.cancel();
					    	  };
					    	  
					    	  $scope.deleteQuery = function() {
					    		  
					    		  var response = $http.get('searchResume/deleteSavedQuery?queryId='+queryId);
					   	          response.success(function(data, status, headers, config) {
//					   	        	location.reload();
					   	        	 $state.go($state.current, {}, {reload: true});
					   	  		   });
					   	          
					   	       response.error(function(data, status, headers, config){
				        			  if(status == constants.FORBIDDEN){
				        				location.href = 'login.html';
				        			  }else{  			  
				        				$state.transitionTo("ErrorPage",{statusvalue  : status});
				        			  }
				        		  });
					   	  		
						    	    $mdDialog.hide();
						    	  };
					    	}
						// onchange function for saved queries
						$scope.SavedQueries = function() {
							$(".underlay").show();
							var quryname = $scope.list_category;
							if(quryname==null){
								$scope.advancedSearch = false;
								$scope.reset();
							}
							else{
								queryNameAutopopulate = quryname;
							$scope.advancedSearch = true;
							qurydata = [];
							var quryname = $scope.list_category;
							var response = $http
									.get('searchResume/getSearchQueries?queryname='
											+ quryname);
							response
									.success(function(data, status, headers,
											config) {
										$(".underlay").hide();
										qurydata = data;
										$scope.keyskills = qurydata[0].keySkill;
										$scope.keywords = qurydata[0].resumeTextQuery;
										$scope.title = qurydata[0].title;
										$scope.firstname = qurydata[0].firstName;
										$scope.lastname = qurydata[0].lastName;
										$scope.email = qurydata[0].email;
										$scope.phone = parseInt(qurydata[0].phoneNumber);
										$scope.location = qurydata[0].city;
										$scope.state = qurydata[0].states;
										$scope.visatype = qurydata[0].visaStats;
										$scope.candidate_search_id = qurydata[0].candidateSearchId;
										var fromDate = qurydata[0].startDate;
										var toDate = qurydata[0].endDate;
										$("#autoLocation")
												.val(qurydata[0].city);
										$("#createdBetween").val(
												fromDate.substring(0, 10)
														+ " - "
														+ toDate.substring(0,
																10));
										 
										$scope.Created = {
											startDate : moment(fromDate).add(1,
													'day'),
											endDate : moment(toDate).add(1,
													'day')
										};
										
										 $("div[class='ranges'] li").removeClass("active");
											$("div[class='ranges'] li:contains("+dateRangeService.findDateRangeSelection(fromDate,toDate)+")").addClass("active");
										
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
						}
						
						$scope.searchCandidateWithoutSave = function(){
							var ab = $scope.phone;
							if(ab)
							{
							ab = ab.replace(/\D/g,'');
							$scope.phone = ab; 
							}
							
							
							$scope.dataFromChecked = [];
							$scope.searchCandidateCheck = [];
							$scope.selectionCheck = [];
							$scope.smsCheck = [];
							$scope.orderName = "";
							$scope.orderType = "";
							isCancel = true;
							SearchCandidate();
							if(!$scope.list_category){
								queryNameAutopopulate = null;
							}
						}

						$scope.searchCandidate = function(e, value, row, index) {
							var ab = $scope.phone;
							if(ab){
							ab = ab.replace(/\D/g,'');
							$scope.phone = ab; 
							}
							
							
							$scope.searchCandidateCheck = [];
							$scope.dataFromChecked = [];
							$scope.selectionCheck = [];
							$scope.smsCheck = [];
							$scope.orderName = "";
							$scope.orderType = "";
							$mdDialog
									.show(
											{
												controller : DialogController,
												templateUrl : 'views/candidates/savequeries.html',
												parent : angular
														.element(document.body),
												targetEvent : e,
												locals : {
													queryname : $scope.list_category,
												},
											}).then(function(answer) {
										$scope.queryName = answer;
										queryNameAutopopulate = answer;
										
										SearchCandidate();

									},function(qName){
	                       		    		queryNameAutopopulate = qName;
	                       		    });

							function DialogController($scope,$mdDialog,queryname) {
								if(!queryname || (queryname && queryname == '')){
						    		 $scope.searchMessage="If you want to save this search criteria,provide an unique name.";
						    		 
						    	 }else{
						    		 $scope.query = queryname;
						    		 $scope.searchMessage="Do you want to update this search criteria.";
						    	 }
						    	  $scope.cancel = function() {
						    		  if(queryname!=undefined || queryname!=null){
						    			  $mdDialog.cancel(queryname);
						    		  }else{
						    			  $mdDialog.hide(null);
						    		  }
						    		  if(queryname){
//						    			  alert(isCancel);
						    			  isCancel = true;
						    			  SearchCandidate();
						    		  }
						    	  };
						    	  $scope.canceldailog = function(){
						    		  $mdDialog.cancel(queryname);
						    	  };
						    	  $scope.answer = function(answer) {
						    		  
						    		  var qryNameData = [];
									  var querynameslist = [];
									  for (var i = 0; i < savedQueryNames.length; i++) {
											var querynameobj = savedQueryNames[i];
											qryNameData.push(querynameobj);
										}
						    		 for(var i = 0; i < savedQueryNames.length; i++) {
						    			 if((qryNameData[i].queryName))
						    			  querynameslist.push(qryNameData[i].queryName);
						    		 }
//						    		 alert(queryname);
						    		  if(!queryname || (queryname && queryname == '')){
						    			  //Saving query name
											 //If error from server, display message "You have exceeded maximum number of queries. Delete some queries"
											 var checkingquery = querynameslist.indexOf(answer);
											 if(querynameslist.length<10){
											 if (checkingquery != -1){
												 $scope.messg = "Please give an unique name.";
												 
											 }else{
												 if(answer){
													 $mdDialog.hide(answer);
													 
												 }else {
													 $scope.messg = "Please give an unique name.";
												 }
												 
											 }
											 }else{
												 $scope.messg = "You have exceeded maximum number of queries. Delete some queries";
											 }
										
											 
						    		  }else {
						    			//Update Query name
						    			  var qrName = qurydata[0].queryName;
						    			  var ind = querynameslist.indexOf(qrName);
						    			  delete querynameslist[ind];
						    			  var checkingquery = querynameslist.indexOf(answer);
						    			  if (checkingquery != -1){
												 $scope.messg = "Please give an unique name.";
												 
											 }else{
												 $scope.queryName = answer;
												 $mdDialog.hide(answer);
											 }
						    			  //Update Query name
						    			  //$mdDialog.hide(answer);
						    		  }
						    		  
						    		  /*if(answer!=undefined ){   
							    	    $mdDialog.hide(answer);
						    		  }*/
							    	  };
						    	}

						}
						
						
						/* Search candidates table data */
						function SearchCandidate() {
//							alert(isCancel);
							$(".underlay").show();
							if(!$('#autoLocation').val()){
								$scope.location = null;
							}
							if ($scope.advancedSearch = true) {

								$scope.advancedSearch = false;

								$scope.searchTable = true;
								$(".underlay").show();
					    		$timeout(function() {
									blockUI.stop();
									$scope.showGridTable = true;
								}, 1000);

								

								var minexp = $scope.minexp;
								var maxexp = $scope.maxexp;
								var experience = '';
								if (minexp != null && maxexp != null) {
									experience = minexp + " To " + maxexp;
								}

								var minsal = $scope.minsal;
								var maxsal = $scope.maxsal;
								var compensation = '';
								if (minsal != null && maxsal != null) {
									compensation = minsal + " To " + maxsal;
								}
								
								$scope.$storage.keywords =$scope.keywords;
								$scope.$storage.keyskills = $scope.keyskills;
								$scope.$storage.title = $scope.title;
								$scope.$storage.location = $scope.location;
								$scope.$storage.state = $scope.state;
								$scope.$storage.visatype = $scope.visatype;
								$scope.$storage.firstname = $scope.firstname;
								$scope.$storage.lastname = $scope.lastname;
								$scope.$storage.email = $scope.email;
								$scope.$storage.LastUpdated = $scope.LastUpdated;
								$scope.$storage.Created = $scope.Created;
								$scope.$storage.phone = $scope.phone;
								$scope.$storage.candidateid = $scope.candidateid;
								$scope.$storage.education = $scope.education;
								$scope.$storage.minexp = $scope.minexp;
								$scope.$storage.maxexp = $scope.maxexp;
								$scope.searchFields = {
									"resumeTextQuery" : $scope.keywords,
									"keySkill" : $scope.keyskills,
									"title" : $scope.title,
									"city" : $scope.location,
									"states" : $scope.state,
									"visaStats" : $scope.visatype,
									"firstName" : $scope.firstname,
									"lastName" : $scope.lastname,
									"email" : $scope.email,
									"lastUpdated" : $scope.LastUpdated,
									"created" : $scope.Created,
									"phoneNumber" : $scope.phone,
									"candidateId" : $scope.candidateid,
									"education" : $scope.education,
									"workExperince" : experience,
									"compensation" : compensation,
//									"queryName" : ($scope.queryName == undefined || $scope.queryName == null)?$scope.list_category :$scope.queryName,
									"queryId" : $scope.candidate_search_id,
									"fieldName" : $scope.orderName,
									"sortName"  : $scope.orderType

								};
								if($scope.queryName){
								$scope.searchFields.queryName = $scope.queryName;
								}else if($scope.list_category && $scope.list_category != ''){
									$scope.searchFields.queryName = $scope.list_category;
								}
								if(isCancel){
									$scope.searchFields.isCancelBtnClicked = true;
								}
//								alert( $scope.queryName+' \t '+$scope.list_category+"\n value:"+$scope.searchFields.queryName);
//								var dateRangeActiveValue = $(".ranges").children("ul").children("li[class='active']").text();
//								alert($('#createdBetween').val());
								
								if($('#createdBetween').val() && $('#createdBetween').val()!=''){
									$scope.searchFields.created.startDate = $('#createdBetween').val().split(' ')[0];
									$scope.searchFields.created.endDate = $('#createdBetween').val().split(' ')[2];
								}
								$scope.$storage.searchFields = $scope.searchFields;
//								alert(JSON.stringify($scope.$storage.searchFields));
//								alert($scope.pageNumber+"\n"+$scope.pageSize+"\n"+JSON.stringify($scope.searchFields));
								var response = $http.post("searchResume/getSearchResumes?pageNumber="+$scope.pageNumber+"&pageSize="+$scope.pageSize,$scope.searchFields);
								response.success(function(data, status,headers, config) 
										{
									isCancel = false;
									$scope.$storage.pageNumber = $scope.pageNumber;
									$scope.$storage.pageSize = $scope.pageSize;
									candidateData = [];
									candidateData.push(data);
									$scope.queryName = null;
									$scope.list_category = null;
									$scope.searchFields.queryName = null;
									$scope.searchFields.queryId = null;
									dispalyTable();
									//alert(JSON.stringify(data));
									$scope.candidate.candidateBsTableControl.options.data =data; 
									$scope.candidate.candidateBsTableControl.options.pageNumber =$scope.$storage.pageNumber;
									$scope.candidate.candidateBsTableControl.options.pageSize =$scope.$storage.pageSize;
									$scope.candidate.candidateBsTableControl.options.totalRows =data[0].totalRecords;
									
									
									$(".underlay").hide();
							      });
								response.error(function(data, status, headers, config){
				        			  if(status == constants.FORBIDDEN){
				        				location.href = 'login.html';
				        			  }else{  			  
				        				$state.transitionTo("ErrorPage",{statusvalue  : status});
				        			  }
				        		  });
								
								$scope.searchForm = false;
								$scope.searchToggle = true;
								$scope.searchBtn = true;
								$scope.advancedSearchRow = false;
								$scope.searchRecords = true;
								$(".underlay").hide();
							}

							if ($scope.advancedSearch == false) {
								$scope.advancedSearch = true;
							}

							// }
						}
						
						

						/* Set table and pagination */
						function dispalyTable() {
							//alert(JSON.stringify($scope.candidate));
										$scope.candidate.candidateBsTableControl = {
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
												detailView : true,
												maintainSelected : true,
												columns : [
														{
															field : 'state',
															checkbox : true,
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
														{
															field : 'resume',
															title : 'Resume',
															align : 'center',
															sortable : false,
															events : window.candidateResumeEvents,
															formatter : resumeFormatter
														},
														{
															field : 'actions',
															title : 'Actions',
															align : 'left',
															sortable : false,
															events : window.searchoperateEvents,
															formatter : actionFormatter
														} ],
														
														
											   onPageChange: function (number, size) {
												   $(".underlay").show();
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
														$scope.candidate.candidateBsTableControl.options.responseHandler(data); 
														$scope.candidate.candidateBsTableControl.options.data =data; 
														$scope.candidate.candidateBsTableControl.options.pageNumber =number;
														$scope.candidate.candidateBsTableControl.options.pageSize =size;
														$scope.candidate.candidateBsTableControl.options.totalRows =data[0].totalRecords;
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
												 onSort: function (name, order) {
													 $(".underlay").show();
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
//													 alert(JSON.stringify(searchFileds));
													 $scope.$storage.searchFields.isCallFromPagination = true;
													 var response = $http.post("searchResume/getSearchResumes?pageNumber="+$scope.$storage.pageNumber+"&pageSize="+ $scope.$storage.pageSize,searchFileds);
														response.success(function(data, status,headers, config) 
																{
															if(data){
															$scope.$storage.searchFields.isCallFromPagination = false;
//															alert(JSON.stringify(data));
															candidateData = [];
															candidateData.push(data);
															dispalyTable();
															$scope.candidate.candidateBsTableControl.options.data =data; 
															$scope.candidate.candidateBsTableControl.options.pageNumber =$scope.$storage.pageNumber;
															$scope.candidate.candidateBsTableControl.options.pageSize =$scope.$storage.pageSize;
															$scope.candidate.candidateBsTableControl.options.totalRows =data[0].totalRecords;
//															 $timeout(function() {//wait for some time to redirect to another page
//																 $('.fixed-table-container thead th .both').css("background-image","url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABMAAAATCAYAAAByUDbMAAAAZ0lEQVQ4y2NgGLKgquEuFxBPAGI2ahhWCsS/gDibUoO0gPgxEP8H4ttArEyuQYxAPBdqEAxPBImTY5gjEL9DM+wTENuQahAvEO9DMwiGdwAxOymGJQLxTyD+jgWDxCMZRsEoGAVoAADeemwtPcZI2wAAAABJRU5ErkJggg==)");
//															 }, 1000);
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
												onExpandRow : function(index,row, $detail) {
													var candidateId = row.id;
													var response = $http.post('searchResume/getSubmittalsInfo?candidateId='+ candidateId);
													response.success(function(data,status,headers,config) {
																expansionData = [];
																for (var i = 0; i < data.length; i++) {
																	var submittalObj = data[i];
																	expansionData.push(submittalObj);
																}
																var tableres = '';
																if (expansionData != '') {
																	tableres = '<table class="innertable">'
																			+ '<tr><th>JobOrder Id</th><th>Submittal Id</th><th>Created On</th><th>Created By</th><th>Status</th></tr>'
																	for (var i = 0; i < expansionData.length; i++) {
																		tableres += '<tr><td>'
																				+ expansionData[i].jobOrderId
																				+ '</td>'
																				+ '<td>'
																				+ expansionData[i].submittalId
																				+ '</td>'
																				+ '<td>'
																				+ expansionData[i].createdOn
																				+ '</td>'
																				+ '<td>'
																				+ expansionData[i].createdBy
																				+ '</td>'
																				+ '<td>'
																				+ expansionData[i].status
																				+ '</td></tr>'
																	}
																} else {
																	tableres = '<table class="innertable">'
																			+ '<tr><th>JobOrder Id</th><th>Submittal Id</th><th>Created By</th><th>Status</th><th>Created On</th></tr>'
																			+ '<tr><td colspan="5" align="center">No Submittals Found</td></tr>'
																}
																tableres += '</table>';
																$detail
																		.html(tableres);
															});
													response.error(function(data, status, headers, config){
									        			  if(status == constants.FORBIDDEN){
									        				location.href = 'login.html';
									        			  }else{  			  
									        				$state.transitionTo("ErrorPage",{statusvalue  : status});
									        			  }
									        		  });
												},

												/* Check box action */
												onCheck : function(row) {
													$(".mesgerror").hide();
													 var id= row.id;
								                   	  	var obj = {"id" : id,
								                   			  "email" : row.email+';',
								                   			"phoneNumber" : row.phoneNumber}
								                   	  $scope.searchCandidateCheck.push(obj);
													/*$scope.dataFromChecked.push(row.email+ ';');
													$scope.selectionCheck.push(row.id);
													$scope.smsCheck.push(row.phoneNumber);*/
												},
												onUncheck : function(row) {
													/*var unCheckedEmail = row.email;
													var phone = row.phoneNumber;
													var unCheckedId = row.id;
													var unCheckIndex = $scope.dataFromChecked.indexOf(unCheckedEmail);
													$scope.dataFromChecked.splice(unCheckIndex,	1);
													var selUnCheckIndex = $scope.selectionCheck.indexOf(unCheckedId);
													$scope.selectionCheck.splice(selUnCheckIndex,	1);
													var smsUnCheckIndex = $scope.smsCheck.indexOf(phone);
													$scope.smsCheck.splice(smsUnCheckIndex,	1);*/
													$.each($scope.searchCandidateCheck, function (i, map) {
														if(map.id == row.id){
															var unCheckIndex = $scope.searchCandidateCheck.indexOf(map);
															$scope.searchCandidateCheck.splice(unCheckIndex, 1);
														}
											    		});
													
												},
												onCheckAll : function(rows) {
													$(".mesgerror").hide();
													
													$.each(rows, function (i, row) {
														if($scope.searchCandidateCheck.length>0){
															var flag=false;
															$.each($scope.searchCandidateCheck, function (i, map) {
														if(map.id == row.id ){
														flag=true;
														}
											    		})
											    		if(!flag){
											    			var obj = {"id" :  row.id,
								                        			  "email" : row.email+';',
								                        			  "phoneNumber" : row.phoneNumber
								                        			  };
											    			$scope.searchCandidateCheck.push(obj);
											    		}
														}else{
															var obj = {"id" :  row.id,
								                        			  "email" : row.email+';',
								                        			  "phoneNumber" : row.phoneNumber
								                        			   };
															$scope.searchCandidateCheck.push(obj);
														}
													});
													
													
													/*for (var i = 0; i < rows.length; i++) {
														if($.inArray(rows[i].id, $scope.selectionCheck) === -1){
															$scope.dataFromChecked.push(rows[i].email+ ';');
															$scope.selectionCheck.push(rows[i].id);
															$scope.smsCheck.push(rows[i].phoneNumber);
														}
														
													}*/
												},
												onUncheckAll : function(rows) {
													
													for (var i = 0; i < rows.length; i++) {
														 
														for(var j=0;j < $scope.searchCandidateCheck.length;j++){
															if($scope.searchCandidateCheck[j].id == rows[i].id ){
																var unCheckIndex = $scope.searchCandidateCheck.indexOf($scope.searchCandidateCheck[j]);
																$scope.searchCandidateCheck.splice(unCheckIndex,	1);
															}
														}
													
													}
													
													/*for (var i = 0; i < rows.length; i++) {
														var unCheckedEmail = rows[i].email;
														var phone = rows[i].phoneNumber;
														var uncheckedId = rows[i].id;
														if($.inArray(rows[i].id, $scope.selectionCheck) !== -1){
															var unCheckIndex = $scope.dataFromChecked.indexOf(unCheckedEmail);
															$scope.dataFromChecked.splice(unCheckIndex,	1);
															var unSelCheckIndex = $scope.selectionCheck.indexOf(uncheckedId);
															$scope.selectionCheck.splice(unSelCheckIndex,	1);
															var smsUnCheckIndex = $scope.smsCheck.indexOf(phone);
															$scope.smsCheck.splice(smsUnCheckIndex,	1);
														}
													}*/
												},
												responseHandler : function(res) {
													
													 $.each(res, function (i, row) {
														 
													    	$.each($scope.searchCandidateCheck, function (i, map) {
													    		if(map.id == row.id ){
													    			row.state = true;
													    		}
													    		
													    	})
													    });
													
												   /* $.each(res, function (i, row) {
												        row.state = $.inArray(row.id, $scope.selectionCheck) !== -1;
												    });*/
												    return res;
												},
											}
										};
										
										// for resume Dialogue box
										function resumeFormatter(value, row,
												index) {
											return ['<a class="view actionIcons" title="View details" flex-gt-md="auto"><i class="fa fa-search" style="font-size:13px;"></i></a>']
											.join('');
											
											/*return [ '<a class="info actionIcons" title="View Resume"><i class="fa fa-file-word-o"></i></a>' ]
													.join('');*/
										}

										window.candidateResumeEvents = {
											/*'click .info' : function(e, value,
													row, index) {
												var candidateId = row.id;
												var docStats = "others";
												var candidateresumeData=[];
												var objresume = null;
												var response = $http.get('searchResume/resumeByCandidateId?candidateId='
														+ candidateId+'&docStats='+docStats);
												response.success(function(data, status,headers, config) 
														{
													var candresume = JSON.stringify(data);
													 objresume = JSON.parse(candresume);
													$scope.CandidateText = objresume.resumeContent;
													if($scope.keyskills && $scope.keywords){
													$scope.CandidateKeywords = $scope.keyskills +","+ $scope.keywords;
													}else if($scope.keywords){
														$scope.CandidateKeywords = $scope.keywords;
													}else{
														$scope.CandidateKeywords = $scope.keyskills;
													}
													if($scope.CandidateKeywords){
														$scope.CandidateKeywords = $scope.CandidateKeywords.toLowerCase();
													}
													
													$mdDialog
															.show({
																controller : ResumeDialogController,
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
														if($scope.keyskills && $scope.keywords){
															data.keyskillsAndKeyWords = $scope.keyskills +","+ $scope.keywords;
															}else if($scope.keywords){
																data.keyskillsAndKeyWords =$scope.keywords;
															}else{
																data.keyskillsAndKeyWords = $scope.keyskills;
															}
															if(data.keyskillsAndKeyWords){
																data.keyskillsAndKeyWords = data.keyskillsAndKeyWords.toLowerCase();
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
														}else{
															
															 $.growl.notice({title : "warning!",message : "Candidate Resume not found"+ "#"+ row.id});
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
										/* Table button action formatters */

										function actionFormatter(value, row,
												index) {
											
											var src = row.hot ? "resources/img/star-red.png": "resources/img/star-gray.png";
											var blocksrc = row.block ? "resources/img/star-block.png": "resources/img/star-unblock.png";
											var hotTitle = row.hot ? "Remove from hotlist": "Add to hotlist";
											var blockTitle = row.block ? "Remove from blacklist": "Add to blacklist";
											
											if($rootScope.rsLoginUser.userRole == constants.Recruiter || $rootScope.rsLoginUser.userRole == constants.HR
													|| $rootScope.rsLoginUser.userRole == constants.Manager || $rootScope.rsLoginUser.userRole == constants.ADM){
												return [
												'<a  ><img class="hotlist actionIcons" id = "hotimg'+ row.id+ '" flex-gt-md="auto" title="'	+ hotTitle+ '" width="12" height="12" src='+ src + ' /></a>',
												'<a class="blacklist actionIcons" title="'+ blockTitle+'" flex-gt-md="auto"><img id = "blockimg'+ row.id+ '" width="12" height="12" src='+ blocksrc+ ' /></a>',
												//'<a class="view actionIcons" title="View details" flex-gt-md="auto"><i class="fa fa-search" style="font-size:12px;"></i></a>',
												'<a class="email actionIcons" title="'+ row.email+ '" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>',
												'<a class="download actionIcons" title="Download resume" ><i class="fa fa-download" style="font-size:12px;"></i></a> ',
												'<a class="edit actionIcons" title="Edit candidate"><i class="fa fa-edit" style="font-size:12px;"></i></a>' ]
												.join('');
											}else{
											return [
													($rootScope.rsLoginUser.userRole != constants.ATS_Executive)? '<a  ><img class="hotlist actionIcons" id = "hotimg'+ row.id+ '" flex-gt-md="auto" title="'	+ hotTitle+ '" width="12" height="12" src='+ src + ' /></a>':'',
													($rootScope.rsLoginUser.userRole != constants.ATS_Executive)?'<a class="blacklist actionIcons" title="'+ blockTitle+'" flex-gt-md="auto"><img id = "blockimg'+ row.id+ '" width="12" height="12" src='+ blocksrc+ ' /></a>':'',
													//'<a class="view actionIcons" title="View details" flex-gt-md="auto"><i class="fa fa-search" style="font-size:12px;"></i></a>',
													($rootScope.rsLoginUser.userRole != constants.ATS_Executive)?'<a class="email actionIcons" title="'+ row.email+ '" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>':'',
													($rootScope.rsLoginUser.userRole != constants.ATS_Executive)?'<a class="download actionIcons" title="Download resume" ><i class="fa fa-download" style="font-size:12px;"></i></a> ':'',
													'<a class="edit actionIcons" title="Edit candidate"><i class="fa fa-edit" style="font-size:12px;"></i></a>',
													($rootScope.rsLoginUser.userRole != constants.ATS_Executive)?'<a class="candidateRemove actionIcons displayInfo" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>':'' ]
													.join('');
											}
										}

										/* Table button actions functionalities */
										window.searchoperateEvents = {

											/* Add Remove to/from blacklist */
											'click .blacklist' : function(e,value, row, index) {
												if(row.hot){
													 $.growl.notice({title : "warning!",message : "Candidate already in Hotlist make it Unhot for Blocklist"+ "#"+ row.id});
												}else{
												$scope.reason = "";
												$mdDialog.show({
																	controller : DialogController,
																	templateUrl : 'views/dialogbox/hotblock.html',
																	parent : angular
																			.element(document.body),
																	locals : {
																		rowData : row,
																	},
																	targetEvent : e,
																	clickOutsideToClose : true,
																}).then(function(answer) {
																	var blocklist = {
																		"candidateId" : row.id,
																		"reason" : answer
																	}
																	$(".underlay").show();
																var response = $http.post("searchResume/saveBlockList",blocklist);
																	response.success(function(data,	status,	headers,config) {
																		 angular.element("#searchControllerId").scope().searchCandidateWithoutSave();
																	
																		 $(".underlay").hide();
																		 $.growl.success({title : "update !",message : data.statusMessage+ "#"+ row.id});
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
											},

											/* Add Remove to/from hotlist */
											'click .hotlist' : function(e,value, row, index) {
												if(row.block){
													 $.growl.notice({title : "warning!",message : "Candidate already in Block list make it UnBlock for Hotlist"+ "#"+ row.id});
												}else{
												$mdDialog.show(	{
																	controller : DialogController,
																	templateUrl : 'views/dialogbox/hotblock.html',
																	parent : angular
																			.element(document.body),
																	targetEvent : e,
																	locals : {
																		rowData : row,
																	},
																	clickOutsideToClose : true,
																}).then(function(answer) {
																	var hotlist = {
																		"candidateId" : row.id,
																		"reason" : answer
																	}
																	$(".underlay").show();
														var response = $http.post("searchResume/saveHotComment",hotlist);
														response.success(function(data,status,headers,config) {
															 angular.element("#searchControllerId").scope().searchCandidateWithoutSave();
														
															 $(".underlay").hide();
															 $.growl.warning({title : "update !",message : data.statusMessage+ "#"+ row.id});
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
											},

											/* View Resume details */
										/*	'click .view' : function(e,value, row, index) {
												$(".underlay").show();
												var candidateId = row.id;
												var response = $http
														.get('searchResume/viewCandidateDetails?candidateId='
																+ candidateId);
												response.success(function(data, status,headers, config) 
														{
													if($scope.keywords){
														data.keyskillsAndKeyWords = $scope.keyskills +","+ $scope.keywords;
														}else{
															data.keyskillsAndKeyWords = $scope.keyskills;
														}
														if(data.keyskillsAndKeyWords){
															data.keyskillsAndKeyWords = data.keyskillsAndKeyWords.toLowerCase();
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
													$(".underlay").hide();
														});
												response.error(function(data, status, headers, config){
								        			  if(status == constants.FORBIDDEN){
								        				location.href = 'login.html';
								        			  }else{  			  
								        				$state.transitionTo("ErrorPage",{statusvalue  : status});
								        			  }
								        		  });
											},*/
											'click .email' : function(e,value, row, index) {
												var email = row.email;

												mailService.sendMail(email, false);

											},
											'click .download' : function(e,value, row, index) {
												
												var candidateId = row.id;
												var docStats = "others"
												var candidateresumeData=[];
												var objresume = null;
												$window.location = 'searchResume/resumeByCandidateId/'+candidateId+'/'+docStats+'/'+constants.ORGDOC;
												
											
											},
											'click .edit' : function(e,value, row, index) {
												
												$rootScope.candidateEditedId = row.id;												
//												$state.transitionTo("EditCandidate",{candidateId : row.id, page:"search"});
												
												
//												  $rootScope.MissingCandidateEditId = row.id;	
						                    	   var url = $state.href("EditCandidate",{candidateId : row.id, page:"search"});
						                    	   window.open(url,'_blank');

											},

											'click .candidateRemove' : function(e,value, row, index) {
												
												
												
												var candidateId = row.id;
												var response = $http.post('searchResume/getSubmittalsInfo?candidateId='+ candidateId);
												response.success(function(data,status,headers,config) {
													if(data){
														 $("#deleteErrMsgDivId").html("The selected candidate can't be delete, because it is already submitted for a job order");
														 $("#deleteErrInfo").show();
														
													}else{
														$mdDialog.show(	{
															controller : DialogController,
															templateUrl : 'views/dialogbox/removecandiatedialogbox.html',
															parent : angular
																	.element(document.body),
															targetEvent : e,
															locals : {
																rowData : row,
															},
															clickOutsideToClose : true,
														})
												.then(function(answer) {
															var deleteReason = {
																"candidateId" : row.id,
																"reason" : answer
															}
															$(".underlay").show();
															
															var candidateId = row.id;
															var response = $http.post('searchResume/deleteCandidate',deleteReason);
															response.success(function(data, status,headers, config) 
																	{
																$(".underlay").hide();
																		if (data.statusCode == 200) {
																			 angular.element("#searchControllerId").scope().searchCandidateWithoutSave();

																			$.growl	.notice({title : "Delete !",message : data.statusMessage+ "#"+ row.id});
																		} else {
																	$.growl.error({title : "Delete !",message : data.statusMessage+ "#"+ row.id	});
																		}
																	});
															response.error(function(data, status, headers, config){
																$(".underlay").hide();
											        			  if(status == constants.FORBIDDEN){
											        				location.href = 'login.html';
											        			  }else{  			  
											        				  $state.transitionTo("ErrorPage",{statusvalue  : status});
																		$.growl	.error({title : "Error !",message : data+ "#"+ row.id});
											        			  }
											        		  });
														
														}, function() {
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
										};
										function DialogController($scope,
												$mdDialog, rowData) {
											$scope.row = rowData;
//											alert(JSON.stringify($scope.row));
											var candresume = JSON.stringify(rowData);
											 var viewObjResume = JSON.parse(candresume);
											$scope.viewCandidateText = viewObjResume.resumeContent;
											
//											alert(rowData.keyskillsAndKeyWords);
											
											
											var viewStrKeywords = rowData.keyskillsAndKeyWords;
											
											
//											alert(viewStrKeywords);
											if(viewStrKeywords != null || viewStrKeywords != undefined){
												viewStrKeywords = viewStrKeywords.replace(/[*+:]/g, ",").replace(/[?]/g, ",").replace(/[!]/g, ",");
//												alert(viewStrKeywords);
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
											
//											alert(viewStrKeywordsArray);
											
											if(viewStrKeywordsArray){
												for(var i=0;i<viewStrKeywordsArray.length;i++){
//													alert(viewStrKeywordsArray[i]+' siva');
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
//												alert(viewStrKeywordsArray[i]);
												if(viewStrKeywordsArray[i] == constants.Dotnet){
													viewStrKeywordsArray[i] = constants.EscDotNet;
												}
											}
											
//											alert(viewStrKeywordsArray);
												viewStrKeywords = viewStrKeywordsArray.join(',');
												viewStrKeywords = viewStrKeywords.replace(/[,]+/g, ",")
												viewStrKeywords = viewStrKeywords.replace(/^,|,$/g,'');
//												alert(viewStrKeywords);
												
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

											$scope.answer = function(answer) {
												if(answer!=undefined){
													 $mdDialog.hide(answer);
													 
												 }else{
													 $scope.msg = "please give reason."
												 }
												//$mdDialog.hide(answer);
											};

										}

										/*function ResumeDialogController($scope,
												$mdDialog, CandidateText,
												CandidateKeywords) {
											$scope.CandidateText = CandidateText;
//											alert(CandidateKeywords);
											var strKeywords = CandidateKeywords;
											if(strKeywords != null || strKeywords != undefined){
											strKeywords = strKeywords.replace(/["']/g, "").replace(/[NOT]/g, "(").replace(/[(]/g, "").replace(/[)]/g, "");
											var strKeyWordsArray = strKeywords.split(/[ \(,\)]+/);
											
											var indexOfAnd = -1;
											
											if(strKeyWordsArray){
												for(var i=0;i<strKeyWordsArray.length;i++){
													indexOfAnd = strKeyWordsArray.indexOf("and");
													if(indexOfAnd != -1){
														strKeyWordsArray.splice( indexOfAnd, 1 );
													}
														indexOfAnd = strKeyWordsArray.indexOf("or");
														if(indexOfAnd != -1){
														strKeyWordsArray.splice( indexOfAnd, 1 );
														}
														indexOfAnd = strKeyWordsArray.indexOf("undefined");
														if(indexOfAnd != -1){
														strKeyWordsArray.splice( indexOfAnd, 1 );
														}
												}
											}
											
											
												strKeywords = strKeyWordsArray.join(',');
//											alert(strKeywords);
											$scope.CandidateKeywords = strKeywords;
											}
											else{
											
											$scope.CandidateKeywords = strKeywords;
											}
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
										
										/* Mail sending on check box action */
										$scope.sendEmails = function() {
											if($scope.searchCandidateCheck.length>0){
												$(".mesgerror").hide();
												var checkedData = '';
												$.each($scope.searchCandidateCheck, function (i, map) {
							    					checkedData += map.email;
										    		})  
												mailService.sendMail(checkedData, true);
											}
											else{
												/*alert("Please select candidates to send Mail");*/
												$(".mesgerror").show();
											}
										}

										$scope.sendSms = function(e){
//											var checkedArray = angular.copy($scope.smsCheck);
											//alert($scope.smsCheck.length);
											if($scope.searchCandidateCheck.length>0){
												$(".mesgerror").hide();
												var checkedArray = [];
								  				$.each($scope.searchCandidateCheck, function (i, map) {
								  					checkedArray.push(map.phoneNumber);
											    		})  
												smsService.sendSms(e, checkedArray);
											}
											else{
												/*alert("Please select Candidates to Send SMS ");*/
												$(".mesgerror").show();
											}
										}
						}
						
						function getDate(fulldate){
							var month = ( '0' + (fulldate.getMonth()+1) ).slice( -2 );
							var date = ( '0' + (fulldate.getDate()) ).slice( -2 );
							var year = fulldate.getFullYear();
							var totaldate = year + "-" + month + "-" + date;
							return totaldate;
						}
						 
						/* RESET Fields */
					$scope.reset = function(form) {
						$(".underlay").hide();
						$scope.candidate_search_id = null;
						delete $scope.list_category;
						delete $scope.keywords;
						delete $scope.keyskills;
						delete $scope.title;
						delete $scope.location;
						delete $scope.state;
						delete $scope.visatype;
						delete $scope.firstname;
						delete $scope.lastname;
						delete $scope.email;
						delete $scope.LastUpdated;
						delete $scope.phone;
						delete $scope.candidateid;
						delete $scope.education;
						$("#autoLocation")
						.val(null);
						
						 $("div[class='ranges'] li").removeClass("active");
							$("div[class='ranges'] li:contains('Last 1 year')").addClass("active");
							$scope.Created = { endDate: moment(), startDate:moment().subtract(12, 'month')};
							
							var currentDate = new Date();
							var oneMonthBeforeDate = moment().subtract(12, 'month').toDate();
							var finaldate = getDate(oneMonthBeforeDate) + " - " + getDate(currentDate);
							$("input[type='daterange']").val(finaldate);
						
						delete $scope.minexp;
						delete $scope.maxexp;
						delete $scope.minsal;
						delete $scope.maxsal;
						form.$setPristine();

					};
						

					});

})(angular);
