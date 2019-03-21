package com.company.coder.publicTaxi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Driver;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.service.LocationUpdateService;
import com.company.coder.publicTaxi.ui.dialogs.MessageDialog;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.NetworkUtil;
import com.company.coder.publicTaxi.utils.ServiceCheck;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.company.coder.publicTaxi.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class DriverActivity extends Activity implements View.OnClickListener {

    private static String TAG = "login_driver_screen";
    private MaterialEditText driverNumber, driverPass;
    private Button driverLogin;
    private TextView changePassLink;
    private Driver driverObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initViews();
        driverLogin.setOnClickListener(this);
        changePassLink.setOnClickListener(this);
    }

    private void initViews() {
        driverNumber = findViewById(R.id.driver_login_email);
        driverPass = findViewById(R.id.driver_login_password);
        driverLogin = findViewById(R.id.driver_login_btn);
        changePassLink = findViewById(R.id.driver_change_password_link);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver_login_btn:
                if (NetworkUtil.isConnected(DriverActivity.this)) {
                    final String name = driverNumber.getText().toString();
                    final String password = driverPass.getText().toString();
                    if (!name.equals("") && !password.equals("")) {
                        WaitDialog.showWaitDialog("Wait...", DriverActivity.this);
                        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = database.child("PTA").child("Drivers");
                        Query loginUser = ref.orderByChild("vehicleUUID").equalTo(name);
                        loginUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, "onDataChang: " + dataSnapshot.toString());
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        Log.d(TAG, "" + child.child("lat").getValue());
                                        Log.d(TAG, "" + child.child("lng").getValue());
//                                        Driver driver = new Driver(String.valueOf(child.child("name").getValue()), String.valueOf(child.child("contact").getValue()), String.valueOf(child.child("image").getValue()), String.valueOf(child.child("password").getValue()),String.valueOf(child.child("vehicleUUID").getValue()), String.valueOf(child.child("lat").getValue()), String.valueOf(child.child("lng").getValue()),String.valueOf(child.child("pushKey").getValue()));
                                        Driver driver = child.getValue(Driver.class);
                                        if (driver != null && driver.getPassword().equals(password)) {
                                            driverObj = driver;
                                            DatabaseReference ref = database.child("PTA").child("Vehicles");
                                            Query vehicleQuery = ref.orderByChild("mPlateNumber").equalTo(driverObj.getVehicleUUID());
                                            vehicleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    WaitDialog.closeWaitDialog();
                                                    Log.d(TAG, "onDataChang: " + dataSnapshot.toString());
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                            Vehicle vehicle = child.getValue(Vehicle.class);
                                                            if (vehicle != null && (vehicle.getmEndLat().equals("0") && vehicle.getmEndLng().equals("0")) && (vehicle.getmStartLat().equals("0") && vehicle.getmStartLng().equals("0"))) {
                                                                Intent intent = new Intent(DriverActivity.this, DriverChooseRouteActivity.class);
                                                                Bundle bundle = new Bundle();
                                                                bundle.putParcelable("driverBundle", driverObj);
                                                                bundle.putParcelable("vehicleBundle", vehicle);
                                                                intent.putExtras(bundle);
                                                                DriverActivity.this.startActivity(intent);
                                                                DriverActivity.this.finish();
                                                            } else {
                                                                launchDriver(driverObj, vehicle);
                                                            }
                                                        }
                                                    } else {
                                                        MessageDialog.showDialogMessage("Error", "Failed to get vehicle !", DriverActivity.this);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    WaitDialog.closeWaitDialog();
                                                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                                                    MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), DriverActivity.this);
                                                }
                                            });
                                        } else {
                                            WaitDialog.closeWaitDialog();
                                            MessageDialog.showDialogMessage("Login Error", "Password not matched !", DriverActivity.this);
                                        }
                                    }
                                } else {
                                    WaitDialog.closeWaitDialog();
                                    MessageDialog.showDialogMessage("Error", "Driver not found !", DriverActivity.this);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                WaitDialog.closeWaitDialog();
                                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                                MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), DriverActivity.this);
                            }
                        });

                    } else {
                        Toast.makeText(DriverActivity.this, "Vehicle number & pass must be required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MessageDialog.showDialogMessage("Error", "Required active network !", DriverActivity.this);
                }
                break;
            case R.id.driver_change_password_link:
                final String name = driverNumber.getText().toString();
                if (!name.equals("")) {
                    WaitDialog.showWaitDialog("Wait...", DriverActivity.this);
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference ref = database.child("PTA").child("Drivers");
                    Query checkDriverQuery = ref.orderByChild("vehicleUUID").equalTo(name);
                    checkDriverQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            WaitDialog.closeWaitDialog();
                            if (dataSnapshot.exists()) {
                                Intent intent = new Intent(DriverActivity.this, ChangePasswordActivity.class);
                                intent.putExtra("userType", "2");
                                intent.putExtra("name", name);
                                driverNumber.setText("");
                                driverPass.setText("");
                                DriverActivity.this.startActivity(intent);
                            } else {
                                MessageDialog.showDialogMessage("Error", "Data not found !", DriverActivity.this);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            WaitDialog.closeWaitDialog();
                            Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                            MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), DriverActivity.this);
                        }
                    });
                } else {
                    Toast.makeText(this, "Vehicle number Required !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void launchDriver(Driver driverObj, Vehicle vehicle) {
        SharedPref.setCurrentDriver(DriverActivity.this, driverObj);
        SharedPref.setCurrentVehicle(DriverActivity.this, vehicle);
        SharedPref.setCurrentUserType(DriverActivity.this, Constants.TYPE_DRIVER);
        if (!ServiceCheck.isServiceRunning(LocationUpdateService.class, DriverActivity.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DriverActivity.this.startForegroundService(new Intent(DriverActivity.this, LocationUpdateService.class));
            } else {
                DriverActivity.this.startService(new Intent(DriverActivity.this, LocationUpdateService.class));
            }

        }
        Intent intent = new Intent(DriverActivity.this, DriverHomeActivity.class);
        DriverActivity.this.startActivity(intent);
        MainActivity.getInstance().finish();
    }
}
