package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class DataBeliCashActivity extends AppCompatActivity {
    Button btnbelicash;
    ListView listView;
    ArrayList<String> listDataCash = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private static final String Url_TAMPIL = "http://192.168.18.12/penjualanlaptop/tampilcash.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_bayar_cash);

        btnbelicash = findViewById(R.id.tomboltambah);
        listView = findViewById(R.id.listbayarcash);

        btnbelicash.setOnClickListener(v -> {
            Intent intent = new Intent(DataBeliCashActivity.this, tambahdatabelicashactivity.class);
            startActivity(intent);
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDataCash);
        listView.setAdapter(adapter);

        loadDataCash();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataCash();
    }

    private void loadDataCash() {
        StringRequest request = new StringRequest(Request.Method.GET, Url_TAMPIL, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                listDataCash.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String kodeCash = obj.getString("kode_cash");
                    String namaPembeli = obj.getString("nama_pembeli");
                    String merkLaptop = obj.getString("merk");
                    String typeLaptop = obj.getString("type");
                    String tanggal = obj.getString("cash_tgl");
                    String cashBayar = obj.getString("cash_bayar");


                    String item = "Kode: " + kodeCash + "\n" +
                            "Pembeli: " + namaPembeli + "\n" +
                            "Laptop: " + merkLaptop + " " + typeLaptop + "\n" +
                            "Tanggal: " + tanggal + "\n" +
                            "Bayar: Rp " + cashBayar;

                    listDataCash.add(item);
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal parsing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Log.e("Volley Error", error.toString());
            Toast.makeText(this, "Gagal memuat data dari server", Toast.LENGTH_SHORT).show();
        });

        Volley.newRequestQueue(this).add(request);
    }
}