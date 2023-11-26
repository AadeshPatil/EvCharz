package com.project.wheels_on_demand.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.wheels_on_demand.R;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText inputMobile;
    private Button buttonGetOTP;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonGetOTP.setOnClickListener(v -> {
            String phoneNumber = inputMobile.getText().toString().trim();
            if (phoneNumber.length() != 10) {
                Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                return;
            }
            String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
            initiatePhoneNumberVerification(formattedPhoneNumber);
        });
    }

    private void initializeViews() {
        inputMobile = findViewById(R.id.txt_phoneNo);
        buttonGetOTP = findViewById(R.id.btn_sendOtp);
        progressBar = findViewById(R.id.progressBar);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+91" + phoneNumber;
        }
        return phoneNumber;
    }

    private void initiatePhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                buttonGetOTP.setVisibility(View.VISIBLE);
                                // Handle verification completion as needed
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                buttonGetOTP.setVisibility(View.VISIBLE);
                                Log.d("failedOTP", "onVerificationFailed: " + e.getMessage());
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.GONE);
                                buttonGetOTP.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(LoginActivity.this, OtpValidation.class);
                                intent.putExtra("phoneNo", phoneNumber); // Send formatted phone number
                                intent.putExtra("otp", verificationId);
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        progressBar.setVisibility(View.VISIBLE);
        buttonGetOTP.setVisibility(View.INVISIBLE);
    }
}
