;(function(angular){
    "use strict";
    
	angular.module("submitalsmodule",['createsubmitals','editsubmitals','submittal_list','angularjs-datetime-picker'])
	.config(['$stateProvider', '$urlRouterProvider','blockUIConfig', function($stateProvider, $urlRouterProvider,blockUIConfig){
		
	 	blockUIConfig.autoBlock = false;
    	$urlRouterProvider.otherwise("/dashboard");
    	
    	$stateProvider
    	
    	.state("createsubmitals",{
    		url: '/submitals/createsubmitals',
    		templateUrl : 'views/submitals/create_submitals.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "createsubmitalscontroller",
    		
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
    	
    	.state("submittal_list",{
    		url: '/submitals/submittal_list',
    		templateUrl : 'views/submitals/submittal_list.html',
    		data: {pataTitle: "Submittal - List"},
    		controller: "submittal_listcontroller",
    		
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
    	
    	
    	
    	
    	
    	
    	
    	
    	.state("editsubmitals",{
    		url: '/submitals/editsubmitals',
    		templateUrl : 'views/submitals/edit_submitals.html',
    		data: {pataTitle: "Candidates - Add Candidate"},
    		controller: "editsubmitalscontroller",
    		
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