package com.scooter.navigation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.scooter.navigation.R;
import com.scooter.navigation.base.BaseActivity;

import java.util.List;

public class MainActivity extends BaseActivity implements PermissionsListener {
    private PermissionsManager permissionsManager;
    private EditText etLat, etLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etLat = findViewById(R.id.et_lat);
        etLng = findViewById(R.id.et_lng);
        checkLocationPermission();
    }

    public void checkLocationPermission() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    public void navigation(View view) {
        String strLat = etLat.getText().toString().trim();
        String strLng = etLng.getText().toString().trim();
        if (TextUtils.isEmpty(strLat) || TextUtils.isEmpty(strLat)) {
            Toast.makeText(this, "latitude and longitude not empty", Toast.LENGTH_SHORT).show();
            return;
        }
        double lat = Double.parseDouble(strLat);
        double lng = Double.parseDouble(strLng);
        Intent intent = new Intent(this, NavigationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }
}
