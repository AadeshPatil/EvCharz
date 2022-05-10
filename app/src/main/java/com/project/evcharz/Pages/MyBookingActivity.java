package com.project.evcharz.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.evcharz.Adapters.MyBookingAdapter;
import com.project.evcharz.Model.BookingModel;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.Model.UserModel;
import com.project.evcharz.R;

import java.util.ArrayList;
import java.util.Objects;

public class MyBookingActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,childRef;
    String currentUid;

    private RecyclerView myBookingRV;
    private ArrayList<BookingModel> myBookingModelArrayList = new ArrayList<>();

    String loggedUserMbNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        myBookingRV  = this.findViewById(R.id.my_booking_list);


        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        String loggedUserMbNo = sh.getString("loggedUserMbNo", "");

        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference = firebaseDatabase.getReference("booking_details");
        ImageButton backBtn = findViewById(R.id.bck_btn);
        backBtn.setOnClickListener(v->{
            finish();
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    BookingModel i = postSnapshot.getValue(BookingModel.class);
                    if (i.getUser_mb_no().equals(loggedUserMbNo)){
                        myBookingModelArrayList.add(i);
                    }
                }
                MyBookingAdapter courseAdapter = new MyBookingAdapter(getApplicationContext(), myBookingModelArrayList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                myBookingRV.setLayoutManager(linearLayoutManager);
                myBookingRV.setAdapter(courseAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });



    }


}