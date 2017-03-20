;(function(angular) {

    "use strict";
    
    angular.module('MissingDataInfo',['ui.bootstrap'])
    .controller('MissingDataInfoController',function($rootScope, $scope, blockUI, $http, $timeout, $filter, $mdDialog, $mdMedia, $window, $state, $stateParams, $sessionStorage, mailService,dateRangeService){
    	
    	
    	$scope.$storage = $sessionStorage;
    	$scope.pageNumber = 1;
    	$scope.pageSize = 50;
    	$scope.orderName = "";
    	$scope.orderType = "";
    	$scope.missingDataTable = false;
    	$scope.missingcandidate = [];
    	
    	
    	
    	$scope.onload = function (){
    		$scope.Created = { endDate: moment(), startDate:moment().subtract(1, 'month')};
    		$scope.daterange ={
        			today : [moment(),moment()],
        			onemonth : [moment().subtract(1, 'month'), moment()],
        			twomonths : [moment().subtract(2, 'month'), moment()],
        			threemonths : [moment().subtract(3, 'month'), moment()],
        			custom : 'custom'
        	}
        
        	  $scope.ranges = {
        	        'Last 1 month': [moment().subtract(1, 'month'), moment()],
        	        'Last 3 months': [moment().subtract(3, 'month'), moment()],
        	        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
        	        'Last 1 year': [moment().subtract(12, 'month'), moment()]
        	        };
    		
    		if($rootScope.MissingCandidateEditId){
    			var editCandidateBackButton = true;
    			$rootScope.MissingCandidateEditId = null;
    		}
        	
        	if($stateParams.editCandidate || editCandidateBackButton){
        		
        		 $scope.Created =  $scope.$storage.startEnd;
        		 $scope.pageNumber = $scope.$storage.number;
        		 $scope.pageSize = $scope.$storage.size; 
        		$scope.viewMissingData();
        	}
    	}
    	$scope.missingDataClick = function(){
    		$scope.pageNumber = 1;
        	$scope.pageSize = 50;
        	$scope.orderName = "";
        	$scope.orderType = "";
    		$scope.viewMissingData();
    	}
    	$scope.viewMissingData = function(){
    		$scope.missingDataTable = true;
    		$(".underlay").show();
    		var startDate = new Date($scope.Created.startDate);
     		  var fromDate = dateRangeService.formatDate(startDate);
     		  var endDate = new Date($scope.Created.endDate);
     		  var toDate = dateRangeService.formatDate(endDate);
     		 $scope.$storage.startEnd = $scope.Created;
     		 $scope.$storage.number = $scope.pageNumber;
     		 $scope.$storage.size = $scope.pageSize; 
     		  var missingFields = {
     				 "startDate" : fromDate,
					  "endDate" : toDate,
					  "fieldName" : $scope.orderName,
					  "sortName"  : $scope.orderType
     		  }
     		  
     		  var response = $http.post("viewCandidateController/getMissingDataCandidates?pageNumber="+$scope.$storage.number+"&pageSize="+$scope.$storage.size, missingFields);
     		  response.success(function(data, status, config, headers){
//     			  alert("success data "+JSON.stringify(data));
//     			 $timeout(function() {//wait for some time to redirect to another page
//     				
//				 }, 400);
     			    missingDataTable();
     			  	$scope.missingcandidate.missingTableControl.options.data =data; 
					$scope.missingcandidate.missingTableControl.options.pageNumber =$scope.$storage.number;
					$scope.missingcandidate.missingTableControl.options.pageSize =$scope.$storage.size;
					if(data){
					$scope.missingcandidate.missingTableControl.options.totalRows =data[0].totalRecords;
					}
					$(".underlay").hide();
     		  });
     		 response.error(function(data, status, config, headers){
     			$(".underlay").hide();
     			 if(status == constants.FORBIDDEN){
       				location.href = 'login.html';
       			  }else{  			  
       				$state.transitionTo("ErrorPage",{statusvalue  : status});
       			  }
    		  });
    	}
    	
    	
    	function missingDataTable(){
    		
            $scope.missingcandidate.missingTableControl = {
                    options: { 
                        striped: true,
                        pagination: true,
                        paginationVAlign: "both", 
                        pageList: [50,100,200],
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
                            field: 'id',
                            title : 'Id',
                            sortable: true,
                            events : window.missingIdEvents,
							formatter : missingIdFormatter
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
                            field: 'city',
                            title: 'City',
                            align: 'left',
                            
                            sortable: true
                        }, {
                            field: 'state',
                            title: 'State',
                            align: 'left',
                            sortable: true
                        }, {
                            field: 'email',
                            title: 'Email',
                            align: 'left',
                            sortable: true
                        }, {
                            field: 'updatedOn',
                            title: 'Updated On',
                            align: 'left',
                            sortable: true
                        },{
								field : 'resume',
								title : 'Type',
								align : 'center',
								sortable : false,
								events : window.missingresumeEvents,
								formatter : missingresumeFormatter
							},{
                            field: 'actions',
                            title: 'Actions',
                            align: 'left',
                            sortable: false,
                            events: window.missingEvents,
                            formatter: missingFormatter
                        }],
                        
                        
                        onPageChange : function(number, size){
                      	  $scope.pageNumber = number;
        					  $scope.pageSize = size;
        					  $scope.viewMissingData();
                        },
                        onSort : function(name, order){
                        	$(".underlay").show();
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
        					  
        					  $scope.viewMissingData();
                  
                          },
                    }
                };
            function missingresumeFormatter(value, row,
					index) {
            	if(row.documentType === constants.HTML){
            		return [ 
    				         '<a  ><img class="missingInfo actionIcons"  title="'+ row.documentType+ '" width="18" height="18" src="resources/img/HTML.png"/></a>']
    						.join('');
            	}
            	if(row.documentType === constants.DOCX){
            		return [ '<a  ><img class="missingInfo actionIcons"  title="'+ row.documentType+ '" width="18" height="18" src="resources/img/DOCX.png"/></a>']
    						.join('');
            	}
            	if(row.documentType === constants.PLAIN){
            		return [ 
    				         '<a  ><img class="missingInfo actionIcons"  title="'+ row.documentType+ '" width="18" height="18" src="resources/img/PLAIN.png"/></a>',
    				         ]
    						.join('');
            	}
            	if(row.documentType === constants.MS_WORD){
            		return [ 
    				         '<a  ><img class="missingInfo actionIcons"  title="'+ row.documentType+ '" width="18" height="18" src="resources/img/MS_WORD.png"/></a>']
    						.join('');
            	}
            	if(row.documentType === constants.PDF){
            		return [ 
    				         '<a  ><img class="missingInfo actionIcons"  title="'+ row.documentType+ '" width="18" height="18" src="resources/img/PDF.png"/></a>'
    				         ]
    						.join('');
            	}
            	if(row.documentType === constants.RTF){
            		return [
    				         '<a  ><img class="missingInfo actionIcons"  title="'+ row.documentType+ '" width="18" height="18" src="resources/img/RTF.png"/></a>']
    						.join('');
            	}
            	
            	
				
			}

			window.missingresumeEvents = {
				'click .missingInfo' : function(e, value,
						row, index) {
					
				}
			};
			
			 function missingFormatter(value, row, index) {  
                 return [ 
                 '<a class="missingView actionIcons" title="View" flex-gt-md="auto" ><i class="fa fa-search" style="font-size:12px;"></i></a>', 
                 ].join(''); 
              } 
              
              /*Table button actions functionalities*/
              window.missingEvents =  {
            		  
                      /*View Resume details*/
                      'click .missingView': function (e, value, row, index) { 
                          	 $mdDialog.show({
                       		      controller: MissingDialogController,
                       		      templateUrl: 'views/dialogbox/viewdetailsdialogbox.html',
                       		      parent: angular.element(document.body),
                       		      targetEvent: e,
                       		      clickOutsideToClose:true,
                       		      locals:{
                       		    	  rowData : row,
                       		      }
                       		    });
                       },
              };
              
              function missingIdFormatter(value, row, index) {  
                  return [ 
                  '<a class="missingId actionIcons" title="'+row.id+'" flex-gt-md="auto" target="_blank">'+row.id+'</a>', 
                  ].join(''); 
               } 
               
               /*Table button actions functionalities*/
               window.missingIdEvents =  {
                       'click .missingId': function (e, value, row, index) { 
                    	   $rootScope.MissingCandidateEditId = row.id;	
                    	   var url = $state.href("EditCandidate",{candidateId : row.id, page:constants.MISSING_DATA});
                    	   window.open(url,'_blank');
                        },
               };
    	}
    	
    	 
    	  function MissingDialogController($scope, $mdDialog, rowData ){
      		 $scope.row = rowData;
      		 $scope.hide = function() {
 		    	    $mdDialog.hide();
 		    	  };

 		    	  $scope.cancel = function() {
 		    	    $mdDialog.cancel();
 		    	  };

 		    	 
      	 }
    });
    
})(angular);