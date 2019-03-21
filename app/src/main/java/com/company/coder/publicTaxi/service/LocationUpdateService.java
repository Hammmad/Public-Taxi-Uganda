package com.company.coder.publicTaxi.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Driver;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

public class LocationUpdateService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000; //10 seconds

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected static final String TAG = "LocationUpdateService";

    private GoogleApiClient mGoogleApiClient = null;
    private static LocationUpdateService mInstance;
    private Driver driver = null;
    private Vehicle vehicle = null;
    private LatLng currentLocation = null;

    public static synchronized LocationUpdateService getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        driver = SharedPref.getCurrentDriver(mInstance);
        vehicle = SharedPref.getCurrentVehicle(mInstance);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service init...");
        try {
            buildGoogleApiClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, "")
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Public Taxi is using your location")
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        }

        return Service.START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient==");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected==");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended==");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "" + location.getLatitude());
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        String lat = String.valueOf(currentLocation.latitude);
        String lng = String.valueOf(currentLocation.longitude);
        Driver driverObj = new Driver(driver.getName(), driver.getContact(), driver.getImage(), driver.getPassword(), driver.getVehicleUUID(), lat, lng, driver.getPushKey(), driver.getOwnerUUID());
        FirebaseDatabase.getInstance().getReference().child("PTA").child("Drivers").child(driver.getPushKey()).setValue(driverObj);
//        final Driver driverObj = new Driver(driver.getName(), driver.getContact(), driver.getImage(), driver.getPassword(), driver.getOwnerUUID(), driver.getVehicleUUID(), lat, lng);
//        FirebaseDatabase.getInstance().getReference().child("PTA").child("Drivers").child(vehicle.getmDriverUuid()).setValue(driverObj);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
        Log.d(TAG, "onDestroy();==");
        try {
            stopLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved();==");
    }


}
