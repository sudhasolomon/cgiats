;(function(angular) {

    "use strict";
    
    angular.module('viewcandidate',['ngMaterial', 'ngMessages','ui.bootstrap','angular-highlight','jcs-autoValidate' ])
    
    .controller('ViewCandidateController',function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage,smsService, mailService){
    	$scope.$storage = $sessionStorage;
    	$scope.$on('$viewContentLoaded', function() {   
 	        // initialize core components
 	        App.initAjax();
 	    }); 
    	
    	 
        
//        	  $state.go($state.current,{},{reload:true});
//				 $scope.viewCandidatesclick();
				
		
    	
    	$scope.status = "PENDING";
    	$("#viewsource").css("display", "none");
    	$scope.sourceOnChange = function(){
    		if($scope.source == "Online Resumes" || $scope.source == "Mobile Resumes"){
    			$("#viewsource").css("display", "block");
    		}else{
    			$("#viewsource").css("display", "none");
    		}
    	}
    	
    	//$rootScope.backButton = false;
    	
    	 $scope.portalcandidate = [];
         $scope.onlinecandidate = [];
         var portalcandidateData = [];
         var onlinecandidateData = [];
    	 $scope.portalTable = false;
    	 $scope.onlineTable = false;
		 $scope.showGridTable = false;
		 $rootScope.onlinePagination = false;
		 $rootScope.pagination = false;
		 $scope.pageNumber = 1;
		 $scope.pageSize = 10;
		 $scope.orderName = "";
		 $scope.orderType = "";
		 $scope.onlinePageNumber =1;
		 $scope.onlinePageSize = 10;
		 $scope.dataFromChecked = [];
		 $scope.selectionCheck = [];
		 $scope.MapselectionCheck = [];
		 $scope.smsCheck = [];
		// Set sidebar closed and body solid layout mode
			$rootScope.settings.layout.pageContentWhite = true;
			$rootScope.settings.layout.pageBodySolid = false;
			$rootScope.settings.layout.pageSidebarClosed = false;
			
    	$scope.onload = function (){
    		if($rootScope.rsLoginUser.userRole == constants.Recruiter || $rootScope.rsLoginUser.userRole == constants.Manager || $rootScope.rsLoginUser.userRole == constants.EM
    				 || $rootScope.rsLoginUser.userRole == constants.HR || $rootScope.rsLoginUser.userRole == constants.DivisionHead || $rootScope.rsLoginUser.userRole == constants.AccountManager){
    		$scope.sources = [sourceConst.CareerBuilder,sourceConst.Monster,sourceConst.Dice,sourceConst.TechFetch];
    		}
    		if($rootScope.rsLoginUser.userRole == constants.Administrator || $rootScope.rsLoginUser.userRole == constants.DM ||
    				$rootScope.rsLoginUser.userRole == constants.ADM){
    			$scope.sources = [sourceConst.OnlineResumes,sourceConst.CareerBuilder,sourceConst.Monster,sourceConst.Dice,sourceConst.TechFetch,sourceConst.MobileResumes];
    		}
    		
    		
    		$scope.Created = {
					endDate : moment(),
					startDate : moment().startOf('month')
				};
    	$scope.daterange ={
    			today : [moment(),moment()],
    			onemonth : [moment().subtract(1, 'month'), moment()],
    			twomonths : [moment().subtract(2, 'month'), moment()],
    			threemonths : [moment().subtract(3, 'month'), moment()],
    			custom : 'custom'
    	}
    
    	  $scope.ranges = {
    			'Today' : [ new Date(), new Date() ],
    			'This Month' : [moment().startOf('month'), moment()],
    	        /*'Last 1 month': [moment().subtract(1, 'month'), moment()],*/
    	        'Last 3 months': [moment().subtract(3, 'month'), moment()],
    	        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
    	        'Last 1 year': [moment().subtract(12, 'month'), moment()],
    	        'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()]
    	        };
    	if($rootScope.candidateEditId){
			var editCandidateBackButton = true;
			$rootScope.candidateEditId = null;
		}
    	
    	if($stateParams.editCandidate || editCandidateBackButton){
    		$scope.Created = $rootScope.createdDate;
    		 $scope.source = $rootScope.source;
    		 $scope.pageNumber =  $rootScope.pageNumber;
    		 $scope.pageSize = $rootScope.pageSize;
    		$scope.viewCandidatesclick();
    	}
    	
    	}
    	
    	$scope.viewCandidates = function(){
    		 	$scope.orderName = "";
				  $scope.orderType = "";
				  $scope.pageNumber = 1;
					 $scope.pageSize = 10;
					 $scope.onlinePageNumber =1;
					 $scope.onlinePageSize = 10;
					 $scope.dataFromChecked = [];
					 $scope.selectionCheck = [];
					 $scope.smsCheck = [];
					 $scope.MapselectionCheck = [];
					 $scope.viewCandidatesclick();
				  
    	};
    	
    	 $scope.viewCandidatesclick = function(){

    		 $(".mesgerror").hide();

    		
    		 $scope.portalcandidate = [];
             $scope.onlinecandidate = [];
			 $scope.portalTable = true;
	    	 $scope.onlineTable = true;
	    	 $scope.showGridTable = true;
   		  
	    	 if($scope.source == "Online Resumes" || $scope.source == "Mobile Resumes"){
	    		 onlinecandidateData = [];
				 onlineCandidates();

				 
	    	 }else{
				 portalcandidateData = [];
				 portalCandidates();
				
				 
	    	 }
			   	 
    	 }
    	
    	 function portalCandidates(){
    		 $(".underlay").show();
//    		 alert("portal"+$scope.orderName+" $scope.orderType");
    		 $scope.$storage.pageNumber = $scope.pageNumber;
    		 $scope.$storage.pageSize = $scope.pageSize; 
    		 
    		 var startDate = new Date($scope.Created.startDate);
      		  var fromDate = ((startDate.getMonth()+1)<10?('0'+(startDate.getMonth()+1)):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?('0'+startDate.getDate()):startDate.getDate())+'-'+startDate.getFullYear();
      		  var endDate = new Date($scope.Created.endDate);
      		  var toDate = ((endDate.getMonth()+1)<10?('0'+(endDate.getMonth()+1)):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?('0'+endDate.getDate()):endDate.getDate())+'-'+endDate.getFullYear();
      		
      		if($('#createdBtwId').val() && $('#createdBtwId').val()!=''){
      			startDate = new Date($('#createdBtwId').val().split(' ')[0]);
      			fromDate = ((startDate.getMonth()+1)<10?('0'+(startDate.getMonth()+1)):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?('0'+startDate.getDate()):startDate.getDate())+'-'+startDate.getFullYear();
      			endDate = new Date($('#createdBtwId').val().split(' ')[2]);
      			toDate = ((endDate.getMonth()+1)<10?('0'+(endDate.getMonth()+1)):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?('0'+endDate.getDate()):endDate.getDate())+'-'+endDate.getFullYear();
			}
      		  
    		 var viewFields = {
					  "startDate" : fromDate,
					  "endDate" : toDate,
					  "createdBy" : $scope.source,
					  "fieldName" : $scope.orderName,
					  "sortName"  : $scope.orderType
			  }
    		  var response = $http.post('viewCandidateController/viewCandidate?pageNumber='+$scope.$storage.pageNumber+'&pageSize='+$scope.$storage.pageSize,viewFields);
    		  response.success(function(data, status, headers, config){
//    			  alert("portal "+JSON.stringify(data));
    			  $(".underlay").hide();	 
    			  	$scope.onlineTable = false;
 				 	$scope.portalTable = true;
 				 	onlinecandidateData = [];
 				 	onlineCandidateTable();
    			  portalcandidateData = [];
    			  for (var i = 0; i < data.length; i++) {
  					var candidateObj = data[i];
  					portalcandidateData.push(candidateObj);
  				}
    			portalCandidateTable();
    			if($rootScope.pagination){
    			$scope.portalcandidate.portalTableControl.options.responseHandler(data);} 
    			$scope.portalcandidate.portalTableControl.options.pageNumber =$scope.$storage.pageNumber;
  				$scope.portalcandidate.portalTableControl.options.pageSize =$scope.$storage.pageSize;
  				$scope.portalcandidate.portalTableControl.options.totalRows =data[0].totalRecords;
  				$rootScope.pagination = false;
    		  });
    		  response.error(function(data, status, headers, config){
    			  $(".underlay").hide();
      			  if(status == constants.FORBIDDEN){
      				location.href = 'login.html';
      			  }
      		  });
    		 
    	 }
    	 
    	 function onlineCandidates(){
    		 $(".underlay").show();
//    		 alert("online"+$scope.orderName+" $scope.orderType");
    		 $scope.$storage.onlinepageNumber = $scope.onlinePageNumber;
    		 $scope.$storage.onlinepageSize = $scope.onlinePageSize; 
    		 
    		 var startDate = new Date($scope.Created.startDate);
      		  var fromDate = ((startDate.getMonth()+1)<10?('0'+(startDate.getMonth()+1)):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?('0'+startDate.getDate()):startDate.getDate())+'-'+startDate.getFullYear();
      		  var endDate = new Date($scope.Created.endDate);
      		  var toDate = ((endDate.getMonth()+1)<10?('0'+(endDate.getMonth()+1)):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?('0'+endDate.getDate()):endDate.getDate())+'-'+endDate.getFullYear();
    		 
      		if($('#createdBtwId').val() && $('#createdBtwId').val()!=''){
      			startDate = new Date($('#createdBtwId').val().split(' ')[0]);
      			fromDate = ((startDate.getMonth()+1)<10?('0'+(startDate.getMonth()+1)):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?('0'+startDate.getDate()):startDate.getDate())+'-'+startDate.getFullYear();
      			endDate = new Date($('#createdBtwId').val().split(' ')[2]);
      			toDate = ((endDate.getMonth()+1)<10?('0'+(endDate.getMonth()+1)):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?('0'+endDate.getDate()):endDate.getDate())+'-'+endDate.getFullYear();
			}

      		var onlineFields = {
					  "startDate" :fromDate,
					  "endDate" :toDate,
					  "status" :$scope.status,
					  "createdBy" : $scope.source,
					  "fieldName" : $scope.orderName,
					  "sortName"  : $scope.orderType
			  }
    		  
    		  var response = $http.post('viewCandidateController/viewMobileAndOnlineCandidates?pageNumber='+$scope.$storage.onlinepageNumber+'&pageSize='+$scope.$storage.onlinepageSize, onlineFields);
    		  response.success(function(data, status, headers, config){
    			  $(".underlay").hide();
//    			  alert("online "+JSON.stringify(data));
    			  $scope.portalTable = false;
 		    	 $scope.onlineTable = true;
 		    	portalcandidateData = [];
 		    	 portalCandidateTable();
    			  onlinecandidateData = [];
    			  for (var i = 0; i < data.length; i++) {
	  					var candidateObj = data[i];
	  					onlinecandidateData.push(candidateObj);
	  				}
    			  $scope.onlinecandidate.onlineTableControl = {};
    			    onlineCandidateTable();
    			    if($rootScope.onlinePagination){
    			    $scope.onlinecandidate.onlineTableControl.options.responseHandler(data);} 
    			    $scope.onlinecandidate.onlineTableControl.options.pageNumber =$scope.$storage.onlinepageNumber;
					$scope.onlinecandidate.onlineTableControl.options.pageSize =$scope.$storage.onlinepageSize;
					$scope.onlinecandidate.onlineTableControl.options.totalRows =data[0].totalRecords;
					$rootScope.onlinePagination = false;
    		  });
    		  response.error(function(data, status, headers, config){
    			  $(".underlay").hide();
      			  if(status == constants.FORBIDDEN){
      				location.href = 'login.html';
      			  }
      		  });
    	 }
    	 
    	 
 
    	  
    	  function portalCandidateTable(){
//    		  alert(portalcandidateData);
                 $scope.portalcandidate.portalTableControl = {
                      options: { 
                    	  data : portalcandidateData || {},
                          striped: true,
                          pagination: true,
                          paginationVAlign: "both", 
                          pageList: [10,20,50],
                          search: false,
                          sidePagination : 'server',
                          silentSort: false,
                          showColumns: false,
                          showRefresh: false,
                          clickToSelect: false,
                          showToggle: false,
                          maintainSelected: true, 
                          columns: [
                                    {
                              field: 'state',
                              checkbox: true,
                          },
                          {
                              field: 'firstName',
                              title: 'Name',
                              align: 'left',
                              
                              sortable: true
                          }, {
                              field: 'title',
                              title: 'Title',
                              align: 'left',
                             
                              sortable: true
                          }, {
                              field: 'location',
                              title: 'Location',
                              align: 'left',
                              
                              sortable: true
                          }, {
                              field: 'createdOn',
                              title: 'Created',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'updatedOn',
                              title: 'Updated',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'status',
                              title: 'Status',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'visaType',
                              title: 'Visa Status',
                              align: 'left',
                              sortable: true
                           },{
								field : 'resume',
								title : 'Resume',
								align : 'center',
								sortable : false,
								events : window.portalresumeEvents,
								formatter : portalresumeFormatter
							},{
                              field: 'actions',
                              title: 'Actions',
                              align: 'left',
                              sortable: false,
                              events: window.portalEvents,
                              formatter: portalFormatter
                          }],
                          
                          
                          onPageChange : function(number, size){
                        	  $scope.pageNumber = number;
          					  $scope.pageSize = size;
          					  $rootScope.pagination = true;
          					  $scope.viewCandidatesclick();
                          },
                          onSort : function(name, order){
                        	  if(!$scope.orderType || ($scope.orderName && $scope.orderName != name)){
                        		  $scope.orderType = order;
                        	  }
                        	  if($scope.orderName && $scope.orderName == name){
                        		  if($scope.orderType && $scope.orderType == constants.ASC){
                        			  $scope.orderType = constants.DESC;
                        		  }else{
                        			  $scope.orderType = constants.ASC;
                        		  }
                        	  }
                        	  $scope.orderName = name;
          					  
          					  $scope.viewCandidatesclick();
                    
                            },
							onCheck : function(row) {
								$(".mesgerror").hide();
								  var id= row.id;
	                        	  var obj = {"id" : id,
	                        			  "email" : row.email+';',
	                        			  "phoneNumber" : row.phoneNumber};
	                        	  $scope.MapselectionCheck.push(obj);
								/*
								
								
								$scope.dataFromChecked.push(row.email+ ';');
								$scope.selectionCheck.push(row.id);
								$scope.smsCheck.push(row.phoneNumber);*/
							},
							onUncheck : function(row) {
								
								
								$.each($scope.MapselectionCheck, function (i, map) {
									if(map.id == row.id){
										var unCheckIndex = $scope.MapselectionCheck.indexOf(map);
										$scope.MapselectionCheck.splice(unCheckIndex,	1);
									}
						    		});
								
								/*var unCheckedEmail = row.email;
								var phone = row.phoneNumber;
								var unCheckedId = row.id;
								var unCheckIndex = $scope.dataFromChecked.indexOf(unCheckedEmail);
								$scope.dataFromChecked.splice(unCheckIndex,	1);
								var selUnCheckIndex = $scope.selectionCheck.indexOf(unCheckedId);
								$scope.selectionCheck.splice(selUnCheckIndex,	1);
								var smsUnCheckIndex = $scope.smsCheck.indexOf(phone);
								$scope.smsCheck.splice(smsUnCheckIndex,	1);*/
								
							},
							onCheckAll : function(rows) {
								$(".mesgerror").hide();
								
								$.each(rows, function (i, row) {
//									alert(rows);
									if($scope.MapselectionCheck.length>0){
										var flag=false;
										$.each($scope.MapselectionCheck, function (i, map) {
//									alert(map+"  map");
									if(map.id == row.id ){
									flag=true;
									}
						    		})
						    		if(!flag){
						    			var obj = {"id" :  row.id,
			                        			  "email" : row.email+';',
			                        			  "phoneNumber" : row.phoneNumber};
			                        	  $scope.MapselectionCheck.push(obj);
						    		}
									}else{
										var obj = {"id" :  row.id,
			                        			  "email" : row.email+';',
			                        			  "phoneNumber" : row.phoneNumber};
			                        	  $scope.MapselectionCheck.push(obj);
									}
								});
							},
							onUncheckAll : function(rows) {
								for (var i = 0; i < rows.length; i++) {
									 
									for(var j=0;j < $scope.MapselectionCheck.length;j++){
										if($scope.MapselectionCheck[j].id == rows[i].id ){
											var unCheckIndex = $scope.MapselectionCheck.indexOf($scope.MapselectionCheck[j]);
											$scope.MapselectionCheck.splice(unCheckIndex,	1);
										}
									}
								
								}
								
							/*	for (var i = 0; i < rows.length; i++) {
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
 
								    	$.each($scope.MapselectionCheck, function (i, map) {
								    		if(map.id == row.id ){
								    			row.state = true;
								    		}
								    		
								    	})
								    });
								
								
							   /* $.each(res, function (i, row) {
							        row.state = $.inArray(row.id, $scope.selectionCheck) !== -1;
							        alert(row.state);
							    });
							    return res;*/
							},
 
                      }
                  };
                 
                 function portalresumeFormatter(value, row,
							index) {
						return [ '<a class="info actionIcons" title="View Resume"><i class="fa fa-file-word-o"></i></a>' ]
								.join('');
					}

					window.portalresumeEvents = {
						'click .info' : function(e, value,
								row, index) {
							var candidateId = row.id;
							var docStats = "others";
							var candidateresumeData=[];
							var objresume = null;
							var response = $http.get('searchResume/resumeByCandidateId?candidateId='+ candidateId+'&docStats='+docStats);
							response.success(function(data, status,headers, config) 
									{
								var candresume = JSON.stringify(data);
								 objresume = JSON.parse(candresume);
								$scope.CandidateText = objresume.resumeContent;
								$scope.CandidateKeywords = $scope.keyskills;

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
							
						}
					};
                
                  /*Table button action formatters*/

                  function portalFormatter(value, row, index) {  
                		if($rootScope.rsLoginUser.userRole == constants.HR
								|| $rootScope.rsLoginUser.userRole == constants.Manager || $rootScope.rsLoginUser.userRole == constants.ADM || $rootScope.rsLoginUser.userRole == constants.Recruiter){
                     return [ 
                     '<a class="view actionIcons" title="View" flex-gt-md="auto"><i class="fa fa-search" style="font-size:12px;"></i></a>', 
                     '<a class="email actionIcons" title="'+row.email+'" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>', 
                     '<a class="download actionIcons" title="Download" ><i class="fa fa-download" style="font-size:12px;"></i></a> ',
                     '<a class="edit actionIcons" title="Edit"><i class="fa fa-edit" style="font-size:12px;"></i></a>', 
//                     '<a class="viewCandidateRemove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>'
                     ].join(''); 
                		}else{
                			 return [ 
                                     '<a class="view actionIcons" title="View" flex-gt-md="auto"><i class="fa fa-search" style="font-size:12px;"></i></a>', 
                                     '<a class="email actionIcons" title="'+row.email+'" flex-gt-md="auto"><i class="fa fa-envelope-o" style="font-size:12px;"></i></a>', 
                                     '<a class="download actionIcons" title="Download" ><i class="fa fa-download" style="font-size:12px;"></i></a> ',
                                     '<a class="edit actionIcons" title="Edit"><i class="fa fa-edit" style="font-size:12px;"></i></a>', 
                                     '<a class="viewCandidateRemove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>'
                                     ].join(''); 
                		}
                  } 
                  
                  /*Table button actions functionalities*/
                  window.portalEvents =  {
                          
                          /*View Resume details*/
                          'click .view': function (e, value, row, index) { 
                              	 $mdDialog.show({
                           		      controller: DialogController,
                           		      templateUrl: 'views/dialogbox/viewdetailsdialogbox.html',
                           		      parent: angular.element(document.body),
                           		      targetEvent: e,
                           		      clickOutsideToClose:true,
                           		      locals:{
                           		    	  rowData : row,
                           		    	  alertInfo:""
                           		      }
                           		    });
                           },
                          'click .email': function (e, value, row, index) { 
                        	  var email = row.email;
                        	  mailService.sendMail(email, false);
                          },
                          'click .download': function (e, value, row, index) { 
                        		var candidateId = row.id;
								var docStats = "others"
								var candidateresumeData=[];
								var objresume = null;
								$window.location = 'searchResume/resumeByCandidateId/'+candidateId+'/'+docStats+'/'+constants.ORGDOC;
                          },
                          'click .edit': function (e, value, row, index) { 
                        	  $rootScope.candidateEditId = row.id;	
                        	  $rootScope.createdDate = $scope.Created;
                        	  $rootScope.source = $scope.source;
                        	  $rootScope.pageNumber =  $scope.pageNumber;
                        	  $rootScope.pageSize = $scope.pageSize;
                        	  
                          	 $state.transitionTo("EditCandidate",{candidateId : row.id, page:"view"});
                          }, 
                          
                          'click .viewCandidateRemove': function (e, value, row, index) {  
                        	  $mdDialog.show({
                      		      controller: DialogController,
                      		      templateUrl: 'views/dialogbox/removecandiatedialogbox.html',
                      		      parent: angular.element(document.body),
                      		      targetEvent: e,
                      		      locals:{
                     		    	  rowData : row,
                     		    	  alertInfo:""
                     		      },
                      		      clickOutsideToClose:true,
                      		    }) .then(function(answer) {
                      		    	$(".underlay").show();
                      		    	var deleteReason = {
                      		    			"candidateId":row.id,
                    		    			"reason":answer
                    		    			}  
                      		    	var candidateId= row.id;
                                	var response = $http.post('searchResume/deleteCandidate',deleteReason);
                                	response.success(function(data, status, headers, config) {
                                		if(data.statusCode == 200){
                                			$scope.pageNumber = $scope.$storage.pageNumber;
                                			$scope.pageSize = $scope.$storage.pageSize;
                                			$(".underlay").hide();
                                			 angular.element("#viewCandidateControllerId").scope().viewCandidatesclick();
                                			 $.growl.notice({ title : "Delete !",message: data.statusMessage+"#"+row.id });
                                		
                                		}else{
                                			$(".underlay").hide();
                                			$.growl.error({  title : "Delete !",message: data.statusMessage+"#"+row.id });
                                		}
                                	});
                                	response.error(function(data, status, headers, config){
                            			  if(status == constants.FORBIDDEN){
                            				location.href = 'login.html';
                            			  }else{  			  
                            				$state.transitionTo("ErrorPage",{statusvalue  : status});
                            			  }
                            		  });
                      		    }, function() {
                      		    });
                            }
                          
                  };
        
  		}
    	  
    	  function onlineCandidateTable(){
//    		  alert("in online candidates");
    		  $scope.onlinecandidate.onlineTableControl = {
                      options: { 
                    	  data : onlinecandidateData || {},
                          striped: true,
                          pagination: true,
                          paginationVAlign: "both", 
                          sidePagination : 'server',
                          silentSort: false,
                          pageList: [10,20,50],
                          search: false,
                          showColumns: false,
                          showRefresh: false,
                          clickToSelect: false,
                          showToggle: false,
                          maintainSelected: true, 
                          columns: [
                                    {
                              field: 'state',
                              checkbox: true,
                          },
                          {
                              field: 'id',
                              title: 'candidateId',
                              align: 'left',
                              
                              sortable: true
                          }, {
                              field: 'jobOrderId',
                              title: 'Job Order Id',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'jobTitle',
                              title: 'Job Title',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'firstName',
                              title: 'Name',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'email',
                              title: 'Email',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'phoneNumber',
                              title: 'Cell',
                              align: 'left',
                              sortable: true
                          }, {
                              field: 'portalInfo',
                              title: 'Portal',
                              align: 'left',
                              sortable: true
                           }, {
                               field: 'dmName',
                               title: 'DM/ADM',
                               align: 'left',
                               sortable: true
                            }, {
                              field: 'actions',
                              title: 'Actions',
                              align: 'left',
                              sortable: false,
                              events: window.onlineEvents,
                              formatter: onlineFormatter
                          }],
                          	onPageChange : function(number, size){
                        	  $scope.onlinePageNumber = number;
          					  $scope.onlinePageSize = size;
          					  $rootScope.onlinePagination = true;
          					  $scope.viewCandidatesclick();
                          },
                          onSort : function(name, order){
                        	  if(!$scope.orderType || ($scope.orderName && $scope.orderName != name)){
                        		  $scope.orderType = order;
                        	  }
                        	  if($scope.orderName && $scope.orderName == name){
                        		  if($scope.orderType && $scope.orderType == constants.ASC){
                        			  $scope.orderType = constants.DESC;
                        		  }else{
                        			  $scope.orderType = constants.ASC;
                        		  }
                        	  }
                        	  $scope.orderName = name;
                        	  $scope.viewCandidatesclick();
                        	
                          },
                          /*Check box action*/
                          onCheck : function(row) {
                        		$(".mesgerror").hide();
                        	  var id= row.id;
                        	  var jobOrder = row.jobOrderId;
                        	  var obj = {"id" : id,
                        			  "jobOrder" : jobOrder,
                        			  "email" : row.email+';',
                        			  "phoneNumber" : row.phoneNumber};
                        	  $scope.MapselectionCheck.push(obj);
                        	 
							},
							onUncheck : function(row) {
								var unCheckedEmail = row.email;
								var phone = row.phoneNumber;
								var unCheckedId = row.id;
								$.each($scope.MapselectionCheck, function (i, map) {
									if(map.id == row.id && map.jobOrder == row.jobOrderId){
										var unCheckIndex = $scope.MapselectionCheck.indexOf(map);
										$scope.MapselectionCheck.splice(unCheckIndex,	1);
									}
						    		});
								
							/*	var unCheckIndex = $scope.dataFromChecked.indexOf(unCheckedEmail);
								$scope.dataFromChecked.splice(unCheckIndex,	1);
								var selUnCheckIndex = $scope.selectionCheck.indexOf(unCheckedId);
								$scope.selectionCheck.splice(selUnCheckIndex,	1);
								var smsUnCheckIndex = $scope.smsCheck.indexOf(phone);
								$scope.smsCheck.splice(smsUnCheckIndex,	1);*/
								
							},
							onCheckAll : function(rows) {
								$(".mesgerror").hide();
								/*for (var i = 0; i < rows.length; i++) {
									if($.inArray(rows[i].id, $scope.selectionCheck) === -1){
										$scope.dataFromChecked.push(rows[i].email+ ';');
										$scope.selectionCheck.push(rows[i].id);
										$scope.smsCheck.push(rows[i].phoneNumber);
									}
									
								}*/
								$.each(rows, function (i, row) {
									if($scope.MapselectionCheck.length>0){
										var flag=false;
										$.each($scope.MapselectionCheck, function (i, map) {
									if(map.id == row.id && map.jobOrder == row.jobOrderId){
									flag=true;
									}
						    		})
						    		if(!flag){
						    			var obj = {"id" :  row.id,
			                        			  "jobOrder" :  row.jobOrderId,
			                        			  "email" : row.email+';',
			                        			  "phoneNumber" : row.phoneNumber};
			                        	  $scope.MapselectionCheck.push(obj);
						    		}
									}else{
										var obj = {"id" :  row.id,
			                        			  "jobOrder" :  row.jobOrderId,
			                        			  "email" : row.email+';',
			                        			  "phoneNumber" : row.phoneNumber};
			                        	  $scope.MapselectionCheck.push(obj);
									}
								});
								
							},
							onUncheckAll : function(rows) {
							/*	$.each(rows, function (i, row) {
									var unCheckIndex = null;
									$.each($scope.MapselectionCheck, function (i, map) {
										if(map.id == row.id && map.jobOrder == row.jobOrderId){
											 unCheckIndex = $scope.MapselectionCheck.indexOf(map);
										}
							    		})
							    		if(unCheckIndex){
							    			alert(JSON.stringify($scope.MapselectionCheck));
							    			$scope.MapselectionCheck.splice(unCheckIndex-1,	1);
							    			alert(JSON.stringify($scope.MapselectionCheck));
							    		}
									});*/
								
								
								for (var i = 0; i < rows.length; i++) {
									 
									for(var j=0;j < $scope.MapselectionCheck.length;j++){
										if($scope.MapselectionCheck[j].id == rows[i].id && $scope.MapselectionCheck[j].jobOrder == rows[i].jobOrderId){
											var unCheckIndex = $scope.MapselectionCheck.indexOf($scope.MapselectionCheck[j]);
											$scope.MapselectionCheck.splice(unCheckIndex,	1);
										}
									}
								
								}
							},
							responseHandler : function(res) {
							    $.each(res, function (i, row) {
							    	$.each($scope.MapselectionCheck, function (i, map) {
							    		if(map.id == row.id && map.jobOrder == row.jobOrderId){
							    			row.state = true;
							    		}
							    		
							    	})
							    });
							    return res;
							},
                      }
                  };
                  
                  /*Mail sending on check box action*/
                
                  /*Table button action formatters*/

                  function onlineFormatter(value, row, index) {  
                	  if($scope.status == "PENDING"){
                		  return [ 
                                  '<a class="info actionIcons" title="View Resume"><i class="fa fa-file-word-o"></i></a>', 
                                  '<a class="approve actionIcons" title="Approve" flex-gt-md="auto"><i class="fa fa-check-circle"></i></a>', 
                                  '<a class="reject actionIcons" title="Reject" flex-gt-md="auto"><i class="fa fa-times-circle" ></i></a>', 
                                  ].join(''); 
                	  }else{
                		  return [ 
                                  '<a class="info actionIcons" title="View Resume"><i class="fa fa-file-word-o"></i></a>', 
                                  ].join(''); 
                	  }
                    
                  } 
                  
                  /*Table button actions functionalities*/
                  window.onlineEvents =  {
                          
                          /*View Resume details*/
                          'click .info': function (e, value, row, index) { 
                        	  var docStats = "others";
                        		var candidateId = row.id;
								var objresume = null;
								var response = $http.get('searchResume/resumeByCandidateId?candidateId='
										+ candidateId+'&docStats='+docStats);
								response.success(function(data, status,headers, config) 
										{
									var candresume = JSON.stringify(data);
									 objresume = JSON.parse(candresume);
									$scope.CandidateText = objresume.resumeContent;
  							$mdDialog
  									.show({
  										controller : DialogController,
  										templateUrl:
  										'views/dialogbox/resumedialogbox.html',
  										parent : angular
  												.element(document.body),
  										targetEvent : e,
  										locals : {
  											rowData : row,
  											alertInfo:$scope.CandidateText
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
                           },
                          'click .approve': function (e, value, row, index) { 
                        	  $mdDialog.show({
                       		      controller: ApproveDialogController,
                       		      templateUrl: 'views/dialogbox/candidatealerts.html',
                       		      parent: angular.element(document.body),
                       		      targetEvent: e,
                       		      clickOutsideToClose:true,
                       		      locals:{
                       		    	  rowData : row,
                       		    	alertInfo:"Are you sure you want to Submit this resume to Job Order?"
                       		      }
                       		    }).then (function(answer){
//                       		    	alert(" in approve answer");
                       		    	var data = $.param({
                       	                candidateId: row.id,
                       	                jobOrderId: row.jobOrderId,
                       	            });
                       		    	var config = {headers :{'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'}}
                       		    	$(".underlay").show();
                       		    	if($scope.source == "Online Resumes"){
                       		    	 var response = $http.post('viewCandidateController/approveOnlineCandidates',data,config);
                        	           response.success(function(data, status, headers, config) {
/*                        	        	   $scope.portalTable = false;
                        	          	 $scope.onlineTable = false;
                        	      		 $scope.showGridTable = false;
                       	        	  $scope.onlinepageNumber = $scope.$storage.onlinePageNumber;
                					  $scope.onlinepageSize = $scope.$storage.onlinePageSize;
                					  $scope.viewCandidatesclick();*/
                        	        	   $(".underlay").hide();
                        	        	   angular.element("#viewCandidateControllerId").scope().viewCandidatesclick();
                        	        	   $.growl.notice({ title : "Candidate Approved !",message: "The selected Candidate Id #"+row.id+" is approved" });
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
                       		    	if($scope.source == "Mobile Resumes"){
                       		    	 var response = $http.post('viewCandidateController/approveMobileCandidates',data,config);
                        	           response.success(function(data, status, headers, config) {
                        	        	   $scope.onlinepageNumber = $scope.$storage.onlinePageNumber;
                     					  $scope.onlinepageSize = $scope.$storage.onlinePageSize;
                     					  $scope.viewCandidatesclick();
                        	           });
                        	           response.error(function(data, status, headers, config){
                   	          			  if(status == constants.FORBIDDEN){
                   	          				location.href = 'login.html';
                   	          			  }else{  			  
                   	          				$state.transitionTo("ErrorPage",{statusvalue  : status});
                   	          			  }
                   	          		  });
                       		    	}
                       		    	
                       		  
                       		    },function(){
                       		    	//alert("cancelled");
                       		    });
                          },
                          'click .reject': function (e, value, row, index) { 
                        	  $mdDialog.show({
                       		      controller: RejectDialogController,
                       		      templateUrl: 'views/dialogbox/candidatealerts.html',
                       		      parent: angular.element(document.body),
                       		      targetEvent: e,
                       		      clickOutsideToClose:true,
                       		      locals:{
                       		    	  rowData : row,
                       		    	alertInfo:"Are you sure you want to Reject this resume to Job Order?"
                       		      }
                       		    }).then (function(answer){
                       		    	var data = $.param({
                       		    		candidateId: row.id,
                       		    		jobOrderId: row.jobOrderId
                       	            });
                       		    	var config = {headers :{'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8;'}}
                       		    	$(".underlay").show();
                       		    	if($scope.source == "Online Resumes"){
                       		    	 var response = $http.post('viewCandidateController/rejectOnlineCandidates', data, config);
                      	           response.success(function(data, status, headers, config) {
                      	        	   
                      	        	/* $scope.onlinepageNumber = $scope.$storage.onlinePageNumber;
                      	        	 $scope.onlinepageSize = $scope.$storage.onlinePageSize;
                      	        	 $scope.viewCandidatesclick();*/
                      	        	 $(".underlay").hide();
                      	        	 angular.element("#viewCandidateControllerId").scope().viewCandidatesclick();
                      	        	 $.growl.notice({ title : "Candidate Rejected !",message: "The selected Candidate Id #"+row.id+" is rejected" });
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
                       		    	if($scope.source == "Mobile Resumes"){
                       		    	 var response = $http.post('viewCandidateController/rejectMobileCandidates', data, config);
                       		    	 
                       		    	 response.success(function(data, status, headers, config) {
                       		    		/* $scope.onlinepageNumber = $scope.$storage.onlinePageNumber;
                       		    		 $scope.onlinepageSize = $scope.$storage.onlinePageSize;
                       		    		 $scope.viewCandidatesclick();*/
                       		    		$(".underlay").hide();
                         	        	 angular.element("#viewCandidateControllerId").scope().viewCandidatesclick();
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
                       		    
                       		    },function(){
                       		    	//alert("cancelled");
                       		    });
                          }
                  };
    	  }
    	  
    	  $scope.sendEmails = function() {
    		  
    		  if($scope.MapselectionCheck.length>0){
					$(".mesgerror").hide();
					var checkedData = '';
    				$.each($scope.MapselectionCheck, function (i, map) {
    					checkedData += map.email;
			    		})  
			    		mailService.sendMail(checkedData, true);
				}
				else{
					$(".mesgerror").show();
				}
			/*	if($scope.dataFromChecked.length>0){
					$(".mesgerror").hide();
					var checkedData = '';
					for (var i = 0; i < $scope.dataFromChecked.length; i++) {
						checkedData += $scope.dataFromChecked[i];
					}
					mailService.sendMail(checkedData, true);
				}
				else{
					//alert("Please select candidates to send Mail");
					$(".mesgerror").show();
				}*/
				
			}
    	  
    	  $scope.sendSms = function(e){
    		  
    		  if($scope.MapselectionCheck.length>0){
					$(".mesgerror").hide();
					var checkedArray = [];
  				$.each($scope.MapselectionCheck, function (i, map) {
  					checkedArray.push(map.phoneNumber);
			    		})  
			    		smsService.sendSms(e, checkedArray);
				}
				else{
					$(".mesgerror").show();
				}
    		  
				/*var checkedArray = angular.copy($scope.smsCheck);
				if($scope.smsCheck.length>0){
					$(".mesgerror").hide();
					smsService.sendSms(e, checkedArray);
				}
				else{
					//alert("Please select Candidates to Send SMS ");
					$(".mesgerror").show();
				}*/
			}
          function ResumeDialogController($scope,
					$mdDialog, CandidateText,
					CandidateKeywords) {
				$scope.CandidateText = CandidateText;
				$scope.CandidateKeywords = CandidateKeywords;

				$scope.cancel = function() {
					$mdDialog.cancel();
				};
			}

          function DialogController($scope, $mdDialog, rowData, alertInfo){
     		 $scope.row = rowData;
     		 $scope.CandidateText = alertInfo;
			 $scope.CandidateKeywords = rowData.firstName;
     		 $scope.alertInfo = alertInfo;
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
			 
    	  function ApproveDialogController($scope, $mdDialog, rowData, alertInfo){
        		 $scope.row = rowData;
        		 $scope.CandidateText = alertInfo;
				 $scope.CandidateKeywords = rowData.firstName;
        		 $scope.alertInfo = alertInfo;
        		 $scope.hide = function() {
   		    	    $mdDialog.hide();
   		    	  };

   		    	  $scope.cancel = function() {
   		    	    $mdDialog.cancel();
   		    	  };

   		    	  $scope.answer = function(answer) {
   		    	    $mdDialog.hide(answer);
   		    	  };
        	 }
    	  function RejectDialogController($scope, $mdDialog, rowData, alertInfo){
     		 $scope.row = rowData;
     		 $scope.CandidateText = alertInfo;
				 $scope.CandidateKeywords = rowData.firstName;
     		 $scope.alertInfo = alertInfo;
     		 $scope.hide = function() {
		    	    $mdDialog.hide();
		    	  };

		    	  $scope.cancel = function() {
		    	    $mdDialog.cancel();
		    	  };

		    	  $scope.answer = function(answer) {
		    	    $mdDialog.hide(answer);
		    	  };
     	 }

    });
})(angular);