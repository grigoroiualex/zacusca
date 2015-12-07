$(function() {
	if (Parse.User.current()) {
		window.location = '/profilul-meu';
	}

	$('#signup-button').click(function() {
		var firstname = $('#first-name').val();
		var lastname = $('#last-name').val();
		var username = $('#email').val();
		var telephone = $('#telephone').val();
		var password = $('#password').val();

		Parse.User.signUp(username, password, {
			firstname: firstname,
			lastname: lastname,
			telephone: telephone
			},
			{
			success: function(user) {
				Materialize.toast('Cont creat cu succes', 5000);
			},
			error: function(user, error) {
				Materialize.toast('Contul nu a putut fi creat', 5000);
				console.log(error.message);
			}
		});
	});

	$('#login-button').click(function() {
		var username = $('#email').val();
		var password = $('password').val();

		Parse.User.logIn(username, password, {
			success: function(user) {
				Materialize.toast('Autentificare reușită', 5000);
			},
			error: function(user,error) {
				Materialize.toast('Autentificare eșuată', 5000);
				console.log(error.message);
			}
		});
	});

	$('#fb-login-button').click(function() {
		Parse.FacebookUtils.logIn('public_profile,email', {
			success: function(user) {
				if (!user.existed()) {
					Materialize.toast('Cont creat prin Facebook cu succes', 5000);

					FB.api('/me', {fields: 'first_name,last_name,email'}, function(response) {
						user.set('firstname', response.first_name);
						user.set('lastname', response.last_name);
						user.set('email', response.email);
						user.save(null, {
							success: function(user) {
								Materialize.toast('și date suplimentare salvate', 5000);
							},
							error: function(user, error) {
								Materialize.toast('dar date suplimentare nesalvate', 5000);
							}
						});
					});
				} else {
					Materialize.toast('Logare cu Facebook reușită', 5000);
				}
			},
			error: function(user, error) {
				Materialize.toast('Conectarea cu Facebook nu a reușit', 5000);
				console.log(error.message);
			}
		});
	});

	$('#has-account-button').click(function() {
		$('#signup-form').slideToggle(300, 'linear', function() {
			$('#login-form').slideToggle(300, 'linear');
		});
	});
});
