;(function(angular){
	"use strict";	
	angular.module("myprofilepage",[])
	.controller("myprofilecontroller",function($scope,$http,$timeout,$state,$rootScope,$stateParams,resumesView,$window){
		
		$scope.onload = function()
		{
			$scope.mypagename = "My Profile";
			
			$scope.userProfile = {firstName:$rootScope.rsLoginUser.firstName,lastName:$rootScope.rsLoginUser.lastName,phone:$rootScope.rsLoginUser.phone,city:$rootScope.rsLoginUser.city,email:$rootScope.rsLoginUser.email};
			
		}
		
	    $scope.myImage='';
	    $scope.myCroppedImage= null;
	    $scope.isCroppedImageVisible = false;
	    

	    var handleFileSelect=function(evt) {
	    	$("#mainimg").show();
	      var file=evt.currentTarget.files[0];
	      $scope.isCroppedImageVisible = true;
	      var reader = new FileReader();
	      reader.onload = function (evt) {
	        $scope.$apply(function($scope){
	          $scope.myImage=evt.target.result;
	        });
	      };
	      reader.readAsDataURL(file);
	    };
	    angular.element(document.querySelector('#fileInput')).on('change',handleFileSelect);
	 
	    $scope.cropsave = function(){
	    	$("#mainimg").hide();
//	    	alert($scope.myCroppedImage);
	    }
	    $scope.cropcancel = function()
	    {
	    	 $scope.isCroppedImageVisible = false;
	    	$("#mainimg").hide();
	    	$scope.myImage='';
	    }
	    
	    $scope.uploadphoto = function()
	    {
	    	$("#fileInput").click();
	    }
		
	    
	    
	    $scope.saveOrUpdateProfile = function(){
//		alert(JSON.stringify($rootScope.rsLoginUser));
	    	$(".underlay").show();
//			alert("Data : "+angular
//					.toJson($scope.createJobOrdersFields));
			if($scope.isCroppedImageVisible){
			$rootScope.rsLoginUser.base64Image = $scope.myCroppedImage;
			}
			
			$rootScope.rsLoginUser.firstName = $scope.userProfile.firstName;
			$rootScope.rsLoginUser.lastName = $scope.userProfile.lastName;
			$rootScope.rsLoginUser.phone = $scope.userProfile.phone;
			$rootScope.rsLoginUser.city = $scope.userProfile.city;
			$rootScope.rsLoginUser.email = $scope.userProfile.email;
//			alert("formData::"+formData);
			var response = $http.post('userController/saveOrUpdateUser', $rootScope.rsLoginUser);
			//var response = $http.post("",formData);
			response.success(function (data,status,headers,config){
//				alert('siva');
				$rootScope.rsLoginUser = data;
				$.growl.success({title : "Info !", message : "Profile Updated Successfully"});
				 $(".underlay").hide();
				 if($rootScope.rsLoginUser.userRole.startsWith('IN_')){
			    		$state.go("myIndiaJobOrders");
			    	}else if($rootScope.rsLoginUser.userRole === constants.ATS_Executive){
			    		$state.go("MissingDataInfo");
			    	}else{
			    		$state.go("dashboard");
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
	
	$scope.cancelprofile = function()
	{
		if($rootScope.rsLoginUser.userRole.startsWith('IN_')){
    		$state.go("myIndiaJobOrders");
    	}else if($rootScope.rsLoginUser.userRole === constants.ATS_Executive){
    		$state.go("MissingDataInfo");
    	}else{
    		$state.go("dashboard");
    	}
	}
	
	
	});
	
	
})(angular);