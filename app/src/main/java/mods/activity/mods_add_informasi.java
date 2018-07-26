package mods.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.irshulx.Editor;
import com.github.irshulx.models.EditorTextStyle;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;
import com.seseorang.sweetalert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Rius on 6/14/2018.
 */

public class mods_add_informasi extends AppCompatActivity {

    private Editor editor;
    private int id_mod=0, sukses, dari=0;
    private long mLastClickTime = 0;
    private String info_tambahan="";
    private String id_user, pwd;

    @Override
    protected void  onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.mods_add_informasi);

        editor = findViewById(R.id.informasi_tambahan);


        id_mod  =Integer.parseInt(getIntent().getStringExtra("id_mod"));
        dari    =Integer.parseInt(getIntent().getStringExtra("dari"));
        info_tambahan = getIntent().getStringExtra("info_tambahan");
        id_user     = getIntent().getStringExtra("id_user");
        pwd         =getIntent().getStringExtra("pwd");

        Toolbar tl = findViewById(R.id.toolbar);
        TextView title_toolbar   = findViewById(R.id.title_toolbar);
        title_toolbar.setText(getResources().getText(R.string.toolbar_informasi_tambahan));

        tl.setNavigationOnClickListener(view -> onBackPressed());

        Button btnLanjut = findViewById(R.id.btnSimpan);
        btnLanjut.setText(getResources().getText(R.string.btnLanjut) );

        btnLanjut.setOnClickListener(view -> {

            if(SystemClock.elapsedRealtime() - mLastClickTime < 5000 ){
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();

            SimpanData();
        });

        setupEditor();

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

                        if(sukses==1){
                            Intent add_seputar = new Intent(mods_add_informasi.this, mods_add_seputar.class);
                            add_seputar.putExtra("id_mod",String.valueOf(id_mod));
                            add_seputar.putExtra("dari", "add");
                            add_seputar.putExtra("id_seputar", String.valueOf(0));
                            add_seputar.putExtra("id_lokasi", String.valueOf(0));
                            add_seputar.putExtra("id_user", id_user);
                            add_seputar.putExtra("pwd", pwd);
                            startActivity(add_seputar);
                            finish();
                        } else if(sukses==2) {
                            final SweetAlertDialog sPesan    = new SweetAlertDialog(mods_add_informasi.this, SweetAlertDialog.ERROR_TYPE);
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
                    Toast.makeText(mods_add_informasi.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams()  {

                Map<String,String> params = new Hashtable<>();

                params.put("id_mod", String.valueOf(id_mod));
                params.put("ket",editor.getContentAsHTML());
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "SimpanInfoTambahan");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setupEditor(){
        findViewById(R.id.action_h1).setOnClickListener(v -> editor.updateTextStyle(EditorTextStyle.H1));

        findViewById(R.id.action_h2).setOnClickListener(v -> editor.updateTextStyle(EditorTextStyle.H2));

        findViewById(R.id.action_h3).setOnClickListener(v -> editor.updateTextStyle(EditorTextStyle.H3));

        findViewById(R.id.action_bold).setOnClickListener(v -> editor.updateTextStyle(EditorTextStyle.BOLD));

        findViewById(R.id.action_Italic).setOnClickListener(v -> editor.updateTextStyle(EditorTextStyle.ITALIC));

        findViewById(R.id.action_indent).setOnClickListener(v -> editor.updateTextStyle(EditorTextStyle.INDENT));

        findViewById(R.id.action_outdent).setOnClickListener(v -> editor.updateTextStyle(EditorTextStyle.OUTDENT));

        findViewById(R.id.action_bulleted).setOnClickListener(v -> editor.insertList(false));

        findViewById(R.id.action_unordered_numbered).setOnClickListener(v -> editor.insertList(true));

        findViewById(R.id.action_hr).setOnClickListener(v -> editor.insertDivider());

        findViewById(R.id.action_erase).setOnClickListener(v -> editor.clearAllContents());


        /*
        Map<Integer, String> contentTypeface = getContentface();
        Map<Integer, String> headingTypeface = getHeadingface();
        editor.setHeadingTypeface(headingTypeface);
        editor.setContentTypeface(contentTypeface);
        editor.setNormalTextSize(getResources().getDimensionPixelSize(R.dimen.text_editor));
        */
        editor.setDividerLayout(R.layout.tmpl_divider_layout);
        editor.setListItemLayout(R.layout.tmpl_list_item);

        if(dari==1) {
            editor.render();
        } else if(dari==2){
            if(info_tambahan.equals("kosong")){
                editor.render();
            } else {
                editor.render(info_tambahan);
            }

        }
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