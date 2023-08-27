package com.lk.individual_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;
import android.widget.ImageButton;

public class HomeScreenActivity extends AppCompatActivity{
    ImageButton btnidentify, btnsearch, btnpoison, btnmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        btnidentify = (ImageButton) findViewById(R.id.btn_identify);
        btnsearch = (ImageButton) findViewById(R.id.btn_search);
        btnpoison = (ImageButton) findViewById(R.id.btn_family);
        btnmore = (ImageButton) findViewById(R.id.btn_more);

        btnidentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenBooks();
            }
        });
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReserveSeat();
            }
        });
        btnpoison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteStory();
            }
        });
        btnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenWhatsapp();
            }
        });
    }
    public void OpenBooks(){
        Intent intent = new Intent(this,CameraPreviewActivity.class);
        startActivity(intent);
    }

    public void ReserveSeat(){
        Intent intent = new Intent(this, FishActivity.class);
        startActivity(intent);
    }
    public void WriteStory(){
        Intent intent = new Intent(this, ReportFishActivity.class);
        startActivity(intent);
    }
    public void OpenWhatsapp(){
        Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
        String url = "https://chat.whatsapp.com/GG3uuQ1JniCL4zRD0ZUSjT";
        intentWhatsapp.setData(Uri.parse(url));
        intentWhatsapp.setPackage("com.whatsapp");
        startActivity(intentWhatsapp);

    }
}