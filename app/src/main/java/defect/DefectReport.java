package defect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
import com.seseorang.demos.utils.FileDownloader;
import com.seseorang.demos.utils.MonthYearPicker;
import com.seseorang.sweetalert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;


import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DefectReport extends AppCompatActivity {

    private MonthYearPicker myp;
    private EditText bulan_tahun;
    private String id_user, pwd;
    private String UrlFile,namanya;
    private int sukses=0;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.defect_report);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        id_user     = getIntent().getStringExtra("id_user");
        pwd         = getIntent().getStringExtra("pwd");

        Toolbar tl = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.title_toolbar);
        textView.setText(getResources().getText(R.string.report_defect));
        tl.setNavigationOnClickListener(view -> onBackPressed());

        Button btnPreview   = findViewById(R.id.btnSimpan);
        btnPreview.setText(getResources().getText(R.string.btnPreview));

        bulan_tahun = findViewById(R.id.bulan_tahun);

        myp = new MonthYearPicker(this);
       myp.build(new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               String hasil = myp.getSelectedMonthName()+" "+myp.getSelectedYear();
               bulan_tahun.setText(hasil);
           }
       },null);

       bulan_tahun.setOnClickListener(view -> myp.show());

       btnPreview.setOnClickListener(view -> {
           if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
               return;
           }

           mLastClickTime = SystemClock.elapsedRealtime();
           ExecuteLinkFileReportDefect(bulan_tahun.getText().toString().trim());
       });

    }


    public void ExecuteLinkFileReportDefect(final String bulan_tahun){

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
        SweetAlertDialog sDialog;

        sDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sDialog.setCanceledOnTouchOutside(false);
        sDialog.setCancelable(false);
        sDialog.setTitleText("Creating File...");
        sDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.DEFECT_REPORT_URL,
                response -> {

                    sDialog.dismiss();
                    JSONObject j;
                    try {
                        j = new JSONObject(response);

                        sukses  = j.getInt("success");
                        String pesan    = j.getString("pesan");

                        if(sukses==1) {

                            UrlFile = j.getString("link_file");
                            namanya = j.getString("namanya");

                            new DefectReport.DownloadFiles().execute(UrlFile, namanya);
                        } else if(sukses==2) {
                            sDialog.dismiss();
                            SweetAlertDialog pError = new SweetAlertDialog(DefectReport.this, SweetAlertDialog.ERROR_TYPE);
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
                    sDialog.dismiss();
                    if(error instanceof NetworkError) {
                        SweetAlertDialog pError = new SweetAlertDialog(DefectReport.this, SweetAlertDialog.ERROR_TYPE);
                        pError.setCanceledOnTouchOutside(false);
                        pError.setCancelable(false);
                        pError.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError.show();
                    } else if(error instanceof ServerError){
                        SweetAlertDialog pError = new SweetAlertDialog(DefectReport.this, SweetAlertDialog.ERROR_TYPE);
                        pError.setCanceledOnTouchOutside(false);
                        pError.setCancelable(false);
                        pError.setTitleText("The server could not be found. Please try again after some time!");
                        pError.show();
                    } else if(error instanceof AuthFailureError){
                        SweetAlertDialog pError = new SweetAlertDialog(DefectReport.this, SweetAlertDialog.ERROR_TYPE);
                        pError.setCanceledOnTouchOutside(false);
                        pError.setCancelable(false);
                        pError.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError.show();
                    } else if(error instanceof ParseError){
                        SweetAlertDialog pError = new SweetAlertDialog(DefectReport.this, SweetAlertDialog.ERROR_TYPE);
                        pError.setCanceledOnTouchOutside(false);
                        pError.setCancelable(false);
                        pError.setTitleText("Parsing error! Please try again after some time!");
                        pError.show();
                    } else if(error instanceof TimeoutError){
                        SweetAlertDialog pError = new SweetAlertDialog(DefectReport.this, SweetAlertDialog.ERROR_TYPE);
                        pError.setCanceledOnTouchOutside(false);
                        pError.setCancelable(false);
                        pError.setTitleText("Cannot connect to Internet...Please check your connection!");
                        pError.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("bulan_tahun", bulan_tahun);
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

        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new SweetAlertDialog(DefectReport.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);
            pDialog.setTitleText("Downloading...");
            pDialog.show();
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

            pDialog.dismiss();
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
