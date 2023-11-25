package com.project.evcharz.Pages;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.evcharz.Adapters.MyBookingAdapter;
import com.project.evcharz.Model.BookingModel;
import com.project.evcharz.R;

import java.util.ArrayList;
import java.util.Objects;

public class MyBookingActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,childRef;
    String currentUid;

    private RecyclerView myBookingRV;
    private final ArrayList<BookingModel> myBookingModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        myBookingRV  = this.findViewById(R.id.my_booking_list);


        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        String loggedUserMbNumber = sh.getString("loggedUserMbNumber", "");

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
                    if (i.getUser_mb_no().equals(loggedUserMbNumber)){
                        myBookingModelArrayList.add(0,i);
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