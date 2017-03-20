;(function(angular) {

    "use strict";

    // CgiATSapp UI Architecture by Srinivas Nagaram   

    angular.module("CgiATSapp", [
        "ui.router", 
        "ui.bootstrap", 
        "oc.lazyLoad",  
        "ngSanitize",  
        "Dashboard",
        "Candidates",
        "jobOrderModule",
        "offerletter",
        "reportsmodule",
        "ngIdle",
        "indiajobOrderModule",
        "indiasubmitalsmodule",
        "indiaReportsModule",
        "settingsModule",
        "totalreportModule",
        "customreportsmodule"
    ])  

    //AngularJS v1.3.x workaround for old style controller declarition in HTML
    .config(['$controllerProvider', function($controllerProvider) {
      // this option might be handy for migrating old apps, but please don't use it
      // in new ones!
      $controllerProvider.allowGlobals();
    }])
.config(['KeepaliveProvider', 'IdleProvider', function(KeepaliveProvider, IdleProvider) {
  IdleProvider.idle(3600);
  IdleProvider.timeout(5);
  KeepaliveProvider.interval(10);
}])

.run(['Idle','$rootScope', function(Idle,$rootScope) {
  Idle.watch();
  $rootScope.$on('IdleStart', function() { alert('Your session is going to expire');/* Display modal warning or sth */ });
  $rootScope.$on('IdleTimeout', function() { location.href = 'login.html';/* Logout user */ });
}])
    /* Setup global settings */
    .factory('settings', ['$rootScope', function($rootScope) {
        // supported languages
        var settings = {
            layout: {
                pageSidebarClosed: false, // sidebar menu state
                pageContentWhite: true, // set page content layout
                pageBodySolid: false, // solid body color state
                pageAutoScrollOnLoad: 1000 // auto scroll to top on page load
            },
            assetsPath: 'assets',
            globalPath: 'assets/global',
            layoutPath: 'assets/layouts/layout',
        };

        $rootScope.settings = settings;

        return settings;
    }])

    /* Setup App Main Controller */
    .controller('MainController', ['$scope', '$rootScope', function($scope, $rootScope) {
        $scope.$on('$viewContentLoaded', function() {
            //App.initComponents(); // init core components
            //Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive 
        }); 
    }]) 

    /* Setup Layout Part - Header */
    .controller('HeaderController', ['$scope','$http','$rootScope','$location','$state','$timeout', function($scope,$http,$rootScope,$location,$state,$timeout) {
    	$scope.$on('$stateChangeStart', function (event, next, current) {
    		var url=null;
    		
    		if($location.search().currentLoginUserId){
    			url = 'LoginController/getLoggedInPersonInfo?currentLoginUserId='+$location.search().currentLoginUserId;
    		}else{
    			url = 'LoginController/getLoggedInPersonInfo';
    		}
    		if(!$rootScope.rsLoginUser && $location.path() != '/joborders/openJobOrders'){
				  $http.get(url).success(function(data, status){
			        	if(data){
			        	$rootScope.rsLoginUser = data;
			        	$scope.checkTheAccess();
			        	}else{
			        		location.href = 'login.html';
			        	}
			        })
			        .error(function(data, status){
			        	location.href = 'login.html';
			        })
    		}else {
    			 $timeout(function() {//wait for some time to redirect to another page
    				 $scope.checkTheAccess();
    			 }, 40);
    			
    		}
    	});
    	
    	$scope.checkTheAccess = function(){
    		if($location.path() === '/joborders/myJobOrders'){
        		if(($rootScope.rsLoginUser.userRole === 'Manager' || $rootScope.rsLoginUser.userRole === 'DivisionHead' || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
//        			$state.go("accessDeniedPage");
        			$state.go("accessDeniedPage");
        		}
        	}
    		
    		if($location.path() === '/joborders/myJobOrders' || $location.path() === '/joborders/hotJobOrders' || $location.path() === '/joborders/allJobOrders'
    			 || $location.path() === '/joborders/emJobOrders' || $location.path() === '/joborders/createJobOrder' || $location.path() === '/joborders/deletedJobOrders'){
        		if(($rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
        			$state.go("accessDeniedPage");
        		}
        	}
    		
    		if(!($location.path() === '/jobboardstats' || $location.path() === '/candidates/missingData' || $location.path() === '/reports/resumes_update_count'
   			 || $location.path() === '/reports/resume_audit_logs' || $location.path() === '/myprofile' || $location.path() === '/changepassword' || 
   			 $location.path() === '/candidates/add' || $location.path() === '/candidates/search'
   			 || $location.path().startsWith('/candidates/edit'))){
       		if(($rootScope.rsLoginUser.userRole === constants.ATS_Executive)){
       			$state.go("accessDeniedPage");
       		}
       	}
    		
    		if($location.path() === '/candidates/search' || $location.path() === '/candidates/add' || $location.path() === '/candidates/view'
   			 || $location.path() === '/candidates/status'){
       		if(($rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
       			$state.go("accessDeniedPage");
       		}
    		}
    		
    		if($location.path() === '/candidates/india'){
          		if(($rootScope.rsLoginUser.userRole === constants.DM || $rootScope.rsLoginUser.userRole === constants.Recruiter 
          				|| $rootScope.rsLoginUser.userRole === constants.ADM || $rootScope.rsLoginUser.userRole === constants.Manager 
          				|| $rootScope.rsLoginUser.userRole === constants.DivisionHead)){
          			$state.go("accessDeniedPage");
          		}
       		}
    		
        	if($location.path() === '/joborders/createJobOrder'){
        		if(($rootScope.rsLoginUser.userRole === constants.Recruiter || $rootScope.rsLoginUser.userRole === 'Manager' || $rootScope.rsLoginUser.userRole === 'DivisionHead' || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/recruitersRankReport'){
        		if($rootScope.rsLoginUser.userRole === constants.Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/joborders/deletedJobOrders'){
        		if(($rootScope.rsLoginUser.userRole === constants.Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter)){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/reports/consultant/info'){
        		if(!($rootScope.rsLoginUser.userRole === 'HR')){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/offerletter/offerLetterReports'){
        		if(!($rootScope.rsLoginUser.userRole === 'HR' || $rootScope.rsLoginUser.userRole === 'DM' || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/offerletter/deleted_offerletters'){
        		if(!($rootScope.rsLoginUser.userRole === 'HR' || $rootScope.rsLoginUser.userRole === 'DM' || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/settings/appConfiguration'){
        		if(!($rootScope.rsLoginUser.userRole === 'EM' || $rootScope.rsLoginUser.userRole === 'Administrator' || $rootScope.rsLoginUser.userId === 'Hari' || $rootScope.rsLoginUser.userId === 'rvemula')){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/settings/users'){
        		if(!($rootScope.rsLoginUser.userRole === 'HR' || $rootScope.rsLoginUser.userRole === 'EM' || $rootScope.rsLoginUser.userRole === 'Administrator' || $rootScope.rsLoginUser.userId === 'Hari' || $rootScope.rsLoginUser.userId === 'rvemula')){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/settings/mailConfiguration'){
        		if(!($rootScope.rsLoginUser.userId === 'Hari')){
        			$state.go("accessDeniedPage");
        		}
        	}
        	if($location.path() === '/settings/addUser'){
        		if(!($rootScope.rsLoginUser.userId === 'Hari') && !($rootScope.rsLoginUser.userId === 'rvemula')){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/reports/recruiter_report' || $location.path() === '/customReports/clientsReport' 
         			){
        		if(($rootScope.rsLoginUser.userRole === constants.Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/reports/resume_audit_logs'
         		 || $location.path() === '/reports/loginInfo'){
        		if(($rootScope.rsLoginUser.userRole === constants.Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/reports/turnaroundreport'){
        		if(($rootScope.rsLoginUser.userRole === constants.Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter ||
        				$rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL || $rootScope.rsLoginUser.userRole === constants.ATS_Executive
        				)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/india_joborders/my_indiajobOrder' || $location.path() === '/india_joborders/create_indiajobOrder'
         		|| $location.path() === '/india_joborders/all_indiajobOrder' || $location.path() === '/india_joborders/delete_indiajobOrder'){
        		if(($rootScope.rsLoginUser.userRole === constants.DM || $rootScope.rsLoginUser.userRole === constants.Recruiter 
          				|| $rootScope.rsLoginUser.userRole === constants.ADM  
          				|| $rootScope.rsLoginUser.userRole === constants.DivisionHead)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/india_joborders/my_indiajobOrder' || $location.path() === '/india_joborders/create_indiajobOrder'){
        		if(($rootScope.rsLoginUser.userRole === 'Manager' || $rootScope.rsLoginUser.userRole === 'DivisionHead' || $rootScope.rsLoginUser.userRole === 'DM' || 
        				$rootScope.rsLoginUser.userRole === constants.Recruiter || $rootScope.rsLoginUser.userRole === 'ADM')){
        			$state.go("accessDeniedPage");
        		}
        	}
         	
         	if($location.path() === '/dashboard'  || $location.path() === '/recruitersreport' || $location.path() === '/jobboardstats'){
        		if(($rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	
         	if($location.path() === '/dmsummaryreport'){
        		if(!($rootScope.rsLoginUser.userRole === constants.Manager || $rootScope.rsLoginUser.userRole === constants.Administrator
        				|| $rootScope.rsLoginUser.userRole === constants.DM)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	
         	if($location.path() === '/customReports/dmsRankReport' || $location.path() === '/customReports/recruitersRankReport'
         		|| $location.path() === '/customReports/clientsReport' || $location.path() === '/reports/JobOrderMetric'){
         		if(!($rootScope.rsLoginUser.userRole === constants.Manager)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/reports/dm_metric'){
         		if(!($rootScope.rsLoginUser.userRole === constants.Manager || $rootScope.rsLoginUser.userRole === constants.ADM 
         				|| $rootScope.rsLoginUser.userRole === constants.DM || $rootScope.rsLoginUser.userRole === constants.IN_DM)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/reports/recruiter_metric'){
         		if(!($rootScope.rsLoginUser.userRole === constants.Manager || $rootScope.rsLoginUser.userRole === constants.ADM 
         				|| $rootScope.rsLoginUser.userRole === constants.DM 
         				|| $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_Recruiter
         				|| $rootScope.rsLoginUser.userRole === constants.IN_TL
         				|| $rootScope.rsLoginUser.userRole === constants.Recruiter)){
        			$state.go("accessDeniedPage");
        		}
        	}
         	if($location.path() === '/customReports/sourceReport'){
         		if(!($rootScope.rsLoginUser.userRole === constants.Manager || $rootScope.rsLoginUser.userRole === constants.ADM 
         				|| $rootScope.rsLoginUser.userRole === constants.DM)){
        			$state.go("accessDeniedPage");
        		}
        	}
    	}
    	
        $scope.$on('$includeContentLoaded', function() {
            Layout.initHeader(); // init header
        });
        
      //Logout action
    	$scope.logout = function(){
    		 $http({
 	            method: 'GET',
 	            url: 'LoginController/logout'
 	        })  
 	        .success(function(data, status){
 	        	$rootScope.rsLoginUser = constants.LogOut;
 	        	location.href = 'login.html';
 	        })
 	        .error(function(data, status){
 	        	location.href = 'login.html';
 	        })
    		
    	};
        
        $scope.globalTitleItems = ['All', 'Boolean'];
        $scope.globalTitleItem = "All";
        $scope.globalTitleSelected = function (item) { 
            $scope.globalTitleItem = item; 
        }
    }])

    /* Setup Layout Part - Sidebar */
    .controller('SidebarController', ['$scope', function($scope) {
        $scope.$on('$includeContentLoaded', function() {
            Layout.initSidebar(); // init sidebar
        });
    }])

    /* Setup Layout Part - Footer */
    .controller('FooterController', ['$scope', function($scope) {
    	$scope.currentYear = new Date().getFullYear();
        $scope.$on('$includeContentLoaded', function() {
            Layout.initFooter(); // init footer
        });
    }])  

    /* Configure ocLazyLoader(refer: https://github.com/ocombe/ocLazyLoad) */
    .config(['$ocLazyLoadProvider', function($ocLazyLoadProvider) {
        $ocLazyLoadProvider.config({
            // global configs go here 
        });
    }])  

    /* Init global settings and run the app */
    .run(["$rootScope", "settings", "$state", function($rootScope, settings, $state) {
        $rootScope.$state = $state; // state to be accessed from view
        $rootScope.$settings = settings; // state to be accessed from view
    }]);

})(angular);