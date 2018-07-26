package defect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.lid.lib.LabelImageView;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;
import com.seseorang.sweetalert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import defect.adapter.DefectViewAdapter;
import defect.model.DefectViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DefectNotif extends AppCompatActivity implements com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {


    private TextView nama_defect, ket_defect, ket_resiko, txtLokasi, uploadby, tgl_target;
    private int sukses;
    SimpleDateFormat simpleDateFormat;
    private LabelImageView labelImageView;
    private int id_defect;
    private Button btnSetTarget,btnTambahStatus;
    private JSONArray result;
    private DefectViewAdapter defectViewAdapter;
    private ArrayList<DefectViewModel> defectViewList;
    private String id_user, pwd;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.defect_notif);

        handleIntent(getIntent());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        defectViewList  = new ArrayList<>();
        defectViewAdapter  = new DefectViewAdapter(this,defectViewList);

        RecyclerView recyclerView = findViewById(R.id.lv_defect);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(defectViewAdapter);


        btnSetTarget    = findViewById(R.id.btnSetTarget);
        labelImageView   = findViewById(R.id.gambar_defect_dua);
        nama_defect     =findViewById(R.id.nama_defect);
        ket_defect      =findViewById(R.id.ket_defect);
        ket_resiko      =findViewById(R.id.ket_resiko);
        txtLokasi       = findViewById(R.id.txtlokasi);
        uploadby        = findViewById(R.id.uploadby);
        tgl_target      =findViewById(R.id.txtTglTarget);
        simpleDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);

        btnSetTarget.setOnClickListener(view -> {

            Calendar calendar   = Calendar.getInstance();

            int curryear    = calendar.get(Calendar.YEAR);
            int currMonth   = calendar.get(Calendar.MONTH);
            int currDay     = calendar.get(Calendar.DATE);
            int nextYear    = curryear+2;
            int minYear     = curryear-1;

            new SpinnerDatePickerDialogBuilder()
                    .context(DefectNotif.this)
                    .callback(DefectNotif.this)
                    .spinnerTheme(R.style.DatePickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(curryear, currMonth, currDay)
                    .maxDate(nextYear, 0, 1)
                    .minDate(minYear, 0, 1)
                    .build()
                    .show();
        });

        btnTambahStatus = findViewById(R.id.btnTambahStatus);

        btnTambahStatus.setOnClickListener(view -> {
            Intent followUp = new Intent(DefectNotif.this, DefectFollowup.class);
            followUp.putExtra("id_defect", String.valueOf(id_defect));
            followUp.putExtra("dari", "notif");
            startActivity(followUp);
            finish();
        });

        Toolbar tl = findViewById(R.id.toolbar);
        TextView title_toolbar   = findViewById(R.id.title_toolbar);
        title_toolbar.setText(getResources().getText(R.string.toolbar_follow_up_defect));
        tl.setNavigationOnClickListener(view -> onBackPressed());

    }


    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        SimpanData(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

                    try {
                        j   = new JSONObject(response);
                        sukses = j.getInt("success");
                        String pesan    = j.getString("pesan");

                        if(sukses==1){
                            GetDataDefectNotif(id_defect);
                            Toast.makeText(DefectNotif.this,"Sukses Set Due Date", Toast.LENGTH_SHORT).show();
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(DefectNotif.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(DefectNotif.this, error.getMessage(), Toast.LENGTH_LONG).show();
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        String id_defect= intent.getStringExtra("id_data");
        if(id_defect!= null) {
            this.id_defect  = Integer.parseInt(id_defect);
            id_user = intent.getStringExtra("id_user");
            pwd     = intent.getStringExtra("pwd");
            GetDataDefectNotif(this.id_defect);
        }
    }


    private void GetDataDefectNotif(final int id_defect){

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
                        j = new JSONObject(response);
                        sukses = j.getInt("success");
                        String pesan = j.getString("pesan");
                        JSONArray hasil = j.getJSONArray(Configs.JSON_ARRAY);


                        if (sukses == 1) {
                            JSONObject a = hasil.getJSONObject(0);

                            ket_resiko.setText(Html.fromHtml(a.getString("resiko")));
                            ket_defect.setText(Html.fromHtml(a.getString("ket")));
                            nama_defect.setText(a.getString("nama_defect"));
                            txtLokasi.setText(a.getString("lokasi"));
                            uploadby.setText(a.getString("nama_lengkap"));
                            tgl_target.setText(a.getString("target"));

                            labelImageView.setLabelTextColor(getResources().getColor(R.color.white));
                            labelImageView.setLabelHeight(50);
                            labelImageView.setLabelTextSize(getResources().getDimensionPixelSize(R.dimen.text));
                            labelImageView.setLabelDistance(60);

                            if(a.getString("target").equals("")){
                                btnSetTarget.setVisibility(View.VISIBLE);
                                btnTambahStatus.setVisibility(View.GONE);
                            } else {
                                btnSetTarget.setVisibility(View.GONE);
                                btnTambahStatus.setVisibility(View.VISIBLE);
                            }

                            switch (a.getString("status_defect")) {
                                case "Defect Baru":
                                    labelImageView.setLabelBackgroundColor(getResources().getColor(R.color.red));
                                    break;
                                case "Selesai":
                                    labelImageView.setLabelBackgroundColor(getResources().getColor(R.color.dark_green));
                                    break;
                                default:
                                    labelImageView.setLabelBackgroundColor(getResources().getColor(R.color.colorOrange));
                                    break;
                            }

                            Picasso.with(DefectNotif.this).load(a.getString("gambar")).into(labelImageView);
                            labelImageView.setLabelText(a.getString("status_defect"));

                            FetcDataHistory(id_defect);

                        } else if (sukses == 2) {
                            final SweetAlertDialog sPesan = new SweetAlertDialog(DefectNotif.this, SweetAlertDialog.ERROR_TYPE);
                            sPesan.setTitleText(pesan);
                            sPesan.setCancelable(false);
                            sPesan.setCanceledOnTouchOutside(false);
                            sPesan.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    final SweetAlertDialog pError;
                    pError  = new SweetAlertDialog(DefectNotif.this, SweetAlertDialog.ERROR_TYPE);
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
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_defect", String.valueOf(id_defect));
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "GetDataDefectNotif");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void FetcDataHistory(final int id_mod_d){

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

                    try {
                        JSONObject j   = new JSONObject(response);
                        result  = j.getJSONArray(Configs.JSON_ARRAY);

                        sukses = j.getInt("success");
                        String pesan = j.getString("pesan");

                        if(sukses==1) {

                            for (int i = 0; i < result.length(); i++) {
                                try {

                                    JSONObject json = result.getJSONObject(i);

                                    final DefectViewModel mainModel = new DefectViewModel();
                                    mainModel.setTanggal(json.getString("tgl"));
                                    mainModel.setGambar(json.getString("gambar"));
                                    mainModel.setKet(json.getString("ket_defect"));
                                    defectViewList.add(mainModel);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            defectViewAdapter.notifyDataSetChanged();

                        } else if(sukses==2) {
                            Toast.makeText(DefectNotif.this, pesan, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();

                    final SweetAlertDialog pError;
                    pError  = new SweetAlertDialog(DefectNotif.this, SweetAlertDialog.ERROR_TYPE);
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

                params.put("action", "GetHistDefect");
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("id_mod_d", String.valueOf(id_mod_d));

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

}
