package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataBeliKreditActivity extends AppCompatActivity {
    Button btnpaket;
    ListView listView;
    ArrayList<String> listKredit = new ArrayList<>();

    private static final String URL_TAMPIL = "http://192.168.18.12/penjualanlaptop/tampilkredit.php";

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_beli_kredit);

        btnpaket = findViewById(R.id.tomboltambah);
        listView = findViewById(R.id.listkredit);

        btnpaket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataBeliKreditActivity.this, tambahdatakreditactivity.class);
                startActivity(intent);
            }
        });

        loadDataKredit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataKredit();
    }

    private void loadDataKredit() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_TAMPIL, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                listKredit.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String kode = obj.getString("kode_kredit");
                    String ktp = obj.getString("ktp");
                    String laptop = obj.getString("kode_laptop");
                    String tanggal = obj.getString("tanggal_kredit");

                    listKredit.add(kode + " - " + ktp + " - " + laptop + " (" + tanggal + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listKredit);
                if (listView != null) {
                    listView.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Volley", error.toString()));

        Volley.newRequestQueue(this).add(request);
    }
}