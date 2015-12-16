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

	$('#aside-want-to-send').on('click', function() {
		renderWantToSendForm();
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

			pkgs = processInput(pkgs);

			_.each(pkgs, function(pkg) {
				$('#package-page-content').append(
					packageItemTemplate(pkg)
				);

				promises.push(
					Parse.Cloud.run('getAvailableTransportsForPackage', { pkg: pkg }, {
						success: function(transports) {
							transports = processInput(transports);

							_.each(transports, function(transport) {
								$('#available-transports-for-' + pkg.objectId).append(
									packageItemAvailableTransportTemplate({
										packageId:	pkg.objectId,
										transportId:	transport.objectId,
										username:	transport.userFullname,
										email:		transport.userEmail,
										source:		transport.source,
										destination:	transport.destination,
										telephone:	transport.userTelephone,
										state:		pkg.state
									})
								);
							});
						},
						error: function(error) {
							console.log('Error retrieving available transports for package ');
							console.log('Additional error info: ' + error.code + ' ' + error.message);
						}
					})
				);
			});

			Parse.Promise.when(promises).then(function() {
				$('.collapsible').collapsible();
				$('.join.btn').on('click', function() {
					$(this).addClass('disabled');
					Parse.Cloud.run('requestJoin', {
						pkgId:		$(this).data('package'),
						transportId:	$(this).data('transport')
					}, {
						success: function(result) {
							Materialize.toast('Cerere trimisă', 2000);
						},
						error: function(error) {
							console.log('Could not request join');
						}
					});
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

			transports = processInput(transports);

			_.each(transports, function(transport) {
				$('#transport-page-content').append(
					transportItemTemplate(transport)
				);

				promises.push(
					Parse.Cloud.run('getPackagesOnBoardForTransport', { transport: transport }, {
						success: function(pkgs) {
							pkgs = processInput(pkgs);

							_.each(pkgs, function(pkg) {
								$('#packages-on-board-for-' + transport.objectId).append(
									transportItempackagesOnBoardItemTemplate({
										transportId:	transport.objectId,
										packageId:	pkg.objectId,
										username:	pkg.userFullname,
										email:		pkg.userEmail,
										source:		pkg.source,
										destination:	pkg.destination,
										telephone:	pkg.userTelephone,
										state:		pkg.state
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
					$(this).addClass('disabled');
					Parse.Cloud.run('acceptJoin', {
						pkgId:		$(this).data('package'),
						transportId:	$(this).data('transport')
					}, {
						success: function(result) {
							Materialize.toast('Pachet acceptat', 2000);
						},
						error: function(error) {
							console.log('Could not accept join');
						}
					}).then(function() {
						$(this).html(
							'<i class="material-icons">done</i>'
						);
					});
				});
			});
		},
		error: function(error) {
			console.log('Error: ' + error.code + ' ' + error.message);
			console.log('Additional error info: ' + error.code + ' ' + error.message);
		}
	});
}

function renderWantToSendForm() {
	var wantToSendTemplate = _.template(
		$('#want-to-send-page-content-template').html()
	);

	$('#content-wrapper').html(
		wantToSendTemplate()
	);

	$('.datepicker').pickadate({
		selectMonths:	true,
		selectYears:	2
	});
}

function renderWantToTransportForm() {
	var wantToTransportTemplate = _.template(
		$('#want-to-transport-page-content-template').html()
	);

	$('#content-wrapper').html(
		wantToTransportTemplate()
	);

	$('.datepicker').pickadate({
		selectMonths:	true,
		selectYears:	2
	});
}

function makeAsideSelection(asideOption) {
	$($('.aside-active')[0]).toggleClass('aside-active');
	asideOption.parent().toggleClass('aside-active');
}

function processInput(items) {
	var ks = JSON.parse(items);

	_.each(ks, function(k) {
		if (k.date !== undefined) {
			k.date = new Date(k.date);
		}
	});

	return ks;
}
