package defect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;
import com.seseorang.sweetalert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


import mods.model.PicEditModel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DefectForward extends AppCompatActivity {


    private String id_user, pwd;
    private Spinner spinnerPic;
    private int idPIC=0, sukses;
    private long mLastClickTime = 0;

    private ArrayList<String> picCaptionList;
    private ArrayAdapter<String> picAdapter;
    private ArrayList<PicEditModel> picList;
    private JSONArray pic;
    private String id_defect;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.defect_forward);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        picCaptionList  = new ArrayList<>();
        picList         = new ArrayList<>();

        spinnerPic  = findViewById(R.id.spin_pic);
        spinnerPic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idPIC   = picList.get(i).getId_pic();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        @SuppressWarnings("unchecked")
        HashMap<String,String> hashMap = (HashMap<String, String>) getIntent().getSerializableExtra("arrayList");

        ImageView gambar    = findViewById(R.id.gambar_defect);
        TextView nama_defect  = findViewById(R.id.nama_defect);
        Picasso.with(DefectForward.this).load(hashMap.get("gambar")).into(gambar);
        nama_defect.setText(hashMap.get("nama_defect"));
        TextView ket_defect = findViewById(R.id.ket_defect);
        ket_defect.setText(Html.fromHtml(hashMap.get("ket")));
        TextView status_defect  = findViewById(R.id.status_defect);
        id_user = hashMap.get("id_user");
        pwd     = hashMap.get("pwd");
        id_defect   = hashMap.get("id_defect");

        TextView ket_resiko = findViewById(R.id.ket_resiko);
        ket_resiko.setText(Html.fromHtml(hashMap.get("resiko")));

        switch (hashMap.get("status")) {
            case "Defect Baru":
                status_defect.setTextColor(Color.parseColor("#e12227"));
                break;
            case "Selesai":
                status_defect.setTextColor(Color.parseColor("#48a846"));
                break;
            default:
                status_defect.setTextColor(Color.parseColor("#F09609"));
                break;
        }


        status_defect.setText(hashMap.get("status"));



        Toolbar tl = findViewById(R.id.toolbar);
        TextView title_toolbar   = findViewById(R.id.title_toolbar);
        title_toolbar.setText(getResources().getText(R.string.forward_defect));
        tl.setNavigationOnClickListener(view -> onBackPressed());

        Button btnForward   = findViewById(R.id.btnSimpan);
        btnForward.setText(getResources().getText(R.string.btnForward));

        FetcDataSeputarDanLokasi();

        btnForward.setOnClickListener(view -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();
            SimpanForwad();
        });

    }


    private void SimpanForwad(){

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
                            Intent main = new Intent(DefectForward.this, DefectActivity.class);
                            main.putExtra("id_user", id_user);
                            main.putExtra("pwd", pwd);
                            startActivity(main);
                            finish();
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(DefectForward.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(DefectForward.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_defect", id_defect);
                params.put("id_pic", String.valueOf(idPIC));
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanForward");

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
        Intent main    =new Intent(DefectForward.this, DefectActivity.class);
        main.putExtra("id_user", id_user);
        main.putExtra("pwd", pwd);
        startActivity(main);
        finish();

        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


    private void FetcDataSeputarDanLokasi(){

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

                        /*PIC*/
                        pic  = j.getJSONArray(Configs.JSON_ARRAY_PIC);
                        for (int p = 0; p < pic.length(); p++) {
                            try {

                                JSONObject pi = pic.getJSONObject(p);

                                PicEditModel picModel = new PicEditModel();
                                picModel.setId_pic(pi.getInt("id_pic"));
                                picModel.setNama_pic(pi.getString("nama_pic"));
                                picCaptionList.add(pi.getString("nama_pic"));

                                picList.add(picModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        picAdapter  = new ArrayAdapter<>(DefectForward.this, R.layout.spinner_item, R.id.text1,picCaptionList);
                        spinnerPic.setAdapter(picAdapter);




                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(DefectForward.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("action", "GetSeputarDanLokasi");
                params.put("id_user", id_user);
                params.put("pwd", pwd);

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
