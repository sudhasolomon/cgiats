;(function(angular){
	"use strict";	
	angular.module("changepasswordpage",['ngMaterial'])
	.controller("changepasswordcontroller",function($scope, $http,$window, $mdDialog, $mdMedia){
		
		$scope.onload = function()
		{
			$("input[ng-model='newpassword']").bootstrapPasswordStrengthMeter({
	            minPasswordLength: 0
	          });
		}
		
		
		$scope.myreset = function(e){
			$scope.newpassword = "";
			$scope.confirmpassword = "";
			$scope.currentpassword = "";
			$scope.changepwd.$setPristine()
		}
		
		$scope.pwdChange = function(e)
		{
			var nwpswd = $scope.newpassword;
			var pattern = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$/;
			if(pattern.test(nwpswd))
				{

				var newpwd = $scope.newpassword;
				var confnewpwd = $scope.confirmpassword;
				if(newpwd == confnewpwd)
				{
					var changeDetails= {
							"password" : $scope.currentpassword,
							"newPassword" : $scope.newpassword
					}
					var response = $http.post("LoginController/changePassword",changeDetails);
					response.success(function(data, status, headers, config){
						
						if(data.statusCode == 200){
						$mdDialog.show(	{
							controller : DialogController,
							template : '<md-dialog><div <md-toolbar style="background-color:#00CED1"><div class="md-toolbar-tools">'+
							'</div></md-toolbar></div><md-dialog-content><div class="md-dialog-content">'+
							'<span><b>Password has been changed successfully</b></span><br>'+
							'<span>Please re-login with new password</span><br><br> </md-dialog-content><md-dialog-actions layout="row">'+
							'<span flex></span><md-button  style="background-color:#3399FF; color:white" ng-click="answer()" style="margin-right:20px;" md-autofocus>'+
							'Ok</md-button></md-dialog-actions></md-dialog> ',
							parent : angular.element(document.body),
							targetEvent : e,
							clickOutsideToClose : true,
						}).then(function(answer) {
							window.location = "login.html";
						});
						}
						else{
							//alert(data.statusMessage);
							$(".progress-bar").css("width","0%");
							$scope.errorMsg = data.statusMessage;
							$scope.myreset();
							
						}
					});
					response.error(function(data, status, headers, config){
			  			  if(status == constants.FORBIDDEN){
			  				location.href = 'login.html';
			  			  }else{  			  
			  				$state.transitionTo("ErrorPage",{statusvalue  : status});
			  			  }
			  		  });
					$(".pwdmatch").hide();
				}
				else
				{
				$(".pwdmatch").show();
				}
			
				}
			else
				{
				$(".pwdmatch").show();
				}
			
		}
		
		$scope.chkeyup = function()
		{
			var newpwd = $scope.newpassword;
			var confnewpwd = $scope.confirmpassword;
			if(newpwd == confnewpwd)
			{
				$(".pwdmatch").hide();
			}
			else
			{
			$(".pwdmatch").show();
			}
		}
		
		$scope.chkeyup01 = function()
		{
						

			
			var nwpswd = $scope.newpassword;
			var pattern = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,}$/;
			if(pattern.test(nwpswd)){
 				   $("#wrngpatt").hide();
			}
			else
			{
				$("#wrngpatt").show();
			}
			
			
			
			
			
			var newpwd = $scope.newpassword;
			var confnewpwd = $scope.confirmpassword;
			if(confnewpwd == "" || confnewpwd == "undefined" || confnewpwd == null)
			{
			
			}
			else
			{
				if(newpwd == confnewpwd)
				{
					$(".pwdmatch").hide();
				}
				else
				{
				$(".pwdmatch").show();
				}
			}
		}
		
		
		function DialogController($scope,$mdDialog) {
			$scope.cancel = function() {
				$mdDialog.cancel();
			};
			$scope.answer = function(answer) {
				$mdDialog.hide(answer);
			};
		}

		
	});
 
})(angular);