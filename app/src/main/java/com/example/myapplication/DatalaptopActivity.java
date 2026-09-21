package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
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

        listView.setOnItemClickListener((parent, view, position, id) -> {
            showOptionDialog(position);
        });
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
                    String type = obj.getString("type");
                    String merk = obj.getString("merk");
                    String warna = obj.getString("warna");
                    String harga = obj.getString("harga");

                    idList.add(kode);
                    itemList.add("Nama\t" + type +
                            "\nMerk\t" + merk +
                            "\nSpesifikasi\t" + warna +
                            "\nHarga\t" + harga +
                            "\n\n");
                }

                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(DatalaptopActivity.this, "Gagal menampilkan data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showOptionDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Pilihan aksi")
                .setItems(new CharSequence[]{"detail", "Edit", "Hapus"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showDetail(position);
                            break;
                        case 1:
                            editItem(position);
                            break;
                        case 2:
                            ConfirmDelete(position);
                            break;
                    }
                });

        builder.show();
    }

    private void showDetail(int position) {
        String detail = itemList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Detail Laptop")
                .setMessage(detail)
                .setPositiveButton("OK", null)
                .show();
    }

    public static final int REQUEST_EDIT = 1;

    private void editItem(int position) {
        Toast.makeText(this, "Edit:" + itemList.get(position), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(DatalaptopActivity.this, EditlaptopActivity.class);

        intent.putExtra("kode_laptop", idList.get(position));

        String[] lines = itemList.get(position).split("\n");

        intent.putExtra("type", lines[0].replace("Nama\t", "").trim());
        intent.putExtra("merk", lines[1].replace("Merk\t", "").trim());
        intent.putExtra("warna", lines[2].replace("Spesifikasi\t", "").trim());
        intent.putExtra("harga", lines[3].replace("Harga\t", "").trim());

        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            new tampildata().execute();
        }
    }

    private void ConfirmDelete(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah anda yakin untuk Menghapus data ini?")
                .setPositiveButton("hapus", (dialog, which) -> deleteItem(position))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteItem(int position) {
        String id = idList.get(position);
        new HapusDatalaptop().execute(id, String.valueOf(position));
    }

    private class HapusDatalaptop extends AsyncTask<String, Void, String> {
        int positionToDelete;

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            positionToDelete = Integer.parseInt(params[1]);

            try {
                URL url = new URL("http://192.168.18.12/penjualanlaptop/hapuslaptop.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String postData = "kode_laptop=" + URLEncoder.encode(id, "UTF-8");

                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader reader;

                if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

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
                return "{\"status\":0,\"message\":\"" + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject response = new JSONObject(result);
                int status = response.getInt("status");
                String message = response.getString("message");

                if (status == 1) {
                    itemList.remove(positionToDelete);
                    idList.remove(positionToDelete);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(DatalaptopActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DatalaptopActivity.this, message, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                Toast.makeText(DatalaptopActivity.this, "Response PHP: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}