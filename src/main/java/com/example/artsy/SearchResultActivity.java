package com.example.artsy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.artsy.adapter.RecyclerViewAdapter;
import com.example.artsy.data.SearchResult;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //Processed the spinner
        LinearLayout spinner;
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle(query);

            RequestQueue requestQueue = Volley.newRequestQueue(SearchResultActivity.this);
            String url = "https://hw9-zihanq.wl.r.appspot.com/search/" + query;
            StringRequest stringRequest = new StringRequest(url, response -> {
                SearchResult[] results = processJsonResponse(response);

                if (results.length == 0) {
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);
                    TextView noResultView = findViewById(R.id.no_result);
                    noResultView.setVisibility(View.VISIBLE);
                }
                else {
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchResultActivity.this);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(SearchResultActivity.this, results);
                    recyclerViewAdapter.setOnItemClickListener((view, position) -> {
                        Intent intentOpenDetail = new Intent(SearchResultActivity.this, ArtistDetailActivity.class);
                        intentOpenDetail.putExtra("name", results[position].getName());
                        intentOpenDetail.putExtra("id", results[position].getId());
                        startActivity(intentOpenDetail);
                    });
                    recyclerView.setAdapter(recyclerViewAdapter);
                    spinner.setVisibility(View.GONE);
                }
            }, error -> {
                Toast.makeText(SearchResultActivity.this, "Error: "+error, Toast.LENGTH_LONG).show();
                Log.d("VolleyError:", String.valueOf(error));
            });
            requestQueue.add(stringRequest);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public SearchResult[] processJsonResponse(String response) {
        SearchResult[] output = new SearchResult[0];
        try {
            JSONArray results = new JSONArray(response);
            int resultLength = results.length();
            output = new SearchResult[resultLength];
            for (int i = 0; i < resultLength; i ++ ) {
                JSONObject record = results.getJSONObject(i);
                String name = record.getString("name");
                String id = record.getString("id");
                String imgPath = record.getString("img");
                output[i] = new SearchResult(name, imgPath, id);

                Log.d("JSON", name);
                Log.d("JSON", id);
                Log.d("JSON", imgPath);
            }
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}