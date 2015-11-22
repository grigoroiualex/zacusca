
// These two lines are required to initialize Express in Cloud Code.
var express = require('express');
var _ = require('underscore');
var app = express();

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
	res.render('profilul-meu', {
		title: titlePrefix + ' - Profilul meu',
	});
});

app.get('/', function(req, res) {
	app.locals.extraJSs.push('login.js');
	res.render('login', {
		title: titlePrefix
	});
});

// Attach the Express app to Cloud Code.
app.listen();
