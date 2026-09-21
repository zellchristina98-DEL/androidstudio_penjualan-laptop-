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

public class EditpaketActivity extends AppCompatActivity {

    EditText edUangMuka, edTenor, edBungaCicilan;
    Button btnupdate, btnBatal;
    String kodepaket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_paket);

        edUangMuka = findViewById(R.id.editUangMuka);
        edTenor = findViewById(R.id.editTenor);
        edBungaCicilan = findViewById(R.id.editBungaCicilan);

        btnupdate = findViewById(R.id.tombolupdate);
        btnBatal = findViewById(R.id.tombolbatal);

        Intent intent = getIntent();

        kodepaket = intent.getStringExtra("kode_paket");

        edUangMuka.setText(intent.getStringExtra("uang_muka"));
        edTenor.setText(intent.getStringExtra("tenor"));
        edBungaCicilan.setText(intent.getStringExtra("bunga_cicilan"));

        btnupdate.setOnClickListener(v -> {

            String uangMuka = edUangMuka.getText().toString().trim();
            String tenor = edTenor.getText().toString().trim();
            String bungaCicilan = edBungaCicilan.getText().toString().trim();

            if (uangMuka.isEmpty() ||
                    tenor.isEmpty() ||
                    bungaCicilan.isEmpty()) {

                Toast.makeText(
                        EditpaketActivity.this,
                        "Semua field harus diisi",
                        Toast.LENGTH_LONG
                ).show();

            } else {

                new UpdateData().execute(
                        kodepaket,
                        uangMuka,
                        tenor,
                        bungaCicilan
                );
            }
        });

        btnBatal.setOnClickListener(v -> finish());
    }

    private class UpdateData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String kode = params[0];
            String uangMuka = params[1];
            String tenor = params[2];
            String bungaCicilan = params[3];

            try {

                URL url = new URL(
                        "http://192.168.18.12/penjualanlaptop/editpaket.php"
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
                                new OutputStreamWriter(os, "UTF-8")
                        );

                String postData =
                        "kode_paket=" +
                                URLEncoder.encode(kode, "UTF-8") +

                                "&uang_muka=" +
                                URLEncoder.encode(uangMuka, "UTF-8") +

                                "&tenor=" +
                                URLEncoder.encode(tenor, "UTF-8") +

                                "&bunga_cicilan=" +
                                URLEncoder.encode(bungaCicilan, "UTF-8");

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

                StringBuilder sb = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                conn.disconnect();

                return sb.toString();

            } catch (Exception e) {

                e.printStackTrace();

                return "{\"status\":0,\"message\":\"error Update\"}";
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
                            EditpaketActivity.this,
                            "Update Berhasil",
                            Toast.LENGTH_LONG
                    ).show();

                    setResult(RESULT_OK);

                    finish();

                } else {

                    Toast.makeText(
                            EditpaketActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }

            } catch (JSONException e) {

                e.printStackTrace();

                Toast.makeText(
                        EditpaketActivity.this,
                        "Response tidak valid",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}