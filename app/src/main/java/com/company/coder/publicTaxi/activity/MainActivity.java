package com.company.coder.publicTaxi.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.company.coder.publicTaxi.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button user, driver, owner;

    private static MainActivity mInstance;

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;
        //changes the color of top bar
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        user = findViewById(R.id.home_user_contBtn);
        driver = findViewById(R.id.home_driver_contBtn);
        owner = findViewById(R.id.home_owner_contBtn);
        user.setOnClickListener(this);
        driver.setOnClickListener(this);
        owner.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_user_contBtn:
                MainActivity.this.startActivity(new Intent(MainActivity.this, UserActivity.class));
//                MainActivity.this.finish();
                break;
            case R.id.home_driver_contBtn:
                MainActivity.this.startActivity(new Intent(MainActivity.this, DriverActivity.class));
//                MainActivity.this.finish();
                break;
            case R.id.home_owner_contBtn:
                MainActivity.this.startActivity(new Intent(MainActivity.this, OwnerActivity.class));
//                MainActivity.this.finish();
                break;
        }
    }
}
