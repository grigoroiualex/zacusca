
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
	var results = [];

	var pkgsQuery = new Parse.Query('Package');
	pkgsQuery.equalTo('user', request.user);

	pkgsQuery.find()
	.then(function(pkgs) {
		var packages = [];

		_.each(pkgs, function(pkg) {
			packages.push({
				name: 		pkg.get('name'),
				source:		pkg.get('source'),
				destination:	pkg.get('destination'),
				date:		pkg.get('date'),
				transports:	[]
			});
		});

		return packages;
	})
	.then(function(pkgs) {
		var promise = Parse.Promise.as();
		_.each(pkgs, function(pkg) {
			var pkgDate = new moment(pkg.date);
			var transportQuery = new Parse.Query('Transport');

			transportQuery.equalTo('source', pkg.source);
			transportQuery.equalTo('destination', pkg.destination);
			transportQuery.greaterThan('date', pkgDate.startOf('day').toDate());
			transportQuery.lessThan('date', pkgDate.endOf('day').toDate());

			promise = promise.then(function() {
				return transportQuery.find().then(function(transports) {
					var trans = [];
					_.each(transports, function(transport) {
						trans.push({
							transportId:	transport.get('objectId'),
							source:		transport.get('source'),
							destination:	transport.get('destination'),
							userFullname:	'',
							userEmail:	''
						});
					});

					return trans;
				})
				.then(function(transports) {
					// TODO cauta utilizatorii

					pkg.transports = transports;
					results.push(pkg);
				});
			});
		});

		return promise;
	}).then(function() {
		response.success(results);
	});
});

					/*
							var userQuery = new Parse.Query(Parse.User);

							query.get((transport.get('user')).id, {
								success: function(transportUser) {
									trans.userFullname = user.get('firstname') + ' ' + user.get('lastname');
									trans.userEmail = user.get('email');
								},
								error: function(error) {
									console.log('Error: ' + error.code + ' ' + error.message);
								}
							});
					*/


// Attach the Express app to Cloud Code.
app.listen();
