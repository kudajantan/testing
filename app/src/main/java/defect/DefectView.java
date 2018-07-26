package defect;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
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
import com.seseorang.sweetalert.SweetAlertDialog;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


import defect.adapter.DefectViewAdapter;
import defect.model.DefectViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DefectView extends AppCompatActivity {

    private JSONArray result;
    private int sukses;

    private DefectViewAdapter defectViewAdapter;
    private ArrayList<DefectViewModel> defectViewList;
    private String id_user, pwd;


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.defect_view);

        setTitle("View Defect");

        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Toolbar tl = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.title_toolbar);
        textView.setText(getResources().getText(R.string.toolbar_view_defect));
        tl.setNavigationOnClickListener(view -> onBackPressed());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        defectViewList = new ArrayList<>();
        defectViewAdapter  = new DefectViewAdapter(this,defectViewList);

        RecyclerView recyclerView = findViewById(R.id.lv_defect);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(defectViewAdapter);


        @SuppressWarnings("unchecked")
        HashMap<String,String> hashMap = (HashMap<String, String>) getIntent().getSerializableExtra("arrayList");


        ImageView gambar    = findViewById(R.id.gambar_defect);
        TextView nama_defect  = findViewById(R.id.nama_defect);
        Picasso.with(DefectView.this).load(hashMap.get("gambar")).into(gambar);
        nama_defect.setText(hashMap.get("nama_defect"));
        TextView ket_defect = findViewById(R.id.ket_defect);
        ket_defect.setText(Html.fromHtml(hashMap.get("ket")));
        TextView status_defect  = findViewById(R.id.status_defect);

        id_user     = hashMap.get("id_user");
        pwd         = hashMap.get("pwd");

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
        FetcDataHistory(Integer.parseInt(hashMap.get("id_defect")));

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
                            Toast.makeText(DefectView.this, pesan, Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();

                    final SweetAlertDialog pError;
                    pError  = new SweetAlertDialog(DefectView.this, SweetAlertDialog.ERROR_TYPE);
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
        finish();
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
