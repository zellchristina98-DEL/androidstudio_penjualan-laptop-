package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class tambahdatakreditactivity extends AppCompatActivity {
    EditText etKodeKredit, etTanggal, etCicilan;
    Spinner spinnerPembeli, spinnerPaket, spinnerlaptop;
    Button btnsimpan;
    ArrayList<String> listPembeli = new ArrayList<>();
    ArrayList<String> listPaket = new ArrayList<>();
    ArrayList<String> listlaptop = new ArrayList<>();
    ArrayList<String> valPembeli = new ArrayList<>();
    ArrayList<String> valPaket = new ArrayList<>();
    ArrayList<String> valLaptop = new ArrayList<>();

    ArrayList<Integer> hargalaptop = new ArrayList<>();
    ArrayList<Integer> tenor = new ArrayList<>();
    ArrayList<Double> uangmuka = new ArrayList<>();
    ArrayList<Double> bunga = new ArrayList<>();

    private static final String URL_TAMBAH = "http://192.168.18.12/penjualanlaptop/tambahkredit.php";
    private static final String URL_PEMBELI = "http://192.168.18.12/penjualanlaptop/tampilpembeli.php";
    private static final String URL_LAPTOP = "http://192.168.18.12/penjualanlaptop/tampillaptop.php";
    private static final String URL_PAKET = "http://192.168.18.12/penjualanlaptop/tampilpaket.php";
    int cicilanPerbulan = 0, totalCicil = 0, tenorDisplin = 0;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_tambah_beli_kredit);
        etKodeKredit = findViewById(R.id.editkodekredit);
        etTanggal = findViewById(R.id.edittanggal);
        etCicilan = findViewById(R.id.editbayar);

        spinnerPembeli = findViewById(R.id.spinnerktp);
        spinnerPaket = findViewById(R.id.spinnerpaket);
        spinnerlaptop = findViewById(R.id.spinnerlaptop);

        btnsimpan = findViewById(R.id.tombolsimpan);
        loadPembeli();
        loadlaptop();
        loadPaket();
        etTanggal.setOnClickListener(v -> showDataPicker());
        btnsimpan.setOnClickListener(v -> {
            String kodekredit = etKodeKredit.getText().toString().trim();
            String tanggal = etTanggal.getText().toString().trim();

            if (kodekredit.isEmpty() || tanggal.isEmpty() || cicilanPerbulan == 0) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
            } else {
                int posPembeli = spinnerPembeli.getSelectedItemPosition();
                int poslaptop = spinnerlaptop.getSelectedItemPosition();
                int pospaket = spinnerPaket.getSelectedItemPosition();
                String ktp = valPembeli.get(posPembeli);
                String kodelaptop = valLaptop.get(poslaptop);
                String kodepaket = valPaket.get(pospaket);

                simpanData(kodekredit, ktp, kodepaket, kodelaptop, tanggal);
            }
        });
        spinnerPaket.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> hitungCicilan()));
        spinnerlaptop.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> hitungCicilan()));
    }

    private void showDataPicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(tambahdatakreditactivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String tanggal = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    etTanggal.setText(tanggal);
                },
                year, month, day
        );
        dialog.show();
    }

    private void loadPembeli() {
        StringRequest req = new StringRequest(Request.Method.GET, URL_PEMBELI, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listPembeli.clear();
                valPembeli.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String ktp = obj.getString("ktp");
                    String nama = obj.getString("nama_pembeli");

                    listPembeli.add(ktp + " - " + nama);
                    valPembeli.add(ktp);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listPembeli);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPembeli.setAdapter(adapter);
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

    private void loadlaptop() {
        StringRequest req = new StringRequest(Request.Method.GET, URL_LAPTOP, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listlaptop.clear();
                valLaptop.clear();
                hargalaptop.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String kode = obj.getString("kode_laptop");
                    String type = obj.getString("type");
                    String merk = obj.getString("merk");
                    String warna = obj.getString("warna");
                    int harga = obj.getInt("harga");

                    listlaptop.add(kode + " = " + merk + " = " + type + " = " + warna + " = " + harga);
                    valLaptop.add(kode);
                    hargalaptop.add(harga);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listlaptop);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerlaptop.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.e("volley", error.toString()));

        Volley.newRequestQueue(this).add(req);
    }

    private void loadPaket() {
        StringRequest req = new StringRequest(Request.Method.GET, URL_PAKET, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listPaket.clear();
                valPaket.clear();
                uangmuka.clear();
                tenor.clear();
                bunga.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String kode = obj.getString("kode_paket");
                    Double dp = obj.getDouble("uang_muka");
                    int tnr = obj.getInt("tenor");
                    Double bng = obj.getDouble("bunga_cicilan");

                    listPaket.add(kode + " - DP" + dp + " %tenor:" + tnr + "Bunga" + bng + "%");
                    valPaket.add(kode);
                    uangmuka.add(dp);
                    tenor.add(tnr);
                    bunga.add(bng);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listPaket);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPaket.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.e("volley", error.toString()));

        Volley.newRequestQueue(this).add(req);
    }

    private void hitungCicilan() {
        if (spinnerPaket.getSelectedItemPosition() >= 0 && spinnerlaptop.getSelectedItemPosition() >= 0) {
            if (!hargalaptop.isEmpty() && !uangmuka.isEmpty() &&
                    spinnerlaptop.getSelectedItemPosition() < hargalaptop.size() &&
                    spinnerPaket.getSelectedItemPosition() < uangmuka.size()) {

                int posPaket = spinnerPaket.getSelectedItemPosition();
                int poslaptop = spinnerlaptop.getSelectedItemPosition();
                int harga = hargalaptop.get(poslaptop);
                double dppersen = uangmuka.get(posPaket);
                tenorDisplin = tenor.get(posPaket);
                double bng = bunga.get(posPaket);

                double dpRupiah = harga * dppersen / 100.0;
                double hutang = harga - dpRupiah;
                double bungaPerBulan = (hutang * bng / 100.0) / 12.0;
                double bungatotal = bungaPerBulan * tenorDisplin;

                totalCicil = (int) (hutang + bungatotal);
                cicilanPerbulan = totalCicil / tenorDisplin;

                etCicilan.setText("Rp" + cicilanPerbulan);
            }
        }
    }

    private void simpanData(final String kodeKredit, final String ktp, final String kodepaket, final String kodelaptop, final String tanggal) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TAMBAH, response -> {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            if (response.contains("Data berhasil disimpan")) {
                startActivity(new Intent(tambahdatakreditactivity.this, DataBeliKreditActivity.class));
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
                params.put("kode_kredit", kodeKredit);
                params.put("ktp", ktp);
                params.put("kode_paket", kodepaket);
                params.put("kode_laptop", kodelaptop);
                params.put("tanggal_kredit", tanggal);
                params.put("bayar_kredit", String.valueOf(cicilanPerbulan));
                params.put("tenor", String.valueOf(tenorDisplin));
                params.put("cicilan", String.valueOf(totalCicil));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public static class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private final Runnable onSelected;

        public SimpleItemSelectedListener(Runnable onSelected) {
            this.onSelected = onSelected;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            onSelected.run();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}