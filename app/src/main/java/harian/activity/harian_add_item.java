package harian.activity;

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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import mods.activity.mods_edit_seputar;
import mods.adapter.SeputarAdapter;
import mods.model.AreaModel;
import mods.model.LokasiModel;
import mods.model.PicModel;
import mods.model.SeputarLoadModel;

import android.R.drawable;


import mods.model.StatusModel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by Rius on 6/14/2018.
 */

public class harian_add_item extends AppCompatActivity {

    private ImageView photo_mod;
    private int GALLERY = 1, CAMERA = 2;
    private String ba1="";
    private Bitmap gambar;
    private static final String IMAGE_DIRECTORY = "/DeMOS";
    private File uriCamera;
    private Editor pResiko, pKet;
    private String id_user, pwd;



    private JSONArray lokasi, arrstatus, area, pic, result;
    private int sukses;

    private ArrayList<String> lokasiCaptionList, statusCaptionList, areaCaptionList, picCaptionList;
    private ArrayAdapter<String> lokasiAdapter, statusAdapter, areaAdapter, picAdapter;

    private ArrayList<LokasiModel> lokasiList;
    private ArrayList<AreaModel> areaList;
    private ArrayList<StatusModel> statusList;
    private ArrayList<PicModel> picList;

    private ArrayList<SeputarLoadModel> seputarLoadModels;
    private SeputarAdapter seputarLoadAdapter;

    private Spinner spinnerLokasi, spinnerStatus, spinnerPic, spinnerArea;
    private int idSeputar=4, idLokasi=0, idStatus=0, idArea=0, idPIC=0, id_mod_m=0;


    private TextView list_caption, info_kosong, caption_nama_defect, caption_resiko;
    private RecyclerView recyclerView;
    private long mLastClickTime = 0;
    private String dari;

    private EditText editText_nama_defect;

