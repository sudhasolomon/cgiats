;(function(angular){
	
	"use strict";
	
			angular.module("barReportsModule", [])
			.controller("barreportscontroller", function($scope, $http, $timeout){
			
				$scope.onload = function()
				{
				
					$scope.pagename = "NEW REPORT";
					
					$("#monthdd li input").click(function(){
						$timeout(function() {
							$scope.getweeksfromdbonchange();
							}, 1000);
					});
					
					
					$timeout(function() {
						$("#yeardd li input").click(function(){
							$scope.getweeksfromdbonchange();
						});
						
						}, 2000);
					
					
					
					
					$scope.getAllSubmittalYears(true,false);
					//$scope.getGridData();
					
					
					/*bar chart setup*/
					
					 Highcharts.setOptions({
					        chart: {
					            backgroundColor: {
					                linearGradient: [0, 0, 500, 500],
					                stops: [
					                    [0, 'rgb(255, 255, 255)'],
					                    [1, 'rgb(240, 240, 255)']
					                    ]
					            },
					            borderWidth: 2,
					            plotBackgroundColor: 'rgba(255, 255, 255, .9)',
					            plotShadow: true,
					            plotBorderWidth: 1,
					        }
					    });
					
					 /*bar chart setup End*/
				}
				
				
				
				$scope.checkandgetbardata = function(){
					
					alert("get data");
					barCallFun();
				}
				
				
				
				function barCallFun(){
					 $scope.myCharts = Highcharts.chart('container',{
						
						 chart:{
							 type : 'column'
						 },
						title:{
							text : 'Fruits'
						},
						xAxis:{
							categories : ['Jan', 'Feb', 'Mar', 'Apr']
						},
						 
						 plotOptions: {
					            series: {
					                cursor: 'pointer',
					                point: {
					                    events: {
					                        click: function () {
					                            alert('Category: ' + this.category + ', value: ' + this.y+ ', name'+this.x);
					                        /*  $http.get("barCall/getBar?cat="+this.category+"&val="+this.y);*/
					                        }
					                    }
					                }
					            }
					        },
						series: [{
							data :[29.9, 71.5, 106.4, 129.2 ]
						}]
					 });
					 $(".highcharts-color-0").css("fill", "#000000");
				}
				
				
				
				$scope.getAllSubmittalYears = function(isFromOnload,isYearChanged){
				var response = $http.get('reportController/getAllSubmittalYears');
				response.success(function (data,status,headers,config){
					$scope.yearList = data;

					$timeout(function() {
					$scope.getweeksfromdb();
					}, 1);
					
				});
				response.error(function(data, status, headers, config){
	  			  if(status == constants.FORBIDDEN){
	  				location.href = 'login.html';
	  			  }else{  			  
	  				$state.transitionTo("ErrorPage",{statusvalue  : status});
	  			  }
	  		  });
			};
				$scope.getweeksfromdb = function()
				{
					var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
					
						$scope.selectedYear = new Date().getFullYear();
						$scope.selectedMonth = monthName[new Date().getMonth()];

//						alert($scope.selectedYear + "" +$scope.selectedMonth);
						
						
							var response = $http.get('reportController/getWeeksOfMonth?year='+$scope.selectedYear+'&month='+$scope.selectedMonth);
							response.success(function (data,status,headers,config){
								$scope.weeksList = data;
//								alert(JSON.stringify($scope.weeksList));
								$timeout(function() {
									//$scope.getrecdata();
									}, 1400);
								
								
							});
							response.error(function(data, status, headers, config){
				  			  if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
				  		  });
				}
				
				$scope.getweeksfromdbonchange = function()
				{
					
					$timeout(function() {
						$("#weekdd li input").click(function(){
							
							var res ="";
							var checkbxval = $("#weekdd").children("li").children("input:checked");
							checkbxval.each(function() {
								var data = $(this).val() + ", ";
								var preres = res + data;
								res = preres;
							});
							
							
							if(res == "")
								{
								$("#weekdd").siblings(".ddbox").val(res);
								$("#weekdd").siblings(".blankmsg").show();
								$("#weekdd").siblings(".errormsg").show();
								$("#weekdd").siblings(".ddbox").css("border-color", "#ff0000");
								}
							else
								{
								$("#weekdd").siblings(".ddbox").val(res);
								$("#weekdd").siblings(".blankmsg").hide();
								$("#weekdd").siblings(".errormsg").hide();
								$("#weekdd").siblings(".ddbox").css("border-color", "#c2cad8");
								}
							
							
							var listid = $(this).parent("li").parent(".ddlist").attr("id");
							var uncheckboxes = $("#" + listid).children("li").children("input:checkbox:not(:checked)");
							
							if($(this).is(":checked"))
							{
							if(uncheckboxes.length == 0)
								{
								$(this).parent("li").siblings(".selectall").prop("checked", "true");
								}
							}
						else
							{
							$(this).parent("li").siblings(".selectall").removeAttr("checked");
							}
							
							
							
						});;
						
					}, 2000);
					
						$scope.selectedYear = $("#recselectedyear").val();
						$scope.selectedMonth = $("#recselectedmonth").val();

//						alert($scope.selectedYear + "" +$scope.selectedMonth);
						
						if(!$scope.selectedYear || !$scope.selectedMonth)
							{
							
							}
						else
							{
							
							if($scope.selectedYear.indexOf(",") < 0 && $scope.selectedMonth.indexOf(",") < 0)
							{
							var response = $http.get('reportController/getWeeksOfMonth?year='+$scope.selectedYear+'&month='+$scope.selectedMonth);
							response.success(function (data,status,headers,config){
								
								$scope.weeksList = data;
//								alert(JSON.stringify($scope.weeksList));
								
								
								
							});
							response.error(function(data, status, headers, config){
				  			  if(status == constants.FORBIDDEN){
				  				location.href = 'login.html';
				  			  }else{  			  
				  				$state.transitionTo("ErrorPage",{statusvalue  : status});
				  			  }
				  		  });
							
							}
						else
							{
							}
							
							}
						
						$timeout(function() {
								var res01 ="";
								var checkbxval01 = $("#weekdd").children("li").children("input:checked");
								checkbxval01.each(function() {
									var data01 = $(this).val() + ", ";
									var preres01 = res01 + data01;
									res01 = preres01;
								});
								if(res01 == "")
									{
									var ab = $("#recselectedweek").val();
									$("#recselectedweek").val(ab);
									}
								else
									{
									$("#weekdd").siblings(".ddbox").val(res01);
									$("#weeksel").prop("checked", "true");
									$("#weekdd").siblings(".blankmsg").hide();
									$("#weekdd").siblings(".ddbox").siblings(".errormsg").hide();
									$("#weekdd").siblings(".ddbox").css("border-color", "#c2cad8");
									
									}
						}, 1000);
						
				}
			});
			
		
})(angular);