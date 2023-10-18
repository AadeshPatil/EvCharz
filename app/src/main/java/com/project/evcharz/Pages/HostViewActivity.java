package com.project.evcharz.Pages;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.project.evcharz.R;

public class HostViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_view);


        ImageButton backBtn = this.findViewById(R.id.back_button);
        backBtn.setOnClickListener(v->{
            finish();
        });


    }
}


