package com.bps.nagekeosatudata.sectoral;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.RecyclerViewClickListener;
import com.bps.nagekeosatudata.podes.PodesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DataSektoralAdapter extends RecyclerView.Adapter<DataSektoralAdapter.Holder> {

    private List<DataSektoral> list;
    private Context context;
    private RecyclerViewClickListener listener;

    public DataSektoralAdapter(List<DataSektoral> list, Context context, RecyclerViewClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_data_sektoral_adapter, parent, false);
        return new DataSektoralAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        DataSektoral item = list.get(position);
        holder.bind(item, listener);
        holder.judul.setText(item.getName());
        holder.image.setImageResource(item.getImage());
        setupCardView(holder.cardView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setupCardView(CardView cardView){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            layoutParams.setMargins(5,4,5,4);
            cardView.setLayoutParams(layoutParams);
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView judul;
        CardView cardView;
        ImageView image;

        public Holder(View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.name_data_sektoral);
            cardView = itemView.findViewById(R.id.card_view_chat);
            image = itemView.findViewById(R.id.logo);
        }

        public void bind(final DataSektoral item, final RecyclerViewClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
