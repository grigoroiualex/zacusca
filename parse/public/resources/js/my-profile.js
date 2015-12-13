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

	Parse.Cloud.run('getPackages', {}, {
		success: function(pkgs) {
			_.each(pkgs, function(pkg) {
				$('#package-page-content').append(
					packageItemTemplate(pkg)
				);

				Parse.Cloud.run('getAvailableTransportsForPackage', { pkg: pkg }, {
					success: function(transports) {
						_.each(transports, function(transport) {
							$('#available-transports-for-' + pkg.objectId).append(
								packageItemAvailableTransportTemplate({
									username:	transport.userFullname,
									email:		transport.userEmail,
									source:		transport.source,
									destination:	transport.destination,
									telephone:	transport.userTelephone
								})
							);
						});

						$('.collapsible').collapsible();
					},
					error: function(error) {
						console.log('Error retrieving available transports for package ' + pkg);
					}
				});
			});
		},
		error: function(error) {
			console.log('Error retrieving packages');
		}
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
		},
		error: function(error) {
			console.log('Error: ' + error.code + ' ' + error.message);
		}
	});
}

function makeAsideSelection(asideOption) {
	$($('.aside-active')[0]).toggleClass('aside-active');
	asideOption.parent().toggleClass('aside-active');
}
