;(function(angular) {

    "use strict";
    
    angular.module('jobOrderModule', ['ngStorage','dateRangeModule','ngCsv', 'bsTable', 'blockUI','DatePicker','ngJsonExportExcel', 'ngAutocomplete', 'myJobOrders', 'createJobOrders', 'allJobOrders','emJobOrders','openJobOrders',  'deleteJobOrders','submitalsmodule','hotJobOrders', 'submitprofilemodule','angucomplete-alt','resultJobOrdersModule', 'pendingJobOrders'])
    .config(['$stateProvider', '$urlRouterProvider','blockUIConfig',  function($stateProvider,  $urlRouterProvider, blockUIConfig) {
    	
    	blockUIConfig.autoBlock = false;
    	$urlRouterProvider.otherwise("/dashboard");
    	
    	$stateProvider
    	
    	.state("myJobOrders",{
    		url: '/joborders/myJobOrders',
    		templateUrl : 'views/joborders/my_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "myJobOrdersController",
    		
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'viewcandidate',  
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
    	
    	.state("resultJobOrdersModule",{
    		url: '/joborders/resultJObOrder',
    		templateUrl : 'views/joborders/result_joborders.html',
    		data: {pataTitle: "JobOrder - Result Page"},
    		controller: "resultJobOrdersController",
    		
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'ResultJobOrders',  
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
    	
    	
    	
    	
    		.state("allJobOrders",{
    		url: '/joborders/allJobOrders',
    		templateUrl : 'views/joborders/all_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "allJobOrdersController",
    	
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'viewcandidate',  
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
    	
    	.state("pendingJobOrders",{
    		url: '/joborders/pendingJobOrders',
    		templateUrl : 'views/joborders/pending_joborders.html',
    		data: {pataTitle: "Job Orders - pending Job Orders"},
    		controller: "pendingJobOrdersController",
    	
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'PendingJobOrder',  
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
    	
    	.state("hotJobOrders",{
    		url: '/joborders/hotJobOrders',
    		templateUrl : 'views/joborders/hot_joborders.html',
    		data: {pataTitle: "Job Orders - Hot Job Orders"},
    		controller: "hotJobOrdersController",
    	
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'viewcandidate',  
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
    
    	.state("emJobOrders",{
    		url: '/joborders/emJobOrders',
    		templateUrl : 'views/joborders/em_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "emJobOrdersController",
    	
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'viewcandidate',  
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
    	
    	
    
    	
    	
    	.state("createJobOrders",{
    		url: '/joborders/createJobOrder',
    		templateUrl : 'views/joborders/create_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "createJobOrdersController"
    	})
    	
    	
    	.state("editJobOrders",{
    		url: '/joborders/editJobOrders/:jobOrderId/:dmName/:page',
    		templateUrl : 'views/joborders/edit_joborders.html',
    		data: {pataTitle: "JobOrder - Edit Job Order"},
    		controller: "createJobOrdersController"
    	})
    	
    	.state("openJobOrders",{
    		url: '/joborders/openJobOrders',
    		templateUrl : 'views/joborders/open_joborders.html',
    		data: {pataTitle: "JobOrder - Open Job Order"},
    		controller: "openJobOrdersController",
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'viewcandidate',  
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
    	
    	
    	
    	.state("deleteJobOrders",{
    		url: '/joborders/deletedJobOrders',
    		templateUrl : 'views/joborders/delete_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "deleteJobOrdersController",
    		
    		resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'viewcandidate',  
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
    .controller("jobOrderModuleController",function(){
    	
    });
    
})(angular);