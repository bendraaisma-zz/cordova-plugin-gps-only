# cordova-plugin-gps-only
NPM Cordova GPS Only Plugin For Android

This npm model only support a typescript wrapper for getting the GPS location using only satellites data

Install: ionic cordova plugin add cordova-plugin-gps-only

Example:

    import {GpsOnly} from 'cordova-plugin-gps-only';
    
	...
    export class DashboardComponent implements OnInit {
    
      public coordenate: GpsOnly;
      
      constructor() {
        this.platform.ready().then((readySource) => {
      	   this.coordenate = new GpsOnly();
    	 });
      }
      
      getGPS() {
        this.coordenate.coordenate((location) => {
	      if (location !== undefined) {
	        ....
	        item.setLatitudeInicio(location.latitude.toString());
	        item.setLongitudeInicio(location.longitude.toString());
	        ....
	      } else {
	        this.showToast('top', 'O sinal do GPS está em branco, verifique o GPS')
	      }
	    }, (error) => {
	      console.error(error);
	      this.showToast('top', 'Erro com sinal de GPS');
	    });
	  }
	...
