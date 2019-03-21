package com.company.coder.publicTaxi.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Driver;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.service.LocationUpdateService;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.ServiceCheck;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.company.coder.publicTaxi.utils.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DriverChooseRouteActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "Driver_Choose_RouteActivity";
    public static final int SELECT_START_POINT = 1;
    public static final int SELECT_END_POINT = 2;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    TextView startPoint, endPoint;
    LinearLayout startClick, endClick;
    private LocationManager locationManager = null;
    List<String> listPermissionsNeeded;
    private Place mStartLocation, mEndLocation = null;
    private List<Place> placeList;
    private Driver driver;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_choose_route);
        getBundles();
        placeList = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startPoint = findViewById(R.id.firstLogin_startPoint);
        endPoint = findViewById(R.id.firstLogin_endPoint);
        startClick = findViewById(R.id.firstLogin_startPoint_Click);
        endClick = findViewById(R.id.firstLogin_endPoint_Click);
//         Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyDecAA1tPGUFmzl8TaHuIevVJFQw5PFh18");

        startClick.setOnClickListener(this);
        endClick.setOnClickListener(this);
    }

    private void getBundles() {
        Bundle bundle = null;
        Intent intent = getIntent();
        if (intent != null) {
            bundle = intent.getExtras();
        }
        if (bundle != null) {
            driver = bundle.getParcelable("driverBundle");
            vehicle = bundle.getParcelable("vehicleBundle");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.firstLogin_startPoint_Click:
                searchPlacesIntent(SELECT_START_POINT);
                break;
            case R.id.firstLogin_endPoint_Click:
                searchPlacesIntent(SELECT_END_POINT);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    mStartLocation = Autocomplete.getPlaceFromIntent(data);
                    placeList.add(mStartLocation);
                    startPoint.setText(mStartLocation.getName());
                    if (placeList.size() > 1) {
                        updateVehicleRoutes();
                    }
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    mEndLocation = Autocomplete.getPlaceFromIntent(data);
                    placeList.add(mEndLocation);
                    endPoint.setText(mEndLocation.getName());
                    if (placeList.size() > 1) {
                        updateVehicleRoutes();
                    }
                }
                break;
        }
    }

    private void updateVehicleRoutes() {
        WaitDialog.showWaitDialog("Loading...", DriverChooseRouteActivity.this);
        LatLng startLatLng = mStartLocation.getLatLng();
        LatLng endLatLng = mEndLocation.getLatLng();
        String startName = String.valueOf(mStartLocation.getName());
        String startLat = String.valueOf(startLatLng.latitude);
        String startLng = String.valueOf(startLatLng.longitude);
        String endName = String.valueOf(mEndLocation.getName());
        String endLat = String.valueOf(endLatLng.latitude);
        String endLng = String.valueOf(endLatLng.longitude);
        final Vehicle vehicleObj = new Vehicle(vehicle.getmMake(), vehicle.getmModel(), vehicle.getmColor(), vehicle.getmPlateNumber(), startLat, startLng, startName, endLat, endLng, endName, "", vehicle.getmOwnerUuid(), vehicle.getmCompanyId());
        FirebaseDatabase.getInstance().getReference().child("PTA").child("Vehicles").child(vehicle.getmPlateNumber()).setValue(vehicleObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                WaitDialog.closeWaitDialog();
                launchDriver(driver, vehicleObj);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                WaitDialog.closeWaitDialog();
                Toast.makeText(DriverChooseRouteActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void searchPlacesIntent(int selectPoint) {
        // Search places intent
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkAndRequestPermissions();
            } else {
                Location location = getLocation();
                if (location != null) {
                    List<Place.Field> fields = Arrays.asList( Place.Field.ADDRESS,Place.Field.NAME,
                            Place.Field.LAT_LNG, Place.Field.ID);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                            .build(DriverChooseRouteActivity.this);
                    startActivityForResult(intent, selectPoint);


                }
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DriverChooseRouteActivity.this);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions();
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

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

    private void checkAndRequestPermissions() {
        int writePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int readPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int internetPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);

        int fineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int coarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        listPermissionsNeeded = new ArrayList<>();

        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                for (String s : permissions) {
                    Log.d("Perm", s);
                }
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "Permission Denied. Denying permission may cause it no longer function intended.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void launchDriver(Driver driverObj, Vehicle vehicle) {
        SharedPref.setCurrentDriver(DriverChooseRouteActivity.this, driverObj);
        SharedPref.setCurrentVehicle(DriverChooseRouteActivity.this, vehicle);
        SharedPref.setCurrentUserType(DriverChooseRouteActivity.this, Constants.TYPE_DRIVER);
        if (!ServiceCheck.isServiceRunning(LocationUpdateService.class, DriverChooseRouteActivity.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DriverChooseRouteActivity.this.startForegroundService(new Intent(DriverChooseRouteActivity.this, LocationUpdateService.class));
            } else {
                DriverChooseRouteActivity.this.startService(new Intent(DriverChooseRouteActivity.this, LocationUpdateService.class));
            }

        }
        Intent intent = new Intent(DriverChooseRouteActivity.this, DriverHomeActivity.class);
        DriverChooseRouteActivity.this.startActivity(intent);
        MainActivity.getInstance().finish();
    }
}
