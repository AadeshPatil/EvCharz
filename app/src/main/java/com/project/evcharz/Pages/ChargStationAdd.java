package com.project.evcharz.Pages;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;

public class ChargStationAdd extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charg_station_add);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chargingStationDetails");

        EditText langitude = findViewById(R.id.longit);
        EditText latitude = findViewById(R.id.lat);
        EditText place_name = findViewById(R.id.place_name);
        EditText unit_rate = findViewById(R.id.txt_unit_rate);
        EditText adress = findViewById(R.id.add_address);
        Button add_btn = findViewById(R.id.add_place);

        add_btn.setOnClickListener(v->{

            Double lat = Double.valueOf(latitude.getText().toString());
            Double longi = Double.valueOf(langitude.getText().toString());
            String place = place_name.getText().toString();
            String unit_rate1 = unit_rate.getText().toString();
            String address = adress.getText().toString();
            String id = databaseReference.push().getKey();

                PlaceModel placeModel = new PlaceModel(id,lat,longi,place,unit_rate1,"","",address);
                databaseReference.child(id).setValue(placeModel).addOnCompleteListener(it->{
                    if (it.isSuccessful()){
                        Toast.makeText(this,"added", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });



        });

    }
}