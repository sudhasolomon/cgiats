;(function(angular){
	angular.module("newrecruitersreportmodule",[])
	.controller("newrecruitersreportcontroller", function($scope,$http,$state,$timeout,$mdDialog){
		var recsData = [];
		var exportrecsData = []
		$scope.recruitersReport = [];
		$scope.exportrecruitersReport = [];
		$scope.RecruitersReportTable = false;
		$scope.exportRecruitersReportTable = false;
		$scope.onload = function()
		{
			$(".underlay").show();
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
			
			
			
			
			
			
			
			
			
			
			
			
			
			$scope.getAllUsers(true,false);
		}
		

		
		$scope.checkandgetdata = function()
		{
			
			
			if($(".errormsg").is(":visible"))
				{
				$("#res").css("display", "block");
				}
			else
				{
				$("#res").css("display", "none");
				$scope.getrecdata();
				}
			
		}
		
		$scope.getrecdata = function()
		{
			$(".underlay").show();
			$scope.Year =  $("#recselectedyear").val().replace(/\s/g,"").replace(/\,$/, '');
			$scope.Month = $("#recselectedmonth").val();
			$scope.Week = $("#recselectedweek").val();
			$scope.dmname = $("#recselecteddmname").val();
			$scope.status = $("#recselectedstatus").val().replace(/\s/g,"").toUpperCase();
		
//			if($scope.dmname && ($scope.userRecords.length == $scope.dmname.trim().split(",").length)){
//				alert('');
//				$scope.dmname = null;
//			}
			
			if($scope.Year.trim().length >5 || $scope.Month.trim().length > 4){
				$scope.Week = null;
			}
			
			
			var obj ={year:$scope.Year, month:$scope.Month, week:$scope.Week,dmName:$scope.dmname, status:$scope.status};
			
//			alert(JSON.stringify(obj));
			
			$scope.RecruitersReportTable = false;
			$scope.exportRecruitersReportTable = false;
			var response = $http.post('reportController/getWeekWiseRecruiterReport',obj);
			response.success(function (data,status,headers,config){
				$scope.totalData = data.total;
				recsData = data.list;
//				alert(JSON.stringify(recsData));
				exportrecsData = data.list
				if($scope.recruitersReport&&$scope.recruitersReport.tableControl && $scope.recruitersReport.tableControl.options){
					$scope.recruitersReport.tableControl.options.data=data.list;
					}
				if($scope.exportrecruitersReport&&$scope.exportrecruitersReport.tableControl && $scope.exportrecruitersReport.tableControl.options){
					$scope.exportrecruitersReport.tableControl.options.data=data.list;
					}
				$scope.RecruitersReportTable = true;
				$scope.exportRecruitersReportTable = true;
				dispalyRecruitersData();
				exportdispalyRecruitersData();
				$scope.cll();
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
			
			
			
			
			
			
			
			
		}
		
		
		
		$scope.getAllUsers = function(isFromOnload,isYearChanged){
			$scope.cityList = [];
			var response = $http.get('reportController/getAllAssignedBDMs');
			response.success(function (data,status,headers,config){
				$scope.userRecords = data;
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
			
			
			
			
			

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
							$scope.getrecdata();
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
		
		
		
		
		
		
		
		
		
			
			
			
			
			
			
			
			
			
			
			
			
		

		
		function dispalyRecruitersData() {
			$scope.recruitersReport.tableControl= {
				options : {
					data : recsData || {},
					striped : true,
					pagination : true,
					paginationVAlign : "both",
					sidePagination : 'client',
					silentSort: false,
					pageList : [ 10, 20, 50 ],
					search : false,
					showColumns : false,
					showRefresh : false,
					showExport:false,
					showFooter : false,
					clickToSelect : false,
					showToggle : false,
					columns : [
							{
								field : 'Name',
								title : 'Recruiter Name',
								align : 'left',
								sortable : true
							},
							{
								field : 'rank',
								title : 'Rank',
								align : 'center',
								sortable : true
							},
							{
								field : 'SUBMITTED',
								title : 'SUBM',
								align : 'center',
								class : 'reportbluetext',
								sortable : true
							},
							{
								field : 'DMREJ',
								title : 'DMREJ',
								align : 'center',
								sortable : true
							},
							{
								field : 'ACCEPTED',
								title : 'ACCEPT',
								align : 'center',
								sortable : true
							},
							{
								field : 'INTERVIEWING',
								title : 'INTVIEW',
								align : 'center',
								class : 'reportbluetext',
								sortable : true
							},
							{
								field : 'CONFIRMED',
								title : 'CONF',
								align : 'center',
								sortable : true
							},
							{
								field : 'REJECTED',
								title : 'REJ',
								align : 'center',
								sortable : true
							},
							{
								field : 'STARTED',
								title : 'START',
								align : 'center',
								class : 'reportgreentext',
								sortable : true
							},
							{
								field : 'BACKOUT',
								title : 'BACKOUT',
								align : 'center',
								sortable : true
							},
							{
								field : 'OUTOFPROJ',
								title : 'OOP',
								align : 'center',
								sortable : true
							},
							{
								field : 'NotUpdated',
								title : 'NU',
								align : 'center',
								sortable : true
							},
							],

					},
			
				
				}
			$(".underlay").hide();
			};
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			function exportdispalyRecruitersData() {
				$scope.exportrecruitersReport.tableControl= {
					options : {
						data : exportrecsData || {},
						striped : true,
						pagination : false,
						paginationVAlign : "both",
						sidePagination : 'client',
						silentSort: false,
						pageList : [ 10, 20, 50 ],
						search : false,
						showColumns : false,
						showRefresh : false,
						showExport:false,
						showFooter : false,
						clickToSelect : false,
						showToggle : false,
						columns : [
								{
									field : 'Name',
									title : 'Name',
									align : 'left',
									sortable : true
								},
								{
									field : 'rank',
									title : 'Rank',
									align : 'center',
									sortable : true
								},
								{
									field : 'SUBMITTED',
									title : 'SUBMITTED',
									align : 'center',
									sortable : true
								},
								{
									field : 'DMREJ',
									title : 'DMREJ',
									align : 'center',
									sortable : true
								},
								{
									field : 'ACCEPTED',
									title : 'ACCEPTED',
									align : 'center',
									sortable : true
								},
								{
									field : 'INTERVIEWING',
									title : 'INTERVIEWING',
									align : 'center',
									sortable : true
								},
								{
									field : 'CONFIRMED',
									title : 'CONFIRMED',
									align : 'center',
									sortable : true
								},
								{
									field : 'REJECTED',
									title : 'REJECTED',
									align : 'center',
									sortable : true
								},
								{
									field : 'STARTED',
									title : 'STARTED',
									align : 'center',
									sortable : true
								},
								{
									field : 'BACKOUT',
									title : 'BACKOUT',
									align : 'center',
									sortable : true
								},
								{
									field : 'OUTOFPROJ',
									title : 'OUTOFPROJ',
									align : 'center',
									sortable : true
								},
								{
									field : 'NotUpdated',
									title : 'NOT UPDATED',
									align : 'center',
									sortable : true
								},
								],

						},
				
					
					}
				$(".underlay").hide();
				
				$timeout(function() {
					$("#exporttable .fixed-table-loading").remove();
					$("#exporttable table thead tr").css("background-color", "#3598dc");
					$("#exporttable table thead tr").css("color", "#ffffff");
					 }, 100);
				
				
				};
			
			
			
			
/*--***********************************************TOTAL FUNCTION STARTS HERE**********************************/
				$scope.cll = function()
		    	{
		    		$timeout(function(){
		    			$("#subm").text($scope.totalData.SUBMITTED);
			    		$("#dmrej").text($scope.totalData.DMREJ);
			    		$("#accept").text($scope.totalData.ACCEPTED);
			    		$("#intview").text($scope.totalData.INTERVIEWING);
			    		$("#conf").text($scope.totalData.CONFIRMED);
			    		$("#rej").text($scope.totalData.REJECTED);
			    		$("#start").text($scope.totalData.STARTED);
			    		$("#backout").text($scope.totalData.BACKOUT);
			    		$("#oop").text($scope.totalData.OUTOFPROJ);
			    		$("#nu").text($scope.totalData.NOT_UPDATED);
			    		
			    		
			    		$("#expsubm").text($scope.totalData.SUBMITTED);
			    		$("#expdmrej").text($scope.totalData.DMREJ);
			    		$("#expaccept").text($scope.totalData.ACCEPTED);
			    		$("#expintview").text($scope.totalData.INTERVIEWING);
			    		$("#expconf").text($scope.totalData.CONFIRMED);
			    		$("#exprej").text($scope.totalData.REJECTED);
			    		$("#expstart").text($scope.totalData.STARTED);
			    		$("#expbackout").text($scope.totalData.BACKOUT);
			    		$("#expoop").text($scope.totalData.OUTOFPROJ);
			    		$("#expnu").text($scope.totalData.NOT_UPDATED);
			    		
			    		
			    		}, 400);
		    	}
/*--***********************************************TOTAL FUNCTION ENDS HERE**********************************/		
		
		
		
/*--***********************************************EXPORT FUNCTION STARTS HERE**********************************/
		$scope.exportData = function () {
	        var blob = new Blob([document.getElementById('exporttable').innerHTML], {
	        	type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
	        });
	        saveAs(blob, "Report.xls");
		}
/*--***********************************************EXPORT FUNCTION ENDS HERE**********************************/

		
		
	});
	
	

	
})(angular);