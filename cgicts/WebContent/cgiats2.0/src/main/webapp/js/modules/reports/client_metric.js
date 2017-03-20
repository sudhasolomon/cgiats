;(function(angular){
	angular.module("clientmetricModule",['jcs-autoValidate', 'angularjs-dropdown-multiselect'])
	.controller("clientmetriccontroller", function($scope,$http,$state,$timeout,$mdDialog, $sessionStorage){
		
		
		$scope.year = [];
		$scope.month = [];
		$scope.client = [];
		$scope.onload = function()
		{
			/*$(".underlay").show();*/
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
			
			$scope.getAllSubmittalYears();
			$scope.getselectedClientsfromDB();
			
			
			$timeout(function() {
				$scope.getdataforreport();
				 }, 300);
			
		}
		
		
		
		
		
		
		
/*-****************************GET CLIENTS FUNCTION STARTS HERE*****************************************************-*/		
		$scope.getselectedClientsfromDB = function()
		{
			
			
			if($scope.year.length == 0)
			{
				$scope.yearforReport = "";
			}
		else
			{
			$scope.yearforReport = "";
			for(var i=0; i < $scope.year.length; i++)
			{
			$scope.yearforReport += $scope.year[i].id + ", ";
			}
			
			}
			
			
			
			
			if($scope.month.length == 0)
			{
			$scope.monthforReport = "";
			}
		else
			{
			$scope.monthforReport = "";
			for(var i=0; i < $scope.month.length; i++)
			{
			$scope.monthforReport += $scope.month[i].id + ", ";
			}
			
			}
	
			
			$scope.statustosend = $scope.status.id;
			$scope.obj ={year:$scope.yearforReport,status:$scope.statustosend,month:$scope.monthforReport};
			
			var response = $http.post('totalReportController/getAllClients',$scope.obj);
			response.success(function (data,status,headers,config){
				if(data && data.data.length > 0){
				$scope.clientsList = data.data;
				$scope.clientList = [];
				$scope.selectedclientList = [];
				for(var i=0; i<data.data.length; i++ )
					{
						var obj = {id: data.data[i].FULL_NAME, label: data.data[i].FULL_NAME};
						$scope.clientList.push(obj);
					}
				if(data.data.length < 10)
					{
						for(var i=0; i<data.data.length; i++ )
						{
							var selectedobj = {id: data.data[i].FULL_NAME, label: data.data[i].FULL_NAME};
							$scope.selectedclientList.push(selectedobj);
						}
					}
				else
					{
					for(var i=0; i<10; i++ )
					{
						var selectedobj = {id: data.data[i].FULL_NAME, label: data.data[i].FULL_NAME};
						$scope.selectedclientList.push(selectedobj);
					}
					}
				
				$scope.client = $scope.selectedclientList;
				$scope.clientdata = $scope.clientList;
				
				
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
/*-****************************GET CLIENTS FUNCTION ENDS HERE*****************************************************-*/
		
		$scope.checkandgetdataforreport = function()
		{
			if($scope.year.length == 0)
				{
				$("#mustyear").show();
				$("#muststatus").hide();
				}
			else
				{
				$("#mustyear").hide();
				if($scope.status.id == "" || $scope.status.id == undefined || $scope.status.id == null)
				{
				$("#muststatus").show();
				}
			else
				{
				$("#muststatus").hide();
				$scope.getGridData();
				}
				}
		}
		
		$scope.getdataforreport = function()
		{
			$scope.getGridData();
		}
		
		
/*-**********************************GET CHART FUNCTION STARTS HERE********************************************************-*/
		$scope.getGridData = function(){
			
			$scope.yearforReport = "";
			$scope.monthforReport = "";
			$scope.clientforReport = "";
			
			for(var i=0; i < $scope.year.length; i++)
			{
			$scope.yearforReport += $scope.year[i].id + ", ";
			}
			for(var i=0; i < $scope.month.length; i++)
			{
			$scope.monthforReport += $scope.month[i].id + ", ";
			}
			for(var i=0; i < $scope.client.length; i++)
			{
				$scope.clientforReport += $scope.client[i].id + ", ";
			}
			
			
			var obj = {year:$scope.yearforReport,status:$scope.statustosend,month:$scope.monthforReport, name:$scope.clientforReport };
			
			
			
			
//			alert(JSON.stringify(obj));
			
			var response = $http.post('totalReportController/getAllClients',obj);
			response.success(function (data,status,headers,config){
//				alert(JSON.stringify(data));
				if(data){
					$scope.totalData = data;
					 var chart;
					   point = null;
					   $(document).ready(function () {

						   Highcharts.setOptions({
							     //colors: ['#ffc59e', '#9ed0a8']
							    });
					       var data = $scope.totalData.data;
					       
					       chart = new Highcharts.Chart(
					       {

					    	   					           
					    	   title:{
					    		   text:'<b>Total Open/Closed Job Orders Report</b>'
					    	   },
					    	   tooltip: {
					        	   pointFormat: '<b>Client: </b>{point.FULL_NAME}<br><b>No Of Job Orders: </b>{point.y}'
					           },
					    	   plotOptions: {
					               pie: {
					                   allowPointSelect: true,
					                   cursor: 'pointer',
					                   dataLabels: {
					                	   enabled: true, 
					                       format: '<b class="asdf">{point.name}</b>',
					                   },
					                  // showInLegend: true
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
					                        	 var dataobj = {name  : this.FULL_NAME, year: $scope.yearforReport,month: $scope.monthforReport, status: $scope.statustosend, Count: this.y};
					                             $scope.senddata = JSON.stringify(dataobj);
					                 			sessionStorage.setItem("filterdata", $scope.senddata);
					                             $state.transitionTo("titlewisemetricModule");
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
			

			var response = $http.get('reportController/getAllSubmittalYears');
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
  		  });
		};
/*-********************************** GET YEARS DATA FUNCTION ENDS HERE******************************************************-*/		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		


		
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
			
			
			
			
			
			
			
			
			
			
			
			
		

		
			
/*-************************************************YEAR AND MONTH CHANGE FUNCTION STARTS HERE*****************************************************-*/			
		$scope.yearevent = {
				onItemSelect: function(item) {
					$scope.getselectedClientsfromDB();
				},
		onItemDeselect: function(item) {
			$scope.getselectedClientsfromDB();
		},
		onDeselectAll: function(item) {
			$timeout(function() {
				$scope.getselectedClientsfromDB();
				 }, 10);
		}
                   
        };

		$scope.monthevent = {
				onItemSelect: function(item) {
					$scope.getselectedClientsfromDB();
				},
		onItemDeselect: function(item) {
			$scope.getselectedClientsfromDB();
		},
		onDeselectAll: function(item) {
			$timeout(function() {
				$scope.getselectedClientsfromDB();
				 }, 10);
		}
		};
		
	/*	$scope.checkandgetweeks = function()
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
		}*/
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
		$scope.status = {id: "OPEN", label: "Open"};
		$scope.statusdata = [
		                     	{id: "OPEN", label: "Open"},
		                     	{id: "CLOSED", label: "Closed"}
		                     ];
			
				$scope.manysettings = {
			            smartButtonMaxItems: 3,
			            smartButtonTextConverter: function(itemText, originalItem) {
			                return itemText;
			            },
			        };
				$scope.onesettings = {
					    selectionLimit: 1,
					    smartButtonMaxItems: 1,
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