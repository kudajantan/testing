package login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;


import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GantiPassword extends AppCompatActivity {

    private String id_user, pwd;
    private EditText pwd_lama, pwd_baru, pwd_baru_ulang;
    private SweetAlertDialog pDialog, pError, pError2;
    private int sukses;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.ganti_password);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        id_user     = getIntent().getStringExtra("id_user");
        pwd         = getIntent().getStringExtra("pwd");

        pwd_lama    =findViewById(R.id.pwd_lama);
        pwd_baru    =findViewById(R.id.pwd_baru);
        pwd_baru_ulang  = findViewById(R.id.pwd_baru_ulang);







        Toolbar tl = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.title_toolbar);
        textView.setText(getResources().getText(R.string.ganti_passwowrd));
        tl.setNavigationOnClickListener(view -> onBackPressed());

        ImageView btnSubmit   = findViewById(R.id.btnSimpan);
        //btnSubmit.setText(getResources().getText(R.string.btnSubmit));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();
                SimpanData();
            }
        });

    }


    private void SimpanData(){

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.setTitleText("Loading...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.MODS_URL,
                response -> {

                    pDialog.dismiss();
                    JSONObject j;
                    try {
                        j = new JSONObject(response);

                        sukses  = j.getInt("success");
                        String pesan    = j.getString("pesan");

                        if(sukses==1) {

                            LoginSessionDemos loginSessionDemos = new LoginSessionDemos(GantiPassword.this);
                            loginSessionDemos.logoutUserDemos();

                            startActivity(new Intent(GantiPassword.this, LoginActivity.class));


                        } else if(sukses==2) {
                            pDialog.dismiss();
                            pError = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                            pError.setCanceledOnTouchOutside(false);
                            pError.setCancelable(false);
                            pError.setTitleText(pesan);
                            pError.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    if(error instanceof NetworkError) {
                        pError2 = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                        pError2.setCanceledOnTouchOutside(false);
                        pError2.setCancelable(false);
                        pError2.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError2.show();
                    } else if(error instanceof ServerError){
                        pError2 = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                        pError2.setCanceledOnTouchOutside(false);
                        pError2.setCancelable(false);
                        pError2.setTitleText("The server could not be found. Please try again after some time!");
                        pError2.show();
                    } else if(error instanceof AuthFailureError){
                        pError2 = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                        pError2.setCanceledOnTouchOutside(false);
                        pError2.setCancelable(false);
                        pError2.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError2.show();
                    } else if(error instanceof ParseError){
                        pError2 = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                        pError2.setCanceledOnTouchOutside(false);
                        pError2.setCancelable(false);
                        pError2.setTitleText("Parsing error! Please try again after some time!");
                        pError2.show();
                    } else if(error instanceof TimeoutError){
                        pError2 = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                        pError2.setCanceledOnTouchOutside(false);
                        pError2.setCancelable(false);
                        pError2.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError2.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("pwd_lama", pwd_lama.getText().toString().trim());
                params.put("pwd_baru", pwd_baru.getText().toString().trim());
                params.put("pwd_baru_ulang", pwd_baru_ulang.getText().toString().trim());
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "GantiPassword");

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
