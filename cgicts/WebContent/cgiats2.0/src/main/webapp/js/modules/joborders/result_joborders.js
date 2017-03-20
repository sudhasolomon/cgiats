;
(function(angular) {

	"use strict";
	
	angular
	.module("resultJobOrdersModule", ['ui.bootstrap'])
	.controller("resultJobOrdersController" , function($scope, $http){
		
		
		$scope.resultJobOrder = [];
		$scope.resultTable = false;
		
		$scope.onload = function(){
			$scope.resultTable = true;
			jobOrderResultTableView();
		}
		
		
		function jobOrderResultTableView(){
			
			 $scope.resultJobOrder.resultBsTableControl = {
	                    options: { 
	                        striped: true,
	                        pagination: true,
	                        paginationVAlign: "bottom", 
	                        pageList: [50,100,200],
	                        search: false,
	                        //sidePagination : 'server',
	                        silentSort: false,
	                        showColumns: false,
	                        showRefresh: false,
	                        clickToSelect: false,
	                        showToggle: false,
	                        maintainSelected: true, 
	                        showFooter : false,
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
	                    }
	            };
			 
			 function resumeFormatter(value, row,
						index) {
					return "";
					
				}
			 
			 window.candidateResumeEvents = {
					 
			 };
			 
				function actionFormatter(value, row,
						index) {
					return "";
				}
				
				window.searchoperateEvents = {
						
				};

		}
	})
})(angular);