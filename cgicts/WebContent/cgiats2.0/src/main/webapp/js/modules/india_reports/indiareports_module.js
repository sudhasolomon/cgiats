;(function(angular){
	
	"use strict";
	 		
 
		angular.module('indiaReportsModule',['indiaJobOrderStatsModule', 'dateRangeModule', 'indiaSubmitalStatsModule', 'indiaSubmitalModule','clientWise_indiaSubmitalStatsModule', 'indiadmsummarymodule', 'recruiterreportmodule'])
 
		.config(['$stateProvider', '$urlRouterProvider', 'blockUIConfig', function($stateProvider, $urlRouterProvider, blockUIConfig,$scope) { 
			
			
			  blockUIConfig.autoBlock = false;
              // Redirect any unmatched url
              $urlRouterProvider.otherwise("/dashboard");  
              $stateProvider
              
              .state('indiaJobOrderStatsModule', {
                  url: "/indiaReports/jobOrderStats",
                  templateUrl: "views/india_reports/indiajoborder_stats.html",            
                  data: {pageTitle: "India Stats  -  JobOrders"},
                  
                  controller: "indiaJobOrderStatsController",
                  resolve: {
                      deps: ['$ocLazyLoad', function($ocLazyLoad) {
                          return $ocLazyLoad.load({
                              name: 'SearchModule',  
                              insertBefore: '#Load_JS_Files_Before_Module',
                              files: [ 
                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css',
                                  'assets/global/plugins/bootstrap-table-master/bootstrap-table.css',
                                  'assets/global/plugins/angularjs/plugins/block-ui/angular-block-ui.css',

                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js' 
                              ] 
                          });
                      }]
                  }
              }) 
              
                  .state('indiaSubmitalStatsModule', {
                  url: "/indiaReports/submitalStats",
                  templateUrl: "views/india_reports/indiasubmital_stats.html",            
                  data: {pageTitle: "India Stats  -  Submital"},
                  
                  controller: "indiaSubmitalStatsController",
                  resolve: {
                      deps: ['$ocLazyLoad', function($ocLazyLoad) {
                          return $ocLazyLoad.load({
                              name: 'StatsModule',  
                              insertBefore: '#Load_JS_Files_Before_Module',
                              files: [ 
                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css',
                                  'assets/global/plugins/bootstrap-table-master/bootstrap-table.css',
                                  'assets/global/plugins/angularjs/plugins/block-ui/angular-block-ui.css',

                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js' 
                              ] 
                          });
                      }]
                  }
              })
              
                 .state('indiaSubmitalModule', {
                  url: "/indiaReports/submital",
                  templateUrl: "views/india_reports/indiasubmitals.html",   
                  params :{
          			statsUser : false,
          			fromDate :false,
          			toDate :false
          		},
                  data: {pageTitle: "India Stats  -  Submital"},
                  
                  controller: "indiaSubmitalController",
                  resolve: {
                      deps: ['$ocLazyLoad', function($ocLazyLoad) {
                          return $ocLazyLoad.load({
                              name: 'StatsModule',  
                              insertBefore: '#Load_JS_Files_Before_Module',
                              files: [ 
                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css',
                                  'assets/global/plugins/bootstrap-table-master/bootstrap-table.css',
                                  'assets/global/plugins/angularjs/plugins/block-ui/angular-block-ui.css',

                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js' 
                              ] 
                          });
                      }]
                  }
              })
              
                    .state('clientWise_indiaSubmitalStatsModule', {
                  url: "/indiaReports/clientwise_submitalStats",
                  templateUrl: "views/india_reports/clientwise_indiasubmital_stats.html",            
                  data: {pageTitle: "India Stats  -  Submital"},
                  
                  controller: "clientwise_indiaSubmitalStatsController",
                  resolve: {
                      deps: ['$ocLazyLoad', function($ocLazyLoad) {
                          return $ocLazyLoad.load({
                              name: 'StatsModule',  
                              insertBefore: '#Load_JS_Files_Before_Module',
                              files: [ 
                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css',
                                  'assets/global/plugins/bootstrap-table-master/bootstrap-table.css',
                                  'assets/global/plugins/angularjs/plugins/block-ui/angular-block-ui.css',

                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js' 
                              ] 
                          });
                      }]
                  }
              })
              
              
              
               .state('indiadmsummarymodule', {
                  url: "/indiaReports/indiadmsummaryreport",
                  templateUrl: "views/india_reports/indiadmsummaryreport.html",            
                  data: {pageTitle: "India DM Summary Report"},
                  controller: "indiadmreportcontroller",
                  resolve: {
                      deps: ['$ocLazyLoad', function($ocLazyLoad) {
                          return $ocLazyLoad.load({
                              name: 'StatsModule',  
                              insertBefore: '#Load_JS_Files_Before_Module',
                              files: [ 
                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css',
                                  'assets/global/plugins/bootstrap-table-master/bootstrap-table.css',
                                  'assets/global/plugins/angularjs/plugins/block-ui/angular-block-ui.css',

                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js' 
                              ] 
                          });
                      }]
                  }
              })
              
              
              .state('recruiterreportmodule', {
                  url: "/indiaReports/recruiter_productivity_report",
                  templateUrl: "views/india_reports/india_recruiter_productivity_report.html",            
                  data: {pageTitle: "Recruiters Productivity Report"},
                  
                  controller: "recruitersreportcontroller",
                  resolve: {
                      deps: ['$ocLazyLoad', function($ocLazyLoad) {
                          return $ocLazyLoad.load({
                              name: 'StatsModule',  
                              insertBefore: '#Load_JS_Files_Before_Module',
                              files: [ 
                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css',
                                  'assets/global/plugins/bootstrap-table-master/bootstrap-table.css',
                                  'assets/global/plugins/angularjs/plugins/block-ui/angular-block-ui.css',

                                  'assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js' 
                              ] 
                          });
                      }]
                  }
              })
              
              
		}]);
})(angular);