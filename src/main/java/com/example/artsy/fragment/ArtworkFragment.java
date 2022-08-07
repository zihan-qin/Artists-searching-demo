package com.example.artsy.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.artsy.ArtistDetailActivity;
import com.example.artsy.R;
import com.example.artsy.SearchResultActivity;
import com.example.artsy.adapter.ArtworkRecyclerViewAdapter;
import com.example.artsy.adapter.RecyclerViewAdapter;
import com.example.artsy.data.SearchResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class ArtworkFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ArrayList<SearchResult> artworks;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtworkFragment(ArrayList<SearchResult> artworks) {
        this.artworks = artworks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        if (artworks.size() == 0) {
            view = inflater.inflate(R.layout.fragment_artwork_list_empty, container, false);
            Log.d("VIEW", "CREATE EMPTY");
        } else {
            view = inflater.inflate(R.layout.fragment_artwork_list, container, false);
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            ArtworkRecyclerViewAdapter recyclerViewAdapter = new ArtworkRecyclerViewAdapter(getActivity(), artworks);
            recyclerViewAdapter.setOnItemClickListener((subView, position) -> {
                String artworkId = artworks.get(position).getId();
                RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
                String url = "https://hw9-zihanq.wl.r.appspot.com/genes/" + artworkId;

                StringRequest stringRequest = new StringRequest(url, response -> {
                    try {
                        JSONArray categoryJSONArray = new JSONArray(response);

                        if( categoryJSONArray.length()  == 0) {
                            final Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.category_dialog_empty);
                            dialog.show();
                        } else {
                            JSONObject categoryJSON = categoryJSONArray.getJSONObject(0);
                            String img = categoryJSON.getString("img");
                            String title = categoryJSON.getString("title");
                            String desc = categoryJSON.getString("desc");

                            Log.d("DIALOG", img);
                            final Dialog dialog = new Dialog(getContext());

                            dialog.setContentView(R.layout.category_dialog);

                            TextView titleView = dialog.findViewById(R.id.category_title);
                            titleView.setText(title);

                            TextView descView = dialog.findViewById(R.id.category_desc);
                            descView.setText(desc);

                            ImageView imgView = dialog.findViewById(R.id.category_img);
                            Picasso.get().load(img).noFade().placeholder(R.drawable.ic_artsy_logo)
                                    .fit().error(R.drawable.ic_artsy_logo)
                                    .centerCrop().into(imgView);

                            dialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.d("VolleyError:", String.valueOf(error));
                });
                requestQueue.add(stringRequest);
            });
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        return view;
    }
}