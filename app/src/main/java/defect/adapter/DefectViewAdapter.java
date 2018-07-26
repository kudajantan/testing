package defect.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seseorang.demos.R;
import com.seseorang.demos.utils.Configs;

import java.util.ArrayList;

import defect.model.DefectViewModel;

public class DefectViewAdapter extends RecyclerView.Adapter<DefectViewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<DefectViewModel> defectViewList;


    public DefectViewAdapter(Context mContext, ArrayList<DefectViewModel> defectViewList){
        this.mContext = mContext;
        this.defectViewList = defectViewList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tgl, ket;
        private ImageView gambar;

        private MyViewHolder(View view){
            super(view);
            tgl = view.findViewById(R.id.tgl_history);
            gambar = view.findViewById(R.id.gambar_defect_history);
            ket = view.findViewById(R.id.ket_defect_history);

        }
    }


    @Override
    @NonNull
    public DefectViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.defect_view_list , parent, false);
        return new DefectViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DefectViewAdapter.MyViewHolder holder, final int position){
            final DefectViewModel defectViewModel   = defectViewList.get(position);

            holder.tgl.setText(defectViewModel.getTanggal() );
            holder.ket.setText(Html.fromHtml(defectViewModel.getKet()));
            Configs.displayImageThumbnailHistory(mContext, defectViewModel, holder.gambar);
    }

    @Override
    public int getItemCount() {
        return defectViewList.size();
    }

}