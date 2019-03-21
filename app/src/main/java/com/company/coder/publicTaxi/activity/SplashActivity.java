package com.company.coder.publicTaxi.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.service.LocationUpdateService;
import com.company.coder.publicTaxi.utils.ServiceCheck;
import com.company.coder.publicTaxi.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static String TAG = "splash_screen_tag";
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 111;
    List<String> listPermissionsNeeded;
    private String userType = null;
    private static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        userType = SharedPref.getCurrentUserType(SplashActivity.this);

//        String name = "Qasim";
//
//        String result = "";
//        for (int i = name.length() - 1; i >= 0; i--) {
//            result = result + name.charAt(i);
//        }
//        Log.d(TAG, result);

        if (Build.VERSION.SDK_INT < 23) {
            startSplashProcess();
        } else {
            if (checkAndRequestPermissions()) {
                startSplashProcess();
            }
        }
    }

    private void startSplashProcess() {
        Log.d("Activity_Splash", "Splash Process");
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (userType != null) {
                    switch (userType) {
                        case "0":
                            SplashActivity.this.startActivity(new Intent(SplashActivity.this, UserHomeActivity.class));
                            SplashActivity.this.finish();
                            break;
                        case "1":
                            SplashActivity.this.startActivity(new Intent(SplashActivity.this, OwnerHomeActivity.class));
                            SplashActivity.this.finish();
                            break;
                        case "2":
                            if (!ServiceCheck.isServiceRunning(LocationUpdateService.class, SplashActivity.this)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    SplashActivity.this.startForegroundService(new Intent(SplashActivity.this, LocationUpdateService.class));
                                } else {
                                    SplashActivity.this.startService(new Intent(SplashActivity.this, LocationUpdateService.class));
                                }
                            }
                            SplashActivity.this.startActivity(new Intent(SplashActivity.this, DriverHomeActivity.class));
                            SplashActivity.this.finish();
                            break;
                        default:
                            SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            SplashActivity.this.finish();
                            break;
                    }
                } else {
                    SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    SplashActivity.this.finish();
                }
            }
        }, (long) SPLASH_TIME_OUT);
    }

    private boolean checkAndRequestPermissions() {
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

        int callPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);

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
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                for (String s : permissions) {
                    Log.d("Perm", s);
                }
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                    startSplashProcess();
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "Permission Denied. Denying permission may cause it no longer function intended.", Toast.LENGTH_SHORT).show();
                    startSplashProcess();
                }
                break;
        }
    }
}
