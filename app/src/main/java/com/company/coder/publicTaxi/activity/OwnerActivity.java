package com.company.coder.publicTaxi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Owner;
import com.company.coder.publicTaxi.ui.dialogs.MessageDialog;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.NetworkUtil;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.company.coder.publicTaxi.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class OwnerActivity extends Activity implements View.OnClickListener {

    private static String TAG = "login_owner_screen";
    private MaterialEditText nameView, passwordView;
    private Button loginBtn;
    private TextView changePassLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        initViews();
        loginBtn.setOnClickListener(this);
        changePassLink.setOnClickListener(this);
    }

    private void initViews() {
        nameView = findViewById(R.id.owner_login_email);
        passwordView = findViewById(R.id.owner_login_password);
        loginBtn = findViewById(R.id.owner_login_btn);
        changePassLink = findViewById(R.id.owner_change_password_link);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.owner_login_btn:
                if (NetworkUtil.isConnected(OwnerActivity.this)) {
                    final String name = nameView.getText().toString();
                    final String password = passwordView.getText().toString();
                    if (!name.equals("") && !password.equals("")) {
                        WaitDialog.showWaitDialog("Wait...", OwnerActivity.this);
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = database.child("PTA").child("Owners");
                        Query loginOwner = ref.orderByChild("name").equalTo(name);
                        loginOwner.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                WaitDialog.closeWaitDialog();
//                                String usersObj = String.valueOf(dataSnapshot.child("Users").getValue());
                                Log.d(TAG, "onDataChang: " + dataSnapshot.toString());
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        Owner owner = child.getValue(Owner.class);
//                                        Log.d(TAG, "onDataChange: " + child.toString());
//                                        Log.d(TAG, "onDataChange: " + child.child("addres").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("imageUrl").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("name").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("password").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("phone").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("pushKey").getValue());
//                                        String address = String.valueOf(child.child("addres").getValue());
//                                        String url = String.valueOf(child.child("imageUrl").getValue());
//                                        String name = String.valueOf(child.child("name").getValue());
//                                        String pass = String.valueOf(child.child("password").getValue());
//                                        String contact = String.valueOf(child.child("phone").getValue());
//                                        String pushKey = String.valueOf(child.child("pushKey").getValue());
                                        if (owner != null && owner.getPassword().equals(password)) {
                                            SharedPref.setCurrentOwner(OwnerActivity.this, owner);
                                            SharedPref.setCurrentUserType(OwnerActivity.this, Constants.TYPE_OWNER);
                                            Intent intent = new Intent(OwnerActivity.this, OwnerHomeActivity.class);
                                            OwnerActivity.this.startActivity(intent);
                                            OwnerActivity.this.finish();
                                            MainActivity.getInstance().finish();
                                        } else {
                                            MessageDialog.showDialogMessage("Login Error", "Password not matched !", OwnerActivity.this);
                                        }
                                    }
                                } else {
                                    MessageDialog.showDialogMessage("Error", "User not found !", OwnerActivity.this);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                WaitDialog.closeWaitDialog();
                                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                                MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), OwnerActivity.this);
                            }
                        });

                    } else {
                        Toast.makeText(OwnerActivity.this, "User name & pass must be required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MessageDialog.showDialogMessage("Error", "Required active network !", OwnerActivity.this);
                }
                break;

            case R.id.owner_change_password_link:
                final String name = nameView.getText().toString();
                if (!name.equals("")) {
                    WaitDialog.showWaitDialog("Wait...", OwnerActivity.this);
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference ref = database.child("PTA").child("Owners");
                    Query checkOwnerQuery = ref.orderByChild("name").equalTo(name);
                    checkOwnerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            WaitDialog.closeWaitDialog();
                            if (dataSnapshot.exists()) {
                                Intent intent = new Intent(OwnerActivity.this, ChangePasswordActivity.class);
                                intent.putExtra("userType", "1");
                                intent.putExtra("name", name);
                                nameView.setText("");
                                passwordView.setText("");
                                OwnerActivity.this.startActivity(intent);
                            } else {
                                MessageDialog.showDialogMessage("Error", "Data not found !", OwnerActivity.this);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            WaitDialog.closeWaitDialog();
                            Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                            MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), OwnerActivity.this);
                        }
                    });
                } else {
                    Toast.makeText(this, "Name Required !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
