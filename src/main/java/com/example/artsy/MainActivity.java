package com.example.artsy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artsy.data.FavorItem;
import com.example.artsy.data.FavorSection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    List<FavorItem> favorList;
    Type ListFavorItem = new TypeToken<List<FavorItem>>(){}.getType();
    Gson gson = new Gson();

    ProgressBar spinner;
    LinearLayout content;
    Handler handler;
    ActionBar actionBar;

    @Override
    protected void onResume() {
        super.onResume();
        // Setup shared preference
        sharedPreferences = getSharedPreferences(
                getResources().getString(R.string.shared_preference_name),
                0);
        if (sharedPreferences.contains("favorites")) {
            String jsonData = sharedPreferences.getString("favorites", "");
            favorList = gson.fromJson(jsonData, ListFavorItem);
            Log.d("JSON DEBUG", jsonData);
        } else {
            favorList = new ArrayList<>();
        }

        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        FavorSection section = new FavorSection(favorList);
        section.setOnItemClickListener((view, position) -> {
            Intent intentOpenDetail = new Intent(MainActivity.this, ArtistDetailActivity.class);
            intentOpenDetail.putExtra("name", favorList.get(position - 1).name);
            intentOpenDetail.putExtra("id", favorList.get(position - 1).id);
            startActivity(intentOpenDetail);
        });
        sectionedRecyclerViewAdapter.addSection(section);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.black)));
        RecyclerView recyclerView = findViewById(R.id.sectioned_recycler_view);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.progressBar);
        content  = findViewById(R.id.content);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
                actionBar.show();
                content.setVisibility(View.VISIBLE);
            }
        }, 3000);

        TextView dateTag = findViewById(R.id.editTextDate);
        dateTag.setText(getCurrentDate());

        TextView artsyLink = findViewById(R.id.artsyLink);
        artsyLink.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.artsy.net/");
            Intent openURL = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(openURL);
        });

    }

    public String getCurrentDate() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), SearchResultActivity.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
    }
}