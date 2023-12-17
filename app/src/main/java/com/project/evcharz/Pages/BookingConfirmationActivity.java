package com.project.evcharz.Pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.evcharz.Model.BookingModel;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;
import java.util.Locale;
import java.util.Objects;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class BookingConfirmationActivity extends AppCompatActivity {

    PlaceModel selectedStation;
    BookingModel bookingObj;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String currentUid;
    String price,id;
    RelativeLayout rl;
    TextView textCanceled;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("booking_details");
        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        price = getIntent().getStringExtra("price");
        id = getIntent().getStringExtra("BookingIdMain");
        selectedStation = (PlaceModel) getIntent().getSerializableExtra("station");
        bookingObj = (BookingModel) getIntent().getSerializableExtra("bookingModel");


        showCelebration();
        TextView station_name = this.findViewById(R.id.nameOfStation);
        TextView distance = this.findViewById(R.id.distanceFromLoc);
        TextView address = this.findViewById(R.id.addressOfStation);

         textCanceled = findViewById(R.id.textView16);
         rl = findViewById(R.id.confirmationID);


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


        Button openMap = this.findViewById(R.id.btn_map);
        Button getSupport = this.findViewById(R.id.btn_suport);
        Button btn_cancle = this.findViewById(R.id.btn_cancle);

        openMap.setOnClickListener(v->{
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", selectedStation.getLatitude(), selectedStation.getLongitude(), selectedStation.getPlace_name());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });


        getSupport.setOnClickListener(v->{
            String number = "+91 7218829436";
            String url = "https://api.whatsapp.com/send?phone="+number;

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_TEXT, "");
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        btn_cancle.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure You want to cancel Your Booking?");

            builder.setPositiveButton("YES", (dialog, which) -> {
               cancelBooking();
            });

            builder.setNegativeButton("NO", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog alert = builder.create();
            alert.show();
        });


    }

    public void cancelBooking(){
        if (currentUid != null){
            bookingObj.setStatus("Canceled");

            Log.d("bookingModel",bookingObj.getStatus());
            databaseReference.child(id).setValue(bookingObj).addOnCompleteListener(it->{
                if(it.isSuccessful()){
                    rl.setBackgroundColor(Color.RED);
                    textCanceled.setText("Your Booking Is canceled");
                    Toast.makeText(this,"Booking Canceled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
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