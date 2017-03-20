;(function(angular){
	angular.module("stackedbarreportModule",['jcs-autoValidate'])
	.controller("stackedbarreportcontroller", function($scope,$http,$state,$timeout,$mdDialog){

		$scope.onload = function()
		{
			/*$scope.config = {
	    		    title: 'Total Report',
	    		    tooltips: true,
	    		    labels: true,
	    		    mouseover: function() {},
	    		    mouseout: function() {},
	    		    click : function(d) {
	    		    	alert(JSON.stringify(d));
	    		    	  $scope.messages.push('clicked!');
	    		    	},
	    		    legend: {
	    		      display: true,
	    		      //could be 'left, right'
	    		      position: 'right'
	    		    }
	    		  };*/
			
			//$(".underlay").show();
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
			//guage();
			$scope.getGridData();
		}
		
		
		
		$scope.getGridData = function(){
			/*var obj ={year:$scope.Year, month:$scope.Month};*/
			
//			var obj ={year:2016};
			
//			alert(JSON.stringify(obj));
			$scope.year =  $("#recselectedyear").val().replace(/\s/g,"").replace(/\,$/, '');
			$scope.month = $("#recselectedmonth").val();
			
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
//			for(var i=0; i < $scope.week.length; i++)
//			{
//			$scope.weekforReport += $scope.week[i].id + ", ";
//			}
			
			
			var obj = {year: (new Date().getFullYear()),dmName:'Vincent',status:'STARTED'};
			
			
			
			var response = $http.post('totalReportController/getDMWiseRecPerformanceTotalReport',obj);
			response.success(function (dataObj,status,headers,config){
				if(dataObj){
					
					$scope.totalData = dataObj;
					  $(document).ready(function () { 
						    var fusioncharts = new FusionCharts({
						        type: 'angulargauge',
						        renderAt: 'chart-container',
						        width: '700',
						        height: '350',
						        dataFormat: 'json',
						        dataSource: {
						            chart: {
						                "caption": "Total No Of Starts By "+$scope.totalData.dmName+" is : <b>"+$scope.totalData.level+"</b>",
						                "lowerLimit": "0",
						                "upperLimit": "100",
						                "lowerLimitDisplay": "Bad",
						                "upperLimitDisplay": "Good",
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
			
			
			$(function () {
			    // Create the chart
			    $('#container').highcharts({
			        chart: {
			            type: 'column'
			        },
			        title: {
			            text: 'Drilldown label styling'
			        },
			        xAxis: {
			            type: 'category'
			        },

			        legend: {
			            enabled: false
			        },

			        plotOptions: {
			            series: {
			                borderWidth: 0,
			                dataLabels: {
			                    enabled: true
			                }
			            }
			        },

			        series: [{
			            name: 'Things',
			            colorByPoint: true,
			            data: [{
			                name: 'Dieren',
			                y: 5,
			                drilldown: 'animals'
			            }, {
			                name: 'Fruit',
			                y: 2,
			                drilldown: 'fruits'
			            }, {
			                name: 'Auto\'s',
			                y: 4
			            }]
			        }],
			        drilldown: {
			            drillUpButton: {
			               // relativeTo: 'spacingBox',
			                position: {
			                    y: 0,
			                    x: 0
			                },
			                theme: {
			                    fill: 'white',
			                    'stroke-width': 1,
			                    stroke: 'silver',
			                    r: 0,
			                    states: {
			                        hover: {
			                            fill: '#bada55'
			                        },
			                        select: {
			                            stroke: '#039',
			                            fill: '#bada55'
			                        }
			                    }
			                }

			            },
			            series: [{
			                id: 'animals',
			                data: [
			                    ['Katten', 4],
			                    ['Honden', 2],
			                    ['Koeien', 1],
			                    ['Schapen', 2],
			                    ['Varkens', 1]
			                ]
			            }, {
			                id: 'fruits',
			                data: [
			                    ['Appels', 4],
			                    ['Sinaasappels', 2]
			                ]
			            }]
			        }
			    });
			});


			
			
	/*		$(function () {
			    // Create the chart
			    $('#container').highcharts({
			        chart: {
			            type: 'column'
			        },
			        title: {
			            text: 'Browser market shares. January, 2015 to May, 2015'
			        },
			        subtitle: {
			            text: 'Click the columns to view versions. Source: <a href="http://netmarketshare.com">netmarketshare.com</a>.'
			        },
			        xAxis: {
			            type: 'category'
			        },
			        yAxis: {
			            title: {
			                text: 'Total percent market share'
			            }

			        },
			        legend: {
			            enabled: false
			        },
			        plotOptions: {
			            series: {
			                borderWidth: 0,
			                dataLabels: {
			                    enabled: true,
			                    format: '{point.y:.1f}%'
			                }
			            }
			        },

			        tooltip: {
			            headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
			            pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
			        },

			        series: [{
			            name: 'Brands',
			            //colorByPoint: true,
			            data: [{
			                name: 'Microsoft Internet Explorer',
			                y: 56.33,
			                drilldown: 'Microsoft Internet Explorer'
			            }, {
			                name: 'Chrome',
			                y: 24.03,
			                drilldown: 'Chrome'
			            }, {
			                name: 'Firefox',
			                y: 10.38,
			                drilldown: 'Firefox'
			            }, {
			                name: 'Safari',
			                y: 4.77,
			                drilldown: 'Safari'
			            }, {
			                name: 'Opera',
			                y: 0.91,
			                drilldown: 'Opera'
			            }, {
			                name: 'Proprietary or Undetectable',
			                y: 0.2,
			                drilldown: null
			            }]
			        }],
			        drilldown: {
			            series: [{
			                name: 'Microsoft Internet Explorer',
			                id: 'Microsoft Internet Explorer',
			                data: [
			                    [
			                        'v11.0',
			                        24.13
			                    ],
			                    [
			                        'v8.0',
			                        17.2
			                    ],
			                    [
			                        'v9.0',
			                        8.11
			                    ],
			                    [
			                        'v10.0',
			                        5.33
			                    ],
			                    [
			                        'v6.0',
			                        1.06
			                    ],
			                    [
			                        'v7.0',
			                        0.5
			                    ]
			                ]
			            }, {
			                name: 'Chrome',
			                id: 'Chrome',
			                data: [
			                    [
			                        'v40.0',
			                        5
			                    ],
			                    [
			                        'v41.0',
			                        4.32
			                    ],
			                    [
			                        'v42.0',
			                        3.68
			                    ],
			                    [
			                        'v39.0',
			                        2.96
			                    ],
			                    [
			                        'v36.0',
			                        2.53
			                    ],
			                    [
			                        'v43.0',
			                        1.45
			                    ],
			                    [
			                        'v31.0',
			                        1.24
			                    ],
			                    [
			                        'v35.0',
			                        0.85
			                    ],
			                    [
			                        'v38.0',
			                        0.6
			                    ],
			                    [
			                        'v32.0',
			                        0.55
			                    ],
			                    [
			                        'v37.0',
			                        0.38
			                    ],
			                    [
			                        'v33.0',
			                        0.19
			                    ],
			                    [
			                        'v34.0',
			                        0.14
			                    ],
			                    [
			                        'v30.0',
			                        0.14
			                    ]
			                ]
			            }, {
			                name: 'Firefox',
			                id: 'Firefox',
			                data: [
			                    [
			                        'v35',
			                        2.76
			                    ],
			                    [
			                        'v36',
			                        2.32
			                    ],
			                    [
			                        'v37',
			                        2.31
			                    ],
			                    [
			                        'v34',
			                        1.27
			                    ],
			                    [
			                        'v38',
			                        1.02
			                    ],
			                    [
			                        'v31',
			                        0.33
			                    ],
			                    [
			                        'v33',
			                        0.22
			                    ],
			                    [
			                        'v32',
			                        0.15
			                    ]
			                ]
			            }, {
			                name: 'Safari',
			                id: 'Safari',
			                data: [
			                    [
			                        'v8.0',
			                        2.56
			                    ],
			                    [
			                        'v7.1',
			                        0.77
			                    ],
			                    [
			                        'v5.1',
			                        0.42
			                    ],
			                    [
			                        'v5.0',
			                        0.3
			                    ],
			                    [
			                        'v6.1',
			                        0.29
			                    ],
			                    [
			                        'v7.0',
			                        0.26
			                    ],
			                    [
			                        'v6.2',
			                        0.17
			                    ]
			                ]
			            }, {
			                name: 'Opera',
			                id: 'Opera',
			                data: [
			                    [
			                        'v12.x',
			                        0.34
			                    ],
			                    [
			                        'v28',
			                        0.24
			                    ],
			                    [
			                        'v27',
			                        0.17
			                    ],
			                    [
			                        'v29',
			                        0.16
			                    ]
			                ]
			            }]
			        }
			    });
			});*/
			
			
		}
		


		
		
		
		
		var recsData = [];
		var exportrecsData = []
		$scope.recruitersReport = [];
		$scope.exportrecruitersReport = [];
		$scope.RecruitersReportTable = false;
		$scope.exportRecruitersReportTable = false;
		
	/*	function guage(){
			var myChart = Highcharts.chart('container',{
				chart: {
		            type: 'gauge',
		            plotBackgroundColor: null,
		            plotBackgroundImage: null,
		            plotBorderWidth: 0,
		            plotShadow: false
		        },

		        title: {
		            text: 'Speedometer'
		        },

		        pane: {
		            startAngle: -150,
		            endAngle: 150,
		            background: [{
		                backgroundColor: {
		                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
		                    stops: [
		                        [0, '#FFF'],
		                        [1, '#333']
		                    ]
		                },
		                borderWidth: 0,
		                outerRadius: '109%'
		            }, {
		                backgroundColor: {
		                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
		                    stops: [
		                        [0, '#333'],
		                        [1, '#FFF']
		                    ]
		                },
		                borderWidth: 1,
		                outerRadius: '107%'
		            }, {
		                // default background
		            }, {
		                backgroundColor: '#DDD',
		                borderWidth: 0,
		                outerRadius: '105%',
		                innerRadius: '103%'
		            }]
		        },

		        // the value axis
		        yAxis: {
		            min: 0,
		            max: 200,

		            minorTickInterval: 'auto',
		            minorTickWidth: 1,
		            minorTickLength: 10,
		            minorTickPosition: 'inside',
		            minorTickColor: '#666',

		            tickPixelInterval: 30,
		            tickWidth: 2,
		            tickPosition: 'inside',
		            tickLength: 10,
		            tickColor: '#666',
		            labels: {
		                step: 2,
		                rotation: 'auto'
		            },
		            title: {
		                text: 'km/h'
		            },
		            plotBands: [{
		                from: 0,
		                to: 120,
		                color: '#55BF3B' // green
		            }, {
		                from: 120,
		                to: 160,
		                color: '#DDDF0D' // yellow
		            }, {
		                from: 160,
		                to: 200,
		                color: '#DF5353' // red
		            }]
		        },

		        series: [{
		            name: 'Speed',
		            data: [80],
		            tooltip: {
		                valueSuffix: ' km/h'
		            }
		        }]

		    },
		    // Add some life
		    function (chart) {
		        if (!chart.renderer.forExport) {
		            setInterval(function () {
		                var point = chart.series[0].points[0],
		                    newVal,
		                    inc = Math.round((Math.random() - 0.5) * 20);

		                newVal = point.y + inc;
		                if (newVal < 0 || newVal > 200) {
		                    newVal = point.y - inc;
		                }

		                point.update(newVal);

		            }, 3000);
		        }
			})
		    $('#container').highcharts({

		        
		    });
			
		
		
	}*/
		
		$scope.checkandgetdata = function()
		{
			
			
			if($(".errormsg").is(":visible"))
				{
				$("#res").css("display", "block");
				}
			else
				{
				$("#res").css("display", "none");
				$scope.Year =  $("#recselectedyear").val().replace(/\s/g,"").replace(/\,$/, '');
				$scope.Month = $("#recselectedmonth").val();
				//$scope.getGridData();
				}
			
	/*			$(function () {
		    $('#container').highcharts({
		        chart: {
		            type: 'bar'
		        },
		        title: {
		            text: 'Stacked bar chart'
		        },
		        xAxis: {
		            categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: 'Total fruit consumption'
		            }
		        },
		        legend: {
		            reversed: true
		        },
		        plotOptions: {
		            series: {
		                stacking: 'normal',
		                         point: {
		                    events: {
		                        click: function () {
		                            alert('Category: ' + this.category + ', value: ' + this.y +',name: '+ this.series.name);
		                        }
		                    }
		                }
		            }
		        },
		        series: [{
		            name: 'John',
		            data: [5, 3, 4, 7, 2]
		        }, {
		            name: 'Jane',
		            data: [2, 2, 3, 2, 1]
		        }, {
		            name: 'Joe',
		            data: [3, 4, 4, 2, 5]
		        }]
		    });
		});*/
			
			
			/*$(function () {
			    $('#container').highcharts({
			        chart: {
			            type: 'column'
			        },
			        title: {
			            text: 'Monthly Average Rainfall'
			        },
			        subtitle: {
			            text: 'Source: WorldClimate.com'
			        },
			        xAxis: {
			            categories: [
			                'Recruiter1',
			                'Recruiter2',
			                'Recruiter3',
			                'Recruiter4',
			                'Recruiter5',
			                'Recruiter6',
			                'Recruiter7',
			                'Recruiter8',
			                'Recruiter9',
			                'Recruiter10'
			            ],
			            crosshair: true
			        },
			        yAxis: {
			            min: 0,
			            title: {
			                text: 'Rainfall (mm)'
			            }
			        },
			        tooltip: {
			            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			                '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
			            footerFormat: '</table>',
			            shared: true,
			            useHTML: true
			        },
			        plotOptions: {
			            column: {
			                pointPadding: 0.2,
			                borderWidth: 0
			            },
			            series: {
			                //stacking: 'normal',
			                         point: {
			                    events: {
			                        click: function () {
			                            alert('Category: ' + this.category + ', value: ' + this.y +',name: '+ this.series.name);
			                        }
			                    }
			                }
			            }
			        },
			        series: [{
			            name: 'Tokyo',
			            data: [49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1]

			        } ,{
			            name: 'New York',
			            data: [83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3]

			        }, {
			            name: 'London',
			            data: [48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2]

			        }, {
			            name: 'Berlin',
			            data: [42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1]

			        }]
			    });
			});
			*/
			
			
			/*
			var chart;
			point = null;
			$(document).ready(function () {

			    var data = [{ name: 'One', y: 10},{ name: 'Two', y: 65},{ name: 'Three', y: 46},{ name: 'Four', y: 56}];
			    
			    chart = new Highcharts.Chart(
			    {
			        title: {
			            text: 'pie chart'
			        },
			        
		            plotOptions: {
		                pie: {
		                    allowPointSelect: true,
		                    cursor: 'pointer',
		                    dataLabels: {
		                        distance: -30,
		                        format: '<b>{point.name}</b>: {point.y}',
		                    },
		                    showInLegend: true
		                }
		            },
		            
			       series:[
			          {
			             "data": data,
			              type: 'pie',
			              animation: false,
			              point:{
			                  events:{
			                      click: function (event) {
			                          alert(this.name);
			                      }
			                  }
			              }          
			          }
			       ],
			       "chart":{
			          "renderTo":"container"
			       },
			    });
			});*/
			
			// Enter a speed between 0 and 180
			 var chart;
			   point = null;
			   $(document).ready(function () {
				   var dmname = $scope.totalData.dmName;
				var value1=$scope.totalData.level;
				var level = value1 >180?180:value1;

				// Trig to calc meter point
				var degrees = 180 - level,
					 radius = .5;
				var radians = degrees * Math.PI / 180;
				var x = radius * Math.cos(radians);
				var y = radius * Math.sin(radians);

				// Path: may have to change to create a better triangle
				var mainPath = 'M -.0 -0.025 L .0 0.025 L ',
					 pathX = String(x),
					 space = ' ',
					 pathY = String(y),
					 pathEnd = ' Z';
				var path = mainPath.concat(pathX,space,pathY,pathEnd);

				var data = [{ type: 'scatter',
				   x: [0], y:[0],
					marker: {size: 28, color:'850000'},
					showlegend: false,
					name: '',
					text: level,
					hoverinfo: 'text+name'},
				  { values: [ 12, 16, 22, 50],
				  rotation: 90,
				  text: ['Poor', 'Average', 'Good', ''],
				  textinfo: 'text',
				  textposition:'inside',	  
				  marker: {colors:['rgba(62, 162, 82, .5)','rgba(255, 139, 62, .5)','rgba(255, 0, 0, .5)',
										 'rgba(255, 255, 255, 0)']},
				  labels: $scope.totalData.range,
				  hoverinfo: 'label',
				  hole: .5,
				  type: 'pie',
				  showlegend: false
				}];

				var layout = {
				  shapes:[{
				      type: 'path',
				      path: path,
				      fillcolor: '850000',
				      line: {
				        color: '850000'
				      }
				    }],
				  title: '<b> No.of Starts for '+dmname+' : '+value1+'</b> ',
				  height: 600,
				  width: 600,
				  xaxis: {zeroline:false, showticklabels:false,
							 showgrid: false, range: [-1, 1]},
				  yaxis: {zeroline:false, showticklabels:false,
							 showgrid: false, range: [-1, 1]}
				};

				Plotly.newPlot('myDiv', data, layout);
				
				});
			
			
			
			
			
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

//				alert($scope.selectedYear + "" +$scope.selectedMonth);
				
				
					var response = $http.get('reportController/getWeeksOfMonth?year='+$scope.selectedYear+'&month='+$scope.selectedMonth);
					response.success(function (data,status,headers,config){
						$scope.weeksList = data;
//						alert(JSON.stringify($scope.weeksList));
						$timeout(function() {
//							$scope.getrecdata();
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

//				alert($scope.selectedYear + "" +$scope.selectedMonth);
				
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
//						alert(JSON.stringify($scope.weeksList));
						
						
						
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