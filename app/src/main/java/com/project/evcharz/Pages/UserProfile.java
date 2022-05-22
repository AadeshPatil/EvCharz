package com.project.evcharz.Pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.project.evcharz.Model.UserModel;
import com.project.evcharz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    UserModel userModel;

    String currentUid;
    EditText txt_name,txt_email,txt_phone_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);

        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        String loggedUserMbNumber = sh.getString("loggedUserMbNumber", "");



        ImageButton backBtn = this.findViewById(R.id.backBtn);

         txt_name = findViewById(R.id.eTextUserName);
         txt_email = findViewById(R.id.email_address);
         txt_phone_no = findViewById(R.id.mb_no);
         txt_phone_no.setText(loggedUserMbNumber);
         txt_phone_no.setEnabled(false);

        Button updateBtn = findViewById(R.id.update_btn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("userDetails");
        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        getUserData();

        backBtn.setOnClickListener(v -> finish());



        updateBtn.setOnClickListener(v->{
            String name = txt_name.getText().toString();
            String phone = txt_phone_no.getText().toString();
            String address = txt_email.getText().toString();

            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(address)) {
                Toast.makeText(this, "Please fill All the Values", Toast.LENGTH_SHORT).show();
            }else{
                String id = databaseReference.push().getKey();
                userModel = new UserModel(currentUid,name,address , phone);
                    if (currentUid != null){
                        databaseReference.child(currentUid).setValue(userModel).addOnCompleteListener(it->{
                            if (it.isSuccessful()){
                                Toast.makeText(this,"Profile Updated", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

            }
        });



    }


    private void getUserData() {

        databaseReference.child(currentUid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                UserModel userModel = task.getResult().getValue(UserModel.class);
                if(userModel != null){
                    txt_phone_no.setText(userModel.getMobileNo());
                    txt_name.setText(userModel.getName());
                    txt_email.setText(userModel.getEmailId());
                    txt_phone_no.setEnabled(false);
                }
            }
        });
    }

}