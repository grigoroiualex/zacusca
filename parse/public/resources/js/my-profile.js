$(function() {
	if (!Parse.User.current()) {
		window.location = '/';
		return;
	}

	$('.button-collapse').sideNav();

	_.templateSettings = {
		interpolate:	/\<\@\=(.+?)\@\>/gim,
		evaluate:	/\<\@(.+?)\@\>/gim,
		escpe:		/\<\@\-(.+?)\@\>/gim
	};

	var currentUser = Parse.User.current();
	currentUser.fetch().then(function() {
		var userInfoTemplate = _.template(
			$('#user-info-template').html()
		);

		var templateData = {
			firstName: currentUser.get("firstname"),
			lastName: currentUser.get("lastname")
		};

		$('#user-info').html(
			userInfoTemplate(templateData)
		);
	});

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

	$('#aside-want-to-transport').on('click', function() {
		renderWantToTransportForm();
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

				if (pkg.state === 'accepted') {
					Parse.Cloud.run('getCoordinatesForPackage', { packageId: pkg.objectId }, {
						success: function(coordinates) {
							if (coordinates.lat !== '' && coordinates.lng !== '') {
								$('#show-' + pkg.objectId + '-on-map').parents('.package-item-current-location').show();
							}
						},
						error: function(error) {
							console.log('Error retrieving coordinates for package');
							console.log(error);
						}
					});
				}

				$('#show-' + pkg.objectId + '-on-map').on('click', function() {
					showPackageOnMap(pkg.objectId, pkg.name);
				});

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
			});
		},
		error: function(error) {
			console.log('Error retrieving packages');
			console.log('Additional error info: ' + error.code + ' ' + error.message);
		}
	});

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

	Parse.Cloud.run('getTransportsByCurrentUser', {}, {
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
			});
		},
		error: function(error) {
			console.log('Error: ' + error.code + ' ' + error.message);
			console.log('Additional error info: ' + error.code + ' ' + error.message);
		}
	});

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

	$('#want-to-send-button').on('click', function() {
		$(this).addClass('disable');

		var name = $('#name').val();
		var date = $('#date').val();
		var source = $('#source').val();
		var destination = $('#destination').val();

		if (!allFieldsFilled([name, date, source, destination])) {
			Materialize.toast('Toate câmpurile trebuie completate!', 2000);
			$(this).toggleClass('disable');

			return;
		}

		date = new Date(date);
		date.setHours(12);

		var PkgObj = Parse.Object.extend('Package');
		var pkg = new PkgObj();

		pkg.save({
			name:		name,
			date:		date,
			source:		source,
			destination:	destination,
			user:		Parse.User.current(),
			state:		'not-joined'
		}, {
			success: function(pkg) {
				Materialize.toast('Pachet adăugat cu succes', 2000);
				$(this).toggleClass('disable');
				$('#name').val('').siblings('label, i').removeClass('active');
				$('#date').val('').siblings('label, i').removeClass('active');
				$('#source').val('').siblings('label, i').removeClass('active');
				$('#destination').val('').siblings('label, i').removeClass('active');
			},
			error: function(error) {
				Materialize.toast('Pachetul nu a putut fi adăugat', 2000);
			}
		});
	});

}

function allFieldsFilled(fieldsValues) {
	for (var i  = 0; i < fieldsValues.length; i++) {
		if (fieldsValues[i] === '') {
			return false;
		}
	}

	return true;
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

	var slider = document.getElementById('slots-available');
	noUiSlider.create(slider, {
		start:		1,
		connect:	'lower',
		step:		1,
		range: {
			'min':	1,
			'max':	10
		},
		format:		wNumb({
			decimals:	0
		})
	});

	$('#want-to-transport-button').on('click', function() {
		$(this).addClass('disable');

		var slotsAvailable = parseInt(slider.noUiSlider.get());
		var date = $('#date').val();
		var source = $('#source').val();
		var destination = $('#destination').val();

		if (!allFieldsFilled([date, source, destination])) {
			Materialize.toast('Toate câmpurile trebuie completate!', 2000);
			$(this).toggleClass('disable');

			return;
		}

		date = new Date(date);
		date.setHours(12);

		var TransObj = Parse.Object.extend('Transport');
		var trans = new TransObj();

		trans.save({
			date:			date,
			source:			source,
			destination:		destination,
			slots_available:	slotsAvailable,
			accepted_packages:	[],
			pending_packages:	[],
			user:			Parse.User.current(),
			lat:			'',
			lng:			''
		}, {
			success: function(trans) {
				Materialize.toast('Transport adăugat cu succes', 2000);
				$(this).toggleClass('disable');
				slider.noUiSlider.set(0);
				$('#date').val('').removeClass('valid').siblings('label, i').removeClass('active');
				$('#source').val('').removeClass('valid').siblings('label, i').removeClass('active');
				$('#destination').val('').removeClass('valid').siblings('label, i').removeClass('active');
			},
			error: function(error) {
				Materialize.toast('Transportul nu a putut fi adăugat', 2000);
				console.log(error);
			}
		});
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

function showPackageOnMap(packageId, packageName) {
	Parse.Cloud.run('getCoordinatesForPackage', { packageId: packageId }, {
		success: function(coordinates) {
			var position = { lat: parseFloat(coordinates.lat), lng: parseFloat(coordinates.lng) };
			var mapTemplate = _.template(
				$('#map-template').html()
			);

			$('#content-wrapper').html(
				mapTemplate()
			);

			var map = new google.maps.Map(document.getElementById('map'), {
				center: position,
				zoom: 12
			});

			var jarMarkerImg = '/resources/img/borcan-zacusca-raureni-marker.png';
			var marker = new google.maps.Marker({
				position: position,
				map: map,
				animation: google.maps.Animation.DROP,
				title: packageName,
				icon: jarMarkerImg
			});

			marker.setMap(map);

			$('#leave-map-button').on('click', function() {
				$('#refresh-content-button').trigger('click');
			});
		},
		error: function(error) {
			console.log("Map could not be loaded");
			console.log(error);
		}
	});
}

function formatMonth(month) {
	return month < 10 ? '0' + month : month;
}
