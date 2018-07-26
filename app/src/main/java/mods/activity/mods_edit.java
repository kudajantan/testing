package mods.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;
import com.seseorang.demos.utils.NumberTextWatcher;
import com.seseorang.sweetalert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import mods.adapter.CariPetugasAdapter;
import mods.model.ShiftModel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class mods_edit extends AppCompatActivity {

    private AutoCompleteTextView petugas_satu, petugas_dua;
    private int id_petugas_satu=0, id_petugas_dua=0, id_shift, sukses, id_mod=0;
    private CariPetugasAdapter cariPetugasAdapter, cariPetugasAdapterDua;
    private EditText mobil, valet, motor, taxi, mobil_box, sec_office_plan, sec_office_actual;
    private EditText sec_mall_plan, sec_mall_actual, sec_kawasan_plan, sec_kawasan_actual;
    private EditText cs_doorman_team_plan, cs_doorman_team_actual, parking_team_plan, parking_team_actual;
    private EditText vallet_team_plan, vallet_team_actual, eng_team_plan, eng_team_actual;
    private EditText cso_plan, cso_actual, csm_plan, csm_actual, gro_team_plan, gro_team_actual;
    private EditText ops_team_plan, ops_team_actual, landscape_team_plan, landscape_team_actual;
    private EditText terminix_team_plan, terminix_team_actual;


    private Spinner spinnerShift;
    private ArrayList<ShiftModel> shiftList;
    private ArrayList<String> shiftCaptionList;
    private ArrayAdapter<String> shiftAdapter;
    private long mLastClickTime = 0;
    private JSONArray result;
    private String info_tambahan="";
    private String id_user, pwd;


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.mods_edit);




        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        shiftList   = new ArrayList<>();
        shiftCaptionList    = new ArrayList<>();

        id_mod          =Integer.parseInt(getIntent().getStringExtra("id_mod"));
        id_user         =getIntent().getStringExtra("id_user");
        pwd             =getIntent().getStringExtra("pwd");


        cariPetugasAdapter = new CariPetugasAdapter(mods_edit.this, R.layout.dropdown_item, id_user, pwd);
        cariPetugasAdapterDua =  new CariPetugasAdapter(mods_edit.this, R.layout.dropdown_item, id_user, pwd);


        Button btnLanjut        = findViewById(R.id.btnSimpan);
        btnLanjut.setText(getResources().getText(R.string.btnLanjut));
        petugas_satu    = findViewById(R.id.nama_petugas_satu);
        petugas_dua     = findViewById(R.id.nama_petugas_dua);
        spinnerShift    = findViewById(R.id.spinner_shift);
        mobil           = findViewById(R.id.mobil);
        valet           = findViewById(R.id.valet);
        motor           = findViewById(R.id.motor);
        taxi            = findViewById(R.id.taxi);
        mobil_box       = findViewById(R.id.mobil_box);
        sec_office_plan = findViewById(R.id.sec_office_plan);
        sec_office_actual   = findViewById(R.id.sec_office_actual);
        sec_mall_plan   = findViewById(R.id.sec_mall_plan);
        sec_mall_actual = findViewById(R.id.sec_mall_actual);
        sec_kawasan_plan   = findViewById(R.id.sec_kawasan_plan);
        sec_kawasan_actual    = findViewById(R.id.sec_kawasan_actual);
        cs_doorman_team_plan    = findViewById(R.id.cs_doorman_team_plan);
        cs_doorman_team_actual  = findViewById(R.id.cs_doorman_team_actual);
        parking_team_plan   = findViewById(R.id.parking_team_plan);
        parking_team_actual = findViewById(R.id.parking_team_actual);
        vallet_team_plan    = findViewById(R.id.vallet_team_plan);
        vallet_team_actual  = findViewById(R.id.vallet_team_actual);
        eng_team_plan       = findViewById(R.id.eng_team_plan);
        eng_team_actual     = findViewById(R.id.eng_team_actual);
        cso_plan    = findViewById(R.id.cso_plan);
        cso_actual  = findViewById(R.id.cso_actual);
        csm_plan    = findViewById(R.id.csm_plan);
        csm_actual  = findViewById(R.id.csm_actual);
        gro_team_plan   = findViewById(R.id.gro_team_plan);
        gro_team_actual = findViewById(R.id.gro_team_actual);
        ops_team_plan   = findViewById(R.id.ops_team_plan);
        ops_team_actual = findViewById(R.id.ops_team_actual);
        landscape_team_plan = findViewById(R.id.landscape_team_plan);
        landscape_team_actual   = findViewById(R.id.landscape_team_actual);
        terminix_team_plan  = findViewById(R.id.terminix_team_plan);
        terminix_team_actual    = findViewById(R.id.terminix_team_actual);


        mobil.addTextChangedListener(new NumberTextWatcher(mobil));
        motor.addTextChangedListener(new NumberTextWatcher(motor));
        valet.addTextChangedListener(new NumberTextWatcher(valet));
        taxi.addTextChangedListener(new NumberTextWatcher(taxi));
        mobil_box.addTextChangedListener(new NumberTextWatcher(mobil_box));

        petugas_satu.setThreshold(3);
        petugas_satu.setAdapter(cariPetugasAdapter);

        petugas_satu.setOnItemClickListener((adapterView, view, i, l) -> {
            id_petugas_satu = Objects.requireNonNull(cariPetugasAdapter.getItem(i)).getId_aphris();
            petugas_satu.setText(Objects.requireNonNull(cariPetugasAdapter.getItem(i)).getNama_petugas());
        });

        petugas_dua.setThreshold(3);
        petugas_dua.setAdapter(cariPetugasAdapterDua);
        petugas_dua.setOnItemClickListener((adapterView, view, i, l) -> {
            id_petugas_dua = Objects.requireNonNull(cariPetugasAdapterDua.getItem(i)).getId_aphris();
            petugas_dua.setText(Objects.requireNonNull(cariPetugasAdapterDua.getItem(i)).getNama_petugas());
        });

       spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               id_shift = shiftList.get(i).getId_shift();
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

        btnLanjut.setOnClickListener(view -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();

            SimpanData();
        });


        Toolbar tl = findViewById(R.id.toolbar);
        TextView judul    = findViewById(R.id.title_toolbar);
        judul.setText(getResources().getText(R.string.toolbar_edit_mod));
        tl.setNavigationOnClickListener(view -> onBackPressed());
        FetcDataEdit(id_mod);

        //new Handler().postDelayed(() -> FetcDataShift(), 1500);


    }


    private void FetcDataEdit(int id_mod){
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
                        JSONArray hasil = j.getJSONArray(Configs.JSON_ARRAY);

                        if(sukses==1){

                            JSONObject a    = hasil.getJSONObject(0);
                            id_petugas_satu = a.getInt("id_petugas1");
                            petugas_satu.setText(a.getString("petugas1"));
                            id_petugas_dua  = a.getInt("id_petugas2");
                            petugas_dua.setText(a.getString("petugas2"));
                            id_shift    =a.getInt("shift");
                            mobil.setText(a.getString("mobil"));
                            motor.setText(a.getString("motor"));
                            taxi.setText(a.getString("taxi"));
                            mobil_box.setText(a.getString("mobil_box"));
                            valet.setText(a.getString("vallet"));
                            csm_plan.setText(a.getString("cs_mall_plan"));
                            csm_actual.setText(a.getString("cs_mall_actual"));
                            cso_plan.setText(a.getString("cs_office_plan"));
                            cso_actual.setText(a.getString("cs_office_actual"));
                            landscape_team_plan.setText(a.getString("landscape_team_plan"));
                            landscape_team_actual.setText(a.getString("landscape_team_actual"));
                            sec_office_plan.setText(a.getString("security_office_plan"));
                            sec_office_actual.setText(a.getString("security_office_actual"));
                            sec_mall_plan.setText(a.getString("security_mall_plan"));
                            sec_mall_actual.setText(a.getString("security_mall_actual"));
                            sec_kawasan_plan.setText(a.getString("security_kawasan_plan"));
                            sec_kawasan_actual.setText(a.getString("security_kawasan_actual"));
                            parking_team_plan.setText(a.getString("parking_team_plan"));
                            parking_team_actual.setText(a.getString("parking_team_actual"));
                            vallet_team_plan.setText(a.getString("vallet_team_plan"));
                            vallet_team_actual.setText(a.getString("vallet_team_actual"));
                            gro_team_plan.setText(a.getString("garbage_room_team_plan"));
                            gro_team_actual.setText(a.getString("garbage_room_team_actual"));
                            terminix_team_plan.setText(a.getString("terminix_team_plan"));
                            terminix_team_actual.setText(a.getString("terminix_team_actual"));
                            cs_doorman_team_plan.setText(a.getString("cs_doorman_team_plan"));
                            cs_doorman_team_actual.setText(a.getString("cs_doorman_team_actual"));
                            eng_team_plan.setText(a.getString("eng_team_plan"));
                            eng_team_actual.setText(a.getString("eng_team_actual"));
                            ops_team_plan.setText(a.getString("ro_team_plan"));
                            ops_team_actual.setText(a.getString("ro_team_actual"));
                            info_tambahan   = a.getString("info_tambahan");

                            FetcDataShift();


                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(mods_edit.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(mods_edit.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_mod",String.valueOf(id_mod));
                params.put("action", "FetchDataEdit");
                params.put("id_user", id_user);
                params.put("pwd", pwd);

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                            Intent info = new Intent(mods_edit.this, mods_add_informasi.class);
                            info.putExtra("id_mod", String.valueOf(id_mod));
                            info.putExtra("dari","2");
                            info.putExtra("info_tambahan", info_tambahan);
                            info.putExtra("id_user", id_user);
                            info.putExtra("pwd", pwd);
                            startActivity(info);
                            finish();
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(mods_edit.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(mods_edit.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("mobil", mobil.getText().toString().trim());
                params.put("vallet", valet.getText().toString().trim());
                params.put("motor", motor.getText().toString().trim());
                params.put("taxi", taxi.getText().toString().trim());
                params.put("mobil_box", mobil_box.getText().toString().trim());
                params.put("sec_office_plan", sec_office_plan.getText().toString().trim());
                params.put("sec_office_actual", sec_office_actual.getText().toString().trim());
                params.put("sec_mall_plan", sec_mall_plan.getText().toString().trim());
                params.put("sec_mall_actual", sec_mall_actual.getText().toString().trim());
                params.put("sec_kawasan_plan", sec_kawasan_plan.getText().toString().trim());
                params.put("sec_kawasan_actual", sec_kawasan_actual.getText().toString().trim());
                params.put("cs_doorman_plan", cs_doorman_team_plan.getText().toString().trim());
                params.put("cs_doorman_actual", cs_doorman_team_actual.getText().toString().trim());
                params.put("parkir_team_plan", parking_team_plan.getText().toString().trim());
                params.put("parkir_team_actual", parking_team_actual.getText().toString().trim());
                params.put("vallet_team_plan", vallet_team_plan.getText().toString().trim());
                params.put("vallet_team_actual", vallet_team_actual.getText().toString().trim());
                params.put("eng_team_plan", eng_team_plan.getText().toString().trim());
                params.put("eng_team_actual", eng_team_actual.getText().toString().trim());
                params.put("cleaning_service_office_plan", cso_plan.getText().toString().trim());
                params.put("cleaning_service_office_actual", cso_actual.getText().toString().trim());
                params.put("cleaning_service_mall_plan", csm_plan.getText().toString().trim());
                params.put("cleaning_service_mall_actual", csm_actual.getText().toString().trim());
                params.put("garbage_room_plan", gro_team_plan.getText().toString().trim());
                params.put("garbage_room_actual", gro_team_actual.getText().toString().trim());
                params.put("ro_plan", ops_team_plan.getText().toString().trim());
                params.put("ro_actual", ops_team_actual.getText().toString().trim());
                params.put("landscape_plan", landscape_team_plan.getText().toString().trim());
                params.put("landscape_actual", landscape_team_actual.getText().toString().trim());
                params.put("terminix_plan", terminix_team_plan.getText().toString().trim());
                params.put("terminix_actual", terminix_team_actual.getText().toString().trim());
                params.put("petugas_satu", petugas_satu.getText().toString().trim());
                params.put("petugas_dua", petugas_dua.getText().toString().trim());
                params.put("id_petugas_satu", String.valueOf(id_petugas_satu));
                params.put("id_petugas_dua", String.valueOf(id_petugas_dua));
                params.put("shift", String.valueOf(id_shift));
                params.put("id_master", String.valueOf(id_mod));
                params.put("mstat",String.valueOf(2));
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanMods");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void FetcDataShift(){

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

                        result  = j.getJSONArray(Configs.JSON_ARRAY);


                        for (int i = 0; i < result.length(); i++) {
                            try {

                                JSONObject json = result.getJSONObject(i);

                                ShiftModel shiftModel = new ShiftModel();
                                shiftModel.setId_shift(json.getInt("id_shift"));
                                shiftModel.setNama_shift(json.getString("nama_shift"));
                                shiftCaptionList.add(json.getString("nama_shift"));

                                shiftList.add(shiftModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        shiftAdapter  = new ArrayAdapter<>(mods_edit.this, R.layout.spinner_item, R.id.text1,  shiftCaptionList);
                        spinnerShift.setAdapter(shiftAdapter);
                        for(int pos =shiftAdapter.getCount(); pos>=0; pos--){
                            if(shiftAdapter.getItemId(pos)== id_shift){
                                spinnerShift.setSelection(pos);
                                break;
                            }
                        }



                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(mods_edit.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("action", "GetShift");
                params.put("id_user", id_user);
                params.put("pwd", pwd);

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
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
