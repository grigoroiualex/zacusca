
// These two lines are required to initialize Express in Cloud Code.
var express = require('express');
var _ = require('underscore');
var app = express();
var moment = require('moment');

// Global app configuration section
app.set('views', 'cloud/views');  // Specify the folder to find templates
app.set('view engine', 'ejs');    // Set the template engine
app.use(express.bodyParser());    // Middleware for reading request body

var titlePrefix = 'Zacusca';

app.locals.extraJSs = [];
app.locals._ = _;

// This is an example of hooking up a request handler with a specific request
// path and HTTP verb using the Express routing API.
app.get('/profilul-meu', function(req, res) {
	app.locals.extraJSs.push('my-profile.js');

	res.render('my-profile', {
		title: titlePrefix + ' - Profilul meu',
	});
});

app.get('/', function(req, res) {
	app.locals.extraJSs.push('login.js');
	res.render('login', {
		title: titlePrefix
	});
});


// Cloud code functions

// Gets the packages that a user wants to send
Parse.Cloud.define('getPackages', function(request, response) {
	var pkgsQuery = new Parse.Query('Package');
	pkgsQuery.equalTo('user', request.user);

	pkgsQuery.find()
	.then(function(pkgs) {
		var packages = [];

		_.each(pkgs, function(pkg) {
			packages.push({
				objectId:	pkg.id,
				name: 		pkg.get('name'),
				source:		pkg.get('source'),
				destination:	pkg.get('destination'),
				state:		pkg.get('state'),
				date:		pkg.get('date')
			});
		});

		return packages;
	}, function(error) {
		resolve.error('Could not query packages')
	})
	.then(function(packages) {
		response.success(packages);
	});
});

// Queries for the transports that the user scheduled
Parse.Cloud.define('getTransports', function(request, response) {
	var transQuery = new Parse.Query('Transport');
	transQuery.equalTo('user', request.user);

	transQuery.find()
	.then(function(trans) {
		var transports = [];

		_.each(trans, function(tran) {
			transports.push({
				objectId:	tran.id,
				source:		tran.get('source'),
				destination:	tran.get('destination'),
				date:		tran.get('date'),
				slotsAvailable:	tran.get('slots_available')
			});
		});

		return transports;
	}, function(error) {
		resolve.error('Could not query transports')
	})
	.then(function(transports) {
		response.success(transports);
	});
});

// Sets user info for given transport and returns the transport
// as a resolved promise
function setUserInfoForItem(user, item) {
	var promise = new Parse.Promise();
	var userQuery = new Parse.Query(Parse.User);

	userQuery.get(user.id, {
		success: function(user) {
			item.userFullname = user.get('firstname') + ' ' + user.get('lastname');
			item.userEmail = user.get('email');
			item.userTelephone = user.get('telephone');

			promise.resolve(item);
		},
		error: function(error) {
			promise.reject(error);
		}
	});

	return promise;
}

// Gets the transports that are scheduled in the same day as the package and
// have the same source and the same destination.
// For each transport, also gets the information about its user
Parse.Cloud.define('getAvailableTransportsForPackage', function(request, response) {
	var packageQuery = new Parse.Query('Package');

	packageQuery.equalTo('objectId', request.params.pkg.objectId);
	packageQuery.first()
	.then(function(pkg) {
		var results = [];
		var pkgDate = new moment(pkg.get('date'));
		var transportQuery = new Parse.Query('Transport');

		transportQuery.notEqualTo('user', pkg.get('user'));
		transportQuery.equalTo('source', pkg.get('source'));
		transportQuery.equalTo('destination', pkg.get('destination'));
		transportQuery.greaterThan('date', pkgDate.startOf('day').toDate());
		transportQuery.lessThan('date', pkgDate.endOf('day').toDate());

		return transportQuery.find()
		.then(function(transports) {
			var promises = [];

			_.each(transports, function(transport) {
				var trans;

				trans = {
					objectId:	transport.id,
					source:		transport.get('source'),
					destination:	transport.get('destination'),
					userFullname:	'',
					userEmail:	'',
					userTelephone:	''
				};

				promises.push(
					setUserInfoForItem(transport.get('user'), trans)
					.then(function(updatedTransport) {
						results.push(updatedTransport);
					}, function(error) {
						response.error('Could not set user info for available transport');
					})
				);
			});

			return Parse.Promise.when(promises);
		}, function(error) {
			response.error('Could not query available transports');
		})
		.then(function() {
			response.success(results);
		});
	}, function(error) {
		response.error('There was an error querying for available transports');
	});
});

// Gets the list of packages that are pending or accepted for the transport
// given as parameter
Parse.Cloud.define('getPackagesOnBoardForTransport', function(request, response) {
	var transportQuery = new Parse.Query('Transport');
	var results = [];

	transportQuery.equalTo('objectId', request.params.transport.objectId);
	transportQuery.first()
	.then(function(transport) {
		var promises = [];
		var packages = _.union(transport.get('accepted_packages'), transport.get('pending_packages'));

		_.each(packages, function(pkgObjectId) {
			var pkgQuery = new Parse.Query('Package');
			pkgQuery.equalTo('objectId', pkgObjectId);

			promises.push(pkgQuery.first()
				.then(function(pkg) {
					var p = {
						objectId:	pkg.id,
						name: 		pkg.get('name'),
						source:		pkg.get('source'),
						destination:	pkg.get('destination'),
						date:		pkg.get('date'),
						state:		pkg.get('state'),
						userFullname:	'',
						userEmail:	'',
						userTelephone:	''
					}

					return setUserInfoForItem(pkg.get('user'), p)
						.then(function(updatedPkg) {
							results.push(updatedPkg);
						});
				}, function(error) {
					response.error('Could not query or update package for transport');
				})
			);
		});

		return Parse.Promise.when(promises);
	}, function(error) {
		response.error('Could not query transport');
	})
	.then(function() {
		response.success(results);
	});
});

// Attach the Express app to Cloud Code.
app.listen();
