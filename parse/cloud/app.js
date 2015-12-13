
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
				date:		pkg.get('date')
			});
		});

		return packages;
	})
	.then(function(packages) {
		response.success(packages);
	});
});

// Sets user info for given transport and returns the transport
// as a resolved promise
function setUserInfoForTransport(user, transport) {
	var promise = new Parse.Promise();
	var userQuery = new Parse.Query(Parse.User);

	userQuery.get(user.id, {
		success: function(user) {
			transport.userFullname = user.get('firstname') + ' ' + user.get('lastname');
			transport.userEmail = user.get('email');
			transport.userTelephone = user.get('telephone');

			promise.resolve(transport);
		},
		error: function(error) {
			promise.reject(error);
		}
	});

	return promise;
}

Parse.Cloud.define('getAvailableTransportsForPackage', function(request, response) {
	var packageQuery = new Parse.Query('Package');

	packageQuery.equalTo('objectId', request.params.pkg.objectId);
	packageQuery.first()
	.then(function(pkg) {
		var results = [];
		var pkgDate = new moment(pkg.get('date'));
		var transportQuery = new Parse.Query('Transport');

		transportQuery.equalTo('source', pkg.get('source'));
		transportQuery.equalTo('destination', pkg.get('destination'));
		transportQuery.greaterThan('date', pkgDate.startOf('day').toDate());
		transportQuery.lessThan('date', pkgDate.endOf('day').toDate());

		return transportQuery.find()
		.then(function(transports) {
			var trans;
			var promises = [];

			_.each(transports, function(transport) {
				trans = {
					objectId:	transport.id,
					source:		transport.get('source'),
					destination:	transport.get('destination'),
					userFullname:	'',
					userEmail:	'',
					userTelephone:	''
				};

				promises.push(
					setUserInfoForTransport(transport.get('user'), trans)
					.then(function(updatedTransport) {
						results.push(updatedTransport);
					})
				);
			});

			return Parse.Promise.when(promises);
		})
		.then(function() {
			response.success(results);
		});
	}, function(error) {
		response.error('There was an error querying');
	});
});

// Attach the Express app to Cloud Code.
app.listen();
