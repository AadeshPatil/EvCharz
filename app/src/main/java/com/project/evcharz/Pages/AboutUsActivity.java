package com.project.evcharz.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.evcharz.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        ImageButton back_btn = this.findViewById(R.id.bk_btn);

        back_btn.setOnClickListener(v->finish());

        TextView email = findViewById(R.id.emailTxt);
        TextView mb = findViewById(R.id.mbNoTxt);
        ImageView wup = findViewById(R.id.launchWhatsapp);



        email.setOnClickListener(V->{
            Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
            selectorIntent.setData(Uri.parse("mailto:"));
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"d24.dish@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            emailIntent.setSelector( selectorIntent );

            startActivity(Intent.createChooser(emailIntent, "Send email..."));


        });

        mb.setOnClickListener(V->{
            Uri u = Uri.parse("tel:7218829436");
            Intent i = new Intent(Intent.ACTION_DIAL, u);
            try
            {
                startActivity(i);
            }
            catch (SecurityException s)
            {
                Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG)
                        .show();
            }
        });

        wup.setOnClickListener(v->{
            String number = "+91 7218829436";
            String url = "https://api.whatsapp.com/send?phone="+number;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_TEXT, "");
            i.setData(Uri.parse(url));
            startActivity(i);
        });

    }
}