$(function() {
	if (!Parse.User.current()) {
		window.location = '/';
		return;
	}

	$('.button-collapse').sideNav();
	$('.collapsible').collapsible();

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

	var packageObj = Parse.Object.extend("Package");
	var query = new Parse.Query(packageObj);

	query.equalTo("user", user);
	query.find({
		success: function(results) {
			removeLoadingCircleFrom($('#content-wrapper'));
			for (var i = 0; i < results.length; i++) {
				$('#package-page-content').append(
					packageItemTemplate({
						name:		results[i].get('name'),
						source:		results[i].get('source'),
						destination:	results[i].get('destination'),
						date:		results[i].get('date')
					})
				);
			}
		},
		error: function(error) {
			console.log('Error: ' + error.code + ' ' + error.message);
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
			removeLoadingCircleFrom($('#content-wrapper'));
			for (var i = 0; i < results.length; i++) {
				$('#transport-page-content').append(
					transportItemTemplate({
						source:		results[i].get('source'),
						destination:	results[i].get('destination'),
						date:		results[i].get('date'),
						availableSlots:	results[i].get('slots_available')
					})
				);
			}
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
