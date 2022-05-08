package com.project.evcharz.Pages;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.evcharz.Model.BookingModel;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    PlaceModel selectedStation;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String currentUid;

    BookingModel bookingModel;

    java.util.Date date2;
    String price,unit_con,duration,start_time,end_time,vehicle_type,transaction_mode;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        price = getIntent().getStringExtra("price");
        unit_con = getIntent().getStringExtra("unit_con");
        duration = getIntent().getStringExtra("duration");
        start_time = getIntent().getStringExtra("start_time");
        end_time = getIntent().getStringExtra("end_time");
        vehicle_type = getIntent().getStringExtra("vehicle_type");
        selectedStation = (PlaceModel) getIntent().getSerializableExtra("StationModel");


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("booking_details");
        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        TextView total_price = this.findViewById(R.id.total_price);
        TextView date = this.findViewById(R.id.txt_date);
        TextView electricity_consumption = this.findViewById(R.id.txt_electricity_con);
        TextView use_duration = this.findViewById(R.id.txt_duration);


        long millis = System.currentTimeMillis();
        date2 = new java.util.Date(millis);


        date.setText(String.valueOf(date2));
        total_price.setText("Rs. "+price + " GST included");
        electricity_consumption.setText(unit_con + " Units");
        use_duration.setText(duration+" Minutes");


        Button payBtn = this.findViewById(R.id.pay_method);
        CheckBox payWallet = this.findViewById(R.id.checkBox_wallet);
        CheckBox payOnline = this.findViewById(R.id.checkBox_online);

        payBtn.setOnClickListener(v->{

            if (payWallet.isChecked()){
                transaction_mode = "Wallet";

            }else if(payOnline.isChecked()){
                transaction_mode = "Online";
                startPayment(price);

            }

        });
    }

    public void startPayment(String price) {
        final Activity activity = this;
        final Checkout co = new Checkout();

        double finalAmount = Float.parseFloat(price)*100;

        co.setKeyID("rzp_test_gRpSX81brvUAyH");


        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "Demoing Charges");
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", finalAmount);
            JSONObject preFill = new JSONObject();
            preFill.put("email", "aadeshpatil650@gmail.com");
            preFill.put("contact", "9960776997");
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        book_station();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Error in payment: ", Toast.LENGTH_SHORT)
                .show();
    }





    private void book_station() {

        int randomPIN = (int)(Math.random()*9000)+1000;

        bookingModel = new BookingModel(randomPIN,String.valueOf(date2),"",start_time,end_time,vehicle_type,
                selectedStation.getStation_id(),selectedStation.getPlace_name(),price,transaction_mode,"Paid",duration);
        String id = databaseReference.push().getKey();

        if (currentUid != null){

            databaseReference.child(currentUid).child(id).setValue(bookingModel).addOnCompleteListener(it->{
                if (it.isSuccessful()){
                    Toast.makeText(this,"Booking Confirmed", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}