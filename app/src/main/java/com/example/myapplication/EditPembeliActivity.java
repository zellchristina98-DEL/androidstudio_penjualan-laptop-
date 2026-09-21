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

public class EditPembeliActivity extends AppCompatActivity {

    EditText edNamaPembeli, edAlamatPembeli, edTelpPembeli;
    Button btnupdate, btnBatal;
    String ktp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pembeli);

        edNamaPembeli = findViewById(R.id.editNamaPembeli);
        edAlamatPembeli = findViewById(R.id.editAlamatPembeli);
        edTelpPembeli = findViewById(R.id.editTelpPembeli);

        btnupdate = findViewById(R.id.tombolupdate);
        btnBatal = findViewById(R.id.tombolbatal);

        Intent intent = getIntent();

        ktp = intent.getStringExtra("ktp");

        edNamaPembeli.setText(
                intent.getStringExtra("nama_pembeli")
        );

        edAlamatPembeli.setText(
                intent.getStringExtra("alamat_pembeli")
        );

        edTelpPembeli.setText(
                intent.getStringExtra("telp_pembeli")
        );

        btnupdate.setOnClickListener(v -> {

            String namaPembeli =
                    edNamaPembeli.getText().toString().trim();

            String alamatPembeli =
                    edAlamatPembeli.getText().toString().trim();

            String telpPembeli =
                    edTelpPembeli.getText().toString().trim();

            if (namaPembeli.isEmpty() ||
                    alamatPembeli.isEmpty() ||
                    telpPembeli.isEmpty()) {

                Toast.makeText(
                        EditPembeliActivity.this,
                        "Semua field harus diisi",
                        Toast.LENGTH_LONG
                ).show();

            } else {

                new UpdateData().execute(
                        ktp,
                        namaPembeli,
                        alamatPembeli,
                        telpPembeli
                );
            }
        });

        btnBatal.setOnClickListener(v -> finish());
    }

    private class UpdateData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String idKtp = params[0];
            String namaPembeli = params[1];
            String alamatPembeli = params[2];
            String telpPembeli = params[3];

            try {

                URL url = new URL(
                        "http://192.168.18.12/penjualanlaptop/editpembeli.php"
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
                        "ktp=" +
                                URLEncoder.encode(idKtp, "UTF-8") +

                                "&nama_pembeli=" +
                                URLEncoder.encode(namaPembeli, "UTF-8") +

                                "&alamat_pembeli=" +
                                URLEncoder.encode(alamatPembeli, "UTF-8") +

                                "&telp_pembeli=" +
                                URLEncoder.encode(telpPembeli, "UTF-8");

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
                            EditPembeliActivity.this,
                            "Update Berhasil",
                            Toast.LENGTH_LONG
                    ).show();

                    setResult(RESULT_OK);
                    finish();

                } else {

                    Toast.makeText(
                            EditPembeliActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }

            } catch (JSONException e) {

                e.printStackTrace();

                Toast.makeText(
                        EditPembeliActivity.this,
                        "Response tidak valid",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}