package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DataBeliCashActivity extends AppCompatActivity {
    Button btnbelicash;
    @Override
    protected void onCreate (Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_data_bayar_cash);

        btnbelicash=findViewById(R.id.tomboltambah);
        btnbelicash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DataBeliCashActivity.this,tambahdatabelicashactivity.class);
                startActivity(intent);
            }
        });

    }
}



