package com.company.coder.publicTaxi.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.ui.UserLoginFragment;

public class UserActivity extends AppCompatActivity {


    private static UserActivity mInstance;

    public static synchronized UserActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mInstance = this;
//        initViews();
        addFragment(UserLoginFragment.newInstance());

    }

    private void addFragment(android.support.v4.app.Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.user_activity_container, newFragment).commit();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            // write your code to switch between fragments.
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
//        int count = getFragmentManager().getBackStackEntryCount();
//        if (count == 0) {
//            super.onBackPressed();
//            //additional code
//        } else {
//            getFragmentManager().popBackStack();
//        }
    }


    private void replaceFragment(android.support.v4.app.Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.user_activity_container, newFragment).commit();
    }
}
