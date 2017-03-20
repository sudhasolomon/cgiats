;(function(angular){
	angular.module("totalreportModule",['jcs-autoValidate', 'angularjs-dropdown-multiselect'])
	.controller("totalreportcontroller", function($scope,$http,$state,$timeout,$mdDialog, $sessionStorage){
		
		
		$scope.year = [];
		$scope.month = [];
		$scope.week = [];
		$scope.onload = function()
		{
			$(".underlay").show();
			$(".caret").addClass("fa fa-caret-down");
			$(".caret").css("font-size", "16px");
			$(".caret").css("color", "#1895ab");
			sessionStorage.clear();
			var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
			var monthFullName = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
			$scope.selectedYear = new Date().getFullYear();
			$scope.selectedMonth = monthName[new Date().getMonth()];
			$scope.selectedMonthFull = monthFullName[new Date().getMonth()];
			$scope.year = [{id: $scope.selectedYear, label: $scope.selectedYear}];
			$scope.month = [{id: $scope.selectedMonth, label: $scope.selectedMonthFull}];
			$scope.getselectedWeeksfromDB();
			$scope.getAllSubmittalYears();
			$scope.getdataforreport();
		}
		
		
		
		
		
		
		
/*-****************************GET WEEKS FUNCTION STARTS HERE*****************************************************-*/		
		$scope.getselectedWeeksfromDB = function()
		{
			$scope.yeartosend = $scope.year[0].id;
			$scope.monthtosend = $scope.month[0].id;
			
			var response = $http.get('reportController/getWeeksOfMonth?year='+$scope.yeartosend+'&month='+$scope.monthtosend);
			response.success(function (data,status,headers,config){
				$scope.weeksList = data;
				$scope.weekList = [];
				for(var i=0; i<data.length; i++ )
					{
						var obj = {id: data[i].value, label: data[i].label};
						$scope.weekList.push(obj);
					}
				$scope.weekdata = $scope.weekList;
				
				
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
}
/*-****************************GET WEEKS FUNCTION ENDS HERE*****************************************************-*/
		
		$scope.checkandgetdataforreport = function()
		{
			if($scope.year.length == 0)
				{
				$("#mustyear").show();
				}
			else
				{
				$scope.getdataforreport();
				}
		}
		
		$scope.getdataforreport = function()
		{
			$("#mustyear").hide();
			$scope.getGridData();
		}
		
		
/*-**********************************GET CHART FUNCTION STARTS HERE********************************************************-*/
		$scope.getGridData = function(){
			$scope.yearforReport = "";
			$scope.monthforReport = "";
			$scope.weekforReport = "";
			for(var i=0; i < $scope.year.length; i++)
			{
			$scope.yearforReport += $scope.year[i].id + ", ";
			}
			for(var i=0; i < $scope.month.length; i++)
			{
			$scope.monthforReport += $scope.month[i].id + ", ";
			}
			for(var i=0; i < $scope.week.length; i++)
			{
			$scope.weekforReport += $scope.week[i].id + ", ";
			}
			
			
			
			
			var obj = {year: $scope.yearforReport, month: $scope.monthforReport, week: $scope.weekforReport};
			var response = $http.post('totalReportController/getTotalOpenAndClosedReport',obj);
			response.success(function (data,status,headers,config){
				if(data){
//					alert(JSON.stringify(data));
					$scope.totalData = data;
					 var chart;
					   point = null;
					   $(document).ready(function () {

						   Highcharts.setOptions({
							     colors: ['#ffc59e', '#9ed0a8']
							    });
					       var data = $scope.totalData.data;
					       
					       chart = new Highcharts.Chart(
					       {

					    	   					           
					    	   title:{
					    		   text:'<b>Total Open/Closed Job Orders Report</b>'
					    	   },
					    	   tooltip: {
					        	   pointFormat: 'Count : {point.y}'
					           },
					    	   plotOptions: {
					               pie: {
					                   allowPointSelect: true,
					                   cursor: 'pointer',
					                   dataLabels: {
					                       distance : -30, 
					                       format: '<b class="asdf">{point.name}</b>: {point.y}',
					                   },
					                   showInLegend: true
					               }
					           },
					          series:[
					             {
					                "data": data,
					                 type: 'pie',
					                 animation: true,
					                 point:{
					                     events:{
					                         click: function (event) {
					                        	 var dataobj = {status  : this.name, year: $scope.yearforReport,month: $scope.monthforReport, week: $scope.weekforReport, Count: this.y};
					                             $scope.senddata = JSON.stringify(dataobj);
					                 			sessionStorage.setItem("filterdata", $scope.senddata);
					                             $state.transitionTo("dmwisereportModule");
					                         },
					                         legendItemClick: function () {
					                             return false; // <== returning false will cancel the default action
					                         }
					                     }
					                 }          
					             }
					          ],
					          "chart":{
					             "renderTo":"container"
					          },
					       });
					   });
					
					
					
					
					
					
				}
				$(".underlay").hide();
			});
			response.error(function(data, status, headers, config){
				$(".underlay").hide();
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
		}
/*-**********************************GET CHART FUNCTION ENDS HERE********************************************************-*/		
		
		
/*-********************************** GET YEARS DATA FUNCTION STARTS HERE******************************************************-*/
$scope.getAllSubmittalYears = function(){
			

	/*		var response = $http.get('reportController/getAllSubmittalYears');
			response.success(function (data,status,headers,config){
				$scope.yearList = [];
				for(var i=0; i<data.length; i++ )
					{
						var obj = {id: data[i], label: data[i]};
						$scope.yearList.push(obj);
					}
				
		        $scope.yeardata = $scope.yearList;
				
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });*/
	$scope.yeardata = [];
	for(var i=2012;i<=(new Date().getFullYear());i++){
		var obj = {id: i, label: i};
		$scope.yeardata.push(obj);
//		$scope.yeardata.push(i+'');
	}
		};
/*-********************************** GET YEARS DATA FUNCTION ENDS HERE******************************************************-*/		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		


		
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
			
			
			
			
			
			
			
			
			
			
			
			
		

		
			
/*-************************************************YEAR AND MONTH CHANGE FUNCTION STARTS HERE*****************************************************-*/			
		$scope.yearevent = {
				onItemSelect: function(item) {
					$scope.checkandgetweeks();
				},
		onItemDeselect: function(item) {
			$scope.checkandgetweeks();
		},
		onDeselectAll: function(item) {
			$timeout(function() {
				$scope.checkandgetweeks();
				 }, 100);
		}
                   
        };

		$scope.monthevent = {
				onItemSelect: function(item) {
					$scope.checkandgetweeks();
				},
		
				onItemDeselect: function(item) {
					$scope.checkandgetweeks();
				},
				
				onDeselectAll: function(item) {
					$timeout(function() {
						$scope.checkandgetweeks();
						 }, 100);
					
				}
				
        };
		
		$scope.checkandgetweeks = function()
		{
			if($scope.year.length <1 || $scope.year.length >1)
			{
        		$("div[selected-model='week'] .dropdown-toggle").attr("disabled", "disabled");
        		$("div[selected-model='week'] .dropdown-toggle").css("background-color", "#cccccc");
        		$("div[selected-model='week'] .dropdown-toggle .caret").css("background-color", "#cccccc");
        		$scope.week = [];
			}
		else
			{
			if($scope.month.length <1 || $scope.month.length >1)
			{
        		$("div[selected-model='week'] .dropdown-toggle").attr("disabled", "disabled");
        		$("div[selected-model='week'] .dropdown-toggle").css("background-color", "#cccccc");
        		$("div[selected-model='week'] .dropdown-toggle .caret").css("background-color", "#cccccc");
        		$scope.week = [];
			}
		else
			{
    		$("div[selected-model='week'] .dropdown-toggle").removeAttr("disabled");
    		$("div[selected-model='week'] .dropdown-toggle").css("background-color", "#ffffff");
    		$("div[selected-model='week'] .dropdown-toggle .caret").css("background-color", "#ffffff");
    		$scope.getselectedWeeksfromDB();
			}
			}
		}
/*-************************************************YEAR AND MONTH CHANGE FUNCTION ENDS HERE*****************************************************-*/		
		
			
			
			
			

		
		
		

		
		
		
            

            

        
		
		$scope.monthdata = [
	       					{id: "Jan", label: "January"},
	       					{id: "Feb", label: "February"},
	       					{id: "Mar", label: "March"},
	       					{id: "Apr", label: "April"},
	       					{id: "May", label: "May"},
	       					{id: "Jun", label: "June"},
	       					{id: "Jul", label: "July"},
	       					{id: "Aug", label: "August"},
	       					{id: "Sep", label: "September"},
	       					{id: "Oct", label: "October"},
	       					{id: "Nov", label: "November"},
	       					{id: "Dec", label: "December"}];
			
				$scope.manysettings = {
			            smartButtonMaxItems: 3,
			            smartButtonTextConverter: function(itemText, originalItem) {
			                return itemText;
			            },
			        };
			
			
			
			
			
	
				
				$scope.showfilters = function()
				{
					var filterdiv = $("#filtersforsearch");
					if(filterdiv.is(":visible"))
						{
						$("#filtersforsearch").slideUp();
						}
					else
						{
						$("#filtersforsearch").slideDown();
						}
				}
			
			
			
			

		
		
	});
	
	

	
})(angular);