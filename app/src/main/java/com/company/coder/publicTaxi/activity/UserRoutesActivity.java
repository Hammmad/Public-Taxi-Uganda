package com.company.coder.publicTaxi.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.adapter.UserRoutesAdapter;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.company.coder.publicTaxi.ui.dialogs.WaitDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRoutesActivity extends AppCompatActivity {

    private static final String TAG = "UserRoutesScreen";
    private RecyclerView recyclerView;
    private List<Vehicle> listItems;
    private UserRoutesAdapter adapter;
    private TextView alertText;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UserRoutesActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_routes);
        setToolBars();
        alertText = findViewById(R.id.user_routes_alert_text);
        recyclerView = findViewById(R.id.user_routes_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        getVehiclesData();
        initializedAdapter();
    }

    private void initializedAdapter() {
        adapter = new UserRoutesAdapter(listItems, this, new UserRoutesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Vehicle item) {

            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getVehiclesData() {
        WaitDialog.showWaitDialog("Loading...", UserRoutesActivity.this);
        FirebaseDatabase.getInstance().getReference().child("PTA").child("Vehicles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "" + dataSnapshot);
                if (dataSnapshot.getChildrenCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    alertText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    alertText.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Vehicle vehicle = snapshot.getValue(Vehicle.class);
                        if (vehicle != null && (!vehicle.getmEndLat().equals("0") && !vehicle.getmEndLng().equals("0")) && (!vehicle.getmStartLat().equals("0") && !vehicle.getmStartLng().equals("0"))) {
                            listItems.add(snapshot.getValue(Vehicle.class));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                WaitDialog.closeWaitDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "" + databaseError.getMessage());
                WaitDialog.closeWaitDialog();
            }
        });
    }

    private void setToolBars() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.user_routes_toolbar);
        toolbar.setTitle("Routes");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable upArrow = getResources().getDrawable(R.drawable.back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
