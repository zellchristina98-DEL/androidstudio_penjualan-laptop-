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

public class tambahdatapembeliactivity extends AppCompatActivity {
    EditText etktp, etnama, etalamat, ettelp;
    Button btnsimpan;

    private static final String URL = "http://192.168.18.12/penjualanlaptop/tambahpembeli.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data_pembeli);

        etktp = findViewById(R.id.editktp);
        etnama = findViewById(R.id.editnama);
        etalamat = findViewById(R.id.editalamat);
        ettelp = findViewById(R.id.editnohp);
        btnsimpan = findViewById(R.id.tombolsimpan);

        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ktp = etktp.getText().toString().trim();
                String nama = etnama.getText().toString().trim();
                String alamat = etalamat.getText().toString().trim();
                String telp = ettelp.getText().toString().trim();

                if (ktp.isEmpty() || nama.isEmpty() || alamat.isEmpty() || telp.isEmpty()) {
                    Toast.makeText(tambahdatapembeliactivity.this, "Harap isi semua data", Toast.LENGTH_LONG).show();
                } else {
                    simpandata(ktp, nama, alamat, telp);
                }
            }
        });
    }

    private void simpandata(final String ktp, final String nama, final String alamat, final String telp) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");

                            Toast.makeText(tambahdatapembeliactivity.this, message, Toast.LENGTH_LONG).show();

                            if (success) {
                                startActivity(new Intent(tambahdatapembeliactivity.this, DataPembeliActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(tambahdatapembeliactivity.this, "Format respon tidak valid: " + response, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(tambahdatapembeliactivity.this, cause, Toast.LENGTH_LONG).show();
                        Log.e("Volley Error", error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ktp", ktp);
                params.put("nama_pembeli", nama);
                params.put("alamat_pembeli", alamat);
                params.put("telp_pembeli", telp);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}