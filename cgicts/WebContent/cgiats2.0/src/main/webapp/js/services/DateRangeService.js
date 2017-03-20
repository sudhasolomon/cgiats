
(function(angular) {

	'use strict';
angular
.module('dateRangeModule',['ui.bootstrap'])
.service('dateRangeService',['$http', function ($http){
	
	this.selectedDateAction = function(dateRangeActiveValue,startAndEndDateObj){
//		alert(JSON.stringify(startAndEndDateObj)+' ss');
//		var myMomentStartDate= moment(startAndEndDateObj.endDate, "YYYY-MM-DD").add(1,'days');
//		alert(JSON.stringify(formatteddate(startAndEndDateObj.endDate))+" :: "+ JSON.stringify(startAndEndDateObj)+"::"+JSON.stringify(moment()));
		
		if(dateRangeActiveValue === constants.TODAY){
			return {endDate: moment(), startDate: moment()};
		}
		if(dateRangeActiveValue === constants.LAST_ONE_MONTH){
			return {startDate: moment().subtract(1, 'month'), endDate: moment()};
		}
		if(dateRangeActiveValue === constants.LAST_ONE_YEAR){
			return {startDate: moment().subtract(12, 'month'), endDate: moment()};
		}
		if(dateRangeActiveValue === constants.LAST_SIX_MONTHS){
			return {startDate: moment().subtract(6, 'month'), endDate: moment()};
		}
		if(dateRangeActiveValue === constants.LAST_THREE_MONTHS){
			return {startDate: moment().subtract(3, 'month'), endDate: moment()};
		}
		if(dateRangeActiveValue === constants.ALLTIME){
			return {startDate: formatteddate(startAndEndDateObj.startDate), endDate: moment()};
		}
		if(dateRangeActiveValue === constants.CUSTOM_RANGE){
			
			return {startDate: formatteddate(startAndEndDateObj.startDate), endDate: startAndEndDateObj.endDate};
		}
	};
	
	this.formatDate = function(inputDate){
		if(inputDate instanceof Date){
		var expDate = new Date(inputDate);
    	 var month = '' + (expDate.getMonth() + 1);
         var day = '' + expDate.getDate();
        var  year = expDate.getFullYear();
    	  if (month.length < 2) month = '0' + month;
    	    if (day.length < 2) day = '0' + day;
    	   return [month, (day),year].join('-');
		}else{
			return inputDate;
		}
	};
	
	this.convertStringToDate = function(strDate){
		if(!(strDate instanceof Date)){
		var pattern = /(\d{2})\-(\d{2})\-(\d{4})/;
		var dateArray = pattern.exec(strDate); 
		var dateObject = new Date(
			    (+dateArray[3]),
			    (+dateArray[1])-1, // Careful, month starts at 0!
			    (+dateArray[2])
			);
		return dateObject;
		}
	}
	
	this.convertStringToDate_india = function(strDate){
		if(!(strDate instanceof Date)){
		var pattern = /(\d{2})\-(\d{2})\-(\d{4})/;
		var dateArray = pattern.exec(strDate); 
		var dateObject = new Date(
			    (+dateArray[3]),
			    (+dateArray[2])-1, // Careful, month starts at 0!
			    (+dateArray[1])
			);
		return dateObject;
		}
	}
	
	this.formatDate_india = function(inputDate){
		if(inputDate instanceof Date){
		var expDate = new Date(inputDate);
    	 var month = '' + (expDate.getMonth() + 1);
         var day = '' + expDate.getDate();
        var  year = expDate.getFullYear();
    	  if (month.length < 2) month = '0' + month;
    	    if (day.length < 2) day = '0' + day;
    	   return [day, (month),year].join('-');
		}else{
			return inputDate;
		}
	};
	
	function formatteddate(inputData){
		if(inputData instanceof Date){
     	  var expDate = new Date(inputData);
     	 var month = '' + (expDate.getMonth() + 1);
          var day = '' + expDate.getDate();
         var  year = expDate.getFullYear();
     	  if (month.length < 2) month = '0' + month;
     	    if (day.length < 2) day = '0' + day;
     	   return [month, (day),year].join('-');
		}else{
			return inputData;
		}
       };
       
       
       this.findDateRangeSelection = function(startDate,endDate){
   		var date1 = new Date(startDate);
		var date2 = new Date(endDate);
		var timeDiff = Math.abs(date2.getTime() - date1.getTime());
		var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
		var rangeDuration = Math.round(diffDays/30);
		
		if(rangeDuration == 0){
			return constants.TODAY;
		}else if(rangeDuration == 1){
			return constants.LAST_ONE_MONTH;
		}
		else if(rangeDuration == 3){
			return constants.LAST_THREE_MONTHS;
		}else if(rangeDuration == 6){
			return constants.LAST_SIX_MONTHS;
		}else if(rangeDuration == 12){
			return constants.LAST_ONE_YEAR;
		}else if(rangeDuration == 50){
			return constants.ALLTIME;
		}else{
			return constants.CUSTOM_RANGE;
		}
		
   	};
}]);
})(angular);