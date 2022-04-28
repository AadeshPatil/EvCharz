package com.project.evcharz.Pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.evcharz.MainActivity;
import com.project.evcharz.R;

public class OtpValidation extends AppCompatActivity {

    private  String backendOtp;
    private  String phone_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);

        phone_no = getIntent().getStringExtra("phoneNo");

        backendOtp = getIntent().getStringExtra("otp");

        EditText userOtp = findViewById(R.id.editTextNumber);


        TextView resendBtn = findViewById(R.id.lbl_mb_no);

        Button verifyOtp = findViewById(R.id.verifyOtp);

        resendBtn.setText("Enter the otp send to +91"+phone_no);

        verifyOtp.setOnClickListener(v -> {
            if (userOtp.getText().toString().length() != 6) {
                Toast.makeText(this, "Enter Otp", Toast.LENGTH_SHORT).show();
            }
            if (backendOtp != null){
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        backendOtp,userOtp.getText().toString()
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails",MODE_PRIVATE);

                        SharedPreferences.Editor LogDet = sharedPreferences.edit();
                        LogDet.putString("loggedUserMbNo", phone_no);
                        LogDet.apply();
                        Intent i = new Intent(OtpValidation.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else {
                        Toast.makeText(OtpValidation.this, "Enter the Correct Otp", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        });
    }
}