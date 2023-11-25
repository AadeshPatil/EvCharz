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

            if (phoneNumber.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                return;
            }
            String finalPhoneNumber = phoneNumber;

            // Format the phone number correctly
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+91" + phoneNumber; // Assuming it's an Indian number, modify as needed
            }

            buttonGetOTP.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder()
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBar.setVisibility(View.GONE);
                                    buttonGetOTP.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.GONE);
                                    buttonGetOTP.setVisibility(View.VISIBLE);
                                    Log.d("failedOTP", "onVerificationFailed: " + e.getMessage());
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBar.setVisibility(View.GONE);
                                    buttonGetOTP.setVisibility(View.VISIBLE);
                                    Intent i = new Intent(LoginActivity.this, OtpValidation.class);
                                    i.putExtra("phoneNo", finalPhoneNumber); // Send formatted phone number
                                    i.putExtra("otp", verificationId);
                                    startActivity(i);
                                }
                            })
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
    }
}
