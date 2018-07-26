package mods.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seseorang.demos.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import harian.activity.harian_add_item;
import mods.activity.mods_add_seputar;
import mods.model.SeputarLoadModel;


public class SeputarAdapter extends RecyclerView.Adapter<SeputarAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<SeputarLoadModel> seputarList;
    private int ListPosisi, posRecord;
    private String dari;



    public SeputarAdapter(Context mContext, ArrayList<SeputarLoadModel> seputarList, String dari){
        this.mContext = mContext;
        this.seputarList = seputarList;
        this.dari = dari;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView area, lokasi, status, pic, ket, nama_defect, resiko;
        private ImageView gambar, overflow;

        private MyViewHolder(View view){
            super(view);
            area = view.findViewById(R.id.area);
            lokasi = view.findViewById(R.id.lokasi);
            status = view.findViewById(R.id.status);
            pic = view.findViewById(R.id.pic);
            gambar = view.findViewById(R.id.gambar_mod);
            pic = view.findViewById(R.id.pic);
            nama_defect = view.findViewById(R.id.nama_defect);
            resiko = view.findViewById(R.id.resiko);
            ket = view.findViewById(R.id.ket);
            overflow = view.findViewById(R.id.overflow);
        }
    }



    @Override
    @NonNull
    public SeputarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.seputar_list , parent, false);
        return new SeputarAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SeputarAdapter.MyViewHolder holder, final int position){
            final SeputarLoadModel seputarLoadModel   = seputarList.get(position);

            holder.area.setText(seputarLoadModel.getArea());
            holder.lokasi.setText(seputarLoadModel.getLokasi());
            holder.status.setText(seputarLoadModel.getStatus());
            holder.pic.setText(seputarLoadModel.getPic());
            holder.ket.setText(Html.fromHtml(seputarLoadModel.getKet()));
            holder.resiko.setText(Html.fromHtml(seputarLoadModel.getResiko()));
            holder.nama_defect.setText(seputarLoadModel.getNama_defect());

            if(seputarLoadModel.getId_seputar()==4){
                holder.nama_defect.setVisibility(View.VISIBLE);
            }

            Picasso.with(mContext).load(seputarLoadModel.getGambar()).into(holder.gambar);

            holder.overflow.setOnClickListener(v -> showPopupMenu(holder.overflow, seputarLoadModel.getId_mod(), holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return seputarList.size();
    }

    private void showPopupMenu(View view, int posisi, int posRecord) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);

        MenuInflater inflater = popup.getMenuInflater();
        ListPosisi = posisi;
        this.posRecord = posRecord;
        inflater.inflate(R.menu.menu_popup_mods_aksi, popup.getMenu());


        popup.setOnMenuItemClickListener(new SeputarAdapter.MyMenuItemClickListener());
        popup.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.mnu_action_edit:

                    if(dari.equals("harian")){
                        ((harian_add_item) mContext).editItem(ListPosisi,posRecord);
                    } else {
                        ((mods_add_seputar) mContext).editSeputar(ListPosisi,posRecord);
                    }

                    return true;
                case R.id.mnu_action_hapus:
                    if(dari.equals("harian")){
                        ((harian_add_item) mContext).TanyaHapus(ListPosisi);
                    } else {
                        ((mods_add_seputar) mContext).TanyaHapus(ListPosisi);
                    }
                    return true;
                default:
            }
            return false;
        }
    }

}