package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class tambahdatabelicashactivity extends AppCompatActivity {
    EditText etKodeCash, etTanggal;
    Spinner spinnerktp, spinnerlaptop;
    Button btnSimpan;

    ArrayList<String> listPembeli = new ArrayList<>();
    ArrayList<String> listLaptop = new ArrayList<>();
    ArrayList<String> listKtpValue = new ArrayList<>();
    ArrayList<String> listLaptopValue = new ArrayList<>();

    private static final String Url_TAMBAH = "http://192.168.18.12/penjualanlaptop/tambahcash.php";
    private static final String Url_PEMBELI = "http://192.168.18.12/penjualanlaptop/tampilpembeli.php";
    private static final String Url_LAPTOP = "http://192.168.18.12/penjualanlaptop/tampillaptop.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_beli_cash);

        etKodeCash = findViewById(R.id.editkodecash);
        etTanggal = findViewById(R.id.edittanggal);
        spinnerktp = findViewById(R.id.spinnerktp);
        spinnerlaptop = findViewById(R.id.spinnerkodelaptop);
        btnSimpan = findViewById(R.id.tombolsimpan);

        loadPembeli();
        loadLaptop();

        etTanggal.setOnClickListener(v -> showDataPicker());

        btnSimpan.setOnClickListener(v -> {
            String KodeCash = etKodeCash.getText().toString().trim();
            String tanggal = etTanggal.getText().toString().trim();

            if (KodeCash.isEmpty() || tanggal.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
            } else {
                int posPembeli = spinnerktp.getSelectedItemPosition();
                int poslaptop = spinnerlaptop.getSelectedItemPosition();

                if (listKtpValue.isEmpty() || listLaptopValue.isEmpty()) {
                    Toast.makeText(this, "Data pembeli atau laptop belum siap", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ktp = listKtpValue.get(posPembeli);
                String kodelaptop = listLaptopValue.get(poslaptop);

                simpanData(KodeCash, ktp, kodelaptop, tanggal);
            }
        });
    }

    private void showDataPicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(tambahdatabelicashactivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String tanggal = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    etTanggal.setText(tanggal);
                },
                year, month, day
        );
        dialog.show();
    }

    private void loadPembeli() {
        StringRequest req = new StringRequest(Request.Method.GET, Url_PEMBELI, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listPembeli.clear();
                listKtpValue.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String ktp = obj.getString("ktp");
                    String nama = obj.getString("nama_pembeli");

                    listPembeli.add(ktp + " - " + nama);
                    listKtpValue.add(ktp);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listPembeli);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerktp.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing KTP: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Log.e("volley", error.toString());
            Toast.makeText(this, "Gagal memuat KTP: " + error.toString(), Toast.LENGTH_LONG).show();
        });

        Volley.newRequestQueue(this).add(req);
    }
    private void loadLaptop() {
        StringRequest req = new StringRequest(Request.Method.GET, Url_LAPTOP, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listLaptop.clear();
                listLaptopValue.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String kode = obj.getString("kode_laptop");
                    String type = obj.getString("type");
                    String merk = obj.getString("merk");
                    String warna = obj.getString("warna");
                    String harga = obj.getString("harga");

                    listLaptop.add(kode + " = " + merk + " = " + type + " = " + warna + " = " + harga);
                    listLaptopValue.add(kode);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listLaptop);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerlaptop.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.e("volley", error.toString()));

        Volley.newRequestQueue(this).add(req);
    }

    private void simpanData(final String kodecash, final String ktp, final String kodelaptop, final String tanggal) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url_TAMBAH, response -> {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            if (response.contains("Data berhasil disimpan")) {
                startActivity(new Intent(tambahdatabelicashactivity.this, DataBeliCashActivity.class));
                finish();
            }
        },
                error -> {
                    Toast.makeText(this, "gagal menyimpan data!", Toast.LENGTH_SHORT).show();
                    Log.e("Volley error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_cash", kodecash);
                params.put("ktp", ktp);
                params.put("kode_laptop", kodelaptop);
                params.put("cash_tgl", tanggal);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}