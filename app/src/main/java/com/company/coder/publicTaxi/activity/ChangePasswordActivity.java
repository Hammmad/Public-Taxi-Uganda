package com.company.coder.publicTaxi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Driver;
import com.company.coder.publicTaxi.modles.Owner;
import com.company.coder.publicTaxi.modles.User;
import com.company.coder.publicTaxi.ui.dialogs.MessageDialog;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "change_password_screen";
    private MaterialEditText currentPass, newPass, cunformPass;
    private Button saveBtn;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ChangePasswordActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initViews();
        saveBtn.setOnClickListener(this);
    }

    private void initViews() {
        currentPass = findViewById(R.id.change_password_current);
        newPass = findViewById(R.id.change_password_new);
        cunformPass = findViewById(R.id.change_password_conform);
        saveBtn = findViewById(R.id.change_password_btn);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_password_btn:
                String currentPassTxt = currentPass.getText().toString();
                String newPassTxt = newPass.getText().toString();
                String cunformPassTxt = cunformPass.getText().toString();
                if (isValidate(currentPassTxt, newPassTxt, cunformPassTxt)) {
                    if (getIntent().hasExtra("userType") && getIntent().hasExtra("name")) {
                        String userType = getIntent().getStringExtra("userType");
                        String userName = getIntent().getStringExtra("name");
                        switch (userType) {
                            case "0":
                                changeUserData(userName, currentPassTxt, newPassTxt);
                                break;
                            case "1":
                                changeOwnerData(userName, currentPassTxt, newPassTxt);
                                break;
                            case "2":
                                changeDriverData(userName, currentPassTxt, newPassTxt);
                                break;
                        }
                    }
                } else {
                    Toast.makeText(this, "Please fill the required field !", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void changeDriverData(String userName, final String currentPassTxt, final String newPassTxt) {
        WaitDialog.showWaitDialog("Wait...", ChangePasswordActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("PTA").child("Drivers");
        Query checkDriverQuery = ref.orderByChild("vehicleUUID").equalTo(userName);
        checkDriverQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WaitDialog.closeWaitDialog();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Driver driver = child.getValue(Driver.class);
                        if (driver != null && driver.getPassword().equals(currentPassTxt)){
                            Driver updatedDriver = new Driver(driver.getName(), driver.getContact(), driver.getImage(), newPassTxt, driver.getVehicleUUID(), driver.getLat(), driver.getLng(), driver.getPushKey(), driver.getOwnerUUID());
                            FirebaseDatabase.getInstance().getReference().child("PTA").child("Owners").child(driver.getPushKey()).setValue(updatedDriver);
                            Toast.makeText(ChangePasswordActivity.this, "Successfully updated !", Toast.LENGTH_SHORT).show();
                            ChangePasswordActivity.this.finish();
                        }else {
                            MessageDialog.showDialogMessage("Error", "Current password not matched !", ChangePasswordActivity.this);
                        }
                    }
                } else {
                    MessageDialog.showDialogMessage("Error", "User not found !", ChangePasswordActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                WaitDialog.closeWaitDialog();
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), ChangePasswordActivity.this);
            }
        });
    }

    private void changeOwnerData(String userName, final String currentPassTxt, final String newPassTxt) {
        WaitDialog.showWaitDialog("Wait...", ChangePasswordActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("PTA").child("Owners");
        Query checkOwnerQuery = ref.orderByChild("name").equalTo(userName);
        checkOwnerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WaitDialog.closeWaitDialog();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Owner owner = child.getValue(Owner.class);
                        if (owner != null && owner.getPassword().equals(currentPassTxt)) {
                            Owner updatedOwner = new Owner(owner.getName(), owner.getPhone(), owner.getAddres(), newPassTxt, owner.getImageUrl(), owner.getPushKey());
                            FirebaseDatabase.getInstance().getReference().child("PTA").child("Owners").child(owner.getPushKey()).setValue(updatedOwner);
                            Toast.makeText(ChangePasswordActivity.this, "Successfully updated !", Toast.LENGTH_SHORT).show();
                            ChangePasswordActivity.this.finish();
                        } else {
                            MessageDialog.showDialogMessage("Error", "Current password not matched !", ChangePasswordActivity.this);
                        }
                    }
                } else {
                    MessageDialog.showDialogMessage("Error", "User not found !", ChangePasswordActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                WaitDialog.closeWaitDialog();
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), ChangePasswordActivity.this);
            }
        });
    }

    private void changeUserData(String userName, final String currentPassTxt, final String newPassTxt) {
        WaitDialog.showWaitDialog("Wait...", ChangePasswordActivity.this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("PTA").child("Users");
        Query checkUserQuery = ref.orderByChild("name").equalTo(userName);
        checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                WaitDialog.closeWaitDialog();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null && user.getPassword().equals(currentPassTxt)) {
                            User updatedUser = new User(user.getName(), user.getContact(), user.getPushKey(), user.getLat(), user.getLng(), newPassTxt);
                            FirebaseDatabase.getInstance().getReference().child("PTA").child("Users").child(user.getPushKey()).setValue(updatedUser);
                            Toast.makeText(ChangePasswordActivity.this, "Successfully updated !", Toast.LENGTH_SHORT).show();
                            ChangePasswordActivity.this.finish();
                        } else {
                            MessageDialog.showDialogMessage("Error", "Current password not matched !", ChangePasswordActivity.this);
                        }
                    }
                } else {
                    MessageDialog.showDialogMessage("Error", "User not found !", ChangePasswordActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                WaitDialog.closeWaitDialog();
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), ChangePasswordActivity.this);
            }
        });
    }

    private boolean isValidate(String currentPassTxt, String passTxt, String cunPassTxt) {
        if (currentPassTxt.equals("")) {
            cunformPass.requestFocus();
            MessageDialog.showDialogMessage("Error", "Please provide your current password !", ChangePasswordActivity.this);
            return false;
        } else if (passTxt.equals("")) {
            newPass.requestFocus();
            MessageDialog.showDialogMessage("Error", "Password must be provided !", ChangePasswordActivity.this);
            return false;
        } else if (!passTxt.equals(cunPassTxt) || cunPassTxt.equals("0")) {
            cunformPass.requestFocus();
            MessageDialog.showDialogMessage("Error", "Password not matched !", ChangePasswordActivity.this);
            return false;
        } else {
            return true;
        }
    }
}
