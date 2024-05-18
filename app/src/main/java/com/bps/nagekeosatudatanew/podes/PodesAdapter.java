package com.bps.nagekeosatudatanew.podes;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bps.nagekeosatudatanew.AppUtils;
import com.bps.nagekeosatudatanew.R;
import com.bps.nagekeosatudatanew.RecyclerViewClickListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PodesAdapter extends RecyclerView.Adapter<PodesAdapter.Holder>{

    private List<PodesItem> list;
    private Context context;
    private RecyclerViewClickListener listener;

    public PodesAdapter(List<PodesItem> list, Context context, RecyclerViewClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public PodesAdapter.Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_podes_adapter, parent, false);
        return new PodesAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PodesAdapter.Holder holder, int position) {
        PodesItem item = list.get(position);
        holder.bind(item, listener);
        holder.judul.setText(item.getNama());

        setupButton(holder.share, item);
        setupCardView(holder.cardView);
    }

    private void setupCardView(CardView cardView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            layoutParams.setMargins(5,4,5,4);
            cardView.setLayoutParams(layoutParams);
        }
    }

    private void setupButton(ImageButton share, final PodesItem item){
        share.setImageDrawable(new IconicsDrawable(context).color(ContextCompat.getColor(context, R.color.green)).icon(GoogleMaterial.Icon.gmd_share));
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do Your Code Here
                String urlShare =  item.getUrl();
                AppUtils.share((Activity) context, item.getNama(), urlShare);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView judul;
        CardView cardView;
        ImageButton share;

        public Holder(View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.judul_podes);
            cardView = itemView.findViewById(R.id.card_view_chat);
            share = itemView.findViewById(R.id.share_button);
        }

        public void bind(final PodesItem item, final RecyclerViewClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
