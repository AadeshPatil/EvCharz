package com.project.evcharz;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.project.evcharz.Pages.HomeActivity;
import com.project.evcharz.Pages.LoginActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);

        String loggedUserMbNo = sh.getString("loggedUserMbNo", "");

        Intent i;
        if (loggedUserMbNo == null || "".equals(loggedUserMbNo)) {
            i = new Intent(MainActivity.this, LoginActivity.class);
        } else {
            i = new Intent(MainActivity.this, HomeActivity.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);


    }


}