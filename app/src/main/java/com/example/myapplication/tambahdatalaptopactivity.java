package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class tambahdatalaptopactivity extends AppCompatActivity {
    EditText etkode, etmerk, ettype, etwarna, etharga;
    Button btnsimpan;

    private static final String URL = "http://192.168.18.12/penjualanlaptop/tambahlaptop.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_laptop);

        etkode = findViewById(R.id.editkode);
        etmerk = findViewById(R.id.editmerek);
        ettype = findViewById(R.id.edittype);
        etwarna = findViewById(R.id.editwarna);
        etharga = findViewById(R.id.editharga);
        btnsimpan = findViewById(R.id.tombollaptop);

        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kode = etkode.getText().toString().trim();
                String merk = etmerk.getText().toString().trim();
                String type = ettype.getText().toString().trim();
                String warna = etwarna.getText().toString().trim();
                String harga = etharga.getText().toString().trim();

                if (kode.isEmpty() || merk.isEmpty() || type.isEmpty() || warna.isEmpty() || harga.isEmpty()) {
                    Toast.makeText(tambahdatalaptopactivity.this, "Harap isi semua data", Toast.LENGTH_LONG).show();
                } else {
                    simpandata(kode, merk, type, warna, harga);
                }
            }
        });
    }

    private void simpandata(final String kode, final String merk, final String type, final String warna, final String harga) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parsing JSON dari PHP
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            Toast.makeText(tambahdatalaptopactivity.this, message, Toast.LENGTH_LONG).show();

                            if (success) {
                                startActivity(new Intent(tambahdatalaptopactivity.this, DatalaptopActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(tambahdatalaptopactivity.this, "Format respon tidak valid: " + response, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String cause = "Gagal terhubung ke server";
                        if (error.networkResponse != null) {
                            cause = "Status HTTP Error: " + error.networkResponse.statusCode;
                        } else if (error.getCause() != null) {
                            cause = error.getCause().getMessage();
                        } else if (error.getMessage() != null) {
                            cause = error.getMessage();
                        }
                        Toast.makeText(tambahdatalaptopactivity.this, cause, Toast.LENGTH_LONG).show();
                        Log.e("Volley Error", error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_laptop", kode);
                params.put("merk", merk);
                params.put("type", type);
                params.put("warna", warna);
                params.put("harga", harga);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}