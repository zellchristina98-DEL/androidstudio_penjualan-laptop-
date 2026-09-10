package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btnlaptop;
    Button btnpembeli;
    Button btnpaket;
    Button btnbelicash;
    Button btnkredit;
    Button btncicilan;
    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main);

        btnlaptop=findViewById(R.id.tombollaptop);
        btnlaptop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DatalaptopActivity.class);
                startActivity(intent);
            }
        });

        btnpembeli=findViewById(R.id.tombolpembeli);
        btnpembeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DataPembeliActivity.class);
                startActivity(intent);
            }
        });

        btnpaket=findViewById(R.id.tombolpaket);
        btnpaket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DataPaketActivity.class);
                startActivity(intent);
            }
        });

        btnbelicash=findViewById(R.id.tombolbeli);
        btnbelicash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DataBeliCashActivity.class);
                startActivity(intent);
            }
        });

        btnkredit=findViewById(R.id.tombolkredit);
        btnkredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DataBeliKreditActivity.class);
                startActivity(intent);
            }
        });

        btncicilan=findViewById(R.id.tombolcicilan);
        btncicilan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DataBayarCicilanActivity.class);
                startActivity(intent);
            }
        });


    }
}

