package com.project.evcharz.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.project.evcharz.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        ImageButton back_btn = this.findViewById(R.id.bk_btn);

        back_btn.setOnClickListener(v->finish());

    }
}