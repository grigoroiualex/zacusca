$(function() {
	if (!Parse.User.current()) {
		window.location = '/';
		return;
	}

	$('.button-collapse').sideNav();

	_.templateSettings = {
		interpolate:	/\<\@\=(.+?)\@\>/gim,
		evaluate:	/\<\@(.+?)\@\>/gim,
		escpe:		/\<\@\-(.+?)\@\>/gim,
		variable:	"rc"
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
			var promises = [];

			_.each(pkgs, function(pkg) {
				$('#package-page-content').append(
					packageItemTemplate(pkg)
				);

				promises.push(
					Parse.Cloud.run('getAvailableTransportsForPackage', { pkg: pkg }, {
						success: function(transports) {
							_.each(transports, function(transport) {
								$('#available-transports-for-' + pkg.objectId).append(
									packageItemAvailableTransportTemplate({
										packageId:	pkg.objectId,
										transportId:	transport.objectId,
										username:	transport.userFullname,
										email:		transport.userEmail,
										source:		transport.source,
										destination:	transport.destination,
										telephone:	transport.userTelephone
									})
								);

							});

						},
						error: function(error) {
							console.log('Error retrieving available transports for package ' + pkg);
							console.log('Additional error info: ' + error.code + ' ' + error.message);
						}
					})
				);
			});

			Parse.Promise.when(promises).then(function() {
				$('.collapsible').collapsible();
				$('.join.btn').on('click', function() {
					console.log($(this).data('transport') + $(this).data('package'));
				});
			});
		},
		error: function(error) {
			console.log('Error retrieving packages');
			console.log('Additional error info: ' + error.code + ' ' + error.message);
		}
	});
}

function renderMyTransports(user) {
	var transportPageContentTemplate = _.template(
		$('#transport-page-content-template').html()
	);

	$('#content-wrapper').html(
		transportPageContentTemplate()
	);

	var transportItemTemplate = _.template(
		$('#transport-item-template').html()
	);

	var transportItempackagesOnBoardItemTemplate = _.template(
		$('#package-on-board-template').html()
	);

	Parse.Cloud.run('getTransports', {}, {
		success: function(transports) {
			var promises = [];

			_.each(transports, function(transport) {
				$('#transport-page-content').append(
					transportItemTemplate(transport)
				);

				promises.push(
					Parse.Cloud.run('getPackagesOnBoardForTransport', { transport: transport }, {
						success: function(pkgs) {
							_.each(pkgs, function(pkg) {
								$('#packages-on-board-for-' + transport.objectId).append(
									transportItempackagesOnBoardItemTemplate({
										transportId:	transport.objectId,
										packageId:	pkg.objectId,
										username:	pkg.userFullname,
										email:		pkg.userEmail,
										source:		pkg.source,
										destination:	pkg.destination,
										telephone:	pkg.userTelephone
									})
								);

							});

						},
						error: function(error) {
							console.log('Error retrieving available transports for package ' + transport);
							console.log('Additional error info: ' + error.code + ' ' + error.message);
						}
					})
				);
			});

			Parse.Promise.when(promises).then(function() {
				$('.collapsible').collapsible();
				$('.accept.btn').on('click', function() {
					console.log($(this).data('transport') + $(this).data('package'));
				});
			});
		},
		error: function(error) {
			console.log('Error: ' + error.code + ' ' + error.message);
			console.log('Additional error info: ' + error.code + ' ' + error.message);
		}
	});
}

function makeAsideSelection(asideOption) {
	$($('.aside-active')[0]).toggleClass('aside-active');
	asideOption.parent().toggleClass('aside-active');
}
