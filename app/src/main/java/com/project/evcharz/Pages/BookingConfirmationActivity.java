package com.project.evcharz.Pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class BookingConfirmationActivity extends AppCompatActivity {

    PlaceModel selectedStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);


        selectedStation = (PlaceModel) getIntent().getSerializableExtra("station");
//        selectedStation = new PlaceModel("123",17.134324,19.134132,"adshaoh","10","4.9",null,"qwejrjwerh");

        showCelebration();
        TextView station_name = this.findViewById(R.id.nameOfStation);
        TextView distance = this.findViewById(R.id.distanceFromLoc);
        TextView address = this.findViewById(R.id.addressOfStation);



        SharedPreferences prefs = getSharedPreferences("distance", MODE_PRIVATE);
        String distance_in_km = prefs.getString("distance_in_km", "0");

        station_name.setText(selectedStation.getPlace_name());
        distance.setText(distance_in_km + " Km");
        address.setText(selectedStation.getAddress());

        ImageButton bckBtn = this.findViewById(R.id.back_btn_booking_details);

        bckBtn.setOnClickListener(v->{
            Intent i;
            i = new Intent(BookingConfirmationActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });


    }

    public void showCelebration(){

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        final KonfettiView celebration = this.findViewById(R.id.celebration);
        celebration.build()
                .addColors(Color.BLUE, Color.RED, Color.GREEN)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, display.widthPixels + 50f, -50f, -50f)
                .streamFor(300, 1500L);
    }
}