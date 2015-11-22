$(function() {
	Parse.initialize('FkO54N3n6vn3gKijXwDlju0lZBaAjMXnqpvApCYF', 'AF43qgGoJwDiDTELqmpc1H3QYuF2XVsNgvo81x0F');
	Parse.$ = jQuery;

	window.fbAsyncInit = function() {
		Parse.FacebookUtils.init({
			appId	: '1222086617807825',
			cookie	: true,
			xfbml	: true,
			version	: 'v2.5'
		});
	};

	(function(d, s, id){
		var js, fjs = d.getElementsByTagName(s)[0];
		if (d.getElementById(id)) {return;}
		js = d.createElement(s); js.id = id;
		js.src = '//connect.facebook.net/en_US/sdk.js';
		fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));
});

