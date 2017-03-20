;(function(angular){
	"use strict";
	
	angular.module("mailConfigModule",[])
	.controller("mailConfigController", function($scope, $http, $state, $mdDialog){
		$scope.mailConfigurationTable = false;
		$scope.mailConfig=[];
		$scope.mailConfiguration = function(){
			
			$scope.mailConfigurationTable = true;
		
			var response = $http.get('config/emailConfiguration?reportName='
					+ $scope.reportName);
			response.success(function(data, status,headers, config) 
					{
					mailCofigurationTable();
					$scope.mailConfig.mailConfigurationTableControl.options.data = data;
					
					});
			response.error(function(data, status, headers, config){
    			  if(status == constants.FORBIDDEN){
    				location.href = 'login.html';
    			  }else{  			  
    				  $state.transitionTo("ErrorPage",{statusvalue  : status});
    			  }
    		  });
			
		}
		
    	function mailCofigurationTable(){
    		
            $scope.mailConfig.mailConfigurationTableControl = {
                    options: { 
                        striped: true,
                        pagination: true,
                        paginationVAlign: "bottom", 
                        pageList: [50,100,200],
                        search: false,
                        //sidePagination : 'server',
                        silentSort: false,
                        showColumns: false,
                        showRefresh: false,
                        clickToSelect: false,
                        showToggle: false,
                        maintainSelected: true, 
                        columns: [
                        {
                            field: 'reportName',
                            title: 'Report Name',
                            align: 'left'
                            
                        }, {
                            field: 'emails',
                            title: 'Email',
                            align: 'left'
                           
                        },
                        {
                            field: 'Delete',
                            title: 'Delete',
                            events: window.deleteMailEvent,
                            formatter : deleteMail,
                            align: 'left'
                           
                        }]
                    }
            };
            
            
            function deleteMail(value, row,
					index){
            	return [
						
            	        '<a class="remove actionIcons" title="Remove"><i class="fa fa-trash-o" style="font-size:12px;"></i></a>',
						 ]
						.join('');
			}
            
            window.deleteMailEvent = {
            		
    				'click .remove' : function(e,value, row, index) {
    					var response = $http.get('config/deleteEmail?reportName='+row.reportName+'&email='+row.emails);
    					response.success(function(data, status,headers, config) 
    							{
    							$.growl.notice({message : "Email deleted Successfully"});
    							$scope.mailConfiguration();
    							});
    					response.error(function(data, status, headers, config){
    		    			  if(status == constants.FORBIDDEN){
    		    				location.href = 'login.html';
    		    			  }else{  			  
    		    				  $state.transitionTo("ErrorPage",{statusvalue  : status});
    		    			  }
    		    		  });
    					},
    			}
    	}
    	
    	$scope.configureEmail = function(){
    		$mdDialog.show(	{
				controller : DialogController,
				templateUrl : 'views/dialogbox/emailconfiguredialogbox.html',
				parent : angular.element(document.body),
				clickOutsideToClose : true,
			})
	.then(function(config) {
		if(config){
		var reportName= config.reportName;
				var response = $http.get('config/saveEmailConfig?reportName='+ config.reportName+'&email='+config.email);
				response.success(function(data, status,headers, config) 
						{
						$.growl.notice({message : data.statusMessage});
						
						if(reportName == $scope.reportName){
							$scope.mailConfiguration();
						}
						
						});
				response.error(function(data, status, headers, config){
        			  if(status == constants.FORBIDDEN){
        				location.href = 'login.html';
        			  }else{  			  
        				  $state.transitionTo("ErrorPage",{statusvalue  : status});
        			  }
        		  });
		}
			},function(){
			});
    		
    	}
    	function DialogController($scope,
				$mdDialog) {
    		$scope.config = {};
			$scope.hide = function() {
				$mdDialog.hide(null);
			};

			$scope.cancel = function() {
				$mdDialog.cancel();
			};

			$scope.answer = function(config) {
				if(config.reportName==undefined || config.email==undefined ||config.reportName=='' || config.email==''){
					$scope.errMsg = "Select Both Report Name and Email";
				}else if(config.reportName && config.email){
					$scope.errMsg = "";
					$mdDialog.hide(config);
				}else{
					$scope.errMsg = "Select Both Report Name and Email";
				}
				
			};

		}
		
	});
})(angular);