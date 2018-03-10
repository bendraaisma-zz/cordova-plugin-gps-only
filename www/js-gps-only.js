'use strict'

var gpsOnly = {
	coordenate : function(success, failure) {
		cordova.exec(success || emptyFnc,
			failure || emptyFnc,
			'GpsOnly',
			'position',
			[]
		);
	}
};

module.exports = gpsOnly;