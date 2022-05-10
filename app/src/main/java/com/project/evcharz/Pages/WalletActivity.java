package com.project.evcharz.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.project.evcharz.R;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);


        ImageButton back_btn = findViewById(R.id.go_to_btn);
        back_btn.setOnClickListener(v->finish());

    }
}