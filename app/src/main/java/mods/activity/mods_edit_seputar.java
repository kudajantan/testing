package mods.activity;

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
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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

import harian.activity.harian_add_item;
import mods.model.AreaEditModel;
import mods.model.LokasiEditModel;
import mods.model.PicEditModel;
import mods.model.StatusEditModel;


import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class mods_edit_seputar extends AppCompatActivity {

    private ImageView photo_mod;
    private int GALLERY = 1, CAMERA = 2;
    private String ba1="", ket="",resiko="";
    private Bitmap gambar;
    private static final String IMAGE_DIRECTORY = "/DeMOS";
    private File uriCamera;
    private Editor pKet, pResiko;

    private ArrayList<HashMap<String,String>> arrSeputar;

    private JSONArray lokasi, arrstatus, area, pic;
    private int sukses;

    private ArrayList<String>  lokasiCaptionList, statusCaptionList, areaCaptionList, picCaptionList;
    private ArrayAdapter<String> lokasiAdapter, statusAdapter, areaAdapter, picAdapter;


    private ArrayList<LokasiEditModel> lokasiList;
    private ArrayList<AreaEditModel> areaList;
    private ArrayList<StatusEditModel> statusList;
    private ArrayList<PicEditModel> picList;



    private Spinner spinnerLokasi, spinnerStatus, spinnerPic, spinnerArea;
    private int idLokasi=0, idStatus=0, idArea=0, idPIC=0, id_mod_d=0, id_mod_m=0, idSeputar;
	
	private TextView caption_resiko, txt_nama_defect;
	private EditText editText_nama_defect;

	private LinearLayout llNamaDefect;
	private NestedScrollView SVResiko;
    private long mLastClickTime = 0;
    private String id_user, pwd;
    private String dari;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.mods_edit_seputar);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Toolbar tl = findViewById(R.id.toolbar);
        tl.setNavigationOnClickListener(view -> onKembali());

        photo_mod   = findViewById(R.id.photo_mod);
        pKet    = findViewById(R.id.keterangan);
		pResiko	= findViewById(R.id.resiko);
		caption_resiko	= findViewById(R.id.caption_resiko);
        editText_nama_defect = findViewById(R.id.editText_nama_defect);

		llNamaDefect    = findViewById(R.id.LLNamaDefect);
		SVResiko    = findViewById(R.id.SVResiko);

        arrSeputar  = new ArrayList<>();

        Button btnBrowse   = findViewById(R.id.browse_photo);
		
		@SuppressWarnings("unchecked")
        HashMap<String,String> hashMap = (HashMap<String, String>) getIntent().getSerializableExtra("arrayList");
		
		HashMap<String,String> hma = new HashMap<>();
		hma.put("id_area", hashMap.get("id_area"));
		hma.put("id_lokasi", hashMap.get("id_lokasi"));
		hma.put("id_status", hashMap.get("id_status"));
		hma.put("id_pic", hashMap.get("id_pic"));
		arrSeputar.add(hma);

		dari    = hashMap.get("dari");

		
		Picasso.with(mods_edit_seputar.this).load(hashMap.get("gambar")).into(photo_mod);

		ket = hashMap.get("ket");
		resiko  = hashMap.get("resiko");

        editText_nama_defect.setText(hashMap.get("nama_defect"));
        id_user = hashMap.get("id_user");
        pwd     = hashMap.get("pwd");
		
		if(Integer.parseInt(hashMap.get("id_seputar"))==4){
			SVResiko.setVisibility(View.VISIBLE);
			caption_resiko.setVisibility(View.VISIBLE);
			llNamaDefect.setVisibility(View.VISIBLE);
		} else {
			SVResiko.setVisibility(View.GONE);
			caption_resiko.setVisibility(View.GONE);
            llNamaDefect.setVisibility(View.GONE);
		}
		
        id_mod_d    = Integer.parseInt(hashMap.get("id_mod_d"));
        id_mod_m    = Integer.parseInt(hashMap.get("id_mod_m"));
        idSeputar   = Integer.parseInt(hashMap.get("id_seputar"));


        lokasiList  = new ArrayList<>();
        lokasiCaptionList   = new ArrayList<>();

        areaList    = new ArrayList<>();
        areaCaptionList = new ArrayList<>();

        statusList  = new ArrayList<>();
        statusCaptionList   = new ArrayList<>();

        picList = new ArrayList<>();
        picCaptionList  = new ArrayList<>();


        spinnerLokasi   = findViewById(R.id.spin_lokasi);
        spinnerArea = findViewById(R.id.spin_area);
        spinnerStatus   = findViewById(R.id.spin_status);
        spinnerPic  = findViewById(R.id.spin_pic);

        TextView textView = findViewById(R.id.title_toolbar);
        if(dari.equals("harian")) {
            textView.setText(getResources().getText(R.string.toolbar_edit_item));
        }

        btnBrowse.setOnClickListener(view -> {


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                if (!checkIfAlreadyhavePermission()) {
                    requestForSpecificPermission();
                }

                //showPictureDialog();
            }

            showPictureDialog();

        });




        spinnerLokasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idLokasi    = lokasiList.get(i).getId_lokasi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idArea  = areaList.get(i).getId_area();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idStatus    = statusList.get(i).getId_status();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerPic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idPIC   = picList.get(i).getId_pic();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setpKet();
        setpResiko();

        Button btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(view -> {

            if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();

            SimpanSeputar();
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FetcDataSeputarDanLokasi();
            }
        },1000);

    }

    private void SimpanSeputar(){

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
                            gambar=null;
                            ba1="";
                            onKembali();
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(mods_edit_seputar.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(mods_edit_seputar.this, error.getMessage(), Toast.LENGTH_LONG).show();
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


                params.put("id_mod_d", String.valueOf(id_mod_d));
                params.put("id_area", String.valueOf(idArea));
                params.put("id_lokasi", String.valueOf(idLokasi));
                params.put("id_status", String.valueOf(idStatus));
                params.put("id_pic", String.valueOf(idPIC));
                params.put("ket", pKet.getContentAsHTML());
                params.put("resiko", pResiko.getContentAsHTML());
                params.put("gambar", ba1);
                params.put("nama_defect", editText_nama_defect.getText().toString());
                params.put("mstat", "2");
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanSeputar");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void setpKet(){
        /*
        Map<Integer, String> contentTypeface = getContentface();
        Map<Integer, String> headingTypeface = getHeadingface();
        pKet.setContentTypeface(contentTypeface);
        pKet.setHeadingTypeface(headingTypeface);
        pKet.setNormalTextSize(getResources().getDimensionPixelSize(R.dimen.text_editor));
        */

        if(ket.equals("")){
            pKet.render();
        } else {
            pKet.render(ket);
        }

    }

    private void setpResiko(){
        /*
        Map<Integer, String> contentTypeface = getContentface();
        Map<Integer, String> headingTypeface = getHeadingface();
        pResiko.setContentTypeface(contentTypeface);
        pResiko.setHeadingTypeface(headingTypeface);
        pResiko.setNormalTextSize(getResources().getDimensionPixelSize(R.dimen.text_editor));
        */

        if(resiko.equals("")){
            pResiko.render();
        } else {
            pResiko.render(resiko);
        }
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

                        /*Lokasi*/
                        lokasi  = j.getJSONArray(Configs.JSON_ARRAY_LOKASI);


                        for (int l = 0; l < lokasi.length(); l++) {
                            try {

                                JSONObject loc = lokasi.getJSONObject(l);

                                LokasiEditModel lokasiModel = new LokasiEditModel();
                                lokasiModel.setId_lokasi(loc.getInt("id_lokasi"));
                                lokasiModel.setNama_lokasi(loc.getString("nama_lokasi"));
                                lokasiCaptionList.add(loc.getString("nama_lokasi"));

                                lokasiList.add(lokasiModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        lokasiAdapter  = new ArrayAdapter<>(mods_edit_seputar.this, R.layout.spinner_item, R.id.text1,lokasiCaptionList);
                        spinnerLokasi.setAdapter(lokasiAdapter);
                        for(int pos=lokasiAdapter.getCount(); pos>=0; pos--){
                            //Toast.makeText(mods_edit_seputar.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                            if(lokasiAdapter.getItemId(pos) ==  Integer.parseInt(arrSeputar.get(0).get("id_lokasi"))){
                                spinnerLokasi.setSelection(pos);
                                //Toast.makeText(mods_edit_seputar.this, String.valueOf(pos), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }


                        /*Area*/
                        area  = j.getJSONArray(Configs.JSON_ARRAY_AREA);


                        for (int a = 0; a < area.length(); a++) {
                            try {

                                JSONObject are = area.getJSONObject(a);

                                AreaEditModel areaModel = new AreaEditModel();
                                areaModel.setId_area(are.getInt("id_area"));
                                areaModel.setNama_area(are.getString("nama_area"));
                                areaCaptionList.add(are.getString("nama_area"));

                                areaList.add(areaModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        areaAdapter  = new ArrayAdapter<>(mods_edit_seputar.this, R.layout.spinner_item, R.id.text1,areaCaptionList);
                        spinnerArea.setAdapter(areaAdapter);
                        for(int pos=areaAdapter.getCount(); pos>=0; pos--){
                            if(areaAdapter.getItemId(pos) ==  Integer.parseInt(arrSeputar.get(0).get("id_area"))){
                                spinnerArea.setSelection(pos);
                                break;
                            }
                        }

                        /*Status*/
                        arrstatus  = j.getJSONArray(Configs.JSON_ARRAY_STATUS);


                        for (int s = 0; s < arrstatus.length(); s++) {
                            try {

                                JSONObject stat = arrstatus.getJSONObject(s);

                                StatusEditModel statusModel = new StatusEditModel();
                                statusModel.setId_status(stat.getInt("id_status"));
                                statusModel.setNama_status(stat.getString("nama_status"));
                                statusCaptionList.add(stat.getString("nama_status"));

                                statusList.add(statusModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        statusAdapter  = new ArrayAdapter<>(mods_edit_seputar.this, R.layout.spinner_item,R.id.text1, statusCaptionList);
                        spinnerStatus.setAdapter(statusAdapter);
                        for(int pos=statusAdapter.getCount(); pos>=0; pos--){
                            if(statusAdapter.getItemId(pos) ==  Integer.parseInt(arrSeputar.get(0).get("id_status"))){
                                spinnerStatus.setSelection(pos);
                                break;
                            }
                        }

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

                        picAdapter  = new ArrayAdapter<>(mods_edit_seputar.this, R.layout.spinner_item, R.id.text1,picCaptionList);
                        spinnerPic.setAdapter(picAdapter);
                        for(int pos=picAdapter.getCount(); pos>=0; pos--){
                            if(picAdapter.getItemId(pos) ==  Integer.parseInt(arrSeputar.get(0).get("id_pic"))){
                                spinnerPic.setSelection(pos);
                                break;
                            }
                        }



                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(mods_edit_seputar.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
                "Photo Dari Gallery",
                "Photo Dari Camera" };
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

        final SweetAlertDialog pDialog = new SweetAlertDialog(mods_edit_seputar.this, SweetAlertDialog.ERROR_TYPE);
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
        setResult(RESULT_CANCELED);
        //super.onBackPressed();
    }

    private void onKembali(){
        if(dari.equals("harian")){
            Intent add_item = new Intent(mods_edit_seputar.this, harian_add_item.class);
            add_item.putExtra("id_mod", String.valueOf(id_mod_m));
            add_item.putExtra("dari", "edit");
            add_item.putExtra("id_seputar", String.valueOf(idSeputar));
            add_item.putExtra("id_lokasi", String.valueOf(idLokasi));
            add_item.putExtra("id_user", id_user);
            add_item.putExtra("pwd", pwd);
            startActivity(add_item);
            finish();
        } else {
            Intent add_seputar = new Intent(mods_edit_seputar.this, mods_add_seputar.class);
            add_seputar.putExtra("id_mod", String.valueOf(id_mod_m));
            add_seputar.putExtra("dari", "edit");
            add_seputar.putExtra("id_seputar", String.valueOf(idSeputar));
            add_seputar.putExtra("id_lokasi", String.valueOf(idLokasi));
            add_seputar.putExtra("id_user", id_user);
            add_seputar.putExtra("pwd", pwd);
            startActivity(add_seputar);
            finish();
        }
    }


}