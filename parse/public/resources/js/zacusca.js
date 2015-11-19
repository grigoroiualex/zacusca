$(function() {
	Parse.$ = jQuery;

	Parse.initialize("FkO54N3n6vn3gKijXwDlju0lZBaAjMXnqpvApCYF", "AF43qgGoJwDiDTELqmpc1H3QYuF2XVsNgvo81x0F");

	$("#create-account-button").click(function() {
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
});
