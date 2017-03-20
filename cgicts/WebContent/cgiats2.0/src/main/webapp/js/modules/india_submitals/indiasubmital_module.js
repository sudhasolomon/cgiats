;(function(angular){
    "use strict";
    
	angular.module("indiasubmitalsmodule",['createindiasubmitals','editindiasubmitals'])
	.config(['$stateProvider', '$urlRouterProvider','blockUIConfig', function($stateProvider, $urlRouterProvider,blockUIConfig){
		
	 	blockUIConfig.autoBlock = false;
    	$urlRouterProvider.otherwise("/dashboard");
    	
   	$stateProvider
    	
    	.state("createindiasubmitals",{
    		url: '/india_submitals/create_indiasubmitals',
    		templateUrl : 'views/india_submitals/create_indiasubmitals.html',
    		data: {pataTitle: "Submittals - Create Submittal"},
    		controller: "createindiasubmitalscontroller",
    		
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
    	
    	.state("editindiasubmitals",{
    		url: '/india_submitals/edit_indiasubmitals',
    		templateUrl : 'views/india_submitals/edit_indiasubmitals.html',
    		data: {pataTitle: "Submittals - Edit Submittal"},
    		controller: "editindiasubmitalscontroller",
    		
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