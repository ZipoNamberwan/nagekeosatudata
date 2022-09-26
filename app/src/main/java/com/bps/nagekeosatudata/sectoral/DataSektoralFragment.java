package com.bps.nagekeosatudata.sectoral;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.RecyclerViewClickListener;
import com.bps.nagekeosatudata.podes.PodesAdapter;
import com.bps.nagekeosatudata.podes.PodesItem;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataSektoralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataSektoralFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isViewCreated;
    private ArrayList<DataSektoral> list;
    private RecyclerView recyclerView;
    private DataSektoralAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DataSektoralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataSektoralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataSektoralFragment newInstance(String param1, String param2) {
        DataSektoralFragment fragment = new DataSektoralFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podes, container, false);
        recyclerView = view.findViewById(R.id.listview);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        list.add(new DataSektoral("1", "Bapellitbangda", R.drawable.bapelitbangda));
        list.add(new DataSektoral("2", "Dinas Kesehatan", R.drawable.pemda));
        list.add(new DataSektoral("3", "Dinas Sosial", R.drawable.pemda));
        list.add(new DataSektoral("4", "Dinas Kominfo", R.drawable.kominfo));
        list.add(new DataSektoral("5", "Dinas Pertanian", R.drawable.pemda));
        list.add(new DataSektoral("6", "Dinas Koperindag", R.drawable.pemda));
        list.add(new DataSektoral("7", "Dinas Peternakan", R.drawable.pemda));
        list.add(new DataSektoral("8", "Dinas Perikanan", R.drawable.pemda));
        list.add(new DataSektoral("9", "Dinas PUPR", R.drawable.pupr));
        list.add(new DataSektoral("10", "Dinas Pangan", R.drawable.pemda));
        list.add(new DataSektoral("11", "Dinas Tanaman Pangan", R.drawable.pemda));
        list.add(new DataSektoral("12", "Dinas Dukcapil", R.drawable.pemda));

        adapter = new DataSektoralAdapter(list, getActivity(), new RecyclerViewClickListener() {
            @Override
            public void onItemClick(Object object) {
                Toast.makeText(getContext(), "Menuju data " + ((DataSektoral) object).getName(), Toast.LENGTH_SHORT).show();
//                Uri uri = Uri.parse(((PodesItem) object).getUrl()); // missing 'http://' will cause crashed
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
            }
        });

        SlideInBottomAnimationAdapter animatedAdapter = new SlideInBottomAnimationAdapter(adapter);
        animatedAdapter.setDuration(500);
        recyclerView.setAdapter(new AlphaInAnimationAdapter(animatedAdapter));

        isViewCreated = true;

        return view;  }
}