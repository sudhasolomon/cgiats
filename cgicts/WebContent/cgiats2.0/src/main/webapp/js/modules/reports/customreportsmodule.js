;(function(angular){
	"use strict";
	
			angular.module("customreportsmodule",['locationreportmodule', 'customdmreportmodule', 'customrecruiterreportmodule', 'dm_clientreportmodule','joborderreportmodule',
			                                     'sourcereportmodule','clientsreportmodule','clientDetailsreportmodule'])
			.config(['$stateProvider', '$urlRouterProvider', function($stateProvider,$urlRouterProvider){
				
				$urlRouterProvider.otherwise("/dashboard");
				
				$stateProvider

				.state('joborderreportmodule', {
                    url: "/customReports/jobOrderReport",
                    templateUrl: "views/reports/customjoborderreport.html",  
                    data: {pageTitle: 'Job Order Report'},
                    controller: "joborderreportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                  //  name: 'viewcandidate',  
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
                .state('dm_clientreportmodule', {
                    url: "/customReports/DMClientReport",
                    templateUrl: "views/reports/dm_clientreport.html",  
                    data: {pageTitle: 'DM/Client Report'},
                    controller: "dm_clientreportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                  //  name: 'viewcandidate',  
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
                .state('customrecruiterreportmodule', {
                    url: "/customReports/recruitersRankReport",
                    templateUrl: "views/reports/customrecruiterreport.html",  
                    data: {pageTitle: 'Recruiters Rank Report'},
                    controller: "customrecruiterreportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                    //name: 'viewcandidate',  
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
                .state('customdmreportmodule', {
                    url: "/customReports/dmsRankReport",
                    templateUrl: "views/reports/customdmreport.html",  
                    data: {pageTitle: 'DMs Rank Report'},
                    controller: "customdmreportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                   // name: 'viewcandidate',  
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
                
                
                
                
                 .state('locationreportmodule', {
                    url: "/reports/locationReport",
                    templateUrl: "views/reports/locationreport.html",  
                    data: {pageTitle: 'Location Report'},
                    controller: "locationreportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                   // name: 'viewcandidate',  
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
                
                
                
                
                
                
                .state('sourcereportmodule', {
                    url: "/customReports/sourceReport",
                    templateUrl: "views/reports/source_report.html",  
                    data: {pageTitle: 'DMs Rank Report'},
                    controller: "sourcereportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                   // name: 'viewcandidate',  
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
                
                	.state('clientsreportmodule', {
                    url: "/customReports/clientsReport",
                    templateUrl: "views/reports/client_report.html",  
                    data: {pageTitle: 'Clients Report'},
                    controller: "clientsreportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                  //  name: 'viewcandidate',  
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
                
                     	.state('clientDetailsreportmodule', {
                    url: "/customReports/clientDetailsReport",
                    templateUrl: "views/reports/clientDetails.html",  
                    data: {pageTitle: 'Client Details Report'},
                    controller: "clientDetailsreportcontroller" ,
                    	resolve: {
                            deps: ['$ocLazyLoad', function($ocLazyLoad) {
                                return $ocLazyLoad.load({
                                  //  name: 'viewcandidate',  
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
                
                
			}])
})(angular);