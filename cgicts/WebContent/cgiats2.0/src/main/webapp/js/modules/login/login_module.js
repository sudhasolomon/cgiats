;(function(angular) {

    "use strict";
    
    angular.module('loginModule', ['login'])
    .config(['$stateProvider', '$urlRouterProvider','blockUIConfig',  function($stateProvider,  $urlRouterProvider, blockUIConfig) {
    	
    	blockUIConfig.autoBlock = false;
    	$urlRouterProvider.otherwise("/dashboard");
    	
    	$stateProvider
    	
    	.state("myJobOrders",{
    		url: '/joborders/myJobOrders',
    		templateUrl : 'views/joborders/my_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "myJobOrdersController"
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
    	
    	
    
    	
    	
    	.state("createJobOrders",{
    		url: '/joborders/createJobOrders',
    		templateUrl : 'views/joborders/create_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "createJobOrdersController"
    	})
    	
    	
    	.state("deleteJobOrders",{
    		url: '/joborders/deleteJobOrders',
    		templateUrl : 'views/joborders/delete_joborders.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "deleteJobOrdersController"
    	})
    	
    	
    	
    	
    }])
    .controller("jobOrderModuleController",function(){
    	
    });
    
})(angular);