package com.example.artsy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artsy.R;
import com.example.artsy.data.BioPair;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BioInfoRecyclerViewAdapter extends RecyclerView.Adapter<BioInfoRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<BioPair> bioInfo;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public BioInfoRecyclerViewAdapter(Context context, ArrayList<BioPair> bioInfo) {
        this.bioInfo = bioInfo;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.fragment_bio_info, parent, false
                )
        );

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIdView.setText(bioInfo.get(position).key);
        holder.mContentView.setText(bioInfo.get(position).value);
    }

    @Override
    public int getItemCount() {
        return bioInfo.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View itemView) {
            super(itemView);
            mIdView = itemView.findViewById(R.id.key);
            mContentView = itemView.findViewById(R.id.value);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}