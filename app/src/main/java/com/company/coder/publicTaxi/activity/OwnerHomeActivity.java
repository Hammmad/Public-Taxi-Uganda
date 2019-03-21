package com.company.coder.publicTaxi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.adapter.VehicleAdapter;
import com.company.coder.publicTaxi.modles.Owner;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.ui.dialogs.MessageDialog;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class OwnerHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OwnerHomeScreen";
    private RecyclerView recyclerView;
    private FloatingActionButton addDriverFab;
    private List<Vehicle> listItems;
    private List<String> mKeys;
    private VehicleAdapter adapter;
    android.support.v7.widget.Toolbar toolbar = null;
    private Owner owner = null;
    private LocationManager manager = null;
    private TextView alertText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.owner_home_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.owner_action_map:
                Intent intent = new Intent(OwnerHomeActivity.this, OwnerMapActivity.class);
                OwnerHomeActivity.this.startActivity(intent);
                return true;
            case R.id.owner_action_support:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Select an action");
                alertDialog.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + "0701074727"));
                        if (ActivityCompat.checkSelfPermission(OwnerHomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        OwnerHomeActivity.this.startActivity(intent);
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
            case R.id.owner_action_logout:
                SharedPref.clearAllData(OwnerHomeActivity.this);
                OwnerHomeActivity.this.startActivity(new Intent(OwnerHomeActivity.this, MainActivity.class));
                OwnerHomeActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setToolBars();
        owner = SharedPref.getCurrentOwner(this);
        alertText = findViewById(R.id.owner_home_alert_text);
        alertText.setText(owner.getName() + ", You have not added vehicles yet !");
        recyclerView = findViewById(R.id.drivers_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addDriverFab = findViewById(R.id.addDrivers_fab);
        addDriverFab.setOnClickListener(this);
        listItems = new ArrayList<>();
        mKeys = new ArrayList<>();
        getVehicleData();
        initializedAdapter();
        setSupportActionBar(toolbar);
        // Check if gps is enabled
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void setToolBars() {
        toolbar = findViewById(R.id.owner_home_toolbar);
        toolbar.setTitle("My Vehicles");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
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

    private void initializedAdapter() {
        adapter = new VehicleAdapter(listItems, this, new VehicleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Vehicle item) {
                Intent intent = new Intent(OwnerHomeActivity.this, VehicleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("driverObj", item);
                intent.putExtras(bundle);
                OwnerHomeActivity.this.startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getVehicleData() {
        WaitDialog.showWaitDialog("Wait...", OwnerHomeActivity.this);
        Log.d(TAG, "Owner UUID: " + owner.getPushKey());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("PTA").child("Vehicles");
        Query driversQuery = ref.orderByChild("mOwnerUuid").equalTo(owner.getPushKey());
        driversQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.toString());
                WaitDialog.closeWaitDialog();
                if (dataSnapshot.getChildrenCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    alertText.setVisibility(View.VISIBLE);
                } else {
//                    if (dataSnapshot.exists()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    alertText.setVisibility(View.GONE);
                    String key = dataSnapshot.getKey();
                    if (!mKeys.contains(key)) {
                        Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                        if (vehicle != null && (!vehicle.getmEndLat().equals("0") && !vehicle.getmEndLng().equals("0")) && (!vehicle.getmStartLat().equals("0") && !vehicle.getmStartLng().equals("0"))) {
                            listItems.add(vehicle);
                            mKeys.add(key);
                            adapter.notifyDataSetChanged();
//                            if (listItems.size() < 1){
//                                MessageDialog.showDialogMessage("Error", "Your vehicles have insufficient data !", OwnerHomeActivity.this);
//                            }
                        }
                    }
//                    } else {
//                        WaitDialog.closeWaitDialog();
//                        MessageDialog.showDialogMessage("Error", "No Data !", OwnerHomeActivity.this);
//                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot.toString());
                String key = dataSnapshot.getKey();
                if (mKeys.contains(key)) {
                    int index = mKeys.indexOf(key);
                    Vehicle oldItem = listItems.get(index);
                    Vehicle newItem = dataSnapshot.getValue(Vehicle.class);
                    listItems.set(index, newItem);
                    adapter.notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: " + dataSnapshot.toString());
                String key = dataSnapshot.getKey();
                if (mKeys.contains(key)) {
                    int index = mKeys.indexOf(key);
                    listItems.remove(index);
                    adapter.notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: " + dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
                Log.d(TAG, "" + databaseError.getMessage());
                WaitDialog.closeWaitDialog();
                MessageDialog.showDialogMessage("Error", "" + databaseError.getMessage(), OwnerHomeActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addDrivers_fab:
                OwnerHomeActivity.this.startActivity(new Intent(OwnerHomeActivity.this, AddVehicleActivity.class));
                break;
        }
    }

    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(OwnerHomeActivity.this);
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
}
