package com.company.coder.publicTaxi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Driver;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.routes.DecodePoly;
import com.company.coder.publicTaxi.routes.Example;
import com.company.coder.publicTaxi.routes.RetrofitMaps;
import com.company.coder.publicTaxi.service.LocationUpdateService;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.ServiceCheck;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class DriverHomeActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "DriverHomeScreen";
    private MapView mapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    android.support.v7.widget.Toolbar toolbar = null;
    private LocationManager manager = null;
    private LatLng currentLatlng = null;
    ArrayList<Driver> driverArrayList;
    HashMap<String, Marker> markerHashMap;
    int markerIndex = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_home_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.driver_action_logout:
                SharedPref.clearAllData(DriverHomeActivity.this);
                if (ServiceCheck.isServiceRunning(LocationUpdateService.class, DriverHomeActivity.this)) {
                    stopService(new Intent(DriverHomeActivity.this, LocationUpdateService.class));
                }
                DriverHomeActivity.this.startActivity(new Intent(DriverHomeActivity.this, MainActivity.class));
                DriverHomeActivity.this.finish();
                return true;
            case R.id.driver_action_support:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Select an action");
                alertDialog.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + "0701074727"));
                        if (ActivityCompat.checkSelfPermission(DriverHomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        DriverHomeActivity.this.startActivity(intent);
                    }
                }).setNegativeButton("Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "kennethbintu@yahoo.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setToolBars();
        mapView = (MapView) findViewById(R.id.driver_home_mapView);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        initializeMap();
        driverArrayList = new ArrayList<>();
        markerHashMap = new HashMap<>();
        // Check if gps is enabled
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void setToolBars() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.driver_home_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            final Drawable upArrow = getResources().getDrawable(R.drawable.back_material);
//            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setHomeAsUpIndicator(upArrow);
//            }
//        }
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        }
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().isMyLocationButtonEnabled();
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(DriverHomeActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setMyLocationEnabled(true);
                map.setTrafficEnabled(true);
                map.setIndoorEnabled(true);
                map.setBuildingsEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setMapToolbarEnabled(true);
//                googleMap.getMyLocation();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(true);
            map.setTrafficEnabled(true);
            map.setIndoorEnabled(true);
            map.setBuildingsEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(true);
//            googleMap.getMyLocation();
        }

        getRoutesData();
        getDriversData();
        if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = getLocation();
            if (location != null) {
                currentLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng, 18));
            }
        }

    }

    private void initializeMap() {
        if (googleMap == null) {
            mapView = (MapView) findViewById(R.id.driver_home_mapView);
            mapView.getMapAsync(this);
            //setup markers etc...
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DriverHomeActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(DriverHomeActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(DriverHomeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(DriverHomeActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(DriverHomeActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(DriverHomeActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
//        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
//        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void getRoutesData() {
        WaitDialog.showWaitDialog("Loading Routes...", DriverHomeActivity.this);
        FirebaseDatabase.getInstance().getReference().child("PTA").child("Vehicles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "All Vehicles: " + dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vehicle vehicle = snapshot.getValue(Vehicle.class);
                    if (vehicle != null && (!vehicle.getmEndLat().equals("0") && !vehicle.getmEndLng().equals("0")) && (!vehicle.getmStartLat().equals("0") && !vehicle.getmStartLng().equals("0"))) {
                        LatLng startLatLng = new LatLng(Double.parseDouble(vehicle.getmStartLat()), Double.parseDouble(vehicle.getmStartLng()));
                        LatLng endLatLng = new LatLng(Double.parseDouble(vehicle.getmEndLat()), Double.parseDouble(vehicle.getmEndLng()));
                        drawNavUsingRetrofit(startLatLng, endLatLng);
                    }
                }
                WaitDialog.closeWaitDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "" + databaseError.getMessage());
                WaitDialog.closeWaitDialog();
            }
        });
    }

    private void getDriversData() {
        FirebaseDatabase.getInstance().getReference().child("PTA").child("Drivers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "All Drivers: " + dataSnapshot.toString());
                createMarkers(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "Updated Drivers: " + dataSnapshot.toString());
//                Log.d(TAG, "String: " + s);
                updateMarker(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateMarker(DataSnapshot dataSnapshot) {
        Driver driver = dataSnapshot.getValue(Driver.class);
        Marker marker = null;
        if (driver != null) {
            marker = markerHashMap.get(driver.getVehicleUUID());
            if (marker != null) {
                Log.d("Marker-Position", "" + marker.getPosition());
                LatLng latLng = new LatLng(Double.parseDouble(driver.getLat()), Double.parseDouble(driver.getLng()));
//            marker.setPosition(latLng);
                animateMarker(marker, latLng);
            }
        }
    }

    private void createMarkers(DataSnapshot dataSnapshot) {
        Driver driverObj = dataSnapshot.getValue(Driver.class);
        if (driverObj != null && (!driverObj.getLat().equals("") && !driverObj.getLng().equals("0"))) {
            driverArrayList.add(dataSnapshot.getValue(Driver.class));
            Driver driver = driverArrayList.get(markerIndex);
            LatLng latLng = new LatLng(Double.parseDouble(driver.getLat()), Double.parseDouble(driver.getLng()));
//        Marker marker = googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(latLng.latitude, latLng.longitude))
//                .title(driver.getVehicleUUID())
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            View markerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_car_marker_layout, null);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(driver.getVehicleUUID())
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, markerView))));
            markerHashMap.put(driver.getVehicleUUID(), marker);
            markerIndex++;
        }
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void drawNavUsingRetrofit(final LatLng origin, final LatLng dest) {
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitMaps service = retrofit.create(RetrofitMaps.class);
        Call<Example> call = service.getDistanceDuration("metric", origin.latitude + "," + origin.longitude, dest.latitude + "," + dest.longitude, "driving");
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(retrofit.Response<Example> response, Retrofit retrofit) {
                Log.d(TAG, "Navigation Response: " + response.message());
                Log.d(TAG, "Navigation Size: " + response.body().getRoutes().size());
                createStartMarker(origin);
                createEndMarker(dest);
                try {
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = DecodePoly.decodePolyLine(encodedString);
                        googleMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(12)
                                .color(Color.parseColor("#0C1558"))
                                .geodesic(true)
                                .clickable(true)
                        );
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Navigation Exception: " + e);
                    WaitDialog.closeWaitDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                WaitDialog.closeWaitDialog();
                Log.d("onFailure", t.toString());
            }
        });
    }

    private void createEndMarker(LatLng dest) {
        View end = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_flag_marker_layout, null);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(dest)
                .title("End")
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, end))));
    }

    private void createStartMarker(LatLng origin) {
        View start = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_map_marker_layout, null);
        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(origin)
                .title("Start")
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, start))));
    }

    public void animateMarker(final Marker marker, final LatLng toPosition) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 200;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    marker.setVisible(true);
                }
            }
        });
    }


    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DriverHomeActivity.this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private Location getLocation() {
        Location currentLocation = null;
        if (ActivityCompat.checkSelfPermission(DriverHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (DriverHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        } else {
            Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                currentLocation = location;
            } else if (location1 != null) {
                currentLocation = location1;
            } else if (location2 != null) {
                currentLocation = location2;
            } else {
                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
            }
        }
        return currentLocation;
    }

}
