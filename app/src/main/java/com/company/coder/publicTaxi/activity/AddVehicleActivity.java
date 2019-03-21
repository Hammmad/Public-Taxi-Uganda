package com.company.coder.publicTaxi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Company;
import com.company.coder.publicTaxi.modles.Owner;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.ui.dialogs.MessageDialog;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.AppLogs;
import com.company.coder.publicTaxi.utils.NetworkUtil;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddVehicleActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "add_driver_screen";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int SELECT_START_POINT = 1;
    public static final int SELECT_END_POINT = 2;
    private MaterialEditText mMake, mModel, mColor, mPlateNo;
    private TextView mStartPoint, mEndPoint;
    private LinearLayout startClick, endClick;
    private ImageView vehicleImage;
    private Button addVehicleBtn;
    private Place mStartLocation, mEndLocation = null;
    private LocationManager locationManager = null;
    private Owner owner = null;
    private Uri carfilePath = null;
    private final int VEHICLE_GALLERY_CODE = 72;
    private final int VEHICLE_CAMERA_CODE = 74;
    String vehicleImageUrl = null;
    List<String> listPermissionsNeeded;
    ArrayList<Company> companyList;
    Spinner companySpinner;
    Company company = null;
//    ArrayAdapter<Company> myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        companyList = new ArrayList<>();
        initViews();
        owner = SharedPref.getCurrentOwner(this);
        startClick.setOnClickListener(this);
        endClick.setOnClickListener(this);
        addVehicleBtn.setOnClickListener(this);
        vehicleImage.setOnClickListener(this);
        getCompaniesData();
        initSpinAdapter();
    }

    private void initSpinAdapter() {
        ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(this, android.R.layout.simple_list_item_1, companyList);
        companySpinner.setAdapter(adapter);
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(AddVehicleActivity.this, companyList.get(i).getName(), Toast.LENGTH_SHORT).show();
                company = companyList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ///////////////////////////////////////////
//        companySpinner = (Spinner) findViewById(R.id.addDriver_company_spinner);
//        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, companyList);
//        companySpinner.setAdapter(myAdapter);
//        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (!(companySpinner.getSelectedItem() == null)) {
//                    Company company = (Company) companySpinner.getSelectedItem();
//                    Log.d(TAG, "Selected: Name " + company.getName());
//                    Toast.makeText(AddVehicleActivity.this, "Name: " + company.getName(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        ///////////////////////////////////////////////////////////
//        companySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (!(companySpinner.getSelectedItem() == null)) {
//                    Company company = (Company) companySpinner.getSelectedItem();
//                    Toast.makeText(AddVehicleActivity.this, "Name: " + company.getName(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    private void getCompaniesData() {
        FirebaseDatabase.getInstance().getReference().child("PTA").child("Companies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot Companies: " + dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    companyList.add(snapshot.getValue(Company.class));
                }
                initSpinAdapter();
//                myAdapter.notifyDataSetChanged();
                WaitDialog.closeWaitDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                WaitDialog.closeWaitDialog();
                MessageDialog.showDialogMessage("Error", "" + databaseError.getMessage(), AddVehicleActivity.this);
            }
        });
    }


    private void initViews() {
        mMake = findViewById(R.id.addVehicle_make);
        mModel = findViewById(R.id.addVehicle_model);
        mColor = findViewById(R.id.addVehicle_color);
        mPlateNo = findViewById(R.id.addVehicle_plate_no);
        mStartPoint = findViewById(R.id.addDriver_start_point);
        mEndPoint = findViewById(R.id.addDriver_end_point);
        addVehicleBtn = findViewById(R.id.addVehicle_btn);
        vehicleImage = findViewById(R.id.addDriver_vehicle_img);
        startClick = findViewById(R.id.addDriver_start_click);
        endClick = findViewById(R.id.addDriver_end_click);
        companySpinner = findViewById(R.id.addDriver_company_spinner);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addVehicle_btn:
                Log.d(TAG, "Add");
                if (NetworkUtil.isConnected(AddVehicleActivity.this)) {
                    final String vMake = mMake.getText().toString();
                    final String vModel = mModel.getText().toString();
                    final String vColor = mColor.getText().toString();
                    final String plateNumber = mPlateNo.getText().toString();
                    if (isValidate(vMake, vModel, vColor, plateNumber, mStartLocation, mEndLocation, carfilePath, company)) {
                        WaitDialog.showWaitDialog("Adding...", AddVehicleActivity.this);
                        final LatLng startlatlng = mStartLocation.getLatLng();
                        final String startName = String.valueOf(mStartLocation.getAddress());
                        final LatLng endlatlng = mEndLocation.getLatLng();
                        final String endName = String.valueOf(mEndLocation.getAddress());
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = database.child("PTA").child("Vehicles");
                        Query mVehicleQuery = ref.orderByChild("mPlateNumber").equalTo(plateNumber);
                        mVehicleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    final String vehicleImagePushKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                    StorageReference reference = FirebaseStorage.getInstance().getReference().child("vehicles/" + vehicleImagePushKey);
                                    UploadTask uploadTask = reference.putFile(carfilePath);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata() != null) {
//                                                vehicleImageUrl = String.valueOf(taskSnapshot.getMetadata().getDownloadUrl());
//                                                Vehicle vehicle = new Vehicle(plateNumber, engineNumber, vehicleImageUrl, String.valueOf(startlatlng.latitude), String.valueOf(startlatlng.longitude), startName, String.valueOf(endlatlng.latitude), String.valueOf(endlatlng.longitude), endName, owner.getPushKey(), driverPushKey);
                                                Vehicle vehicle = new Vehicle(vMake, vModel, vColor, plateNumber, String.valueOf(startlatlng.latitude), String.valueOf(startlatlng.longitude), startName, String.valueOf(endlatlng.latitude), String.valueOf(endlatlng.longitude), endName, vehicleImageUrl, owner.getPushKey(), company.getPushKey());
                                                FirebaseDatabase.getInstance().getReference().child("PTA").child("Vehicles").child(plateNumber).setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        WaitDialog.closeWaitDialog();
                                                        Toast.makeText(AddVehicleActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                        AddVehicleActivity.this.finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        WaitDialog.closeWaitDialog();
                                                        e.printStackTrace();
                                                        Log.d("OwnerActivity", "" + e.getMessage());
                                                        MessageDialog.showDialogMessage("Error", "" + e.getMessage(), AddVehicleActivity.this);
                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            WaitDialog.closeWaitDialog();
                                            e.printStackTrace();
                                            Log.d("OwnerActivity", "" + e.getMessage());
                                            MessageDialog.showDialogMessage("Error", "" + e.getMessage(), AddVehicleActivity.this);
                                        }
                                    });
                                } else {
                                    WaitDialog.closeWaitDialog();
                                    MessageDialog.showDialogMessage("Error", "Vehicle number must be unique !", AddVehicleActivity.this);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                WaitDialog.closeWaitDialog();
                                MessageDialog.showDialogMessage("Error", "" + databaseError.getMessage(), AddVehicleActivity.this);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Network Error !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.addDriver_start_click:
                Log.d(TAG, "Start");
                searchPlacesIntent(SELECT_START_POINT);
                break;
            case R.id.addDriver_end_click:
                Log.d(TAG, "End");
                searchPlacesIntent(SELECT_END_POINT);
                break;
            case R.id.addDriver_vehicle_img:
//                Toast.makeText(this, "Need to be implemented", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    checkAndRequestPermissions();
                } else {
                    chooseImage(VEHICLE_GALLERY_CODE, VEHICLE_CAMERA_CODE);
                }
                break;
        }
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
                    //                        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN)
