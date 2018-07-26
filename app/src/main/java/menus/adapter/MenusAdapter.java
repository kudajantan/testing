package menus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seseorang.demos.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import menus.model.MenusModel;


public class MenusAdapter extends RecyclerView.Adapter<MenusAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MenusModel> menusModelList;

    private ItemClickListener listener;
    private int selectedPos = 0;

    public interface ItemClickListener {
        void onClick(View view, int position);
    }


    public MenusAdapter(ArrayList<MenusModel> menusModels,  Context mContext){
        this.menusModelList = menusModels;
        this.mContext = mContext;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nama_menus;
        private ImageView icon_menus;

        MyViewHolder(View itemView){
            super(itemView);
            nama_menus =  itemView.findViewById(R.id.nama_menus);
            icon_menus =  itemView.findViewById(R.id.icon_menus);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if( listener != null ) listener.onClick(view, getAdapterPosition() );

            notifyItemChanged(selectedPos);
            selectedPos = getAdapterPosition();
            notifyItemChanged(selectedPos);
        }


    }

    @NonNull
    @Override
    public MenusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View    itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_items, parent, false);

        return new MenusAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MenusAdapter.MyViewHolder holder, final int position){
        final MenusModel menusModel = menusModelList.get(position);

        holder.itemView.setBackgroundColor(selectedPos==position ? mContext.getResources().getColor(R.color.smoke_white) : Color.TRANSPARENT);
        holder.nama_menus.setTextColor(selectedPos==position ? mContext.getResources().getColor(R.color.colorBlueDark) : Color.BLACK);
        Picasso.with(mContext).load(menusModel.getIcon_menu()).into(holder.icon_menus);
        holder.nama_menus.setText(menusModel.getNama_menu());
    }

    @Override
    public int getItemCount() {
        return menusModelList == null ? 0 : menusModelList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener){
        this.listener = itemClickListener;
    }

}