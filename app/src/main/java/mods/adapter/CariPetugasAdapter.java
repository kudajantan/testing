package mods.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.CircularTextView;
import com.seseorang.demos.utils.Configs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Handler;

import mods.model.PetugasModel;

public class CariPetugasAdapter extends ArrayAdapter<PetugasModel> implements Filterable {

    private final ArrayList<PetugasModel> mPetugas;
    private int sukses;
    private JSONArray result;
    private String pesan;
    private Context mContext;
    private String id_user, pwd;


    public CariPetugasAdapter(Context context, int resource, String id_user, String pwd){
        super(context,resource);
        this.mContext = context;
        mPetugas = new ArrayList<>();
        this.id_user = id_user;
        this.pwd = pwd;


    }


    @Override
    public int getCount(){
        return mPetugas.size();
    }

    @Override
    public PetugasModel getItem(int index){
        return mPetugas.get(index);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View vi = convertView;
        Holder holder;

        if (convertView==null ){
            vi = inflater.inflate(R.layout.dropdown_item,parent,false);
            holder = new Holder();
            holder.ID = vi.findViewById(R.id.txtIDPetugas);
            holder.NAMA = vi.findViewById(R.id.txtNamaPetugas);
            holder.circularTextView = vi.findViewById(R.id.txtInisial);

            vi.setTag(holder);
        } else {
            holder = (Holder) vi.getTag();



            PetugasModel petugasModel = mPetugas.get(position);
            holder.ID.setText(String.valueOf(petugasModel.getId_aphris()) );
            holder.NAMA.setText(petugasModel.getNama_petugas());
            holder.circularTextView.setSolidColor(petugasModel.getColor());
            holder.circularTextView.setText(petugasModel.getInisial());
        }

        return vi;

    }

    private static class Holder {
        private TextView ID,NAMA;
        private CircularTextView circularTextView;
    }


    private final Filter Myfilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint != null) {

                String term = constraint.toString().trim();
                CariPetugas(term);

                //Assign the data to the FilterResults
                filterResults.values = mPetugas;
                filterResults.count = mPetugas.size();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };


    @Override
    @NonNull
    public Filter getFilter() {
        return Myfilter;
    }

    private void CariPetugas(final String words){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configs.MODS_URL,
                response -> {

                    if(mPetugas.size()>0) {
                        mPetugas.clear();
                    }

                    JSONObject j;

                    try {
                        j   = new JSONObject(response);
                        result  = j.getJSONArray(Configs.JSON_ARRAY);
                        sukses = j.getInt("success");
                        pesan = j.getString("pesan");


                        if(sukses==1) {

                            for (int i = 0; i < result.length(); i++) {
                                try {

                                    JSONObject json = result.getJSONObject(i);

                                    PetugasModel petugasModel = new PetugasModel();
                                    petugasModel.setId_aphris (json.getInt("id"));
                                    petugasModel.setNama_petugas(json.getString("nama"));
                                    petugasModel.setInisial(json.getString("inisial"));
                                    petugasModel.setColor(getRandomMaterialColor());
                                    mPetugas.add(petugasModel);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            notifyDataSetChanged();


                        } else if(sukses==2) {
                            Toast.makeText(getContext(), pesan, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e ) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams()  {

                Map<String,String> params = new Hashtable<>();

                params.put("kata", words);
                params.put("id_user", id_user);
                params.put("pwd", pwd);
                params.put("action", "CariPetugas");

                return params;
            }
        };

        RequestQueue requestQueue   = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private int getRandomMaterialColor() {
        int returnColor = Color.GRAY;
        int arrayId = mContext.getResources().getIdentifier("mdcolor_" + "400", "array", mContext.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

}
