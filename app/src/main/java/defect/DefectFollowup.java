package defect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
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
import com.github.irshulx.Editor;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;
import com.seseorang.sweetalert.SweetAlertDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import defect.model.DefectStatus;
import login.LoginSessionDemos;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DefectFollowup extends AppCompatActivity {


    private ArrayList<String> defectStatusList;
    private ArrayList<DefectStatus> defectStatusArrayList;
    private DefectStatus defectStatus;
    private Spinner spin_status;
    private Editor sEditor;
    private int idStatusDefect, sukses;
    private int GALLERY = 1, CAMERA = 2, id_defect=0;
    private String ba1="", dari="", id_user, pwd;
    private Bitmap gambar;
    private static final String IMAGE_DIRECTORY = "/DeMOS";
    private File uriCamera;
    private ImageView photo_mod;
    private long mLastClickTime = 0;
    private LoginSessionDemos loginSessionDemos;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.defect_follow_up);


        loginSessionDemos = new LoginSessionDemos(DefectFollowup.this);
        HashMap<String,String> hma  = loginSessionDemos.getUserDetailDemos();
        id_user = hma.get(LoginSessionDemos.ID_USER_DEMOS);
        pwd     = hma.get(LoginSessionDemos.PWD_DEMOS);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        spin_status = findViewById(R.id.spinner_status);
        Button btnBrowse   = findViewById(R.id.browse_photo);
        Button btnSimpan   = findViewById(R.id.btnSimpan);
        btnSimpan.setText(getResources().getText(R.string.btnSubmit));
        sEditor     = findViewById(R.id.keterangan);
        photo_mod   = findViewById(R.id.photo_mod);

        id_defect   = Integer.parseInt(getIntent().getStringExtra("id_defect"));
        dari        = getIntent().getStringExtra("dari");

        setupKet();

        Toolbar tl = findViewById(R.id.toolbar);
        TextView textView   = findViewById(R.id.title_toolbar);
        textView.setText(getResources().getText(R.string.tambah_status_defect));

        tl.setNavigationOnClickListener(view -> {
            if(gambar!=null){
                gambar=null;
            }
            onBackPressed();
        });


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        defectStatusList      = new ArrayList<>();
        defectStatusArrayList = new ArrayList<>();
        setupInitStatus();

        btnBrowse.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                if (!checkIfAlreadyhavePermission()) {
                    requestForSpecificPermission();
                }

                //showPictureDialog();
            }

            showPictureDialog();
        });

        spin_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idStatusDefect  = defectStatusArrayList.get(i).getId_status();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnSimpan.setOnClickListener(view -> {
            if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();
            SimpanData();
        });

    }

    private void setupKet(){
        /*
        Map<Integer, String> contentTypeface = getContentface();
        Map<Integer, String> headingTypeface = getHeadingface();
        sEditor.setHeadingTypeface(headingTypeface);
        sEditor.setContentTypeface(contentTypeface);
        sEditor.setNormalTextSize(getResources().getDimensionPixelSize(R.dimen.text_editor));
        */
        sEditor.render();
    }

    private Map<Integer, String> getHeadingface() {

        @SuppressLint("UseSparseArrays")
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/calibri.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/calibrib.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/calibrii.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/calibriz.ttf");
        return typefaceMap;
    }

    private Map<Integer, String> getContentface() {

        @SuppressLint("UseSparseArrays")
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/calibri.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/calibrib.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/calibrii.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/calibriz.ttf");
        return typefaceMap;
    }


    private void setupInitStatus(){
        defectStatus    = new DefectStatus();

        defectStatus.setId_status(0);
        defectStatus.setNama_status("Pilih Status");
        defectStatusList.add("Pilih Status");
        defectStatusArrayList.add(defectStatus);

        defectStatus    = new DefectStatus();
        defectStatus.setId_status(1);
        defectStatus.setNama_status("Dalam Proses");
        defectStatusList.add("Dalam Proses");
        defectStatusArrayList.add(defectStatus);

        defectStatus    = new DefectStatus();
        defectStatus.setId_status(2);
        defectStatus.setNama_status("Selesai");
        defectStatusList.add("Selesai");
        defectStatusArrayList.add(defectStatus);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DefectFollowup.this, android.R.layout.simple_spinner_dropdown_item,defectStatusList);
        spin_status.setAdapter(arrayAdapter);

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
                        if(gambar!=null){
                            gambar=null;
                        }

                        if(sukses==1){
                            if(dari.equals("notif")) {
                                Intent notif = new Intent(DefectFollowup.this, DefectNotif.class);
                                notif.putExtra("id_data", String.valueOf(id_defect));
                                notif.putExtra("id_user", id_user);
                                notif.putExtra("pwd", pwd);
                                startActivity(notif);
                                finish();
                            } else {
                                Intent defek_activity = new Intent(DefectFollowup.this, DefectActivity.class);
                                defek_activity.putExtra("id_user", id_user);
                                defek_activity.putExtra("pwd", pwd);
                                startActivity(defek_activity);
                                finish();
                            }
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(DefectFollowup.this, SweetAlertDialog.ERROR_TYPE);
                            sPesan.setTitleText(pesan);
                            sPesan.setCancelable(false);
                            sPesan.setCanceledOnTouchOutside(false);
                            sPesan.show();

                            if(gambar!=null){
                                gambar=null;
                            }
                        }

                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(DefectFollowup.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                if(gambar!=null) {
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    gambar.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                    final byte[] ba = bao.toByteArray();
                    ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                }


                params.put("id_defect", String.valueOf(id_defect));
                params.put("id_status_defect", String.valueOf(idStatusDefect));
                params.put("ket", sEditor.getContentAsHTML());
                params.put("gambar", ba1);
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanFollowUpDefect");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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



    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Pilih Sumber Photo");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallary();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            if(!wallpaperDirectory.mkdirs()){
                Log.d("DeMOS", "Error Created File");
            }
        }

        uriCamera  = new File(wallpaperDirectory, Calendar.getInstance()
                .getTimeInMillis() + ".jpg");


        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(uriCamera));
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY && resultCode == RESULT_OK ) {
            if (data != null) {
                gambar  = decodeBitmap(data.getData());
                photo_mod.setImageBitmap(gambar);
            }

        } else if (requestCode == CAMERA && resultCode == RESULT_OK) {
            gambar  = decodeBitmap(Uri.fromFile(uriCamera));
            photo_mod.setImageBitmap(gambar);
        }
    }


    private Bitmap decodeBitmap(Uri theUri) {
        Bitmap outputBitmap = null;
        AssetFileDescriptor fileDescriptor;

        final SweetAlertDialog pDialog = new SweetAlertDialog(DefectFollowup.this, SweetAlertDialog.ERROR_TYPE);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        try {
            fileDescriptor = getContentResolver().openAssetFileDescriptor(theUri, "r");

            // Get size of bitmap file
            BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
            boundsOptions.inJustDecodeBounds = true;
            assert fileDescriptor != null;
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, boundsOptions);

            // Get desired sample size. Note that these must be powers-of-two.
            int[] sampleSizes = new int[]{8, 4, 2, 1};
            int selectedSampleSize = 1; // 1 by default (original image)


            if(boundsOptions.outWidth<boundsOptions.outHeight ){
                pDialog.setTitleText("Hanya Boleh Landscape");
                pDialog.show();
            } else {

                for (int sampleSize : sampleSizes) {
                    selectedSampleSize = sampleSize;
                    int targetWidth = boundsOptions.outWidth / sampleSize;
                    int targetHeight = boundsOptions.outHeight / sampleSize;
                    if (targetWidth >= 310 && targetHeight >= 198) {
                        break;
                    }
                }

                // Decode bitmap at desired size
                BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                decodeOptions.inSampleSize = selectedSampleSize;
                outputBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, decodeOptions);
                if (outputBitmap != null) {
                    Log.i("DeMOS", "Loaded image with sample size " + decodeOptions.inSampleSize + "\t\t"
                            + "Bitmap width: " + outputBitmap.getWidth()
                            + "\theight: " + outputBitmap.getHeight());
                }
                fileDescriptor.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputBitmap;
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
        if(dari.equals("notif")) {
            Intent notif = new Intent(DefectFollowup.this, DefectNotif.class);
            notif.putExtra("id_data", String.valueOf(id_defect));
            notif.putExtra("id_user", id_user);
            notif.putExtra("pwd", pwd);
            startActivity(notif);
            finish();
        } else {
            Intent defek_activity = new Intent(DefectFollowup.this, DefectActivity.class);
            defek_activity.putExtra("id_user", id_user);
            defek_activity.putExtra("pwd", pwd);
            startActivity(defek_activity);
            finish();
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}