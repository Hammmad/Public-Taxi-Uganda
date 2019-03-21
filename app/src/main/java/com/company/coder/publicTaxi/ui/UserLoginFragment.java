package com.company.coder.publicTaxi.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.activity.ChangePasswordActivity;
import com.company.coder.publicTaxi.activity.MainActivity;
import com.company.coder.publicTaxi.activity.UserActivity;
import com.company.coder.publicTaxi.activity.UserHomeActivity;
import com.company.coder.publicTaxi.modles.User;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class UserLoginFragment extends Fragment implements View.OnClickListener {


    private static String TAG = "login_user_screen";
    private MaterialEditText userName, userPass;
    private Button userLogin;
    private TextView userSignUp, userChangePassword;

    public UserLoginFragment() {
        // Required empty public constructor
    }

    public static UserLoginFragment newInstance() {
        return new UserLoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);
        initViews(view);
        userSignUp.setOnClickListener(this);
        userLogin.setOnClickListener(this);
        userChangePassword.setOnClickListener(this);
        return view;
    }

    private void initViews(View view) {
        userName = view.findViewById(R.id.user_login_email);
        userPass = view.findViewById(R.id.user_login_password);
        userLogin = view.findViewById(R.id.user_login_btn);
        userSignUp = view.findViewById(R.id.user_create_link);
        userChangePassword = view.findViewById(R.id.user_change_password_link);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_login_btn:
                if (NetworkUtil.isConnected(getContext())) {
                    final String name = userName.getText().toString();
                    final String password = userPass.getText().toString();
                    if (!name.equals("") && !password.equals("")) {
                        WaitDialog.showWaitDialog("Wait...", getContext());
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = database.child("PTA").child("Users");
                        Query loginUser = ref.orderByChild("name").equalTo(name);
                        loginUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                WaitDialog.closeWaitDialog();
//                                String usersObj = String.valueOf(dataSnapshot.child("Users").getValue());
                                Log.d(TAG, "onDataChang: " + dataSnapshot.toString());
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        User user = child.getValue(User.class);
//                                        Log.d(TAG, "onDataChange: " + child.toString());
//                                        Log.d(TAG, "onDataChange: " + child.child("name").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("contact").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("password").getValue());
//                                        Log.d(TAG, "onDataChange: " + child.child("pushKey").getValue());
//                                        String name = String.valueOf(child.child("name").getValue());
//                                        String contact = String.valueOf(child.child("contact").getValue());
//                                        String pass = String.valueOf(child.child("password").getValue());
//                                        String pushKey = String.valueOf(child.child("pushKey").getValue());
                                        if (user != null && user.getPassword().equals(password)) {
//                                            MessageDialog.showDialogMessage("Succes", "", getContext());
                                            if (getActivity() != null) {
                                                SharedPref.setCurrentUser(getContext(), user);
                                                SharedPref.setCurrentUserType(getContext(), Constants.TYPE_USER);
                                                Intent intent = new Intent(getContext(), UserHomeActivity.class);
                                                getActivity().startActivity(intent);
                                                UserActivity.getInstance().finish();
                                                MainActivity.getInstance().finish();
                                            }
                                        } else {
                                            MessageDialog.showDialogMessage("Login Error", "Password not matched !", getContext());
                                        }
                                    }
                                } else {
                                    MessageDialog.showDialogMessage("Error", "User not found !", getContext());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                WaitDialog.closeWaitDialog();
                                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                                MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), getContext());
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "User name & pass must be required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MessageDialog.showDialogMessage("Error", "Required active network !", getContext());
                }
                break;
            case R.id.user_create_link:
                replaceFragment(UserSignUpFragment.newInstance());
                break;
            case R.id.user_change_password_link:
                final String name = userName.getText().toString();
                if (!name.equals("")) {
                    WaitDialog.showWaitDialog("Wait...", getContext());
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference ref = database.child("PTA").child("Users");
                    Query checkUserQuery = ref.orderByChild("name").equalTo(name);
                    checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            WaitDialog.closeWaitDialog();
                            if (dataSnapshot.exists()) {
                                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                                intent.putExtra("userType", "0");
                                intent.putExtra("name", name);
                                if (getActivity() != null) {
                                    userName.setText("");
                                    userPass.setText("");
                                    getActivity().startActivity(intent);
                                }
                            } else {
                                MessageDialog.showDialogMessage("Error", "User not found !", getContext());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            WaitDialog.closeWaitDialog();
                            Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                            MessageDialog.showDialogMessage("onCancelled", databaseError.getMessage(), getContext());
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "Name Required !", Toast.LENGTH_SHORT).show();
                }
//                getActivity().startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;
        }
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.user_activity_container, newFragment).commit();
    }

}
