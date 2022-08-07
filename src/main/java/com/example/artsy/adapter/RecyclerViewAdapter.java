package com.example.artsy.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artsy.R;
import com.example.artsy.data.SearchResult;
import com.squareup.picasso.Picasso;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TVHolder> {

    private final SearchResult[] results;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public RecyclerViewAdapter(Context context, SearchResult[] results){
        this.results = results;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public TVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TVHolder(layoutInflater.inflate(R.layout.result_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TVHolder holder, int position) {
        String img_path = results[position].getImg();
        String name = results[position].getName();
        Log.d("LOGGING", img_path);

        Picasso.get().load(img_path).noFade().placeholder(R.drawable.ic_artsy_logo)
                .fit().error(R.drawable.ic_artsy_logo)
                .centerCrop().into(holder.imgView);
        holder.textView.setText(name);

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getLayoutPosition();
                onItemClickListener.onItemClick(holder.itemView, pos);
            });
        }
    }

    @Override
    public int getItemCount() {
        return results == null? 0 : results.length;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class TVHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;
        ImageView imgView;
        public TVHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.item_cardview);
            textView = (TextView) itemView.findViewById(R.id.name);
            imgView = (ImageView) itemView.findViewById(R.id.img);
        }
    }
}
