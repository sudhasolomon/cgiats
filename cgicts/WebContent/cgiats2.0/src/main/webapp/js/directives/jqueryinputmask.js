/**
 * @license ng-bs-daterangepicker v0.0.5
 * (c) 2013 Luis Farzati http://github.com/luisfarzati/ng-bs-daterangepicker
 * License: MIT
 */
(function(angular) {

	'use strict';

	angular
		.module('InputMask', [])
		.directive('uiPhone', function() {
		    return {
		      require: '?ngModel',
		      link: function($scope, element, attrs, controller) {
		          element.mask("(999) 999 9999",{completed: function() {
		              
		              $scope.$apply();
		          }});
		      }
		    };
		  })
		  
		  
		  
		  .directive('uiDate', function() {
			    return {
			      require: '?ngModel',
			      link: function($scope, element, attrs, controller) {
			          element.mask("99/99/9999",{completed: function() {
			              $scope.$apply();
			          }});
			      }
			    };
			  });

})(angular);