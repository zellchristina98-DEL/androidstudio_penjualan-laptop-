package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DatalaptopActivity extends AppCompatActivity {
    Button btndatalaptop;
    ListView listView;
    ArrayList<String> itemList;
    ArrayList<String> idList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_laptop);
        btndatalaptop = findViewById(R.id.tomboldata);
        listView = findViewById(R.id.listmobil);
        itemList = new ArrayList<>();
        idList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btndatalaptop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatalaptopActivity.this, tambahdatalaptopactivity.class);
                startActivity(intent);
            }
        });

        new tampildata().execute();
    }

    private class tampildata extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://192.168.18.12/penjualanlaptop/tampillaptop.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                itemList.clear();
                idList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String kode = obj.getString("kode_laptop");
                    String nama = obj.getString("nama_laptop");
                    String merk = obj.getString("merk");
                    String spesifikasi = obj.getString("spesifikasi");
                    String harga = obj.getString("harga");
                    String stok = obj.getString("stok");

                    idList.add(kode);
                    itemList.add("Nama\t" + nama +
                            "\nMerk\t" + merk +
                            "\nSpesifikasi\t" + spesifikasi +
                            "\nHarga\t" + harga +
                            "\nStok\t" + stok +
                            "\n\n");
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}