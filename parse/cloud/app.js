
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
var jsResourcesPath = '/resources/js/';
var jsVendorsPath = '/vendors/js/';

app.locals.extraJSs = [];
app.locals._ = _;

// This is an example of hooking up a request handler with a specific request
// path and HTTP verb using the Express routing API.
app.get('/profilul-meu', function(req, res) {
	app.locals.extraJSs.push(jsResourcesPath + 'my-profile.js');
	app.locals.extraJSs.push('https://maps.googleapis.com/maps/api/js?key=AIzaSyDaHp5QjUTb2ve2hpdSNtp7gdLl3dd6QHg');

	res.render('my-profile', {
		title: titlePrefix + ' - Profilul meu'
	});
});

app.get('/', function(req, res) {
	app.locals.extraJSs.push(jsResourcesPath + 'login.js');
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
				name:		pkg.get('name'),
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
		response.success(JSON.stringify(packages));
	});
});

// Queries for the transports that the user scheduled
Parse.Cloud.define('getTransportsByFbId', function(request, response) {
	var promise = getTransportsBy('fbId', request.params.facebookId);

	promise.then(function(transports) {
		if (transports == null) {
			response.error('Error retrieving transports');
		} else {
			response.success(JSON.stringify(transports));
		}
	});
});
Parse.Cloud.define('getTransportsByUserId', function(request, response) {
	var promise = getTransportsBy('userId', request.params.userId);

	promise.then(function(transports) {
		if (transports == null) {
			response.error('Error retrieving transports');
		} else {
			response.success(JSON.stringify(transports));
		}
	});
});
Parse.Cloud.define('getTransportsByUser', function(request, response) {
	var promise = getTransportsBy('userObj', request.params.userObj);

	promise.then(function(transports) {
		if (transports == null) {
			response.error('Error retrieving transports');
		} else {
			response.success(JSON.stringify(transports));
		}
	});
});
Parse.Cloud.define('getTransportsByCurrentUser', function(request, response) {
	var promise = getTransportsBy('userObj', request.user);

	promise.then(function(transports) {
		if (transports == null) {
			response.error('Error retrieving transports');
		} else {
			response.success(JSON.stringify(transports));
		}
	});
});

function getUserObject(identifierType, identifier) {
	if (identifierType === 'userObj') {
		return new Parse.Promise.as(identifier);
	} else if (identifierType === 'userId') {
		var userQuery = new Parse.Query(Parse.User);

		return userQuery.get(user.id, {
			success: function(user) {
				return user;
			}
		});
	} else if (identifierType === 'fbId') {
		var userQuery = new Parse.Query(Parse.User);

		userQuery.equalTo('facebook_id', identifier);
		return userQuery.find()
			.then(function(users) {
				return users[0];
			});
	}
	
	return null;
}

