;(function(angular) {

    "use strict";

    angular.module('Dashboard', ['DatePicker', 'ngAutocomplete', 'helppage', 'changepasswordpage', 'myprofilepage','dmsummaryreport', 'recruitersreportmodule',  'ngImgCrop', 'jobBoardStatsModule', 'newrecruitersreportmodule'])
            .config(['$stateProvider', '$urlRouterProvider',  function($stateProvider,  $urlRouterProvider) {
                // Redirect any unmatched url
                $urlRouterProvider.otherwise("/dashboard"); 
                
                $stateProvider

                    // Dashboard
                .state('dashboard', {
                    url: "/dashboard",
                    templateUrl: "views/dashboard/dashboard.html",            
                    data: {pageTitle: 'Dashboard'},
                    controller: "DashboardController"  
                })

                
                
                .state('help', {
                    url: "/help",
                    templateUrl: "views/help/help.html",            
                    data: {pageTitle: 'Help Page'},
                    controller: "DashboardController"  
                })
                
                .state('changepassword', {
                    url: "/changepassword",
                    templateUrl: "views/myaccount/changepassword.html",            
                    data: {pageTitle: 'Change Password Page'},
                    controller: "DashboardController"  
                })
                
                
                .state('myprofile', {
                    url: "/myprofile",
                    templateUrl: "views/myaccount/myprofile.html",            
                    data: {pageTitle: 'My Profile Page'},
                    controller: "DashboardController"  
                })
                
                
                
                .state('dmsummaryreport', {
                    url: "/dmsummaryreport",
                    templateUrl: "views/dashboard/DMsummaryreport.html",            
                    data: {pageTitle: 'DM Summary Report'},
                    controller: "dmsummaryrptcontroller",
                    
                    
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
                
                
                 .state('jobBoardStatsModule', {
                    url: "/jobboardstats",
                    templateUrl: "views/dashboard/jobboardstats.html",            
                    data: {pageTitle: 'DM Summary Report'},
                    controller: "jobboardstatscontroller",
                    
                    
                	resolve: {
                        deps: ['$ocLazyLoad', function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'jobboardstats',  
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
                
                
                
                
                
                
                
                
                
                
                .state('recruitersreportmodule', {
                    url: "/recruitersreport",
                    templateUrl: "views/dashboard/recruitersreport.html",            
                    data: {pageTitle: 'Recruiters Summary Report'},
                    controller: "DBrecruitersreportcontroller",
                    
                    
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
                
                
                
                
                
                
                
                .state('newrecruitersreportmodule', {
                    url: "/recruitersRankReport",
                    templateUrl: "views/dashboard/new_recruitersreport.html",            
                    data: {pageTitle: 'Recruiters Rank Report'},
                    controller: "newrecruitersreportcontroller",
                    
                    
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
            .controller('DashboardController', function($rootScope, $scope,$http,$location) {  
            	//Get the login person information
         
//            	$scope.getLoggedInPersonInfo();
            	$scope.dashboarddata = {};
            	$scope.onload = function()
            	{
            		
            		$scope.currentYear = new Date().getFullYear();
            		$scope.previousYear = (new Date().getFullYear())-1;
            		
            		
            		$(".underlay").show();
            	 var response = $http.get("dashBoardController/getDashBoardStats");
            	 response.success(function(data, status,headers, config){
            		 $scope.dashboarddata = data;
            		 $(".underlay").hide();
            		 $scope.indiaDashBoardStats();
            	 });
            	 response.error(function(data, status,headers, config){
            		 $(".underlay").hide();
            	 });
            		
            	 $scope.getFieldValue = function(fieldValue){
         			var num = fieldValue?fieldValue:0
         			
         			return num.toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,");
         		}
            	 
            	 
            	 //////India dashboard details////////
            	 
            
            	}
            	
            	
            	$scope.indiaDashBoardStats = function(){
            		 var response = $http.get("dashBoardController/getIndiaDashBoardStats");
                	 response.success(function(data, status,headers, config){
                		 $scope.indiadashboarddata = data;
                		 $(".underlay").hide();
                	 });
                	 response.error(function(data, status,headers, config){
                		 $(".underlay").hide();
                	 });
            	}
            	
            }); 

})(angular);