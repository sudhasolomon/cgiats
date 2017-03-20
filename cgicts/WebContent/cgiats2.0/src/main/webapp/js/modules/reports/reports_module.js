;(function(angular) {

    "use strict";


 
    angular.module('reportsmodule', ['DatePicker', 'ngAutocomplete', 'helppage', 'consultantinfopage', 'fillprojectopage', 'checklist-model', 
                                     'resumesupdatecountreport' , 'recmodule', 'liverecruitersreportmodule', 'recruitersreportbysourcemodule','totalreportModule', 
                                     'dmwisereportModule', 'dmreportModule', 'recruitermetreportModule','barReportsModule','stackedbarreportModule','recruiterLevelModule', 
                                     'clientmetricModule', 'titlewisemetricModule','loginInfoModule','resumeAuditLogsReportModule','turnaroundemodule'])


            .config(['$stateProvider', '$urlRouterProvider',  function($stateProvider,  $urlRouterProvider) {
                // Redirect any unmatched url
                $urlRouterProvider.otherwise("/dashboard"); 
                
                $stateProvider

                 
                .state('consultantinfo', {
                    url: "/reports/consultant/info",
                    templateUrl: "views/reports/consultant_info.html",  
                    params : {cancelButton : false,
                    	saveOrUpdate : false},
                    data: {pageTitle: 'Consultant Info'},
                    controller: "consultantinfocontroller" ,
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
                
                
                
                
                .state('fillprojectdetails', {
                    url: "/reports/fillprojectdetails/:id/:infoId",
                    templateUrl: "views/reports/fillproject_details.html",            
                    data: {pageTitle: 'Fill Project Details'},
                    controller: "consultantinfocontroller"  
                })

                
                  .state('resumesupdatecountreport', {
                    url: "/reports/resumes_update_count",
                    templateUrl: "views/reports/resumes_updatecount_report.html",            
                    data: {pageTitle: 'Resumes Update Count Report'},
                    controller: "resumesupdatecountcontroller",
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
                
                

                
                   .state('recmodule', {
                    url: "/reports/recruiter_report",
                    templateUrl: "views/reports/Recruiter_report.html",            
                    data: {pageTitle: 'Recuiters Performance Report'},
                    controller: "recsreportcontroller" ,
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
                
                
                
                .state('liverecruitersreportmodule', {
                    url: "/reports/liverecruitersreport",
                    templateUrl: "views/reports/liverecruitersreport.html",            
                    data: {pageTitle: 'Live Recruiters Performance Report'},
                    controller: "liverecruitersreportcontroller"  ,
                    
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
                
                
                
                
                
                
                
                
                
                
                .state('recruitersreportbysourcemodule', {
                    url: "/RecruitersReportBySource",
                    templateUrl: "views/reports/recruiters_report_by_source.html",            
                    data: {pageTitle: 'Recruiters Report By Source'},
                    controller: "recruitersreportbysourcecontroller"  ,
                    
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
                
                 
                .state('dmreportModule', {
                    url: "/reports/dm_metric",
                    templateUrl: "views/reports/Dm_Metric.html",            
                    data: {pageTitle: 'DM Metric'},
                    controller: "dmreportcontroller",
                    resolve: {
                        deps: ['$ocLazyLoad', function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'dmreportModule',  
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
                
                
                
                
                .state('recruitermetreportModule', {
                    url: "/reports/recruiter_metric",
                    templateUrl: "views/reports/recruiter_Metric.html",            
                    data: {pageTitle: 'Recruiter Metric'},
                    controller: "recruitermetreportcontroller",
                    resolve: {
                        deps: ['$ocLazyLoad', function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'recruitermetreportModule',  
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
                
                
                .state('totalreportModule', {
                    url: "/reports/JobOrderMetric",
                    templateUrl: "views/reports/total_reports.html",            
                    data: {pageTitle: 'Job Order Metric'},
                    controller: "totalreportcontroller"  ,
                    
                })
                
                
                .state('dmwisereportModule', {
                    url: "/reports/dmwisejobordersreport",
                    templateUrl: "views/reports/dmwisejoborders.html",            
                    data: {pageTitle: 'DM Wise Job Order Report'},
                    controller: "dmwisereportcontroller",
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
                
                .state('barReportsModule', {
                    url: "/reports/barreport",
                    templateUrl: "views/reports/bar_reports.html",            
                    data: {pageTitle: 'Fill Project Details'},
                    controller: "barreportscontroller"  
                })
                
                     .state('stackedbarreportModule', {
                    url: "/reports/stackedbarreport",
                    templateUrl: "views/reports/stacked_bar_report.html",            
                    data: {pageTitle: 'Stacked Bar Report'},
                    controller: "stackedbarreportcontroller"  ,
                    
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
                
                .state('recruiterLevelModule', {
                    url: "/reports/recruiterLevel/:jobOrderId",
                    templateUrl: "views/reports/recruiterLevelReport.html",            
                    data: {pageTitle: 'Recruiter Wise Reports'},
                    controller: "recruiterlevelcontroller"  ,
                    
                    resolve: {
                        deps: ['$ocLazyLoad', function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'RecruiterLevelView',  
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
                
                
                
                
                .state('clientmetricModule', {
                    url: "/reports/ClientMetric",
                    templateUrl: "views/reports/client_metric.html",            
                    data: {pageTitle: 'Client Metric'},
                    controller: "clientmetriccontroller"  ,
                })
                
                
                
                .state('titlewisemetricModule', {
                    url: "/reports/TitleWiseMetric",
                    templateUrl: "views/reports/titlewise_metric.html",            
                    data: {pageTitle: 'Title Wise Metric'},
                    controller: "titlewisemetriccontroller"  ,

                    resolve: {
                        deps: ['$ocLazyLoad', function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'RecruiterLevelView',  
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
                
                .state('loginInfo', {
                    url: "/reports/loginInfo",
                    templateUrl: "views/reports/login_info.html",            
                    data: {pageTitle: 'Login Info'},
                    controller: "loginInfoController"  ,

                    resolve: {
                        deps: ['$ocLazyLoad', function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'RecruiterLevelView',  
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
                
                .state('turnaroundemodule', {
                    url: "/reports/turnaroundreport",
                    templateUrl: "views/reports/turnaroundreport.html",            
                    data: {pageTitle: 'Turn Around'},
                    controller: "turnAroundController"  ,

                    resolve: {
                        deps: ['$ocLazyLoad', function($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'TurnAroundView',  
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
                
 
                    .state('resumeAuditLogsReportModule', {
                    url: "/reports/resume_audit_logs",
                    templateUrl: "views/reports/resume_audit_log_report.html",            
                    data: {pageTitle: 'Resume Audit Logs Report'},
                    controller: "resumeauditreportcontroller",
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