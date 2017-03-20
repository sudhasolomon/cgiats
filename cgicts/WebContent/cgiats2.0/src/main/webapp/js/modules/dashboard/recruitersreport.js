;(function(angular){
	angular.module("recruitersreportmodule",[])
	.controller("DBrecruitersreportcontroller", function($scope,$http,$state,$timeout,$mdDialog){
		
		$scope.onload = function()
		{
			$(".underlay").show();
			$scope.pagename = "NEW REPORT";
			
			$timeout(function() {
				$scope.statename = $("#dmcity").val();
				 }, 610);
			
			$scope.userStatus = constants.ACTIVE;
			
$scope.created = { endDate: moment(), startDate:moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1})};
    		
			$scope.dates4 = { startDate: moment().subtract(1, 'day'), endDate: moment().subtract(1, 'day') };
			$scope.ranges = {
					/*'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],*/
					'All Time'  : [moment({'year' :2012, 'month' :5, 'day' :1}), moment()],
					'Today': [moment(),moment()],
			        'This Month': [moment({'year' :(new Date()).getFullYear(), 'month' :((new Date()).getMonth()), 'day' :1}),moment()],
			        /*'Last 1 month': [moment().subtract(1, 'month'), moment()],*/
			        'Last 3 months': [moment().subtract(3, 'month'), moment()],
			        'Last 6 months': [moment().subtract(6, 'month'), moment()], 
			        'Last 1 year': [moment().subtract(12, 'month'), moment()]
				
			};		

			
			$scope.getAllUsers();
			

			
			
		}
		
		$scope.getAllUsers = function(){
			$scope.cityList = [];
			var response = $http.get('commonController/getAllDMAndADM_OfficeLocations?isAuthRequired='+true+'&isActive='+true);
			response.success(function (data,status,headers,config){
				if(data){
				$scope.userRecords = data.users;
//				$scope.cityList = data.cities;
				
				var categories = [];

				$.each(data.cities, function(index, value) {
//				    if ($.inArray(value.officeLocation, categories) === -1) {
//				        categories.push(value.officeLocation);
					var obj={officeLocation:value};
				        $scope.cityList.push(obj);
//				    }
				});
				
				$timeout(function() {
					$scope.searchrecruitersreport(false,true);
					 }, 200);
				$timeout(function() {
					$scope.timeOutFunction();
					 }, 100);
				}
			});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
		};
		
		$scope.timeOutFunction = function(){
				
				$("#selct").show();

			var res = "";
			var maindiv = $(".multi01");

			maindiv.each(function() {

				var txtbx = $(this).children(".ddbox01")
				var checkbx = $(this).children(".ddlist01")
						.children("li").children("input:checked");
				checkbx.each(function() {

					var data = $(this).val() + ", ";
					var preres = res + data;
					res = preres;

				});

				res = res.substring(0, res.length - 2);
				txtbx.val(res);
				res = "";

			});
			
			
			
			
			
			
			$('.ddlist01 li input[type="checkbox"]')
			.on(
					'click',
					function() {
						var title = $(this)
								.closest('.ddlist01')
								.find(
										'input[type="checkbox"]:checked')
								.val(), title = $(this).val()
								+ " ,";
						var maininput = $(this).parent()
								.parent().siblings(".ddbox01");
						
						var usrcty = $(this).val();
						
						if ($(this).is(':checked')) {
							
							
							var inputfield = maininput.val();
							if (inputfield !== '') {
								inputfield += ", "
										+ $(this).val();

							} else {
								inputfield = $(this).val();

							}

							maininput.val(inputfield);
							
							
							
							
							

						} else {
							
							var inputfield = maininput.val();
							var removeval = $(this).val() + ", ";
							var findsome = inputfield
									.indexOf(removeval);

							if (findsome > -1) {
								inputfield = inputfield
										.replace(removeval, '');
							} else {

								var removeval = ", "
										+ $(this).val();
								var findsome = inputfield
										.indexOf(removeval);

								if (findsome > -1) {
									inputfield = inputfield
											.replace(removeval,
													'');
								} else {
									var removeval = $(this)
											.val();
									inputfield = inputfield
											.replace(removeval,
													'');
								}

							}

							maininput.val(inputfield);
							
							

						}
						
						

						
					});
			
			
			
			
			
			
			$(".ddlist01 li input").click(
					function() {
						var checktxtbox = $(this).parent().parent()
								.siblings(".ddbox01");
						if (checktxtbox.val() == "") {
							$(this).parent().parent().siblings(
									".blankmsg").show();
						} else {
							$(this).parent().parent().siblings(
									".blankmsg").hide();
						}

					});
		}
		
		var obj = null;
		$scope.searchrecruitersreport = function(isFromView,isFromOnload)
    	{
    		$scope.viewstartdate = null;
    		$scope.viewEndDate = null;
    		if($('#dmSubmittalRangesId').val() && $('#dmSubmittalRangesId').val()!=''){
				var startDate = $('#dmSubmittalRangesId').val().split(' ')[0];
				var endDate = $('#dmSubmittalRangesId').val().split(' ')[2];
				obj = {startDate:startDate, endDate:endDate};
				obj.userStatus = $scope.userStatus;
				$scope.viewstartdate = obj.startDate; 
				$scope.viewEndDate = obj.endDate; 
			}else{
				obj = {startDate:$scope.created.startDate, endDate:$scope.created.endDate};
				var startDate = obj.startDate.toDate();
				var endDate = obj.endDate.toDate();
	    		$scope.viewstartdate = startDate.getFullYear()+'-'+((startDate.getMonth()+1)<10?"0"+(startDate.getMonth()+1):(startDate.getMonth()+1))+'-'+(startDate.getDate()<10?"0"+startDate.getDate():startDate.getDate());
	    		$scope.viewEndDate = endDate.getFullYear()+'-'+((endDate.getMonth()+1)<10?"0"+(endDate.getMonth()+1):(endDate.getMonth()+1))+'-'+(endDate.getDate()<10?"0"+endDate.getDate():endDate.getDate());
	    		obj = {startDate:$scope.viewstartdate, endDate:$scope.viewEndDate};
			}
    		if(!isFromOnload){
			obj.strOfficeLocations = $('#dmcity').val();
//    		}else{
//    			obj.strOfficeLocations = null;
//    		}
			obj.strDMOrAdms = $('#dmname').val();
    		}else{
    			obj.strOfficeLocations = null;
    			obj.strDMOrAdms = null;
    		}
    		$scope.getSubmittalResult(isFromView);
    		/*alert(JSON.stringify(obj));*/
    	
    	
			
			
			

			
			
			
    	
    	
    	}
		$scope.searchByDMNames = function()
    	{
			obj.strDMOrAdms = $('#dmname').val();
			$scope.getSubmittalResult(false);
    	}
		
		$scope.getSubmittalResult = function(fromLocation){
			$(".underlay").show();
			obj.isAuthRequired = true;
			var response = $http.post("submittalStatsController/getSubmittalStatsReport",obj);
    		response.success(function(data, status,headers, config) 
					{
    			$(".underlay").hide();
    		/*	alert(JSON.stringify(data));*/
    			$scope.records = data;
    			if(data){
    			/*	$scope.userRecords = [];
    				for(var i=0;i<data.length;i++){
    					for(var j=0;j<data[i].records.length;j++){
    						$scope.userRecords.push(data[i].records[j]);
    					}
    				}*/
//    				if(!fromLocation){
//    				$scope.userRecords = data[0].bdm;
//    				}
    				
    				/*alert(JSON.stringify($scope.userRecords));*/
    				for(var i=0;i<data.length;i++){
//    					for(var j=0;j<data[i].records.length;j++){
    						data[i].pageNo = 0;
    						data[i].noOfRecords = data[i].records.length;
    						data[i].displayRecords = [];
    						data[i].pages = [];
    						$scope.getCurrentPageRecords(data[i].pageNo,data[i].records,data[i].displayRecords,data[i].noOfRecords);
    						data[i].lblCurrentPageRecords = $scope.calculatePageNavigationValues(data[i].noOfRecords,data[i].pages);
//    					}
    				}
    			}
    		
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
			
			
			
			
			
			
			
			
			
			
			
			
		$scope.getUserSubmittalsById = function(userId,recordStatus){

    		obj.userId = userId;
    		obj.isDm = false;
    		obj.status = recordStatus;
    		var response = $http.post("submittalStatsController/getUserSubmittalsById",obj);
    		response.success(function(data, status,headers, config) 
					{
//    			alert(JSON.stringify(data));
//    			$scope.totalcount = data.totalRecordsCount;
//    			$scope.records = data.records;
    			$mdDialog
				.show(
						{
							controller : DialogController,
							templateUrl : 'views/dialogbox/viewSubmittalInfoDialogbox.html',
							parent : angular
									.element(document.body),
							locals : {
								rowData : data,
								recordStatus : recordStatus
							},
							clickOutsideToClose : true,
						});
    			
					});
			response.error(function(data, status, headers, config){
  			  if(status == constants.FORBIDDEN){
  				location.href = 'login.html';
  			  }else{  			  
  				$state.transitionTo("ErrorPage",{statusvalue  : status});
  			  }
  		  });
    	
			
		}
		
		function DialogController($scope,
				$mdDialog, rowData,recordStatus) {
			$scope.records = rowData;
			$scope.recordStatus = recordStatus;
			$scope.hide = function() {
				$mdDialog.hide();
			};

			$scope.cancel = function() {
				$mdDialog.cancel();
			};
		}
			
			
			
			
			
		
		$scope.getSubmittalList = function(userId){
			window.location="#/submitals/submittal_list?startDate="+obj.startDate+"&endDate="+obj.endDate+"&userId="+userId;
		}
		
		
		
		
		
		$scope.exportData = function () {
	        var blob = new Blob([document.getElementById('exporttable').innerHTML], {
	            /*type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"*/
	        	type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
	        		/*type: "text/csv;charset=utf-8;"*/
	        });
	        saveAs(blob, "Report.xls");
	        
	        
	        
		}
		
		$scope.onChangeDateValue = function(){
			$scope.searchrecruitersreport(false,true);
			$("#dmcity").val($scope.statename);
			$("#dmcity").siblings(".blankmsg").hide();
			var inpt = $("#dmcity").siblings(".ddlist").children("li").children("input[type='checkbox']");
			inpt.each(function(){
				$(this).prop("checked", "true");
			});
			
			
			
			$("#dmname").val("");
			$("#dmname").siblings(".blankmsg").show();
			var inpt01 = $("#dmname").siblings(".ddlist01").children("li").children("input[type='checkbox']");
			inpt01.each(function(){
				$(this).removeAttr("checked");
			});
			
			
			
			
			
			
			
			
			
			
			





		}
		
		
		/* *********************************************PAGINATION STARTS HERE-------------------------------------------------- */
		var recordsPerPage=10;
		
		$scope.nextPageRecords = function(record,recordOffset){
			record.lblCurrentPageRecords = "Showing "+(recordOffset+1)+" to "+(((recordOffset+recordsPerPage)<record.noOfRecords)?(recordOffset+recordsPerPage):(record.noOfRecords))+" of "+record.noOfRecords;
			record.pageNo = recordOffset;
	  	  $scope.getCurrentPageRecords(recordOffset,record.records,record.displayRecords,record.noOfRecords);
	    };
	    
	    //when we click on search button
	    $scope.getCurrentPageRecords = function(pageNo,records,displayRecords,noOfRecords){
	    	displayRecords.splice(0,displayRecords.length)
	    var iteratationCount = ((pageNo)+recordsPerPage)> noOfRecords? noOfRecords:((pageNo)+recordsPerPage);
	    	for(var i=pageNo;i<iteratationCount;i++){
	    		displayRecords.push(records[i]);
	    	}
	    };
		
		 $scope.calculatePageNavigationValues = function(noOfRecords,pages){
			var lblCurrentPageRecords = "Showing 1 to "+(((recordsPerPage)<noOfRecords)?(recordsPerPage):(noOfRecords))+" of "+noOfRecords;
//			 $scope.pages = [{}];
//			 $scope.pages.splice(0,1);
			 if(noOfRecords > recordsPerPage){
				var j=1;
				
				for(var i=1;i<=noOfRecords;){
					var obj = {};
					obj.number = j++;
						obj.recordOffset = i-1;
					pages.push(obj);
					i += recordsPerPage;
				}
			 }
			 return lblCurrentPageRecords;
		    };
		    /* *********************************************PAGINATION ENDS HERE-------------------------------------------------- */		    
		    
		
	});
	
	
	
	
	
})(angular);