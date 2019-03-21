package com.company.coder.publicTaxi.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.activity.MainActivity;
import com.company.coder.publicTaxi.activity.UserActivity;
import com.company.coder.publicTaxi.activity.UserHomeActivity;
import com.company.coder.publicTaxi.modles.User;
import com.company.coder.publicTaxi.ui.dialogs.MessageDialog;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.company.coder.publicTaxi.utils.NetworkUtil;
import com.company.coder.publicTaxi.utils.SharedPref;
import com.company.coder.publicTaxi.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class UserSignUpFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "signup_user_screen";
    private MaterialEditText name, contact, password, cunPassword;
    private Button signUp;

    public UserSignUpFragment() {
        // Required empty public constructor
    }

    public static UserSignUpFragment newInstance() {
        return new UserSignUpFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_sign_up, container, false);
        initViews(view);
        signUp.setOnClickListener(this);
        return view;
    }

    private void initViews(View view) {
        name = view.findViewById(R.id.user_signup_email);
        contact = view.findViewById(R.id.user_signup_contact);
        password = view.findViewById(R.id.user_signup_password);
        cunPassword = view.findViewById(R.id.user_signup_cunform_password);
        signUp = view.findViewById(R.id.user_signup_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_signup_btn:
                if (NetworkUtil.isConnected(getContext())) {
                    final String nameTxt = name.getText().toString();
                    final String contactTxt = contact.getText().toString();
                    final String passwordTxt = password.getText().toString();
                    String cunPasswordTxt = cunPassword.getText().toString();
                    if (isValidate(nameTxt, contactTxt, passwordTxt, cunPasswordTxt)) {
                        WaitDialog.showWaitDialog("Wait...", getContext());
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference ref = database.child("PTA").child("Users");
                        Query mSignUpUser = ref.orderByChild("name").equalTo(nameTxt);
                        mSignUpUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String usersObj = String.valueOf(dataSnapshot.child("Users").getValue());
                                Log.d(TAG, "onDataChange: " + dataSnapshot);
                                if (!dataSnapshot.exists()) {
                                    final String pushKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                    User user = new User(nameTxt, contactTxt, pushKey, "0", "0", passwordTxt);
                                    try {
                                        FirebaseDatabase.getInstance().getReference().child("PTA").child("Users").child(pushKey).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                WaitDialog.closeWaitDialog();
                                                if (getActivity() != null) {
                                                    SharedPref.setCurrentUser(getContext(), new User(nameTxt, contactTxt, pushKey, passwordTxt));
                                                    SharedPref.setCurrentUserType(getContext(), Constants.TYPE_USER);
                                                    Intent intent = new Intent(getContext(), UserHomeActivity.class);
                                                    getActivity().startActivity(intent);
                                                    UserActivity.getInstance().finish();
                                                    MainActivity.getInstance().finish();
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        WaitDialog.closeWaitDialog();
                                        e.printStackTrace();
                                        Log.d("OwnerActivity", "" + e.getMessage());
                                    }
                                } else {
                                    WaitDialog.closeWaitDialog();
                                    MessageDialog.showDialogMessage("Error", "User name must be unique !", getContext());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                WaitDialog.closeWaitDialog();
                                MessageDialog.showDialogMessage("Database Error", "" + databaseError.getMessage(), getContext());
                            }
                        });
                    }
                } else {
                    MessageDialog.showDialogMessage("Error", "Required active network !", getContext());
                }
                break;
        }
    }

    private boolean isValidate(String nameTxt, String contactTxt, String passTxt, String cunPassTxt) {
        if (nameTxt.equals("")) {
            name.requestFocus();
            MessageDialog.showDialogMessage("Error", "Name must be define !", getContext());
            return false;
        } else if (contactTxt.equals("")) {
            contact.requestFocus();
            MessageDialog.showDialogMessage("Error", "Contact must be define !", getContext());
            return false;
        } else if (passTxt == null) {
            password.requestFocus();
            MessageDialog.showDialogMessage("Error", "Password required !", getContext());
            return false;
        } else if (!passTxt.equals(cunPassTxt)) {
            cunPassword.requestFocus();
            MessageDialog.showDialogMessage("Error", "Password not matched !", getContext());
            return false;
        } else {
            return true;
        }
    }
}
