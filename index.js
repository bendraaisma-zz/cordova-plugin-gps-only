'use strict'

const gpsOnly = require("./www/js-gps-only.");

var gpsOnlyModule = (function() {
	
	var GpsOnly = function() {};
	
	GpsOnly.prototype.coordenate = function(success, failure) {

		gpsOnly.coordenate({
			success: success,
			failure: failure
		});
	};
	
	return {
		GpsOnly : GpsOnly
	}
	
}());

module.exports = gpsOnly;
