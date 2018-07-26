package defect.adapter;

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

import com.lid.lib.LabelImageView;
import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;

import java.util.ArrayList;

import defect.DefectActivity;
import defect.model.DefectModel;

public class DefectAdapter extends RecyclerView.Adapter<DefectAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<DefectModel> defectList;
    private int IDDefect, locRecord;


    public DefectAdapter(Context mContext, ArrayList<DefectModel> defectList){
        this.mContext = mContext;
        this.defectList = defectList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tgl, nama, ket, nama_lengkap, pic, lokasi, status, due_date, resiko, tgl_selesai;
        private ImageView overflow;
        private LabelImageView labelImageView;

        private MyViewHolder(View view){
            super(view);
            tgl = view.findViewById(R.id.tgl);
            nama = view.findViewById(R.id.nama_defect);
            ket = view.findViewById(R.id.ket_defect);
            nama_lengkap = view.findViewById(R.id.uploadby);
            pic = view.findViewById(R.id.pic);
            overflow = view.findViewById(R.id.overflow);
            lokasi = view.findViewById(R.id.lokasi);
            status = view.findViewById(R.id.status_defect);
            due_date = view.findViewById(R.id.tgl_target);
            resiko = view.findViewById(R.id.ket_resiko);
            labelImageView = view.findViewById(R.id.gambar_defect_dua);
            tgl_selesai = view.findViewById(R.id.tgl_selesai);
        }
    }



    @Override
    @NonNull
    public DefectAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.defect_list , parent, false);
        return new DefectAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DefectAdapter.MyViewHolder holder, final int position){
            final DefectModel defectModel   = defectList.get(position);

            holder.tgl.setText(defectModel.getTanggal());
            holder.nama.setText(defectModel.getNama_defect ());
            holder.ket.setText(Html.fromHtml(defectModel.getKet_defect()));
            holder.resiko.setText(Html.fromHtml(defectModel.getResiko()));
            holder.nama_lengkap.setText(defectModel.getNama_lengkap());
            holder.pic.setText(defectModel.getPic());
            holder.lokasi.setText(defectModel.getLokasi());
            holder.due_date.setText(defectModel.getDue_date());
            holder.tgl_selesai.setText(defectModel.getTgl_selesai());
            holder.labelImageView.setLabelTextColor(mContext.getResources().getColor(R.color.white));
            holder.labelImageView.setLabelHeight(50);
            holder.labelImageView.setLabelTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.text));
            holder.labelImageView.setLabelDistance(60);

        switch (defectModel.getStatus_defect()) {
            case "Defect Baru":
                holder.status.setTextColor(mContext.getResources().getColor(R.color.red));
                holder.labelImageView.setLabelBackgroundColor(mContext.getResources().getColor(R.color.red));
                break;
            case "Selesai":
                holder.status.setTextColor(mContext.getResources().getColor(R.color.dark_green));
                holder.labelImageView.setLabelBackgroundColor(mContext.getResources().getColor(R.color.dark_green));
                break;
            default:
                holder.status.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
                holder.labelImageView.setLabelBackgroundColor(mContext.getResources().getColor(R.color.colorOrange));
                break;
        }


            holder.status.setText(defectModel.getStatus_defect());
            holder.labelImageView.setLabelText(defectModel.getStatus_defect());
            Configs.displayImageThumbnail(mContext, defectModel, holder.labelImageView);

            holder.overflow.setOnClickListener(view ->
                    showPopupMenu(view, defectModel.getId_defect(), defectModel.getStatus_defect(), defectModel.getLevel_user(), holder.getAdapterPosition(), defectModel.getDue_date() )
            );

    }

    @Override
    public int getItemCount() {
        return defectList.size();
    }

    private void showPopupMenu(View view, int id_defect, String status_defect, int level_user, int posRec, String due_date) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);

        MenuInflater inflater = popup.getMenuInflater();
        IDDefect = id_defect;
        locRecord = posRec;
        inflater.inflate(R.menu.menu_popup_defect, popup.getMenu());

        if(level_user==1){
            popup.getMenu().findItem(R.id.mnu_set_target).setVisible(false);
            popup.getMenu().findItem(R.id.mnu_forward).setVisible(false);
            popup.getMenu().findItem(R.id.mnu_follow_up).setVisible(false);
        } else if(level_user==2){
            switch (status_defect) {
                case "Defect Baru":

                    if(due_date.equals("")){
                        popup.getMenu().findItem(R.id.mnu_set_target).setVisible(true);
                        popup.getMenu().findItem(R.id.mnu_follow_up).setVisible(false);
                        popup.getMenu().findItem(R.id.mnu_forward).setVisible(true);
                    } else {
                        popup.getMenu().findItem(R.id.mnu_set_target).setVisible(false);
                        popup.getMenu().findItem(R.id.mnu_follow_up).setVisible(true);
                        popup.getMenu().findItem(R.id.mnu_forward).setVisible(false);
                    }

                    break;
                case "Selesai":
                    popup.getMenu().findItem(R.id.mnu_set_target).setVisible(false);
                    popup.getMenu().findItem(R.id.mnu_forward).setVisible(false);
                    popup.getMenu().findItem(R.id.mnu_follow_up).setVisible(false);
                    break;
                default:
                    popup.getMenu().findItem(R.id.mnu_set_target).setVisible(false);
                    popup.getMenu().findItem(R.id.mnu_forward).setVisible(false);
                    break;
            }
        }


        popup.setOnMenuItemClickListener(new DefectAdapter.MyMenuItemClickListener());
        popup.show();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.mnu_view_defect:
                    ((DefectActivity) mContext).viewDefect(locRecord, IDDefect);
                    return true;
                case R.id.mnu_follow_up:
                    ((DefectActivity) mContext).FollowUpDefect(IDDefect);
                    return true;
                case R.id.mnu_set_target:
                    ((DefectActivity) mContext).SetDueDateDefect(IDDefect);
                    return true;
                case R.id.mnu_forward:
                    ((DefectActivity) mContext).ForwardDefect(locRecord, IDDefect);
                    return true;
                default:
            }
            return false;
        }
    }

}