package harian.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;
import com.seseorang.sweetalert.SweetAlertDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;


import mods.adapter.CariPetugasAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class harian_add extends AppCompatActivity {

    private AutoCompleteTextView cari_petugas;
    private int id_petugas=0;
    private CariPetugasAdapter cariPetugasAdapter;
    private String id_user, pwd;
    private int sukses;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.harian_add);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        id_user     = getIntent().getStringExtra("id_user");
        pwd         = getIntent().getStringExtra("pwd");

        cari_petugas    = findViewById(R.id.nama_petugas_satu);
        cariPetugasAdapter  = new CariPetugasAdapter(this, R.layout.dropdown_item, id_user, pwd);
        cari_petugas.setThreshold(3);
        cari_petugas.setAdapter(cariPetugasAdapter);

        cari_petugas.setOnItemClickListener((adapterView, view, i, l) -> {
            id_petugas = Objects.requireNonNull(cariPetugasAdapter.getItem(i)).getId_aphris();
            cari_petugas.setText(Objects.requireNonNull(cariPetugasAdapter.getItem(i)).getNama_petugas());
        });

        Toolbar tl = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.title_toolbar);
        textView.setText(getResources().getText(R.string.toolbar_defect_harian));
        tl.setNavigationOnClickListener(view -> onBackPressed());

        Button btnLanjut        = findViewById(R.id.btnSimpan);
        btnLanjut.setText(getResources().getText(R.string.btnLanjut));

        btnLanjut.setOnClickListener(view -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime < 7000 ){
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();

            SimpanData();
        });

    }


    private void SimpanData(){

        final SweetAlertDialog pDialog;

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.MODS_URL,
                response -> {
                    pDialog.dismiss();
                    JSONObject j;

                    try {
                        j   = new JSONObject(response);
                        sukses = j.getInt("success");
                        String pesan    = j.getString("pesan");

                        if(sukses==1){

                            Intent info = new Intent(harian_add.this, harian_add_item.class);
                            info.putExtra("id_mod", String.valueOf(j.getInt("id_mod")));
                            info.putExtra("dari", "add");
                            info.putExtra("id_lokasi", String.valueOf(0));
                            info.putExtra("id_user", id_user);
                            info.putExtra("pwd", pwd);
                            startActivity(info);
                            finish();

                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(harian_add.this, SweetAlertDialog.ERROR_TYPE);
                            sPesan.setTitleText(pesan);
                            sPesan.setCancelable(false);
                            sPesan.setCanceledOnTouchOutside(false);
                            sPesan.show();

                        }

                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(harian_add.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_petugas", String.valueOf(id_petugas));
                params.put("petugas_satu", cari_petugas.getText().toString().trim());
                params.put("mstat",String.valueOf(1));
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanMasterHarian");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent harian_main  = new Intent(harian_add.this, harian_main.class);
        harian_main.putExtra("id_user", id_user);
        harian_main.putExtra("pwd", pwd);
        startActivity(harian_main);
        finish();
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}