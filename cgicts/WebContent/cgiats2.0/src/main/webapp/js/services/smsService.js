
(function(angular) {

	'use strict';
angular
.module('smsModule',[ 'ngMaterial','ngMessages', 'ui.bootstrap'])
.service('smsService',['$http','$mdDialog','$mdMedia', function ($http, $mdDialog, $mdMedia){
	
	this.sendSms = function(e, smsNums){
		if(smsNums.length>10){
			$.growl.error({title : "Limit Exceeded",
				message : "maximum no.of candidates for SMS is 10"
			});
		}else{
		 $mdDialog.show({
  		      controller: SMSDialogController,
  		      templateUrl: 'views/dialogbox/smsbox.html',
  		      parent: angular.element(document.body),
  		      targetEvent: e,
  		      clickOutsideToClose:true,
  		      locals:{
  		    	  phoneNums : smsNums
  		      }
  		    }).then (function(answer){
//   		    	alert(smsNums);
   		    //	var phoneNumbers = '919866228123,14084807165'
   		    		var sstrData = "version=2.0"  
   		    		sstrData = sstrData + "&To="+smsNums
   		    		sstrData = sstrData + "&From=27126"     
   		    		sstrData = sstrData + "&UserId=cgiats" 
   		    		sstrData = sstrData + "&Password=charter123" 
   		    		sstrData = sstrData + "&vasid=8753" 
   		    		sstrData = sstrData + "&networkid=44" 
   		    		sstrData = sstrData + "&profileid=2" 
   		    		sstrData = sstrData + "&Text="+answer 
//   		    		alert("sstrData  "+sstrData);
   		    	var response = $http.post('http://smsapi.wire2air.com/smsadmin/submitsm.aspx',sstrData,{
					headers : {
						'Content-Type' : "application/x-www-form-urlencoded"
					}
   		    	});
				response.success(function(data, status,headers, config) 
						{
//						 alert("success data "+data);
						});
				response.error(function(data, status, headers, config){
		  			  if(status == constants.FORBIDDEN){
		  				location.href = 'login.html';
		  			  }else{  			  
		  				$state.transitionTo("ErrorPage",{statusvalue  : status});
		  			  }
		  		  });
   		    })
		}
	}
	
	function SMSDialogController($scope, $mdDialog, phoneNums){
//		alert("phone numbers "+phoneNums);
		
		if(phoneNums.length <=1){
			$scope.phoneNumbers = phoneNums;
		}else{
			$scope.phoneNumbers=phoneNums.join(',');
		}
//		alert("comma "+$scope.phoneNumbers);
		 $scope.hide = function() {
	    	    $mdDialog.hide();
	    	  };

	    	  $scope.cancel = function() {
	    	    $mdDialog.cancel();
	    	  };

	    	  $scope.answer = function(answer) {
	    		  if(answer!=undefined){
						 $mdDialog.hide(answer);
						 
					 }else{
						 $scope.msg = "please enter some  text."
					 }
	    	   
	    	  };
	 }
}]);
})(angular);