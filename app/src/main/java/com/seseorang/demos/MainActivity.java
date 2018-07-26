package com.seseorang.demos;


import android.content.Context;
import android.content.Intent;

import android.graphics.Color;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.seseorang.demos.utils.Configs;
import com.seseorang.sweetalert.SweetAlertDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import defect.DefectActivity;
import defect.DefectReport;
import harian.activity.harian_main;
import login.GantiPassword;
import login.LoginSessionDemos;
import menus.adapter.MenusAdapter;
import menus.model.MenusModel;
import mods.activity.mods_activity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements MenusAdapter.ItemClickListener {

    private DrawerLayout dr;
    private Toolbar tl;
    private String id_user, pwd;
    private LoginSessionDemos loginSessionDemos;
    int LevelUser=0;
    private MenusAdapter menusAdapter;
    private ArrayList<MenusModel> menusModelArrayList;
    private JSONArray result;
    private int sukses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_utama);

        tl = findViewById(R.id.toolbar);

        ShowDrawer();
        loginSessionDemos = new LoginSessionDemos(MainActivity.this);

        HashMap<String,String> hashMap  = loginSessionDemos.getUserDetailDemos();

        id_user     = hashMap.get(LoginSessionDemos.ID_USER_DEMOS);
        pwd         = hashMap.get(LoginSessionDemos.PWD_DEMOS);
        LevelUser   = Integer.parseInt(hashMap.get(LoginSessionDemos.LEVEL_DEMOS));
        String dept = hashMap.get(LoginSessionDemos.NAMA_DEPT_DEMOS);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        RecyclerView recyclerView   = findViewById(R.id.nav_list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        menusModelArrayList = new ArrayList<>();
        menusAdapter    = new MenusAdapter(menusModelArrayList, MainActivity.this);
        menusAdapter.setClickListener(this);
        recyclerView.setAdapter(menusAdapter);

        TextView nama_lengkap   = findViewById(R.id.nama_lengkap);
        nama_lengkap.setText(hashMap.get(LoginSessionDemos.NAMA_LENGKAP_DEMOS));

        TextView email = findViewById(R.id.email);
        email.setText(hashMap.get(LoginSessionDemos.EMAIL_DEMOS));

        if(LevelUser==2){
            FirebaseMessaging.getInstance().subscribeToTopic(dept);
            Log.d("DeMOS", "Subscribe to Topic "+dept);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(dept);
            Log.d("DeMOS", "Unsubscribe from Topic "+dept);
        }

        FetchMenu();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onClick(View view, int position){
        final MenusModel menusModel = menusModelArrayList.get(position);
        int id_menu = menusModel.getId_menu();
        switch (id_menu){
            case 1:
                dr.closeDrawers();
                break;
            case 2:
                Intent defek = new Intent(MainActivity.this, DefectActivity.class);
                defek.putExtra("id_user", id_user);
                defek.putExtra("pwd", pwd);
                startActivity(defek);
                dr.closeDrawers();
                break;
            case 3:
                Intent report_defect   =new Intent(MainActivity.this, DefectReport.class);
                report_defect.putExtra("id_user", id_user);
                report_defect.putExtra("pwd", pwd);
                startActivity(report_defect);
                dr.closeDrawers();
                break;
            case 4:
                Intent man_on_duty = new Intent(MainActivity.this, mods_activity.class);
                man_on_duty.putExtra("id_user", id_user);
                man_on_duty.putExtra("pwd", pwd);
                startActivity(man_on_duty);
                dr.closeDrawers();
                break;
            case 5:
                Intent harian = new Intent(MainActivity.this, harian_main.class);
                harian.putExtra("id_user", id_user);
                harian.putExtra("pwd", pwd);
                startActivity(harian);
                dr.closeDrawers();
                break;
            case 6:
                Intent ganti    = new Intent(MainActivity.this, GantiPassword.class);
                ganti.putExtra("id_user", id_user);
                ganti.putExtra("pwd", pwd);
                startActivity(ganti);
                dr.closeDrawers();
                break;
            case 7:
                loginSessionDemos.logoutUserDemos();
                finish();
                break;
        }

    }

    private void FetchMenu(){

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

                                    final MenusModel mainModel = new MenusModel();
                                    mainModel.setId_menu(json.getInt("id_menu"));
                                    mainModel.setIcon_menu(json.getString("icon_menu"));
                                    mainModel.setNama_menu(json.getString("nama_menu"));
                                    menusModelArrayList.add(mainModel);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            menusAdapter.notifyDataSetChanged();

                        } else if(sukses==2) {
                            Toast.makeText(MainActivity.this, pesan, Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();

                    final SweetAlertDialog pError;
                    pError  = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
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

                params.put("action", "GetListMenu");
                params.put("id_user", id_user);
                params.put("pwd", pwd);

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void ShowDrawer() {
        dr = findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, dr, tl, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
                v.bringToFront();
                v.requestLayout();
            }
        };
        dr.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

}