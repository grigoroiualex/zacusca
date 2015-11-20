$(function() {
	Parse.initialize("FkO54N3n6vn3gKijXwDlju0lZBaAjMXnqpvApCYF", "AF43qgGoJwDiDTELqmpc1H3QYuF2XVsNgvo81x0F");
	Parse.$ = jQuery;

	window.fbAsyncInit = function() {
		Parse.FacebookUtils.init({
			appId	: '1222086617807825',
			status	: true,
			cookie	: true,
			xfbml	: true,
			version	: 'v2.5'
		});
	};

	(function(d, s, id){
		var js, fjs = d.getElementsByTagName(s)[0];
		if (d.getElementById(id)) {return;}
		js = d.createElement(s); js.id = id;
		js.src = "//connect.facebook.net/en_US/sdk.js";
		fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));

	$("#signup-button").click(function() {
		var firstname = $("#first-name").val();
		var lastname = $("#last-name").val();
		var username = $("#email").val();
		var telephone = $("#telephone").val();
		var password = $("#password").val();

		Parse.User.signUp(username, password, {
			firstname: firstname,
			lastname: lastname,
			telephone: telephone
			},
			{
			success: function(user) {
				Materialize.toast("Cont creat cu succes", 5000);
			},
			error: function(user, error) {
				Materialize.toast("Contul nu a putut fi creat", 5000);
			}
		});
	});

	$("#login-button").click(function() {
		var username = $("#email").val();
		var password = $("password").val();

		Parse.User.logIn(username, password, {
			success: function(user) {
				Materialize.toast("Autentificare reușită", 5000);
			},
			error: function(user) {
				Materialize.toast("Autentificare eșuată", 5000);
			}
		});
	});

	$("#fb-login-button").click(function() {
		Parse.FacebookUtils.logIn("public_profile,email", {
			success: function(user) {
				if (!user.existed()) {
					Materialize.toast("Cont creat prin Facebook cu succes", 5000);
				} else {
					Materialize.toast("Logare cu Facebook reușită", 5000);
				}

				FB.api("/me", {fields: "first_name,last_name,email"}, function(response) {
					user.set("firstname", response.first_name);
					user.set("lastname", response.last_name);
					user.set("email", response.email);
				});
			},
			error: function(user, error) {
				Materialize.toast("Conectarea cu Facebook nu a reușit", 5000);
			}
		});
	});
});
