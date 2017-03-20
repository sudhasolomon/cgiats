;(function(angular) {

    "use strict";

    angular.module('offerletter', ['DatePicker', 'ngAutocomplete', 'createofferletter','checklist-model','valid-number','offerLetterReports','offerletterstatus','deletedOfferLetters'])
            .config(['$stateProvider', '$urlRouterProvider',  function($stateProvider,  $urlRouterProvider) {

                $urlRouterProvider.otherwise("/dashboard"); 
                
                $stateProvider
               
                .state('createofferletter', {
                    url: "/offerletter/createofferletter",
                    templateUrl: "views/offerletter/create_offerletter.html",            
                    data: {pageTitle: 'Create Offer Letter Page'},
                    controller: "createofferlettercontroller"  
                })
                
                
                
                .state('offerletterstatus', {
                    url: "/offerletter/offerletterstatus",
                    templateUrl: "views/offerletter/offerletter_status.html",            
                    data: {pageTitle: 'Offer Letter Status'},
                    controller: "offerletterstatuscontroller"  
                })
                
                                
                .state("offerLetterReports",{
                	url: "/offerletter/offerLetterReports",
                	 templateUrl: "views/offerletter/offerletter_reports.html",       
		    		data: {pataTitle: "Offer Letter Reports"},
		    		controller: "offerLetterReportscontroller",
		    	
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
                
                .state("deltedOfferLetters",{
                	url: "/offerletter/deleted_offerletters",
                	 templateUrl: "views/offerletter/deleted_offerletters.html",       
		    		data: {pataTitle: "Deleted Offer Letters"},
		    		controller: "deletedOfferLetterscontroller",
		    	
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