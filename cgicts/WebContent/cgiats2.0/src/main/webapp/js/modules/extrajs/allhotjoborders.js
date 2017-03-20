;(function(angular) {

    "use strict";

    angular.module('allhotjobsapp', ["ngGrid","ngTable"])
    .directive("repeatEnd", function(){
            return {
                restrict: "A",
                link: function (scope, element, attrs) {
                    if (scope.$last) {
                        scope.$eval(attrs.repeatEnd);
                    }
                }
            };
        })
    
    
    .controller("allhotjobcontroller",function($rootScope, $scope, $http,NgTableParams, $timeout) {
            	
            	
            	$scope.onload = function(){
            		$scope.pagename = "ALL HOT JOB ORDERS"
        			

        			$scope.searchhotjoborder();

        		}
            	
            	
        		$scope.searchhotjoborder = function(){
        			$scope.pageNumber = 1;
        			$("#searchtable").css("display", "block");
        			search_hotjoborder();
        		}
        		var rows=[];
        		function search_hotjoborder(){

        			$(".underlay").show();
        			$scope.searchFields= {
        					"strPriorities" : $("#priority").val().toUpperCase(),
        					"strStatuses" : $("#status").val().toUpperCase(),
        					"strJobTypes" :($("#jobtype").val().toUpperCase()).replace(/ +/g, ""),
        					"jobOrderTimeIntervalMap" :$scope.Created,
        					"strJobBelongsTo" : $("#jobbelongsto").val().toUpperCase(),
        					"jobOrderId" : $scope.joborderid,
        					"hot"       : true
        			}
        			
        			$scope.searchFields.jobOrderTimeIntervalMap = {};
        				$scope.searchFields.jobOrderTimeIntervalMap.startDate = moment({'year' :2012, 'month' :5, 'day' :1});
        				$scope.searchFields.jobOrderTimeIntervalMap.endDate = moment();
        				
        			var response = $http.post("jobOrder/getHotJobOrdersForKen",$scope.searchFields);
        			response.success(function(data, status,headers, config) 
        					{
//        				alert("data "+JSON.stringify(data));
        				$scope.hotJobOrdersList = data;
        				
        				$scope.noOfRecords = $scope.hotJobOrdersList.length;
             			 $scope.pageNo = 0;
             	    	$scope.getCurrentPageRecords();
             	    	$scope.calculatePageNavigationValues();
        				
        				$scope.tableParams = new NgTableParams({}, { dataset: $scope.displayRecords});
        				$(".underlay").hide();
        		      });
        			response.error(function(data, status, headers, config){
          			  if(status == constants.FORBIDDEN){
          				location.href = 'login.html';
          			  }else{  			  
          				$state.transitionTo("ErrorPage",{statusvalue  : status});
          			  }
          		  });
//        			$(".underlay").hide();
        		
        		}
        		
        		
        		
        		/*
        		$scope.gridOptions = { 
        		          data: 'hotJobOrdersList',
//        		        data: 'myData1',
        		          enablePaging: true,
        		          showFooter: true,
        	              showFilter: true,
        	              rowHeight: 50,
        	              multiSelect:false,
        	              pagingOptions: {
        	                  pageSizes: [5,10,20,50],
        	                  pageSize: 1,
        	                  currentPage: 1
        	              },
        	              enableRowSelection: false,
        	              columnDefs: [
        	               {
        	                 field: "jobOrderId",
        	                 displayName: "Id",
        	               },
        	               {
          	                 field: "priority",
          	                 displayName: "Priority",
          	               },
        	               {
          	                 field: "status",
          	                 displayName: "Status",
          	               },
          	             {
								field : 'type',
								title : 'Type',
								align : 'left',
							},
							{
								field : 'title',
								title : 'Title',
							},
							{
								field : 'client',
								title : 'Client',
							},
							{
								field : 'location',
								title : 'Location',
							},
							{
								field : 'dm',
								title : 'DM',
							},
							{
								field : 'assignedTo',
								title : 'Assigned To',
							},
							{
								field : 'updatedDate',
								title : 'Updated On',
							},
							{
								field : 'sbm',
								title : 'Sbm',
							},
							{
								field : 'activeDays',
								title : 'Active Days',
							},
        	               ]};
        		*/
        		
        		 $scope.getTimeFnc = function(){
        				$scope.currentDateWithTime = new Date();
        			}

        		

        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        	/*$scope.onEnd = function()
        	{
        		$timeout(function(){
        			
        			var tablerow = $("#rpt").children("tr");
        			var self=$("#rpt");
        			var kids=self.children("tr");
        			kids.slice(10).hide();
        			
        			setInterval(function(){
        				
        				kids.eq(0).fadeOut(function(){
        					$(this).appendTo(self)
        					kids=self.children()
        				})
        				kids.filter(':hidden').eq(0).fadeIn()
        			},5000)
        			
                }, 1);
        	}*/
        		
        		
        		
        		 /* *********************************************PAGINATION STARTS HERE-------------------------------------------------- */
        			var recordsPerPage=20;
        			
        			$scope.nextPageRecords = function(pageNo){
        			 $scope.lblCurrentPageRecords = "Showing "+(pageNo+1)+" to "+(((pageNo+recordsPerPage)<$scope.noOfRecords)?(pageNo+recordsPerPage):($scope.noOfRecords))+" of "+$scope.noOfRecords;
        		    	$scope.isProcessing = true;
        		  	  $scope.pageNo = pageNo;
        		  	  $scope.getCurrentPageRecords();
        		  	$scope.tableParams = new NgTableParams({}, { dataset: $scope.displayRecords});
        		    };
        		    
        		    //when we click on search button
        		    $scope.getCurrentPageRecords = function(){
        		    	$scope.displayRecords =[];
        		    	var iteratationCount = (($scope.pageNo)+recordsPerPage)> $scope.noOfRecords? $scope.noOfRecords:(($scope.pageNo)+recordsPerPage);
        		for(var i=$scope.pageNo;i<iteratationCount;i++){
        			$scope.displayRecords.push($scope.hotJobOrdersList[i]);
        		}
        		    };
        			
        			 $scope.calculatePageNavigationValues = function(){
        				 $scope.lblCurrentPageRecords = "Showing 1 to "+(((recordsPerPage)<$scope.noOfRecords)?(recordsPerPage):($scope.noOfRecords))+" of "+$scope.noOfRecords;
        				 $scope.pages = [{}];
        				 $scope.pages.splice(0,1);
        				 if($scope.noOfRecords > recordsPerPage){
        					var j=1;
        					
        					for(var i=1;i<=$scope.noOfRecords;){
        						var obj = {};
        						obj.number = j++;
        							obj.recordOffset = i-1;
        						$scope.pages.push(obj);
        						i += recordsPerPage;
        					}
        				 }
        			    };
        			    /* *********************************************PAGINATION ENDS HERE-------------------------------------------------- */		 
        		
        		
        		
        		
        		
            	
            
            	
            }); 

})(angular);