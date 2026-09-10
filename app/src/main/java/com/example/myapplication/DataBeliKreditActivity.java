package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DataBeliKreditActivity extends AppCompatActivity {
    Button btnpaket;
    @Override
    protected void onCreate (Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_beli_kredit);

        btnpaket=findViewById(R.id.tomboltambah);
        btnpaket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DataBeliKreditActivity.this,tambahdatakreditactivity.class);
                startActivity(intent);
            }
        });

    }
}



