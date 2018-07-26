package defect;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;
import com.seseorang.demos.utils.EndlessRecyclerOnScrollListener;
import com.seseorang.sweetalert.SweetAlertDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import defect.adapter.DefectAdapter;
import defect.model.DefectModel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DefectActivity extends AppCompatActivity  implements com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    private JSONArray result;
    private DefectAdapter defectAdapter;
    private ArrayList<DefectModel> defectList;
    private int hal=1;
    private int sukses, id_defect=0;

    private TextView dateTextView;
    private SimpleDateFormat simpleDateFormat;
    private String id_user, pwd;
    private Boolean isSetDate=false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.defect);

        setTitle("LIST DEFECT");

        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        defectList = new ArrayList<>();
        dateTextView    = findViewById(R.id.date_textview);
        simpleDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);
        id_user         = getIntent().getStringExtra("id_user");
        pwd             = getIntent().getStringExtra("pwd");

        RecyclerView recyclerView = findViewById(R.id.lv_defect);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        defectAdapter = new DefectAdapter(this, defectList);
        recyclerView.setAdapter(defectAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                hal++;
                FetcDataDefect(hal);
            }
        });

        Toolbar tl = findViewById(R.id.toolbar);
        TextView title_toolbar   = findViewById(R.id.title_toolbar);
        title_toolbar.setText(getResources().getText(R.string.toolbar_list_defect));
        tl.setNavigationOnClickListener(view -> onBackPressed());

        FetcDataDefect(hal);

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void FetcDataDefect(final int hal){

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

                    if(defectList.size()>0 & isSetDate ){
                        defectList.clear();
                    }

                    try {
                        JSONObject j   = new JSONObject(response);
                        result  = j.getJSONArray(Configs.JSON_ARRAY);

                        sukses = j.getInt("success");
                        String pesan = j.getString("pesan");

                        if(sukses==1) {

                            for (int i = 0; i < result.length(); i++) {
                                try {

                                    JSONObject json = result.getJSONObject(i);

                                    final DefectModel mainModel = new DefectModel();
                                    mainModel.setId_defect(json.getInt("id_mod_d"));
                                    mainModel.setTanggal(json.getString("tgl"));
                                    mainModel.setNama_defect(json.getString("nama_defect"));
                                    mainModel.setGambar(json.getString("gambar"));
                                    mainModel.setKet_defect(json.getString("ket_defect"));
                                    mainModel.setNama_lengkap(json.getString("nama_lengkap"));
                                    mainModel.setPic(json.getString("nama_pic"));
                                    mainModel.setLokasi(json.getString("lokasi"));
                                    mainModel.setStatus_defect(json.getString("status_defek"));
                                    mainModel.setDue_date(json.getString("due_date"));
                                    mainModel.setLevel_user(json.getInt("level_user"));
                                    mainModel.setResiko(json.getString("resiko"));
                                    mainModel.setTgl_selesai(json.getString("tgl_selesai"));
                                    defectList.add(mainModel);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            defectAdapter.notifyDataSetChanged();

                        } else if(sukses==2) {
                            Toast.makeText(DefectActivity.this, pesan, Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();

                    final SweetAlertDialog pError;
                    pError  = new SweetAlertDialog(DefectActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pError.setCanceledOnTouchOutside(false);
                    pError.setCancelable(false);

                    if(error instanceof NetworkError) {
                        pError.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError.show();
                    } else if(error instanceof ServerError){
                        pError.setTitleText("The server could not be found. Please try again after some time!");
                        pError.show();
                    } else if(error instanceof AuthFailureError){
                        pError.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError.show();
                    } else if(error instanceof ParseError){
                        pError.setTitleText("Parsing error! Please try again after some time!");
                        pError.show();
                    } else if(error instanceof TimeoutError){
                        pError.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams()  {

                Map<String,String> params = new Hashtable<>();

                params.put("action", "GetListDefect");
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("p", String.valueOf(hal));

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void viewDefect(int postRec, int id_defect){
        Intent defect_view = new Intent(DefectActivity.this, DefectView.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("gambar", defectList.get(postRec).getGambar());
        params.put("status", defectList.get(postRec).getStatus_defect());
        params.put("ket", defectList.get(postRec).getKet_defect());
        params.put("nama_defect", defectList.get(postRec).getNama_defect());
        params.put("id_defect", String.valueOf(id_defect));
        params.put("resiko", defectList.get(postRec).getResiko());
        params.put("id_user", id_user);
        params.put("pwd", pwd);

        defect_view.putExtra("arrayList", params);
        startActivity(defect_view);
    }

    public void ForwardDefect(int postRec, int id_defect){
        Intent defect_view = new Intent(DefectActivity.this, DefectForward.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("gambar", defectList.get(postRec).getGambar());
        params.put("status", defectList.get(postRec).getStatus_defect());
        params.put("ket", defectList.get(postRec).getKet_defect());
        params.put("nama_defect", defectList.get(postRec).getNama_defect());
        params.put("id_defect", String.valueOf(id_defect));
        params.put("resiko", defectList.get(postRec).getResiko());
        params.put("id_user", id_user);
        params.put("pwd", pwd);

        defect_view.putExtra("arrayList", params);
        startActivity(defect_view);
        finish();
    }


    public void SetDueDateDefect(int id_defect){

        Calendar calendar   = Calendar.getInstance();
        this.id_defect = id_defect;

        int curryear    = calendar.get(Calendar.YEAR);
        int currMonth   = calendar.get(Calendar.MONTH);
        int currDay     = calendar.get(Calendar.DATE);
        int nextYear    = curryear+2;
        int minYear     = curryear-1;

        new SpinnerDatePickerDialogBuilder()
                .context(DefectActivity.this)
                .callback(DefectActivity.this)
                .spinnerTheme(R.style.DatePickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(curryear, currMonth, currDay)
                .maxDate(nextYear, 0, 1)
                .minDate(minYear, 0, 1)
                .build()
                .show();
    }


    public void FollowUpDefect(int id_defect){
        Intent followUp = new Intent(DefectActivity.this, DefectFollowup.class);
        followUp.putExtra("id_defect", String.valueOf(id_defect));
        followUp.putExtra("dari", "main");
        startActivity(followUp);
        finish();
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
        finish();
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        dateTextView.setText(simpleDateFormat.format(calendar.getTime()));
        SimpanData(simpleDateFormat.format(calendar.getTime()));
    }


    private void SimpanData(String tgl){

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
                    isSetDate =true;

                    try {
                        j   = new JSONObject(response);
                        sukses = j.getInt("success");
                        String pesan    = j.getString("pesan");


                        if(sukses==1){
                            new Handler().postDelayed(() -> FetcDataDefect(hal),1000);
                            Toast.makeText(DefectActivity.this,"Sukses Set Due Date", Toast.LENGTH_SHORT).show();
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(DefectActivity.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(DefectActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_defect", String.valueOf(id_defect));
                params.put("due_date", tgl);
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanDueDateDefect");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}