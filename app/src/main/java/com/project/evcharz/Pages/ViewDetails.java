package com.project.evcharz.Pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;
import java.io.Serializable;
import java.util.Locale;

public class ViewDetails extends AppCompatActivity implements Serializable {

    PlaceModel selectedStation;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);



        selectedStation = (PlaceModel) getIntent().getSerializableExtra("StationModel");
        String cur_Latitude = getIntent().getStringExtra("cur_Latitude");
        String cur_Longitude = getIntent().getStringExtra("cur_Longitude");


        TextView station_name = findViewById(R.id.txt_name_of_station);

        TextView txt_address = findViewById(R.id.addressOfStation);
        TextView txt_distance = findViewById(R.id.txt_distance);
        ImageButton backBtn = findViewById(R.id.btn_back_home);
        backBtn.setOnClickListener(v->{
            finish();
        });

        station_name.setText(selectedStation.getPlace_name());
        txt_address.setText(selectedStation.getAddress());
        txt_distance.setText("17 km");
        Log.d("cur_Latitude",cur_Latitude);
        Log.d("cur_Latitude",cur_Longitude);

        double distance;
        Location locationA = new Location("Location1");
        locationA.setLatitude(Double.parseDouble(cur_Latitude));
        locationA.setLongitude(Double.parseDouble(cur_Longitude));

        Location locationB = new Location("Location2");
        locationB.setLatitude(selectedStation.getLatitude());
        locationB.setLongitude(selectedStation.getLongitude());

//        distance = locationA.distanceTo(locationB);   // in meters
        distance = locationA.distanceTo(locationB)/1000;
        txt_distance.setText(distance+" KM");
        distance=Math.floor(distance*100) / 100;

        txt_distance.setText(distance+" KM");

        SharedPreferences.Editor editor = getSharedPreferences("distance", MODE_PRIVATE).edit();
        editor.putString("distance_in_km", String.valueOf(distance));
        editor.apply();

        ImageButton openMap = findViewById(R.id.locate_to_map);
        openMap.setOnClickListener(v->{
//
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", selectedStation.getLatitude(), selectedStation.getLongitude(), selectedStation.getPlace_name());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });

        Button book_time = findViewById(R.id.book_slot);
        book_time.setOnClickListener(v->{
             Intent intent= new Intent(this, BookStation.class);
                intent.putExtra("StationModel",selectedStation);
                this.startActivity(intent);
        });


    }


}