package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DataBayarCicilanActivity extends AppCompatActivity {
    Button btncicilan;
    @Override
    protected void onCreate (Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_pembayaran_cicilan);

        btncicilan=findViewById(R.id.tomboltambah);
        btncicilan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DataBayarCicilanActivity.this,tambahdatabayarcicilanactivity.class);
                startActivity(intent);
            }
        });

    }
}



