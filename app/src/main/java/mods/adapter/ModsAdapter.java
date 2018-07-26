package mods.adapter;

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


import mods.activity.mods_activity;
import mods.model.mods;

public class ModsAdapter extends RecyclerView.Adapter<ModsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<mods> modsList;
    private int ListPosisi;



    public ModsAdapter(Context mContext, ArrayList<mods> modsList){
        this.mContext = mContext;
        this.modsList = modsList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tgl, shift, nama;
        private ImageView overflow;
        private CircularTextView inisial;

        private MyViewHolder(View view){
            super(view);
            tgl = view.findViewById(R.id.tgl);
            shift = view.findViewById(R.id.shift);
            nama = view.findViewById(R.id.nama_petugas);
            inisial = view.findViewById(R.id.txtInisial);
            overflow = view.findViewById(R.id.overflow);
        }
    }



    @Override
    @NonNull
    public ModsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mods_item , parent, false);
        return new ModsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ModsAdapter.MyViewHolder holder, final int position){
            final mods modsModel   = modsList.get(position);

            holder.tgl.setText(modsModel.getTanggal());
            holder.shift.setText(String.valueOf(modsModel.getShift()) );
            holder.nama.setText(modsModel.getNama_petugas());

            holder.inisial.setSolidColor(modsModel.getColor());
            holder.inisial.setText(modsModel.getInisial());

            holder.overflow.setOnClickListener(v -> showPopupMenu(holder.overflow, modsModel.getId_mod(),
                        modsModel.getSdhLewat(), modsModel.getLevel()));
    }

    @Override
    public int getItemCount() {
        return modsList.size();
    }

    private void showPopupMenu(View view, int posisi, int sdhLewat, int level) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);

        MenuInflater inflater = popup.getMenuInflater();
        ListPosisi = posisi;
        inflater.inflate(R.menu.menu_popup_mods, popup.getMenu());

        if(sdhLewat==1){
            popup.getMenu().findItem(R.id.mnu_action_edit).setVisible(false);
            popup.getMenu().findItem(R.id.mnu_add_seputar).setVisible(false);
        } else {
            popup.getMenu().findItem(R.id.mnu_action_edit).setVisible(true);
            popup.getMenu().findItem(R.id.mnu_add_seputar).setVisible(true);
        }

        if(level!=3){
            popup.getMenu().findItem(R.id.mnu_action_hapus).setVisible(false);
        } else {
            popup.getMenu().findItem(R.id.mnu_action_hapus).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new ModsAdapter.MyMenuItemClickListener());
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
                    ((mods_activity) mContext ).TambahSeputar(ListPosisi);
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