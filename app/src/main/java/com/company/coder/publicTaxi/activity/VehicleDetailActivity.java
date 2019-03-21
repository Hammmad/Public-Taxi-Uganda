package com.company.coder.publicTaxi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.AppLogs;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VehicleDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "details_screen";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int SELECT_START_POINT = 1;
    public static final int SELECT_END_POINT = 2;
    private final int VEHICLE_GALLERY_CODE = 72;
    private final int VEHICLE_CAMERA_CODE = 74;
    List<String> listPermissionsNeeded;
    private Vehicle vehicleObj = null;
    private CircleImageView displayVehicleImg, updateVehicleImg;
    private TextView displayVehicleMake, displayVehicleModel, displayVehicleColor, displayPlateNumber, displayStartPoint, displayEndPoint, updateStartPoint, updateEndPoint;
    private EditText updateVehicleMakeInput, updateVehicleModelInput, updateVehicleColorInput, updateVehiclePlateInput;
    private Button updateCancelBtn, updateBtn;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabUpdate, fabDelete;
    private RelativeLayout displayView, updateView;
    private Place mStartLocation, mEndLocation = null;
    private Uri carfilePath = null;
    String vehicleImageUrl = null;
    private LocationManager locationManager = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                VehicleDetailActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_detail);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setToolBars();
        initViews();
        getBundles();
        updateVehicleImg.setOnClickListener(this);
        updateStartPoint.setOnClickListener(this);
        updateEndPoint.setOnClickListener(this);
        fabUpdate.setOnClickListener(this);
        fabDelete.setOnClickListener(this);
        updateCancelBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
    }

    private void initViews() {
        displayVehicleImg = findViewById(R.id.detail_vehicle_display_img);
        updateVehicleImg = findViewById(R.id.detail_vehicle_update_img);

        displayVehicleMake = findViewById(R.id.detail_vehicle_display_make);
        displayVehicleModel = findViewById(R.id.detail_vehicle_display_model);
        displayVehicleColor = findViewById(R.id.detail_vehicle_display_color);
        displayPlateNumber = findViewById(R.id.detail_vehicle_display_number_plate);
        displayStartPoint = findViewById(R.id.detail_vehicle_display_start_point);
        displayEndPoint = findViewById(R.id.detail_vehicle_display_end_point);
        updateStartPoint = findViewById(R.id.detail_vehicle_update_start_point);
        updateEndPoint = findViewById(R.id.detail_vehicle_update_end_point);

        updateVehicleMakeInput = findViewById(R.id.detail_vehicle_update_make);
        updateVehicleModelInput = findViewById(R.id.detail_vehicle_update_model);
        updateVehicleColorInput = findViewById(R.id.detail_vehicle_update_color);
        updateVehiclePlateInput = findViewById(R.id.detail_vehicle_update_number_plate);

        updateCancelBtn = findViewById(R.id.detail_update_cancel_btn);
        updateBtn = findViewById(R.id.detail_update_btn);

        fabMenu = findViewById(R.id.fab_menu);
        fabUpdate = findViewById(R.id.fab_update_item);
        fabDelete = findViewById(R.id.fab_delete_item);

        displayView = findViewById(R.id.detail_display_view);
        updateView = findViewById(R.id.detail_update_view);

    }

    private void getBundles() {
        Bundle bundle = null;
        Intent intent = getIntent();
        if (intent != null) {
            bundle = intent.getExtras();
        }
        if (bundle != null) {
            vehicleObj = bundle.getParcelable("driverObj");
        }
        if (vehicleObj != null) {
            setDataToViews(vehicleObj);
        }
    }

    private void setDataToViews(Vehicle vehicle) {
        displayVehicleMake.setText(vehicle.getmMake());
        displayVehicleModel.setText(vehicle.getmModel());
        displayVehicleColor.setText(vehicle.getmColor());
        displayPlateNumber.setText(vehicle.getmPlateNumber());
        displayStartPoint.setText(vehicle.getmStartName());
        displayEndPoint.setText(vehicle.getmEndName());

        updateVehicleMakeInput.setText(vehicle.getmMake());
        updateVehicleModelInput.setText(vehicle.getmModel());
        updateVehicleColorInput.setText(vehicle.getmColor());
        updateVehiclePlateInput.setText(vehicle.getmPlateNumber());
        updateStartPoint.setText(vehicle.getmStartName());
        updateEndPoint.setText(vehicle.getmEndName());

        if (!vehicle.getmImgUrl().equals("")) {
            Picasso.with(VehicleDetailActivity.this)
                    .load(vehicle.getmImgUrl())
                    .placeholder(R.drawable.progress_animation)
                    .into(displayVehicleImg);

            Picasso.with(VehicleDetailActivity.this)
                    .load(vehicle.getmImgUrl())
                    .placeholder(R.drawable.progress_animation)
                    .into(updateVehicleImg);
        }
    }

    private void setToolBars() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.vehicle_detail_toolbar);
        toolbar.setTitle("DETAILS");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable upArrow = getResources().getDrawable(R.drawable.back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_delete_item:

                AlertDialog.Builder builder = new AlertDialog.Builder(VehicleDetailActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            case R.id.fab_update_item:
//                if (vehicleObj != null) {
//                    setDataToViews(vehicleObj);
//                }
                displayView.setVisibility(View.GONE);
                fabMenu.setVisibility(View.GONE);
                updateView.setVisibility(View.VISIBLE);
                break;
            case R.id.detail_update_cancel_btn:
                updateView.setVisibility(View.GONE);
                displayView.setVisibility(View.VISIBLE);
                fabMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.detail_update_btn:
                WaitDialog.showWaitDialog("Updating...", VehicleDetailActivity.this);
                updateUserDataToFirebase();
                break;
            case R.id.detail_vehicle_update_img:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    checkAndRequestPermissions();
                } else {
                    chooseImage(VEHICLE_GALLERY_CODE, VEHICLE_CAMERA_CODE);
                }
                break;
            case R.id.detail_vehicle_update_start_point:
                Log.d(TAG, "Start");
                searchPlacesIntent(SELECT_START_POINT);
                break;
            case R.id.detail_vehicle_update_end_point:
                Log.d(TAG, "End");
                searchPlacesIntent(SELECT_END_POINT);
                break;

        }
    }

    private void updateUserDataToFirebase() {
        final String vMake = updateVehicleMakeInput.getText().toString();
        final String vModel = updateVehicleModelInput.getText().toString();
        final String vColor = updateVehicleColorInput.getText().toString();
        final String vPlate = updateVehiclePlateInput.getText().toString();
        final String startName;
        final String startLat;
        String startLng = null;
        final String endName;
        final String endLat;
        String endLng = null;
        if (mStartLocation != null) {
            LatLng start = mStartLocation.getLatLng();
            startName = String.valueOf(mStartLocation.getAddress());
            startLat = String.valueOf(start.latitude);
            startLng = String.valueOf(start.longitude);
        } else {
            startName = vehicleObj.getmStartName();
            startLat = vehicleObj.getmStartLat();
            startLng = vehicleObj.getmEndLng();
        }
        if (mEndLocation != null) {
            LatLng end = mEndLocation.getLatLng();
            endName = String.valueOf(mEndLocation.getAddress());
            endLat = String.valueOf(end.latitude);
            endLng = String.valueOf(end.longitude);
        } else {
            endName = vehicleObj.getmEndName();
            endLat = vehicleObj.getmEndLat();
            endLng = vehicleObj.getmEndLng();
        }

//        if (driverfilePath != null) {
//            final String driverImagePushKey = FirebaseDatabase.getInstance().getReference().push().getKey();
//            StorageReference reference = FirebaseStorage.getInstance().getReference().child("drivers/" + driverImagePushKey);
//            reference.putFile(driverfilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    if (taskSnapshot.getMetadata() != null) {
//                        driverImageUrl = String.valueOf(taskSnapshot.getMetadata().getDownloadUrl());
//                        final Driver driver = new Driver(dName, dNumber, driverImageUrl, driverObj.getPassword(), driverObj.getOwnerUUID(), driverObj.getVehicleUUID(), driverObj.getLat(), driverObj.getLng());
//                        FirebaseDatabase.getInstance().getReference().child("PTA").child("Drivers").child(vehicleObj.getmDriverUuid()).setValue(driver);
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    WaitDialog.closeWaitDialog();
//                    Log.d(TAG, "onFailure Driver Img: " + e.getMessage());
//                }
//            });
//        } else {
//            final Driver driver = new Driver(dName, dNumber, driverObj.getImage(), driverObj.getPassword(), driverObj.getOwnerUUID(), driverObj.getVehicleUUID(), driverObj.getLat(), driverObj.getLng());
//            FirebaseDatabase.getInstance().getReference().child("PTA").child("Drivers").child(vehicleObj.getmDriverUuid()).setValue(driver);
//        }

        if (carfilePath != null) {
            final String vehicleImagePushKey = FirebaseDatabase.getInstance().getReference().push().getKey();
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("vehicles/" + vehicleImagePushKey);
            final String finalStartLng = startLng;
            final String finalEndLng = endLng;
            reference.putFile(carfilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
//                        vehicleImageUrl = String.valueOf(taskSnapshot.getMetadata().getDownloadUrl());
//                        Vehicle vehicle = new Vehicle(dPlate, dEngine, vehicleImageUrl, startLat, finalStartLng, startName, endLat, finalEndLng, endName, vehicleObj.getmOwnerUuid(), vehicleObj.getmDriverUuid());
                        Vehicle vehicle = new Vehicle(vMake, vModel, vColor, vPlate, startLat, finalStartLng, startName, endLat, finalEndLng, endName, vehicleImageUrl, vehicleObj.getmOwnerUuid(), vehicleObj.getmCompanyId());
                        FirebaseDatabase.getInstance().getReference().child("PTA").child("Vehicles").child(vehicleObj.getmPlateNumber()).setValue(vehicle);
                    }
                    WaitDialog.closeWaitDialog();
                    VehicleDetailActivity.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure Vehicle Img: " + e.getMessage());
                    WaitDialog.closeWaitDialog();
                }
            });
        } else {
            Vehicle vehicle = new Vehicle(vMake, vModel, vColor, vPlate, startLat, startLng, startName, endLat, endLng, endName, vehicleObj.getmImgUrl(), vehicleObj.getmOwnerUuid(), vehicleObj.getmCompanyId());
            FirebaseDatabase.getInstance().getReference().child("PTA").child("Vehicles").child(vehicleObj.getmPlateNumber()).setValue(vehicle);
            WaitDialog.closeWaitDialog();
            VehicleDetailActivity.this.finish();
        }
    }

    private void searchPlacesIntent(int selectPoint) {
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkAndRequestPermissions();
            } else {
                Location location = getLocation();
                if (location != null) {
                    //                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                                .setBoundsBias(new LatLngBounds(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude())))
//                                .build(VehicleDetailActivity.this);
//                        startActivityForResult(intent, selectPoint);
                }
            }
        }
    }

    private void chooseImage(final int gallery, final int camera) {
//        Intent intent = new Intent();
////        intent.setType("image/*");
////        intent.setAction(Intent.ACTION_GET_CONTENT);
////        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Select an action");
        alertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, gallery);
            }
        }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, camera);
                }
            }
        });
        alertDialog.show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    mStartLocation = Autocomplete.getPlaceFromIntent(data);
                    updateStartPoint.setText(mStartLocation.getAddress());
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    mEndLocation = Autocomplete.getPlaceFromIntent(data);
                    updateEndPoint.setText(mEndLocation.getAddress());
                }
                break;
            case VEHICLE_GALLERY_CODE:
                if (resultCode == Activity.RESULT_OK && (data != null && data.getData() != null)) {
                    carfilePath = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), carfilePath);
                        updateVehicleImg.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case VEHICLE_CAMERA_CODE:
                if (resultCode == Activity.RESULT_OK && (data != null && data.getExtras() != null)) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        carfilePath = getImageUri(VehicleDetailActivity.this, bitmap);
                        updateVehicleImg.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(VehicleDetailActivity.this);
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

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    WaitDialog.showWaitDialog("Deleting", VehicleDetailActivity.this);
                    FirebaseDatabase.getInstance().getReference().child("PTA").child("Vehicles").child(vehicleObj.getmPlateNumber()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            WaitDialog.closeWaitDialog();
                            VehicleDetailActivity.this.finish();
                        }
                    });
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

}
