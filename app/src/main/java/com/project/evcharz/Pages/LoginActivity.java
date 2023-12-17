package com.project.evcharz.Pages;

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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.evcharz.R;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText inputMobile = findViewById(R.id.txt_phoneNo);
        final Button buttonGetOTP = findViewById(R.id.btn_sendOtp);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        buttonGetOTP.setOnClickListener(v -> {
            String phoneNumber = inputMobile.getText().toString().trim();

            if (phoneNumber.length() != 10) {
                Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+91" + phoneNumber;
            }

            buttonGetOTP.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            initiatePhoneNumberVerification(phoneNumber);
        });
    }

    private void initiatePhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                handleVerificationCompleted();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                handleVerificationFailed(e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                handleCodeSent(verificationId);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void handleVerificationCompleted() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button buttonGetOTP = findViewById(R.id.btn_sendOtp);
        progressBar.setVisibility(View.GONE);
        buttonGetOTP.setVisibility(View.VISIBLE);
        // Handle verification completion if needed
    }

    private void handleVerificationFailed(FirebaseException exception) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button buttonGetOTP = findViewById(R.id.btn_sendOtp);
        progressBar.setVisibility(View.GONE);
        buttonGetOTP.setVisibility(View.VISIBLE);
        Log.d("failedOTP", "onVerificationFailed: " + exception.getMessage());
        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        // Handle verification failure if needed
    }

    private void handleCodeSent(String verificationId) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        Button buttonGetOTP = findViewById(R.id.btn_sendOtp);
        progressBar.setVisibility(View.GONE);
        buttonGetOTP.setVisibility(View.VISIBLE);
        EditText inputMobile = findViewById(R.id.txt_phoneNo);
        String finalPhoneNumber = inputMobile.getText().toString().trim();
        Intent i = new Intent(LoginActivity.this, OtpValidation.class);
        i.putExtra("phoneNo", finalPhoneNumber); // Send formatted phone number
        i.putExtra("otp", verificationId);
        startActivity(i);
    }
}
