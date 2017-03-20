
(function(angular) {

	'use strict';
angular
.module('mailModule',['ui.bootstrap'])
.service('mailService',['$http', function ($http){
	
	this.sendMail = function(mail, flag){
		//alert("service called");
		var mails = mail.split(";");
		var maillength = (mails.length)-1;
		if(maillength<=90){
		$(function() {
			$.fn.myMailer = function() {
				this.display = function() {
					var url = 'mailto:'
							+ this.To
							+ '?subject='
							+ encodeURIComponent(this.Subject)
							+ '&body='
							+ encodeURIComponent(this.HTMLBody)
							+ '&cc='
							+ encodeURIComponent(this.cc)
							+ '&bcc='
							+ encodeURIComponent(this.bcc);

					window.location = url;
					return true;
				}
			}
		});
		var myMailItem = new $.fn.myMailer();

		myMailItem.Subject = '[CGI-ATS]';
		myMailItem.HTMLBody = 'Whatever you want your E-Mail to say';
		myMailItem.cc = 'cgiats@charterglobal.com';
		if (flag == true) {
			myMailItem.To = 'cgiats@charterglobal.com';
			myMailItem.bcc = mail;
		} else {
			myMailItem.To = mail;
			myMailItem.bcc = '';
		}
		// myMailItem.bcc = mail;
		myMailItem.display(0);
	
	}
	else{
		$.growl.error({title : "Limit Exceeded",
			message : "maximum no.of candidates for mailing is 90"
		});
	}
	}
}]);
})(angular);