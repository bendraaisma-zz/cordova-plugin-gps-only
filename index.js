'use strict'

const jsGpsOnly = require("./www/js-gps-only.");

var gpsOnlyModule = (function() {
	
	var GpsOnly = function() {};
	
	GpsOnly.prototype.coordenate = function(success, failure) {

		jsGpsOnly.coordenate({
			success: success,
			failure: failure
		});
	};
	
	return {
		GpsOnly : GpsOnly
	}
	
}());

module.exports = jsGpsOnly;
