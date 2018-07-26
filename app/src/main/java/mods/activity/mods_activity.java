package mods.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;


import mods.model.mods;
import mods.adapter.ModsAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import com.seseorang.demos.utils.EndlessRecyclerOnScrollListener;
import com.seseorang.demos.utils.FileDownloader;
import com.seseorang.sweetalert.SweetAlertDialog;


public class mods_activity extends AppCompatActivity {

    private int sukses;
    private JSONArray result;
    private ArrayList<mods> modsList;
    private ModsAdapter modsAdapter;
    private int hal=1;
    private String UrlFile,namanya;
    private String id_user, pwd;


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.mods);

        setTitle("LIST MODS");
		
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        id_user = getIntent().getStringExtra("id_user");
        pwd     = getIntent().getStringExtra("pwd");




        if(getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        modsList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.lv_mods);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        modsAdapter = new ModsAdapter(this, modsList);
        recyclerView.setAdapter(modsAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                hal++;
                FetcDataMods(hal);
            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Toolbar tl = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.title_toolbar);
        textView.setText(getResources().getText(R.string.toolbar_history_mod));

        tl.setNavigationOnClickListener(view -> onBackPressed());

        FetcDataMods(hal);

        FloatingActionButton fab_add_mod = findViewById(R.id.fab_add_mods);
        fab_add_mod.setOnClickListener(view -> {
            Intent add_mods = new Intent(mods_activity.this, mods_add.class);
            add_mods.putExtra("id_user", id_user);
            add_mods.putExtra("pwd", pwd);
            startActivity(add_mods);
        });


    }


    public void FetcDataMods(final int page_no){

        SweetAlertDialog uDialog;

        uDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        uDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        uDialog.setCanceledOnTouchOutside(false);
        uDialog.setCancelable(false);
        uDialog.setTitleText("Loading...");
        uDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.MODS_URL,
                response -> {
                    uDialog.dismiss();
                    JSONObject j;
                    try {
                        j   = new JSONObject(response);
                        result  = j.getJSONArray(Configs.JSON_ARRAY);
                        sukses = j.getInt("success");
                        String pesan = j.getString("pesan");

                        if(sukses==1) {

                            for (int i = 0; i < result.length(); i++) {
                                try {

                                    JSONObject json = result.getJSONObject(i);

                                    mods Mods = new mods();
                                    Mods.setId_mod(json.getInt("id_mod"));
                                    Mods.setNama_petugas(json.getString("nama_petugas"));
                                    Mods.setTanggal(json.getString("tanggal"));
                                    Mods.setSdhLewat(json.getInt("sdh_lewat"));
                                    Mods.setShift(json.getInt("shift"));
                                    Mods.setInisial(json.getString("inisial"));
                                    Mods.setColor(getRandomMaterialColor());
                                    modsList.add(Mods);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            modsAdapter.notifyDataSetChanged();

                        } else if(sukses==2) {
                            SweetAlertDialog oDialog;
                            oDialog = new SweetAlertDialog(mods_activity.this, SweetAlertDialog.ERROR_TYPE);
                            oDialog.setCancelable(false);
                            oDialog.setCanceledOnTouchOutside(false);
                            oDialog.setTitleText(pesan);
                            oDialog.show();
                        }

                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    uDialog.dismiss();
                    SweetAlertDialog pError;
                    pError = new SweetAlertDialog(mods_activity.this, SweetAlertDialog.ERROR_TYPE);
                    pError.setCancelable(false);
                    pError.setCanceledOnTouchOutside(false);
                    pError.setTitleText(error.getMessage());
                    pError.show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id", "11112");
                params.put("action", "GetListMod");
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("p", String.valueOf(page_no));

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void EditMods(int id_mod){
        Intent editMods = new Intent(mods_activity.this, mods_edit.class);
        editMods.putExtra("id_mod", String.valueOf(id_mod));
        editMods.putExtra("id_user", id_user);
        editMods.putExtra("pwd", pwd);
        startActivity(editMods);
    }

    public void TambahSeputar(int id_mod){
        Intent intent   = new Intent(mods_activity.this, mods_add_seputar.class);
        intent.putExtra("id_mod", String.valueOf(id_mod));
        intent.putExtra("dari", "add");
        intent.putExtra("id_seputar", String.valueOf(0));
        intent.putExtra("id_lokasi", String.valueOf(0));
        intent.putExtra("id_user", id_user);
        intent.putExtra("pwd", pwd);
        startActivity(intent);
    }

    private int getRandomMaterialColor() {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + "400", "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }


    public void TanyaHapus(final int id_mod){
        SweetAlertDialog kDialog = new SweetAlertDialog(mods_activity.this, SweetAlertDialog.WARNING_TYPE);

        kDialog.setTitleText("Are you Sure?")
                .setContentText("Won't be able to recover this Data!")
                .setCancelText("No,cancel pls!")
                .setConfirmText("Yes,delete it!")
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener(sweetAlertDialog -> HapusData(id_mod))
                .show();
    }


    private void HapusData(final int id_mod){
        SweetAlertDialog mDialog;
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setTitleText("Processing...");
        mDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.PDF_URL,
                response -> {
                    mDialog.dismiss();
                    JSONObject j;
                    try {

                        j = new JSONObject(response);

                        String pesan    = j.getString("pesan");
                        FetcDataMods(1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    mDialog.dismiss();
                    if(error instanceof NetworkError) {
                        Toast.makeText(mods_activity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof ServerError){
                        Toast.makeText(mods_activity.this, "The server could not be found. Please try again after some time!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof AuthFailureError){
                        Toast.makeText(mods_activity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof ParseError){
                        Toast.makeText(mods_activity.this,"Parsing error! Please try again after some time!", Toast.LENGTH_SHORT ).show();
                    } else if(error instanceof TimeoutError){
                        Toast.makeText(mods_activity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_mod", String.valueOf(id_mod));
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "HapusMod");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
	
	
    public void ExecuteLinkFileDemos(final int idpdfnya){

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

        SweetAlertDialog kDialog;

        kDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        kDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        kDialog.setCanceledOnTouchOutside(false);
        kDialog.setCancelable(false);
        kDialog.setTitleText("Creating File...");
        kDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.PDF_URL,
                response -> {

                    kDialog.dismiss();
                    JSONObject j;
                    try {
                        j = new JSONObject(response);
                        UrlFile = j.getString("link_file");
                        namanya = j.getString("namanya");

                        new DownloadFiles().execute(UrlFile, namanya);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    kDialog.dismiss();
                    if(error instanceof NetworkError) {
                        Toast.makeText(mods_activity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof ServerError){
                        Toast.makeText(mods_activity.this, "The server could not be found. Please try again after some time!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof AuthFailureError){
                        Toast.makeText(mods_activity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    } else if(error instanceof ParseError){
                        Toast.makeText(mods_activity.this,"Parsing error! Please try again after some time!", Toast.LENGTH_SHORT ).show();
                    } else if(error instanceof TimeoutError){
                        Toast.makeText(mods_activity.this, "Cannot connect to Internet...Please check your connection!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("ids", String.valueOf(idpdfnya));
                params.put("id_user", id_user);
                params.put("pwd", pwd);

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadFiles extends AsyncTask<String, Void, Void> {

        SweetAlertDialog sDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            sDialog = new SweetAlertDialog(mods_activity.this, SweetAlertDialog.PROGRESS_TYPE);
            sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sDialog.setCanceledOnTouchOutside(false);
            sDialog.setCancelable(false);
            sDialog.setTitleText("Downloading...");
            sDialog.show();
        }

        @Override
        protected Void doInBackground(String... args) {
            String fileUrl = args[0];
            String fileName = args[1];

            File folder = new File(Environment.getExternalStorageDirectory()+ "/DeMOS");

            if(!folder.exists()){
                if(!folder.mkdir()){
                    Log.d("DeMOS", "Gagal Buat Folder");
                }
            }

            File pdfFile = new File(folder+"/"+fileName);

            try {
                if(!pdfFile.createNewFile()){
                    Log.d("DeMOS", "Gagal Buat File");
                }

                FileDownloader.downloadFile(fileUrl, pdfFile);

            } catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            super.onPostExecute(v);

            sDialog.dismiss();
            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/DeMOS/" + namanya);
            Uri path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            final String appPackageName = "com.google.android.apps.pdfviewer";

            try {
                startActivity(pdfIntent);
            } catch(ActivityNotFoundException e) {
                try {
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                    appStoreIntent.setPackage("com.android.vending");
                    startActivity(appStoreIntent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }


        }


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


    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DeMOS", "Permission Granted");
            }
        }
    }


}
