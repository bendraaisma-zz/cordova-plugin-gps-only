package br.com.etpi.gps;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationProvider.OUT_OF_SERVICE;

public class GpsOnly extends CordovaPlugin {

    private static final String[] PERMISSION = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
    private static final int REQUEST_CODE = 0;
    private LocationManager locationManager;
    private Location location = new Location("none");
    private String provider = "none";
    private int status = OUT_OF_SERVICE;
    private boolean providerEnabled = false;
    private boolean hasPermission = false;

    @SuppressLint("MissingPermission")
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        locationManager = (LocationManager) webView.getContext().getSystemService(LOCATION_SERVICE);
        if (!(cordova.hasPermission(PERMISSION[0]) && cordova.hasPermission(PERMISSION[2]))) {
            cordova.requestPermissions(this, REQUEST_CODE, PERMISSION);
        } else {
            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, new GpsOnlyLocationListener());
            Gps.this.hasPermission = true;
        }
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if ("coordenate".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if (Gps.this.hasPermission) {
                        JSONObject r = new JSONObject();
                        try {
                            r.put("status =", Gps.this.status);
                            r.put("providerEnabled", Gps.this.providerEnabled);
                            r.put("provider", Gps.this.provider);
                            r.put("longitude", Gps.this.location.getLongitude());
                            r.put("latitude", Gps.this.location.getLatitude());
                            r.put("altitude", Gps.this.location.getAltitude());
                            r.put("speed", Gps.this.location.getSpeed());
                            r.put("time", Gps.this.location.getTime());
                            callbackContext.success(r);
                        } catch (JSONException e) {
                            callbackContext.error(e.getMessage());
                        }
                    } else {
                        callbackContext.error("No permission to read GPS data, verify GPS permission");
                    }
                }
            });
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (REQUEST_CODE == requestCode && PERMISSION.length == permissions.length && PERMISSION.length == grantResults.length && PERMISSION_GRANTED == grantResults[0] && PERMISSION_GRANTED == grantResults[1]) {
            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, new GpsOnlyLocationListener());
            Gps.this.hasPermission = true;
        }
    }

    class GpsOnlyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            Gps.this.location = location;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Gps.this.provider = provider;
            Gps.this.status = status;
        }

        public void onProviderEnabled(String provider) {
            Gps.this.provider = provider;
            Gps.this.providerEnabled = true;
        }

        public void onProviderDisabled(String provider) {
            Gps.this.provider = provider;
            Gps.this.providerEnabled = false;
        }
    }
}
