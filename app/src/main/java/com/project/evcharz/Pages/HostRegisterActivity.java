package com.project.evcharz.Pages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.evcharz.Model.HostModel;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;

public class HostRegisterActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView latitude,longitude;
    GoogleMap nmap;
    SupportMapFragment supportMapFragment;
    Marker new_marker;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference chargingStationRef,hostListRef;
    EditText address,station_name,unit_rate;
    String loggedUserMbNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_register);
        firebaseDatabase = FirebaseDatabase.getInstance();
        chargingStationRef = firebaseDatabase.getReference("chargingStationDetails");
        hostListRef = firebaseDatabase.getReference("hostUserList");


        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);

        loggedUserMbNumber = sh.getString("loggedUserMbNumber", "");

         latitude = this.findViewById(R.id.latitude);
         longitude = this.findViewById(R.id.longitude);
         station_name = findViewById(R.id.stationName);
         unit_rate = findViewById(R.id.unit_rate);
         address = findViewById(R.id.addresss);
        Button add_btn = findViewById(R.id.btn_register);
        ImageButton back_btn = this.findViewById(R.id.btn_back_id);

        back_btn.setOnClickListener(v->{
            finish();
        });

        add_btn.setOnClickListener(v->{
            Double lat = Double.valueOf(latitude.getText().toString());
            Double longi = Double.valueOf(longitude.getText().toString());
            String place = station_name.getText().toString();
            String unit_rate1 = unit_rate.getText().toString();
            String addresss = address.getText().toString();
            String id = chargingStationRef.push().getKey();

            PlaceModel placeModel = new PlaceModel(id,lat,longi,place,unit_rate1,"","",addresss);
            registerHost(placeModel);
        });


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_for_location);
        supportMapFragment.getMapAsync(this);

    }

    private void registerHost(PlaceModel placeModel) {
        HostModel newHost = new HostModel(placeModel.getStation_id(),placeModel.getPlace_name(),loggedUserMbNumber,
                placeModel.getStation_id());

        chargingStationRef.child(placeModel.getStation_id()).setValue(placeModel).addOnCompleteListener(it->{
            if (it.isSuccessful()){
                hostListRef.child(loggedUserMbNumber).setValue(newHost).addOnCompleteListener(it2->{
                    if (it.isSuccessful()){
                        Toast.makeText(this,"Congratulation You are Host now", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        nmap = googleMap;
        Criteria criteria = new Criteria();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
////
//            double latitude = 0;
//            double longitude = 0;
//
//            Location location = locationManager.getLastKnownLocation(provider);
//            if (location != null) {
//                Log.e("TAG", "GPS is on");
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//
//            }
//            else{
//                locationManager.requestLocationUpdates(provider, 1000, 0, (LocationListener) this);
//            }

            LatLng currentLocation = new LatLng(18.6454538, 73.7908715);
            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f));

            View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);

        }

        nmap.setOnMapClickListener(point -> {

            if (new_marker != null){
                new_marker.remove();
            }
            Location location = new Location("Clicked");
            location.setLatitude(point.latitude);
            location.setLongitude(point.longitude);

            latitude.setText(String.valueOf(point.latitude));
            longitude.setText(String.valueOf(point.longitude));


            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(newLatLng)
                    .title(newLatLng.toString());

            new_marker = nmap.addMarker(markerOptions);

        });


    }

}