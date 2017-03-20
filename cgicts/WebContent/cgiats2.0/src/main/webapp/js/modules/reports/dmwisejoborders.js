;(function(angular){
	angular.module("dmwisereportModule",['jcs-autoValidate'])
	.controller("dmwisereportcontroller", function($scope,$http,$state,$timeout,$mdDialog,$stateParams, $sessionStorage){
		
		
		
		$scope.dmwisejoborder = [];
		$scope.dmjoborderTable = false;
		$scope.emptyChart = false;
		$scope.dataChart = false;
		$scope.dmClick = false;
		$scope.onload = function()
		{
			$scope.filterdetails = JSON.parse(sessionStorage.getItem("filterdata"));
			
			$scope.detailsfromdm = JSON.parse(sessionStorage.getItem("dmdetails"));
			if($scope.detailsfromdm == "" || $scope.detailsfromdm == undefined || $scope.detailsfromdm == null)
				{
				}
			else
				{
				
				dmwisejobordertableonloadfun();
     			dmwisejobordersidebaronloadfun();
     			
     			 
				}
			function dmwisejobordertableonloadfun(){
				$scope.selectedDmName = $scope.detailsfromdm.fulldmName;
				$scope.dmobj = {year: $scope.filterdetails.year, month: $scope.filterdetails.month, week: $scope.filterdetails.week, status: $scope.filterdetails.status};
				$scope.dmobj.dmName = $scope.detailsfromdm.name;
				var response = $http.post('totalReportController/getOpenOrClosedOrdersByDM',$scope.dmobj);
     			response.success(function (data,status,headers,config){
     				$scope.dmjoborderTable = true;
     				dmwisejoborderstable();
     				 $scope.dmwisejoborder.dmwisejoborderTableControl.options.data = data;
     				 $("#dmtable").slideDown();
     			});
     			response.error(function (data,status,headers,config){
     				$(".underlay").hide();
     	  			  if(status == constants.FORBIDDEN){
     	  				location.href = 'login.html';
     	  			  }else{  			  
     	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
     	  			  }
     			});
			}
			
			function dmwisejobordersidebaronloadfun(){
				$scope.selectedDmName = $scope.detailsfromdm.fulldmName;
				$scope.onloadchartObj = {year: $scope.filterdetails.year, month: $scope.filterdetails.month, week: $scope.filterdetails.week, status: $scope.filterdetails.status};
     			$scope.onloadchartObj.dmName = $scope.detailsfromdm.name;
//				alert(JSON.stringify($scope.chartObj));
				 var response = $http.post('totalReportController/getSubmittal_Service_Of_All_Job_Orders_BY_DM',$scope.onloadchartObj);
     			response.success(function (data,status,headers,config){
//     				alert(JSON.stringify(data));
     				
     				if(data && data.series){
//     					alert("data");
     					$scope.dataChart = true;
     					$scope.emptyChart = false;
     					$scope.totalsidebarData = data;
     				sideBarCallFun();
     			}else{
//     				alert("no data");
     				$scope.emptyChart = true;
     				$scope.dataChart = false;
     				sideBarNoDataCall();
     			}
     			});
     			response.error(function (data,status,headers,config){
     				$(".underlay").hide();
     	  			  if(status == constants.FORBIDDEN){
     	  				location.href = 'login.html';
     	  			  }else{  			  
     	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
     	  			  }
     			});
			}
			
			
			
			$scope.joborderstatus = $scope.filterdetails.status;
			var firstlet = $scope.filterdetails.status.substring(0,1)
			var alltext = $scope.filterdetails.status.slice(1).toLowerCase();
			$scope.statustoshow = firstlet+alltext;
			$scope.totalCountfrompg = $scope.filterdetails.Count;
			$scope.getdataforreport();
			$scope.getClientPieChart();
			
		}
		
		
		
/*-***********************************DATA for REPORT FUNCTION STARTS HERE*******************************************-*/
		$scope.getGridData = function(){
			$(".underlay").show();
			$scope.obj = {year: $scope.filterdetails.year, month: $scope.filterdetails.month, week: $scope.filterdetails.week, status: $scope.filterdetails.status};
			var response = $http.post('totalReportController/getAllDMsOpenAndClosedJobOrders',$scope.obj);
			response.success(function (data,status,headers,config){
//				alert("test data here"+JSON.stringify(data));
				if(data){
					
					$scope.totalData = data;
					
					 var chart;
					   point = null;
					   $(document).ready(function () {
						   Highcharts.setOptions({
							     colors: ['#6bbc6b', '#ffcfa0', '#ffa556', '#c6d8ef', '#62a0ca', '#b7e8ad', '#e26868']
							    });
					       var data = $scope.totalData;
					       
					       chart = new Highcharts.Chart(
					       {
					    	   title:{
					    		   text: '<b class="charttitle">DM Wise '+$scope.statustoshow+' Job Orders Report</b>'
					    	   },
					    	   plotOptions: {
					               pie: {
					                   allowPointSelect: true,
					                   cursor: 'pointer',
					                   dataLabels: {
					                	   enabled: true,
					                    	 format: '{point.name}',
					                   }/*,
					                   showInLegend: true*/
					               }
					           },
					           tooltip: {
					        	   pointFormat: '<b class="asdf">Count</b>: {point.y}<br><b class="asdf">Avg Days'+($scope.filterdetails.status == "OPEN" ? ' Active ': ' to Close')+'</b>: {point.avgDays}'
					           },
					          series:[
					             {
					                "data": data,
					                 type: 'pie',
					                 animation: true,
					                 point:{
					                     events:{
					                         click: function (event) {
					                        	 $scope.selectedDmName = this.name;
					                        	 $scope.dmwisejoborder = [];
					                     		$scope.dmjoborderTable = false;
					                             getJobOrderDetailsInTableFun(this.userId);
					                             getJobOderDetailsInBarChartFun(this.userId);
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
			
			
			function getJobOderDetailsInBarChartFun(userId){
				$scope.obj = {year: $scope.filterdetails.year, month: $scope.filterdetails.month, week: $scope.filterdetails.week, status: $scope.filterdetails.status};
				$scope.obj.dmName = userId;
//				alert(JSON.stringify($scope.chartObj));
				 var response = $http.post('totalReportController/getSubmittal_Service_Of_All_Job_Orders_BY_DM',$scope.obj);
     			response.success(function (data,status,headers,config){
     				
     				if(data && data.series){
//     					alert("data");
     					$scope.dataChart = true;
     					$scope.emptyChart = false;
     					$scope.totalsidebarData = data;
     				sideBarCallFun();
     			}else{
//     				alert("no data");
     				$scope.emptyChart = true;
     				$scope.dataChart = false;
     				sideBarNoDataCall();
     			}
     			});
     			response.error(function (data,status,headers,config){
     				$(".underlay").hide();
     	  			  if(status == constants.FORBIDDEN){
     	  				location.href = 'login.html';
     	  			  }else{  			  
     	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
     	  			  }
     			});
			}
			
			
			
			function getJobOrderDetailsInTableFun(userId){
//				alert(JSON.stringify($scope.obj));
				$scope.obj.dmName = userId;
//			        	alert(JSON.stringify($scope.obj));
				 var response = $http.post('totalReportController/getOpenOrClosedOrdersByDM',$scope.obj);
      			response.success(function (data,status,headers,config){
      				$scope.dmjoborderTable = true;
      				dmwisejoborderstable();
      				 $scope.dmwisejoborder.dmwisejoborderTableControl.options.data = data;
      				 $("#dmtable").slideDown();
      			});
      			response.error(function (data,status,headers,config){
      				$(".underlay").hide();
      	  			  if(status == constants.FORBIDDEN){
      	  				location.href = 'login.html';
      	  			  }else{  			  
      	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
      	  			  }
      			});
			}
		}
		
		$scope.getClientPieChart = function(){
			$scope.obj = {year: $scope.filterdetails.year, month: $scope.filterdetails.month, week: $scope.filterdetails.week, status: $scope.filterdetails.status};
			var response = $http.post('totalReportController/getAllClients',$scope.obj);
  			response.success(function (data,status,headers,config){
//  				 alert(JSON.stringify(data));
  				 $scope.clientData = data;
  				 clientPieChartCall();
  			});
  			response.error(function (data,status,headers,config){
  				$(".underlay").hide();
  	  			  if(status == constants.FORBIDDEN){
  	  				location.href = 'login.html';
  	  			  }else{  			  
  	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  	  			  }
  			});
			
			
		}
		
		function clientPieChartCall(){
			//alert(JSON.stringify($scope.clientData));
			$scope.clientCharts = Highcharts.chart('containerClientPie',{
				chart: {
		            plotBackgroundColor: null,
		            plotBorderWidth: null,
		            plotShadow: false,
		            type: 'pie'
		        },
		        title: {
		            text: 'Browser market shares January, 2015 to May, 2015'
		        },
		        tooltip: {
		            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		        },
		        plotOptions: {
		            pie: {
		                allowPointSelect: true,
		                cursor: 'pointer',
		                dataLabels: {
		                    enabled: true,
		                    format: '{point.name}',
		                    style: {
		                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
		                    },
			                   showInLegend: false
		        
		                }
		            }
		        },
		        series: [{
		            name: 'Brands',
		            colorByPoint: true,
		            data: $scope.clientData
		        }]
			});
		}
		
		function  sideBarCallFun(){
			$scope.myCharts = Highcharts.chart('containerside',{
				
				 chart:{
					 type : 'bar'
				 },
				title:{
					text : '<b>'+ $scope.selectedDmName +' Job Order Submittals</b>'
				},
				xAxis:{
					min: 0,
					categories: $scope.totalsidebarData.dms
				},
				yAxis: {
		            min: 0,
		            title: {
		                text: 'No of Submittals'
		            }
		        },
		        legend: {
		            reversed: true
		        },
				 
				 plotOptions: {
			            series: {
			            	 stacking: 'normal',
			                cursor: 'pointer',
			                point: {
			                    events: {
			                        click: function (e) {
//			                            alert('Category: ' + this.category + ', value: ' + this.y+ ', name'+this.series.name); 
			                        }
			                    }
			                }
			            }
			        },
			        series:  $scope.totalsidebarData.series
			 });
		}
		
/*-***********************************DATA for REPORT FUNCTION ENDS HERE*******************************************-*/
		


		
		
		
/*-***********************************TABLE FUNCTION STARTS HERE*******************************************-*/		
	function dmwisejoborderstable(){
            $scope.dmwisejoborder.dmwisejoborderTableControl = {
                    options: { 
                        striped: true,
                        pagination: true,
                        paginationVAlign: "bottom", 
                        pageList: [50,100,200],
                        search: false,
                        //sidePagination : 'server',
                        silentSort: false,
                        pageSize: 5,
                        showColumns: false,
                        showRefresh: false,
                        clickToSelect: false,
                        showToggle: false,
                        maintainSelected: true, 
                        showFooter : false,
                        columns: [
                         {
                            field: 'jobOrderId',
                            title: 'JobOrder Id',
                            events : window.jobOrderIdEvents,
							formatter : jobOrderIdFormatter,
                            align: 'left',
                            
                        },{
                            field: 'title',
                            title: 'Title',
                            align: 'left',
                           
                        }, {
                            field: 'client',
                            title: 'Client',
                            align: 'left',
                           
                        },{
                            field: 'sbm',
                            title: 'Sbm',
                            align: 'left',
                           
                        },{
                            field: 'noOfPositions',
                            title: '# Pos',
                            align: 'left',
                           
                        },{
                            field: 'openJobOrders',
                            title: '# Open',
                            align: 'left',
                           
                        },{
                            field: 'activeDays',
                            title: 'Active Days',
                            align: 'left',
                           
                        },]
                    }
            };
            
    	}
	
	
/*-***********************************TABLE FUNCTION ENDS HERE*******************************************-*/
	
	
	
	
/*-***********************************OTHER FUNCTION STARTS HERE*******************************************-*/
	
	function jobOrderIdFormatter(value, row, index) {  
		if(row.sbm>0){ 
        return [ 
        '<a class="jobId actionIcons" title="Submittals : '+row.sbm+'" flex-gt-md="auto" >'+row.jobOrderId+'</a>', 
        ].join(''); 
		}else{
			return ['<div>'+row.jobOrderId+'</div>'].join('');
		}
     } 
     
     window.jobOrderIdEvents =  {
             'click .jobId': function (e, value, row, index) {
            	 if(row.sbm>0){ 
            		 var dmdataobj = {name  : $scope.obj.dmName? $scope.obj.dmName : $scope.dmobj.dmName , jobOrderId: row.jobOrderId, fulldmName: $scope.selectedDmName};
                     $scope.senddmdata = JSON.stringify(dmdataobj);
         			sessionStorage.setItem("dmdetails", $scope.senddmdata);
          	   $state.transitionTo("recruiterLevelModule");
            	 }
              },
     };
		
     $scope.getdataforreport = function()
		{
			$scope.getGridData();
		}
         
/*-***********************************OTHER FUNCTION ENDS HERE*******************************************-*/     
     
     
     
     
	
	});
	
	

	
})(angular);