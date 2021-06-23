package com.backgroundservice;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 100;
    TextView latitude, longitude, address, area, locality;
  //  Button getLocation;
    Geocoder geocoder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean permission;
    Double latitudeValue,longitudeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);
        area = findViewById(R.id.area);
        locality = findViewById(R.id.location);
       // getLocation = findViewById(R.id.getLocation);
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        startService(new Intent(MainActivity.this,LocationService.class));

//        getLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("Button Clicked","ButtonClicked");
//                if (permission) {
//                  //  if (sharedPreferences.getString("service", "").matches("")) {
////                        editor.putString("service", "service").commit();
//                        Log.d("Starting Service","Starting Service");
//                        Intent intent = new Intent(MainActivity.this, LocationService.class);
//                        startService(intent);
//
////                    } else {
////                        Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
////                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        Log.d("Service Started","Service Started");
        getPermission();
    }
    private void getPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                        },REQUEST_PERMISSIONS);
            }
        } else {
            Log.d("Permission","Permission Granted");
            permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadCast","In BroadCast Receiver");
            latitudeValue = Double.valueOf(intent.getStringExtra("latitude"));
            longitudeValue = Double.valueOf(intent.getStringExtra("longitude"));
            Log.d("latitude","latitude"+latitudeValue);
            Log.d("longitude","longitude"+longitudeValue);
            latitude.setText("Latitude : "+intent.getStringExtra("latitude"));
            longitude.setText("Longitude : "+intent.getStringExtra("longitude"));

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitudeValue, longitudeValue, 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);

                area.setText("Address : "+cityName);
                locality.setText(stateName);
                address.setText(countryName);


                //Log.d("area",cityName);
//                Log.d("locality",stateName);
              //  Log.d("country",countryName);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
//            latitude.setText(latitude+"");
//            longitude.setText(longitude+"");
//            address.getText();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(LocationService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}

