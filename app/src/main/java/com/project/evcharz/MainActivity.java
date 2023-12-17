package com.project.evcharz;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.project.evcharz.Pages.HomeActivity;
import com.project.evcharz.Pages.LoginActivity;


public class MainActivity extends AppCompatActivity {
    String loggedUserMbNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        loggedUserMbNumber = sh.getString("loggedUserMbNumber", "");

        Intent i;
        if ("".equals(loggedUserMbNumber)) {
            i = new Intent(MainActivity.this, LoginActivity.class);
        } else {
            i = new Intent(MainActivity.this, HomeActivity.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

    }
}