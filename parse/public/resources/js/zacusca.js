$(function() {
	Parse.$ = jQuery;

	Parse.initialize("FkO54N3n6vn3gKijXwDlju0lZBaAjMXnqpvApCYF", "AF43qgGoJwDiDTELqmpc1H3QYuF2XVsNgvo81x0F");

	$("#signup-button").click(function() {
		var firstname = $("#first-name").val();
		var lastname = $("#last-name").val();
		var username = $("#email").val();
		var telephone = $("#telephone").val();
		var password = $("#password").val();

		Parse.User.signUp(username, password, {
			ACL: new Parse.ACL(),
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
});
