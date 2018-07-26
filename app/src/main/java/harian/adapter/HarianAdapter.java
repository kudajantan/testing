package harian.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seseorang.demos.R;
import com.seseorang.demos.utils.CircularTextView;

import java.util.ArrayList;

import harian.activity.harian_main;
import harian.model.HarianModel;
import mods.activity.mods_activity;
import mods.model.mods;

public class HarianAdapter extends RecyclerView.Adapter<HarianAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<HarianModel> harianList;
    private int ListPosisi;



    public HarianAdapter(Context mContext, ArrayList<HarianModel> harianList){
        this.mContext = mContext;
        this.harianList = harianList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tgl, nama;
        private ImageView overflow;
        private CircularTextView inisial;

        private MyViewHolder(View view){
            super(view);
            tgl = view.findViewById(R.id.tgl);
            nama = view.findViewById(R.id.nama_petugas);
            inisial = view.findViewById(R.id.txtInisial);
            overflow = view.findViewById(R.id.overflow);
        }
    }



    @Override
    @NonNull
    public HarianAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.harian_item , parent, false);
        return new HarianAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HarianAdapter.MyViewHolder holder, final int position){
            final HarianModel harianModel   = harianList.get(position);

            holder.tgl.setText(harianModel.getTanggal());
            holder.nama.setText(harianModel.getNama_petugas());

            holder.inisial.setSolidColor(harianModel.getColor());
            holder.inisial.setText(harianModel.getInisial());

            holder.overflow.setOnClickListener(v -> showPopupMenu(holder.overflow, harianModel.getId_mod(),
                        harianModel.getSdhLewat()));
    }

    @Override
    public int getItemCount() {
        return harianList.size();
    }

    private void showPopupMenu(View view, int posisi, int sdhLewat) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);

        MenuInflater inflater = popup.getMenuInflater();
        ListPosisi = posisi;
        inflater.inflate(R.menu.menu_popup_harian, popup.getMenu());

        if(sdhLewat==1){
            popup.getMenu().findItem(R.id.mnu_action_edit).setVisible(false);
            popup.getMenu().findItem(R.id.mnu_add_seputar).setVisible(false);
        } else {
            popup.getMenu().findItem(R.id.mnu_action_edit).setVisible(false);
            popup.getMenu().findItem(R.id.mnu_add_seputar).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new HarianAdapter.MyMenuItemClickListener());
        popup.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.mnu_action_edit:
                    ((mods_activity) mContext).EditMods(ListPosisi);
                    return true;
                case R.id.mnu_action_preview:
                    ((mods_activity) mContext).ExecuteLinkFileDemos(ListPosisi);
                    return true;
                case R.id.mnu_add_seputar:
                    ((harian_main) mContext ).TambahItemHarian(ListPosisi);
                    return  true;
                case R.id.mnu_action_hapus:
                    ((mods_activity) mContext).TanyaHapus(ListPosisi);
                    return true;
                case R.id.mnu_action_send:
                    //Toast.makeText(mContext, String.valueOf(ListPosisi), Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

}