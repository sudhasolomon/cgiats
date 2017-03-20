;(function(angular) {

    "use strict";
    
    angular.module('indiajobOrderModule', ['createIndiaJobOrders','myIndiaJobOrders','allIndiaJobOrders','deleteIndiaJobOrders', 'angucomplete-alt'])
    .config(['$stateProvider', '$urlRouterProvider','blockUIConfig',  function($stateProvider,  $urlRouterProvider, blockUIConfig) {
    	
    	blockUIConfig.autoBlock = false;
    	$urlRouterProvider.otherwise("/dashboard");
    	
    	$stateProvider
    	
    	.state("createIndiaJobOrders",{
    		url: '/india_joborders/create_indiajobOrder',
    		templateUrl : 'views/india_joborders/create_indiajoborders.html',
    		data: {pataTitle: "Job Orders - Create India Job Order"},
    		controller: "createIndiaJobOrdersController"
    	})
    	
    	.state("editIndiaJobOrders",{
    		url: '/india_joborders/edit_indiajobOrders/:jobOrderId',
    		templateUrl : 'views/india_joborders/edit_indiajoborders.html',
    		data: {pataTitle: "JobOrder - Edit India Job Order"},
    		controller: "createIndiaJobOrdersController"
    	})
//    	/:statsUser/:fromDate/:toDate
    	
       .state("myIndiaJobOrders",{
    		url: '/india_joborders/my_indiajobOrder',
    		templateUrl : 'views/india_joborders/my_indiajoborders.html',
    		params :{
    			statsUser : false,
    			fromDate :false,
    			toDate :false
    		},
    		data: {pataTitle: "Job Orders - My Job Order"},
    		controller: "myIndiaJobOrdersController",
    		
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
    	
     	.state("allIndiaJobOrders",{
    		url: '/india_joborders/all_indiajobOrder',
    		templateUrl : 'views/india_joborders/all_indiajoborders.html',
    		data: {pataTitle: "Job Orders - All Job Order"},
    		controller: "allIndiaJobOrdersController",
    		
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
    	
    	 	.state("deleteIndiaJobOrders",{
    		url: '/india_joborders/delete_indiajobOrder',
    		templateUrl : 'views/india_joborders/delete_indiajoborders.html',
    		data: {pataTitle: "Job Orders - Delete Job Order"},
    		controller: "deleteIndiaJobOrdersController",
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
    
    	
    })(angular);