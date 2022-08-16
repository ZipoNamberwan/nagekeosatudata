package com.bps.nagekeosatudata.publikasi;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bps.nagekeosatudata.DatabaseHelper;
import com.bps.nagekeosatudata.EndlessRecyclerViewScrollListener;
import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.RecyclerViewClickListener;
import com.bps.nagekeosatudata.VolleySingleton;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

import static com.bps.nagekeosatudata.MainActivity.SEARCH_KEYWORD;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublikasiFragment extends Fragment {

    private RequestQueue queue;
    private ArrayList<PublikasiItem> list;
    private PublikasiAdapter adapter;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private DatabaseHelper db;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View failureView;
    private boolean isViewCreated;
    private String keyword;


    public PublikasiFragment() {
        // Required empty public constructor
        isViewCreated = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publikasi, container, false);
        db = new DatabaseHelper(getContext());

        if (getArguments()!=null){
            keyword = "&keyword=" + getArguments().getString(SEARCH_KEYWORD);
        }else {
            keyword = "";
        }

        isLoading = true;
        shimmerFrameLayout = view.findViewById(R.id.shimmer);

        failureView = view.findViewById(R.id.view_failure);
        Button refreshButton = view.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDataToArray(1);
            }
        });

        recyclerView = view.findViewById(R.id.listview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        queue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        isLoading = false;

        list = new ArrayList<>();

        adapter = new PublikasiAdapter(list, getActivity(), new RecyclerViewClickListener() {
            @Override
            public void onItemClick(Object object) {
                Intent i = new Intent(getActivity(), ViewPublikasiActivity.class);
                i.putExtra(ViewPublikasiActivity.ID_PUBLIKASI, ((PublikasiItem)object).getId());
                startActivity(i);
            }
        });

        SlideInBottomAnimationAdapter animatedAdapter = new SlideInBottomAnimationAdapter(adapter);
        animatedAdapter.setDuration(500);
        recyclerView.setAdapter(new AlphaInAnimationAdapter(animatedAdapter));
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                addDataToArray(page + 1);
            }
        });

        setViewVisibility(false, true, false);

        if (getUserVisibleHint()){
            addDataToArray(1);
        }

        isViewCreated = true;

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isViewCreated & isVisibleToUser){
            if (list.isEmpty()){
                addDataToArray(1);
            }
        }
    }


    private void addDataToArray(final int page){
        isLoading = true;
        if (list.isEmpty()){
            setViewVisibility(false, true, false);
        }
        String url = getString(R.string.web_service_path_list_publication)
                + getString(R.string.api_key) + "&page="+page + keyword;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (list.isEmpty()){
                            setViewVisibility(true, false, false);
                        }
                        addJSONToAdapter(jsonObject, page);
                        isLoading = false;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isLoading = false;
                if (list.isEmpty()){
                    setViewVisibility(false, false, true);
                }
            }
        });
        queue.add(request);
    }

    private void addJSONToAdapter(JSONObject jsonObject, int page) {
        try {
            if (jsonObject.getString("data-availability").equals("available")){
                JSONArray jsonArray = jsonObject.getJSONArray("data").getJSONArray(1);
                for(int i = 0; i < jsonArray.length() ; i++){
                    list.add(new PublikasiItem(jsonArray.getJSONObject(i).getString("pub_id"),
                            jsonArray.getJSONObject(i).getString("title"),jsonArray.getJSONObject(i).getString("rl_date"),
                            jsonArray.getJSONObject(i).getString("issn"),jsonArray.getJSONObject(i).getString("title"),
                            jsonArray.getJSONObject(i).getString("issn"),jsonArray.getJSONObject(i).getString("cover"),
                            jsonArray.getJSONObject(i).getString("pdf"),
                            db.isPublikasiBookmarked(jsonArray.getJSONObject(i).getString("pub_id")), false));
                }
                adapter.notifyItemRangeInserted(page * 10 + 1,jsonArray.length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setViewVisibility(boolean isMainVisible, boolean isShimmerVisible, boolean isFailureVisible) {

        if (isMainVisible){
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.GONE);
        }

        if (isShimmerVisible){
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
        }else {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }

        if (isFailureVisible){
            failureView.setVisibility(View.VISIBLE);
        }else {
            failureView.setVisibility(View.GONE);
        }

    }
}
