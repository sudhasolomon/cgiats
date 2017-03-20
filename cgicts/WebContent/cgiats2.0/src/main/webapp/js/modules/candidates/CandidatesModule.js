;(function(angular) {

    "use strict";

    angular.module('Candidates', ['ngStorage','bsTable', 'blockUI', 'SearchModule', 'DatePicker', 'jcs-autoValidate',
                                  'AddEditCandidateModule','viewcandidate', 'ErrorPage','accessDeniedPage', 'InputMask','indiacandidate','AddEditIndiaCandidates',
                                  ,'candidatesStatus','smsModule','mailModule','MissingDataInfo','angularCharts']) 
            .config(['$stateProvider', '$urlRouterProvider', 'blockUIConfig', function($stateProvider, $urlRouterProvider, blockUIConfig,$scope) { 

                blockUIConfig.autoBlock = false;
                // Redirect any unmatched url
                $urlRouterProvider.otherwise("/dashboard");  
                $stateProvider

                    // Search Candidates
                    .state('SearchModule', {
                        url: "/candidates/search",
                        templateUrl: "views/candidates/search.html",            
                        data: {pageTitle: "Candidates  -  Search"},
                        params: {newCandidate:false,
                        	     editCandidate:false,
                        	     searchToggle:false},
                        controller: "SearchController",
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

                    .state('AddEditCandidateModule', { 
                        url: "/candidates/add",
                        templateUrl: "views/candidates/AddCandidate.html",
                        data: {pataTitle: "Candidates - Add Candidate"},
                        controller: "CandidatesController" 
                    }) 
                    
                    .state('ErrorPage',{
                    	 url: "/candidates/error page/:statusvalue",
                         templateUrl: "views/candidates/error_page.html",
                         data: {pataTitle: "Error page"},
                         controller: "errorPageController"
                        
                    })
                    
                    .state('accessDeniedPage',{
                    	 url: "/access/denied",
                         templateUrl: "views/others/access_denied.html",
                         data: {pataTitle: "Access Denied Page"},
                         controller: "accessDeniedController"
                        
                    })

                    .state('EditCandidate', {  
                        url: "/candidates/edit/:candidateId/:page",
                        templateUrl: "views/candidates/EditCandidate.html",
                        data: {pataTitle: "Candidates - Edit Candidate"},
                        controller: "CandidatesController"
                    })
                    
                    .state('viewcandidate',{
                    	url:"/candidates/view",
                    	templateUrl: "views/candidates/viewcandidates.html",
                    	params: {editCandidate:false},
                    	data: {pataTitle:"Candidates - View Candidate"},
                    	controller: "ViewCandidateController",
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
                     .state('indiacandidate',{
                    	url:"/candidates/india",
                    	templateUrl: "views/candidates/indiacandidates.html",
                    	data: {pataTitle:"Candidates - India Candidate"},
                    	 params: {newCandidate:false,
                      	     editCandidate:false},
                    	controller: "IndiaCandidateController",
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
                    .state('AddEditIndiaCandidates', { 
                        url: "/candidates/addIndia",
                        templateUrl: "views/candidates/addIndiaCandidates.html",
                        data: {pataTitle: "Candidates - Add India Candidates"},
                        controller: "AddEditIndiaController" 
                    }) 
                     .state('EditIndiaCandidates', { 
                        url: "/candidates/editIndia/:candidateId",
                        templateUrl: "views/candidates/editIndiaCandidates.html",
                        data: {pataTitle: "Candidates - Add India Candidates"},
                        controller: "AddEditIndiaController" 
                    }) 
                    .state('candidatesStatus',{
                    	url:"/candidates/status",
                    	templateUrl: "views/candidates/candidates_status.html",
                    	params: {editCandidate:false},
                    	data: {pataTitle:"Candidates - Candidates Status"},
                    	controller: "CandidatesStatusController",
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
                    .state('MissingDataInfo',{
                    	url:"/candidates/missingData",
                    	templateUrl: "views/candidates/missingdatainfo.html",
                    	params: {editCandidate:false},
                    	data: {pataTitle:"Candidates - Missing Data Info"},
                    	controller: "MissingDataInfoController",
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