package login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import com.seseorang.demos.R;
import com.seseorang.demos.MainActivity;
import com.seseorang.demos.utils.Configs;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername, txtPwd;
    private LoginSessionDemos loginSession;
    private JSONArray hslpos;
    private int sukses;
    private String pesan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        loginSession = new LoginSessionDemos(getApplicationContext());


        if(loginSession.isLoggedInDemos()){
            Intent a = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(a);
            finish();
        }

        Button btnLogin = findViewById(R.id.btnLogin);
        txtUsername =  findViewById(R.id.txtUserName);
        txtPwd  = findViewById(R.id.txtPassword);



        btnLogin.setOnClickListener(v -> LoginClick());

    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void LoginClick(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.MODS_URL,
                response -> {

                    try {
                       JSONObject j   = new JSONObject(response);

                        hslpos  = j.getJSONArray(Configs.JSON_ARRAY);
                        sukses  = j.getInt("success");
                        pesan   = j.getString("pesan");

                        if(sukses==1){
                            JSONObject json = hslpos.getJSONObject(0);

                            loginSession.createLoginSessionDemos(json.getString("nama_lengkap"), json.getInt("id_user"),
                                                        json.getString("pwd"), json.getString("email"),
                                                        json.getInt("id_aphris"), json.getInt("level"), json.getInt("jenis_user"),
                                                        json.getString("dept"));
                            Intent a = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(a);
                            finish();
                        } else if(sukses==2){
                            Toast.makeText(LoginActivity.this, pesan , Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    if(error instanceof NetworkError) {
                        Toast.makeText(LoginActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof ServerError){
                        Toast.makeText(LoginActivity.this, "The server could not be found. Please try again after some time!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof AuthFailureError){
                        Toast.makeText(LoginActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof ParseError){
                        Toast.makeText(LoginActivity.this,"Parsing error! Please try again after some time!", Toast.LENGTH_SHORT ).show();
                    } else if(error instanceof TimeoutError){
                        Toast.makeText(LoginActivity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams()  {

                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("uname", txtUsername.getText().toString().trim());
                params.put("pwd", txtPwd.getText().toString().trim());
                params.put("action", "LoginDemos");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}