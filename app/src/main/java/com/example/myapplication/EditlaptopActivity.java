package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditlaptopActivity extends AppCompatActivity {

    EditText edmerk, edType, edWarna, edharga;
    Button btnupdate, btnBatal;
    String kodelaptop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_laptop);

        edmerk = findViewById(R.id.editmerk);
        edType = findViewById(R.id.editType);
        edWarna = findViewById(R.id.editWarna);
        edharga = findViewById(R.id.editharga);

        btnupdate = findViewById(R.id.tombolupdate);
        btnBatal = findViewById(R.id.tombolbatal);

        Intent intent = getIntent();

        kodelaptop = intent.getStringExtra("kode_laptop");

        edmerk.setText(intent.getStringExtra("merk"));
        edType.setText(intent.getStringExtra("type"));
        edWarna.setText(intent.getStringExtra("warna"));
        edharga.setText(intent.getStringExtra("harga"));

        btnupdate.setOnClickListener(v -> {

            String merk = edmerk.getText().toString().trim();
            String type = edType.getText().toString().trim();
            String warna = edWarna.getText().toString().trim();
            String harga = edharga.getText().toString().trim();

            if (merk.isEmpty() ||
                    type.isEmpty() ||
                    warna.isEmpty() ||
                    harga.isEmpty()) {

                Toast.makeText(
                        this,
                        "semua field harus diisi",
                        Toast.LENGTH_LONG
                ).show();

            } else {

                new UpdateData().execute(
                        kodelaptop,
                        merk,
                        type,
                        warna,
                        harga
                );
            }
        });

        btnBatal.setOnClickListener(v -> finish());
    }

    private class UpdateData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String id = params[0];
            String merk = params[1];
            String type = params[2];
            String warna = params[3];
            String harga = params[4];

            try {

                URL url = new URL(
                        "http://192.168.18.12/penjualanlaptop/editlaptop.php"
                );

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                conn.setRequestProperty(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                );

                OutputStream os = conn.getOutputStream();

                BufferedWriter writer =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        os,
                                        "UTF-8"
                                )
                        );

                String postData =
                        "kode_laptop=" +
                                URLEncoder.encode(id, "UTF-8") +

                                "&merk=" +
                                URLEncoder.encode(merk, "UTF-8") +

                                "&type=" +
                                URLEncoder.encode(type, "UTF-8") +

                                "&warna=" +
                                URLEncoder.encode(warna, "UTF-8") +

                                "&harga=" +
                                URLEncoder.encode(harga, "UTF-8");

                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        conn.getInputStream()
                                )
                        );

                StringBuilder sb =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                conn.disconnect();

                return sb.toString();

            } catch (Exception e) {

                e.printStackTrace();

                return "{\"status\":0,\"message\":\"Error Update\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                JSONObject response =
                        new JSONObject(result);

                int status =
                        response.getInt("status");

                String message =
                        response.getString("message");

                if (status == 1) {

                    Toast.makeText(
                            EditlaptopActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    setResult(RESULT_OK);
                    finish();

                } else {

                    Toast.makeText(
                            EditlaptopActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }

            } catch (JSONException e) {

                e.printStackTrace();

                Toast.makeText(
                        EditlaptopActivity.this,
                        "Response tidak valid: " + result,
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}