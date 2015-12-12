$(function() {
	if (!Parse.User.current()) {
		window.location = '/';
		return;
	}

	$('.button-collapse').sideNav();

	_.templateSettings = {
		interpolate : /\{\{(.+?)\}\}/g,
		variable: "rc"
	};

	var currentUser = Parse.User.current();

	var userInfoTemplate = _.template(
		$('#user-info-template').html()
	);

	var templateData = {
		firstName : currentUser.get("firstname"),
		lastName : currentUser.get("lastname")
	};

	$('#user-info').html(
		userInfoTemplate(templateData)
	);

	$('#aside-my-packages').on('click', function() {
		renderMyPackages(currentUser);
		makeAsideSelection($(this));
	});

	$('#aside-my-packages').trigger('click');

	$('#aside-my-transports').on('click', function() {
		renderMyTransports(currentUser);
		makeAsideSelection($(this));
	});

	$('#logout-button').on('click', function() {
		Parse.User.logOut();
		window.location = '/';
	});

	$('#refresh-content-button').on('click', function() {
		$($('.aside-active > span')[0]).trigger('click');
	});
});

function renderMyPackages(user) {
	console.log(user);
	addLoadingCircleTo($('#content-wrapper'));
	var packagePageContentTemplate = _.template(
		$('#package-page-content-template').html()
	);

	$('#content-wrapper').html(
		packagePageContentTemplate()
	);

	var packageItemTemplate = _.template(
		$('#package-item-template').html()
	);

	var packageItemAvailableTransportTemplate = _.template(
		$('#package-item-available-transport-template').html()
	);

	var packageObj = Parse.Object.extend('Package');
	var query = new Parse.Query(packageObj);
	var test = [];

	query.equalTo('user', user);
	console.log("ceva");
	console.log(query);
	query.find().then(function(results) {

		var promise = Parse.Promise.as();
		_.each(results, function(result) {
			$('#package-page-content').append(
				packageItemTemplate({
					name:		result.get('name'),
					source:		result.get('source'),
					destination:	result.get('destination'),
					date:		result.get('date')
				})
			);

			promise = promise.then(function() {
				var transportObj = Parse.Object.extend('Transport');
				var q = new Parse.Query(transportObj);

				q.equalTo('source', result.get('source'));
				q.equalTo('destination', result.get('destination'));

				q.find().then(function(results) {
					_.each(results, function(result) {
						var q1 = new Parse.Query(Parse.User);
						var username, email;

						console.log((result.get('user')).id);
						query.get((result.get('user')).id, {
							success: function(transportUser) {
								username = user.get('firstname') + ' ' + user.get('lastname');
								email = user.getEmail();
							},
							error: function(error) {
								console.log('Error: ' + error.code + ' ' + error.message);
							}
						});

						var bro = $('.available-transports');
						$(bro[bro.length - 1]).append(
							packageItemAvailableTransportTemplate({
								username:	username,
								email:		email,
								source:		result.get('source'),
								destination:	result.get('destination')
							})
						);
						test.push(result.get('source'));
						$('.collapsible').collapsible();
					});
				});
				removeLoadingCircleFrom($('#content-wrapper'));
			});
		});

		console.log(test);
		return promise;
	}, function(error) {
		console.log('Error: ' + error.code + ' ' + error.message);
	});
	
}

function renderMyTransports(user) {
	addLoadingCircleTo($('#content-wrapper'));
	var transportPageContentTemplate = _.template(
		$('#transport-page-content-template').html()
	);

	$('#content-wrapper').html(
		transportPageContentTemplate()
	);

	var transportItemTemplate = _.template(
		$('#transport-item-template').html()
	);

	var transportObj = Parse.Object.extend("Transport");
	var query = new Parse.Query(transportObj);

	query.equalTo("user", user);
	query.find({
		success: function(results) {
			_.each(results, function(result) {
				$('#transport-page-content').append(
					transportItemTemplate({
						source:		result.get('source'),
						destination:	result.get('destination'),
						date:		result.get('date'),
						availableSlots:	result.get('slots_available')
					})
				);
			});

			removeLoadingCircleFrom($('#content-wrapper'));
		},
		error: function(error) {
			console.log('Error: ' + error.code + ' ' + error.message);
		}
	});
}

// Adds loading circle to element given as jQuery object
function addLoadingCircleTo(element) {
	$('#loading-circle-template').clone().attr('id', 'loading-circle').appendTo(element);
}

function removeLoadingCircleFrom() {
	$('#loading-circle').remove();
}

function makeAsideSelection(asideOption) {
	$($('.aside-active')[0]).toggleClass('aside-active');
	asideOption.parent().toggleClass('aside-active');
}