// Queries for the transports that the user scheduled.
// identifierType can be 'userObj', 'userId' or 'fbId' and represents the
// column where to look for the identifier
function getTransportsBy(identifierType, identifier) {
	var transQuery = new Parse.Query('Transport');
	var promise = getUserObject(identifierType, identifier);

	return promise.then(function(userObject) {
		transQuery.equalTo('user', userObject);
		return transQuery.find()
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
			});
	});
}

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

			return promise.resolve(item);
		},
		error: function(error) {
			return promise.reject(error);
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
		transportQuery.greaterThan('slots_available', 0);
		transportQuery.include('pending_packages');
		transportQuery.include('accepted_packages');

		return transportQuery.find()
		.then(function(transports) {
			var promises = [];

			_.each(transports, function(transport) {
				var trans, state;
				var compareFunction = function(p) { return p === pkg.id; };

				if (_.find(transport.get('accepted_packages'), compareFunction)) {
					state = 'accepted';
				} else if(_.find(transport.get('pending_packages'), compareFunction)) {
					state = 'pending';
				} else {
					state = 'not-accepted';
				}

				trans = {
					objectId:	transport.id,
					source:		transport.get('source'),
					destination:	transport.get('destination'),
					state:		state,
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
			response.success(JSON.stringify(results));
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

	transportQuery.include('pending_packages');
	transportQuery.get(request.params.transport.objectId, {})
	.then(function(transport) {
		var promises = [];
		var packages = _.union(transport.get('accepted_packages'), transport.get('pending_packages'));

		_.each(packages, function(pkgObjectId) {
			var pkgQuery = new Parse.Query('Package');

			promises.push(
				pkgQuery.get(pkgObjectId, {})
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
		response.success(JSON.stringify(results));
	});
});

// Changes the state of the package with the given pkgId to the
// state given as param. If everything went well returns a
// solved promise, otherwise a rejected promise with the error
function changePackageState(pkgId, state) {
	var promise = new Parse.Promise();
	var pkgQuery = new Parse.Query('Package');

	pkgQuery.get(pkgId, {
		success: function(pkg) {
			pkg.set('state', state);
			pkg.save();
			promise.resolve();
		},
		error: function(object, error) {
			promise.reject(error);
		}
	});

	return promise;
}

function addPackageToPendingList(pkgId, transportId) {
	var promise = new Parse.Promise();
	var pkgQuery = new Parse.Query('Package');

	pkgQuery.get(pkgId, {
		success: function(pkg) {
			promise.resolve(pkg);
		},
		error: function(error) {
			promise.reject(error);
		}
	});

	return promise.then(function(pkg) {
		var transQuery = new Parse.Query('Transport');
		transQuery.include('pending_packages');

		return transQuery.get(transportId, {
			success: function(trans) {
				pkg.set('transport', trans);
				return pkg.save().then(function() {
					trans.addUnique('pending_packages', pkg.id);

					return trans.save();
				});
			},
			error: function(error) {
				return Parse.Promise.error(error);
			}
		});
	});
}

Parse.Cloud.define('requestJoin', function(request, response) {
	addPackageToPendingList(request.params.pkgId, request.params.transportId)
	.then(function() {
		return changePackageState(request.params.pkgId, 'pending');
	}, function(error) {
		response.error('Could not join package to transport. ' + error.message);
	})
	.then(function() {
		response.success('success');
	});
});

function movePackageToAcceptedList(pkgId, transportId) {
	var promise = new Parse.Promise;
	var pkgQuery = new Parse.Query('Package');

	pkgQuery.get(pkgId, {
		success: function(pkg) {
			promise.resolve(pkg);
		},
		error: function(error) {
			promise.reject(error);
		}
	});

	return promise.then(function(pkg) {
		var transQuery = new Parse.Query('Transport');

		return transQuery.get(transportId, {
			success: function(trans) {
				trans.remove('pending_packages', pkg.id);
				trans.addUnique('accepted_packages', pkg.id);
				trans.increment('slots_available', -1);

				return trans.save();
			},
			error: function(error) {
				return Parse.Promise.error(error);
			}
		});
	});
}

Parse.Cloud.define('acceptJoin', function(request, response) {
	changePackageState(request.params.pkgId, 'accepted')
	.then(function() {
		return movePackageToAcceptedList(request.params.pkgId, request.params.transportId);
	}, function(error) {
		response.error('Could not accept package. ' + error.message);
	})
	.then(function() {
		response.success('success');
	});
});

Parse.Cloud.define('setCoordinatesForTransport', function(request, response) {
	var transportQuery = new Parse.Query('Transport');

	transportQuery.equalTo('objectId', request.params.transportId);
	transportQuery.first().then(function(transport) {
		transport.set('lat', request.params.lat);
		transport.set('lng', request.params.lng);

		transport.save();

		response.success('success');
	},
	function(error) {
		response.error(error);
	});
});

Parse.Cloud.define('getCoordinatesForPackage', function(request, response) {
	var packageQuery = new Parse.Query('Package');

	packageQuery.equalTo('objectId', request.params.packageId);
	packageQuery.first().then(function(pkg) {
		var transportQuery = new Parse.Query('Transport');

		transportQuery.equalTo('objectId', pkg.get('transport').id);
		transportQuery.first().then(function(transport) {
			var coordinates = {
				'lat': transport.get('lat'),
				'lng': transport.get('lng')
			};

			response.success(coordinates);
		});
	});
});

// Attach the Express app to Cloud Code.
app.listen();
