;(function(angular){
	angular.module("recruitermetreportModule",['jcs-autoValidate', 'angularjs-dropdown-multiselect'])
	.controller("recruitermetreportcontroller", function($scope,$http,$state,$timeout,$mdDialog,$rootScope){

		$scope.year = [];
		$scope.month = [];
		$scope.dm = [];
		$scope.recruiters=[];
		$scope.recruitersTable=false;
		
		$scope.onload = function()
		{
			$scope.tabfunction();
			if(($rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
				$scope.sitename = "Off Shore";
			}else{
			$scope.sitename = "On Site";
			}
			$(".underlay").show();
			$(".caret").addClass("fa fa-caret-down");
			$(".caret").css("font-size", "16px");
			$(".caret").css("color", "#1895ab");
			
			var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
			var monthFullName = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
			
			$scope.selectedYear = new Date().getFullYear();
			$scope.selectedMonth = monthName[new Date().getMonth()];
			$scope.selectedMonthFull = monthFullName[new Date().getMonth()];
			
			$scope.year = [{id: $scope.selectedYear, label: $scope.selectedYear}];
			$scope.month = [{id: $scope.selectedMonth, label: $scope.selectedMonthFull}];
			
			$scope.getAllSubmittalYears();
			
		}
		
		
		
/*-***************************************GET YEARS FUNCTION STARTS HERE****************************-*/
$scope.getAllSubmittalYears = function(isFromOnload,isYearChanged){
			

		/*	var response = $http.get('reportController/getAllSubmittalYears');
			response.success(function (data,status,headers,config){
				$scope.yearList = [];
				for(var i=0; i<data.length; i++ )
					{
						var obj = {id: data[i], label: data[i]};
						$scope.yearList.push(obj);
					}
				
		        $scope.yeardata = $scope.yearList;
		        $scope.getAllUsers();
		      
			
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
	 $scope.getAllUsers();
	
};
/*-***************************************GET YEARS FUNCTION ENDS HERE****************************-*/
		
/*-*************************** GET USERS FUNCTION STARTS HERE********************************************-*/
$scope.getAllUsers = function(isFromOnload,isYearChanged){
	var response = $http.get('commonController/getAllRecruitersAndADMs?isAuthRequired='+true+'&isIndia='+false);
	response.success(function (data,status,headers,config){
		$scope.userRecords = data;
		if(data && data.length>0){
		$scope.uesrList = [];
		for(var i=0; i<data.length; i++ )
			{
				var obj = {id: data[i].userId, label: data[i].fullName};
				$scope.uesrList.push(obj);
			}
		
		$scope.dm = {id: data[0].userId, label: data[0].fullName};
		$scope.dmdata = $scope.uesrList;
		$scope.getdataforreport();
		}else{
			$(".underlay").hide();
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
/*-*************************** GET USERS FUNCTION ENDS HERE********************************************-*/
		



/*-*************************** GET LEVEL AND NAME FUNCTION STARTS HERE********************************************-*/

$scope.getdataforreport = function()
{
	$scope.recruiters=[];
	if($scope.year == "" || $scope.year == undefined || $scope.year == null)
	{
		$("#mustyear").show();
	}
else
	{
	$("#mustyear").hide();
	var dmnametocheck = $scope.dm.id;
	if(dmnametocheck == "" || dmnametocheck == undefined || dmnametocheck == null)
	{
		$("#mustdm").show();
	}
else
	{
	$("#mustdm").hide();
	$scope.recruitersTable=false;
	$scope.getGridData();
	}
	
	}
}
/*-*************************** GET LEVEL AND NAME FUNCTION ENDS HERE********************************************-*/



/*$scope.hideandgetdetails = function()
{
	$("#filterfields").slideUp();
	$scope.getdataforreport();
	
}*/


		
/*-*************************** GET DATA FOR REPORT STARTS HERE********************************************-*/

		$scope.getGridData = function(){
			
			$scope.yearforReport = "";
			$scope.monthforReport = "";
			
			for(var i=0; i < $scope.year.length; i++)
			{
			$scope.yearforReport += $scope.year[i].id + ", ";
			}
			for(var i=0; i < $scope.month.length; i++)
			{
			$scope.monthforReport += $scope.month[i].id + ", ";
			}
			
			
			
			
			for(var i=0; i<$scope.uesrList.length; i++)
			{
				if($scope.dm.id == $scope.uesrList[i].id)
					{
					$scope.showFullName = $scope.uesrList[i].label;
					}
				else
					{
					
					}
			}
			
			
			
			$scope.hitRatioData = {year: $scope.yearforReport, month: $scope.monthforReport =='' ? null : $scope.monthforReport, dmName: $scope.dm.id};
			var obj = {year: $scope.yearforReport, month: $scope.monthforReport =='' ? null : $scope.monthforReport, dmName: $scope.dm.id, status:'STARTED'};
			//alert(JSON.stringify(obj));
			$scope.datafordays = obj;
			
			if($scope.dm.label == "" || $scope.dm.label == undefined || $scope.dm.label == null)
			{
			$scope.showname = $scope.dm.id;
			}
		else
			{
			$scope.showname = $scope.dm.label;
			}
			
			var response = $http.post('totalReportController/getPeriodWiseTotalNumberOfStatus',obj);
			response.success(function (dataObj,status,headers,config){
				//alert(JSON.stringify(dataObj));
				if(dataObj && dataObj.dmName){
					$scope.reportdata = dataObj;
					$scope.getRecruitersTable();
					$scope.getNoOfStats();
					$scope.dmRecruitersTable = true;
				}else{
					$(document).ready(function () { 
						var fusioncharts = new FusionCharts({
						    type: 'angulargauge',
						    renderAt: 'chart-container',
						    width: '350',
						    height: '250',
						    dataFormat: 'json',
						    dataSource: {
						        "chart": {
						            "caption": "Total No Of Starts By "+$scope.showFullName,
						            "lowerLimit": "0",
						            "upperLimit": "100",
						            "lowerLimitDisplay": "Poor",
						            "upperLimitDisplay": "Good",
						            "showValue": "1",
						            "valueBelowPivot": "1",
						            "theme": "fint"
						        },
						        "colorRange": {
						            "color": [{
						                "minValue": "0",
						                "maxValue": "25",
						                "code": "#bb3a0f"
						            }, {
						                "minValue": "25",
						                "maxValue": "50",
						                "code": "#d2c459"
						            }, {
						                "minValue": "50",
						                "maxValue": "75",
						                "code": "#92d3bb"
						            },
						            {
						                "minValue": "75",
						                "maxValue": "100",
						                "code": "#3a9271"
						            }]
						        },
						        "dials": {
						            "dial": [{
						                "value": "0"
						            }]
						        }
						    }
						}
						);
						    fusioncharts.render();
						    
					   });
					
					$(document).ready(function () { 
						var fusioncharts = new FusionCharts({
						    type: 'angulargauge',
						    renderAt: 'chart-container1',
						    width: '350',
						    height: '250',
						    dataFormat: 'json',
						    dataSource: {
						        "chart": {
						            "caption": "Average No of Days to Starts",
						            "lowerLimit": "0",
						            "upperLimit": "100",
						            "lowerLimitDisplay": "Good",
						            "upperLimitDisplay": "Poor",
						            "showValue": "1",
						            "valueBelowPivot": "1",
						            "theme": "fint"
						        },
						        "colorRange": {
						            "color": [{
						                "minValue": "0",
						                "maxValue": "25",
						                "code": "#bb3a0f"
						            }, {
						                "minValue": "25",
						                "maxValue": "50",
						                "code": "#d2c459"
						            }, {
						                "minValue": "50",
						                "maxValue": "75",
						                "code": "#92d3bb"
						            },
						            {
						                "minValue": "75",
						                "maxValue": "100",
						                "code": "#3a9271"
						            }]
						        },
						        "dials": {
						            "dial": [{
						                "value": "0"
						            }]
						        }
						    }
						}
						);
						    fusioncharts.render();
					   });
					
					$(document).ready(function () { 
						var fusioncharts = new FusionCharts({
						    type: 'angulargauge',
						    renderAt: 'chart-container2',
						    width: '350',
						    height: '250',
						    dataFormat: 'json',
						    dataSource: {
						        "chart": {
						            "caption": "Starts to Submittals Rate (%)",
						            "lowerLimit": "0",
						            "upperLimit": "100",
						            "lowerLimitDisplay": "Poor",
						            "upperLimitDisplay": "Good",
						            "showValue": "1",
						            "valueBelowPivot": "1",
						            "theme": "fint"
						        },
						        "colorRange": {
						            "color": [{
						                "minValue": "0",
						                "maxValue": "25",
						                "code": "#bb3a0f"
						            }, {
						                "minValue": "25",
						                "maxValue": "50",
						                "code": "#d2c459"
						            }, {
						                "minValue": "50",
						                "maxValue": "75",
						                "code": "#92d3bb"
						            },
						            {
						                "minValue": "75",
						                "maxValue": "100",
						                "code": "#3a9271"
						            }]
						        },
						        "dials": {
						            "dial": [{
						                "value": "0"
						            }]
						        }
						    }
						}
						);
						    fusioncharts.render();
					   });
					$scope.reportdata = [];
					$scope.getRecruitersTable();
				}
				//$(".underlay").hide();
			});
			response.error(function(data, status, headers, config){
				//$(".underlay").hide();
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
			
			
			$(".underlay").hide();
			
		}
	
/*-*************************** GET DATA FOR REPORT ENDS HERE********************************************-*/
		
		
$scope.getNoOfStats = function()
{
	$scope.totalData = $scope.reportdata;
	//alert(JSON.stringify($scope.totalData));
	$(document).ready(function () { 
	    var fusioncharts = new FusionCharts({
	        type: 'angulargauge',
	        renderAt: 'chart-container',
	        width: '350',
	        height: '250',
	        dataFormat: 'json',
	        dataSource: {
	            chart: {
	                "caption": "Total No Of Starts By "+$scope.showFullName,
	                "lowerLimit": "0",
	                "upperLimit": $scope.totalData.maxValue,
	                "lowerLimitDisplay": "0",
	                "upperLimitDisplay": $scope.totalData.maxValue,
	                "showValue": "1",
	                "valueBelowPivot": "1",
	                "theme": "fint"
	            },
	            colorRange: {
	            	 color: $scope.totalData.range
	            },
	            dials: {
	                dial: [{
	                    value: $scope.totalData.level
	                }]
	            }
	        }
	    }
	    );
	        fusioncharts.render();
	   });

	
	
	$scope.getAvgNoOfDays();
	
}
		

$scope.getAvgNoOfDays = function()
{
//	alert(JSON.stringify($scope.datafordays));
	var response = $http.post('totalReportController/getDMAverageNoOfDaysForStatus',$scope.datafordays);
	response.success(function (dataObj,status,headers,config){
		$scope.avgTotalData = dataObj;
		if(dataObj){
			$(document).ready(function () { 
				var fusioncharts = new FusionCharts({
				    type: 'angulargauge',
				    renderAt: 'chart-container1',
				    width: '350',
			        height: '250',
			        dataFormat: 'json',
			        dataSource: {
			            chart: {
			                "caption": "Average No of Days to Starts",
			                "lowerLimit": "0",
			                "upperLimit": $scope.avgTotalData.maxValue,
			                "lowerLimitDisplay": "0",
			                "upperLimitDisplay": $scope.avgTotalData.maxValue,
			                "showValue": "1",
			                "valueBelowPivot": "1",
			                "theme": "fint"
			            },
			            colorRange: {
			                color: $scope.avgTotalData.range
			            },
			            dials: {
			                dial: [{
			                    value: $scope.avgTotalData.level
			                }]
			            }
			        }
			    }
			    );
			        fusioncharts.render();
				
				 });
			
		}
		//$(".underlay").hide();
	});
	response.error(function(data, status, headers, config){
		//$(".underlay").hide();
		  if(status == constants.FORBIDDEN){
			location.href = 'login.html';
		  }else{  			  
			$state.transitionTo("ErrorPage",{statusvalue  : status});
		  }
	  });
	$scope.getHitRatio();
}

$scope.getHitRatio = function(){
	var response = $http.post('totalReportController/findHitRatio',$scope.hitRatioData);
	response.success(function (dataObj,status,headers,config){
		$scope.DmHitRatioData = dataObj;
		if(dataObj){
			$(document).ready(function () { 
				var fusioncharts = new FusionCharts({
				    type: 'angulargauge',
				    renderAt: 'chart-container2',
				    width: '350',
			        height: '250',
			        dataFormat: 'json',
			        dataSource: {
			            chart: {
			                "caption": "Starts to Submittals Rate (%)",
			                "lowerLimit": "0",
			                "upperLimit": $scope.DmHitRatioData.maxValue,
			                "lowerLimitDisplay": "0",
			                "upperLimitDisplay": $scope.DmHitRatioData.maxValue,
			                "showValue": "1",
			                "valueBelowPivot": "1",
			                "theme": "fint"
			            },
			            colorRange: {
			                color: $scope.DmHitRatioData.range
			            },
			            dials: {
			                dial: [{
			                    value: $scope.DmHitRatioData.level
			                }]
			            }
			        }
			    }
			    );
			        fusioncharts.render();
				
				 });
			
		}
		
	});
	response.error(function(data, status, headers, config){
		//$(".underlay").hide();
		  if(status == constants.FORBIDDEN){
			location.href = 'login.html';
		  }else{  			  
			$state.transitionTo("ErrorPage",{statusvalue  : status});
		  }
	  });
}

/*-***********************************RECRUITERS DATA STARTS HERE*******************************************-*/	
$scope.getRecruitersTable = function() {
	$scope.recruitersTable=true;
	var recData = $scope.reportdata.recList;
	//alert(JSON.stringify(recData));
	dmwiseRecruitersTable();
	$scope.recruiters.RecruitersTableControl.options.data = recData;
}
/*-***********************************RECRUITERS DATA ENDS HERE*******************************************-*/	


/*-***********************************TABLE FUNCTION STARTS HERE*******************************************-*/		
function dmwiseRecruitersTable(){
        $scope.recruiters.RecruitersTableControl = {
                options: { 
                    striped: true,
                    pagination: true,
                    paginationVAlign: "bottom", 
                    pageList: [50,100,200],
                    search: false,
                    //sidePagination : 'server',
                    silentSort: false,
                    pageSize: 50,
                    showColumns: false,
                    showRefresh: false,
                    clickToSelect: false,
                    showToggle: false,
                    maintainSelected: true, 
                    showFooter : false,
                    undefinedText: 'NA',
                    columns: [
                   {
                        field: 'fullName',
                        title: 'Recruiter Name',
                        align: 'left',
                       
                    },{
                        field: 'designation',
                        title: 'Designation',
                        align: 'left',
                       
                    },{
                        field: 'status',
                        title: 'Status',
                        align: 'left',
                       
                    },{
                        field: 'noOfStarts',
                        title: '# Total Starts',
                        align: 'left',
                       
                    },{
                        field: 'noOfInActiveStarts',
                        title: 'InActive Starts',
                        align: 'left',
                       
                    },{
                        field: 'noOfMonthsWorked',
                        title: 'Active Months',
                        align: 'left',
                       
                    },{
                        field: 'avgHires',
                        title: 'Avg Hires',
                        align: 'left',
                       
                    },/*{
                        field: 'strJoinDate',
                        title: 'Joining Date',
                        align: 'left',
                       
                    },*/{
                        field: 'minStartCount',
                        title: 'Min / Year',
                        align: 'left',
                       
                    },{
                        field: 'maxStartCount',
                        title: 'Max / Year',
                        align: 'left',
                       
                    },{
                        field: 'statusValue',
                        title: 'Performance',
                        align: 'left',
                        formatter : colorFormatter
                       
                    },]
                }
        };
        
	}

function colorFormatter(value, row, index) {  
	 if(row.statusValue){
		 if(row.statusValue ==1){
			 return [
		                '<div  style = "background-color: #bb3a0f; color:#ffffff; text-align: center; width:100px;"> Poor </div>'
		                ].join(''); 
				 
  	 }
		 else if (row.statusValue ==2) {
			 return [
		                '<div  style = "background-color: #ffc200; color:#000000; text-align: center; width:100px;"> Average </div>'
		                ].join(''); 
		}
		 else if (row.statusValue ==3) {
			 return [
		                '<div  style = "background-color: #92d3bb; color:#000000; text-align: center; width:100px;"> Good </div>'
		                ].join(''); 
		}
		 else if (row.statusValue ==4) {
			 return [
		                '<div  style = "background-color: #3a9271; color:#ffffff; text-align: center; width:100px;"> Excellent </div>'
		                ].join(''); 
		}
		 else{
  		/* return [
		                '<div  style = "background-color: #ffffff; color:green;"> '+row.statusValue+' </div>'
		                ].join(''); */
  	 }
	 }
		 
   } 


/*-***********************************TABLE FUNCTION ENDS HERE*******************************************-*/
		
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
				
				$scope.onesettings = {
					    selectionLimit: 1,
					    smartButtonMaxItems: 1,
					             smartButtonTextConverter: function(itemText, originalItem) {
					                 return itemText;
					             },
					         };
				
				
				$scope.onesearchsettings = {
					    selectionLimit: 1,
					    enableSearch: true,
					    smartButtonMaxItems: 1,
					             smartButtonTextConverter: function(itemText, originalItem) {
					                 return itemText;
					             },
					         };
	
		
				
				$scope.showfilters = function()
				{
					if($("#filterfields").is(":visible"))
					{
					$("#filterfields").slideUp();
					}
				else
					{
					$("#filterfields").slideDown();
					}
					
				}
				
				$scope.showfiltersIN = function()
				{
					if($("#filterfieldsIN").is(":visible"))
					{
					$("#filterfieldsIN").slideUp();
					}
				else
					{
					$("#filterfieldsIN").slideDown();
					}
					
				}
				
				
				
				
				
				
				


				
				
				$scope.tabfunction = function()
				{
					$("div[data-click='tabitem']").click(function(){
						var clickedbuttonname = $(this).attr("data-tabname");
						$(".tabsmain div").removeClass("activetab");
						if(clickedbuttonname == "onsite")
							{
							$scope.sitename = "On Site";
							$(this).addClass("activetab");
							$("#onsite").show();
							$("#ofshore").hide();
							$("#usfilter").show();
							$("#infilter").hide();
							}
						if(clickedbuttonname == "ofshore")
							{
							$scope.sitename = "Off Shore";
							$(this).addClass("activetab");
							$("#onsite").hide();
							$("#ofshore").show();
							$("#usfilter").hide();
							$("#infilter").show();
							}
					});
				}
				
		
	})
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	.controller("INrecruitermetreportcontroller", function($scope,$http,$state,$timeout,$mdDialog,$rootScope){

		$scope.year = [];
		$scope.month = [];
		$scope.dm = [];
		$scope.recruiters=[];
		$scope.recruitersTable=false;
		
		$scope.onload = function()
		{
			
			if(($rootScope.rsLoginUser.userRole === constants.IN_Recruiter || $rootScope.rsLoginUser.userRole === constants.IN_DM || $rootScope.rsLoginUser.userRole === constants.IN_TL)){
				$scope.sitename = "Off Shore";
				$(this).addClass("activetab");
				$("#onsite").hide();
				$("#ofshore").show();
				$("#usfilter").hide();
				$("#infilter").show();
				}
			
			$(".underlay").show();
			$(".caret").addClass("fa fa-caret-down");
			$(".caret").css("font-size", "16px");
			$(".caret").css("color", "#1895ab");
			
			var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
			var monthFullName = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
			
			$scope.selectedYear = new Date().getFullYear();
			$scope.selectedMonth = monthName[new Date().getMonth()];
			$scope.selectedMonthFull = monthFullName[new Date().getMonth()];
			
			$scope.year = [{id: $scope.selectedYear, label: $scope.selectedYear}];
			$scope.month = [{id: $scope.selectedMonth, label: $scope.selectedMonthFull}];
			
			$scope.getAllSubmittalYears();
			
		}
		
		
		
/*-***************************************GET YEARS FUNCTION STARTS HERE****************************-*/
$scope.getAllSubmittalYears = function(isFromOnload,isYearChanged){
			

		/*	var response = $http.get('reportController/getAllSubmittalYears');
			response.success(function (data,status,headers,config){
				$scope.yearList = [];
				for(var i=0; i<data.length; i++ )
					{
						var obj = {id: data[i], label: data[i]};
						$scope.yearList.push(obj);
					}
				
		        $scope.yeardata = $scope.yearList;
		        $scope.getAllUsers();
		      
			
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
	 $scope.getAllUsers();

};
/*-***************************************GET YEARS FUNCTION ENDS HERE****************************-*/
		
/*-*************************** GET USERS FUNCTION STARTS HERE********************************************-*/
$scope.getAllUsers = function(isFromOnload,isYearChanged){
	var response = $http.get('IndiaCommonController/getAllRecruiters?isAuthRequired='+true+'&isIndia='+true);
	response.success(function (data,status,headers,config){
		
		$scope.userRecords = data;
		$scope.dmdata = [];
		if(data && data.length>0){
		$scope.uesrList = [];
		for(var i=0; i<data.length; i++ )
			{
				var obj = {id: data[i].userId, label: data[i].fullName};
				$scope.uesrList.push(obj);
			}
		
		$scope.dm = {id: data[0].userId, label: data[0].fullName};
		$scope.dmdata = $scope.uesrList;
		$scope.getdataforreport();
		}else{
			$(".underlay").hide();
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
/*-*************************** GET USERS FUNCTION ENDS HERE********************************************-*/
		



/*-*************************** GET LEVEL AND NAME FUNCTION STARTS HERE********************************************-*/

$scope.getdataforreport = function()
{
	$scope.recruiters=[];
	if($scope.year == "" || $scope.year == undefined || $scope.year == null)
	{
		$("#mustyear").show();
	}
else
	{
	$("#mustyear").hide();
	var dmnametocheck = $scope.dm.id;
	if(dmnametocheck == "" || dmnametocheck == undefined || dmnametocheck == null)
	{
		$("#mustdm").show();
	}
else
	{
	$("#mustdm").hide();
	$scope.recruitersTable=false;
	$scope.getGridData();
	}
	
	}
}
/*-*************************** GET LEVEL AND NAME FUNCTION ENDS HERE********************************************-*/



/*$scope.hideandgetdetails = function()
{
	$("#filterfields").slideUp();
	$scope.getdataforreport();
	
}*/


		
/*-*************************** GET DATA FOR REPORT STARTS HERE********************************************-*/

		$scope.getGridData = function(){
			
			$scope.yearforReport = "";
			$scope.monthforReport = "";
			
			for(var i=0; i < $scope.year.length; i++)
			{
			$scope.yearforReport += $scope.year[i].id + ", ";
			}
			for(var i=0; i < $scope.month.length; i++)
			{
			$scope.monthforReport += $scope.month[i].id + ", ";
			}
			
			
			
			
			for(var i=0; i<$scope.uesrList.length; i++)
			{
				if($scope.dm.id == $scope.uesrList[i].id)
					{
					$scope.showFullName = $scope.uesrList[i].label;
					}
				else
					{
					
					}
			}
			
			
			
			$scope.hitRatioData = {year: $scope.yearforReport, month: $scope.monthforReport =='' ? null : $scope.monthforReport, dmName: $scope.dm.id};
			var obj = {year: $scope.yearforReport, month: $scope.monthforReport =='' ? null : $scope.monthforReport, dmName: $scope.dm.id, status:'STARTED'};
			//alert(JSON.stringify(obj));
			$scope.datafordays = obj;
			
			if($scope.dm.label == "" || $scope.dm.label == undefined || $scope.dm.label == null)
			{
			$scope.showname = $scope.dm.id;
			}
		else
			{
			$scope.showname = $scope.dm.label;
			}
			
			var response = $http.post('indiaReports/getIndiaDmMetricData',obj);
			response.success(function (dataObj,status,headers,config){
				//alert(JSON.stringify(dataObj));
				if(dataObj && dataObj.dmName){
					$scope.reportdata = dataObj;
					$scope.getRecruitersTable();
					$scope.getNoOfStats();
					$scope.dmRecruitersTable = true;
				}else{
					$(document).ready(function () { 
						var fusioncharts = new FusionCharts({
						    type: 'angulargauge',
						    renderAt: 'chart-containerIN',
						    width: '350',
						    height: '250',
						    dataFormat: 'json',
						    dataSource: {
						        "chart": {
						            "caption": "Total No Of Starts By "+$scope.showFullName,
						            "lowerLimit": "0",
						            "upperLimit": "100",
						            "lowerLimitDisplay": "Poor",
						            "upperLimitDisplay": "Good",
						            "showValue": "1",
						            "valueBelowPivot": "1",
						            "theme": "fint"
						        },
						        "colorRange": {
						            "color": [{
						                "minValue": "0",
						                "maxValue": "25",
						                "code": "#bb3a0f"
						            }, {
						                "minValue": "25",
						                "maxValue": "50",
						                "code": "#d2c459"
						            }, {
						                "minValue": "50",
						                "maxValue": "75",
						                "code": "#92d3bb"
						            },
						            {
						                "minValue": "75",
						                "maxValue": "100",
						                "code": "#3a9271"
						            }]
						        },
						        "dials": {
						            "dial": [{
						                "value": "0"
						            }]
						        }
						    }
						}
						);
						    fusioncharts.render();
						    
					   });
					
					$(document).ready(function () { 
						var fusioncharts = new FusionCharts({
						    type: 'angulargauge',
						    renderAt: 'chart-containerIN1',
						    width: '350',
						    height: '250',
						    dataFormat: 'json',
						    dataSource: {
						        "chart": {
						            "caption": "Average No of Days to Starts",
						            "lowerLimit": "0",
						            "upperLimit": "100",
						            "lowerLimitDisplay": "Good",
						            "upperLimitDisplay": "Poor",
						            "showValue": "1",
						            "valueBelowPivot": "1",
						            "theme": "fint"
						        },
						        "colorRange": {
						            "color": [{
						                "minValue": "0",
						                "maxValue": "25",
						                "code": "#bb3a0f"
						            }, {
						                "minValue": "25",
						                "maxValue": "50",
						                "code": "#d2c459"
						            }, {
						                "minValue": "50",
						                "maxValue": "75",
						                "code": "#92d3bb"
						            },
						            {
						                "minValue": "75",
						                "maxValue": "100",
						                "code": "#3a9271"
						            }]
						        },
						        "dials": {
						            "dial": [{
						                "value": "0"
						            }]
						        }
						    }
						}
						);
						    fusioncharts.render();
					   });
					
					$(document).ready(function () { 
						var fusioncharts = new FusionCharts({
						    type: 'angulargauge',
						    renderAt: 'chart-containerIN2',
						    width: '350',
						    height: '250',
						    dataFormat: 'json',
						    dataSource: {
						        "chart": {
						            "caption": "Starts to Submittals Rate (%)",
						            "lowerLimit": "0",
						            "upperLimit": "100",
						            "lowerLimitDisplay": "Poor",
						            "upperLimitDisplay": "Good",
						            "showValue": "1",
						            "valueBelowPivot": "1",
						            "theme": "fint"
						        },
						        "colorRange": {
						            "color": [{
						                "minValue": "0",
						                "maxValue": "25",
						                "code": "#bb3a0f"
						            }, {
						                "minValue": "25",
						                "maxValue": "50",
						                "code": "#d2c459"
						            }, {
						                "minValue": "50",
						                "maxValue": "75",
						                "code": "#92d3bb"
						            },
						            {
						                "minValue": "75",
						                "maxValue": "100",
						                "code": "#3a9271"
						            }]
						        },
						        "dials": {
						            "dial": [{
						                "value": "0"
						            }]
						        }
						    }
						}
						);
						    fusioncharts.render();
					   });
					$scope.reportdata = [];
					$scope.getRecruitersTable();
				}
				//$(".underlay").hide();
			});
			response.error(function(data, status, headers, config){
				//$(".underlay").hide();
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
			
			
			$(".underlay").hide();
			
		}
	
/*-*************************** GET DATA FOR REPORT ENDS HERE********************************************-*/
		
		
$scope.getNoOfStats = function()
{
	$scope.totalData = $scope.reportdata;
	//alert(JSON.stringify($scope.totalData));
	$(document).ready(function () { 
	    var fusioncharts = new FusionCharts({
	        type: 'angulargauge',
	        renderAt: 'chart-containerIN',
	        width: '350',
	        height: '250',
	        dataFormat: 'json',
	        dataSource: {
	            chart: {
	                "caption": "Total No Of Starts By "+$scope.showFullName,
	                "lowerLimit": "0",
	                "upperLimit": $scope.totalData.maxValue,
	                "lowerLimitDisplay": "0",
	                "upperLimitDisplay": $scope.totalData.maxValue,
	                "showValue": "1",
	                "valueBelowPivot": "1",
	                "theme": "fint"
	            },
	            colorRange: {
	            	 color: $scope.totalData.range
	            },
	            dials: {
	                dial: [{
	                    value: $scope.totalData.level
	                }]
	            }
	        }
	    }
	    );
	        fusioncharts.render();
	   });

	
	
	$scope.getAvgNoOfDays();
	
}
		

$scope.getAvgNoOfDays = function()
{
//	alert(JSON.stringify($scope.datafordays));
	var response = $http.post('indiaReports/getIndiaDMAverageNoOfDaysForStatus',$scope.datafordays);
	response.success(function (dataObj,status,headers,config){
		$scope.avgTotalData = dataObj;
		if(dataObj){
			$(document).ready(function () { 
				var fusioncharts = new FusionCharts({
				    type: 'angulargauge',
				    renderAt: 'chart-containerIN1',
				    width: '350',
			        height: '250',
			        dataFormat: 'json',
			        dataSource: {
			            chart: {
			                "caption": "Average No of Days to Starts",
			                "lowerLimit": "0",
			                "upperLimit": $scope.avgTotalData.maxValue,
			                "lowerLimitDisplay": "0",
			                "upperLimitDisplay": $scope.avgTotalData.maxValue,
			                "showValue": "1",
			                "valueBelowPivot": "1",
			                "theme": "fint"
			            },
			            colorRange: {
			                color: $scope.avgTotalData.range
			            },
			            dials: {
			                dial: [{
			                    value: $scope.avgTotalData.level
			                }]
			            }
			        }
			    }
			    );
			        fusioncharts.render();
				
				 });
			
		}
		//$(".underlay").hide();
	});
	response.error(function(data, status, headers, config){
		//$(".underlay").hide();
		  if(status == constants.FORBIDDEN){
			location.href = 'login.html';
		  }else{  			  
			$state.transitionTo("ErrorPage",{statusvalue  : status});
		  }
	  });
	$scope.getHitRatio();
}

$scope.getHitRatio = function(){
	var response = $http.post('indiaReports/indiadmfindHitRatio',$scope.hitRatioData);
	response.success(function (dataObj,status,headers,config){
		$scope.DmHitRatioData = dataObj;
		if(dataObj){
			$(document).ready(function () { 
				var fusioncharts = new FusionCharts({
				    type: 'angulargauge',
				    renderAt: 'chart-containerIN2',
				    width: '350',
			        height: '250',
			        dataFormat: 'json',
			        dataSource: {
			            chart: {
			                "caption": "Starts to Submittals Rate (%)",
			                "lowerLimit": "0",
			                "upperLimit": $scope.DmHitRatioData.maxValue,
			                "lowerLimitDisplay": "0",
			                "upperLimitDisplay": $scope.DmHitRatioData.maxValue,
			                "showValue": "1",
			                "valueBelowPivot": "1",
			                "theme": "fint"
			            },
			            colorRange: {
			                color: $scope.DmHitRatioData.range
			            },
			            dials: {
			                dial: [{
			                    value: $scope.DmHitRatioData.level
			                }]
			            }
			        }
			    }
			    );
			        fusioncharts.render();
				
				 });
			
		}
		
	});
	response.error(function(data, status, headers, config){
		//$(".underlay").hide();
		  if(status == constants.FORBIDDEN){
			location.href = 'login.html';
		  }else{  			  
			$state.transitionTo("ErrorPage",{statusvalue  : status});
		  }
	  });
}

/*-***********************************RECRUITERS DATA STARTS HERE*******************************************-*/	
$scope.getRecruitersTable = function() {
	$scope.recruitersTable=true;
	var recData = $scope.reportdata.recList;
	//alert(JSON.stringify(recData));
	dmwiseRecruitersTable();
	$scope.recruiters.RecruitersTableControl.options.data = recData;
}
/*-***********************************RECRUITERS DATA ENDS HERE*******************************************-*/	


/*-***********************************TABLE FUNCTION STARTS HERE*******************************************-*/		
function dmwiseRecruitersTable(){
        $scope.recruiters.RecruitersTableControl = {
                options: { 
                    striped: true,
                    pagination: true,
                    paginationVAlign: "bottom", 
                    pageList: [50,100,200],
                    search: false,
                    //sidePagination : 'server',
                    silentSort: false,
                    pageSize: 50,
                    showColumns: false,
                    showRefresh: false,
                    clickToSelect: false,
                    showToggle: false,
                    maintainSelected: true, 
                    showFooter : false,
                    undefinedText: 'NA',
                    columns: [
                   {
                        field: 'fullName',
                        title: 'Recruiter Name',
                        align: 'left',
                       
                    },{
                        field: 'designation',
                        title: 'Designation',
                        align: 'left',
                       
                    },{
                        field: 'status',
                        title: 'Status',
                        align: 'left',
                       
                    },{
                        field: 'noOfStarts',
                        title: '# Total Starts',
                        align: 'left',
                       
                    },{
                        field: 'noOfBackOuts',
                        title: '# Total Backouts',
                        align: 'left',
                       
                    },{
                        field: 'noOfInActiveStarts',
                        title: 'InActive Starts',
                        align: 'left',
                       
                    },{
                        field: 'noOfMonthsWorked',
                        title: 'Active Months',
                        align: 'left',
                       
                    },{
                        field: 'avgHires',
                        title: 'Avg Hires',
                        align: 'left',
                       
                    },{
                        field: 'minStartCount',
                        title: 'Min / Year',
                        align: 'left',
                       
                    },{
                        field: 'avgStartCount',
                        title: 'Avg / Year',
                        align: 'left',
                       
                    },{
                        field: 'maxStartCount',
                        title: 'Max / Year',
                        align: 'left',
                       
                    },{
                        field: 'statusValue',
                        title: 'Performance',
                        align: 'left',
                        formatter : colorFormatter
                       
                    },]
                }
        };
        
	}

function colorFormatter(value, row, index) {  
	 if(row.statusValue){
		 if(row.statusValue ==1){
			 return [
		                '<div  style = "background-color: #bb3a0f; color:#ffffff; text-align: center; width:100px;"> Poor </div>'
		                ].join(''); 
				 
  	 }
		 else if (row.statusValue ==2) {
			 return [
		                '<div  style = "background-color: #ffc200; color:#000000; text-align: center; width:100px;"> Average </div>'
		                ].join(''); 
		}
		 else if (row.statusValue ==3) {
			 return [
		                '<div  style = "background-color: #92d3bb; color:#000000; text-align: center; width:100px;"> Good </div>'
		                ].join(''); 
		}
		 else if (row.statusValue ==4) {
			 return [
		                '<div  style = "background-color: #3a9271; color:#ffffff; text-align: center; width:100px;"> Excellent </div>'
		                ].join(''); 
		}
		 else{
  		/* return [
		                '<div  style = "background-color: #ffffff; color:green;"> '+row.statusValue+' </div>'
		                ].join(''); */
  	 }
	 }
		 
   } 


/*-***********************************TABLE FUNCTION ENDS HERE*******************************************-*/
		
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
				
				$scope.onesettings = {
					    selectionLimit: 1,
					    smartButtonMaxItems: 1,
					             smartButtonTextConverter: function(itemText, originalItem) {
					                 return itemText;
					             },
					         };
				
				
				$scope.onesearchsettings = {
					    selectionLimit: 1,
					    enableSearch: true,
					    smartButtonMaxItems: 1,
					             smartButtonTextConverter: function(itemText, originalItem) {
					                 return itemText;
					             },
					         };
	
		
				
				$scope.showfilters = function()
				{
					if($("#filterfields").is(":visible"))
						{
						$("#filterfields").slideUp();
						}
					else
						{
						$("#filterfields").slideDown();
						}
					
				}
				
		
	});
	
	
	
	

	
	

	
})(angular);