    private Boolean sdhRender =false;


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.harian_add_item);

        photo_mod   = findViewById(R.id.photo_mod);
        pKet    = findViewById(R.id.keterangan);

        pResiko = findViewById(R.id.resiko);
        caption_resiko  = findViewById(R.id.caption_resiko);

        setpKet();
        setpResiko();

        caption_nama_defect = findViewById(R.id.txtCaptionDefect);
        editText_nama_defect = findViewById(R.id.editText_nama_defect);


        list_caption    = findViewById(R.id.caption_list_seputar);
        info_kosong     = findViewById(R.id.info_kosong);

        Toolbar tl = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.title_toolbar);
        textView.setText(getResources().getText(R.string.toolbar_tambah_item));
        tl.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent harian_main  = new Intent(harian_add_item.this, harian_main.class);
                harian_main.putExtra("id_user", id_user);
                harian_main.putExtra("pwd", pwd);
                startActivity(harian_main);
                finish();
            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MavenPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        Button btnBrowse   = findViewById(R.id.browse_photo);
        Button btnSimpan   = findViewById(R.id.btnSimpan);
        btnSimpan.setText(getResources().getText(R.string.btnSubmit));
        id_mod_m    = Integer.parseInt(getIntent().getStringExtra("id_mod"));
        id_user     = getIntent().getStringExtra("id_user");
        pwd         = getIntent().getStringExtra("pwd");
        idLokasi    = Integer.parseInt(getIntent().getStringExtra("id_lokasi"));
        dari        =getIntent().getStringExtra("dari");


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

        seputarLoadModels   = new ArrayList<>();
        seputarLoadAdapter  = new SeputarAdapter(this, seputarLoadModels, "harian");


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    if (!checkIfAlreadyhavePermission()) {
                        requestForSpecificPermission();
                    }

                    //showPictureDialog();
                }

                showPictureDialog();
            }
        });

        FetcDataSeputarDanLokasi();

        recyclerView = findViewById(R.id.lv_item_harian);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(seputarLoadAdapter);


        spinnerLokasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idLokasi    = lokasiList.get(i).getId_lokasi();

                FetcDataSeputar(id_mod_m, idSeputar, idLokasi);

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



        btnSimpan.setOnClickListener(view -> {
            // preventing multiple click

            if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();
            SimpanItemHarian();

        });

    }


    private void setpKet(){
        /*
        Map<Integer, String> contentTypeface = getContentface();
        Map<Integer, String> headingTypeface = getHeadingface();
        pKet.setHeadingTypeface(headingTypeface);
        pKet.setContentTypeface(contentTypeface);
        pKet.setNormalTextSize(getResources().getDimensionPixelSize(R.dimen.text_editor));
        */
        pKet.render();
    }

    private void setpResiko(){
        /*
        Map<Integer, String> contentTypeface = getContentface();
        Map<Integer, String> headingTypeface = getHeadingface();
        pResiko.setHeadingTypeface(headingTypeface);
        pResiko.setContentTypeface(contentTypeface);
        pResiko.setNormalTextSize(getResources().getDimensionPixelSize(R.dimen.text_editor));
        */
        pResiko.render();
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

    private void SimpanItemHarian(){

        final SweetAlertDialog pDialog, sPesan;

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        sPesan  = new SweetAlertDialog(harian_add_item.this, SweetAlertDialog.ERROR_TYPE);
        sPesan.setCancelable(false);
        sPesan.setCanceledOnTouchOutside(false);

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
                            spinnerArea.setSelection(0);
                            spinnerStatus.setSelection(0);
                            spinnerPic.setSelection(0);
                            pKet.clearAllContents();
                            pResiko.clearAllContents();
                            editText_nama_defect.setText("");
                            ba1="";


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                photo_mod.setImageDrawable(getResources().getDrawable(drawable.ic_menu_gallery, getApplicationContext().getTheme()));
                            } else {
                                photo_mod.setImageDrawable(getResources().getDrawable(drawable.ic_menu_gallery));
                            }


                            FetcDataSeputar(id_mod_m, idSeputar, idLokasi);
                        } else if(sukses==2) {
                            sPesan.setTitleText(pesan);
                            sPesan.show();
                        }

                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(harian_add_item.this, error.getMessage(), Toast.LENGTH_LONG).show();
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


                params.put("id_mod", String.valueOf(id_mod_m));
                params.put("id_seputar", String.valueOf(idSeputar));
                params.put("id_area", String.valueOf(idArea));
                params.put("id_lokasi", String.valueOf(idLokasi));
                params.put("id_status", String.valueOf(idStatus));
                params.put("id_pic", String.valueOf(idPIC));
                params.put("ket", pKet.getContentAsHTML());
                params.put("resiko", pResiko.getContentAsHTML());
                params.put("gambar", ba1);
                params.put("nama_defect", editText_nama_defect.getText().toString());
                params.put("mstat","1");
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanSeputar");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

                                LokasiModel lokasiModel = new LokasiModel();
                                lokasiModel.setId_lokasi(loc.getInt("id_lokasi"));
                                lokasiModel.setNama_lokasi(loc.getString("nama_lokasi"));
                                lokasiCaptionList.add(loc.getString("nama_lokasi"));

                                lokasiList.add(lokasiModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        lokasiAdapter  = new ArrayAdapter<>(harian_add_item.this, R.layout.spinner_item, R.id.text1,lokasiCaptionList);
                        spinnerLokasi.setAdapter(lokasiAdapter);
                        if(dari.equals("edit")){
                            for(int pos=lokasiAdapter.getCount();pos>=0;pos--){
                                if(lokasiAdapter.getItemId(pos)==idLokasi){
                                    spinnerLokasi.setSelection(pos);
                                    break;
                                }
                            }
                        }


                        /*Area*/
                        area  = j.getJSONArray(Configs.JSON_ARRAY_AREA);


                        for (int a = 0; a < area.length(); a++) {
                            try {

                                JSONObject are = area.getJSONObject(a);

                                AreaModel areaModel = new AreaModel();
                                areaModel.setId_area(are.getInt("id_area"));
                                areaModel.setNama_area(are.getString("nama_area"));
                                areaCaptionList.add(are.getString("nama_area"));

                                areaList.add(areaModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        areaAdapter  = new ArrayAdapter<>(harian_add_item.this, R.layout.spinner_item, R.id.text1,areaCaptionList);
                        spinnerArea.setAdapter(areaAdapter);

                        /*Status*/
                        arrstatus  = j.getJSONArray(Configs.JSON_ARRAY_STATUS);


                        for (int s = 0; s < arrstatus.length(); s++) {
                            try {

                                JSONObject stat = arrstatus.getJSONObject(s);

                                StatusModel statusModel = new StatusModel();
                                statusModel.setId_status(stat.getInt("id_status"));
                                statusModel.setNama_status(stat.getString("nama_status"));
                                statusCaptionList.add(stat.getString("nama_status"));

                                statusList.add(statusModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        statusAdapter  = new ArrayAdapter<>(harian_add_item.this, R.layout.spinner_item,R.id.text1, statusCaptionList);
                        spinnerStatus.setAdapter(statusAdapter);

                        /*PIC*/
                        pic  = j.getJSONArray(Configs.JSON_ARRAY_PIC);


                        for (int p = 0; p < pic.length(); p++) {
                            try {

                                JSONObject pi = pic.getJSONObject(p);

                                PicModel picModel = new PicModel();
                                picModel.setId_pic(pi.getInt("id_pic"));
                                picModel.setNama_pic(pi.getString("nama_pic"));
                                picCaptionList.add(pi.getString("nama_pic"));

                                picList.add(picModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        picAdapter  = new ArrayAdapter<>(harian_add_item.this, R.layout.spinner_item, R.id.text1,picCaptionList);
                        spinnerPic.setAdapter(picAdapter);



                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(harian_add_item.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams()  {

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

    public void FetcDataSeputar(final int id_mod, final int id_seputar, final int id_lokasi){

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
                    if(seputarLoadModels.size()>0) {
                        seputarLoadModels.clear();
                    }

                    if(recyclerView.getVisibility()==View.GONE){
                        recyclerView.setVisibility(View.VISIBLE);
                        info_kosong.setVisibility(View.GONE);
                    }

                    JSONObject j;
                    try {
                        j   = new JSONObject(response);
                        result  = j.getJSONArray(Configs.JSON_ARRAY);
                        sukses = j.getInt("success");
                        String pesan = j.getString("pesan");
                        list_caption.setText(j.getString("caption") );

                        if(sukses==1) {

                            for (int i = 0; i < result.length(); i++) {
                                try {

                                    JSONObject json = result.getJSONObject(i);

                                    SeputarLoadModel Mods = new SeputarLoadModel();
                                    Mods.setId_mod(json.getInt("id_mod_d"));
                                    Mods.setId_seputar(json.getInt("id_seputar"));
                                    Mods.setArea(json.getString("nama_area"));
                                    Mods.setId_area(json.getInt("id_area"));
                                    Mods.setLokasi(json.getString("nama_lokasi"));
                                    Mods.setId_lokasi(json.getInt("id_lokasi"));
                                    Mods.setGambar(json.getString("gambar"));
                                    Mods.setKet (json.getString("ket"));
                                    Mods.setResiko(json.getString("resiko"));
                                    Mods.setNama_defect(json.getString("nama_defect"));
                                    Mods.setPic(json.getString("nama_pic"));
                                    Mods.setId_pic(json.getInt("id_pic"));
                                    Mods.setStatus(json.getString("nama_status"));
                                    Mods.setId_status(json.getInt("id_status"));
                                    seputarLoadModels.add(Mods);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            seputarLoadAdapter.notifyDataSetChanged();

                        } else if(sukses==2) {
                            if(recyclerView.getVisibility()==View.VISIBLE){
                                recyclerView.setVisibility(View.GONE);
                                info_kosong.setVisibility(View.VISIBLE);
                            }
                        }

                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    pDialog.dismiss();
                    Toast.makeText(harian_add_item.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_mod", String.valueOf(id_mod));
                params.put("id_seputar", String.valueOf(id_seputar));
                params.put("id_lokasi", String.valueOf(id_lokasi));
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "GetSeputarList");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void editItem(int id_mod_d, int postRec){
        Intent editSep = new Intent(harian_add_item.this, mods_edit_seputar.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("gambar", seputarLoadModels.get(postRec).getGambar());
        params.put("id_status", String.valueOf(seputarLoadModels.get(postRec).getId_status()));
        params.put("id_pic", String.valueOf(seputarLoadModels.get(postRec).getId_pic()));
        params.put("id_area", String.valueOf(seputarLoadModels.get(postRec).getId_area()));
        params.put("id_lokasi", String.valueOf(seputarLoadModels.get(postRec).getId_lokasi()));
		params.put("ket", seputarLoadModels.get(postRec).getKet());
		params.put("nama_defect", seputarLoadModels.get(postRec).getNama_defect());
		params.put("resiko", seputarLoadModels.get(postRec).getResiko());
		params.put("id_mod_d", String.valueOf(id_mod_d));
		params.put("id_mod_m", String.valueOf(id_mod_m));
		params.put("id_seputar", String.valueOf(seputarLoadModels.get(postRec).getId_seputar()));
		params.put("id_user", id_user);
		params.put("pwd", pwd);
		params.put("dari", "harian");
        editSep.putExtra("arrayList", params);
        startActivity(editSep);
        finish();
    }

    public void TanyaHapus(int id_mod_d){
        SweetAlertDialog pTanya = new SweetAlertDialog(harian_add_item.this, SweetAlertDialog.WARNING_TYPE);
        pTanya.setCancelable(false);
        pTanya.setCanceledOnTouchOutside(false);
        pTanya.setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this data!")
                .setCancelText("No!")
                .setConfirmText("Yes!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        HapusDataDetail(id_mod_d);
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void HapusDataDetail(final int id_mod_d){

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
                            FetcDataSeputar(id_mod_m, idSeputar, idLokasi);
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(harian_add_item.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(harian_add_item.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new Hashtable<>();

                params.put("id_mod_d", String.valueOf(id_mod_d));
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "HapusMoDetail");

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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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

        final SweetAlertDialog pDialog = new SweetAlertDialog(harian_add_item.this, SweetAlertDialog.ERROR_TYPE);
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    photo_mod.setImageDrawable(getResources().getDrawable(drawable.ic_menu_gallery, getApplicationContext().getTheme()));
                } else {
                    photo_mod.setImageDrawable(getResources().getDrawable(drawable.ic_menu_gallery));
                }

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
    }


}