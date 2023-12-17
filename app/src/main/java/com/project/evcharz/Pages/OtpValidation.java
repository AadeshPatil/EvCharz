package com.project.evcharz.Pages;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.evcharz.MainActivity;
import com.project.evcharz.R;

import java.util.concurrent.TimeUnit;

public class OtpValidation extends AppCompatActivity {
    private String backendOtp;
    private String phoneNo;
    private TextView resendBtn;
    private ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);
        phoneNo = getIntent().getStringExtra("phoneNo");
        backendOtp = getIntent().getStringExtra("otp");

        EditText userOtp = findViewById(R.id.editTextNumber);
        TextView mb = findViewById(R.id.lbl_mb_no);
        resendBtn = findViewById(R.id.resendOtpBtn);
        Button verifyOtp = findViewById(R.id.verifyOtp);

        mb.setText("Enter the OTP sent to +91" + phoneNo);

        verifyOtp.setOnClickListener(v -> {
            String enteredOtp = userOtp.getText().toString().trim();
            if (enteredOtp.length() != 6) {
                Toast.makeText(this, "Enter the Correct 6-digit OTP", Toast.LENGTH_SHORT).show();
            } else {
                if (backendOtp != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backendOtp, enteredOtp);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveUserPhoneNumber();
                            navigateToMainActivity();
                        } else {
                            Toast.makeText(OtpValidation.this, "Enter the Correct OTP", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resendBtn.setOnClickListener(v -> resendVerificationCode());
    }

    private void saveUserPhoneNumber() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedUserMbNumber", phoneNo);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(OtpValidation.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void resendVerificationCode() {
        progressBar.setVisibility(View.VISIBLE);
        resendBtn.setVisibility(View.INVISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder()
                        .setPhoneNumber(phoneNo)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(OtpValidation.this, "OTP Resend Successful", Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                resendBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(OtpValidation.this, "Error in resending OTP: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                progressBar.setVisibility(View.GONE);
                                resendBtn.setVisibility(View.VISIBLE);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
