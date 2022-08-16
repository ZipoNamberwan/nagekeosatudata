package com.bps.nagekeosatudata.brs;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import com.bps.nagekeosatudata.AppUtils;
import com.bps.nagekeosatudata.DatabaseHelper;
import com.bps.nagekeosatudata.EndlessRecyclerViewScrollListener;
import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.RecyclerViewClickListener;
import com.bps.nagekeosatudata.VolleySingleton;

import static com.bps.nagekeosatudata.MainActivity.SEARCH_KEYWORD;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrsFragment extends Fragment {

    private DatabaseHelper db;
    private RequestQueue queue;
    private boolean isLoading;

    private ArrayList<BrsItem> list;
    private BrsAdapter adapter;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View failureView;
    private boolean isViewCreated;

    private String keyword;

    public static final String IS_FIRST_FRAGMENT = "first fragment";

    public BrsFragment() {
        // Required empty public constructor
        isViewCreated = false;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = new DatabaseHelper(getContext());
        queue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        isLoading = true;

        if (getArguments()!=null){
            keyword = "&keyword=" + getArguments().getString(SEARCH_KEYWORD);
        }else {
            keyword = "";
        }

        View view = inflater.inflate(R.layout.fragment_brs, container, false);

        failureView = view.findViewById(R.id.view_failure);
        Button refreshButton = view.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDataToArray(1);
            }
        });

        shimmerFrameLayout = view.findViewById(R.id.shimmer);

        recyclerView = view.findViewById(R.id.listview);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        adapter = new BrsAdapter(list, getActivity(), new RecyclerViewClickListener() {
            @Override
            public void onItemClick(Object object) {
                Intent i = new Intent(getActivity(), ViewBrsActivity.class);
                i.putExtra(ViewBrsActivity.ID_BRS, ((BrsItem) object).getId());
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

        if (isVisible()){
            addDataToArray(1);
        }else {
            if (getArguments()!=null){
                if (getArguments().getBoolean(IS_FIRST_FRAGMENT)){
                    addDataToArray(1);
                }
            }
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


    private void addDataToArray(final int page) {
        isLoading = true;
        if (list.isEmpty()){
            setViewVisibility(false, true, false);
        }
        String url = getString(R.string.web_service_path_list_press_release)
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

    private void addJSONToAdapter(JSONObject jsonObject, int page){
        try {
            if (jsonObject.getString("data-availability").equals("available")){
                JSONArray jsonArray = jsonObject.getJSONArray("data").getJSONArray(1);
                for (int i = 0; i < jsonArray.length(); i++){
                    boolean isSection;
                    if (i == 0) {
                        isSection = list.isEmpty() || !AppUtils.getDate(list.get(list.size() - 1).getTanggalRilis(), true).equals(AppUtils.getDate(jsonArray.getJSONObject(0).getString("rl_date"), true));
                    }else {
                        isSection = !AppUtils.getDate(jsonArray.getJSONObject(i).getString("rl_date"), true).equals(AppUtils.getDate(jsonArray.getJSONObject(i - 1).getString("rl_date"), true));
                    }
                    list.add(new BrsItem(jsonArray.getJSONObject(i).getString("brs_id"),
                            jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("brs_id"),
                            jsonArray.getJSONObject(i).getString("rl_date"), jsonArray.getJSONObject(i).getString("pdf"),
                            jsonArray.getJSONObject(i).getString("subj"), jsonArray.getJSONObject(i).getString("title"),
                            4,
                            db.isBrsBookmarked(jsonArray.getJSONObject(i).getString("brs_id")), isSection));
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

