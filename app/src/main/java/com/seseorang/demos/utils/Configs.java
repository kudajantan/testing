package com.seseorang.demos.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import defect.model.DefectModel;
import defect.model.DefectViewModel;

public class Configs {

    public static final String MODS_URL ="http://hrd.kuningancity.com/demos/API/mods.php";
	public static final String PDF_URL = "http://hrd.kuningancity.com/demos/kirim/buat_pdf_api.php";
    public static final String DEFECT_REPORT_URL = "http://hrd.kuningancity.com/demos/kirim/defect_pdf_api.php";
	
    public static final String JSON_ARRAY   = "result";
    public static final String JSON_ARRAY_SEPUTAR ="seputar";
    public static final String JSON_ARRAY_LOKASI ="lokasi";
    public static final String JSON_ARRAY_AREA ="area";
    public static final String JSON_ARRAY_STATUS ="status";
    public static final String JSON_ARRAY_PIC ="pic";


    public static void displayImageThumbnail(Context ctx, DefectModel p, ImageView imageView){
        try{
            String url = "";
            if(p.getGambar() !=null && !p.getGambar().equals("")) {
                url = p.getGambar();
            }

            if(!TextUtils.isEmpty(url)){
                Picasso.with(ctx).load(url).into(imageView);
            }
        }catch (Exception e){
            Log.e("DeMOS", "Failed when display image - "+e.getMessage());
        }
    }

    public static void displayImageThumbnailHistory(Context ctx, DefectViewModel p, ImageView imageView){
        try{
            String url = "";
            if(p.getGambar() !=null && !p.getGambar().equals("")) {
                url = p.getGambar();
            }

            if(!TextUtils.isEmpty(url)){
                Picasso.with(ctx).load(url).into(imageView);
            }
        }catch (Exception e){
            Log.e("DeMOS", "Failed when display image - "+e.getMessage());
        }
    }
	
	

	
	
}
