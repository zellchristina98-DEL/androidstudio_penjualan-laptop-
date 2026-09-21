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

public class tambahdatapaketactivity extends AppCompatActivity {
    EditText etkode, etuangmuka, etbunga, ettenor;
    Button btnsimpan;

    private static final String URL = "http://192.168.18.12/penjualanlaptop/tambahpaket.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data_paket);

        etkode = findViewById(R.id.editkodepaket);
        etuangmuka = findViewById(R.id.editdp);
        etbunga = findViewById(R.id.editbunga);
        ettenor = findViewById(R.id.editlamatenor);
        btnsimpan = findViewById(R.id.tombolsimpan);

        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kode = etkode.getText().toString().trim();
                String uangMuka = etuangmuka.getText().toString().trim();
                String bunga = etbunga.getText().toString().trim();
                String tenor = ettenor.getText().toString().trim();

                if (kode.isEmpty() || uangMuka.isEmpty() || bunga.isEmpty() || tenor.isEmpty()) {
                    Toast.makeText(tambahdatapaketactivity.this, "Harap isi semua data", Toast.LENGTH_LONG).show();
                } else {
                    simpandata(kode, uangMuka, bunga, tenor);
                }
            }
        });
    }

    private void simpandata(final String kode, final String uangMuka, final String bunga, final String tenor) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            Toast.makeText(tambahdatapaketactivity.this, message, Toast.LENGTH_LONG).show();

                            if (success) {
                                startActivity(new Intent(tambahdatapaketactivity.this, DataPaketActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(tambahdatapaketactivity.this, "Format respon tidak valid: " + response, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(tambahdatapaketactivity.this, cause, Toast.LENGTH_LONG).show();
                        Log.e("Volley Error", error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_paket", kode);
                params.put("uang_muka", uangMuka);
                params.put("bunga_cicilan", bunga);   // FIXED: sebelumnya "bunga", sekarang cocok dengan PHP & DB
                params.put("tenor", tenor);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}