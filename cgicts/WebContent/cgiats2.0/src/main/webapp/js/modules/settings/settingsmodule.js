;(function (angular){
	
	"use strict"
	
	angular.module('settingsModule',['usersModule', 'addUserModule', 'appConfigModule', 'mailConfigModule'])
	.config(['$stateProvider', '$urlRouterProvider',  function($stateProvider,  $urlRouterProvider){
		$urlRouterProvider.otherwise("/dashboard");
		
		$stateProvider
		
		.state('usersModule', {
            url: "/settings/users",
            templateUrl: "views/settings/userDetails.html",            
            data: {pageTitle: 'User Details'},
            params : {
            	create : false,
            	edit : false
            },
            controller: "userController",
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
        
        
        .state('addUserModule', {
            url: "/settings/addUser",
            templateUrl: "views/settings/addUser.html",            
            data: {pageTitle: 'User Details'},
            controller: "addUserController",
        })
        
           .state('editUserModule', {
            url: "/settings/editUser/:userId/:userRole",
            templateUrl: "views/settings/addUser.html",            
            data: {pageTitle: 'User Details'},
            controller: "addUserController",
        })
        
        .state('appConfigModule', {
            url: "/settings/appConfiguration",
            templateUrl: "views/settings/appConfiguration.html",            
            data: {pageTitle: 'App Configuration'},
            controller: "appConfigController",
        })
        
        .state('mailConfigModule', {
            url: "/settings/mailConfiguration",
            templateUrl: "views/settings/mailConfiguration.html",            
            data: {pageTitle: 'Mail Configuration'},
            controller: "mailConfigController",
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
	}]);
})(angular);