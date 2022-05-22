package com.project.evcharz.Pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.evcharz.MainActivity;
import com.project.evcharz.R;

import java.util.concurrent.TimeUnit;

public class OtpValidation extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private  String backendOtp;
    private  String phone_no;
    TextView resendBtn;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);
        phone_no = getIntent().getStringExtra("phoneNo");
        mAuth = FirebaseAuth.getInstance();

        backendOtp = getIntent().getStringExtra("otp");

        EditText userOtp = findViewById(R.id.editTextNumber);


        TextView mb = findViewById(R.id.lbl_mb_no);
        resendBtn = findViewById(R.id.resendOtpBtn);


        Button verifyOtp = findViewById(R.id.verifyOtp);

        mb.setText("Enter the otp send to +91"+phone_no);


        verifyOtp.setOnClickListener(v -> {
            if (userOtp.getText().toString().length() != 6) {
                Toast.makeText(this, "Enter the Correct 6 digit Otp", Toast.LENGTH_SHORT).show();
            }else {
                if (backendOtp != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            backendOtp, userOtp.getText().toString()
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);

                            SharedPreferences.Editor LogDet = sharedPreferences.edit();
                            LogDet.putString("loggedUserMbNumber", phone_no);
                            LogDet.apply();
                            Intent i = new Intent(OtpValidation.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            Toast.makeText(OtpValidation.this, "Enter the Correct Otp", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resendBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            resendBtn.setVisibility(View.INVISIBLE);

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91" + phone_no,
                    60,
                    TimeUnit.SECONDS,
                    OtpValidation.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OtpValidation.this, "Otp Resend SuccessFull", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            progressBar.setVisibility(View.GONE);
                            resendBtn.setVisibility(View.VISIBLE);
                            System.out.println("error in otp "+e.getMessage());
                            Toast.makeText(OtpValidation.this, e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCodeSent(@NonNull String backend_otp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(backend_otp, forceResendingToken);
                            progressBar.setVisibility(View.GONE);
                            resendBtn.setVisibility(View.VISIBLE);
                        }
                    }

            );

        });
    }

}