//                                .setBoundsBias(new LatLngBounds(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude())))
//                                .build(AddVehicleActivity.this);
//                        startActivityForResult(intent, selectPoint);
                }
            }
        }
    }

    private void chooseImage(final int gallery, final int camera) {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request);
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

    private boolean isValidate(String make, String modle, String color, String plateTxt, Place start, Place end, Uri carfilePath, Company companyObj) {
        if (make.equals("")) {
            mMake.requestFocus();
            MessageDialog.showDialogMessage("Error", "Data must be required !", AddVehicleActivity.this);
            return false;
        } else if (modle.equals("")) {
            mModel.requestFocus();
            MessageDialog.showDialogMessage("Error", "Model must be define !", AddVehicleActivity.this);
            return false;
        } else if (color.equals("")) {
            mColor.requestFocus();
            MessageDialog.showDialogMessage("Error", "Model must be define !", AddVehicleActivity.this);
            return false;
        } else if (plateTxt.equals("")) {
            mPlateNo.requestFocus();
            MessageDialog.showDialogMessage("Error", "Plate number must be define !", AddVehicleActivity.this);
            return false;
        } else if (plateTxt.length() != 8) {
            mPlateNo.requestFocus();
            MessageDialog.showDialogMessage("Error", "Invalid plate number !", AddVehicleActivity.this);
            return false;
        } else if (start == null) {
            MessageDialog.showDialogMessage("Error", "Start point must be define !", AddVehicleActivity.this);
            return false;
        } else if (end == null) {
            MessageDialog.showDialogMessage("Error", "End point must be define !", AddVehicleActivity.this);
            return false;
        } else if (carfilePath == null) {
            MessageDialog.showDialogMessage("Error", "Please select vehicle image !", AddVehicleActivity.this);
            return false;
        }else if (companyObj == null){
            MessageDialog.showDialogMessage("Error", "Please select a company !", AddVehicleActivity.this);
            return false;
        }
//        else if (spinnerResult == null) {
//            MessageDialog.showDialogMessage("Error", "Please select a company !", AddDriverActivity.this);
//            return false;
//        }
        else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    mStartLocation = Autocomplete.getPlaceFromIntent(data);
                    mStartPoint.setText(mStartLocation.getAddress());
                }
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    mEndLocation = Autocomplete.getPlaceFromIntent(data);
                    mEndPoint.setText(mEndLocation.getAddress());
                }
                break;
            case VEHICLE_GALLERY_CODE:
                if (resultCode == Activity.RESULT_OK && (data != null && data.getData() != null)) {
                    carfilePath = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), carfilePath);
                        vehicleImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case VEHICLE_CAMERA_CODE:
                if (resultCode == Activity.RESULT_OK && (data != null && data.getExtras() != null)) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        carfilePath = getImageUri(AddVehicleActivity.this, bitmap);
                        vehicleImage.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddVehicleActivity.this);
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

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, null, null);
        return Uri.parse(path);
    }

//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        switch (view.getId()) {
//            case R.id.addDriver_company_spinner:
//                spinnerResult = adapterView.getItemAtPosition(i).toString();
//                break;
//        }
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }

//    private void startCropImageActivity(Uri imageUri) {
//        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMultiTouchEnabled(true)
//                .start(this);
//    }
}
