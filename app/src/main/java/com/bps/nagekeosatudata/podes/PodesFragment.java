package com.bps.nagekeosatudata.podes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bps.nagekeosatudata.EndlessRecyclerViewScrollListener;
import com.bps.nagekeosatudata.R;
import com.bps.nagekeosatudata.RecyclerViewClickListener;
import com.bps.nagekeosatudata.berita.BeritaAdapter;
import com.bps.nagekeosatudata.berita.BeritaItem;
import com.bps.nagekeosatudata.berita.ViewBeritaActivity;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

public class PodesFragment extends Fragment {

    private boolean isViewCreated;
    private ArrayList<PodesItem> list;
    private RecyclerView recyclerView;
    private PodesAdapter adapter;

    public PodesFragment() {
        isViewCreated = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_podes, container, false);
        recyclerView = view.findViewById(R.id.listview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        list.add(new PodesItem("1", "Kependudukan", "https://www.google.com/maps/d/edit?mid=1u8wLoTNeg9QPDEIpoLr6e2aubvq65AY&usp=sharing"));
        list.add(new PodesItem("2", "Geografi", "https://www.google.com/maps/d/edit?mid=1xee0dQhu7eXFjeNzmf4t8NGDZBw5E1g&usp=sharing"));
        list.add(new PodesItem("3", "Pendidikan", "https://www.google.com/maps/d/edit?mid=1yvR0d2WmPDXUDsgPizPbAutCuGZptF8&usp=sharing"));
        list.add(new PodesItem("4", "Penerangan dan Bahan Bakar Utama", "https://www.google.com/maps/d/edit?mid=1QRnbLX0sDMcNk82NvXggaipck0_e6s8&usp=sharing"));
        list.add(new PodesItem("5", "Perdagangan dan Akomodasi", "https://www.google.com/maps/d/edit?mid=1LhTw5lNSg8YKw6bMNTgegddKaDSQbuY&usp=sharing"));
        list.add(new PodesItem("6", "Koperasi dan Perbankan", "https://www.google.com/maps/d/edit?mid=1S0ATDdvAZE32azRZov3FJ58AxnHiBxk&usp=sharing"));
        list.add(new PodesItem("7", "Komunikasi", "https://www.google.com/maps/d/edit?mid=1c-E9ef42ou9WtBRUHgm2s6Me6hVEXt0&usp=sharing"));
        list.add(new PodesItem("8", "Kesehatan", "https://www.google.com/maps/d/edit?mid=1SIi9oQbOJnAinfSW3DfCKEMl4LIe1FA&usp=sharing"));
        list.add(new PodesItem("9", "Industri", "https://www.google.com/maps/d/edit?mid=1Q-RWcqqrahLngt95FJ1aUM-Wq0hG-zM&usp=sharing"));
        list.add(new PodesItem("10", "Transportasi", "https://www.google.com/maps/d/edit?mid=1MF8y9fbtEwZMVF9qG-6WGnyqQKYnuIQ&usp=sharing"));
        list.add(new PodesItem("11", "Sumber Air Minum Utama dan Sanitasi", "https://www.google.com/maps/d/edit?mid=1PiLxqSAedK8B8GYmZhAJwKp6oxlfI_E&usp=sharing"));

        adapter = new PodesAdapter(list, getActivity(), new RecyclerViewClickListener() {
            @Override
            public void onItemClick(Object object) {
                Uri uri = Uri.parse(((PodesItem) object).getUrl()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        SlideInBottomAnimationAdapter animatedAdapter = new SlideInBottomAnimationAdapter(adapter);
        animatedAdapter.setDuration(500);
        recyclerView.setAdapter(new AlphaInAnimationAdapter(animatedAdapter));

        isViewCreated = true;

        return view;
    }
}