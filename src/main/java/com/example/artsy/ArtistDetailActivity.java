package com.example.artsy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.artsy.adapter.ViewPagerAdapter;
import com.example.artsy.data.BioPair;
import com.example.artsy.data.FavorItem;
import com.example.artsy.data.SearchResult;
import com.example.artsy.fragment.ArtworkFragment;
import com.example.artsy.fragment.BioInfoFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArtistDetailActivity extends AppCompatActivity implements TabLayoutMediator.TabConfigurationStrategy {
    ViewPager2 viewPager;
    TabLayout tabLayout;
    ArrayList<String> titles;
    ArrayList<Drawable> icons;
    ArrayList<BioPair> bioInfo;
    ArrayList<SearchResult> artwork;
    boolean isFavor = false;

    boolean getBioInfoDone = false;
    boolean getArtworkDone = false;
    boolean allDone = false;

    String name;
    String id;
    String birthday;
    String nationality;
    LinearLayout spinner;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    List<FavorItem> favorList;
    Type ListFavorItem = new TypeToken<List<FavorItem>>(){}.getType();
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        //Loading shared preference
        sharedPreferences = getSharedPreferences(
                getResources().getString(R.string.shared_preference_name),
                0);
        sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains("favorites")) {
            String jsonData = sharedPreferences.getString("favorites", "");
            favorList = gson.fromJson(jsonData, ListFavorItem);
            Log.d("JSON DEBUG", jsonData);
        } else {
            favorList = new ArrayList<>();
        }

        //Processed the spinner
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            id = intent.getStringExtra("id");

            for (FavorItem favorItem: favorList){
                if (Objects.equals(favorItem.id, id)) {
                    isFavor = true;
                    break;
                }
            }

            bioInfo = new ArrayList<>();
            artwork = new ArrayList<>();

            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle(name);

            loadingData(id);
        }

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);

        titles = new ArrayList<>();
        titles.add("Details");
        titles.add("Artworks");

        icons = new ArrayList<>();
        icons.add(ContextCompat.getDrawable(this,R.drawable.ic_tab_bioinfo));
        icons.add(ContextCompat.getDrawable(this,R.drawable.ic_tab_artwork));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_favor);
        if (isFavor) {
            menuItem.setIcon(R.drawable.star);
            Log.d("FAV_DEBUG", "onCreateOptionsMenu: favoriteItemIcon is checked");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_favor){
            if (isFavor){
                isFavor = false;
                for (int i = 0; i < favorList.size(); i ++ ){
                    if (favorList.get(i).id.equals(this.id)) {
                        favorList.remove(i);
                        i -- ;
                    }
                }
                String jsonFormat = gson.toJson(favorList, ListFavorItem);
                Log.d("JSONDEBUG", jsonFormat);
                sharedPreferencesEditor.putString("favorites", jsonFormat).apply();
                item.setIcon(R.drawable.star_outline);
                Toast.makeText(this,
                        name + " is removed from favorites",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                isFavor = true;
                FavorItem favorItem = new FavorItem(this.name, this.id, this.birthday, this.nationality);
                favorList.add(favorItem);
                String jsonFormat = gson.toJson(favorList, ListFavorItem);
                Log.d("JSONDEBUG", jsonFormat);
                sharedPreferencesEditor.putString("favorites", jsonFormat).apply();
                item.setIcon(R.drawable.star);
                Toast.makeText(this,
                        name + "  is add to favorites",
                        Toast.LENGTH_SHORT).show();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new BioInfoFragment(bioInfo));
        fragmentList.add(new ArtworkFragment(artwork));
        viewPagerAdapter.setData(fragmentList);
        viewPager.setAdapter(viewPagerAdapter);
    }

    public void loadingData(String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(ArtistDetailActivity.this);
        String bioInfoUrl = "https://hw9-zihanq.wl.r.appspot.com/select/" + id;
        String artworkUrl = "https://hw9-zihanq.wl.r.appspot.com/endpoints/" + id;

        StringRequest stringRequestBioInfo = new StringRequest(bioInfoUrl, response -> {
            processBioInfoJsonResponse(response);
            getBioInfoDone = true;
            allDone = getArtworkDone;
            if (allDone) {
                spinner.setVisibility(View.GONE);
                setViewPagerAdapter();
                new TabLayoutMediator(tabLayout, viewPager, ArtistDetailActivity.this).attach();
            }
        }, error -> {
            Toast.makeText(ArtistDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            Log.d("VolleyError:", String.valueOf(error));
        });

        StringRequest stringRequestArtworks = new StringRequest(artworkUrl, response -> {
            processArtworkJsonResponse(response);
            getArtworkDone = true;
            allDone = getBioInfoDone;
            if (allDone) {
                spinner.setVisibility(View.GONE);
                setViewPagerAdapter();
                new TabLayoutMediator(tabLayout, viewPager, ArtistDetailActivity.this).attach();
            }
        }, error -> {
            Toast.makeText(ArtistDetailActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            Log.d("VolleyError:", String.valueOf(error));
        });

        requestQueue.add(stringRequestBioInfo);
        requestQueue.add(stringRequestArtworks);
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(titles.get(position));
        tab.setIcon(icons.get(position));
    }

    public void processBioInfoJsonResponse(String response) {
        try {
            JSONObject bioInfoJson = new JSONObject(response);

            String[] keys = {"name", "nationality", "birthday", "deathday", "biography"};
            for (String key: keys) {
                String value = bioInfoJson.getString(key);
                if (key.equals("birthday")){
                    birthday = value;
                } else if (key.equals("nationality")) {
                    nationality = value;
                }
                if (!value.isEmpty()) {
                    String nameKey = key.substring(0, 1).toUpperCase() + key.substring(1);
                    bioInfo.add(new BioPair(nameKey, value));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processArtworkJsonResponse(String response) {
        try {
            JSONArray artworkJSON = new JSONArray(response);
            int resultLength = artworkJSON.length();
            for (int i = 0; i < resultLength; i ++ ) {
                JSONObject record = artworkJSON.getJSONObject(i);
                String title = record.getString("title");
                String img = record.getString("img");
                String id = record.getString("id");
                artwork.add(new SearchResult(title, img, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}