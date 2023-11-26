package com.project.wheels_on_demand.Pages;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.project.wheels_on_demand.R;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ImageButton back_btn = findViewById(R.id.go_to_btn);
        back_btn.setOnClickListener(v->finish());

    }
}