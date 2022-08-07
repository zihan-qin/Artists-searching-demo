package com.example.artsy.data;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artsy.R;
import com.example.artsy.adapter.ArtworkRecyclerViewAdapter;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;

public class FavorSection extends Section {
    List<FavorItem> itemList;

    public FavorSection(List<FavorItem> itemList) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.section_item)
                .headerResourceId(R.layout.section_header)
                .build()
        );
        this.itemList = itemList;
    }

    @Override
    public int getContentItemsTotal() {
        return itemList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;
        itemHolder.nameItem.setText(itemList.get(position).name);
        itemHolder.nationalityItem.setText(itemList.get(position).nationality);
        itemHolder.birthdayItem.setText(itemList.get(position).birthday);

        if (onItemClickListener != null) {
            itemHolder.buttonView.setOnClickListener(v -> {
                int pos = holder.getLayoutPosition();
                onItemClickListener.onItemClick(itemHolder.buttonView, pos);
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new EmptyViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private FavorSection.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(FavorSection.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class MyItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameItem;
        private final TextView birthdayItem;
        private final TextView nationalityItem;
        private final ImageButton buttonView;

        public MyItemViewHolder(View itemView) {
            super(itemView);
            nameItem = itemView.findViewById(R.id.favor_name);
            birthdayItem = itemView.findViewById(R.id.favor_nationality);
            nationalityItem = itemView.findViewById(R.id.favor_birthday);
            buttonView = itemView.findViewById(R.id.imageButton);
        }
    }
}