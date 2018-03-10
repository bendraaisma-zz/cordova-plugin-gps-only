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
import static android.util.Log.e;
import static android.util.Log.i;
import static android.util.Log.w;

public class GpsOnly extends CordovaPlugin {

    private static final String TAG = "GpsOnly";
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
        if (!(cordova.hasPermission(PERMISSION[0]) && cordova.hasPermission(PERMISSION[1]))) {
            i(TAG, "Requesting GPS permission");
            cordova.requestPermissions(this, REQUEST_CODE, PERMISSION);
        } else {
            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, new GpsOnlyLocationListener());
            GpsOnly.this.hasPermission = true;
            i(TAG, "GPS permission enabled");
        }
    }

    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        if ("position".equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if (GpsOnly.this.hasPermission) {
                        JSONObject r = new JSONObject();
                        try {
                            r.put("status", GpsOnly.this.status);
                            r.put("providerEnabled", GpsOnly.this.providerEnabled);
                            r.put("provider", GpsOnly.this.provider);
                            r.put("longitude", GpsOnly.this.location.getLongitude());
                            r.put("latitude", GpsOnly.this.location.getLatitude());
                            r.put("altitude", GpsOnly.this.location.getAltitude());
                            r.put("speed", GpsOnly.this.location.getSpeed());
                            r.put("time", GpsOnly.this.location.getTime());
                            callbackContext.success(r);
                            i(TAG, "GPS successful");
                        } catch (JSONException e) {
                            callbackContext.error(e.getMessage());
                            e(TAG, "GPS error " + e.getMessage());
                        }
                    } else {
                        callbackContext.error("No permission to read GPS data, verify GPS permission");
                        w(TAG, "No permission to read GPS data, verify GPS permission");
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
            GpsOnly.this.hasPermission = true;
            i(TAG, "GPS permission enabled");
        }
    }

    class GpsOnlyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            GpsOnly.this.location = location;
            i(TAG, "GPS Location changed");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            GpsOnly.this.provider = provider;
            GpsOnly.this.status = status;
            i(TAG, "GPS Status changed");
        }

        public void onProviderEnabled(String provider) {
            GpsOnly.this.provider = provider;
            GpsOnly.this.providerEnabled = true;
            i(TAG, "GPS Provider enabled");
        }

        public void onProviderDisabled(String provider) {
            GpsOnly.this.provider = provider;
            GpsOnly.this.providerEnabled = false;
            i(TAG, "GPS Provider disabled");
        }
    }
}
